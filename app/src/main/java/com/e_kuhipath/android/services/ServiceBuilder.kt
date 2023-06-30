package com.e_kuhipath.android.services

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.*
import okhttp3.internal.http2.ConnectionShutdownException
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit

object ServiceBuilder{
    // Before release, change this URL to your live server URL such as "https://smartherd.com/"
    var URL = ""
    lateinit var response: Response
    lateinit var retrofit: Retrofit
    lateinit var builder: Retrofit.Builder
    lateinit var okHttp: OkHttpClient.Builder
    // Create a logger
    private val logger: HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(
        HttpLoggingInterceptor.Level.BODY
    )

    private val headerInterceptor: Interceptor = object : Interceptor {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun intercept(chain: Interceptor.Chain): Response {

            var request: Request = chain.request()
            Log.i("zzz", "request--->" + request)

            request = request.newBuilder()
                .header("Accept","application/json")
                .addHeader("x-device-type", Build.DEVICE)
                .addHeader("Accept-Language", Locale.getDefault().language)
                .build()

            try {

                response= chain.proceed(request)
                val bodyString = response.body!!.string()

                return response.newBuilder()
                    .body(ResponseBody.create(response.body?.contentType(), bodyString))
                    .build()
            }
            catch (e: Exception){
                e.printStackTrace()
                var msg = ""
                when (e) {
                    is SocketTimeoutException -> {
                        msg = "Timeout - Please check your internet connection"
                        Log.i("zz","msg1--->"+msg)
                    }
                    is UnknownHostException -> {
                        msg = "Unable to make a connection. Please check your internet"
                        Log.i("zz","msg2--->"+msg)

                    }
                    is ConnectionShutdownException -> {
                        msg = "Connection shutdown. Please check your internet"
                        Log.i("zz","msg3--->"+msg)

                    }
                    is IOException -> {
                        msg = "Server is unreachable, please try again later."
                        Log.i("zz","msg4--->"+msg)

                    }
                    is IllegalStateException -> {
                        msg = "${e.message}"
                        Log.i("zz","msg5--->"+msg)

                    }
                    else -> {
                        msg = "${e.message}"
                        Log.i("zz","msg6--->"+msg)

                    }

                }

                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(999)
                    .message(msg)
                    .body(ResponseBody.create(null, "{${e}}")).build()

            }



        }
    }


    fun <T> buildService(serviceType: Class<T>): T {
        URL = "https://www.ekuhipath.com/api/ekuhipath-v1/"
        Log.i("qqq","URL->"+URL)
        try {
            okHttp = OkHttpClient.Builder()
                .callTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(headerInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(logger)


            // Create Activities Builder
            builder = Retrofit.Builder().baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttp.build())

            // Create Activities Instance
            retrofit = builder.build()
            // Create OkHttp Client

        }catch (e: Exception){
            Log.i("xx","Socket time out---->")
        }
        return retrofit.create(serviceType)
    }



}