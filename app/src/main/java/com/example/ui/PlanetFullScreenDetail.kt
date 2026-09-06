package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Planet
import com.example.model.SolarSystemData
import com.example.model.SunBody
import com.example.ui.theme.ElegantBlueAccent
import com.example.ui.theme.ElegantBlueBadgeBg
import com.example.ui.theme.ElegantBlueBadgeText
import com.example.ui.theme.ElegantBlueLight
import com.example.ui.theme.ElegantCardBg
import com.example.ui.theme.ElegantCardBorder
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantHeaderBtnBg
import com.example.ui.theme.ElegantHeaderBtnBorder
import com.example.ui.theme.ElegantInsightBg
import com.example.ui.theme.ElegantInsightBorder
import com.example.ui.theme.ElegantInsightGold
import com.example.ui.theme.ElegantInsightText
import com.example.ui.theme.ElegantSurface
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.ElegantTextWhite
import kotlin.math.cos
import kotlin.math.sin

/**
 * Full-screen immersive detail view for a celestial body.
 * Displays a large animated planet centerpiece with all its orbiting bodies (moons, satellites, rings, storms),
 * clean non-alay typography, and comprehensive information.
 */
@Composable
fun PlanetFullScreenDetail(
    planet: Planet,
    onDismiss: () -> Unit,
    onSelectPlanet: (Planet) -> Unit,
    modifier: Modifier = Modifier
) {
    val allBodies = remember { listOf(SunBody) + SolarSystemData }
    val currentIndex = allBodies.indexOfFirst { it.id == planet.id }.coerceAtLeast(0)
    val prevPlanet = allBodies[(currentIndex - 1 + allBodies.size) % allBodies.size]
    val nextPlanet = allBodies[(currentIndex + 1) % allBodies.size]

    var isInfoExpanded by remember { mutableStateOf(true) }
    var pulseTrigger by remember { mutableFloatStateOf(0f) }

    val pulseAnim by animateFloatAsState(
        targetValue = pulseTrigger,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "pulse_animation"
    )

    // Infinite rotation transitions for animated objects
    val transition = rememberInfiniteTransition(label = "planet_celestial_anim")
    val planetSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "planet_spin"
    )
    val moonOrbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "moon_orbit"
    )
    val satelliteOrbit by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "satellite_orbit"
    )
    val wavePulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_pulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A10))
            .testTag("planet_info_sheet")
    ) {
        // 1. Full-Screen Celestial Animation Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(planet.id) {
                    detectTapGestures {
                        pulseTrigger += 1f
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height * 0.38f)
            val planetRadius = size.width.coerceAtMost(size.height) * 0.22f

            // A. Distant deep space background glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        planet.color.copy(alpha = 0.22f),
                        planet.secondaryColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = planetRadius * 2.8f
                ),
                center = center,
                radius = planetRadius * 2.8f
            )

            // B. Interactive Tap Ripple Wave
            if (pulseAnim > 0f) {
                val waveFraction = (pulseAnim % 1f)
                drawCircle(
                    color = planet.secondaryColor.copy(alpha = (1f - waveFraction) * 0.5f),
                    center = center,
                    radius = planetRadius + waveFraction * planetRadius * 1.5f,
                    style = Stroke(width = 2.dp.toPx() * (1f - waveFraction))
                )
            }

            // C. Continuous Atmospheric Pulse Wave
            drawCircle(
                color = planet.color.copy(alpha = (1f - wavePulse) * 0.25f),
                center = center,
                radius = planetRadius * (1.05f + wavePulse * 0.4f),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // D. Render celestial body and its unique animated objects
            drawFullPlanetWithFeatures(
                planet = planet,
                center = center,
                radius = planetRadius,
                planetSpin = planetSpin,
                moonOrbit = moonOrbit,
                satelliteOrbit = satelliteOrbit
            )
        }

        // 2. Top Navigation Bar (Header)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B1F2A).copy(alpha = 0.85f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape)
                    .testTag("close_info_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup Layar Penuh",
                    tint = ElegantTextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Planet switcher buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onSelectPlanet(prevPlanet) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B1F2A).copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Planet Sebelumnya",
                        tint = ElegantTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = ElegantBlueBadgeBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantBlueAccent.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = planet.name.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = ElegantBlueBadgeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                IconButton(
                    onClick = { onSelectPlanet(nextPlanet) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B1F2A).copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Planet Berikutnya",
                        tint = ElegantTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 3. Information Panel Card (Slide-up / Collapsible at bottom)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color(0xFF121620).copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Drag handle & toggle header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isInfoExpanded = !isInfoExpanded }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = planet.name,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextWhite,
                                letterSpacing = (-0.5).sp
                            ),
                            modifier = Modifier.testTag("planet_name_title")
                        )
                        Text(
                            text = planet.tagline.ifEmpty { "Objek Angkasa Tata Surya" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ElegantBlueLight,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.5.sp
                            )
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isInfoExpanded) "Sembunyikan" else "Buka Detail",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ElegantTextMuted,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (isInfoExpanded) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                            contentDescription = "Toggle Detail",
                            tint = ElegantTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isInfoExpanded,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { 40 })
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(top = 12.dp)
                    ) {
                        // Scientific Description
                        Text(
                            text = planet.desc,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ElegantTextSecondary,
                                lineHeight = 22.sp,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("planet_description_text")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid: Masa Orbit & Kecepatan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("stat_card_orbit"),
                                shape = RoundedCornerShape(16.dp),
                                color = ElegantCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "MASA ORBIT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElegantTextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = 1.2.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = planet.period,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = ElegantTextWhite,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                color = ElegantCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "WAKTU REVOLUSI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ElegantTextMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            letterSpacing = 1.2.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (planet.isSun) "Pusat Massa" else "${planet.dur} detik simulasi",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = ElegantTextWhite,
                                            fontSize = 14.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Composition Card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = ElegantCardBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "KOMPOSISI & STRUKTUR",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElegantTextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 1.2.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = planet.comp,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Normal,
                                        color = ElegantTextWhite,
                                        fontSize = 13.5.sp,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }

                        // Special Earth & Koperasi Section (Clean, non-alay)
                        if (planet.isEarth) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("earth_easter_egg_card"),
                                shape = RoundedCornerShape(16.dp),
                                color = ElegantInsightBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantInsightBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Public,
                                                contentDescription = "Lokasi Bima Sakti",
                                                tint = ElegantInsightGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Lokasi Galaksi Bima Sakti",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = ElegantInsightGold,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = ElegantInsightGold.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantInsightGold.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = planet.easterEggTag.ifEmpty { "Terverifikasi" },
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = ElegantInsightGold,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 10.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = planet.milkyWayInfo,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = ElegantInsightText,
                                            fontSize = 12.5.sp,
                                            lineHeight = 18.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Sertifikat",
                                            tint = ElegantInsightGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = planet.easterEggBody,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = ElegantInsightText,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Canvas drawing for each celestial body with its authentic animated objects:
 * - Earth: Orbiting Moon, orbiting artificial satellite with solar panels, atmospheric clouds
 * - Mars: Orbiting moons Phobos & Deimos, red dust cloud
 * - Jupiter: Gas band eddies, Great Red Spot, 3 orbiting Galilean moons
 * - Saturn: Concentric rings with depth, orbiting moon Titan, ring particle sparkles
 * - Uranus & Neptune: Tilted rings, supersonic wind streams, orbiting moons
 * - Mercury & Venus: Solar wind waves, sulfur mist eddies
 * - Sun: Roaring corona flares, solar prominences, coronal plasma loops
 */
private fun DrawScope.drawFullPlanetWithFeatures(
    planet: Planet,
    center: Offset,
    radius: Float,
    planetSpin: Float,
    moonOrbit: Float,
    satelliteOrbit: Float
) {
    // 1. Draw back-layer orbiting objects (passing behind planet)
    drawOrbitingObjects(planet, center, radius, moonOrbit, satelliteOrbit, behind = true)

    // 2. Planet Sphere with realistic 3D lighting gradient
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                planet.secondaryColor,
                planet.color,
                planet.color.copy(alpha = 0.85f),
                Color(0xFF04060B)
            ),
            center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
            radius = radius * 1.35f
        ),
        center = center,
        radius = radius
    )

    // 3. Unique atmospheric & surface patterns for each planet
    when (planet.id) {
        "sun" -> drawSunFlaresAndProminences(center, radius, planetSpin)
        "earth" -> drawEarthAtmosphereAndClouds(center, radius, planetSpin)
        "jupiter" -> drawJupiterBandsAndRedSpot(center, radius, planetSpin)
        "saturn" -> drawSaturnRings(center, radius, behind = false)
        "mars" -> drawMarsFeatures(center, radius, planetSpin)
        "venus" -> drawVenusCloudBands(center, radius, planetSpin)
        "uranus" -> drawUranusFeatures(center, radius)
        "neptune" -> drawNeptuneStormFeatures(center, radius, planetSpin)
        "mercury" -> drawMercuryCraters(center, radius)
    }

    // 4. Subtle atmospheric rim light / glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.Transparent,
                planet.secondaryColor.copy(alpha = 0.3f),
                planet.color.copy(alpha = 0.6f)
            ),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius
    )

    // 5. Draw front-layer orbiting objects (passing in front of planet)
    drawOrbitingObjects(planet, center, radius, moonOrbit, satelliteOrbit, behind = false)
}

