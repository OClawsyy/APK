package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.model.Planet
import com.example.model.SolarSystemData
import com.example.model.SunBody
import com.example.ui.theme.ElegantBlueAccent
import com.example.ui.theme.ElegantDarkBg
import com.example.ui.theme.ElegantSunEnd
import com.example.ui.theme.ElegantSunGlow
import com.example.ui.theme.ElegantSunStart
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

// Parallax star representation for multi-depth space background
private data class ParallaxStar(
    val xRatio: Float,
    val yRatio: Float,
    val size: Float,
    val alphaBase: Float,
    val twinkleSpeed: Float,
    val color: Color,
    val depth: Float // Depth factor: distant (0.2f), midground (0.6f), foreground (1.25f)
)

// Nature of particles based on real celestial composition
private enum class ParticleType {
    ROCKY_DUST,       // Mercury, Mars: fine sharp mineral and rust dust
    SULFUR_VAPOR,     // Venus: dense swirling sulfurous acidic mist
    WATER_ATMOSPHERE, // Earth: gentle aquatic moisture & nitrogen-oxygen cloudlets
    GAS_STORM,        // Jupiter: large swirling ammonia & hydrogen storm eddies
    ICE_CRYSTAL,      // Saturn: glistening reflective ice crystal flakes & ring dust
    METHANE_HAZE,     // Uranus: cool cyan methane ice crystal mist
    WIND_STREAK,      // Neptune: high-velocity supersonic frozen methane cloud particles
    SOLAR_PLASMA      // Sun: glowing solar flare sparks & hot plasma
}

// Tailored particle instance
private data class CelestialParticle(
    val angleOffset: Float,
    val radiusRatio: Float, // Relative distance multiplier from planet surface
    val speed: Float,
    val size: Float,
    val baseAlpha: Float,
    val color: Color,
    val type: ParticleType,
    val wobbleFreq: Float = 1f
)

