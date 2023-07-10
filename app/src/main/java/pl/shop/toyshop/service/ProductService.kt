package pl.shop.toyshop.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import pl.shop.toyshop.model.Picture
import pl.shop.toyshop.model.Products
import java.io.IOException

class ProductService {
    private val urlProductAll = "http://192.168.0.138:8080/api/products/all"
    private val urlProductById = "http://192.168.0.138:8080/api/products/get?id="
    private val urlPictureAll = "http://192.168.0.138:8080/api/products/images/all"
    private val urlPictureById = "http://192.168.0.138:8080/api/products/images?productId="
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getProductAll(context: Context): ArrayList<Products> = withContext(Dispatchers.IO) {
        try {
            val requestProduct = Request.Builder()
                .url(urlProductAll)
                .build()

            val responseProduct = client.newCall(requestProduct).execute()
            val responseBodyProduct = responseProduct.body?.string() ?: ""

            if (responseProduct.isSuccessful && responseBodyProduct.isNotEmpty()) {
                // Deserializacja JSON do listy obiektów
                val listType = object : TypeToken<ArrayList<Products>>() {}.type
                return@withContext gson.fromJson<ArrayList<Products>>(responseBodyProduct, listType)
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
            val requestPicture = Request.Builder()
                .url(urlPictureAll)
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
