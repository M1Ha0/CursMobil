package com.example.mycursmob

import android.app.Application

class FitnessApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: FitnessApp
            private set
    }
}
