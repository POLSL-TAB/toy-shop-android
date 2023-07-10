package pl.shop.toyshop.service

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import pl.shop.toyshop.model.Order

import pl.shop.toyshop.model.ShoppingCart
import java.io.IOException

class OrderService {

    private val urlItemShoppingCartAll = "http://192.168.0.138:8080/api/cart/items"
    private val urlOrderUpdateQuantity = "http://192.168.0.138:8080/api/cart/add"
    private val urlDeleteProductShoppingCart = "http://192.168.0.138:8080/api/cart/delete?id="
    private val urlSubmitOrder = "http://192.168.0.138:8080/api/order/create"
    private val urlOrderAll = "http://192.168.0.138:8080/api/order/all"
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getItemShoppingAll(
        context: Context,
        username: String,
        password: String
    ): ArrayList<ShoppingCart> = withContext(Dispatchers.IO) {
        try {

            val credentials = Credentials.basic(username, password)

            val requestProduct = Request.Builder()
                .url(urlItemShoppingCartAll)
                .header("Authorization", credentials)
                .build()

            val responseOrders = client.newCall(requestProduct).execute()
            val responseBodyOrders = responseOrders.body?.string() ?: ""

            if (responseOrders.isSuccessful && responseBodyOrders.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<ShoppingCart>>() {}.type
                return@withContext gson.fromJson<ArrayList<ShoppingCart>>(
                    responseBodyOrders,
                    listType
                )
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

    suspend fun updateOrAddQuantityByProductId(
        context: Context,
        shoppingCartId: Int,
        quantity: Int,
        username: String,
        password: String
    ) {
        val shoppingCart = ShoppingCart()
        shoppingCart.productId = shoppingCartId.toString()
        shoppingCart.quantity = quantity.toString()
        val json = gson.toJson(shoppingCart)

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val credentials = Credentials.basic(username, password)

        val request = Request.Builder()
            .url(urlOrderUpdateQuantity)
            .post(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()


            if (response.isSuccessful) {
                 response.body?.string()

            } else {
                Toast.makeText(context, "Błąd dodawania/odejmowania produktu", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

        suspend fun deleteProductShoppingCart(context: Context,username: String,
                                              password: String,productId: Int){

            val credentials = Credentials.basic(username, password)

            val request = Request.Builder()
                .url(urlDeleteProductShoppingCart + productId)
                .delete()
                .header("Authorization", credentials)
                .build()

            withContext(Dispatchers.IO){
                val response = client.newCall(request).execute()

                if (response.isSuccessful){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Produkt został usunięty", Toast.LENGTH_LONG).show()
                    }
                }else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Nie udało się usunąć produktu", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }



    suspend fun submitOrder(context: Context,username: String,password: String){

        val credentials = Credentials.basic(username, password)

        val request = Request.Builder()
            .url(urlSubmitOrder)
            .put(create(null,""))
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO){
            val response = client.newCall(request).execute()

            if (response.isSuccessful){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Zamówienie zostało złożone", Toast.LENGTH_LONG).show()
                }
            }else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Nie udało się złożyć zamówienia", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    suspend fun getOrderAll(
        context: Context,
        username: String,
        password: String
    ): ArrayList<Order> = withContext(Dispatchers.IO) {
        try {
            val credentials = Credentials.basic(username, password)

            val request = Request.Builder()
                .url(urlOrderAll)
                .header("Authorization", credentials)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (response.isSuccessful && responseBody.isNotEmpty()) {
                val listType = object : TypeToken<ArrayList<Order>>() {}.type
                return@withContext gson.fromJson<ArrayList<Order>>(responseBody, listType)
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




}