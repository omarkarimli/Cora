package com.omarkarimli.cora.ui.presentation.screen.chatHistory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.omarkarimli.cora.domain.models.ChatHistoryItemModel
import com.omarkarimli.cora.domain.repository.ChatHistoryRepo
import com.omarkarimli.cora.domain.repository.TranslateRepository
import com.omarkarimli.cora.ui.presentation.common.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChatHistoryViewModel @Inject constructor(
    private val chatHistoryRepo: ChatHistoryRepo,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val paginatedItems: Flow<PagingData<ChatHistoryItemModel>> =
        searchQuery
            .debounce(300L)
            .distinctUntilChanged()
            .flatMapLatest { query -> chatHistoryRepo.getPagination(query) }
            .cachedIn(viewModelScope)

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearAll() {
        viewModelScope.launch {
            chatHistoryRepo.clearAll()
        }
    }

    fun deleteItem(item: ChatHistoryItemModel) {
        viewModelScope.launch {
            chatHistoryRepo.deleteInstance(item.id)
        }
    }

    fun translate(text: String): String {
        var translatedText = text
        viewModelScope.launch {
            try {
                val result = translateRepository.translate(text)
                translatedText = result
            } catch (e: Exception) {
                Log.e("ChatHistoryViewModel", "Error translating text: ${e.message}")
            }
        }
        return translatedText
    }
}
