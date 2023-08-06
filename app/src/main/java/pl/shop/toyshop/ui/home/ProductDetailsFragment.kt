package pl.shop.toyshop.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.shop.toyshop.R
import pl.shop.toyshop.model.Picture
import pl.shop.toyshop.service.BackPressedHandler
import pl.shop.toyshop.service.OrderService
import pl.shop.toyshop.ui.login_signup.LoginViewModel
import java.lang.Math.abs

class ProductDetailsFragment : Fragment() {
    private val sharedViewModel: HomeViewModel by activityViewModels()
    private val sharedViewModelLogin: LoginViewModel by activityViewModels()
    private lateinit var backPressedHandler: BackPressedHandler



    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var adapter: ImageAdapter
    private var orderService = OrderService()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_product_details, container, false)

        backPressedHandler = BackPressedHandler(this)
        backPressedHandler.register()


        init(root)
        setUpTransformer()

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)

                // handler.postDelayed(runnable, 2000)
            }
        })

        val productNameTextView = root.findViewById<TextView>(R.id.name)
        val productPriceTextView = root.findViewById<TextView>(R.id.price)
        val productStockTextView = root.findViewById<TextView>(R.id.stock)
        val productDescriptionTextView = root.findViewById<TextView>(R.id.description)

        if (((sharedViewModelLogin.login.value) != null) && ((sharedViewModelLogin.password.value) != null)) {
            var countResult: Int = 0
            val ShoppingCartMain = root.findViewById<LinearLayout>(R.id.ShoppingCartMain)
            val addShoppingCartButton =
                root.findViewById<ImageButton>(R.id.addShoppingCartHomeDetails)
            val subtractShoppingCart = root.findViewById<ImageButton>(R.id.subtractShoppingCartHome)
            val addToShoppingCart = root.findViewById<ImageButton>(R.id.addToShoppingCart)
            val count = root.findViewById<TextView>(R.id.count)
            ShoppingCartMain.visibility = View.VISIBLE
            count.text = countResult.toString()

            addShoppingCartButton.setOnClickListener {
                countResult += 1
                count.text = countResult.toString()
            }

            subtractShoppingCart.setOnClickListener {
                countResult += -1
                count.text = countResult.toString()
            }

            addToShoppingCart.setOnClickListener {
                if (countResult > 0) {



                    lifecycleScope.launch {
                        orderService.updateOrAddQuantityByProductId(
                            requireContext(),
                            sharedViewModel.product.value!!.id,
                            countResult,
                            sharedViewModelLogin.login.value.toString(),
                            sharedViewModelLogin.password.value.toString()
                        )

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Produkt został dodany do koszyka", Toast.LENGTH_LONG).show()
                        }
                        countResult = 0
                        count.text = countResult.toString()

                    }


                }
            }
        }


        productNameTextView.text = sharedViewModel.product.value?.name
        productPriceTextView.text = "${sharedViewModel.product.value?.price} zł"
        productStockTextView.text = "Pozostało ${sharedViewModel.product.value?.stock} sztuk"
        productDescriptionTextView.text = sharedViewModel.product.value?.description

        return root
    }


    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable, 2000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1

    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.50f + r * 0.50f
        }

        viewPager2.setPageTransformer(transformer)


    }

    private fun init(view: View) {
        viewPager2 = view.findViewById(R.id.ViewPager2)
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        // Pobierz listę obiektów Picture i przekształć na obiekty PictureAdapter
        val pictureList: ArrayList<Picture> = getPictureList()
        adapter = ImageAdapter(pictureList, viewPager2)

        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

    }

    private fun getPictureList(): ArrayList<Picture> {
        val pictureList: ArrayList<Picture> = ArrayList()

        // Pobieranie listy obiektów Picture z ViewModelu HomeViewModel
        val picturesLiveData: LiveData<ArrayList<Picture>> = sharedViewModel.pictures
        val picturesData: ArrayList<Picture>? = picturesLiveData.value

        if (picturesData != null) {
            pictureList.addAll(picturesData)
        }

        return pictureList
    }

    override fun onDestroyView() {
        super.onDestroyView()

        backPressedHandler.unregister()
    }

}