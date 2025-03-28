package com.example.skypass.ui.travel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skypass.data.model.TravelEntryWithTags
import com.example.skypass.data.repository.TravelEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelEntryDetailViewModel @Inject constructor(
    private val repository: TravelEntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Get entry ID from saved state handle
    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: 0L

    // UI state
    private val _uiState = MutableStateFlow<TravelEntryDetailState>(TravelEntryDetailState.Loading)
    val uiState: StateFlow<TravelEntryDetailState> = _uiState.asStateFlow()

    // Entry with tags
    val entryWithTags = repository.getTravelEntryWithTags(entryId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        loadTravelEntry()
    }

    private fun loadTravelEntry() {
        viewModelScope.launch {
            try {
                // State is updated based on Flow collection in entryWithTags
                _uiState.value = TravelEntryDetailState.Success
            } catch (e: Exception) {
                _uiState.value = TravelEntryDetailState.Error(e.message ?: "Failed to load travel entry")
            }
        }
    }

    fun deleteTravelEntry() {
        viewModelScope.launch {
            try {
                entryWithTags.value?.travelEntry?.let { entry ->
                    repository.deleteTravelEntry(entry)
                    _uiState.value = TravelEntryDetailState.Deleted
                }
            } catch (e: Exception) {
                _uiState.value = TravelEntryDetailState.Error(e.message ?: "Failed to delete travel entry")
            }
        }
    }
}

// UI states
sealed class TravelEntryDetailState {
    object Loading : TravelEntryDetailState()
    object Success : TravelEntryDetailState()
    object Deleted : TravelEntryDetailState()
    data class Error(val message: String) : TravelEntryDetailState()
}