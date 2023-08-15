package pl.shop.toyshop.ui.complaints

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.shop.toyshop.model.Complaint
import pl.shop.toyshop.model.Products

class ComplaintsViewModel : ViewModel() {
    val complaint = MutableLiveData<Complaint>()

}