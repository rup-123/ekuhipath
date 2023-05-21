package com.example.e_kuhipath.fragments

import android.app.Dialog
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
import com.example.e_kuhipath.R
import com.example.e_kuhipath.activities.adapters.PaidCoursesAdapter
import com.example.e_kuhipath.models.PaidCourses
import com.example.e_kuhipath.services.RetroService
import com.example.e_kuhipath.services.ServiceBuilder
import com.example.e_kuhipath.utils.NetworkChangeReceiver
import kotlinx.android.synthetic.main.fragment_paid_courses.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception

class PaidCoursesFragment() : Fragment(R.layout.fragment_paid_courses), NetworkChangeReceiver.NetworkChangeListener {
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


    private fun setUpUI(code: PaidCourses?) {
        paidcourses_recycler_view.layoutManager = LinearLayoutManager(activity)
        val paidCoursesAdapter = PaidCoursesAdapter(code!!,requireContext())

        paidcourses_recycler_view.adapter = paidCoursesAdapter
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
            lifecycleScope.launch {

                try {
                    showProgressDialog()
                    Log.i("ss","paid frag---->")
                    val response = retroService1.getPaidCourses(final_token)

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
                            if (code.data.paidCourse.isEmpty()){
                                no_courses_ll.visibility = View.VISIBLE
                                paidcourses_recycler_view.visibility = View.GONE
                            }
                            else {
                                no_courses_ll.visibility = View.GONE
                                paidcourses_recycler_view.visibility = View.VISIBLE
                                setUpUI(code)
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
        } else {
            // Show a message to the user that there is no internet connectivity
            Toast.makeText(activity, "No internet connectivity!", Toast.LENGTH_SHORT).show()
        }
    }


}