@Composable
fun PlanetariumCanvas(
    selectedPlanet: Planet?,
    onPlanetSelected: (Planet) -> Unit,
    orbitSpeedMultiplier: Float,
    isOrbitPaused: Boolean,
    modifier: Modifier = Modifier
) {
    // 1. Generate multi-depth Parallax stars once (3 distinct depth layers for 3D illusion)
    val parallaxStars = remember {
        val rand = Random(42)
        val list = mutableListOf<ParallaxStar>()

        // Layer 0: Distant background stars (slow parallax 0.2f, smaller, subtle)
        repeat(55) {
            list.add(
                ParallaxStar(
                    xRatio = rand.nextFloat(),
                    yRatio = rand.nextFloat(),
                    size = rand.nextFloat() * 1.0f + 0.7f,
                    alphaBase = rand.nextFloat() * 0.35f + 0.2f,
                    twinkleSpeed = rand.nextFloat() * 1.5f + 0.8f,
                    color = Color.White.copy(alpha = 0.8f),
                    depth = 0.2f
                )
            )
        }

        // Layer 1: Midground stars (medium parallax 0.6f, warm/cool tints)
        repeat(45) {
            val color = when (rand.nextInt(4)) {
                0 -> Color(0xFFD6E4FF) // blue tint
                1 -> Color(0xFFFFF0D4) // warm amber tint
                2 -> Color(0xFFE2F7FF) // cyan tint
                else -> Color.White
            }
            list.add(
                ParallaxStar(
                    xRatio = rand.nextFloat(),
                    yRatio = rand.nextFloat(),
                    size = rand.nextFloat() * 1.6f + 1.2f,
                    alphaBase = rand.nextFloat() * 0.5f + 0.4f,
                    twinkleSpeed = rand.nextFloat() * 2.2f + 1.2f,
                    color = color,
                    depth = 0.6f
                )
            )
        }

        // Layer 2: Foreground close stars (fast parallax 1.25f, bright, large, noticeable depth)
        repeat(20) {
            val color = if (rand.nextBoolean()) Color(0xFFFFFFFF) else Color(0xFFFFF9E6)
            list.add(
                ParallaxStar(
                    xRatio = rand.nextFloat(),
                    yRatio = rand.nextFloat(),
                    size = rand.nextFloat() * 2.2f + 2.0f,
                    alphaBase = rand.nextFloat() * 0.3f + 0.7f,
                    twinkleSpeed = rand.nextFloat() * 2.8f + 1.8f,
                    color = color,
                    depth = 1.25f
                )
            )
        }
        list
    }

    // 2. Pre-generate specific particle systems tailored for each planet based on its composition
    val allBodies = remember { listOf(SunBody) + SolarSystemData }
    val planetParticleSystems = remember {
        allBodies.associate { planet ->
            planet.id to createParticlesForPlanet(planet)
        }
    }

    // Smooth hover/active alpha per planet (0f = hidden, 1f = visible)
    val particleAlphaMap = remember { mutableStateMapOf<String, Float>() }

    // Hover state tracking
    var hoverPointerOffset by remember { mutableStateOf<Offset?>(null) }
    var hoveredPlanetId by remember { mutableStateOf<String?>(null) }

    // Tap pulse & shake state
    var tappedPlanetId by remember { mutableStateOf<String?>(null) }
    var tapAnimStartTime by remember { mutableFloatStateOf(0f) }

    // Continuous time accumulator for orbits
    var elapsedTimeSeconds by remember { mutableFloatStateOf(0f) }
    var lastNanoTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isOrbitPaused, orbitSpeedMultiplier) {
        lastNanoTime = 0L
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (lastNanoTime != 0L) {
                    val deltaSeconds = (frameTimeNanos - lastNanoTime) / 1_000_000_000f
                    val safeDelta = deltaSeconds.coerceIn(0f, 0.1f)

                    if (!isOrbitPaused) {
                        elapsedTimeSeconds += safeDelta * orbitSpeedMultiplier
                    }

                    // Smoothly animate particle alpha for hover and selection transitions
                    allBodies.forEach { planet ->
                        val isTargetActive = (hoveredPlanetId == planet.id) || (selectedPlanet?.id == planet.id)
                        val targetAlpha = if (isTargetActive) 1f else 0f
                        val current = particleAlphaMap[planet.id] ?: 0f
                        if (current != targetAlpha) {
                            val speed = if (targetAlpha > current) 10f else 6f // quick fade in, gentle fade out
                            val step = (targetAlpha - current) * (safeDelta * speed).coerceAtMost(1f)
                            particleAlphaMap[planet.id] = (current + step).coerceIn(0f, 1f)
                        }
                    }
                }
                lastNanoTime = frameTimeNanos
            }
        }
    }

    // Gentle pulse animation for Sun aura and selected planet beacon
    val infiniteTransition = rememberInfiniteTransition(label = "celestial_loop")
    val sunPulse by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sun_pulse"
    )

    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 1.1f,
        targetValue = 1.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_pulse"
    )

    val particleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_rot"
    )

    // Zoom & Pan state for exploration
    var scale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.75f, 2.8f)
        panOffset += offsetChange
    }

    // Keep tracked planet coordinates for hit-testing & hover detection
    var currentPlanetPositions by remember {
        mutableStateOf<Map<String, Pair<Offset, Float>>>(emptyMap())
    }

    // Check hover candidate on pointer movement
    fun updateHoveredPlanet(pos: Offset?) {
        hoverPointerOffset = pos
        if (pos == null) {
            hoveredPlanetId = null
            return
        }
        var closestId: String? = null
        var minDist = Float.MAX_VALUE

        for ((id, posData) in currentPlanetPositions) {
            val (planetPos, hitRadius) = posData
            val dist = (pos - planetPos).getDistance()
            if (dist <= hitRadius && dist < minDist) {
                minDist = dist
                closestId = id
            }
        }
        hoveredPlanetId = closestId
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("planetarium_canvas_box")
            .transformable(state = transformState)
            // 1. Pointer input for Cursor Hover detection
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Move, PointerEventType.Enter -> {
                                val currentPos = event.changes.firstOrNull()?.position
                                updateHoveredPlanet(currentPos)
                            }
                            PointerEventType.Exit -> {
                                updateHoveredPlanet(null)
                            }
                        }
                    }
                }
            }
            // 2. Pointer input for Click / Tap interaction with Pulse & Shake
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    var tappedPlanet: Planet? = null
                    var minDist = Float.MAX_VALUE

                    for (planet in allBodies) {
                        val posData = currentPlanetPositions[planet.id] ?: continue
                        val (pos, hitRadius) = posData
                        val dist = (tapOffset - pos).getDistance()
                        if (dist <= hitRadius && dist < minDist) {
                            minDist = dist
                            tappedPlanet = planet
                        }
                    }

                    tappedPlanet?.let { planet ->
                        // Trigger tactile pulse and shake animation
                        tappedPlanetId = planet.id
                        tapAnimStartTime = elapsedTimeSeconds
                        onPlanetSelected(planet)
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("planetarium_canvas")
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = (canvasW / 2f) + panOffset.x
            val centerY = (canvasH / 2f) + panOffset.y
            val center = Offset(centerX, centerY)

            // Draw deep space background with elegant dark gradient
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF141824),
                        ElegantDarkBg,
                        Color(0xFF06080C)
                    ),
                    center = center,
                    radius = (canvasW.coerceAtLeast(canvasH)) * 0.85f
                )
            )

            // Parallax Starfield with 3D depth illusion:
            // Bintang foreground (depth 1.25x) bergerak jauh lebih cepat daripada latar belakang (depth 0.2x)
            parallaxStars.forEach { star ->
                val parallaxX = (panOffset.x * star.depth) % canvasW
                val parallaxY = (panOffset.y * star.depth) % canvasH

                var starX = (star.xRatio * canvasW + parallaxX) % canvasW
                if (starX < 0f) starX += canvasW
                var starY = (star.yRatio * canvasH + parallaxY) % canvasH
                if (starY < 0f) starY += canvasH

                // Twinkle modulation
                val twinkle = (sin((elapsedTimeSeconds * star.twinkleSpeed) + (star.xRatio * 10f)) + 1f) / 2f
                val alpha = (star.alphaBase * (0.5f + 0.5f * twinkle)).coerceIn(0.1f, 1f)

                drawCircle(
                    color = star.color.copy(alpha = alpha),
                    radius = star.size * scale.coerceIn(0.8f, 1.3f),
                    center = Offset(starX, starY)
                )
            }

            // Calculate scale ratio: Neptune max r is ~226
            val maxSolarRadius = 240f
            val baseRadiusScale = ((canvasW.coerceAtMost(canvasH) / 2f) * 0.88f) / maxSolarRadius
            val effectiveRadiusScale = baseRadiusScale * scale

            val newPositions = mutableMapOf<String, Pair<Offset, Float>>()

            // 1. Draw Orbit Paths (Rings)
            SolarSystemData.forEach { planet ->
                val orbitRadius = planet.r * effectiveRadiusScale
                val isSelected = selectedPlanet?.id == planet.id
                val isHovered = hoveredPlanetId == planet.id

                val orbitColor = when {
                    isSelected -> ElegantBlueAccent.copy(alpha = 0.45f)
                    isHovered -> ElegantBlueAccent.copy(alpha = 0.28f)
                    else -> Color.White.copy(alpha = 0.08f)
                }

                drawCircle(
                    color = orbitColor,
                    radius = orbitRadius,
                    center = center,
                    style = Stroke(
                        width = if (isSelected || isHovered) 1.8f else 1f,
                        pathEffect = if (isSelected) null else PathEffect.dashPathEffect(floatArrayOf(5f, 7f), 0f)
                    )
                )
            }

            // 2. Draw the Central Sun
            val sunRadius = (SunBody.size * 0.5f) * scale.coerceIn(0.7f, 1.5f)
            val sunHitRadius = (sunRadius * 2.2f).coerceAtLeast(46f)
            newPositions[SunBody.id] = Pair(center, sunHitRadius)

            // Sun pulse & shake if tapped
            var sunScaleMultiplier = 1f
            var sunShakeOffset = Offset.Zero
            if (tappedPlanetId == SunBody.id) {
                val elapsedSinceTap = elapsedTimeSeconds - tapAnimStartTime
                val animDuration = 0.38f // 380 ms smooth feedback
                if (elapsedSinceTap in 0f..animDuration) {
                    val t = elapsedSinceTap / animDuration
                    sunScaleMultiplier = 1f + 0.22f * sin(t * PI.toFloat())
                    val shakeX = sin(t * 5f * 2f * PI.toFloat()) * (1f - t) * 3f
                    sunShakeOffset = Offset(shakeX, 0f)
                } else {
                    tappedPlanetId = null
                }
            }

            val dynamicSunRadius = sunRadius * sunScaleMultiplier
            val dynamicSunCenter = center + sunShakeOffset

            // Radiant Outer Solar Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElegantSunGlow,
                        ElegantSunEnd.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = dynamicSunCenter,
                    radius = dynamicSunRadius * 3.6f * sunPulse
                ),
                radius = dynamicSunRadius * 3.6f * sunPulse,
                center = dynamicSunCenter
            )

            // Inner Core Sun
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFF9E6),
                        ElegantSunStart,
                        ElegantSunEnd
                    ),
                    center = dynamicSunCenter,
                    radius = dynamicSunRadius
                ),
                radius = dynamicSunRadius,
                center = dynamicSunCenter
            )

            // Corona Flare ring
            drawCircle(
                color = ElegantSunStart.copy(alpha = 0.35f),
                radius = dynamicSunRadius * 1.22f,
                center = dynamicSunCenter,
                style = Stroke(width = 1.6f)
            )

            // Sun particles (Solar plasma sparks on hover or selection)
            val sunParticleAlpha = particleAlphaMap[SunBody.id] ?: 0f
            if (sunParticleAlpha > 0.01f) {
                val sunParticles = planetParticleSystems[SunBody.id].orEmpty()
                drawCelestialParticles(
                    particles = sunParticles,
                    center = dynamicSunCenter,
                    planetRadius = dynamicSunRadius,
                    rotationAngle = particleRotation,
                    elapsedTime = elapsedTimeSeconds,
                    alphaFactor = sunParticleAlpha
                )
            }

            // 3. Draw Planets & their current orbital positions
            SolarSystemData.forEach { planet ->
                val orbitRadius = planet.r * effectiveRadiusScale
                val angleRad = if (planet.dur > 0f) {
                    ((elapsedTimeSeconds / planet.dur) * 2f * PI.toFloat()) % (2f * PI.toFloat())
                } else 0f

                val rawPlanetX = center.x + orbitRadius * cos(angleRad)
                val rawPlanetY = center.y + orbitRadius * sin(angleRad)
                val basePos = Offset(rawPlanetX, rawPlanetY)

                val baseRadius = (planet.size * 0.5f) * scale.coerceIn(0.8f, 1.4f)
                val hitRadius = (baseRadius * 2.8f).coerceAtLeast(44f)

                // Calculate pulse and shake animation when tapped
                var planetScaleMultiplier = 1f
                var planetShakeOffset = Offset.Zero

                if (tappedPlanetId == planet.id) {
                    val elapsedSinceTap = elapsedTimeSeconds - tapAnimStartTime
                    val animDuration = 0.38f // 380 ms smooth tactile feedback
                    if (elapsedSinceTap in 0f..animDuration) {
                        val t = elapsedSinceTap / animDuration
                        // Smooth pulse: scales up to ~1.28x then smoothly settles back
                        planetScaleMultiplier = 1f + 0.28f * sin(t * PI.toFloat())
                        // Gentle harmonic dampening shake: horizontal oscillation fading out
                        val shakeX = sin(t * 5f * 2f * PI.toFloat()) * (1f - t) * 3.5f
                        planetShakeOffset = Offset(shakeX, 0f)
                    } else {
                        // Animation finishes smoothly once tapped and panel appears
                        tappedPlanetId = null
                    }
                }

                val planetPos = basePos + planetShakeOffset
                val planetRadius = baseRadius * planetScaleMultiplier

                newPositions[planet.id] = Pair(planetPos, hitRadius)

                val isSelected = selectedPlanet?.id == planet.id
                val isHovered = hoveredPlanetId == planet.id

                // Selection beacon ring (only for selected planet)
                if (isSelected) {
                    drawCircle(
                        color = ElegantBlueAccent.copy(alpha = 0.25f),
                        radius = (planetRadius * beaconPulse + 12f),
                        center = planetPos,
                        style = Stroke(width = 3.5f)
                    )
                    drawCircle(
                        color = ElegantBlueAccent.copy(alpha = 0.65f),
                        radius = (planetRadius * beaconPulse + 6f),
                        center = planetPos,
                        style = Stroke(width = 1.5f)
                    )
                } else if (isHovered) {
                    // Soft hover ring feedback
                    drawCircle(
                        color = ElegantBlueAccent.copy(alpha = 0.4f),
                        radius = planetRadius + 7f,
                        center = planetPos,
                        style = Stroke(width = 1.8f)
                    )
                }

                // Swirling particles tailored to planet type (appears on hover or when selected, fades smoothly)
                val currentParticleAlpha = particleAlphaMap[planet.id] ?: 0f
                if (currentParticleAlpha > 0.01f) {
                    val particles = planetParticleSystems[planet.id].orEmpty()
                    drawCelestialParticles(
                        particles = particles,
                        center = planetPos,
                        planetRadius = planetRadius,
                        rotationAngle = particleRotation,
                        elapsedTime = elapsedTimeSeconds,
                        alphaFactor = currentParticleAlpha
                    )
                }

                // If planet has rings (Saturn), draw back part of ring first
                if (planet.hasRings) {
                    drawSaturnRings(
                        center = planetPos,
                        planetRadius = planetRadius,
                        ringColor = planet.ringColor,
                        tiltAngleDeg = 24f,
                        isFront = false
                    )
                }

                // Planet Body with 3D spherical gradient shading (facing toward Sun)
                val lightDx = dynamicSunCenter.x - planetPos.x
                val lightDy = dynamicSunCenter.y - planetPos.y
                val lightDist = sqrt(lightDx * lightDx + lightDy * lightDy).coerceAtLeast(1f)
                val highlightCenter = Offset(
                    planetPos.x + (lightDx / lightDist) * (planetRadius * 0.35f),
                    planetPos.y + (lightDy / lightDist) * (planetRadius * 0.35f)
                )

                // Atmospheric limb
                drawCircle(
                    color = planet.secondaryColor.copy(alpha = 0.25f),
                    radius = planetRadius + 1.8f,
                    center = planetPos
                )

                // Planet Core Sphere with radial depth gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            planet.secondaryColor,
                            planet.color,
                            planet.color.copy(alpha = 0.95f),
                            Color(0xFF090D18)
                        ),
                        center = highlightCenter,
                        radius = planetRadius * 1.35f
                    ),
                    radius = planetRadius,
                    center = planetPos
                )

                // Surface feature accents: Jupiter storm bands & Great Red Spot
                if (planet.id == "jupiter") {
                    drawJupiterDetails(planetPos, planetRadius)
                }

                // Surface feature accents: Earth continents
                if (planet.id == "earth") {
                    drawEarthDetails(planetPos, planetRadius)
                }

                // If planet has rings (Saturn), draw front part of ring over body
                if (planet.hasRings) {
                    drawSaturnRings(
                        center = planetPos,
                        planetRadius = planetRadius,
                        ringColor = planet.ringColor,
                        tiltAngleDeg = 24f,
                        isFront = true
                    )
                }
            }

            currentPlanetPositions = newPositions
        }
    }
}

