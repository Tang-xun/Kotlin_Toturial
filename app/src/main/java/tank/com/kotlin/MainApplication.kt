package tank.com.kotlin

import android.app.Application

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mainApplication = this

    }


    companion object {
        var mainApplication: MainApplication?  = null
    }

}


