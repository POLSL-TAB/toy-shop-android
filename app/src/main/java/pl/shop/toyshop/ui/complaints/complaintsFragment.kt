package pl.shop.toyshop.ui.complaints

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.shop.toyshop.R

class complaintsFragment : Fragment() {

    companion object {
        fun newInstance() = complaintsFragment()
    }

    private lateinit var viewModel: ComplaintsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complaints, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ComplaintsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}