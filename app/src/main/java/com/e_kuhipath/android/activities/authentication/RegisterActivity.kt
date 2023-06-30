package com.e_kuhipath.android.activities.authentication

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.e_kuhipath.android.R
import com.e_kuhipath.android.models.StudentRegister
import com.e_kuhipath.android.models.StudentRegisterTokens
import com.e_kuhipath.android.services.RetroService
import com.e_kuhipath.android.services.ServiceBuilder
import com.e_kuhipath.android.utils.IsOnline
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity: AppCompatActivity() {

    private lateinit var dialog: Dialog
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var backbtn = findViewById<ImageView>(R.id.login_back)

        backbtn.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        val registerbtn = findViewById<Button>(R.id.register_btn)

        val fullname_et = findViewById<TextInputEditText>(R.id.fullname)
        val email_et = findViewById<TextInputEditText>(R.id.email)
        val mobileno_et = findViewById<TextInputEditText>(R.id.mobileno)
        val password_et = findViewById<TextInputEditText>(R.id.password)
        val password_confirm_et = findViewById<TextInputEditText>(R.id.password_confirm)


        val a = IsOnline()
        val isOnline = a.isOnline(this)
        if (isOnline == false) {
            Toast.makeText(this,"No Internet",Toast.LENGTH_LONG).show()
        }


        registerbtn.setOnClickListener {
            /* val editor = sharedPref.edit()
             editor.putInt("value", 0)
             editor.apply()*/
            var i = 0


            val fullname = fullname_et.text.toString()
            val email = email_et.text.toString()
            val mobileno = mobileno_et.text.toString()
            val password = password_et.text.toString()
            val confirmpassword = password_confirm_et.text.toString()

            // val no = mobilenotv.toInt()
            val studentRegister = StudentRegister(fullname, email,mobileno,password,confirmpassword)

            if (fullname.isEmpty()) {
                i = 1
                fullname_et.setError("Please enter full name")
            }
            if (email.isEmpty()) {
                i = 1
                email_et.setError("Please enter email")
            }
            if (mobileno.isEmpty()) {
                i = 1
                mobileno_et.setError("Please enter mobileno")
            }
            if (password.isEmpty()) {
                i = 1
                password_et.setError("Please enter password")
            }
            if (confirmpassword.isEmpty()) {
                i = 1
                password_confirm_et.setError("Please enter password")
            }

            fullname_et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    fullname_et.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            email_et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    email_et.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            mobileno_et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    mobileno_et.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            password_et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    password_et.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            password_confirm_et.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    password_confirm_et.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            if (i == 0) {
                showProgressDialog()
                val retroService: RetroService = ServiceBuilder.buildService(
                    RetroService::class.java
                )
                val requestcall: Call<StudentRegisterTokens> =
                    retroService.getRegisterTokens(studentRegister)

                if (isOnline == true) {
                    requestcall.enqueue(object : Callback<StudentRegisterTokens> {
                        override fun onResponse(
                            call: Call<StudentRegisterTokens>,
                            response: Response<StudentRegisterTokens>
                        ) {
                            if (response.isSuccessful) {
                                val code = response.body()
                                if (code == null) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Wrong Code!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    /*savetokens(code)
                                    senddevicetoken()
                                    anotherActivity(submitbtn, code)*/
                                    dialog.dismiss()
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Registered Successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                                Log.i("qqq", "response->" + response)
                                Log.i("qqq", "code->" + response.body()!!)
                            } else {

                                dialog.dismiss()
                                if (response.code() == 422) {

                                    val b = JSONObject(response.errorBody()!!.string())

                                    if (b.has("errors")) {
                                        val v = b.get("errors").toString()
                                        val q = JSONObject(v)
                                        if(q.has("email")){
                                            val y = q.get("email").toString()
                                            val z = JSONArray(y)
                                            val message = z.get(0).toString()
                                            Handler(Looper.getMainLooper()).post {
                                                email_et.setError(message)
                                            }
                                            Log.i("qqq","y--->"+y)
                                            Log.i("qqq","z--->"+z)
                                        }

                                        if(q.has("mobile_no")){
                                            val y = q.get("mobile_no").toString()
                                            val z = JSONArray(y)
                                            val message = z.get(0).toString()
                                            Handler(Looper.getMainLooper()).post {
                                                mobileno_et.setError(message)
                                            }
                                            Log.i("qqq","y--->"+y)
                                            Log.i("qqq","z--->"+z)
                                        }
                                        if(q.has("full_name")){
                                            val y = q.get("full_name").toString()
                                            val z = JSONArray(y)
                                            val message = z.get(0).toString()
                                            Handler(Looper.getMainLooper()).post {
                                                fullname_et.setError(message)
                                            }
                                            Log.i("qqq","y--->"+y)
                                            Log.i("qqq","z--->"+z)
                                        }

                                        /*Toast.makeText(
                                            this@RegisterActivity,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()*/
                                        //  edittext.requestFocus()
                                        //  awesomeValidation.addValidation(dialog,R.id.complainuniqueid, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)
                                        //   awesomeValidation.addValidation(uniqueid,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)

                                    }
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "error code: " + response.code(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }

                        override fun onFailure(call: Call<StudentRegisterTokens>, t: Throwable) {

                            /*Log.i("www", "failure----->")
                            val intent = Intent(this@LoginActivity, NoInternetActivity::class.java)
                            startActivity(intent)*/
                        }

                    })
                } else if (isOnline == false) {
                    /* val intent = Intent(this, NoInternetActivity::class.java)
                     startActivity(intent)*/
                }
            }
        }
    }

    private fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }

    override fun onBackPressed() {

        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

}