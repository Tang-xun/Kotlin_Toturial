package tank.com.kotlin.ai

import android.content.Context
import android.speech.SpeechRecognizer

class VoiceService(context: Context?) {
    private var mSpeechRecognizer: SpeechRecognizer? = null

    init {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val recognizer = mSpeechRecognizer
        recognizer?.setRecognitionListener(VoiceListener(null))
    }
}

data class SpeechData(val sentence: String, val score: Float)

interface IViewListener {

    fun <T> notifyData(data: T)

    fun notifyError(code: Int, msg: String)

    fun onReady()
}