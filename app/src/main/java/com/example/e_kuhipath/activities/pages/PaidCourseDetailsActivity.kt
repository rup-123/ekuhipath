package com.example.e_kuhipath.activities.pages

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
import com.example.e_kuhipath.R
import com.example.e_kuhipath.activities.adapters.PaidCoursesAdapter
import com.example.e_kuhipath.activities.adapters.ParentPaidCourseAdapter
import com.example.e_kuhipath.activities.authentication.LoginActivity
import com.example.e_kuhipath.databinding.ActivityPaidCoursedetailsBinding
import com.example.e_kuhipath.fragments.VideoDialogFragment
import com.example.e_kuhipath.models.PaidCourseDetails
import com.example.e_kuhipath.models.PaidCourses
import com.example.e_kuhipath.models.UnpaidCourseReturn
import com.example.e_kuhipath.services.RetroService
import com.example.e_kuhipath.services.ServiceBuilder
import com.example.e_kuhipath.utils.IsOnline
import com.example.e_kuhipath.utils.NetworkChangeReceiver
import kotlinx.android.synthetic.main.activity_paid_coursedetails.*
import kotlinx.android.synthetic.main.fragment_paid_courses.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception


object GlobalPaidCourseDetails {
    var paidCourseDetails: PaidCourseDetails? = null
}
class PaidCourseDetailsActivity: AppCompatActivity(), NetworkChangeReceiver.NetworkChangeListener {
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    lateinit var context: Context
    private lateinit var dialog: Dialog
    private lateinit var navController: NavController
    private lateinit var binding: ActivityPaidCoursedetailsBinding
    private lateinit var receiver: NetworkChangeReceiver
    var subcourseid:String? = null
    var videos:String? = null


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaidCoursedetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subcourseid = intent.getStringExtra("subcourseid")
        videos = intent.getStringExtra("totalvideos")
        binding.textView7.text = "Total "+videos + " lectures"
        Log.i("zz","subcourseid--->"+subcourseid)
        receiver = NetworkChangeReceiver()
        receiver.listener = this

        paid_backbtn.setOnClickListener{
            GlobalPaidCourseDetails.paidCourseDetails = null

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
        GlobalPaidCourseDetails.paidCourseDetails = null
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
            sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)


            val accesstoken = sharedPref.getString("accesstoken", "")
            val final_token = "Bearer " + accesstoken

            val retroService1: RetroService = ServiceBuilder.buildService(
                RetroService::class.java
            )
            if (GlobalPaidCourseDetails.paidCourseDetails!=null){
                setUpUI(GlobalPaidCourseDetails.paidCourseDetails!!)
            }
            else{
                lifecycleScope.launch {

                    try {
                        showProgressDialog()
                        Log.i("ss","paid frag---->")
                        val response = retroService1.getPaidCourseDetails(subcourseid!!,final_token)

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
                                GlobalPaidCourseDetails.paidCourseDetails = code
                                if (code.data.chapters.isEmpty()){
                                    binding.noPaidcoursesLl.visibility = View.VISIBLE
                                    binding.paidcoursedetailsParentRecyclerView.visibility = View.GONE
                                }
                                else {
                                    binding.noPaidcoursesLl.visibility = View.GONE
                                    binding.paidcoursedetailsParentRecyclerView.visibility = View.VISIBLE
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
                                        this@PaidCourseDetailsActivity,
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

            }
        } else {
            // Show a message to the user that there is no internet connectivity
            Toast.makeText(this, "No internet connectivity!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpUI(code: PaidCourseDetails) {
        binding.paidcoursedetailsParentRecyclerView.layoutManager = LinearLayoutManager(this)
        val paidCoursesAdapter = ParentPaidCourseAdapter(code,this){ videoid,videoname -> playVideo(videoid,videoname) }

        binding.paidcoursedetailsParentRecyclerView.adapter = paidCoursesAdapter
    }

    private fun playVideo(videoid: String,videoname:String) {
        // implement video playback logic

        val video_thumbnail = "https://www.ekuhipath.com/api/ekuhipath-v1/video-course/get-video-thumbnail/" +  videoid

        Log.i("zz","playvideo---->"+videoid)
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)


            val accesstoken = sharedPref.getString("accesstoken", "")
            val final_token = "Bearer " + accesstoken

            val retroService1: RetroService = ServiceBuilder.buildService(
                RetroService::class.java
            )

            lifecycleScope.launch {

                try {
                    showProgressDialog()
                    Log.i("ss","paid frag---->")
                    val response = retroService1.getVideoDetails(videoid,final_token)

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
                            if (code.data.video_details.main_video_name.equals("")){
                                Toast.makeText(this@PaidCourseDetailsActivity,"Video Unavailable",Toast.LENGTH_LONG).show()
                            }
                            else {
                                val videoUrl = "https://www.ekuhipath.com/api/ekuhipath-v1/video-course/watch/$subcourseid/$videoid/${code.data.video_details.main_video_name}"
                                /*val dialogFragment = VideoDialogFragment()
                                val args = Bundle()
                                args.putString("video_url", videoUrl)
                                args.putString("video_thumbnail", video_thumbnail)
                                args.putString("videoname",videoname)
                                dialogFragment.arguments = args
                                dialogFragment.show(supportFragmentManager, "VideoDialogFragment")*/
                                val intent = Intent(this@PaidCourseDetailsActivity,VideoPlayerActivity::class.java)
                                intent.putExtra("video_url",videoUrl)
                                intent.putExtra("video_thumbnail",video_thumbnail)
                                intent.putExtra("videoname",videoname)
                                intent.putExtra("videoid",videoid)

                                intent.putExtra("subcourseid",subcourseid)
                                intent.putExtra("totalvideos",videos)
                                startActivity(intent)
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
                                    this@PaidCourseDetailsActivity,
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val editor = sharedPref.edit()
                                editor.putString("accesstoken", null)
                                editor.apply()
                                val intent = Intent(
                                    this@PaidCourseDetailsActivity,
                                    LoginActivity::class.java
                                )
                                startActivity(intent)
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
        else {
            // Network is not available, show an error message
            Toast.makeText(this, "Network is not available", Toast.LENGTH_SHORT).show()
        }

    }
}
