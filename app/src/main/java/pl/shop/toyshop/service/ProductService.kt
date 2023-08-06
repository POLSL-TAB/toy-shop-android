package pl.shop.toyshop.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import pl.shop.toyshop.model.*
import java.io.IOException

class ProductService {
    private val urlProductAll = "http://192.168.0.138:8080/api/products/all"
    private val urlProductById = "http://192.168.0.138:8080/api/products/get?id="
    private val urlPictureAll = "http://192.168.0.138:8080/api/products/images/all"
    private  var  urlPictureById = "http://192.168.0.138:8080/api/products/images?productId="
    private  var  urlAddProduct = "http://192.168.0.138:8080/api/staff/products/add"
    private  var  urlAddPicture = "http://192.168.0.138:8080/api/staff/products/images/add"
    private  var  urlUpdateProduct = "http://192.168.0.138:8080/api/staff/products/update"
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getProductAll(context: Context): ArrayList<Products> = withContext(Dispatchers.IO) {
        try {
            println("getProductAll() is called")
            val requestProduct = Request.Builder()
                .url(urlProductAll)
                .build()

            val responseProduct = client.newCall(requestProduct).execute()
            val responseBodyProduct = responseProduct.body?.string() ?: ""

            if (responseProduct.isSuccessful && responseBodyProduct.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<Products>>() {}.type
                val productList = gson.fromJson<ArrayList<Products>>(responseBodyProduct, listType)
                println("getProductAll() - Received ${productList.size} products")
                return@withContext productList
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

    suspend fun getPictureAll(context: Context): ArrayList<Picture> = withContext(Dispatchers.IO) {
        try {
            println("getPictureAll() is called")
            val requestPicture = Request.Builder()
                .url(urlPictureAll)
                .build()

            val responsePicture = client.newCall(requestPicture).execute()
            val responseBodyPicture = responsePicture.body?.string() ?: ""

            if (responsePicture.isSuccessful && responseBodyPicture.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<Picture>>() {}.type
                val pictureList = gson.fromJson<ArrayList<Picture>>(responseBodyPicture, listType)
                println("getPictureAll() - Received ${pictureList.size} pictures")
                return@withContext pictureList
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

    suspend fun getProductById(context: Context, id: Int): Products? = withContext(Dispatchers.IO) {
        try {
            val requestProduct = Request.Builder()
                .url(urlProductById + id)
                .build()

            val responseProduct = client.newCall(requestProduct).execute()
            val responseBodyProduct = responseProduct.body?.string() ?: ""

            if (responseProduct.isSuccessful && responseBodyProduct.isNotEmpty()) {
                // Deserializacja JSON do obiektu
                val product = gson.fromJson(responseBodyProduct, Products::class.java)
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


    suspend fun getPictureById(context: Context, id: Int): ArrayList<Picture> = withContext(Dispatchers.IO) {
        try {
            val requestPicture = Request.Builder()
                .url(urlPictureById + id)
                .build()

            val responsePicture = client.newCall(requestPicture).execute()
            val responseBodyPicture = responsePicture.body?.string() ?: ""

            if (responsePicture.isSuccessful && responseBodyPicture.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<Picture>>() {}.type
                return@withContext gson.fromJson<ArrayList<Picture>>(responseBodyPicture, listType)
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


    suspend fun addProduct(
        context: Context,
        nameProduct: String,
        description: String,
        price: String,
        stock: Int,
        username: String,
        password: String
    ) {
        val addProduct = AddingProduct(nameProduct, description, price, stock)

        val json = gson.toJson(addProduct)

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val credentials = Credentials.basic(username, password)

        val request = Request.Builder()
            .url(urlAddProduct)
            .post(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body?.string()

                } else {
                    Toast.makeText(context, "Błąd dodawania produktu", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    suspend fun addImage(
        context: Context,
        productId: Int,
        pictureB64: String,
        username: String,
        password: String
    ) {
        val addPicture = AddingPicture(productId, pictureB64)

        val json = gson.toJson(addPicture)

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val credentials = Credentials.basic(username, password)

        val request = Request.Builder()
            .url(urlAddPicture)
            .put(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()


            if (response.isSuccessful) {
                response.body?.string()


            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Błąd dodawania Zdjęć", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    suspend fun updateProduct(
        context: Context,
        id: Int,
        name: String,
        description: String,
        price: String,
        stock: Int,
        username: String,
        password: String
    ) {
        val updateProduct = UpdateProduct(id, name, description, price, stock)

        val json = gson.toJson(updateProduct)

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val credentials = Credentials.basic(username, password)

        val request = Request.Builder()
            .url(urlUpdateProduct)
            .post(requestBody)
            .header("Authorization", credentials)
            .build()

        withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    response.body?.string()
                    Toast.makeText(context, "Produkt został zaaktualizowany", Toast.LENGTH_LONG)
                        .show()

                } else {
                    Toast.makeText(context, "Błąd aktualizacji  produktu", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun getPicturesById(pictures: ArrayList<Picture>, idProducts: Int): ArrayList<Picture> {
        val newArrayPicture = ArrayList<Picture>()
        for (picture in pictures) {
            if (picture.productId == idProducts)
                newArrayPicture.add(picture)
        }
        return newArrayPicture
    }

    fun pictureB64ToImage(picture: Picture): Bitmap? {

            val base64ImageData = picture.pictureB64
            val decodedBytes = Base64.decode(base64ImageData, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            return bitmap
    }





}
