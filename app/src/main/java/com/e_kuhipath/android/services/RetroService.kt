package com.e_kuhipath.android.services

import com.e_kuhipath.android.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetroService {

    @POST("login")
    fun getLoginTokens(@Body studentLogin: StudentLogin): Call<StudentLoginTokens>


    @POST("register")
    fun getRegisterTokens(@Body studentRegister: StudentRegister): Call<StudentRegisterTokens>

    /*@Multipart
    @POST("infotute/mysubjects/{id1}/assignment/{id2}/")
    suspend fun createAssignments(
        @Path(value = "id1", encoded = true) sectionid:String,
        @Path(value = "id2", encoded = true) subjectid:String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("word_limit") word_limit: RequestBody,
        @Part("file_upload") file_upload: RequestBody,
        @Part("file_size") file_size: RequestBody,
        @Part("max_attachment") max_attachment: RequestBody,
        @Part("enable_submit_after_due") enable_submit_after_due: RequestBody,
        @Part("online_submit") online_submit: RequestBody,
        @Part("score") score: RequestBody,
        @Part("available_date") available_date: RequestBody,
        @Part("available_time") available_time: RequestBody,
        @Part("due_date") due_date: RequestBody,
        @Part("due_time") due_time: RequestBody,
        @Part("grade_scale_type") grade_scale_type: RequestBody,
        @Part("grade_scale_id") grade_scale_id: RequestBody,
        @Part("attempt_limit") attempt_limit: RequestBody,
        @Part filetypes: List<MultipartBody.Part?>,
        @Part attachment: MultipartBody.Part?,
        @Header("Authorization") accesstoken: String
    ): Response<JsonObject>*/

    @POST("logout")
    fun getLogoutTokens(
        @Header("Authorization") accesstoken: String
    ): Call<StudentLogoutTokens>

    @GET("video-course/get-unpaid-courses")
    fun getUnpaidCourses(
        @Header("Authorization") accesstoken: String
    ): Call<UnpaidCourseReturn>

    @GET("video-course/get-unpaid-course-details/{id}")
    fun getUnpaidCourseDetails(
        @Path(value = "id", encoded = true)courseid:String, @Header("Authorization") accesstoken: String
    ): Call<UnpaidCourseDetailsReturn>

    @GET("video-course/get-paid-courses")
    suspend fun getPaidCourses(@Header("Authorization") accesstoken: String): Response<PaidCourses>

    @GET("video-course/get-paid-course-details/{id}")
    suspend fun getPaidCourseDetails(@Path(value = "id", encoded = true)subcourseid:String,@Header("Authorization") accesstoken: String): Response<PaidCourseDetails>

    @GET("video-course/get-video-details/{id}")
    suspend fun getVideoDetails(@Path(value = "id", encoded = true)videoid:String,@Header("Authorization") accesstoken: String): Response<PaidCourseVideoDetails>

}