package com.eynnzerr.bandoristation.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * @param name 数据源名称
 * @param type 数据源类型
 */
@Serializable
data class SourceInfo (
  @SerialName("name") val name : String? = null,
  @SerialName("type") val type : String? = null
)