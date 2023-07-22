package com.e_kuhipath.android.activities.pages

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.adapters.PaidPdfCourseFileAdapter
import com.e_kuhipath.android.activities.adapters.ParentPaidCourseAdapter
import com.e_kuhipath.android.activities.authentication.LoginActivity
import com.e_kuhipath.android.databinding.ActivityPaidCoursedetailsBinding
import com.e_kuhipath.android.databinding.ActivityPaidPdfcoursedetailsBinding
import com.e_kuhipath.android.models.PaidCourseDetails
import com.e_kuhipath.android.models.PaidPdfCourse
import com.e_kuhipath.android.models.PaidPdfCourseFile
import com.e_kuhipath.android.services.RetroService
import com.e_kuhipath.android.services.ServiceBuilder
import com.e_kuhipath.android.utils.NetworkChangeReceiver
import kotlinx.android.synthetic.main.activity_paid_coursedetails.*
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception

/*object GlobalPaidPdfCourseDetails {
    var paidPdfCourseDetails: List<PaidPdfCourseFile>? = null
}*/
class PaidPdfCourseDetailsActivity: AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var navController: NavController
    private lateinit var binding: ActivityPaidPdfcoursedetailsBinding
    private lateinit var receiver: NetworkChangeReceiver
    var courseid:String? = null
    var pdfname:String? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaidPdfcoursedetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        courseid = intent.getStringExtra("courseid")
        pdfname = intent.getStringExtra("pdfname")
        binding.pdfname.text = pdfname
        Log.i("zz","pdfname--->"+pdfname)
        Log.i("zz","courseid--->"+courseid)
        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)

        receiver = NetworkChangeReceiver()
        receiver.listener = this

        binding.paidpdfcourseBackbtn.setOnClickListener{
         //   GlobalPaidPdfCourseDetails.paidPdfCourseDetails  = null
            /*val editor = sharedPref.edit()
            editor.putString("segment","pdf")
            editor.apply()*/
            val intent = Intent(this,PaidCoursesActivity::class.java)
            startActivity(intent)
        }
        Log.i("zz","oncreate---->")
    }

    override fun onResume() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        this.registerReceiver(receiver, filter)
        super.onResume()

        Log.i("zz","onResume---->")

    }

    override fun onBackPressed() {
        /*val editor = sharedPref.edit()
        editor.putString("segment","pdf")
        editor.apply()*/
        val intent = Intent(this,PaidCoursesActivity::class.java)

        startActivity(intent)
    }
    override fun onPause() {
        super.onPause()
        Log.i("zz","onPause---->")
    }
    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to avoid memory leaks
        this.unregisterReceiver(receiver)

        Log.i("zz","ondestroy---->")

    }
    private fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }
    override fun onNetworkChanged(isConnected: Boolean, shouldCallApi: Boolean) {
        if (isConnected) {
            Log.i("zz","connected--->")


            val accesstoken = sharedPref.getString("accesstoken", "")
            val final_token = "Bearer " + accesstoken

            val retroService1: RetroService = ServiceBuilder.buildService(
                RetroService::class.java
            )
            /*if (GlobalPaidPdfCourseDetails.paidPdfCourseDetails!=null){
                setUpUI(GlobalPaidPdfCourseDetails.paidPdfCourseDetails!!)
            }*/

                lifecycleScope.launch {

                    try {
                        showProgressDialog()
                        Log.i("ss","paid frag---->")
                        val response = retroService1.getPaidPdfCourseFiles(courseid!!,final_token)

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
                                //GlobalPaidPdfCourseDetails.paidPdfCourseDetails = code.data.paid_pdf_course_files
                                if (code.data.paid_pdf_course_files.isEmpty()){
                                    binding.noPaidpdfcoursedetailsLl.visibility = View.VISIBLE
                                    binding.paidpdfcoursedetailsRecyclerView.visibility = View.GONE
                                }
                                else {
                                    binding.noPaidpdfcoursedetailsLl.visibility = View.GONE
                                    binding.paidpdfcoursedetailsRecyclerView.visibility = View.VISIBLE
                                    setUpUI(code.data.paid_pdf_course_files)
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
                                        this@PaidPdfCourseDetailsActivity,
                                        message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val editor = sharedPref.edit()
                                    editor.putString("accesstoken", null)
                                    editor.apply()

                                    val intent = Intent(
                                        context,
                                        LoginActivity::class.java
                                    )
                                    context.startActivity(intent)
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
            Toast.makeText(this, "No internet connectivity!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpUI(code: List<PaidPdfCourseFile>) {
        binding.paidpdfcoursedetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        val paidCoursesAdapter = PaidPdfCourseFileAdapter(code)

        binding.paidpdfcoursedetailsRecyclerView.adapter = paidCoursesAdapter
    }

}