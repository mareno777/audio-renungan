package com.church.injilkeselamatan.audiorenungan.data.remote

import com.church.injilkeselamatan.audiorenungan.data.models.Music
import retrofit2.Response
import retrofit2.http.*

interface SongsApi {

    @GET("wp-content/catalog.json")
    suspend fun getSongs(): Music

//    @FormUrlEncoded
//    @Headers("Content-Type:application/x-www-form-urlencoded")
//    @POST("api/jemaat")
//    suspend fun addUser(
//        @Field("ACCESS_TOKEN") accessToken: String,
//        @Field("nij") nij: String,
//        @Field("nama") nama: String,
//        @Field("alamat") alamat: String
//    ): Response<MessageResponse>

//    @FormUrlEncoded
//    @Headers("Content-Type:application/x-www-form-urlencoded")
//    @PUT("api/jemaat")
//    suspend fun editUser(
//        @Field("ACCESS_TOKEN") accessToken: String,
//        @Field("nij") nij: String
//    ): Response<Btat>
//
//    @FormUrlEncoded
//    @Headers("Content-Type:application/x-www-form-urlencoded")
//    @HTTP(method = "DELETE", path = "api/jemaat", hasBody = true)
//    suspend fun deleteUser(
//        @Field("ACCESS_TOKEN") accessToken: String,
//        @Field("nij") nij: String
//    ): Response<Btat>
}