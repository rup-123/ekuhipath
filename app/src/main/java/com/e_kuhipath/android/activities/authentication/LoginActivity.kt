package com.e_kuhipath.android.activities.authentication

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.e_kuhipath.android.R
import com.e_kuhipath.android.activities.landingpage.WelcomeActivity
import com.e_kuhipath.android.models.StudentLogin
import com.e_kuhipath.android.models.StudentLoginTokens
import com.e_kuhipath.android.services.RetroService
import com.e_kuhipath.android.services.ServiceBuilder
import com.e_kuhipath.android.utils.IsOnline
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity: AppCompatActivity() {

    private var pressedTime: Long = 0
    private var PRIVATE_MODE = 0
    lateinit var sharedPref: SharedPreferences
    private lateinit var dialog: Dialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val register = findViewById<TextView>(R.id.register_tv)
        val loginbtn = findViewById<Button>(R.id.login_btn)

        val mobileno = findViewById<TextInputEditText>(R.id.mobileno_et)
        val password = findViewById<TextInputEditText>(R.id.password_et)

        val a = IsOnline()
        val isOnline = a.isOnline(this)
        if (isOnline == false) {
           Toast.makeText(this,"No Internet",Toast.LENGTH_LONG).show()
        }
        register.setOnClickListener{
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
        loginbtn.setOnClickListener{
            val intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }

        loginbtn.setOnClickListener {
            /* val editor = sharedPref.edit()
             editor.putInt("value", 0)
             editor.apply()*/
            var i = 0


            val mobilenotv = mobileno.text.toString()
            val pass = password.text.toString()

           // val no = mobilenotv.toInt()
            val studentLogIn = StudentLogin(mobilenotv, pass)

            if (mobilenotv.isEmpty()) {
                i = 1
                mobileno.setError("Please enter user name")
            }
            if (pass.isEmpty()) {
                i = 1
                password.setError("Please enter password")
            }
            mobileno.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    mobileno.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            password.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    password.setError(null)
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
            if (i == 0) {
                showProgressDialog()
                val retroService: RetroService = ServiceBuilder.buildService(
                    RetroService::class.java
                )
                val requestcall: Call<StudentLoginTokens> =
                    retroService.getLoginTokens(studentLogIn)

                if (isOnline == true) {
                    requestcall.enqueue(object : Callback<StudentLoginTokens> {
                        override fun onResponse(
                            call: Call<StudentLoginTokens>,
                            response: Response<StudentLoginTokens>
                        ) {
                            if (response.isSuccessful) {
                                val code = response.body()
                                if (code == null) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Wrong Code!!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    /*savetokens(code)
                                    senddevicetoken()
                                    anotherActivity(submitbtn, code)*/
                                    dialog.dismiss()
                                    savetokens(code)
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Logged In Successfully",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val intent = Intent(this@LoginActivity,WelcomeActivity::class.java)
                                    startActivity(intent)
                                }
                                Log.i("qqq", "response->" + response)
                                Log.i("qqq", "code->" + response.body()!!)
                            } else {

                                dialog.dismiss()
                                if (response.code() == 401) {
                                    val b = JSONObject(response.errorBody()!!.string())

                                    if (b.has("message")) {
                                        val message = b.get("message").toString()
                                        Log.i("zzg", "message---->" + message)

                                        Toast.makeText(
                                            this@LoginActivity,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        //  edittext.requestFocus()
                                        //  awesomeValidation.addValidation(dialog,R.id.complainuniqueid, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)
                                        //   awesomeValidation.addValidation(uniqueid,"^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", message)

                                    }
                                }
                                else {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "error code: " + response.code(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }

                        override fun onFailure(call: Call<StudentLoginTokens>, t: Throwable) {

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

    private fun savetokens(code: StudentLoginTokens) {

        sharedPref = this.getSharedPreferences("sharedpref", PRIVATE_MODE)
        val editor = sharedPref.edit()
        editor.putString("accesstoken", code.data.token)
       // editor.putString("id", code.data.user.id.toString())
        editor.putString("mobileno", code.data.user.mobile_no)
        editor.putString("name", code.data.user.name)
        editor.putString("email", code.data.user.email)

        editor.apply()
    }
    private fun showProgressDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.show()
    }

    override fun onBackPressed() {


        if (pressedTime + 2000 > System.currentTimeMillis()) {
            finishAffinity()
            finish()
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT)
                .show()
        }
        pressedTime = System.currentTimeMillis()

    }

}