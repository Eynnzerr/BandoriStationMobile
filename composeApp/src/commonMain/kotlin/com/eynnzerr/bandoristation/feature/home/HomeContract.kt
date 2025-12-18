package com.eynnzerr.bandoristation.feature.home

import com.eynnzerr.bandoristation.base.UIEffect
import com.eynnzerr.bandoristation.base.UIEvent
import com.eynnzerr.bandoristation.base.UIState
import com.eynnzerr.bandoristation.model.ApiRequest
import com.eynnzerr.bandoristation.model.room.RoomFilter
import com.eynnzerr.bandoristation.model.room.RoomInfo
import com.eynnzerr.bandoristation.model.UserInfo
import com.eynnzerr.bandoristation.model.GithubRelease
import com.eynnzerr.bandoristation.navigation.Screen
import org.jetbrains.compose.resources.StringResource
import bandoristationm.composeapp.generated.resources.Res
import bandoristationm.composeapp.generated.resources.home_screen_title
import com.eynnzerr.bandoristation.model.ChatMessage
import com.eynnzerr.bandoristation.model.account.AccountInfo
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupChange
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupDetails
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupMessage
import com.eynnzerr.bandoristation.model.chat_group.ChatGroupSyncData
import com.eynnzerr.bandoristation.model.room.RoomAccessRequest
import com.eynnzerr.bandoristation.ui.dialog.RequestRoomState
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class HomeState (
    val rooms: List<RoomInfo> = emptyList(),
    val roomFilter: RoomFilter = RoomFilter(),
    val hasUnReadMessages: Boolean = false,
    val selectedRoom: RoomInfo? = null,
    val serverTimestampMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val joinedTimestampMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val presetWords: Set<String> = emptySet(),
    val title: StringResource = Res.string.home_screen_title,
    val isShowingPlayerBrief: Boolean = false,
    val selectedUser: AccountInfo = AccountInfo(),
    val followingUsers: List<Long> = emptyList(),
    val isAutoUploading: Boolean = false,
    val userId: Long = 0,
    // for request room dialog
    val requestingRoomInfo: RoomInfo? = null,
    val showRequestRoomDialog: Boolean = false,
    val requestRoomState: RequestRoomState = RequestRoomState.INITIAL,
    val decryptedRoomNumber: String? = null,
    val requestRoomError: String? = null,
    // for approve request dialog
    val accessRequestQueue: List<RoomAccessRequest> = emptyList(),
    val encryptedRoomNumber: String? = null,
    // for chat group logic
    val isInChat: Boolean = false,
    val isGroupOwner: Boolean = false,
    val chatGroups: List<ChatGroupDetails> = emptyList(),
    val groupName: String = "",
    val groupMessages: List<ChatGroupMessage> = emptyList(),
) : UIState {
    companion object {
        fun initial() = HomeState(
        )
    }
}

sealed class HomeIntent: UIEvent {
    class SetNoReminder(): HomeIntent()
    data class UpdateRoomList(val rooms: List<RoomInfo>): HomeIntent()
    data class AppendRoomList(val rooms: List<RoomInfo>): HomeIntent()
    data class UpdateTimestamp(val timestampMillis: Long): HomeIntent()
    data class UpdateMessageBadge(val hasUnReadMessages: Boolean): HomeIntent()
    data class JoinRoom(val room: RoomInfo?): HomeIntent()
    data class UploadRoom(
        val number: String,
        val description: String = "",
        val continuous: Boolean = false,
        val encrypted: Boolean = false,
    ): HomeIntent()
    data class UpdatePresetWords(val words: Set<String>): HomeIntent()
    data class AddPresetWord(val word: String): HomeIntent()
    data class RemovePresetWord(val word: String): HomeIntent()
    class ClearRooms(): HomeIntent()
    class RefreshRooms(): HomeIntent()
    data class InformUser(val params: ApiRequest.InformUser): HomeIntent()
    class GetRoomFilter(): HomeIntent()
    data class UpdateRoomFilter(val filter: RoomFilter): HomeIntent()
    data class BrowseUser(val id: Long): HomeIntent()
    data class FollowUser(val id: Long): HomeIntent()

    // for request room dialog
    data class OnRequestRoom(val room: RoomInfo): HomeIntent()
    class OnDismissRequestRoomDialog: HomeIntent()
    data class OnSubmitInviteCode(val targetUser: String, val code: String): HomeIntent()
    class OnApplyOnline(val requestingRoomInfo: RoomInfo?): HomeIntent()

    // for approve request dialog
    data class RespondToAccessRequest(
        val isApproved: Boolean,
        val addToBlacklist: Boolean = false,
        val addToWhiteList: Boolean = false,
    ): HomeIntent()

    data class UpdateChatGroupList(val update: ChatGroupChange): HomeIntent()
    class RefreshChatGroupList: HomeIntent()
    data class SyncChatGroup(val syncData: ChatGroupSyncData): HomeIntent()
    data class AppendNewChat(val chatMessage: ChatGroupMessage): HomeIntent()
    data class CreateChatGroup(val name: String): HomeIntent()
    data class JoinChatGroup(val ownerId: String): HomeIntent()
    class LeaveChatGroup: HomeIntent()
    data class SendChat(val content: String): HomeIntent()
    data class RemoveUserFromGroup(val userId: String): HomeIntent()
}

sealed class HomeEffect: UIEffect {
    data class NavigateToScreen(val destination: Screen): HomeEffect()
    data class CopyRoomNumber(val roomNumber: String): HomeEffect()
    data class ShowResourceSnackbar(val textRes: StringResource): HomeEffect()
    data class ShowSnackbar(val text: String): HomeEffect()
    data class ShowRequestResultBySnackbar(val text: String, val number: String): HomeEffect()
    class ScrollToFirst: HomeEffect()
    data class OpenSendRoomDialog(
        val prefillRoomNumber: String = "",
        val prefillDescription: String = "",
    ): HomeEffect()
    class CloseSendRoomDialog(): HomeEffect()
    data class OpenInformUserDialog(
        val roomToInform: RoomInfo,
    ): HomeEffect()
    class CloseInformUserDialog(): HomeEffect()
    data class OpenFilterDialog(
        val prefillWords: List<String> = emptyList(),
        val prefillUsers: List<UserInfo> = emptyList(),
    ): HomeEffect()
    class CloseFilterDialog(): HomeEffect()
    data class OpenBlockUserDialog(
        val userToBlock: UserInfo,
    ): HomeEffect()
    class CloseBlockUserDialog(): HomeEffect()
    class OpenHelpDialog(): HomeEffect()
    class CloseHelpDialog(): HomeEffect()
    data class OpenUpdateDialog(val release: GithubRelease): HomeEffect()
    class CloseUpdateDialog(): HomeEffect()
    data class ControlProfileDialog(val visible: Boolean): HomeEffect()
    data class ControlCreateChatGroupDialog(val visible: Boolean): HomeEffect()
    data class ControlDrawer(val visible: Boolean): HomeEffect()
}