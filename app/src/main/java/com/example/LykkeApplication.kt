package com.example

import android.app.Application
import com.example.integration.LykkeActivationManager

/**
 * Aplicación principal de Lykke.
 * Inicializa los gestores de activación global y monitoreo de infraestructura.
 */
class LykkeApplication : Application() {

    companion object {
        lateinit var instance: LykkeApplication
            private set
        lateinit var activationManager: LykkeActivationManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        activationManager = LykkeActivationManager(applicationContext)
    }
}
