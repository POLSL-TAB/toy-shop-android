package pl.shop.toyshop.service

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class BackPressedHandler(private val fragment: Fragment) {

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            fragment.findNavController().navigateUp()
        }
    }

    fun register() {
        fragment.requireActivity().onBackPressedDispatcher.addCallback(
            fragment.viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    fun unregister() {
        onBackPressedCallback.remove()
    }



}