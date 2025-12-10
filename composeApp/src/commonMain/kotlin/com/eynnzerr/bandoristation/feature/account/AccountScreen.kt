package com.eynnzerr.bandoristation.feature.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.copy_room_snackbar
import com.eynnzerr.bandoristation.feature.account.AccountIntent.*
import com.eynnzerr.bandoristation.navigation.Screen
import com.eynnzerr.bandoristation.navigation.ext.navigateTo
import com.eynnzerr.bandoristation.ui.common.LocalAppProperty
import com.eynnzerr.bandoristation.ui.component.EditAccountButton
import com.eynnzerr.bandoristation.ui.dialog.LoginDialog
import com.eynnzerr.bandoristation.ui.component.app.SuiteScaffold
import com.eynnzerr.bandoristation.ui.component.UserProfile
import com.eynnzerr.bandoristation.ui.dialog.EditProfileDialog
import com.eynnzerr.bandoristation.ui.dialog.FollowListDialog
import com.eynnzerr.bandoristation.ui.dialog.LoginScreenState
import com.eynnzerr.bandoristation.utils.rememberFlowWithLifecycle
import com.eynnzerr.bandoristation.utils.toBase64String
import kotlinx.coroutines.launch
import network.chaintech.cmpimagepickncrop.CMPImagePickNCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.ImageAspectRatio
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import org.jetbrains.compose.resources.getString
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import bandoristationm.composeapp.generated.resources.account_following_list_title
import bandoristationm.composeapp.generated.resources.account_no_followers_placeholder
import bandoristationm.composeapp.generated.resources.account_no_followings_placeholder
import bandoristationm.composeapp.generated.resources.account_follower_list_title
import bandoristationm.composeapp.generated.resources.account_logout_button
import bandoristationm.composeapp.generated.resources.account_reset_token_button
import com.eynnzerr.bandoristation.utils.resizeCenterCrop
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavHostController,
    viewModel: AccountViewModel = koinViewModel<AccountViewModel>()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isExpanded = LocalAppProperty.current.screenInfo.isLandscape()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    val clipboardManager = LocalClipboardManager.current
    var showAvatarPickerDialog by remember { mutableStateOf(false) }
    var showBannerPickerDialog by remember { mutableStateOf(false) }
    var showFollowerDialog by remember { mutableStateOf(false) }
    var showFollowingDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var loginDialogState by remember { mutableStateOf(LoginScreenState.INITIAL) }
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(effect) {
        effect.collect { action ->
            when (action) {
                is AccountEffect.ShowSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = action.text,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is AccountEffect.CopyRoomNumber -> {
                    clipboardManager.setText(AnnotatedString(action.roomNumber))
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.copy_room_snackbar),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is AccountEffect.NavigateToScreen -> {
                    navController.navigateTo(action.destination)
                }

                is AccountEffect.ControlLoginDialog -> {
                    showLoginDialog = action.visible
                    loginDialogState = LoginScreenState.INITIAL
                    if (!action.visible && !state.isLoggedIn && state.isLoading) {
                        viewModel.sendEvent(CancelLoading())
                    }
                }

                is AccountEffect.ControlDrawer -> {
                    coroutineScope.launch {
                        if (action.visible) {
                            drawerState.open()
                        } else {
                            drawerState.close()
                        }
                    }
                }

                is AccountEffect.ControlLoginDialogScreen -> {
                    loginDialogState = action.destination
                }

                is AccountEffect.PopBackLoginDialog -> {
                    loginDialogState = when (loginDialogState) {
                        LoginScreenState.REGISTER, LoginScreenState.FORGOT_PASSWORD -> LoginScreenState.PASSWORD_LOGIN
                        LoginScreenState.RESET_PASSWORD -> LoginScreenState.FORGOT_PASSWORD
                        LoginScreenState.VERIFY_EMAIL -> LoginScreenState.REGISTER
                        else -> LoginScreenState.INITIAL
                    }
                }

                is AccountEffect.ControlFollowerDialog -> {
                    // lazy loading
                    if (action.visible) {
                        viewModel.sendEvent(GetFollowers())
                    }
                    showFollowerDialog = action.visible
                }

                is AccountEffect.ControlFollowingDialog -> {
                    if (action.visible) {
                        viewModel.sendEvent(GetFollowings())
                    }
                    showFollowingDialog = action.visible
                }

                is AccountEffect.ControlEditProfileDialog -> {
                    if (action.visible) {
                        viewModel.sendEvent(GetEditProfileData())
                    }
                    showEditProfileDialog = action.visible
                }
            }
        }
    }

    LoginDialog(
        isVisible = showLoginDialog,
        currentScreen = loginDialogState,
        onPopBack = { viewModel.sendEffect(AccountEffect.PopBackLoginDialog()) },
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(false)) },
        onHelp = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.HELP)) },
        onEnterLogin = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.PASSWORD_LOGIN)) },
        onEnterToken = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.TOKEN_LOGIN)) },
        onEnterRegister = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.REGISTER)) },
        onEnterForgot = { viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.FORGOT_PASSWORD)) },
        onLoginWithToken = { token ->
            viewModel.sendEvent(GetUserInfo(token))
        },
        onLoginWithPassword = { username, password ->
            viewModel.sendEvent(Login(username, password))
        },
        onRegister = { username, password, email ->
            viewModel.sendEvent(Signup(username, password, email))
        },
        onSendVerificationCode = {
            viewModel.sendEvent(SendVerificationCode())
        },
        onVerifyCode = { code ->
            viewModel.sendEvent(VerifyEmail(code))
        },
        onSendCodeForResetPassword = {
            viewModel.sendEvent(ResetPasswordSendVCode(it))
        },
        onVerifyCodeForResetPassword = { email, code ->
            viewModel.sendEvent(ResetPasswordVerifyCode(email, code))
        },
        onResetPassword = {
            viewModel.sendEvent(ResetPassword(it))
        },
        sendCountDown = state.countDown
    )

    EditProfileDialog(
        isVisible = showEditProfileDialog,
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlEditProfileDialog(false)) },
        avatar = state.accountInfo.accountSummary.avatar,
        profile = state.editProfileData,
        onAvatarClick = {
            showAvatarPickerDialog = true
        },
        onBannerClick = {
            showBannerPickerDialog = true
        },
        onUsernameEdit = {
            viewModel.sendEvent(UpdateUsername(it))
        },
        onIntroductionEdit = {
            viewModel.sendEvent(UpdateIntroduction(it))
        },
        onPasswordEdit = { oldPassword, newPassword ->
            viewModel.sendEvent(UpdatePassword(oldPassword, newPassword))
        },
        onQQEdit = {
            viewModel.sendEvent(BindQQ(it))
        },
        onEmailEdit = { _, code ->
            viewModel.sendEvent(VerifyUpdateEmail(code))
        },
        onSendEmailVerification = {
            viewModel.sendEvent(SendUpdateEmailVCode(it))
        }
    )

    FollowListDialog(
        isVisible = showFollowingDialog,
        title = stringResource(Res.string.account_following_list_title),
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlFollowingDialog(false)) },
        followingIds = state.followingIdList,
        followList = state.followings,
        onFollow = { viewModel.sendEvent(FollowUser(it)) },
        placeholder = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.account_no_followings_placeholder))
            }
        }
    )

    FollowListDialog(
        isVisible = showFollowerDialog,
        title = stringResource(Res.string.account_follower_list_title),
        onDismissRequest = { viewModel.sendEffect(AccountEffect.ControlFollowerDialog(false)) },
        followingIds = state.followingIdList,
        followList = state.followers,
        onFollow = { viewModel.sendEvent(FollowUser(it)) },
        placeholder = {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.account_no_followers_placeholder))
            }
        }
    )

    CMPImagePickNCropDialog(
        imageCropper = rememberImageCropper(),
        openImagePicker = showAvatarPickerDialog,
        defaultAspectRatio = ImageAspectRatio(1, 1),
        cropEnable = true,
        autoZoom = false,
        enabledFlipOption = true,
        enableRotationOption = true,
        aspects = listOf(ImageAspectRatio(1, 1)),
        showCameraOption = false,
        showGalleryOption = true,
        shapes = null,
        selectedImageFileCallback = { file ->
            // Do nothing with file.
        },
        imagePickerDialogHandler = { isOpen ->
            showAvatarPickerDialog = isOpen
        },
        selectedImageCallback = { imageBitmap ->
            coroutineScope.launch {
                imageBitmap
                    .resizeCenterCrop(200, 200) // 同网页端
                    .toBase64String()?.let { base64 ->
                        viewModel.sendEvent(UpdateAvatar(base64))
                    }
            }
        },
    )

    CMPImagePickNCropDialog(
        imageCropper = rememberImageCropper(),
        openImagePicker = showBannerPickerDialog,
        defaultAspectRatio = ImageAspectRatio(3, 1),
        cropEnable = true,
        autoZoom = false,
        enabledFlipOption = true,
        enableRotationOption = true,
        aspects = listOf(ImageAspectRatio(3, 1)),
        showCameraOption = false,
        showGalleryOption = true,
        shapes = null,
        selectedImageFileCallback = { file ->
            // Do nothing with file.
        },
        imagePickerDialogHandler = { isOpen ->
            showBannerPickerDialog = isOpen
        },
        selectedImageCallback = { imageBitmap ->
            coroutineScope.launch {
                imageBitmap
                    .resizeCenterCrop(900, 300) // 同网页端
                    .toBase64String()?.let { base64 ->
                        viewModel.sendEvent(UpdateBanner(base64))
                    }
            }
        },
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Default.Logout, contentDescription = null) },
                        label = { Text(stringResource(Res.string.account_logout_button)) },
                        onClick = { viewModel.sendEvent(Logout()) },
                        selected = false,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Token, contentDescription = null) },
                        label = { Text(stringResource(Res.string.account_reset_token_button)) },
                        onClick = {
                            viewModel.sendEffect(AccountEffect.ControlLoginDialog(true))
                            viewModel.sendEffect(AccountEffect.ControlLoginDialogScreen(LoginScreenState.TOKEN_LOGIN))
                        },
                        selected = false,
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        content = {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .zIndex(Float.MAX_VALUE)
                            .imePadding()
                    )
                }
            ) { paddingValues ->
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        LoadingIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    UserProfile(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        accountInfo = state.accountInfo,
                        roomHistories = state.roomHistory.asReversed(),
                        sideButton = {
                            EditAccountButton(
                                isLoggedIn = state.isLoggedIn,
                                onLogIn = { viewModel.sendEffect(AccountEffect.ControlLoginDialog(true)) },
                                onEditAccount = {
                                    viewModel.sendEffect(AccountEffect.ControlEditProfileDialog(true))
                                },
                            )
                        },
                        onBrowseFollowings = { viewModel.sendEffect(AccountEffect.ControlFollowingDialog(true)) },
                        onBrowseFollowers = { viewModel.sendEffect(AccountEffect.ControlFollowerDialog(true)) },
                        onDeleteHistory = { viewModel.sendEvent(DeleteRoomHistory(it)) },
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun AccountScreenPreview() {
    val navController = rememberNavController()
    AccountScreen(navController)
}

private const val TAG = "AccountScreen"
