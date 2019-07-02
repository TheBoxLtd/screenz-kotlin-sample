package app.com.screenz.sample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.gson.Gson
import com.screenz.shell_library.ShellLibraryBuilder
import com.screenz.shell_library.config.ConfigManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : FragmentActivity() {

    private val CONFIG_FILE_NAME = "config.json"
    private var mLocalConfig: LocalConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadLocalConfig()

        val configManager = ConfigManager.getInstance()
        configManager.coreData = mLocalConfig!!.core

//        configManager.setExtraData(this,"#data_to_store"); //In case you want to provide data to the sdk
//        configManager.setLaunchPageID(this,"#PAGEID"); //In case you want to set the pageid to be opened on launch
//        configManager.setPid(this,#PID); //In case you want to set the pageid to be opened on launch

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ShellLibraryBuilder.create(this))
            .commit()

        val intentFilter = IntentFilter("publishData")
        this.registerReceiver(dataReceiver, intentFilter)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentFragment?.onActivityResult(requestCode, resultCode, data)
    }

    private val dataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getStringExtra("data")
            Log.d("DATA RECEIVER", data)
            if (data.equals("sdk-exit-new", ignoreCase = true)) {
                finish()
            }
        }
    }

    override fun finish() {
        this.unregisterReceiver(dataReceiver)
        super.finish()
    }

    private fun loadLocalConfig() {
        var `in`: BufferedReader? = null
        try {
            val json = assets.open(CONFIG_FILE_NAME)
            `in` = BufferedReader(InputStreamReader(json, "UTF-8"))
            var str: String?
            val buf = StringBuilder()
            str = `in`.readLine()
            while (str != null) {
                buf.append(str)
                str = `in`.readLine()
            }
            mLocalConfig = Gson().fromJson(buf.toString(), LocalConfig::class.java)
        } catch (e: Exception) {
            Log.e("EXCEPTION",e.message)
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    //Do nothing
                }

            }
        }
    }
}



