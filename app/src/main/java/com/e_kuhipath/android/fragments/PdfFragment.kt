

package com.e_kuhipath.android.fragments

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.adapters.PaidCoursesAdapter
import com.e_kuhipath.android.activities.adapters.PaidPdfCourseAdapter
import com.e_kuhipath.android.activities.authentication.LoginActivity
import com.e_kuhipath.android.models.PaidCourses
import com.e_kuhipath.android.models.PaidPdfCourse
import com.e_kuhipath.android.services.RetroService
import com.e_kuhipath.android.services.ServiceBuilder
import com.e_kuhipath.android.utils.NetworkChangeReceiver
import kotlinx.android.synthetic.main.fragment_paid_courses.*
import kotlinx.android.synthetic.main.fragment_pdf.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception


object GlobalPaidPdfCourse {
    var paidCourses: List<PaidPdfCourse>? = null
}
class PdfFragment() : Fragment(R.layout.fragment_pdf), NetworkChangeReceiver.NetworkChangeListener {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    var uniqueid = ""
    private lateinit var dialog: Dialog
    private lateinit var receiver: NetworkChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receiver = NetworkChangeReceiver()
        receiver.listener = this
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Register the receiver
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        activity?.registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to avoid memory leaks
        activity?.unregisterReceiver(receiver)
    }


    private fun setUpUI(code: List<PaidPdfCourse>?) {
        paidpdfcourses_recycler_view.layoutManager = LinearLayoutManager(activity)
        val paidCoursesAdapter = PaidPdfCourseAdapter(code!!)

        paidpdfcourses_recycler_view.adapter = paidCoursesAdapter
        Log.i("www","end of alerts fragment")
    }

    private fun showProgressDialog() {
        dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }


    override fun onNetworkChanged(isConnected: Boolean, shouldCallApi: Boolean) {
        if (isConnected) {
            Log.i("zz","connected--->")
            sharedPref = requireActivity().getSharedPreferences("sharedpref", PRIVATE_MODE)


            val accesstoken = sharedPref.getString("accesstoken", "")
            val final_token = "Bearer " + accesstoken


            val retroService1: RetroService = ServiceBuilder.buildService(
                RetroService::class.java
            )
            if (GlobalPaidPdfCourse.paidCourses!=null){
                setUpUI(GlobalPaidPdfCourse.paidCourses!!)
            }
            else{
                lifecycleScope.launch {

                    try {
                        showProgressDialog()
                        Log.i("ss","paid frag---->")
                        val response = retroService1.getPaidPdfCourses(final_token)

                        if (response.isSuccessful) {
                            val code = response.body()
                            if (code == null) {
                                Toast.makeText(
                                    context,
                                    "Response Body is empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                dialog.hide()
                                GlobalPaidPdfCourse.paidCourses = code.data.paid_pdf_courses
                                if (code.data.paid_pdf_courses.isEmpty()){
                                    no_pdfcourses_ll.visibility = View.VISIBLE
                                    paidpdfcourses_recycler_view.visibility = View.GONE
                                }
                                else {
                                    no_pdfcourses_ll.visibility = View.GONE
                                    paidpdfcourses_recycler_view.visibility = View.VISIBLE
                                    setUpUI(code.data.paid_pdf_courses)
                                }
                            }
                        }
                        else {
                            dialog.hide()
                            if (response.code() == 401) {
                                val b = JSONObject(response.errorBody()!!.string())

                                if (b.has("message")) {
                                    val message = b.get("message").toString()
                                    Log.i("zzg", "message---->" + message)

                                    Toast.makeText(
                                        requireActivity(),
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val editor = sharedPref.edit()
                                    editor.putString("accesstoken", null)
                                    editor.apply()
                                    val intent = Intent(requireContext(), LoginActivity::class.java)
                                    requireActivity().startActivity(intent)
                                    //  edittext.requestFocus()
                                    //  awesomeValidation.addValidation(dialog,R.id.complainuniqueid, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)
                                    //   awesomeValidation.addValidation(uniqueid,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)

                                }
                            }
                            else if (response.code() == 400){
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                if (jObjError.has("message")){
                                    val message = jObjError.get("message").toString()
                                    Toast.makeText(context,message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                    } catch (e: HttpException) {
                        Log.i("xxx", "httpexception--->" + e)
                    } catch (e: Exception) {
                        Log.i("xxx", "other exception--->" + e)
                    }
                    Log.i("zz", "outside on response teacher profile online true---->")
                }

            }
        } else {
            // Show a message to the user that there is no internet connectivity
            Toast.makeText(activity, "No internet connectivity!", Toast.LENGTH_SHORT).show()
        }
    }


}