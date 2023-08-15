package pl.shop.toyshop.ui.adminPanel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.AdminService
import pl.shop.toyshop.ui.login_signup.LoginViewModel

class adminPanelFragment : Fragment() {

val adminService = AdminService()

    private val sharedViewModel: AdminPanelViewModel by activityViewModels()
    private val sharedViewModelLogin: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_panel, container, false)

        val username = sharedViewModelLogin.login.value.toString()
        val password = sharedViewModelLogin.password.value.toString()
        lifecycleScope.launch {

            val userAll = adminService.getUserAll(requireContext(), username, password)
            val mainLinearAdminList = view.findViewById<LinearLayout>(R.id.mainLinearAdminList)


            for (user in userAll) {

                val listView = layoutInflater.inflate(R.layout.fragment_admin_panel_list_user, mainLinearAdminList, false)
                listView.id = R.id.userView

                val emailTextView = listView.findViewById<TextView>(R.id.adminPanelEmailList)
                val nameTextView = listView.findViewById<TextView>(R.id.adminPanelNameList)
                val surnameTextView = listView.findViewById<TextView>(R.id.adminPanelSurnameList)

                if (user.email.isNotEmpty()) {
                    emailTextView.text = user.email
                }

                if (user.name?.isNotEmpty() == true){
                    nameTextView.text = user.name
                }

                if (user.surname?.isNotEmpty() == true){
                    surnameTextView.text = user.surname
                }


                emailTextView.setOnClickListener {
                    sharedViewModel.user.value = user
                    findNavController().navigate(R.id.action_nav_adminPanel_to_adminPanelFragmentDetails)
                }

                mainLinearAdminList.addView(listView)
            }


        }



        return view
    }



}