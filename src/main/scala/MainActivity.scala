package edu.luc.etl.cs313.android.scala.primechecker

import java.net.{ MalformedURLException, URL }
import java.util.{ ArrayList, List }
import scala.collection.mutable.{ ArraySeq, ArrayBuffer }
import android.os.{ AsyncTask, Bundle }
import android.app.Activity
import android.view.{ Menu, View }
import android.widget.{ ProgressBar, TextView, ToggleButton }
import scala.collection.mutable.ArraySeq

class MainActivity extends Activity with TypedActivity {

  private var input: TextView = null

  private val NUM = 3

  private val workers = new ArraySeq[Boolean](NUM)

  private val remotes = new ArraySeq[Boolean](NUM)

  private val progressBars = new ArraySeq[ProgressBar](NUM)

  private val urls = new ArraySeq[TextView](NUM)

  private val localTasks = new ArrayBuffer[AsyncTask[AnyRef, AnyRef, Boolean]](NUM)

  private val remoteTasks = new ArrayBuffer[AsyncTask[AnyRef, Void, Boolean]](NUM);

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
  }

  override def onResume() {
    super.onResume()
    input = findView(TR.inputCandidate)
    progressBars(0) = findView(TR.progressBar1)
    progressBars(1) = findView(TR.progressBar2)
    progressBars(2) = findView(TR.progressBar3)
    urls(0) = findView(TR.inputUrl1)
    urls(1) = findView(TR.inputUrl2)
    urls(2) = findView(TR.inputUrl3)
  }

  override def onDestroy() {
    super.onDestroy()
    onCancel(input)
  }

  def onCheck(view: View) {
    onCancel(view)
    for (i <- 0 until NUM) {
      progressBars(i).setProgress(0)
      if (workers(i))
        if (remotes(i)) {
          val t = new PrimeCheckerRemoteTask(progressBars(i), input)
          remoteTasks.append(t)
          val url = new URL(urls(i).getText.toString + input.getText.toString)
          t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url)
        } else {
          val t = new PrimeCheckerLocalTask(progressBars(i), input)
          localTasks.append(t)
          t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
            input.getText.toString.toLong: java.lang.Long)
        }
    }
  }

  def onCancel(view: View) {
    localTasks foreach { _ cancel true }
    remoteTasks foreach { _ cancel true }
    localTasks.clear()
    remoteTasks.clear()
  }

  def onWorker(number: Int, enabled: Boolean) {
    workers(number) = enabled
  }

  def onRemote(number: Int, enabled: Boolean) {
    remotes(number) = enabled
  }

  def onWorker1(view: View) { onWorker(0, view.asInstanceOf[ToggleButton].isChecked) }
  def onWorker2(view: View) { onWorker(1, view.asInstanceOf[ToggleButton].isChecked) }
  def onWorker3(view: View) { onWorker(2, view.asInstanceOf[ToggleButton].isChecked) }
  def onRemote1(view: View) { onRemote(0, view.asInstanceOf[ToggleButton].isChecked) }
  def onRemote2(view: View) { onRemote(1, view.asInstanceOf[ToggleButton].isChecked) }
  def onRemote3(view: View) { onRemote(2, view.asInstanceOf[ToggleButton].isChecked) }
}
