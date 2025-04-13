package com.eynnzerr.bandoristation.utils

import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.RoomInfo
import com.eynnzerr.bandoristation.model.SourceInfo
import com.eynnzerr.bandoristation.model.UserInfo

const val testToken = "wVpDGe+kD4kIIVXVxGU+COchjrJ7kj25gsdWk1WYJMXU9z+B8O/e1GlyrULo+Vj9736xO7OD6Mg8NpFf9MgMmL5m7w4KQgQrDM4ukaPZojs="

val mockRoomList = listOf(
    RoomInfo(
        number = "114514",
        rawMessage = "114514 5w 130+ 大e长 禁hdfc 欢迎清火 q1",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743581164274,
        userInfo = UserInfo(
            type = "local",
            userId = 8146,
            username = "Tsugu代发",
            avatar = "7ec6cbbc94d098d96b291ab4955baa7a.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "223344",
        rawMessage = "223344 4w 125+ 大e短 缺鼓手 欢迎萌新 q2",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743581100000,
        userInfo = UserInfo(
            type = "local",
            userId = 8147,
            username = "Kasumi代发",
            avatar = "kasumi_avatar.png",
            role = 1,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "334455",
        rawMessage = "334455 3w 120+ 小e长 缺主唱 禁断连 q3",
        sourceInfo = SourceInfo(
            name = "Local",
            type = "web"
        ),
        type = "other",
        time = 1743581000000,
        userInfo = UserInfo(
            type = "local",
            userId = 8148,
            username = "Arisa发布",
            avatar = "arisa_avatar.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "445566",
        rawMessage = "445566 5w 140+ 大e长 全开 欢迎大佬 q4",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743580900000,
        userInfo = UserInfo(
            type = "local",
            userId = 8149,
            username = "Tae转发",
            avatar = "tae_avatar.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "556677",
        rawMessage = "556677 4w 135+ 中e短 缺键盘 禁火星 q5",
        sourceInfo = SourceInfo(
            name = "Local",
            type = "web"
        ),
        type = "other",
        time = 1743580800000,
        userInfo = UserInfo(
            type = "local",
            userId = 8150,
            username = "Rimi发布",
            avatar = "rimi_avatar.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "667788",
        rawMessage = "667788 3w 125+ 小e长 缺吉他 欢迎新手 q6",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743580700000,
        userInfo = UserInfo(
            type = "local",
            userId = 8151,
            username = "Sayo代发",
            avatar = "sayo_avatar.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "778899",
        rawMessage = "778899 5w 145+ 大e短 全开 高手速来 q7",
        sourceInfo = SourceInfo(
            name = "Local",
            type = "web"
        ),
        type = "other",
        time = 1743580600000,
        userInfo = UserInfo(
            type = "local",
            userId = 8152,
            username = "Lisa发布",
            avatar = "lisa_avatar.png",
            role = 1,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "889900",
        rawMessage = "889900 4w 130+ 中e长 缺贝斯 禁断连 q8",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743580500000,
        userInfo = UserInfo(
            type = "local",
            userId = 8153,
            username = "Ako代发",
            avatar = "ako_avatar.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "990011",
        rawMessage = "990011 3w 120+ 小e短 缺主音 欢迎萌新 q9",
        sourceInfo = SourceInfo(
            name = "Local",
            type = "web"
        ),
        type = "other",
        time = 1743580400000,
        userInfo = UserInfo(
            type = "local",
            userId = 8154,
            username = "Rinko发布",
            avatar = "rinko_avatar.png",
            role = 0,
            playerBriefInfo = null,
        )
    ),
    RoomInfo(
        number = "001122",
        rawMessage = "001122 5w 150+ 大e长 全开 高难度 q10",
        sourceInfo = SourceInfo(
            name = "Tsugu",
            type = "qq"
        ),
        type = "other",
        time = 1743580300000,
        userInfo = UserInfo(
            type = "local",
            userId = 8155,
            username = "Yukina代发",
            avatar = "yukina_avatar.png",
            role = 1,
            playerBriefInfo = null,
        )
    )
)

val mockChatList = listOf(
    ChatMessage(
        timestamp = 1712306400000, // 2024-04-05 10:00:00
        content = "大家好！今天有人想一起打协力吗？",
        userInfo = UserInfo(
            userId = 1001,
            username = "Kasumi",
            avatar = "kasumi_toyama"
        )
    ),
    ChatMessage(
        timestamp = 1712306460000, // 2024-04-05 10:01:00
        content = "我可以！刚好想刷一下活动点数",
        userInfo = UserInfo(
            userId = 1002,
            username = "Arisa",
            avatar = "arisa_ichigaya"
        )
    ),
    ChatMessage(
        timestamp = 1712306520000, // 2024-04-05 10:02:00
        content = "这次活动曲的难度有点高啊，特别是EX难度的部分",
        userInfo = UserInfo(
            userId = 1003,
            username = "Tae",
            avatar = "tae_hanazono"
        )
    ),
    ChatMessage(
        timestamp = 1712306580000, // 2024-04-05 10:03:00
        content = "没关系，多练习几次就会熟悉的！我们可以一起加油！",
        userInfo = UserInfo(
            userId = 1001,
            username = "Kasumi",
            avatar = "kasumi_toyama"
        )
    ),
    ChatMessage(
        timestamp = 1712306640000, // 2024-04-05 10:04:00
        content = "我刚抽到了新的四星卡，好开心！",
        userInfo = UserInfo(
            userId = 1004,
            username = "Rimi",
            avatar = "rimi_ushigome"
        )
    ),
    ChatMessage(
        timestamp = 1712306700000, // 2024-04-05 10:05:00
        content = "恭喜！是哪张卡？",
        userInfo = UserInfo(
            userId = 1005,
            username = "Saaya",
            avatar = "saaya_yamabuki"
        )
    ),
    ChatMessage(
        timestamp = 1712306760000, // 2024-04-05 10:06:00
        content = "是限定的Poppin'Party联动卡，技能效果很不错",
        userInfo = UserInfo(
            userId = 1004,
            username = "Rimi",
            avatar = "rimi_ushigome"
        )
    ),
    ChatMessage(
        timestamp = 1712306820000, // 2024-04-05 10:07:00
        content = "我今天尝试了Master难度的曲子，全连了！",
        userInfo = UserInfo(
            userId = 1006,
            username = "Yukina",
            avatar = "yukina_minato"
        )
    ),
    ChatMessage(
        timestamp = 1712306880000, // 2024-04-05 10:08:00
        content = "太厉害了！我还在努力练习中",
        userInfo = UserInfo(
            userId = 1007,
            username = "Aya",
            avatar = "aya_maruyama"
        )
    ),
    ChatMessage(
        timestamp = 1712306940000, // 2024-04-05 10:09:00
        content = "大家有推荐的歌曲吗？我想练习一些新的曲子",
        userInfo = UserInfo(
            userId = 1008,
            username = "Kokoro",
            avatar = "kokoro_tsurumaki"
        )
    )
)