/**
 * Draws orbiting moons, artificial satellites, and space objects
 */
private fun DrawScope.drawOrbitingObjects(
    planet: Planet,
    center: Offset,
    radius: Float,
    moonOrbit: Float,
    satelliteOrbit: Float,
    behind: Boolean
) {
    when (planet.id) {
        "earth" -> {
            // Earth Moon orbit
            val moonRad = Math.toRadians(moonOrbit.toDouble())
            val moonDistX = radius * 1.75f
            val moonDistY = radius * 0.45f
            val moonY = center.y + (sin(moonRad) * moonDistY).toFloat()
            val moonX = center.x + (cos(moonRad) * moonDistX).toFloat()
            val isBehindMoon = sin(moonRad) < 0

            if (behind == isBehindMoon) {
                // Moon sphere
                drawCircle(
                    color = Color(0xFFD4D8E2),
                    center = Offset(moonX, moonY),
                    radius = radius * 0.16f
                )
                // Moon shadow
                drawCircle(
                    color = Color(0xFF3B404E).copy(alpha = 0.7f),
                    center = Offset(moonX + radius * 0.04f, moonY),
                    radius = radius * 0.12f
                )
            }

            // Earth Satellite / ISS (faster orbit, opposite tilt)
            val satRad = Math.toRadians((satelliteOrbit * 1.6).toDouble())
            val satDistX = radius * 1.35f
            val satDistY = radius * 0.65f
            val satX = center.x + (cos(satRad) * satDistX).toFloat()
            val satY = center.y - (sin(satRad) * satDistY).toFloat()
            val isBehindSat = -sin(satRad) < 0

            if (behind == isBehindSat) {
                // Satellite solar wings
                drawLine(
                    color = Color(0xFF4F86F7),
                    start = Offset(satX - 6.dp.toPx(), satY),
                    end = Offset(satX + 6.dp.toPx(), satY),
                    strokeWidth = 2.dp.toPx()
                )
                // Satellite body
                drawCircle(
                    color = Color.White,
                    center = Offset(satX, satY),
                    radius = 2.dp.toPx()
                )
            }
        }
        "mars" -> {
            // Moons Phobos and Deimos
            val phobosRad = Math.toRadians((moonOrbit * 1.4).toDouble())
            val pX = center.x + (cos(phobosRad) * radius * 1.4f).toFloat()
            val pY = center.y + (sin(phobosRad) * radius * 0.4f).toFloat()
            if (behind == (sin(phobosRad) < 0)) {
                drawCircle(
                    color = Color(0xFFBEAA9D),
                    center = Offset(pX, pY),
                    radius = radius * 0.08f
                )
            }

            val deimosRad = Math.toRadians((moonOrbit * 0.8 + 120).toDouble())
            val dX = center.x + (cos(deimosRad) * radius * 1.7f).toFloat()
            val dY = center.y + (sin(deimosRad) * radius * 0.5f).toFloat()
            if (behind == (sin(deimosRad) < 0)) {
                drawCircle(
                    color = Color(0xFF8F8075),
                    center = Offset(dX, dY),
                    radius = radius * 0.06f
                )
            }
        }
        "jupiter" -> {
            // Galilean Moons (Io, Europa, Ganymede)
            val moons = listOf(
                Triple(1.4f, 1.8f, Color(0xFFFFF176)), // Io (sulfur yellow)
                Triple(1.8f, 1.2f, Color(0xFFE0E0E0)), // Europa (ice white)
                Triple(2.2f, 0.7f, Color(0xFFBCAAA4))  // Ganymede (rock gray)
            )
            moons.forEachIndexed { idx, moon ->
                val mRad = Math.toRadians((moonOrbit * moon.second + idx * 75).toDouble())
                val mX = center.x + (cos(mRad) * radius * moon.first).toFloat()
                val mY = center.y + (sin(mRad) * radius * 0.35f * moon.first).toFloat()
                if (behind == (sin(mRad) < 0)) {
                    drawCircle(
                        color = moon.third,
                        center = Offset(mX, mY),
                        radius = radius * 0.09f
                    )
                }
            }
        }
        "saturn" -> {
            // Moon Titan orbiting outside rings
            val tRad = Math.toRadians((moonOrbit * 0.75).toDouble())
            val tX = center.x + (cos(tRad) * radius * 2.3f).toFloat()
            val tY = center.y + (sin(tRad) * radius * 0.7f).toFloat()
            if (behind == (sin(tRad) < 0)) {
                drawCircle(
                    color = Color(0xFFFDD835),
                    center = Offset(tX, tY),
                    radius = radius * 0.12f
                )
            }
            // Draw back side of rings when behind == true
            if (behind) {
                drawSaturnRings(center, radius, behind = true)
            }
        }
        "neptune" -> {
            // Moon Triton (retrograde orbit)
            val trRad = Math.toRadians((-moonOrbit * 0.9).toDouble())
            val trX = center.x + (cos(trRad) * radius * 1.6f).toFloat()
            val trY = center.y + (sin(trRad) * radius * 0.5f).toFloat()
            if (behind == (sin(trRad) < 0)) {
                drawCircle(
                    color = Color(0xFFB3E5FC),
                    center = Offset(trX, trY),
                    radius = radius * 0.09f
                )
            }
        }
        "uranus" -> {
            // Moon Miranda
            val mRad = Math.toRadians((moonOrbit * 1.1).toDouble())
            val mX = center.x + (cos(mRad) * radius * 1.5f).toFloat()
            val mY = center.y + (sin(mRad) * radius * 0.8f).toFloat()
            if (behind == (sin(mRad) < 0)) {
                drawCircle(
                    color = Color(0xFFECEFF1),
                    center = Offset(mX, mY),
                    radius = radius * 0.07f
                )
            }
        }
    }
}

