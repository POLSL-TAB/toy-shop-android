package pl.shop.toyshop.ui.addProduct


import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.net.Uri

import android.os.Bundle
import android.util.Base64
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.ProductService
import pl.shop.toyshop.ui.login_signup.LoginViewModel
import java.io.ByteArrayOutputStream

class addProductFragment : Fragment() {


    private lateinit var viewModel: AddProductViewModel
    private val sharedViewModel: LoginViewModel by activityViewModels()

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var imageUris: MutableList<Uri>
    private lateinit var listImageNewProduct: LinearLayout
    private lateinit var imageViewNewProduct: ImageView
    private lateinit var progressBar: ProgressBar
    val productService = ProductService()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        val nameNewProduct = view.findViewById<TextView>(R.id.nameNewProduct)
        val descriptionNewProduct = view.findViewById<TextView>(R.id.descriptionNewProduct)
        val priceNewProduct = view.findViewById<TextView>(R.id.priceNewProduct)
        val stockNewProduct = view.findViewById<TextView>(R.id.stockNewProduct)
        val addImageButton = view.findViewById<Button>(R.id.addNewImageButton)
        val addProductButton = view.findViewById<Button>(R.id.addNewProductButton)
        val scrollViewImages = view.findViewById<HorizontalScrollView>(R.id.scrollViewImages)
        progressBar = view.findViewById(R.id.progressBarAddProduct)

        imageViewNewProduct = view.findViewById(R.id.ImageNewProduct)
        listImageNewProduct = view.findViewById(R.id.listImageNewProduct)

        imageUris = mutableListOf()
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                imageUris.add(imageUri)
                displayImagesInContainer(listImageNewProduct)
                scrollViewImages.visibility = View.VISIBLE
            }
        }


        addImageButton.setOnClickListener {
            getContent.launch("image/*")
        }

        addProductButton.setOnClickListener {
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE


                productService.addProduct(
                    requireContext(),
                    nameNewProduct.text.toString(),
                    descriptionNewProduct.text.toString(),
                    priceNewProduct.text.toString(),
                    stockNewProduct.text.toString().toInt(),
                    sharedViewModel.login.value.toString(),
                    sharedViewModel.password.value.toString()
                )

                val productAll = productService.getProductAll(requireContext())

                for (products in productAll) {
                    if (products.name == nameNewProduct.text.toString()) {
                        for (i in 0 until listImageNewProduct.childCount) {
                            val childView = listImageNewProduct.getChildAt(i)
                            if (childView is ImageView) {
                                val drawable = childView.drawable
                                if (drawable is BitmapDrawable) {
                                    val bitmap = drawable.bitmap
                                    val byteArrayOutputStream = ByteArrayOutputStream()
                                    bitmap.compress(
                                        Bitmap.CompressFormat.PNG,
                                        100,
                                        byteArrayOutputStream
                                    )
                                    val byteArray = byteArrayOutputStream.toByteArray()
                                    val encodedImage =
                                        Base64.encodeToString(byteArray, Base64.NO_WRAP)
                                    productService.addImage(
                                        requireContext(),
                                        products.id,
                                        encodedImage,
                                        sharedViewModel.login.value.toString(),
                                        sharedViewModel.password.value.toString()
                                    )
                                }
                            }
                        }

                        nameNewProduct.text = ""
                        descriptionNewProduct.text = ""
                        priceNewProduct.text = ""
                        stockNewProduct.text = ""
                        scrollViewImages.visibility = View.GONE
                        break
                    }
                }
                progressBar.visibility = View.GONE

                Toast.makeText(context, "produkt został dodany", Toast.LENGTH_LONG)
                    .show()

            }
        }

        return view
    }


    private fun displayImagesInContainer(container: LinearLayout) {
        container.removeAllViews()

        for (imageUri in imageUris) {
            val imageView = ImageView(requireContext())

            val dpSize = 200
            val pixelSize = (dpSize * resources.displayMetrics.density).toInt()

            val layoutParams = LinearLayout.LayoutParams(pixelSize, pixelSize)
            imageView.layoutParams = layoutParams
            imageView.setImageURI(imageUri)
            container.addView(imageView)
        }
    }

}