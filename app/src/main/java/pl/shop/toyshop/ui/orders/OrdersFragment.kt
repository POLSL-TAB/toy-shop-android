package pl.shop.toyshop.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pl.shop.toyshop.MainActivity
import pl.shop.toyshop.R
import pl.shop.toyshop.service.OrderService
import pl.shop.toyshop.service.ProductService
import pl.shop.toyshop.ui.login_signup.LoginViewModel


class OrdersFragment : Fragment() {
    private val sharedViewModel: LoginViewModel by activityViewModels()
    private val sharedViewModelOrdersFragment: OrdersViewModel by activityViewModels()
    private var orderService = OrderService()
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        val mainLinearOrder = view.findViewById<LinearLayout>(R.id.mainLinearOrder)
        progressBar = view.findViewById(R.id.progressBarOrders)

        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE

            val ordersAll = orderService.getOrderAll(
                requireContext(),
                sharedViewModel.login.value.toString(),
                sharedViewModel.password.value.toString()
            )

            for (order in ordersAll) {
                val productView =
                    layoutInflater.inflate(
                        R.layout.fragment_orders_list,
                        mainLinearOrder,
                        false
                    )
                productView.id = R.id.OrderViews

                val orderId = productView.findViewById<TextView>(R.id.orderId)
                val dateOrder = productView.findViewById<TextView>(R.id.dateOrder)

                orderId.text = "Zamówienie nr: ${order.id}"
                dateOrder.text = "${order.created}"


                orderId.setOnClickListener {


                    sharedViewModelOrdersFragment.order.value = order
                    findNavController().navigate(R.id.action_nav_orders_to_ordersDeatilsFragment)


                }


                mainLinearOrder.addView(productView)
            }

            progressBar.visibility = View.GONE
        }



        return view
    }


}