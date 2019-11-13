package tank.com.kotlin.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import tank.com.kotlin.model.LoginData

class LoginViewModel : ViewModel() {

    private var mLoginData = MutableLiveData<LoginData?>()

    private var mClient: OkHttpClient = OkHttpClient()

    fun login(userName: String, pwd: String) {

        if (checkParams(userName, pwd)) {
            Log.i(TAG, "do login error checkParams fail ")
            return
        }
        GlobalScope.launch {
            val request: Request? = Request.Builder()
                    .url("http://****.com")
                    .addHeader("devType", "android")
                    .addHeader("appId", "10001")
                    .get()
                    .build()
            val response: Response = mClient.newCall(request).execute()

            if (response.isSuccessful) {
                Log.i(TAG, "request success " + response.code() + "\t" + response.message())
            } else {
                Log.i(TAG, "request fail " + response.code() + "\t" + response.message())
            }
        }
    }

    private fun checkParams(userName: String?, pwd: String?): Boolean {
        return userName.isNullOrBlank() && pwd.isNullOrBlank()
    }

    companion object {
        val TAG: String = LoginViewModel::class.java.simpleName
    }
}

