package pl.shop.toyshop.ui.complaints

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.ComplaintService
import pl.shop.toyshop.ui.login_signup.LoginViewModel

class ComplaintsFragment : Fragment() {



    private val sharedComplaintsViewModel: ComplaintsViewModel by activityViewModels()
    private val sharedViewModelLogin: LoginViewModel by activityViewModels()
    private lateinit var progressBar: ProgressBar

    val complaintService = ComplaintService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_complaints, container, false)
        progressBar = view.findViewById(R.id.progressBarComplaints)

        lifecycleScope.launch {
            progressBar.visibility = View.VISIBLE

            val allComplaint = complaintService.getAllComplaint(
                requireContext(),
                sharedViewModelLogin.login.value.toString(),
                sharedViewModelLogin.password.value.toString()
            )
            val complaintList = view.findViewById<LinearLayout>(R.id.listComplaints)


            for (complaint in allComplaint) {


                val complaintView =
                    layoutInflater.inflate(R.layout.fragment_complaint_list, complaintList, false)
                complaintView.id = R.id.complaintView

                val complaintNumber = complaintView.findViewById<TextView>(R.id.complaintNumber)
                val complaintStatus = complaintView.findViewById<TextView>(R.id.complaintStatus)

                complaintNumber.text = "${complaintNumber.text}${complaint.id}"
                complaintStatus.text = "${complaintStatus.text}${complaint.status}"

                val newTextColor = ContextCompat.getColor(requireContext(), R.color.lavender)
                complaintStatus.setTextColor(newTextColor)

                complaintNumber.setOnClickListener {
                    sharedComplaintsViewModel.complaint.value = complaint
                    findNavController().navigate(R.id.action_nav_complaints_to_complaintsDetails)
                }


                complaintList.addView(complaintView)
            }
            progressBar.visibility = View.GONE

        }

        return view
    }



}