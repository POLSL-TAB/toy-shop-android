package pl.shop.toyshop.ui.adminPanel

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.shop.toyshop.R

class adminPanelFragment : Fragment() {

    companion object {
        fun newInstance() = adminPanelFragment()
    }

    private lateinit var viewModel: AdminPanelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_panel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AdminPanelViewModel::class.java)
        // TODO: Use the ViewModel
    }

}