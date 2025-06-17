package com.eynnzerr.bandoristation.ui.theme

import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.ag_logo
import bandoristationm.composeapp.generated.resources.hhw_logo
import bandoristationm.composeapp.generated.resources.monica_logo
import bandoristationm.composeapp.generated.resources.mygo_logo
import bandoristationm.composeapp.generated.resources.pp_logo
import bandoristationm.composeapp.generated.resources.ppp_logo
import bandoristationm.composeapp.generated.resources.r_logo
import bandoristationm.composeapp.generated.resources.ras_logo
import org.jetbrains.compose.resources.DrawableResource

sealed class BandThemeConfig(
    val name: String,
    val seedColorLong: Long,
    val bandIcon: DrawableResource? = null,
    val bandLogo: DrawableResource? = null,
) {
    companion object {
        const val POPPIN_PARTY_NAME = "Poppin'Party"
        const val ROSELIA_NAME = "Roselia"
        const val AFTERGLOW_NAME = "Afterglow"
        const val HELLO_HAPPY_WORLD_NAME = "Hello, Happy World!"
        const val PASTEL_PALETTES_NAME = "Pastel*Palettes"
        const val RAS_NAME = "RAS A SUILEN"
        const val MONICA_NAME = "Morfonica"
        const val MYGO_NAME = "MyGO!!!!!"
    }

    object Default : BandThemeConfig(
        name = "",
        seedColorLong = 0xFFFFDDEE,
    )

    object PoppinParty : BandThemeConfig(
        name = POPPIN_PARTY_NAME,
        seedColorLong = 0xFFFFDDEE, // FF3377
        bandIcon = Res.drawable.ppp_logo,
        bandLogo = Res.drawable.ppp_logo,
    )

    object Roselia : BandThemeConfig(
        name = ROSELIA_NAME,
        seedColorLong = 0xFF3344AA, // 3344AA
        bandIcon = Res.drawable.r_logo,
        bandLogo = Res.drawable.r_logo,
    )

    object Afterglow : BandThemeConfig(
        name = AFTERGLOW_NAME,
        seedColorLong = 0xFFEE3344, // EE3344
        bandIcon = Res.drawable.ag_logo,
        bandLogo = Res.drawable.ag_logo,
    )

    object HelloHappyWorld : BandThemeConfig(
        name = HELLO_HAPPY_WORLD_NAME,
        seedColorLong = 0xFFFFDD00, // FFDD00
        bandIcon = Res.drawable.hhw_logo,
        bandLogo = Res.drawable.hhw_logo,
    )

    object PastelPalettes : BandThemeConfig(
        name = PASTEL_PALETTES_NAME,
        seedColorLong = 0xFF33DDAA, // 33DDAA
        bandIcon = Res.drawable.pp_logo,
        bandLogo = Res.drawable.pp_logo,
    )

    object RaiseASuilen : BandThemeConfig(
        name = RAS_NAME,
        seedColorLong = 0xFF8844DD, // 33CCCC 8844DD
        bandIcon = Res.drawable.ras_logo,
        bandLogo = Res.drawable.ras_logo,
    )

    object Morfonica : BandThemeConfig(
        name = MONICA_NAME,
        seedColorLong = 0xFF33AAFF,
        bandIcon = Res.drawable.monica_logo,
        bandLogo = Res.drawable.monica_logo,
    )

    object Mygo : BandThemeConfig(
        name = MYGO_NAME,
        seedColorLong = 0xFF3388BB,
        bandIcon = Res.drawable.mygo_logo,
        bandLogo = Res.drawable.mygo_logo,
    )
}

fun getBandConfig(name: String?) = when (name) {
    BandThemeConfig.POPPIN_PARTY_NAME -> BandThemeConfig.PoppinParty
    BandThemeConfig.ROSELIA_NAME -> BandThemeConfig.Roselia
    BandThemeConfig.AFTERGLOW_NAME -> BandThemeConfig.Afterglow
    BandThemeConfig.PASTEL_PALETTES_NAME -> BandThemeConfig.PastelPalettes
    BandThemeConfig.HELLO_HAPPY_WORLD_NAME -> BandThemeConfig.HelloHappyWorld
    BandThemeConfig.RAS_NAME -> BandThemeConfig.RaiseASuilen
    BandThemeConfig.MONICA_NAME -> BandThemeConfig.Morfonica
    BandThemeConfig.MYGO_NAME -> BandThemeConfig.Mygo
    else -> BandThemeConfig.Default
}

val bandThemeList = listOf(
    BandThemeConfig.Default,
    BandThemeConfig.PoppinParty,
    BandThemeConfig.Roselia,
    BandThemeConfig.Afterglow,
    BandThemeConfig.PastelPalettes,
    BandThemeConfig.HelloHappyWorld,
    BandThemeConfig.RaiseASuilen,
    BandThemeConfig.Morfonica,
    BandThemeConfig.Mygo,
)