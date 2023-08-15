package pl.shop.toyshop.ui.adminPanel

import android.app.AlertDialog
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.AdminService
import pl.shop.toyshop.ui.complaints.ComplaintsViewModel
import pl.shop.toyshop.ui.login_signup.LoginViewModel


class adminPanelFragmentDetails : Fragment() {
    private val sharedAdminViewModel: AdminPanelViewModel by activityViewModels()
    private val sharedViewModelLogin: LoginViewModel by activityViewModels()
    private val adminService = AdminService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_panel_details, container, false)
        val username = sharedViewModelLogin.login.value.toString()
        val password = sharedViewModelLogin.password.value.toString()
        val email = sharedAdminViewModel.user.value?.email.toString()
        val name = sharedAdminViewModel.user.value?.name.toString()
        val surname = sharedAdminViewModel.user.value?.surname.toString()

        val updateRoleButton = view.findViewById<Button>(R.id.updateRoleAdmin)
        val removeUserButton = view.findViewById<Button>(R.id.removeUserAdminButton)
        val emailTextView = view.findViewById<TextView>(R.id.emailAdminDetailsTextView)
        val nameTextView = view.findViewById<TextView>(R.id.nameAdminDetailsTextView)
        val surnameTextView = view.findViewById<TextView>(R.id.surnameAdminDetailsTextView)
        val userCheckbox = view.findViewById<CheckBox>(R.id.userCheckBox)
        val staffCheckbox = view.findViewById<CheckBox>(R.id.staffCheckBox)
        val adminCheckbox = view.findViewById<CheckBox>(R.id.adminCheckBox)



        emailTextView.text = email
        nameTextView.text = name
        surnameTextView.text = surname

        lifecycleScope.launch {
            val roleUser = adminService.getRoleUser(requireContext(), email, username, password)
            for (role in roleUser) {
                if (role == "USER") {
                    userCheckbox.isChecked = true
                }
                if (role == "STAFF") {
                    staffCheckbox.isChecked = true
                }
                if (role == "ADMIN") {
                    adminCheckbox.isChecked = true
                }
            }

        }


        updateRoleButton.setOnClickListener {
            lifecycleScope.launch {
                adminService.setRole(
                    requireContext(),
                    email,
                    userCheckbox.isChecked,
                    staffCheckbox.isChecked,
                    adminCheckbox.isChecked,
                    username,
                    password
                )
            }
        }

        removeUserButton.setOnClickListener {

            createConfirmDialog(email).show()

        }

        return view
    }

    private fun createConfirmDialog(email: String): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Potwierdź usunięcie")
            .setView(R.layout.dialog_confirm_delete)
            .setPositiveButton("Tak") { dialog, _ ->
                lifecycleScope.launch {
                    adminService.removeUser(
                        requireContext(),
                        email,
                        sharedViewModelLogin.login.value.toString(),
                        sharedViewModelLogin.password.value.toString()
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }


}