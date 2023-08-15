package pl.shop.toyshop.ui.complaints

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pl.shop.toyshop.R
import pl.shop.toyshop.service.BackPressedHandler
import pl.shop.toyshop.service.ComplaintService
import pl.shop.toyshop.ui.login_signup.LoginViewModel

class ComplaintsDetails : Fragment() {

    private val sharedComplaintsViewModel: ComplaintsViewModel by activityViewModels()
    private val sharedViewModelLogin: LoginViewModel by activityViewModels()
    private lateinit var backPressedHandler: BackPressedHandler
    private lateinit var progressBar: ProgressBar

    val complaintService = ComplaintService()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_complaints_details, container, false)
        backPressedHandler = BackPressedHandler(this)
        backPressedHandler.register()
        val complaint = sharedComplaintsViewModel.complaint.value
        val username = sharedViewModelLogin.login.value.toString()
        val password = sharedViewModelLogin.password.value.toString()

        val numberComplaint = view.findViewById<TextView>(R.id.numberComplaintDetails)
        val statusComplaint = view.findViewById<TextView>(R.id.statusComplaintDetails)
        val reasonComplaint = view.findViewById<TextView>(R.id.reasonComplaintDetails)
        val createdComplaint = view.findViewById<TextView>(R.id.createdComplaintDetails)
        val updateComplaint = view.findViewById<TextView>(R.id.updatedComplaintDetails)
        val radioUpdate = view.findViewById<RadioGroup>(R.id.radioUpdateStatus)
        val updateButton = view.findViewById<Button>(R.id.updateComplaintButton)
        progressBar = view.findViewById(R.id.progressBarComplaintsDetails)


        numberComplaint.text = "${numberComplaint.text} ${complaint?.orderId}"
        statusComplaint.text = "${statusComplaint.text} ${complaint?.status}"
        reasonComplaint.text = "${reasonComplaint.text} ${complaint?.reason}"
        createdComplaint.text = "${createdComplaint.text} ${complaint?.created}"
        updateComplaint.text = "${updateComplaint.text} ${complaint?.updated}"

        updateButton.setOnClickListener {
            lifecycleScope.launch {
                progressBar.visibility = View.VISIBLE
                val checkedId = radioUpdate.checkedRadioButtonId
                when (checkedId) {
                    R.id.CREATED -> {
                        if (complaint != null) {
                                complaintService.updateComplaint(
                                    requireContext(),
                                    complaint.id,
                                    resources.getResourceEntryName(R.id.CREATED),
                                    username,
                                    password
                                )

                        }
                    }
                    R.id.SENT_TO_MANUFACTURER ->{
                        if (complaint != null) {
                            complaintService.updateComplaint(
                                requireContext(),
                                complaint.id,
                                resources.getResourceEntryName(R.id.SENT_TO_MANUFACTURER),
                                username.toString(),
                                password.toString()
                            )

                        }
                    }
                    R.id.REJECTED_BY_SELLER ->{
                        if (complaint != null) {
                            complaintService.updateComplaint(
                                requireContext(),
                                complaint.id,
                                resources.getResourceEntryName(R.id.REJECTED_BY_SELLER),
                                username.toString(),
                                password.toString()
                            )

                        }
                    }
                    R.id.REJECTED_BY_MANUFACTURER->{
                        if (complaint != null) {
                            complaintService.updateComplaint(
                                requireContext(),
                                complaint.id,
                                resources.getResourceEntryName(R.id.REJECTED_BY_MANUFACTURER),
                                username.toString(),
                                password.toString()
                            )

                        }
                    }

                    R.id.WAITING_FOR_DELIVERY->{
                        if (complaint != null) {
                            complaintService.updateComplaint(
                                requireContext(),
                                complaint.id,
                                resources.getResourceEntryName(R.id.WAITING_FOR_DELIVERY),
                                username.toString(),
                                password.toString()
                            )

                        }
                    }
                    R.id.COMPLETED->{
                        if (complaint != null) {
                            complaintService.updateComplaint(
                                requireContext(),
                                complaint.id,
                                resources.getResourceEntryName(R.id.COMPLETED),
                                username.toString(),
                                password.toString()
                            )

                        }
                    }

                }
            }
            progressBar.visibility = View.GONE

        }



        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        backPressedHandler.unregister()
    }

}