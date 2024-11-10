package com.timi.centre.found.bean

data class BannerObject(
    val banners: List<Banner>?,
    val code: Int
)

data class Banner(
    val alg: String,
    val bannerBizType: String,
    val bannerId: String,
    val pic: String,
    val requestId: String,
    val showAdTag: Boolean,
    val titleColor: String,
    val typeTitle: String,
    val url: String,
    val video: Any
)
