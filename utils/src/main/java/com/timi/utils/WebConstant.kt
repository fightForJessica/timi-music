package com.timi.utils

/**
 * 存放各个功能的API接口
 */
object WebConstant{
    
    const val currentUrl = "http://192.168.11.147:3000/" // 三方 API 所开启的 ip 地址

    object Login{
        const val API_TOURIST = "register/anonimous"            //游客登陆
    }

    object Register{
        const val API_REGISTER = "register/cellphone"           //注册
    }

    object Captcha{
        const val API_CAPTCHA = "captcha/sent"                  //发送登录验证码
    }

    object Found{
        const val API_BANNER = "banner?type=1"                  //发现页轮播图
        const val API_RECOMMANDPLAY = "personalized?limit=15"   //发现页推荐歌单
    }

    object PlaylistSquare{
        const val API_PLAYLIST_CATEGORY = "playlist/catlist"    //歌单广场-歌单分类
        const val API_PLAYLIST_CATEGORY_DETAIL = "top/playlist"//歌单广场-网友精选歌单
        const val API_PLAYLIST_CATEGORY_DETAIL_QUALITY = "top/playlist/highquality"//歌单广场-获取精品歌单
    }

    object Search{
        const val API_HOT_RECOMMAND = "search/hot"              //简略热搜(猜你喜欢)
        const val API_HOT_SEARCH = "search/hot/detail"          //热搜榜
        const val API_HOT_ARTISTS = "top/artists?limit=20"      //热门歌手榜
        const val API_SEARCH = "search"                         //搜索
    }

    object General{
        const val API_PLAYLIST_DETAIL = "playlist/detail"       //获取歌单详情
    }

    object Song{
        const val API_SONG_URL = "song/url"                     //获取歌曲url
        const val API_SONG_LYRIC = "lyric"                      //获取歌曲歌词
    }

}