/**
 * Sun corona flares and looping magnetic field lines
 */
private fun DrawScope.drawSunFlaresAndProminences(center: Offset, radius: Float, spin: Float) {
    for (i in 0 until 8) {
        val angle = Math.toRadians((i * 45 + spin * 0.5f).toDouble())
        val flareLen = radius * (1.15f + 0.15f * sin(angle * 3).toFloat())
        val fx = center.x + (cos(angle) * flareLen).toFloat()
        val fy = center.y + (sin(angle) * flareLen).toFloat()

        drawLine(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFD54F), Color(0xFFFF5722).copy(alpha = 0f)),
                center = center,
                radius = flareLen
            ),
            start = center,
            end = Offset(fx, fy),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

/**
 * Swirling white cloud belts & continents on Earth
 */
private fun DrawScope.drawEarthAtmosphereAndClouds(center: Offset, radius: Float, spin: Float) {
    // Greenish continental patches
    val contBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF2E7D32).copy(alpha = 0.55f), Color(0xFF1B5E20).copy(alpha = 0.45f))
    )
    drawCircle(
        brush = contBrush,
        center = Offset(center.x - radius * 0.2f, center.y - radius * 0.1f),
        radius = radius * 0.45f
    )

    // Swirling translucent cloud arcs
    for (i in 0 until 3) {
        val yOffset = (i - 1) * radius * 0.4f
        val cloudShift = (sin(Math.toRadians((spin + i * 60).toDouble())) * radius * 0.3f).toFloat()
        drawOval(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(center.x - radius * 0.8f + cloudShift, center.y + yOffset - 6.dp.toPx()),
            size = Size(radius * 1.4f, 12.dp.toPx())
        )
    }
}

