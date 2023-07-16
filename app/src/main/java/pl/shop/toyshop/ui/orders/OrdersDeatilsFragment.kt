package pl.shop.toyshop.ui.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.BackPressedHandler
import pl.shop.toyshop.service.ProductService
import pl.shop.toyshop.service.OrdersComplaintService
import pl.shop.toyshop.ui.login_signup.LoginViewModel

class OrdersDeatilsFragment : Fragment() {


    private val sharedViewModelLogin: LoginViewModel by activityViewModels()
    private val sharedViewModelOrders: OrdersViewModel by activityViewModels()
    private lateinit var backPressedHandler: BackPressedHandler
    val productService = ProductService()
    val ordersComplaintService = OrdersComplaintService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         val view = inflater.inflate(R.layout.fragment_orders_deatils, container, false)
        backPressedHandler = BackPressedHandler(this)
        backPressedHandler.register()
        val reportComplaint = view.findViewById<Button>(R.id.reportComplaintButton)
        val reportOrder = view.findViewById<Button>(R.id.reportOrderButton)
        val reportComplaintMain = view.findViewById<LinearLayout>(R.id.reportComplaintMain)
        val orderIdDetails = view.findViewById<TextView>(R.id.orderIdDetails)
        val emailOrderDetails = view.findViewById<TextView>(R.id.emailOrderDetails)
        val paidOrderDetails = view.findViewById<TextView>(R.id.paidOrderDetails)
        val returnedOrderDetails = view.findViewById<TextView>(R.id.returnedOrderDetails)
        val ordersView = view.findViewById<LinearLayout>(R.id.ordersView)
        val reasonComplaint = view.findViewById<EditText>(R.id.reasonComplaint)
        val scrollViewDetailsOrderComplain = view.findViewById<ScrollView>(R.id.ScrollViewDetailsOrderComplain)
        val dateSendReason = view.findViewById<TextView>(R.id.dateSendReason)
        val textViewReason = view.findViewById<TextView>(R.id.textViewReasonOrderComplaint)
        val orderProduct = sharedViewModelOrders.order.value?.oderItems

        orderIdDetails.text = "${orderIdDetails.text} ${sharedViewModelOrders.order.value?.id}"
        emailOrderDetails.text = "${emailOrderDetails.text} ${sharedViewModelOrders.order.value?.userEmail}"
        if (sharedViewModelOrders.order.value?.paid == true){
            paidOrderDetails.text= "${paidOrderDetails.text} Zapałacono"
        }else{
            paidOrderDetails.text=  "${paidOrderDetails.text} Niezapałacono"
        }

        if (sharedViewModelOrders.order.value?.returned == true){
            returnedOrderDetails.text = "${returnedOrderDetails.text} Tak"
            reportComplaint.text = getString(R.string.button_show_orders_complain)
        }else{
            returnedOrderDetails.text ="${returnedOrderDetails.text} Nie"
        }
        reportComplaint.setOnClickListener {
            if (reportComplaint.text == getString(R.string.button_report_complaint_close)){
                reportComplaintMain.visibility = View.VISIBLE
                reportComplaint.text = getString(R.string.button_report_complaint_open)

            }else if(reportComplaint.text == getString(R.string.button_report_complaint_open)){
                reportComplaintMain.visibility = View.GONE
                reportComplaint.text = getString(R.string.button_report_complaint_close)

            }else if (reportComplaint.text == getString(R.string.button_show_orders_complain)){
                scrollViewDetailsOrderComplain.visibility = View.VISIBLE
                lifecycleScope.launch {
                   val ordersComplainDetails = ordersComplaintService.getOrderComplainById(
                        requireContext(),
                        sharedViewModelOrders.order.value?.id!!,
                        sharedViewModelLogin.login.value.toString(),
                        sharedViewModelLogin.password.value.toString()
                    )

                    dateSendReason.text = ordersComplainDetails?.created
                    textViewReason.text = ordersComplainDetails?.reason
                    reportComplaint.text = getString(R.string.button_hide_orders_complain)

                }
            }else if (reportComplaint.text == getString(R.string.button_hide_orders_complain))
                scrollViewDetailsOrderComplain.visibility = View.GONE
            reportComplaint.text = getString(R.string.button_show_orders_complain)

        }

        reportOrder.setOnClickListener {
            lifecycleScope.launch {
                ordersComplaintService.updateOrAddQuantityByProductId(
                    requireContext(),
                    sharedViewModelOrders.order.value?.id!!,
                    reasonComplaint.text.toString(),
                    sharedViewModelLogin.login.value.toString(),
                    sharedViewModelLogin.password.value.toString()
                )
                reportComplaintMain.visibility = View.GONE
                reportComplaint.text = getString(R.string.button_show_orders_complain)

            }
        }


        orderProduct?.forEach { item ->
            val productId = item.productId
            val quantity = item.quantity
            lifecycleScope.launch {
                val picture = productService.getPictureById(requireContext(), productId)
                val product = productService.getProductById(requireContext(),productId)

                val productView =
                    layoutInflater.inflate(R.layout.fragment_order_details_product_list, ordersView, false)
                productView.id = R.id.productView
                val image = productView.findViewById<ImageView>(R.id.ImageShoppingCartDetails)
                val productName = productView.findViewById<TextView>(R.id.nameProductShoppingCartDeatils)
                val quantityDetails = productView.findViewById<TextView>(R.id.quantityDetails)
                image.setImageBitmap(productService.pictureB64ToImage(picture.first()))
                productName.text = product?.name
                quantityDetails.text = "${quantityDetails.text} ${quantity}"
                ordersView.addView(productView)
            }


        }







        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        backPressedHandler.unregister()
    }

}