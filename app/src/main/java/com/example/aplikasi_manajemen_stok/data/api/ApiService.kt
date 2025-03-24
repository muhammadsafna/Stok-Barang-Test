package com.example.aplikasimanajemenstok.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

data class LoginResponse(
    @SerializedName("statusCode") val statusCode: Int,
    val message: String,
    val data: LoginData?
)

data class LoginData(
    val email: String,
    val name: String,
    @SerializedName("department") val department: String,
    val position: String,
    @SerializedName("api_token") val apiToken: String
)

data class ItemResponse(
    @SerializedName("statusCode") val statusCode: Int,
    val message: String,
    val data: List<Item>?
)

data class Item(
    val id: Int,
    @SerializedName("item_name") val itemName: String?,
    @SerializedName("stock") val stock: Int,
    @SerializedName("unit") val unit: String?
)

interface ApiService {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("api/dev/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("api/dev/list-items")
    suspend fun getItems(
        @Header("Authorization") auth: String
    ): Response<ItemResponse>
}
