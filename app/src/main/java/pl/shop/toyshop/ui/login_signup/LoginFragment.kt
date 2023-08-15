package pl.shop.toyshop.ui.login_signup


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.LoginAndSignupService




class LoginFragment : Fragment() {

    private lateinit var navView: NavigationView
    private lateinit var progressBar: ProgressBar

    private val loginViewModel: LoginViewModel by activityViewModels()
    private val loginService = LoginAndSignupService()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        navView = requireActivity().findViewById(R.id.nav_view)
        val loginItem = navView.menu.findItem(R.id.nav_login)

        progressBar = view.findViewById(R.id.progressBarLogin)



        if (loginItem.title == getString(R.string.menu_wyloguj)){
            progressBar.visibility = View.VISIBLE


            loginItem.setIcon(R.drawable.baseline_login_24)
                loginItem.setTitle(R.string.menu_login)
                loginViewModel.login.value=null
                loginViewModel.password.value=null
                navView.menu.setGroupVisible(R.id.userGroup, false)
                navView.menu.setGroupVisible(R.id.staffGroup, false)
                navView.menu.setGroupVisible(R.id.adminPanelGroup, false)


                progressBar.visibility = View.GONE // Ukrywa ProgressBar

                findNavController().navigate(R.id.nav_home) // Przekierowanie na inny ekran



            Toast.makeText(context, "Wylogowane pomyślnie", Toast.LENGTH_LONG).show()
            }


        val emailText = view.findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordText = view.findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

         val email = "example@admin"
        val password = "example"

        loginButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {

            if (loginItem.title == getString(R.string.menu_login)) {
                val email = email //emailText.text.toString()
                val password = password //passwordText.text.toString()
                val userRole = loginService.checkingRoleUser(email, password)
                val staffRole = loginService.checkingRoleStaff(email, password)
                val adminRole = loginService.checkingRoleAdmin(email, password)

                if (userRole) {
                    navView.menu.setGroupVisible(R.id.userGroup, true)

                }
                if (staffRole) {
                    navView.menu.setGroupVisible(R.id.staffGroup, true)
                }
                if (adminRole) {

                    navView.menu.setGroupVisible(R.id.adminPanelGroup, true)
                }
                if (userRole || staffRole || adminRole) {
                    loginItem.setTitle(R.string.menu_wyloguj)
                    loginItem.setIcon(R.drawable.baseline_logout_24)

                    loginViewModel.login.value = email
                    loginViewModel.password.value = password
                    Toast.makeText(context, "Zalogowano pomyślnie", Toast.LENGTH_LONG).show()

                    progressBar.visibility = View.GONE // Ukrywa ProgressBar

                    findNavController().navigate(R.id.nav_home) // Przekierowanie na inny ekran


                } else {
                    progressBar.visibility = View.GONE // Ukrywa ProgressBar

                    Toast.makeText(context, "Niepoprawny e-mail lub hasło", Toast.LENGTH_LONG)
                        .show()

                }
            }

        }
        }


        val signup = view.findViewById<TextView>(R.id.signup)
            signup.setOnClickListener{
                findNavController().navigate(R.id.action_nav_login_to_signupFragment)
            }

        return view
    }








}
