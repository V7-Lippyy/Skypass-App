// ui/compass/CompassScreen.kt
package com.example.skypass.ui.compass

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

// Helper function to convert degrees to direction text
private fun getDirectionText(degrees: Float): String {
    val normalizedDegrees = (degrees.roundToInt() + 360) % 360
    return when (normalizedDegrees) {
        in 0..22, in 338..360 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        in 293..337 -> "NW"
        else -> "N"
    }
}

// Helper function for drawing modern compass - NOT a Composable function
private fun drawModernCompass(drawScope: DrawScope, backgroundColor: Color, accentColor: Color, textColor: Color) {
    with(drawScope) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.width / 2 - 16.dp.toPx()
        val innerRadius = outerRadius * 0.85f

        // Draw outer circle (background)
        drawCircle(
            color = backgroundColor.copy(alpha = 0.2f),
            radius = outerRadius,
            center = center
        )

        // Draw inner circle (slightly transparent)
        drawCircle(
            color = backgroundColor.copy(alpha = 0.4f),
            radius = innerRadius,
            center = center
        )

        // Draw outer border
        drawCircle(
            color = accentColor.copy(alpha = 0.6f),
            radius = outerRadius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw cardinal directions
        val cardinalPoints = mapOf(
            "N" to 0,
            "E" to 90,
            "S" to 180,
            "W" to 270
        )

        cardinalPoints.forEach { (point, angle) ->
            val radians = Math.toRadians(angle.toDouble())
            val distance = outerRadius - 28.dp.toPx()
            val x = center.x + distance * sin(radians).toFloat()
            val y = center.y - distance * cos(radians).toFloat()

            // Draw cardinal point with small circle background
            drawCircle(
                color = if (point == "N") accentColor else backgroundColor.copy(alpha = 0.6f),
                radius = 14.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Draw degree markers
        for (i in 0 until 360 step 15) {
            val radians = Math.toRadians(i.toDouble())

            // Determine marker size based on position
            val isCardinal = i % 90 == 0
            val isIntercardinal = i % 45 == 0 && !isCardinal

            val markerLength = when {
                isCardinal -> 16.dp.toPx()
                isIntercardinal -> 10.dp.toPx()
                else -> 6.dp.toPx()
            }

            val startRadius = outerRadius - markerLength
            val endRadius = outerRadius - 2.dp.toPx()

            val startX = center.x + startRadius * sin(radians).toFloat()
            val startY = center.y - startRadius * cos(radians).toFloat()
            val endX = center.x + endRadius * sin(radians).toFloat()
            val endY = center.y - endRadius * cos(radians).toFloat()

            val markerColor = when {
                isCardinal -> accentColor
                isIntercardinal -> accentColor.copy(alpha = 0.7f)
                else -> accentColor.copy(alpha = 0.4f)
            }

            drawLine(
                color = markerColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = when {
                    isCardinal -> 2.5f.dp.toPx()
                    isIntercardinal -> 1.5f.dp.toPx()
                    else -> 1f.dp.toPx()
                },
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CompassScreen(
    onNavigateBack: () -> Unit,
    viewModel: CompassViewModel = hiltViewModel()
) {
    val compassState by viewModel.compassState.collectAsState()
    val direction by viewModel.compassDirection.collectAsState()
    val sensorAccuracy by viewModel.sensorAccuracy.collectAsState()
    val context = LocalContext.current

    // Animate the compass rotation
    val rotation by animateFloatAsState(
        targetValue = -direction,
        label = "compass_rotation"
    )

    // Background gradient based on time of day
    val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val isNightTime = currentHour !in 6..18

    val backgroundGradient = if (isNightTime) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0A1128),
                Color(0xFF1C2541)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF3282B8),
                Color(0xFF0F4C75)
            )
        )
    }

    // Define custom colors for modern look
    val accentColor = if (isNightTime) Color(0xFFEE6C4D) else Color(0xFFEE6C4D)
    val textColor = Color.White
    val cardBackgroundColor = if (isNightTime)
        Color(0xFF1C2541).copy(alpha = 0.7f)
    else
        Color(0xFF0F4C75).copy(alpha = 0.7f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column {
            // Custom top app bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor
                    )
                }

                // Title
                Text(
                    text = "Compass",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )

                // Placeholder to maintain center alignment
                Box(modifier = Modifier.size(48.dp))
            }

            // Main content based on compass state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (compassState) {
                    CompassState.SensorsNotAvailable -> {
                        SensorsNotAvailableContent(accentColor, textColor, cardBackgroundColor)
                    }

                    CompassState.Initializing -> {
                        LoadingContent(accentColor)
                    }

                    CompassState.Active -> {
                        ActiveCompassContent(
                            direction = direction,
                            rotation = rotation,
                            sensorAccuracy = sensorAccuracy,
                            accentColor = accentColor,
                            textColor = textColor,
                            cardBackgroundColor = cardBackgroundColor,
                            context = context,
                            onCalibrate = {
                                viewModel.calibrate()
                                Toast.makeText(context, "Calibrating compass...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.startListening()
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.stopListening()
        }
    }
}

@Composable
fun SensorsNotAvailableContent(accentColor: Color, textColor: Color, cardBackgroundColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SensorDoor,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = accentColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Compass Sensors Not Available",
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your device doesn't have the required sensors for compass functionality.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = textColor.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun CompassWithLabels(
    rotation: Float,
    accentColor: Color,
    textColor: Color,
    cardBackgroundColor: Color
) {
    Box(
        modifier = Modifier.size(320.dp),
        contentAlignment = Alignment.Center
    ) {
        // Compass dial
        Canvas(
            modifier = Modifier
                .size(300.dp)
                .rotate(rotation)
        ) {
            drawModernCompass(this, cardBackgroundColor, accentColor, textColor)
        }

        // Cardinal direction labels (these won't rotate with the compass)
        Box(
            modifier = Modifier.size(300.dp)
        ) {
            // North
            Text(
                text = "N",
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 30.dp)
            )
            // East
            Text(
                text = "E",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = (-30).dp)
            )
            // South
            Text(
                text = "S",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-30).dp)
            )
            // West
            Text(
                text = "W",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 30.dp)
            )
        }

        // Center pointer
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(cardBackgroundColor.copy(alpha = 0.9f))
                .shadow(4.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // North arrow
            Icon(
                imageVector = Icons.Filled.Navigation,
                contentDescription = "North",
                tint = accentColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LoadingContent(accentColor: Color) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            color = accentColor,
            strokeWidth = 6.dp
        )
    }
}

