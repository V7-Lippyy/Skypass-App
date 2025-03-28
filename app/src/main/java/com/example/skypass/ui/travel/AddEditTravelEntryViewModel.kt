package com.example.skypass.ui.travel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skypass.data.model.TravelCategory
import com.example.skypass.data.model.TravelTag
import com.example.skypass.data.repository.LocationRepository
import com.example.skypass.data.repository.TravelEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddEditTravelEntryViewModel @Inject constructor(
    private val repository: TravelEntryRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Entry ID for editing (0 if adding new)
    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: 0L
    private val isEdit = entryId != 0L

    // UI state
    private val _uiState = MutableStateFlow<AddEditTravelEntryState>(
        if (isEdit) AddEditTravelEntryState.Loading else AddEditTravelEntryState.Initial
    )
    val uiState: StateFlow<AddEditTravelEntryState> = _uiState.asStateFlow()

    // Form state
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _date = MutableStateFlow(Date())
    val date: StateFlow<Date> = _date.asStateFlow()

    private val _category = MutableStateFlow("General")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    private val _compassDirection = MutableStateFlow(0f)
    val compassDirection: StateFlow<Float> = _compassDirection.asStateFlow()

    private val _distance = MutableStateFlow<Float?>(null)
    val distance: StateFlow<Float?> = _distance.asStateFlow()

    private val _selectedTagIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedTagIds: StateFlow<List<Long>> = _selectedTagIds.asStateFlow()

    // New: Is Create Category Dialog visible
    private val _showCreateCategoryDialog = MutableStateFlow(false)
    val showCreateCategoryDialog: StateFlow<Boolean> = _showCreateCategoryDialog.asStateFlow()

    // New: New Category Name
    private val _newCategoryName = MutableStateFlow("")
    val newCategoryName: StateFlow<String> = _newCategoryName.asStateFlow()

    // New: New Category Color (default to blue)
    private val _newCategoryColor = MutableStateFlow("#4A90E2")
    val newCategoryColor: StateFlow<String> = _newCategoryColor.asStateFlow()

    // New: Available colors for category
    val availableCategoryColors = listOf(
        "#4A90E2", // Blue
        "#4CAF50", // Green
        "#FF9800", // Orange
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#F44336", // Red
        "#795548", // Brown
        "#607D8B"  // Blue Grey
    )

    // All tags
    val allTags = repository.getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All categories from database as TravelCategory objects
    private val _allTravelCategories = repository.getAllTravelCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Categories as category names for UI
    val categories = _allTravelCategories
        .map { categories -> categories.map { it.name } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        if (isEdit) {
            loadEntryForEditing()
        } else {
            getCurrentLocation()
        }
    }

    private fun loadEntryForEditing() {
        viewModelScope.launch {
            try {
                val entryWithTags = repository.getTravelEntryWithTags(entryId).first()

                entryWithTags?.let { entry ->
                    // Populate form state
                    _title.value = entry.travelEntry.title
                    _description.value = entry.travelEntry.description
                    _date.value = entry.travelEntry.date
                    _category.value = entry.travelEntry.category
                    _latitude.value = entry.travelEntry.latitude
                    _longitude.value = entry.travelEntry.longitude
                    _compassDirection.value = entry.travelEntry.compassDirection
                    _distance.value = entry.travelEntry.distance
                    _selectedTagIds.value = entry.tags.map { it.id }

                    _uiState.value = AddEditTravelEntryState.InputReady
                }
            } catch (e: Exception) {
                _uiState.value = AddEditTravelEntryState.Error(e.message ?: "Failed to load entry")
            }
        }
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                locationRepository.getCurrentLocation().collect { result ->
                    result.fold(
                        onSuccess = { location ->
                            _latitude.value = location.latitude
                            _longitude.value = location.longitude
                            _uiState.value = AddEditTravelEntryState.InputReady
                        },
                        onFailure = {
                            // Still allow input without location
                            _uiState.value = AddEditTravelEntryState.InputReady
                        }
                    )
                }
            } catch (e: Exception) {
                // Still allow input without location
                _uiState.value = AddEditTravelEntryState.InputReady
            }
        }
    }

    // Update form values
    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateDescription(description: String) {
        _description.value = description
    }

    fun updateDate(date: Date) {
        _date.value = date
    }

    fun updateCategory(category: String) {
        _category.value = category
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
    }

    fun updateCompassDirection(direction: Float) {
        _compassDirection.value = direction
    }

    fun updateDistance(distance: Float?) {
        _distance.value = distance
    }

    fun toggleTagSelection(tagId: Long) {
        val currentSelectedTags = _selectedTagIds.value.toMutableList()
        if (tagId in currentSelectedTags) {
            currentSelectedTags.remove(tagId)
        } else {
            currentSelectedTags.add(tagId)
        }
        _selectedTagIds.value = currentSelectedTags
    }

    // Category dialog functions
    fun showCreateCategoryDialog() {
        _showCreateCategoryDialog.value = true
    }

    fun hideCreateCategoryDialog() {
        _showCreateCategoryDialog.value = false
        // Reset form
        _newCategoryName.value = ""
        _newCategoryColor.value = "#4A90E2" // Reset to default blue
    }

    fun updateNewCategoryName(name: String) {
        _newCategoryName.value = name
    }

    fun updateNewCategoryColor(color: String) {
        _newCategoryColor.value = color
    }

    fun createNewCategory() {
        val categoryName = _newCategoryName.value
        val categoryColor = _newCategoryColor.value

        if (categoryName.isBlank()) {
            _uiState.value = AddEditTravelEntryState.Error("Category name cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                // Create the new category
                repository.createCategory(categoryName, categoryColor)

                // Set the new category as selected
                _category.value = categoryName

                // Reset dialog
                hideCreateCategoryDialog()

                // Show success message
                _uiState.value = AddEditTravelEntryState.Success("Category created: $categoryName")

                // Reset to input ready after a moment
                _uiState.value = AddEditTravelEntryState.InputReady
            } catch (e: Exception) {
                _uiState.value = AddEditTravelEntryState.Error(e.message ?: "Failed to create category")
            }
        }
    }

    // Save entry
    fun saveEntry() {
        viewModelScope.launch {
            try {
                _uiState.value = AddEditTravelEntryState.Saving

                if (!validateInput()) {
                    _uiState.value = AddEditTravelEntryState.Error("Please fill all required fields")
                    return@launch
                }

                repository.saveTravelEntryWithTags(
                    entry = com.example.skypass.data.model.TravelEntry(
                        id = entryId,
                        title = title.value,
                        description = description.value,
                        date = date.value,
                        latitude = latitude.value,
                        longitude = longitude.value,
                        compassDirection = compassDirection.value,
                        distance = distance.value,
                        category = category.value,
                        updatedAt = Date()
                    ),
                    tags = selectedTagIds.value
                )

                _uiState.value = AddEditTravelEntryState.Saved
            } catch (e: Exception) {
                _uiState.value = AddEditTravelEntryState.Error(e.message ?: "Failed to save entry")
            }
        }
    }

    private fun validateInput(): Boolean {
        return title.value.isNotBlank() &&
                latitude.value != 0.0 &&
                longitude.value != 0.0
    }

    // Create a new tag
    fun createTag(name: String, color: String) {
        viewModelScope.launch {
            try {
                if (name.isBlank()) return@launch

                val tagId = repository.insertTag(TravelTag(name = name, color = color))
                // Automatically select the newly created tag
                val currentTags = _selectedTagIds.value.toMutableList()
                currentTags.add(tagId)
                _selectedTagIds.value = currentTags
            } catch (e: Exception) {
                _uiState.value = AddEditTravelEntryState.Error(e.message ?: "Failed to create tag")
            }
        }
    }
}

// UI states
sealed class AddEditTravelEntryState {
    object Initial : AddEditTravelEntryState()
    object Loading : AddEditTravelEntryState()
    object InputReady : AddEditTravelEntryState()
    object Saving : AddEditTravelEntryState()
    object Saved : AddEditTravelEntryState()
    data class Success(val message: String) : AddEditTravelEntryState()
    data class Error(val message: String) : AddEditTravelEntryState()
}