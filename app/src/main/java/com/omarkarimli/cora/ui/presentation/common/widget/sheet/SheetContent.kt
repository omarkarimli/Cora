package com.omarkarimli.cora.ui.presentation.common.widget.sheet

import com.omarkarimli.cora.domain.models.CategoryDetailModel
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.models.ItemAnalysisModel

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
    object PasteUrl : SheetContent()
    object Parts : SheetContent()
    object Confirm : SheetContent()
    data class DeleteChatHistoryItem(val item: ChatHistoryItemModel) : SheetContent()
    data class DeleteItemAnalysis(val item: ItemAnalysisModel) : SheetContent()
    data class DeleteCategoryResult(val item: CategoryDetailModel) : SheetContent()
}