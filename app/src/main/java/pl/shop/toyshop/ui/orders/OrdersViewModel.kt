package pl.shop.toyshop.ui.orders


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.shop.toyshop.model.Order

class OrdersViewModel : ViewModel() {

    val order =  MutableLiveData<Order>()
}