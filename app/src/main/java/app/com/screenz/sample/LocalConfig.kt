package app.com.screenz.sample

import com.google.gson.annotations.SerializedName
import com.screenz.shell_library.config.CoreData

data class LocalConfig (
    @SerializedName("core") val core : CoreData,
    @SerializedName("noConnectionError") val noConnectionError : String
)