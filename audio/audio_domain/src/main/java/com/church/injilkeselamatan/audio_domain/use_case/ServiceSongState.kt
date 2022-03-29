package com.church.injilkeselamatan.audio_domain.use_case

import androidx.lifecycle.MutableLiveData

data class ServiceSongState(
    val curSongDuration: MutableLiveData<Long> = MutableLiveData(),
    val curSongIndex: MutableLiveData<Int> = MutableLiveData()
)
