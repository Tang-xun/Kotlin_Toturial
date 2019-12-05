package tank.com.kotlin.ai

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer

class VoiceListener(iView: IViewListener?) : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRmsChanged(rmsdB: Float) {
        // your code here for displaying noise level change
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        if (data.isNullOrEmpty()) return

        data.let {
            // todo something when has data result
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBeginningOfSpeech() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEndOfSpeech() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResults(results: Bundle?) {
        val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

        if (data.isNullOrEmpty()) return
        val confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
        // Highest confidence results in data[0] with score confidence[0]
        if (data.size > 1) {
            for (i in data.indices) {
                // for other results
            }
        }
    }
}