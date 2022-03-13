package com.church.injilkeselamatan.account_data.data_source.remote.model

import com.church.injilkeselamatan.account_domain.model.GetIpAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetIpAddressDto(
    @SerialName("ip")
    val ipAddress: String
) {
    fun toGetIpAddress(): GetIpAddress {
        return GetIpAddress(ipAddress)
    }
}
