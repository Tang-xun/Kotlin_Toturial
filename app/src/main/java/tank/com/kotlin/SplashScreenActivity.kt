package tank.com.kotlin

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    class GetOrFetchData : AsyncTask<Void, Void, Void>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }

        override fun doInBackground(vararg p0: Void?): Void {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}