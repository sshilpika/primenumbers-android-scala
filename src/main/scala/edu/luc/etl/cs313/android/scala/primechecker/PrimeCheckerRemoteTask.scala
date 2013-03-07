package edu.luc.etl.cs313.android.scala.primechecker

import java.net.URL

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient

import android.graphics.Color
import android.os.AsyncTask
import android.widget.{ ProgressBar, TextView }

/**
 * Background task for checking remotely whether a number is prime.
 * Expects at the given URL a suitable cloud service such as an instance of
 * https://github.com/webservices-cs-luc-edu/primenumbers-spray-scala
 *
 * Using AnyRef instead of Long as the type parameter to work around
 * a problem with Android not finding our implementation of doInBackground.
 */
class PrimeCheckerRemoteTask(progressBar: ProgressBar, input: TextView)
  extends AsyncTask[AnyRef, Void, Boolean] {

  private var request: HttpGet = null

  override protected def onPreExecute() {
    progressBar.setMax(100)
    input.setBackgroundColor(Color.YELLOW)
  }

  override protected def doInBackground(params: AnyRef*): Boolean = {
    require { params.length == 1 }
    val url = params(0).asInstanceOf[URL]
    progressBar.setIndeterminate(true)
    request = new HttpGet(url.toURI)
    val response = (new DefaultHttpClient).execute(request)
    val status = response.getStatusLine.getStatusCode
    if (status == 200)
      true
    else if (status == 404)
      false
    else
      throw new RuntimeException("unexpected server response")
  }

  override protected def onPostExecute(result: Boolean) {
    progressBar.setIndeterminate(false)
    progressBar.setProgress(100)
    input.setBackgroundColor(if (result) Color.GREEN else Color.RED)
  }

  override protected def onCancelled(result: Boolean) {
    progressBar.setIndeterminate(false)
    input.setBackgroundColor(Color.WHITE)
    request.abort()
  }
}