/**
 * Creates particles specifically tailored to the planet's actual composition:
 * - Rocky planets (Mercury, Mars): fine mineral dust specks & rust grains.
 * - Venus: dense sulfuric acid droplets & hazy boiling vapor.
 * - Earth: gentle water moisture & atmospheric nitrogen/oxygen clouds.
 * - Jupiter: wide swirling ammonia & hydrogen storm gas eddies.
 * - Saturn: glistening reflective ice flakes & fine ring debris.
 * - Uranus: cyan methane ice crystal haze.
 * - Neptune: supersonic high-speed frozen methane wind particles.
 * - Sun: solar plasma flares and glowing corona sparks.
 */
private fun createParticlesForPlanet(planet: Planet): List<CelestialParticle> {
    val rand = Random(planet.id.hashCode())

    return when (planet.id) {
        "sun" -> List(24) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 0.8f + 1.25f,
                speed = rand.nextFloat() * 1.4f + 0.6f,
                size = rand.nextFloat() * 3.0f + 1.5f,
                baseAlpha = rand.nextFloat() * 0.5f + 0.4f,
                color = if (rand.nextBoolean()) ElegantSunStart else Color(0xFFFF5722),
                type = ParticleType.SOLAR_PLASMA,
                wobbleFreq = 2.5f
            )
        }
        "mercury" -> List(16) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 0.9f + 1.2f,
                speed = (rand.nextFloat() * 1.2f + 0.5f) * (if (rand.nextBoolean()) 1f else -1f),
                size = rand.nextFloat() * 1.6f + 0.8f, // fine sharp rocky grains
                baseAlpha = rand.nextFloat() * 0.4f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFFBCAAA4) else Color(0xFF9E9E9E),
                type = ParticleType.ROCKY_DUST
            )
        }
        "venus" -> List(22) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.1f + 1.15f,
                speed = (rand.nextFloat() * 1.5f + 0.8f),
                size = rand.nextFloat() * 3.2f + 1.8f, // dense sulfuric droplets
                baseAlpha = rand.nextFloat() * 0.45f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFFFFEE58) else Color(0xFFFFD54F),
                type = ParticleType.SULFUR_VAPOR,
                wobbleFreq = 1.8f
            )
        }
        "earth" -> List(18) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.0f + 1.2f,
                speed = (rand.nextFloat() * 1.1f + 0.5f),
                size = rand.nextFloat() * 2.2f + 1.2f, // water moisture & atmospheric haze
                baseAlpha = rand.nextFloat() * 0.5f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFFE0F7FA) else Color(0xFF80DEEA),
                type = ParticleType.WATER_ATMOSPHERE,
                wobbleFreq = 1.2f
            )
        }
        "mars" -> List(20) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.2f + 1.15f,
                speed = (rand.nextFloat() * 1.3f + 0.6f) * (if (rand.nextBoolean()) 1f else -1f),
                size = rand.nextFloat() * 2.0f + 1.0f, // reddish rust dust particles
                baseAlpha = rand.nextFloat() * 0.55f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFFFF5722) else Color(0xFFE64A19),
                type = ParticleType.ROCKY_DUST,
                wobbleFreq = 1.5f
            )
        }
        "jupiter" -> List(26) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.4f + 1.2f,
                speed = (rand.nextFloat() * 1.8f + 0.8f),
                size = rand.nextFloat() * 4.2f + 2.4f, // giant swirling gas eddies
                baseAlpha = rand.nextFloat() * 0.4f + 0.3f,
                color = if (rand.nextBoolean()) Color(0xFFFFCC80) else Color(0xFFFFE0B2),
                type = ParticleType.GAS_STORM,
                wobbleFreq = 1.6f
            )
        }
        "saturn" -> List(24) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.5f + 1.3f,
                speed = (rand.nextFloat() * 1.4f + 0.7f),
                size = rand.nextFloat() * 2.2f + 1.2f, // sparkling ice crystals & ring debris
                baseAlpha = rand.nextFloat() * 0.55f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFFFFF9C4) else Color(0xFFFFFFFF),
                type = ParticleType.ICE_CRYSTAL,
                wobbleFreq = 2.0f
            )
        }
        "uranus" -> List(18) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.2f + 1.2f,
                speed = (rand.nextFloat() * 1.2f + 0.5f),
                size = rand.nextFloat() * 2.5f + 1.4f, // cyan methane ice crystals
                baseAlpha = rand.nextFloat() * 0.45f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFFB2DFDB) else Color(0xFFE0F2F1),
                type = ParticleType.METHANE_HAZE,
                wobbleFreq = 1.4f
            )
        }
        "neptune" -> List(22) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.3f + 1.2f,
                speed = (rand.nextFloat() * 2.4f + 1.4f), // high velocity wind streaks
                size = rand.nextFloat() * 2.8f + 1.2f,
                baseAlpha = rand.nextFloat() * 0.55f + 0.35f,
                color = if (rand.nextBoolean()) Color(0xFF81D4FA) else Color(0xFF29B6F6),
                type = ParticleType.WIND_STREAK,
                wobbleFreq = 2.8f
            )
        }
        else -> List(16) {
            CelestialParticle(
                angleOffset = rand.nextFloat() * 360f,
                radiusRatio = rand.nextFloat() * 1.0f + 1.2f,
                speed = 1.0f,
                size = 2f,
                baseAlpha = 0.4f,
                color = planet.color,
                type = ParticleType.ROCKY_DUST
            )
        }
    }
}

