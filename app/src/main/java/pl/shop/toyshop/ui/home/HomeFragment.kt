package pl.shop.toyshop.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.model.Picture
import pl.shop.toyshop.model.Products
import pl.shop.toyshop.service.ProductService

class HomeFragment : Fragment() {
    private val sharedViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val productService = ProductService()


        // Wywołanie funkcji asynchronicznie za pomocą korutyn
        lifecycleScope.launch {
            val productAllJson: ArrayList<Products> = productService.getProductAll(requireContext())
            val pictures: ArrayList<Picture> = productService.getPictureAll(requireContext())

            // Tworzenie i ustawianie widoków produktów
            val mainLinear = root.findViewById<LinearLayout>(R.id.mainLinear)
            for (products in productAllJson) {
                val productView =
                    layoutInflater.inflate(R.layout.fragment_home_list, mainLinear, false)
                productView.id = R.id.productView
                val picture = productService.getPicturesById(pictures, products.id)


                val productNameTextView = productView.findViewById<TextView>(R.id.name)
                val productPriceTextView = productView.findViewById<TextView>(R.id.price)
                val productStockTextView = productView.findViewById<TextView>(R.id.stock)
                val productImageView = productView.findViewById<ImageView>(R.id.imageToy)

                productNameTextView.text = products.name
                productPriceTextView.text = "${products.price} zł"
                productStockTextView.text = "Pozostało  ${products.stock} sztuk"
                // Ustawienie obrazka dla produktu (jeśli dostępny)
                if (picture.isNotEmpty()) {
                    val pictureImageDecode = productService.pictureB64ToImage(picture.first())
                    productImageView.setImageBitmap(pictureImageDecode)
                }

                productView.setOnClickListener {
                    sharedViewModel.pictures.value = picture
                    sharedViewModel.product.value = products
                    findNavController().navigate(R.id.action_nav_home_to_productDetailsFragment)
                }
                mainLinear.addView(productView)


            }
        }


        return root
    }


}