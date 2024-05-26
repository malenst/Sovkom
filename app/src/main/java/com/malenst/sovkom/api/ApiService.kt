package com.malenst.sovkom.api

import com.malenst.sovkom.model.Credentials
import com.malenst.sovkom.model.LoginResponse
import com.malenst.sovkom.model.Message
import com.malenst.sovkom.model.Task
import com.malenst.sovkom.model.User
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/auth/login")
    suspend fun loginUser(@Body credentials: Credentials): Response<LoginResponse>

    @POST("tasks/create")
    suspend fun createTask(@Body task: Task): Response<Task>

    @GET("tasks/{userId}")
    suspend fun getTasks(@Path("userId") userId: Long): Response<List<Task>>

    @GET("messages/{userId}")
    suspend fun getMessages(@Path("userId") userId: Long): Response<List<Message>>

    @GET("tasks/{executorId}")
    fun getTasksForDate(
        @Path("executorId") executorId: Long,
        @Query("date") date: String
    ): Call<List<Task>>

    @GET("chat/{receiverId}")
    fun getChatMessages(@Path("receiverId") receiverId: Long): Call<List<Message>>

    @GET("api/users/{userId}")
    fun getUserDetails(@Path("userId") userId: Long): Call<User>

    @FormUrlEncoded
    @POST("sendMessage")
    fun sendMessage(
        @Field("receiverId") receiverId: Long,
        @Field("senderId") senderId: Long,
        @Field("message") message: String
    ): Call<String>

}