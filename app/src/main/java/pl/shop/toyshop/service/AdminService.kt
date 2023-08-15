package pl.shop.toyshop.service

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import pl.shop.toyshop.model.User
import java.io.IOException

class AdminService {

    private  var  urlGetAllUsers = "${GlobalVariables.serverIpAddress}admin/users/all"
    private  var  urlSetRole = "${GlobalVariables.serverIpAddress}admin/users/role?"
    private  var  urlGetRole = "${GlobalVariables.serverIpAddress}admin/users/role?email="
    private  var  urlRemoveUser = "${GlobalVariables.serverIpAddress}admin/users/delete?email="

    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getUserAll(context: Context, username: String,
                           password: String): ArrayList<User> = withContext(Dispatchers.IO) {
        try {
            val credentials = Credentials.basic(username, password)

            val requestUser = Request.Builder()
                .url(urlGetAllUsers)
                .header("Authorization", credentials)
                .build()

            val responseUser = client.newCall(requestUser).execute()
            val responseBodyUser = responseUser.body?.string() ?: ""

            if (responseUser.isSuccessful && responseBodyUser.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<User>>() {}.type
                val userList = gson.fromJson<ArrayList<User>>(responseBodyUser, listType)
                println("getProductAll() - Received ${userList.size} products")
                return@withContext userList
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd pobierania danych", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Błąd połączenia z serwerem", Toast.LENGTH_LONG).show()
            }
        }
        return@withContext ArrayList()
    }



    suspend fun setRole(
        context: Context,
        email:String,
        roleUser: Boolean,
        roleStaff: Boolean,
        roleAdmin: Boolean,
        username: String,
        password: String
    ) {

        val credentials = Credentials.basic(username, password)

        val urlRoles = StringBuilder()
        urlRoles.append("${urlSetRole}email=${email}&roles=")
        if (roleUser) {
            urlRoles.append("USER,")
        }

        if (roleStaff) {
            urlRoles.append("STAFF,")
        }

        if (roleAdmin) {
            urlRoles.append("ADMIN,")
        }
        if (urlRoles.isNotEmpty()) {
            urlRoles.deleteCharAt(urlRoles.length - 1)
        }

        val requestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), "")

        val request = Request.Builder()
            .url(urlRoles.toString()) ///${urlSetRole}email=example@user&roles=USER,ADMIN")
            .post(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body?.string()
                    Toast.makeText(context, "Role zostały zaaktualizowane", Toast.LENGTH_LONG).show()


                } else {
                    Toast.makeText(context, "Błąd aktualizacji ról", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    suspend fun getRoleUser(context: Context,email: String, username: String, password: String): ArrayList<String> = withContext(Dispatchers.IO) {
        try {

            val credentials = Credentials.basic(username, password)

            val requestPicture = Request.Builder()
                .url(urlGetRole+email)
                .get()
                .header("Authorization", credentials)
                .build()

            val responseRole = client.newCall(requestPicture).execute()
            val responseBodyString = responseRole.body?.string() ?: ""

            if (responseRole.isSuccessful && responseBodyString.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<String>>() {}.type
                return@withContext gson.fromJson<ArrayList<String>>(responseBodyString, listType)
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd pobierania danych", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Błąd połączenia z serwerem", Toast.LENGTH_LONG).show()
            }
        }
        return@withContext ArrayList()
    }


    suspend fun removeUser(
        context: Context,
        email:String,
        username: String,
        password: String
    ) {

        val credentials = Credentials.basic(username, password)



        val request = Request.Builder()
            .url(urlRemoveUser + email)
            .delete()
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body?.string()
                    Toast.makeText(context, "użytkownik został usunięty", Toast.LENGTH_LONG).show()


                } else {
                    Toast.makeText(context, "Błąd usuwania użytkownika", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

}