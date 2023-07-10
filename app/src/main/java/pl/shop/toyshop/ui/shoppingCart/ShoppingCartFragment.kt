package pl.shop.toyshop.ui.shoppingCart

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.model.ShoppingCart
import pl.shop.toyshop.service.ProductService
import pl.shop.toyshop.service.OrderService
import pl.shop.toyshop.ui.login_signup.LoginViewModel

class ShoppingCartFragment : Fragment() {
    private val sharedViewModel: LoginViewModel by activityViewModels()


    private lateinit var viewModel: ShoppingCartViewModel
    private var orderService = OrderService()
    private var productService = ProductService()
    private  var summary: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shopping_cart, container, false)
        val mainCartList = view.findViewById<LinearLayout>(R.id.mainCartList)
        val summaryTextView = view.findViewById<TextView>(R.id.summary)

        lifecycleScope.launch {

            val ordersAll = orderService.getItemShoppingAll(
                requireContext(),
                sharedViewModel.login.value.toString(),
                sharedViewModel.password.value.toString()
            )


            for (shoppingCart in ordersAll) {
                val productView =
                    layoutInflater.inflate(
                        R.layout.fragment_shopping_cart_list,
                        mainCartList,
                        false
                    )
                productView.id = R.id.ordersView
                val nameProduct = productView.findViewById<TextView>(R.id.nameProductShoppingCartDeatils)
                val quantityShoppingCart =
                    productView.findViewById<TextView>(R.id.quantityShoppingCart)
                val ImageSchoppingCart = productView.findViewById<ImageView>(R.id.ImageShoppingCartDetails)
                val priceShoppingCart = productView.findViewById<TextView>(R.id.priceShoppingCart)
                val addQuantity = productView.findViewById<ImageButton>(R.id.addQuantity)
                val subtractQuantity = productView.findViewById<ImageButton>(R.id.subtractQuantity)
                val removeShoppingCart =
                    productView.findViewById<ImageButton>(R.id.removeShoppingCart)
                val submitOrder =  view.findViewById<Button>(R.id.submmitOrder)


                val product =
                    productService.getProductById(requireContext(), shoppingCart.productId.toInt())
                val picture =
                    productService.getPictureById(requireContext(), shoppingCart.productId.toInt())

                if (picture.isNotEmpty()) {
                    val pictureImageDecode = productService.pictureB64ToImage(picture.first())
                    ImageSchoppingCart.setImageBitmap(pictureImageDecode)
                }

                nameProduct.text = product?.name
                quantityShoppingCart.text = shoppingCart.quantity
                var summaryProduct = product?.price?.toInt()?.times(shoppingCart.quantity.toInt())
                priceShoppingCart.text = summaryProduct.toString() + "zł"
                summary = summary.plus(summaryProduct!!)
                summaryTextView.text = "$summary zł"


                submitOrder.setOnClickListener {
                    lifecycleScope.launch {
                        orderService.submitOrder(
                            requireContext(), sharedViewModel.login.value.toString(),
                            sharedViewModel.password.value.toString()
                        )
                        mainCartList.removeAllViews()
                    }
                }


                addQuantity.setOnClickListener {
                    updateQuantityButtonListener(shoppingCart, quantityShoppingCart, 1)
                    shoppingCart.quantity = (shoppingCart.quantity.toInt()+1).toString()
                    priceShoppingCart.text = "${(product?.price?.toInt()?.times(shoppingCart.quantity.toInt())).toString()} zł"
                    summary = summary.plus(product!!.price.toInt())
                    summaryTextView.text = "$summary zł"


                }
                subtractQuantity.setOnClickListener {
                    updateQuantityButtonListener(shoppingCart, quantityShoppingCart, -1)
                    shoppingCart.quantity = (shoppingCart.quantity.toInt()-1).toString()
                    priceShoppingCart.text = "${(product?.price?.toInt()?.times(shoppingCart.quantity.toInt())).toString()} zł"
                    summary = summary.minus(product!!.price.toInt())
                    summaryTextView.text = "$summary zł"

                }



                removeShoppingCart.setOnClickListener {
                    lifecycleScope.launch {
                        orderService.deleteProductShoppingCart(
                            requireContext(),
                            sharedViewModel.login.value.toString(),
                            sharedViewModel.password.value.toString(),
                            shoppingCart.productId.toInt()
                        )
                    }
                    mainCartList.removeView(productView)
                  val cout = shoppingCart.quantity.toInt()
                  val count1 = product?.price?.toInt()
                    val result = count1?.let { it1 -> cout.times(it1) }
                summary = result?.let { it1 -> summary.minus(it1) }!!
                    summaryTextView.text = "$summary zł"

                }



                mainCartList.addView(productView)

            }


        }


        return view
    }

    private fun updateQuantityButtonListener(
        shoppingCart: ShoppingCart,
        quantityTextView: TextView,
        value: Int
    ) {
        lifecycleScope.launch {
            orderService.updateOrAddQuantityByProductId(
                requireContext(), shoppingCart.productId.toInt(), value,
                sharedViewModel.login.value.toString(),
                sharedViewModel.password.value.toString()
            )
            val orders = orderService.getItemShoppingAll(
                requireContext(),
                sharedViewModel.login.value.toString(),
                sharedViewModel.password.value.toString()
            )

            for (order in orders) {
                if (order.productId == shoppingCart.productId)
                    quantityTextView.text = order.quantity
            }
        }
    }
}