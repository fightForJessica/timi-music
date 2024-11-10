package com.timi.centre.playlist.bean

data class PlaylistDetail(
    val code: Int,
    val fromUserCount: Int,
    val fromUsers: Any,
    val playlist: Playlist?,
    val privileges: List<Privilege>,
    val songFromUsers: Any,
    val urls: Any
)

data class Playlist(
    val cloudTrackCount: Int,
    val commentCount: Int,
    val coverImgUrl: String,
    val createTime: Long,
    val creator: Creator,
    val description: String,
    val englishTitle: Any,
    val gradeStatus: String,
    val highQuality: Boolean,
    val id: Long,
    val name: String,
    val ordered: Boolean,
    val playCount: Int,
    val score: Any,
    val shareCount: Int,
    val specialType: Int,
    val status: Int,
    val subscribed: Boolean,
    val subscribedCount: Int,
    val subscribers: List<Subscriber>,
    val tags: List<String>,
    val trackCount: Int,
    val trackNumberUpdateTime: Long,
    val trackUpdateTime: Long,
    val tracks: List<Track>,
    val trialMode: Int,
    val updateTime: Long,
    val userId: Long,
)

data class Privilege(
    val chargeInfoList: List<ChargeInfo>,
    val cp: Int,
    val cs: Boolean,
    val dl: Int,
    val dlLevel: String,
    val downloadMaxBrLevel: String,
    val downloadMaxbr: Int,
    val fee: Int,
    val fl: Int,
    val flLevel: String,
    val flag: Int,
    val freeTrialPrivilege: FreeTrialPrivilege,
    val id: Int,
    val maxBrLevel: String,
    val maxbr: Int,
    val paidBigBang: Boolean,
    val payed: Int,
    val pc: Any,
    val pl: Int,
    val plLevel: String,
    val playMaxBrLevel: String,
    val playMaxbr: Int,
    val preSell: Boolean,
    val realPayed: Int,
    val rightSource: Int,
    val rscl: Any,
    val sp: Int,
    val st: Int,
    val subp: Int,
    val toast: Boolean
)

data class Creator(
    val accountStatus: Int,
    val anchor: Boolean,
    val authStatus: Int,
    val authenticationTypes: Int,
    val authority: Int,
    val avatarDetail: AvatarDetail,
    val avatarImgId: Long,
    val avatarImgIdStr: String,
    val avatarImgId_str: String,
    val avatarUrl: String,
    val backgroundImgId: Long,
    val backgroundImgIdStr: String,
    val backgroundUrl: String,
    val birthday: Int,
    val city: Int,
    val defaultAvatar: Boolean,
    val description: String,
    val detailDescription: String,
    val djStatus: Int,
    val expertTags: List<String>,
    val experts: Any,
    val followed: Boolean,
    val gender: Int,
    val mutual: Boolean,
    val nickname: String,
    val province: Int,
    val remarkName: Any,
    val signature: String,
    val userId: Long,
    val userType: Int,
    val vipType: Int
)

data class Subscriber(
    val authStatus: Int,
    val authority: Int,
    val avatarUrl: String,
    val backgroundUrl: String,
    val city: Int,
    val defaultAvatar: Boolean,
    val description: String,
    val detailDescription: String,
    val djStatus: Int,
    val followed: Boolean,
    val nickname: String,
    val signature: String,
    val userId: Long,
    val userType: Int,
    val vipType: Int
)


data class Track(
    val al: Al,
    val ar: List<Ar>,
    val id: Long,
    val mv: Int,
    val name: String,
    val publishTime: Long,
    val resourceState: Boolean,
    val tns: List<String>,
    val version: Int
)

data class AvatarDetail(
    val identityIconUrl: String,
    val identityLevel: Int,
    val userType: Int
)

data class Al(
    val id: Int,
    val name: String,
    val picUrl: String,
)

data class Ar(
    val id: Int,
    val name: String,
)

data class ChargeInfo(
    val chargeMessage: Any,
    val chargeType: Int,
    val chargeUrl: Any,
    val rate: Int
)

data class FreeTrialPrivilege(
    val cannotListenReason: Int,
    val resConsumable: Boolean,
    val userConsumable: Boolean
)