/**
 * Draws floating celestial particles around a planet.
 */
private fun DrawScope.drawCelestialParticles(
    particles: List<CelestialParticle>,
    center: Offset,
    planetRadius: Float,
    rotationAngle: Float,
    elapsedTime: Float,
    alphaFactor: Float
) {
    particles.forEach { particle ->
        val currentAngle = ((rotationAngle * particle.speed) + particle.angleOffset) * (PI.toFloat() / 180f)
        val wobble = sin((elapsedTime * particle.wobbleFreq) + particle.angleOffset) * 2.5f
        val distance = (planetRadius * particle.radiusRatio) + wobble

        val px = center.x + distance * cos(currentAngle)
        val py = center.y + distance * sin(currentAngle)
        val finalAlpha = (particle.baseAlpha * alphaFactor).coerceIn(0f, 1f)

        when (particle.type) {
            ParticleType.WIND_STREAK -> {
                // High velocity streak line
                val streakLength = particle.size * 3.2f
                val dx = -sin(currentAngle) * streakLength
                val dy = cos(currentAngle) * streakLength
                drawLine(
                    color = particle.color.copy(alpha = finalAlpha),
                    start = Offset(px, py),
                    end = Offset(px + dx, py + dy),
                    strokeWidth = particle.size * 0.8f,
                    cap = StrokeCap.Round
                )
            }
            ParticleType.GAS_STORM -> {
                // Diffuse gas storm blob
                drawCircle(
                    color = particle.color.copy(alpha = finalAlpha * 0.7f),
                    radius = particle.size * 1.5f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = particle.color.copy(alpha = finalAlpha),
                    radius = particle.size * 0.8f,
                    center = Offset(px, py)
                )
            }
            ParticleType.SOLAR_PLASMA, ParticleType.ICE_CRYSTAL -> {
                // Sparkling core + glow
                drawCircle(
                    color = particle.color.copy(alpha = finalAlpha * 0.4f),
                    radius = particle.size * 1.8f,
                    center = Offset(px, py)
                )
                drawCircle(
                    color = Color.White.copy(alpha = finalAlpha),
                    radius = particle.size * 0.7f,
                    center = Offset(px, py)
                )
            }
            else -> {
                // Standard organic dust speck
                drawCircle(
                    color = particle.color.copy(alpha = finalAlpha),
                    radius = particle.size,
                    center = Offset(px, py)
                )
            }
        }
    }
}

