package pl.shop.toyshop.service

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.shop.toyshop.model.AddingProduct
import pl.shop.toyshop.model.User


class LoginAndSignupService {

   private val urlItems = "${GlobalVariables.serverIpAddress}cart/items"
    private val urlStaff = "${GlobalVariables.serverIpAddress}staff/complaints/all"
    private val urlAdmin = "${GlobalVariables.serverIpAddress}admin/users/all"
    private val urlSignup= "${GlobalVariables.serverIpAddress}auth/signup"
    private val client = OkHttpClient()
    private val gson = Gson()
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

    suspend fun registerUser(
        context: Context,
        email: String,
        password: String,
        name: String = "",
        surname: String = ""
    ) {
        val addUser = User(email, password, name, surname)

        val json = gson.toJson(addUser)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(urlSignup)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Zarejestrowano pomyślnie", Toast.LENGTH_LONG).show()
            }

        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Użytkownik o podanym emailu już istnieje", Toast.LENGTH_LONG).show()
            }

        }
    }


}