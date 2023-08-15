package pl.shop.toyshop.ui.login_signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.BackPressedHandler
import pl.shop.toyshop.service.LoginAndSignupService

class SignupFragment : Fragment() {

    private lateinit var backPressedHandler: BackPressedHandler
    private lateinit var viewModel: SignupViewModel
    private val loginService = LoginAndSignupService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        backPressedHandler = BackPressedHandler(this)
        backPressedHandler.register()

        val email = view.findViewById<EditText>(R.id.emailSignupEditText)
        val password = view.findViewById<EditText>(R.id.passwordSignupEditText)
        val name = view.findViewById<EditText>(R.id.nameSignupEditText)
        val surname = view.findViewById<EditText>(R.id.surnameSignupEditText)
        val signupButton = view.findViewById<Button>(R.id.signupButton)


        signupButton.setOnClickListener {

            lifecycleScope.launch {
                if (name.text.isNotEmpty() && surname.text.isNotEmpty()) {
                     loginService.registerUser(
                        requireContext(),
                        email.text.toString(),
                        password.text.toString(),
                        name.text.toString(),
                        surname.text.toString()
                    )
                    findNavController().navigate(R.id.action_signupFragment_to_nav_login)

                } else if (name.text.isNotEmpty()) {
                      loginService.registerUser(
                        requireContext(),
                        email.text.toString(),
                        password.text.toString(),
                        name.text.toString())
                    findNavController().navigate(R.id.action_signupFragment_to_nav_login)


                } else if (surname.text.isNotEmpty()) {
                    loginService.registerUser(
                        requireContext(),
                        email.text.toString(),
                        password.text.toString(),
                        "",
                        surname.text.toString()
                    )
                    findNavController().navigate(R.id.action_signupFragment_to_nav_login)

                } else {
                     loginService.registerUser(
                        requireContext(),
                        email.text.toString(),
                        password.text.toString()
                    )
                    findNavController().navigate(R.id.action_signupFragment_to_nav_login)

                }

            }
        }



        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()

        backPressedHandler.unregister()
    }

}