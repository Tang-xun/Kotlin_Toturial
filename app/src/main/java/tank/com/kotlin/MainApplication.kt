package tank.com.kotlin

import android.app.Application
import tank.com.kotlin.utils.md5

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mContext = this
        val name = "tank"
        name.md5()
    }

    companion object {
        var mContext: MainApplication?  = null
    }

}


