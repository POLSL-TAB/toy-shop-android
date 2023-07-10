package pl.shop.toyshop.ui.login_signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    val login = MutableLiveData<String>()
    val password  = MutableLiveData<String>()
    val role = MutableLiveData<String>()
}