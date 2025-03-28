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
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.sp
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

        // Draw outer circle (background) with better contrast
        drawCircle(
            color = backgroundColor,
            radius = outerRadius,
            center = center
        )

        // Draw inner circle with better contrast
        drawCircle(
            color = backgroundColor.copy(alpha = 0.7f),
            radius = innerRadius,
            center = center
        )

        // Draw outer border with increased contrast
        drawCircle(
            color = accentColor.copy(alpha = 0.9f),
            radius = outerRadius,
            center = center,
            style = Stroke(width = 2.5f.dp.toPx())
        )

        // Draw a prominent North indicator
        val northRadians = Math.toRadians(0.0)
        val northDistance = outerRadius - 5.dp.toPx()
        val northX = center.x + northDistance * sin(northRadians).toFloat()
        val northY = center.y - northDistance * cos(northRadians).toFloat()

        // Triangle for North
        val triangleHeight = 24.dp.toPx()
        val triangleBase = 14.dp.toPx()
        val northPath = Path().apply {
            moveTo(northX, northY - triangleHeight/2)
            lineTo(northX - triangleBase/2, northY + triangleHeight/2)
            lineTo(northX + triangleBase/2, northY + triangleHeight/2)
            close()
        }
        drawPath(
            path = northPath,
            color = Color(0xFFFF5252), // Red color for North
            alpha = 0.9f
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
                color = when(point) {
                    "N" -> Color(0xFFFF5252).copy(alpha = 0.8f) // Red for North
                    "S" -> Color(0xFF42A5F5).copy(alpha = 0.8f) // Blue for South
                    else -> backgroundColor.copy(alpha = 0.6f)
                },
                radius = 18.dp.toPx(),
                center = Offset(x, y)
            )

            // Draw text for cardinal points
            drawContext.canvas.nativeCanvas.apply {
                val fontSize = 14.sp.toPx()
                val fontColor = if (point == "N") Color(0xFFFFFFFF) else accentColor
                drawText(
                    point,
                    x,
                    y + fontSize/3,
                    android.graphics.Paint().apply {
                        color = fontColor.toArgb()
                        textSize = fontSize
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = true
                    }
                )
            }
        }

        // Draw degree markers with more contrast
        for (i in 0 until 360 step 15) {
            val radians = Math.toRadians(i.toDouble())

            // Determine marker size based on position
            val isCardinal = i % 90 == 0
            val isIntercardinal = i % 45 == 0 && !isCardinal

            val markerLength = when {
                isCardinal -> 20.dp.toPx()
                isIntercardinal -> 14.dp.toPx()
                else -> 8.dp.toPx()
            }

            val startRadius = outerRadius - markerLength
            val endRadius = outerRadius - 2.dp.toPx()

            val startX = center.x + startRadius * sin(radians).toFloat()
            val startY = center.y - startRadius * cos(radians).toFloat()
            val endX = center.x + endRadius * sin(radians).toFloat()
            val endY = center.y - endRadius * cos(radians).toFloat()

            val markerColor = when {
                i == 0 -> Color(0xFFFF5252) // Red for North
                isCardinal -> accentColor
                isIntercardinal -> accentColor.copy(alpha = 0.9f)
                else -> accentColor.copy(alpha = 0.7f)
            }

            drawLine(
                color = markerColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = when {
                    i == 0 -> 5.dp.toPx()
                    isCardinal -> 4.dp.toPx()
                    isIntercardinal -> 3.dp.toPx()
                    else -> 2.dp.toPx()
                },
                cap = StrokeCap.Round
            )
        }

        // Add directional lines/patterns to make rotation more visible
        for (i in 0 until 360 step 30) {
            if (i % 90 != 0) { // Skip cardinal points
                val radians = Math.toRadians(i.toDouble())
                val arrowLength = 28.dp.toPx()
                val arrowStart = innerRadius - 20.dp.toPx()

                val startX = center.x + arrowStart * sin(radians).toFloat()
                val startY = center.y - arrowStart * cos(radians).toFloat()
                val endX = center.x + (arrowStart - arrowLength) * sin(radians).toFloat()
                val endY = center.y - (arrowStart - arrowLength) * cos(radians).toFloat()

                drawLine(
                    color = accentColor.copy(alpha = 0.8f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    // Enhanced gradient background for glass effect
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF3A80D2), // Darker blue at top
            Color(0xFF4A90E2), // Mid blue
            Color(0xFF62A6EB)  // Lighter blue at bottom
        )
    )

    // Define colors to match ModernTravelLogScreen
    val accentColor = Color.White
    val textColor = Color.White
    val cardBackgroundColor = Color.White.copy(alpha = 0.1f)
    val buttonColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Compass", color = Color.White) },
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
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            floatingActionButton = {
                if (compassState == CompassState.Active) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.calibrate()
                            Toast.makeText(context, "Calibrating compass...", Toast.LENGTH_SHORT).show()
                        },
                        containerColor = Color.White.copy(alpha = 0.9f),
                        contentColor = Color(0xFF4A90E2),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 12.dp
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recalibrate")
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            // Main content based on compass state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when (compassState) {
                    CompassState.SensorsNotAvailable -> {
                        ModernSensorsNotAvailableContent()
                    }

                    CompassState.Initializing -> {
                        ModernLoadingContent()
                    }

                    CompassState.Active -> {
                        ModernActiveCompassContent(
                            direction = direction,
                            rotation = rotation,
                            sensorAccuracy = sensorAccuracy
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
fun ModernSensorsNotAvailableContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SensorDoor,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Compass Sensors Not Available",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your device doesn't have the required sensors for compass functionality.",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernLoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(80.dp),
                strokeWidth = 6.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Initializing Compass",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModernActiveCompassContent(
    direction: Float,
    rotation: Float,
    sensorAccuracy: SensorAccuracy
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Direction display card with glass effect
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.White.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Direction heading with icon and text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Current Direction",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

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
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Direction value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Direction text (N, NE, E, etc.)
                    Text(
                        text = getDirectionText(direction),
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Direction in degrees
                    Text(
                        text = "${direction.roundToInt()}°",
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accuracy indicator
                val accuracyColor = when (sensorAccuracy) {
                    SensorAccuracy.High -> Color(0xFF4CAF50)  // Green
                    SensorAccuracy.Medium -> Color(0xFFFFC107)  // Amber
                    SensorAccuracy.Low -> Color(0xFFFF5722)  // Deep Orange
                    SensorAccuracy.Unreliable -> Color(0xFF9E9E9E)  // Gray
                }

                val accuracyText = when (sensorAccuracy) {
                    SensorAccuracy.High -> "High Accuracy"
                    SensorAccuracy.Medium -> "Medium Accuracy"
                    SensorAccuracy.Low -> "Low Accuracy"
                    SensorAccuracy.Unreliable -> "Unreliable"
                }

                val accuracyIcon = when (sensorAccuracy) {
                    SensorAccuracy.High -> Icons.Rounded.CheckCircle
                    SensorAccuracy.Medium -> Icons.Rounded.Info
                    SensorAccuracy.Low -> Icons.Rounded.Warning
                    SensorAccuracy.Unreliable -> Icons.Rounded.Error
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = accuracyIcon,
                        contentDescription = null,
                        tint = accuracyColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = accuracyText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = accuracyColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sensor accuracy tip
                if (sensorAccuracy != SensorAccuracy.High) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Move your phone in a figure 8 pattern to improve accuracy",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Compass visualization with glass effect
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    spotColor = Color.White.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Compass dial with rotation
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(300.dp)
                            .rotate(rotation)
                    ) {
                        drawModernCompass(
                            this,
                            Color(0xFF294D7F), // Darker blue background
                            Color.White.copy(alpha = 0.9f), // More opaque white
                            Color.White  // Fully opaque white text
                        )
                    }

                    // Static indicator for reference direction
                    Box(
                        modifier = Modifier.size(300.dp)
                    ) {
                        // Reference indicator at the top with stronger contrast
                        Box(
                            modifier = Modifier
                                .size(20.dp, 40.dp)
                                .align(Alignment.TopCenter)
                                .offset(y = (-5).dp)
                                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                                .background(Color(0xFFFF5252))
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                                )
                        )
                    }

                    // Center pointer with glass effect
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .shadow(6.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4A90E2).copy(alpha = 0.9f),
                                        Color(0xFF5C9CE5).copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // North arrow
                        Icon(
                            imageVector = Icons.Filled.Navigation,
                            contentDescription = "North",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}