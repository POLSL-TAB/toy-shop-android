package pl.shop.toyshop.service

import android.app.Application

class GlobalVariables: Application() {
    companion object {
        lateinit var serverIpAddress: String
    }

}