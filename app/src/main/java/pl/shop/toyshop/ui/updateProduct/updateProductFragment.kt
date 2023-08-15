package pl.shop.toyshop.ui.updateProduct

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.model.Picture
import pl.shop.toyshop.model.Products
import pl.shop.toyshop.service.ProductService
import pl.shop.toyshop.ui.home.HomeViewModel

class updateProductFragment : Fragment() {

    private val sharedViewModel: UpdateProductViewModel by activityViewModels()
    private lateinit var progressBar: ProgressBar
    val productService = ProductService()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_product, container, false)
        progressBar = view.findViewById(R.id.ProgressbarUpdateProduct)

lifecycleScope.launch {
    progressBar.visibility = View.VISIBLE
    val productAllJson: ArrayList<Products> = productService.getProductAll(requireContext())
    val pictures: ArrayList<Picture> = productService.getPictureAll(requireContext())

    val listToUpdate = view.findViewById<LinearLayout>(R.id.listProductToUpdate)
    for (products in productAllJson) {
        val productView =
            layoutInflater.inflate(R.layout.fragment_update_product_list, listToUpdate, false)
        productView.id = R.id.productView
        val picture = productService.getPicturesById(pictures, products.id)

        val productName = productView.findViewById<TextView>(R.id.updateProductName2)
        val productImage = productView.findViewById<ImageView>(R.id.updateProductImage2)

        productName.text = products.name
        if (picture.isNotEmpty()) {
            val pictureImageDecode = productService.pictureB64ToImage(picture.first())
            productImage.setImageBitmap(pictureImageDecode)
        }

        productView.setOnClickListener {

            sharedViewModel.pictures.value = picture
            sharedViewModel.product.value = products
            findNavController().navigate(R.id.action_nav_updateProduct_to_updateProductMain)

        }

        listToUpdate.addView(productView)
    }
    progressBar.visibility = View.GONE

}

        return view
    }




}