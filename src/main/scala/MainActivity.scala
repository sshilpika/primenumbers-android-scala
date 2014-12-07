package edu.luc.etl.cs313.android.scala.primechecker

import android.os.{ AsyncTask, Bundle }
import android.app.Activity
import android.view.View
import android.widget.ToggleButton
import scala.collection.mutable.{ArraySeq, ArrayBuffer}

class MainActivity extends Activity with TypedActivity with RollbarSupport {

  private def input = findView(TR.inputCandidate)

  private val NUM = 3

  private def progressBars(i: Int) =
    Array(findView(TR.progressBar1), findView(TR.progressBar2), findView(TR.progressBar3)).apply(i)

  private def urls(i: Int) =
    Array(findView(TR.inputUrl1), findView(TR.inputUrl2), findView(TR.inputUrl2)).apply(i)

  private val workers = new ArraySeq[Boolean](NUM)

  private val remotes = new ArraySeq[Boolean](NUM)

  private val localTasks = new ArrayBuffer[PrimeCheckerLocalTask](NUM)

  private val remoteTasks = new ArrayBuffer[PrimeCheckerRemoteTask](NUM)

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
  }

  override def onDestroy() = {
    super.onDestroy()
    onCancel(input)
  }

  def onCheck(view: View): Unit = {
    onCancel(view)
    for (i <- 0 until NUM) {
      progressBars(i).setProgress(0)
      if (workers(i))
        if (remotes(i)) {
          val t = new PrimeCheckerRemoteTask(progressBars(i), input)
          remoteTasks.append(t)
          t.start(urls(i).getText.toString + input.getText.toString)
        } else {
          val t = new PrimeCheckerLocalTask(progressBars(i), input)
          localTasks.append(t)
          t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
            Int.box(input.getText.toString.toInt))
        }
    }
  }

  def onCancel(view: View): Unit = {
    localTasks foreach { _.cancel(true) }
    remoteTasks foreach { _.cancel() }
    localTasks.clear()
    remoteTasks.clear()
  }

  def onWorker(number: Int, enabled: Boolean): Unit = workers(number) = enabled

  def onRemote(number: Int, enabled: Boolean): Unit = remotes(number) = enabled

  def onWorker1(view: View): Unit = onWorker(0, view.asInstanceOf[ToggleButton].isChecked)
  def onWorker2(view: View): Unit = onWorker(1, view.asInstanceOf[ToggleButton].isChecked)
  def onWorker3(view: View): Unit = onWorker(2, view.asInstanceOf[ToggleButton].isChecked)
  def onRemote1(view: View): Unit = onRemote(0, view.asInstanceOf[ToggleButton].isChecked)
  def onRemote2(view: View): Unit = onRemote(1, view.asInstanceOf[ToggleButton].isChecked)
  def onRemote3(view: View): Unit = onRemote(2, view.asInstanceOf[ToggleButton].isChecked)
}