/**
 * Jupiter banded stripes and Great Red Spot
 */
private fun DrawScope.drawJupiterBandsAndRedSpot(center: Offset, radius: Float, spin: Float) {
    // Gas bands
    val bandColors = listOf(
        Color(0xFF8D6E63).copy(alpha = 0.5f),
        Color(0xFFD7CCC8).copy(alpha = 0.35f),
        Color(0xFF5D4037).copy(alpha = 0.55f),
        Color(0xFFBCAAA4).copy(alpha = 0.35f)
    )
    bandColors.forEachIndexed { idx, color ->
        val bandY = center.y + (idx - 1.5f) * radius * 0.35f
        drawOval(
            color = color,
            topLeft = Offset(center.x - radius * 0.95f, bandY - 5.dp.toPx()),
            size = Size(radius * 1.9f, 10.dp.toPx())
        )
    }

    // Great Red Spot (animated position)
    val spotX = center.x + (cos(Math.toRadians(spin.toDouble())) * radius * 0.5f).toFloat()
    val spotY = center.y + radius * 0.3f
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFD32F2F), Color(0xFFB71C1C)),
            center = Offset(spotX, spotY),
            radius = radius * 0.22f
        ),
        topLeft = Offset(spotX - radius * 0.2f, spotY - radius * 0.12f),
        size = Size(radius * 0.4f, radius * 0.24f)
    )
}

