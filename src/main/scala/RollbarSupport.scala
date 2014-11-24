package edu.luc.etl.cs313.android.scala.primechecker

import android.util.Log
import android.app.Activity
import android.os.Bundle
import com.rollbar.android.Rollbar

/** Mixin (stackable trait) for the Rollbar cloud-based logging service. */
trait RollbarSupport extends Activity {

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "initializing Rollbar logging")
    Rollbar.init(this, "76db56167ee949ffba208fd3e6331241", "production")
    Rollbar.reportMessage("Starting primenumber-android-scala", "debug")
  }
}
