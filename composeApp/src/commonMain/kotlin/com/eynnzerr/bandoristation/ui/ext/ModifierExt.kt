package com.eynnzerr.bandoristation.ui.ext

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
fun Modifier.appBarScroll(
    scrollable: Boolean,
    topAppBarScrollBehavior: TopAppBarScrollBehavior
) =
    if (scrollable) this.then(Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)) else this