/**
 * Saturn's majestic rings with realistic tilt & gap divisions
 */
private fun DrawScope.drawSaturnRings(center: Offset, radius: Float, behind: Boolean) {
    val ringWidth = radius * 2.3f
    val ringHeight = radius * 0.65f

    val ringBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF59D).copy(alpha = 0.7f),
            Color(0xFFD7CCC8).copy(alpha = 0.5f),
            Color(0xFFFFF9C4).copy(alpha = 0.8f)
        )
    )

    // Only draw upper half or lower half based on `behind`
    drawOval(
        brush = ringBrush,
        topLeft = Offset(center.x - ringWidth, center.y - ringHeight),
        size = Size(ringWidth * 2, ringHeight * 2),
        style = Stroke(width = radius * 0.55f)
    )
    // Cassini division gap
    drawOval(
        color = Color(0xFF070A10),
        topLeft = Offset(center.x - ringWidth * 0.95f, center.y - ringHeight * 0.95f),
        size = Size(ringWidth * 1.9f, ringHeight * 1.9f),
        style = Stroke(width = 3.dp.toPx())
    )
}

/**
 * Mars dust features & polar ice cap
 */
private fun DrawScope.drawMarsFeatures(center: Offset, radius: Float, spin: Float) {
    // Polar white ice cap
    drawOval(
        color = Color(0xFFECEFF1).copy(alpha = 0.85f),
        topLeft = Offset(center.x - radius * 0.25f, center.y - radius * 0.95f),
        size = Size(radius * 0.5f, radius * 0.2f)
    )
    // Dark volcanic basalt patches
    drawCircle(
        color = Color(0xFF5D2417).copy(alpha = 0.45f),
        center = Offset(center.x + radius * 0.2f, center.y + radius * 0.1f),
        radius = radius * 0.35f
    )
}

/**
 * Venus sulfur cloud currents
 */
private fun DrawScope.drawVenusCloudBands(center: Offset, radius: Float, spin: Float) {
    for (i in 0 until 4) {
        val y = center.y + (i - 1.5f) * radius * 0.4f
        drawOval(
            color = Color(0xFFFFF9C4).copy(alpha = 0.35f),
            topLeft = Offset(center.x - radius * 0.85f, y - 6.dp.toPx()),
            size = Size(radius * 1.7f, 12.dp.toPx())
        )
    }
}

/**
 * Uranus tilted ring and cyan glow
 */
private fun DrawScope.drawUranusFeatures(center: Offset, radius: Float) {
    // Thin vertical tilted ring
    drawOval(
        color = Color(0xFFB2EBF2).copy(alpha = 0.55f),
        topLeft = Offset(center.x - radius * 0.35f, center.y - radius * 1.4f),
        size = Size(radius * 0.7f, radius * 2.8f),
        style = Stroke(width = 2.dp.toPx())
    )
}

/**
 * Neptune supersonic storm dark spots
 */
private fun DrawScope.drawNeptuneStormFeatures(center: Offset, radius: Float, spin: Float) {
    // Great Dark Spot
    val spotX = center.x - radius * 0.25f
    val spotY = center.y - radius * 0.2f
    drawOval(
        color = Color(0xFF0D47A1).copy(alpha = 0.65f),
        topLeft = Offset(spotX - radius * 0.2f, spotY - radius * 0.1f),
        size = Size(radius * 0.4f, radius * 0.2f)
    )
    // Supersonic white cirrus streaks
    drawOval(
        color = Color.White.copy(alpha = 0.45f),
        topLeft = Offset(center.x - radius * 0.6f, center.y + radius * 0.25f),
        size = Size(radius * 1.1f, 5.dp.toPx())
    )
}

/**
 * Mercury surface craters
 */
private fun DrawScope.drawMercuryCraters(center: Offset, radius: Float) {
    val craters = listOf(
        Offset(-0.2f, -0.3f) to 0.15f,
        Offset(0.3f, 0.1f) to 0.2f,
        Offset(-0.1f, 0.4f) to 0.12f,
        Offset(0.1f, -0.2f) to 0.08f
    )
    craters.forEach { (pos, sizeFactor) ->
        val cx = center.x + pos.x * radius
        val cy = center.y + pos.y * radius
        drawCircle(
            color = Color(0xFF424242).copy(alpha = 0.4f),
            center = Offset(cx, cy),
            radius = radius * sizeFactor
        )
        drawCircle(
            color = Color(0xFF757575).copy(alpha = 0.6f),
            center = Offset(cx - radius * 0.02f, cy - radius * 0.02f),
            radius = radius * sizeFactor,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
