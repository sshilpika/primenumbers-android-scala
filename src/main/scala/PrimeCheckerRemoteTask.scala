package edu.luc.etl.cs313.android.scala.primechecker

import android.graphics.Color
import android.util.Log
import android.widget.{ ProgressBar, TextView }

import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestHandle
import org.apache.http.Header

/**
 * Background task for checking remotely whether a number is prime.
 * Expects at the given URL a suitable cloud service such as an instance of
 * https://github.com/webservices-cs-luc-edu/primenumbers-spray-scala
 *
 * Using AnyRef instead of Long as the type parameter to work around
 * a problem with Android not finding our implementation of doInBackground.
 */
class PrimeCheckerRemoteTask(progressBar: ProgressBar, input: TextView) {

  private val TAG = "edu.luc.etl.cs313.android.primechecker.android.PrimeCheckerRemoteTask"

  private var request: RequestHandle = _

  def start(url: String): Unit = {
    Log.d(TAG, "starting request for URL = " + url)
    progressBar.setMax(100)
    progressBar.setIndeterminate(true)
    input.setBackgroundColor(Color.YELLOW)
    Log.d(TAG, "creating client")
    val client = new AsyncHttpClient
    Log.d(TAG, "submitting request to " + client)
    request = client.get(url, new AsyncHttpResponseHandler {
      override def onStart() = Log.d(TAG, "request started")
      override def onSuccess(statusCode: Int, headers: Array[Header], responseBody: Array[Byte]) = {
        Log.d(TAG, "request handled successfully with status code " + statusCode)
        input.setBackgroundColor(if (statusCode == 200) Color.GREEN else Color.MAGENTA)
      }
      override def onFailure(statusCode: Int, headers: Array[Header], responseBody: Array[Byte], error: Throwable) = {
        Log.d(TAG, "request failed with status code " + statusCode)
        input.setBackgroundColor(if (statusCode == 404) Color.RED else Color.MAGENTA)
        if (error != null) {
          Log.d(TAG, "request failed with error " + error)
        }
      }
      override def onFinish() = {
        progressBar.setIndeterminate(false)
        progressBar.setProgress(100)
      }
    })
    Log.d(TAG, "submitted request")
  }

  def cancel(): Unit = {
    Log.d(TAG, "canceling request")
    progressBar.setIndeterminate(false)
    input.setBackgroundColor(Color.WHITE)
    request.cancel(true)
    Log.d(TAG, "canceled request")
  }

  // end-method-remoteCancel
}
