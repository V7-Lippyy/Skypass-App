package com.example.skypass.ui.travel

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.skypass.ui.travel.components.CreateCategoryDialog
import com.example.skypass.ui.travel.components.TagChip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTravelEntryScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AddEditTravelEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val date by viewModel.date.collectAsState()
    val category by viewModel.category.collectAsState()
    val latitude by viewModel.latitude.collectAsState()
    val longitude by viewModel.longitude.collectAsState()
    val compassDirection by viewModel.compassDirection.collectAsState()
    val distance by viewModel.distance.collectAsState()
    val selectedTagIds by viewModel.selectedTagIds.collectAsState()
    val allTags by viewModel.allTags.collectAsState()
    val categories by viewModel.categories.collectAsState()

    // Category dialog states
    val showCreateCategoryDialog by viewModel.showCreateCategoryDialog.collectAsState()
    val newCategoryName by viewModel.newCategoryName.collectAsState()
    val newCategoryColor by viewModel.newCategoryColor.collectAsState()
    val availableCategoryColors = viewModel.availableCategoryColors

    // Local state
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var expandedCategory by remember { mutableStateOf(false) }

    // Date picker
    val calendar = Calendar.getInstance()
    calendar.time = date
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            viewModel.updateDate(calendar.time)
        },
        year,
        month,
        day
    )

    // Handle save success
    LaunchedEffect(uiState) {
        if (uiState is AddEditTravelEntryState.Saved) {
            onSaveSuccess()
        } else if (uiState is AddEditTravelEntryState.Error) {
            snackbarHostState.showSnackbar(
                message = (uiState as AddEditTravelEntryState.Error).message
            )
        } else if (uiState is AddEditTravelEntryState.Success) {
            snackbarHostState.showSnackbar(
                message = (uiState as AddEditTravelEntryState.Success).message
            )
        }
    }

    // Background gradient - matching the modern theme
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4A5BFB), // Dark blue at top
            Color(0xFF5C6CFF)  // Medium blue at bottom
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (uiState is AddEditTravelEntryState.Loading) "Edit Travel Entry" else "Add Travel Entry",
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent,
                        titleContentColor = Color.White
                    )
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (uiState !is AddEditTravelEntryState.Loading && uiState !is AddEditTravelEntryState.Saving) {
                    FloatingActionButton(
                        onClick = { viewModel.saveEntry() },
                        containerColor = Color.White,
                        contentColor = Color(0xFF4A5BFB)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Entry")
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            // Handle loading state
            if (uiState is AddEditTravelEntryState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
                return@Scaffold
            }

            // Handle saving state
            if (uiState is AddEditTravelEntryState.Saving) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
                return@Scaffold
            }

            // Main form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Title - white text field with semi-transparent background
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Title*", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date selector
                OutlinedTextField(
                    value = dateFormatter.format(date),
                    onValueChange = { },
                    label = { Text("Date", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Date",
                                tint = Color.White
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category dropdown with option to create new
                Text(
                    text = "Category",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Main dropdown field
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false },
                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                            ) {
                                // Create new category option
                                DropdownMenuItem(
                                    text = { Text("+ Create new category") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Add, contentDescription = null)
                                    },
                                    onClick = {
                                        expandedCategory = false
                                        viewModel.showCreateCategoryDialog()
                                    }
                                )

                                Divider()

                                // Existing categories
                                categories.forEach { categoryOption ->
                                    DropdownMenuItem(
                                        text = { Text(categoryOption) },
                                        onClick = {
                                            viewModel.updateCategory(categoryOption)
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = latitude.toString(),
                        onValueChange = {
                            val lat = it.toDoubleOrNull() ?: latitude
                            viewModel.updateLocation(lat, longitude)
                        },
                        label = { Text("Latitude*", color = Color.White) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = longitude.toString(),
                        onValueChange = {
                            val long = it.toDoubleOrNull() ?: longitude
                            viewModel.updateLocation(latitude, long)
                        },
                        label = { Text("Longitude*", color = Color.White) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Compass direction with visual indicator
                Text(
                    text = "Compass Direction: ${compassDirection.toInt()}°",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(compassDirection),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Slider(
                        value = compassDirection,
                        onValueChange = { viewModel.updateCompassDirection(it) },
                        valueRange = 0f..359f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Distance (optional)
                OutlinedTextField(
                    value = distance?.toString() ?: "",
                    onValueChange = {
                        val dist = it.toFloatOrNull()
                        viewModel.updateDistance(dist)
                    },
                    label = { Text("Distance (km, optional)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tags section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tags",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    TextButton(
                        onClick = { /* Show add tag dialog */ },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Tag")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (allTags.isEmpty()) {
                    Text(
                        text = "No tags available. Create your first tag!",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    // Tags in a wrapped grid (simplified from FlowRow)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Just show first 3 tags as example (you'd want a more complex layout for wrapping)
                        allTags.take(3).forEach { tag ->
                            val isSelected = selectedTagIds.contains(tag.id)
                            TagChip(
                                tagName = tag.name,
                                color = Color(android.graphics.Color.parseColor(tag.color)),
                                selected = isSelected,
                                modifier = Modifier.clickable {
                                    viewModel.toggleTagSelection(tag.id)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Description", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    }

    // Create Category Dialog
    if (showCreateCategoryDialog) {
        CreateCategoryDialog(
            categoryName = newCategoryName,
            onCategoryNameChanged = viewModel::updateNewCategoryName,
            selectedColor = newCategoryColor,
            onColorSelected = viewModel::updateNewCategoryColor,
            availableColors = availableCategoryColors,
            onCreateCategory = viewModel::createNewCategory,
            onDismiss = viewModel::hideCreateCategoryDialog
        )
    }
}