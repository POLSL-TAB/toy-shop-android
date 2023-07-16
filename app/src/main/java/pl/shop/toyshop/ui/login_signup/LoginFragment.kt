package pl.shop.toyshop.ui.login_signup


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextPaint
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import pl.shop.toyshop.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


class LoginFragment : Fragment() {

    private lateinit var navView: NavigationView
    private lateinit var executor: ExecutorService

    private val loginViewModel: LoginViewModel by activityViewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        navView = requireActivity().findViewById(R.id.nav_view)
        val loginItem = navView.menu.findItem(R.id.nav_login)


            if (loginItem.title == getString(R.string.menu_wyloguj)){
                loginItem.setIcon(R.drawable.baseline_login_24)
                loginItem.setTitle(R.string.menu_login)
                loginViewModel.login.value=null
                loginViewModel.password.value=null
                navView.menu.setGroupVisible(R.id.userGroup, false)
                navView.menu.setGroupVisible(R.id.staffGroup, false)
                navView.menu.setGroupVisible(R.id.adminPanelGroup, false)
                findNavController().navigate(R.id.nav_home)
                Toast.makeText(context, "Wylogowane pomyślnie", Toast.LENGTH_LONG).show()
            }



        executor = Executors.newSingleThreadExecutor()
        val emailText = view.findViewById<EditText>(R.id.editTextTextEmailAddress)
        val passwordText = view.findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = view.findViewById<Button>(R.id.loginButton)

        val urlItems = "http://192.168.0.138:8080/api/cart/items"
        val urlStaff = "http://192.168.0.138:8080/api/staff/complaints/all"
        val urlAdmin = "http://192.168.0.138:8080/api/admin/users/all"
         val email = "example@staff"
        val password = "example"

        loginButton.setOnClickListener {
            if (loginItem.title == getString(R.string.menu_login)){
            val email = email  //emailText.text.toString()
            val password =  password  //passwordText.text.toString()
            val userRole = checkingRole(email, password, urlItems)
            val staffRole = checkingRole(email, password, urlStaff)
            val adminRole = checkingRole(email, password, urlAdmin)

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

                findNavController().navigate(R.id.nav_home)
            }else{
                Toast.makeText(context, "Niepoprawny e-mail lub hasło", Toast.LENGTH_LONG).show()

            }

        }
    }

        val signup = view.findViewById<TextView>(R.id.signup)
            signup.setOnClickListener{
                findNavController().navigate(R.id.action_nav_login_to_signupFragment)
            }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }


    private fun checkingRole(email: String, password: String, url: String):Boolean {
        val credentials = Credentials.basic(email, password)
        val future: Future<Boolean> = executor.submit<Boolean> {

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", credentials)
                .build()
            val response = client.newCall(request).execute()

            response.isSuccessful
        }
        try {
            return future.get()
        } catch (e: Exception) {
            Toast.makeText(context, "Nie można połączyć się z serwerem", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    }
