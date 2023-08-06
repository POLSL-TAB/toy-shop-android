package pl.shop.toyshop.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request


class LoginService {

    val urlItems = "http://192.168.0.138:8080/api/cart/items"
    val urlStaff = "http://192.168.0.138:8080/api/staff/complaints/all"
    val urlAdmin = "http://192.168.0.138:8080/api/admin/users/all"

    suspend fun checkingRoleUser(email: String, password: String):Boolean = withContext(Dispatchers.IO) {
        val credentials = Credentials.basic(email, password)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlItems)
            .header("Authorization", credentials)
            .build()
        val response = client.newCall(request).execute()

        return@withContext response.isSuccessful


    }
    suspend fun checkingRoleStaff(email: String, password: String):Boolean = withContext(Dispatchers.IO) {
        val credentials = Credentials.basic(email, password)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlStaff)
            .header("Authorization", credentials)
            .build()
        val response = client.newCall(request).execute()

        return@withContext response.isSuccessful


    }
    suspend fun checkingRoleAdmin(email: String, password: String):Boolean = withContext(Dispatchers.IO) {
        val credentials = Credentials.basic(email, password)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlAdmin)
            .header("Authorization", credentials)
            .build()
        val response = client.newCall(request).execute()

        return@withContext response.isSuccessful


    }

}