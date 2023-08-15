package pl.shop.toyshop.service

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import pl.shop.toyshop.model.AddingProduct
import pl.shop.toyshop.model.Complaint
import pl.shop.toyshop.model.OrderComplaintDetails
import pl.shop.toyshop.model.Picture
import java.io.IOException

class ComplaintService {

    private val urlGetAllComplaint = "${GlobalVariables.serverIpAddress}staff/complaints/all"
    private val urlChangeStatusComplaint = "${GlobalVariables.serverIpAddress}staff/complaints/update"



    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getAllComplaint(context: Context, username: String,
                                password: String): ArrayList<Complaint> = withContext(Dispatchers.IO) {
        try {

            val credentials = Credentials.basic(username, password)
            val requestComplaint = Request.Builder()
                .url(urlGetAllComplaint)
                .header("Authorization", credentials)
                .build()

            val responseComplaint = client.newCall(requestComplaint).execute()
            val responseBodyComplaint = responseComplaint.body?.string() ?: ""

            if (responseComplaint.isSuccessful && responseBodyComplaint.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<Complaint>>() {}.type
                return@withContext gson.fromJson<ArrayList<Complaint>>(responseBodyComplaint, listType)
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



    suspend fun updateComplaint(
        context: Context,
        id: Int,
        status: String,
        username: String,
        password: String
    ) {


        val credentials = Credentials.basic(username, password)

        val jsonObject  = JsonObject()
        jsonObject.addProperty("id", id)
        jsonObject.addProperty("status", status)
        val requestBody = RequestBody.Companion.create(
            "application/json".toMediaTypeOrNull(),
            jsonObject.toString()
        )

        val request = Request.Builder()
            .url(urlChangeStatusComplaint)
            .post(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body?.string()
                    Toast.makeText(context, "Status Reklamacji został zaaktualizowany", Toast.LENGTH_LONG)
                        .show()

                } else {
                    Toast.makeText(context, "Błąd aktualizacji statusu", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

}