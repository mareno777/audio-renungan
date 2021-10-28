package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MusicApiDto
import retrofit2.http.GET

interface SongsApi {

    @GET("api/devotional")
    suspend fun getSongs(): MusicApiDto

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