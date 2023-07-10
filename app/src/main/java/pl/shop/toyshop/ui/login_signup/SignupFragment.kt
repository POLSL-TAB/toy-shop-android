package pl.shop.toyshop.ui.login_signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.shop.toyshop.R
import pl.shop.toyshop.service.BackPressedHandler

class SignupFragment : Fragment() {

    private lateinit var backPressedHandler: BackPressedHandler
    private lateinit var viewModel: SignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        backPressedHandler = BackPressedHandler(this)
        backPressedHandler.register()


        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()

        backPressedHandler.unregister()
    }

}