package pl.shop.toyshop.ui.updateProduct

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.shop.toyshop.R

class updateProductFragment : Fragment() {

    companion object {
        fun newInstance() = updateProductFragment()
    }

    private lateinit var viewModel: UpdateProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_product, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(UpdateProductViewModel::class.java)
        // TODO: Use the ViewModel
    }

}