package pl.shop.toyshop.ui.updateProduct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.shop.toyshop.model.Picture
import pl.shop.toyshop.model.Products

class UpdateProductViewModel : ViewModel() {
    val pictures = MutableLiveData<ArrayList<Picture>>()
    val product = MutableLiveData<Products>()
}