@Composable
fun ActiveCompassContent(
    direction: Float,
    rotation: Float,
    sensorAccuracy: SensorAccuracy,
    accentColor: Color,
    textColor: Color,
    cardBackgroundColor: Color,
    context: Context,
    onCalibrate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Direction display card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Direction text (N, NE, E, etc.)
                Text(
                    text = getDirectionText(direction),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 36.sp
                    ),
                    color = accentColor,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Direction in degrees - fixed alignment by using textAlign and fillMaxWidth
                Text(
                    text = "${direction.roundToInt()}°",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp
                    ),
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Direction description
                val directionDescription = when(getDirectionText(direction)) {
                    "N" -> "North"
                    "NE" -> "Northeast"
                    "E" -> "East"
                    "SE" -> "Southeast"
                    "S" -> "South"
                    "SW" -> "Southwest"
                    "W" -> "West"
                    "NW" -> "Northwest"
                    else -> ""
                }

                Text(
                    text = directionDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Compass view
        Box(
            modifier = Modifier
                .size(320.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer decorative ring
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(cardBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                // Compass dial with rotation
                Canvas(
                    modifier = Modifier
                        .size(300.dp)
                        .rotate(rotation)
                ) {
                    // Using our modern drawing function
                    drawModernCompass(this, cardBackgroundColor, accentColor, textColor)
                }

                // Center circle and north pointer (fixed, doesn't rotate)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(cardBackgroundColor.copy(alpha = 0.9f))
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // North arrow (red triangle pointing north)
                    Icon(
                        imageVector = Icons.Filled.Navigation,
                        contentDescription = "North",
                        tint = accentColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom panel with accuracy and calibration
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sensor accuracy indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Accuracy: ",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    val accuracyColor = when (sensorAccuracy) {
                        SensorAccuracy.High -> Color(0xFF4CAF50)  // Green
                        SensorAccuracy.Medium -> Color(0xFFFFC107)  // Amber
                        SensorAccuracy.Low -> Color(0xFFFF5722)  // Deep Orange
                        SensorAccuracy.Unreliable -> Color(0xFF9E9E9E)  // Gray
                    }

                    val accuracyText = when (sensorAccuracy) {
                        SensorAccuracy.High -> "High"
                        SensorAccuracy.Medium -> "Medium"
                        SensorAccuracy.Low -> "Low"
                        SensorAccuracy.Unreliable -> "Unreliable"
                    }

                    val accuracyIcon = when (sensorAccuracy) {
                        SensorAccuracy.High -> Icons.Rounded.CheckCircle
                        SensorAccuracy.Medium -> Icons.Rounded.Info
                        SensorAccuracy.Low -> Icons.Rounded.Warning
                        SensorAccuracy.Unreliable -> Icons.Rounded.Error
                    }

                    Icon(
                        imageVector = accuracyIcon,
                        contentDescription = null,
                        tint = accuracyColor,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = accuracyText,
                        style = MaterialTheme.typography.titleMedium,
                        color = accuracyColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sensor accuracy tip
                if (sensorAccuracy != SensorAccuracy.High) {
                    Text(
                        text = "Move your phone in a figure 8 pattern to improve accuracy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Calibration button
                Button(
                    onClick = onCalibrate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Recalibrate",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}