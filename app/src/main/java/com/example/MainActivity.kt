package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Planet
import com.example.ui.PlanetInfoSheet
import com.example.ui.PlanetQuickSelector
import com.example.ui.PlanetariumCanvas
import com.example.ui.theme.ElegantBlueLight
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantHeaderBtnBg
import com.example.ui.theme.ElegantHeaderBtnBorder
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextWhite
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PlanetariumApp()
            }
        }
    }
}

@Composable
fun PlanetariumApp() {
    var selectedPlanet by remember { mutableStateOf<Planet?>(null) }
    var orbitSpeedMultiplier by remember { mutableFloatStateOf(1.0f) }
    var isOrbitPaused by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ElegantDarkBg),
        containerColor = ElegantDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Cosmic Canvas with animated orbits & interactive stars
            PlanetariumCanvas(
                selectedPlanet = selectedPlanet,
                onPlanetSelected = { planet ->
                    selectedPlanet = planet
                },
                orbitSpeedMultiplier = orbitSpeedMultiplier,
                isOrbitPaused = isOrbitPaused,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Top Header & Orbit Controls Bar (Elegant Dark: p-6 flex justify-between items-center)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Planetarium Mini",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantTextWhite,
                                fontSize = 20.sp,
                                letterSpacing = (-0.4).sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Ketuk planet untuk eksplorasi",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ElegantTextMuted,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Controls: Speed cycle, Play/Pause, and Theme Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Speed Cycle Button (0.5x, 1x, 2x)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable {
                                    orbitSpeedMultiplier = when (orbitSpeedMultiplier) {
                                        1.0f -> 2.0f
                                        2.0f -> 0.5f
                                        else -> 1.0f
                                    }
                                }
                                .testTag("speed_toggle_button"),
                            shape = RoundedCornerShape(100.dp),
                            color = ElegantHeaderBtnBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantHeaderBtnBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Kecepatan orbit",
                                    tint = ElegantBlueLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${orbitSpeedMultiplier}x",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElegantBlueLight,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        // Play/Pause Orbit Button
                        IconButton(
                            onClick = { isOrbitPaused = !isOrbitPaused },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElegantHeaderBtnBg)
                                .border(1.dp, ElegantHeaderBtnBorder, CircleShape)
                                .testTag("orbit_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isOrbitPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isOrbitPaused) "Lanjutkan Orbit" else "Jeda Orbit",
                                tint = ElegantBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Elegant Aesthetic Badge Icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElegantHeaderBtnBg)
                                .border(1.dp, ElegantHeaderBtnBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Aesthetic Badge",
                                tint = ElegantBlueLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Planet Selector Pills
                PlanetQuickSelector(
                    selectedPlanet = selectedPlanet,
                    onPlanetSelected = { planet ->
                        selectedPlanet = planet
                    }
                )
            }

            // 3. Bottom Information Sheet with Elegant Dark Theme (when no planet selected)
            if (selectedPlanet == null) {
                PlanetInfoSheet(
                    planet = null,
                    onDismiss = { selectedPlanet = null },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                )
            } else {
                // Full Screen Immersive Planet Experience with animated celestial features
                com.example.ui.PlanetFullScreenDetail(
                    planet = selectedPlanet!!,
                    onDismiss = { selectedPlanet = null },
                    onSelectPlanet = { planet -> selectedPlanet = planet }
                )
            }
        }
    }
}
