package edu.luc.etl.cs313.android.scala.primechecker

import scala.util.control.Breaks._
import android.graphics.Color
import android.os.AsyncTask
import android.widget.ProgressBar
import android.widget.TextView

/**
 * Background task for checking locally whether a number is prime.
 * Implemented using a brute-force algorithm as a CPU-intensive task with
 * observable progress.
 *
 * Using AnyRef instead of Int as the type parameter to work around
 * a problem with Android not finding our implementation of doInBackground.
 * (Unresolved Scala-Java vararg interoperability issue:
 * http://piotrbuda.eu/2012/12/scala-and-android-asynctask-implementation-problem.html)
 */
class PrimeCheckerLocalTask(progressBar: ProgressBar, input: TextView)
  extends AsyncTask[AnyRef, AnyRef, Boolean] {

  override def onPreExecute() = {
    progressBar.setMax(100)
    input.setBackgroundColor(Color.YELLOW)
  }

  override protected def doInBackground(params: AnyRef*): Boolean = {
    require { params.length == 1 }
    val i = params(0).asInstanceOf[Int]
    if (i < 2) return false
    val half = i / 2
    var k = 2
    while (k <= half) {
      if (isCancelled()) break
      publishProgress(Int.box(k * 100 / half))
      if (i % k == 0) return false
      k += 1
    }
    true
  }

  override protected def onProgressUpdate(values: AnyRef*) =
    progressBar.setProgress(values(0).asInstanceOf[Integer])

  override protected def onPostExecute(result: Boolean) =
    input.setBackgroundColor(if (result) Color.GREEN else Color.RED)

  override protected def onCancelled(result: Boolean) =
    input.setBackgroundColor(Color.WHITE)
}
