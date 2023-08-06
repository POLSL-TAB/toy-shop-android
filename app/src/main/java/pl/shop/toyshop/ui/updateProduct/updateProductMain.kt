package pl.shop.toyshop.ui.updateProduct

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.model.Picture
import pl.shop.toyshop.model.Products
import pl.shop.toyshop.service.BackPressedHandler
import pl.shop.toyshop.service.ProductService
import pl.shop.toyshop.ui.login_signup.LoginViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class updateProductMain : Fragment() {



    private  val sharedViewModel: UpdateProductViewModel by activityViewModels()
    private  val sharedViewModelLogin: LoginViewModel by activityViewModels()
    private lateinit var backPressedHandler: BackPressedHandler
    val productService = ProductService()
    private lateinit var buttonUpdateProduct: Button

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var imageUris: MutableList<Uri>
    private lateinit var ListImagesUpdateProduct: LinearLayout
    private lateinit var imageProductUpdate: ImageView
    private lateinit var addImageUpdateProduct: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_update_product_main, container, false)
        backPressedHandler = BackPressedHandler(this)
        backPressedHandler.register()


        val productName = view.findViewById<EditText>(R.id.productNameToUpdate)
        val productPrice = view.findViewById<EditText>(R.id.priceProductToUpdate)
        val productCount = view.findViewById<EditText>(R.id.countProductToUpdate)
        val productDescribe = view.findViewById<EditText>((R.id.describeProductToUpdate))
        buttonUpdateProduct = view.findViewById((R.id.buttonUpdateProduct))
        ListImagesUpdateProduct = view.findViewById(R.id.ListImagesUpdateProduct)
        imageProductUpdate = view.findViewById(R.id.imageProductUpdate)
        addImageUpdateProduct = view.findViewById(R.id.addImageUpdateProduct)
        val ScrollviewUpdateproductImage = view.findViewById<HorizontalScrollView>(R.id.ScrollviewUpdateproductImage)


        productName.setText(sharedViewModel.product.value?.name.toString())
        productPrice.setText(sharedViewModel.product.value?.price.toString())
        productCount.setText(sharedViewModel.product.value?.stock.toString())
        productDescribe.setText(sharedViewModel.product.value?.description.toString())


        imageUris = mutableListOf()
        loadPicturesToImageUris()

        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                imageUris.add(imageUri)
                displayImagesInContainer(ListImagesUpdateProduct)
                ScrollviewUpdateproductImage.visibility = View.VISIBLE
            }
        }

        addImageUpdateProduct.setOnClickListener {
            getContent.launch("image/*")
        }



        buttonUpdateProduct.setOnClickListener {
            lifecycleScope.launch {
                productService.updateProduct(
                    requireContext(),
                    sharedViewModel.product.value!!.id,
                    productName.text.toString(),
                    productDescribe.text.toString(),
                    productPrice.text.toString(),
                    productCount.text.toString().toInt(),
                    sharedViewModelLogin.login.value.toString(),
                    sharedViewModelLogin.password.value.toString()
                )

                val productAll = productService.getProductAll(requireContext())

                for (products in productAll) {
                    if (products.name == sharedViewModel.product.value!!.name) {
                        findImageViewAndProcess(products, ListImagesUpdateProduct)
                        break
                    }
                }
            }
        }




        return view
    }

    override fun onResume() {
        super.onResume()

        val rootView = requireView().rootView

        // Sprawdź, czy klawiatura jest widoczna przy starcie Fragmentu
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        if (keypadHeight > screenHeight * 0.15) {
            hideButton()
        }

        // Monitoruj zmiany widoczności klawiatury
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) {
                // Klawiatura jest widoczna, ukryj przycisk
                hideButton()
            } else {
                // Klawiatura jest ukryta, pokaż przycisk
                showButton()
            }
        }
    }

    private fun hideButton() {
        buttonUpdateProduct.visibility = View.GONE
    }

    private fun showButton() {
        buttonUpdateProduct.visibility = View.VISIBLE
    }

    private fun loadPicturesToImageUris() {
        sharedViewModel.pictures.value?.let { pictures ->
            imageUris.clear()

            for (picture in pictures) {
                val uri = decodeBase64ToUri(requireContext(), picture.pictureB64)
                imageUris.add(uri)
            }

            displayImagesInContainer(ListImagesUpdateProduct)
        }
    }


    private fun decodeBase64ToUri(context: Context, base64String: String): Uri {
        val imageByteArray = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)

        val tempDir = context.cacheDir // Katalog tymczasowy aplikacji
        val imageFile = File.createTempFile("image_", ".png", tempDir)

        try {
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        } catch (e: Exception) {
            // Obsługa wyjątku, jeśli jest to potrzebne
            e.printStackTrace()
        }

        return Uri.fromFile(imageFile)
    }









      private suspend fun findImageViewAndProcess(products: Products, viewGroup: ViewGroup) {
          for (i in 0 until viewGroup.childCount) {
              val childView = viewGroup.getChildAt(i)

              if (childView is ImageView) {

                  val drawable = childView.drawable
                  if (drawable is BitmapDrawable) {
                      val bitmap = drawable.bitmap
                      val byteArrayOutputStream = ByteArrayOutputStream()
                      bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                      val byteArray = byteArrayOutputStream.toByteArray()
                      val encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                      productService.addImage(
                          requireContext(),
                          products.id,
                          encodedImage,
                          sharedViewModelLogin.login.value.toString(),
                          sharedViewModelLogin.password.value.toString()
                      )
                  }
              } else if (childView is ViewGroup) {
                  // Jeśli znaleziony widok jest ViewGroup (np. LinearLayout),
                  // wykonujemy rekurencyjnie tę samą funkcję, aby znaleźć ImageView
                  findImageViewAndProcess(products, childView)
              }
          }
      }




    private fun displayImagesInContainer(container: LinearLayout) {
        container.removeAllViews()

        for (imageUri in imageUris) {
            val linearLayout = createVerticalLinearLayout(requireContext())
            val imageView = ImageView(requireContext())
            val button = createButton(requireContext())

            val dpSize = 150
            val pixelSize = (dpSize * resources.displayMetrics.density).toInt()

            val layoutParams = LinearLayout.LayoutParams(pixelSize, pixelSize)
            imageView.layoutParams = layoutParams
            imageView.setImageURI(imageUri)

            button.setOnClickListener {
                val index = container.indexOfChild(linearLayout)
                if (index >= 0) {
                    imageUris.removeAt(index)
                    container.removeViewAt(index)
                }
            }

            linearLayout.addView(imageView)
            linearLayout.addView(button)
            container.addView(linearLayout)


        }
    }


    private fun createButton(context: Context): Button {
        val button = Button(context)

        // Ustawiamy ID przycisku (opcjonalne, jeśli potrzebujesz identyfikatora do późniejszego odwoływania się do tego przycisku)
        button.id = View.generateViewId()

        // Ustawiamy szerokość i wysokość przycisku
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        button.width = 100
        button.height = 100
        button.layoutParams = layoutParams
        button.backgroundTintList = context.getColorStateList(android.R.color.holo_red_light)
        button.text = "Usuń zdjęcie"
        button.visibility = View.VISIBLE

        return button
    }

    private fun createVerticalLinearLayout(context: Context): LinearLayout {
        val linearLayout = LinearLayout(context)

        // Ustawiamy orientację układu na pionową
        linearLayout.orientation = LinearLayout.VERTICAL

        // Ustawiamy szerokość i wysokość układu (opcjonalne, zależy od wymagań)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(0,0,10,0)
        linearLayout.layoutParams = layoutParams
        linearLayout.visibility = View.VISIBLE

        return linearLayout
    }


    override fun onDestroyView() {
        super.onDestroyView()

        backPressedHandler.unregister()
    }

}