/**
 * Draws Saturn's magnificent tilted rings with realistic foreground/background clipping.
 */
private fun DrawScope.drawSaturnRings(
    center: Offset,
    planetRadius: Float,
    ringColor: Color,
    tiltAngleDeg: Float,
    isFront: Boolean
) {
    rotate(degrees = tiltAngleDeg, pivot = center) {
        val ringWidth = planetRadius * 2.8f
        val ringHeight = planetRadius * 0.85f

        if (!isFront) {
            // Draw background ring (behind planet)
            drawOval(
                color = ringColor.copy(alpha = 0.65f),
                topLeft = Offset(center.x - ringWidth, center.y - ringHeight),
                size = Size(ringWidth * 2f, ringHeight * 2f),
                style = Stroke(width = planetRadius * 0.45f)
            )
            drawOval(
                color = ringColor.copy(alpha = 0.3f),
                topLeft = Offset(center.x - ringWidth * 1.15f, center.y - ringHeight * 1.15f),
                size = Size(ringWidth * 2.3f, ringHeight * 2.3f),
                style = Stroke(width = 1.2f)
            )
        } else {
            // Foreground ring stroke (lower portion overlapping the planet)
            drawArc(
                color = ringColor.copy(alpha = 0.85f),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(center.x - ringWidth, center.y - ringHeight),
                size = Size(ringWidth * 2f, ringHeight * 2f),
                style = Stroke(width = planetRadius * 0.45f, cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Atmospheric bands for Jupiter.
 */
private fun DrawScope.drawJupiterDetails(center: Offset, radius: Float) {
    val bandHeight = radius * 0.28f
    drawRect(
        color = Color(0xFF9E6539).copy(alpha = 0.4f),
        topLeft = Offset(center.x - radius * 0.8f, center.y - bandHeight / 2f),
        size = Size(radius * 1.6f, bandHeight)
    )

    // Great Red Spot
    drawCircle(
        color = Color(0xFFC0392B).copy(alpha = 0.7f),
        radius = radius * 0.22f,
        center = Offset(center.x + radius * 0.28f, center.y + radius * 0.22f)
    )
}

/**
 * Green continental swirl for Earth.
 */
private fun DrawScope.drawEarthDetails(center: Offset, radius: Float) {
    drawCircle(
        color = Color(0xFF27AE60).copy(alpha = 0.6f),
        radius = radius * 0.32f,
        center = Offset(center.x - radius * 0.2f, center.y - radius * 0.15f)
    )
    drawCircle(
        color = Color(0xFF2ECC71).copy(alpha = 0.5f),
        radius = radius * 0.22f,
        center = Offset(center.x + radius * 0.25f, center.y + radius * 0.18f)
    )
}
