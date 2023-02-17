package com.creations.bawender.paperdoc

import android.app.Application

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PaperDoc.init(this)
    }
}