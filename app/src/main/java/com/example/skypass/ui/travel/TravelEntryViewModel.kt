package com.example.skypass.ui.travel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skypass.data.model.TravelEntry
import com.example.skypass.data.model.TravelEntryWithTags
import com.example.skypass.data.model.TravelTag
import com.example.skypass.data.repository.TravelEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TravelEntryViewModel @Inject constructor(
    private val repository: TravelEntryRepository
) : ViewModel() {

    // UI state for travel entries list
    private val _uiState = MutableStateFlow<TravelLogUiState>(TravelLogUiState.Loading)
    val uiState: StateFlow<TravelLogUiState> = _uiState.asStateFlow()

    // Selected category filter (null means show all)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Tags state
    val allTags = repository.getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Categories state
    val categories = repository.getAllCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Combined state for entries with filters applied
    val filteredEntries = combine(
        repository.getAllTravelEntriesWithTags(),
        _selectedCategory,
        _searchQuery
    ) { entries, category, query ->
        var result = entries

        // Apply category filter if selected
        if (category != null) {
            result = result.filter { it.travelEntry.category == category }
        }

        // Apply search query if not empty
        if (query.isNotEmpty()) {
            result = result.filter {
                it.travelEntry.title.contains(query, ignoreCase = true) ||
                        it.travelEntry.description.contains(query, ignoreCase = true)
            }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadTravelEntries()
    }

    private fun loadTravelEntries() {
        viewModelScope.launch {
            try {
                _uiState.value = TravelLogUiState.Success
            } catch (e: Exception) {
                _uiState.value = TravelLogUiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    // Set category filter
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    // Set search query
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Save or update travel entry with tags
    fun saveTravelEntry(
        title: String,
        description: String,
        date: Date,
        latitude: Double,
        longitude: Double,
        compassDirection: Float,
        distance: Float?,
        category: String,
        tagIds: List<Long>,
        existingEntryId: Long = 0
    ) {
        viewModelScope.launch {
            try {
                val entry = TravelEntry(
                    id = existingEntryId,
                    title = title,
                    description = description,
                    date = date,
                    latitude = latitude,
                    longitude = longitude,
                    compassDirection = compassDirection,
                    distance = distance,
                    category = category,
                    updatedAt = Date()
                )

                repository.saveTravelEntryWithTags(entry, tagIds)
                _uiState.value = TravelLogUiState.Success
            } catch (e: Exception) {
                _uiState.value = TravelLogUiState.Error(e.message ?: "Failed to save travel entry")
            }
        }
    }

    // Delete travel entry
    fun deleteTravelEntry(entry: TravelEntry) {
        viewModelScope.launch {
            try {
                repository.deleteTravelEntry(entry)
            } catch (e: Exception) {
                _uiState.value = TravelLogUiState.Error(e.message ?: "Failed to delete travel entry")
            }
        }
    }

    // Tag operations
    fun createTag(name: String, color: String) {
        viewModelScope.launch {
            try {
                val tag = TravelTag(name = name, color = color)
                repository.insertTag(tag)
            } catch (e: Exception) {
                _uiState.value = TravelLogUiState.Error(e.message ?: "Failed to create tag")
            }
        }
    }
}

// UI states
sealed class TravelLogUiState {
    object Loading : TravelLogUiState()
    object Success : TravelLogUiState()
    data class Error(val message: String) : TravelLogUiState()
}