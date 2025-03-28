package com.example.skypass.ui.travel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for creating a new travel category with color selection
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryDialog(
    categoryName: String,
    onCategoryNameChanged: (String) -> Unit,
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    availableColors: List<String>,
    onCreateCategory: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create New Category",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category name input
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = onCategoryNameChanged,
                    label = { Text("Category Name") },
                    placeholder = { Text("e.g., Hiking, Road Trip, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color selection
                Text(
                    text = "Choose a color",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.height(120.dp)
                ) {
                    items(availableColors) { colorHex ->
                        ColorSelectionItem(
                            colorHex = colorHex,
                            isSelected = colorHex == selectedColor,
                            onClick = { onColorSelected(colorHex) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Create button
                Button(
                    onClick = onCreateCategory,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = categoryName.isNotBlank()
                ) {
                    Text("Create Category")
                }
            }
        }
    }
}

@Composable
private fun ColorSelectionItem(
    colorHex: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = Color(android.graphics.Color.parseColor(colorHex))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(if (isSelected) 3.dp else 0.dp)
            .background(color)
            .clickable(onClick = onClick)
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (isDarkColor(colorHex)) Color.White else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// Helper function to determine if a color is dark to choose appropriate check icon color
private fun isDarkColor(colorHex: String): Boolean {
    val color = android.graphics.Color.parseColor(colorHex)
    val darkness = 1 - (0.299 * android.graphics.Color.red(color) +
            0.587 * android.graphics.Color.green(color) +
            0.114 * android.graphics.Color.blue(color)) / 255
    return darkness >= 0.5
}