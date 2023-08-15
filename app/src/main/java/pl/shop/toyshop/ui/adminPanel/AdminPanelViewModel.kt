package pl.shop.toyshop.ui.adminPanel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.shop.toyshop.model.User

class AdminPanelViewModel : ViewModel() {
    val user = MutableLiveData<User>()

}