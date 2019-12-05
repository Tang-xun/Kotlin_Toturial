package tank.com.kotlin

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import tank.com.kotlin.anim.WeatherRankChangeAnimation

class AnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(WeatherRankChangeAnimation(this))
    }

}