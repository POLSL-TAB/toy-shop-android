package pl.shop.toyshop.service

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.shop.toyshop.model.OrderComplaint
import pl.shop.toyshop.model.OrderComplaintDetails
import java.io.IOException


class OrdersComplaintService {


    private val urlCreateOrderComplaint = "${GlobalVariables.serverIpAddress}order/complaint/create"
    private val urlGetOrderComplaint = "${GlobalVariables.serverIpAddress}order/complaint/get?orderId="

    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun updateOrAddQuantityByProductId(
        context: Context,
        orderId: Int,
        reason: String,
        username: String,
        password: String
    ) {

        val orderComplaint = OrderComplaint(orderId, reason)

        val json = gson.toJson(orderComplaint)

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val credentials = Credentials.basic(username, password)

        val request = Request.Builder()
            .url(urlCreateOrderComplaint)
            .put(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()


            if (response.isSuccessful) {
                response.body?.string()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Zgłoszenie reklamacji zostało wysłane",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Błąd wysyłania zgłoszenia reklamacji",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }




    suspend fun getOrderComplainById(context: Context, id: Int, username: String,
                                     password: String): OrderComplaintDetails? = withContext(Dispatchers.IO) {
        try {

            val credentials = Credentials.basic(username, password)
            val requestProduct = Request.Builder()
                .url(urlGetOrderComplaint + id)
                .header("Authorization", credentials)
                .build()

            val responseOrder = client.newCall(requestProduct).execute()
            val responseBodyOrder = responseOrder.body?.string() ?: ""

            if (responseOrder.isSuccessful && responseBodyOrder.isNotEmpty()) {
                // Deserializacja JSON do obiektu
                val product = gson.fromJson(responseBodyOrder, OrderComplaintDetails::class.java)
                return@withContext product
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
        return@withContext null
    }
}