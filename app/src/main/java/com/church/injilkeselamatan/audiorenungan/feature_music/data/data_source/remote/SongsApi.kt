package com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote

import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.MusicApiDto
import com.church.injilkeselamatan.audiorenungan.feature_music.data.data_source.remote.models.UpdateSongDto
import com.church.injilkeselamatan.audiorenungan.feature_music.domain.use_case.UpdateSong
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface SongsApi {

    @GET("/audio")
    suspend fun getSongs(): MusicApiDto

    @PUT("/audio/{mediaId}")
    suspend fun updateSong(
        @Path("mediaId") mediaId: String,
        @Body updateSong: UpdateSongDto

    ): MusicApiDto

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