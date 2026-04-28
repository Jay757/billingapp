package com.aslibill

import android.app.Application
import com.aslibill.data.AppContainer

class BillSuperApplication : Application() {
  lateinit var container: AppContainer
    private set

  override fun onCreate() {
    super.onCreate()
    container = AppContainer(this)
  }
}

