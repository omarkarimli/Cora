package com.omarkarimli.cora.ui.presentation.common.widget.sheet

import com.omarkarimli.cora.domain.models.ChatHistoryItemModel

sealed class SheetContent {
    object None : SheetContent()
    object SavingPath : SheetContent()
    object Languages : SheetContent()
    object Notifications : SheetContent()
    object LiveTranslation : SheetContent()
    object DarkMode : SheetContent()
    object DynamicColor : SheetContent()
    object ReportIssue : SheetContent()
    object ResetSettings : SheetContent()
    object Confirm : SheetContent()
    data class DeleteChatHistoryItem(val item: ChatHistoryItemModel) : SheetContent()
    object Permission : SheetContent()
}