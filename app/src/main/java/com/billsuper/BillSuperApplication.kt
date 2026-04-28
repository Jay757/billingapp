package com.billsuper

import android.app.Application
import com.billsuper.data.AppContainer

class BillSuperApplication : Application() {
  lateinit var container: AppContainer
    private set

  override fun onCreate() {
    super.onCreate()
    container = AppContainer(this)
  }
}


