package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Planet
import com.example.ui.theme.ElegantBlueAccent
import com.example.ui.theme.ElegantBlueBadgeBg
import com.example.ui.theme.ElegantBlueBadgeText
import com.example.ui.theme.ElegantBlueLight
import com.example.ui.theme.ElegantCardBg
import com.example.ui.theme.ElegantCardBorder
import com.example.ui.theme.ElegantHandle
import com.example.ui.theme.ElegantInsightBg
import com.example.ui.theme.ElegantInsightBorder
import com.example.ui.theme.ElegantInsightGold
import com.example.ui.theme.ElegantInsightText
import com.example.ui.theme.ElegantSurface
import com.example.ui.theme.ElegantTextMuted
import com.example.ui.theme.ElegantTextPrimary
import com.example.ui.theme.ElegantTextSecondary
import com.example.ui.theme.ElegantTextWhite

@Composable
fun PlanetInfoSheet(
    planet: Planet?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("planet_info_sheet")
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                spotColor = Color.Black
            ),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(
            containerColor = ElegantSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Elegant Drag handle: w-12 h-1.5 bg-[#44474E] rounded-full mx-auto mb-6
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 6.dp)
                    .clip(CircleShape)
                    .background(ElegantHandle)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedContent(
                targetState = planet,
                transitionSpec = {
                    fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                            slideInVertically(
                                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                initialOffsetY = { 35 }
                            ) togetherWith fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                },
                label = "planet_sheet_content"
            ) { currentPlanet ->
                if (currentPlanet == null) {
                    PlaceholderView()
                } else {
                    PlanetDetailContent(planet = currentPlanet, onDismiss = onDismiss)
                }
            }
        }
    }
}

@Composable
private fun PlaceholderView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .testTag("placeholder_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.04f))
                .border(1.dp, ElegantCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = "Ketuk planet",
                tint = ElegantBlueLight,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Planetarium Interaktif",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = ElegantTextWhite,
                fontSize = 17.sp,
                letterSpacing = (-0.3).sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ketuk salah satu planet di atas buat lihat kandungan, deskripsi, dan masa orbitnya.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = ElegantTextMuted,
                lineHeight = 19.sp,
                fontSize = 13.sp
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun PlanetDetailContent(
    planet: Planet,
    onDismiss: () -> Unit
) {
    var easterEggTapCount by remember(planet.id) { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("planet_detail_content")
    ) {
        // Header row: Planet name & tagline on left, selected pill & close button on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Miniature planet glow avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    planet.secondaryColor,
                                    planet.color,
                                    Color(0xFF090D18)
                                )
                            )
                        )
                        .border(1.5.dp, planet.color.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (planet.hasRings) {
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 10.dp)
                                .border(1.2.dp, planet.ringColor, RoundedCornerShape(5.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (planet.isEarth) "Bumi (Earth)" else planet.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ElegantTextWhite,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            modifier = Modifier.testTag("planet_name_title")
                        )
                    }

                    val subtitleText = if (planet.isEarth) "Zona Layak Huni" else planet.tagline.ifEmpty { "Objek Tata Surya" }
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ElegantBlueLight,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Elegant "Selected" pill badge: px-3 py-1 bg-[#4F86F7]/20 text-[#D2E3FC] text-[10px] uppercase font-bold rounded-full border border-[#4F86F7]/30 tracking-wider
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(ElegantBlueBadgeBg)
                        .border(1.dp, ElegantBlueAccent.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SELECTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ElegantBlueBadgeText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.2.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .testTag("close_info_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup Info",
                        tint = ElegantTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Description text: text-[#C4C6D0] text-sm leading-relaxed mb-6
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

        Spacer(modifier = Modifier.height(18.dp))

        // Grid of Stat Cards: bg-[#2B2D33] p-4 rounded-2xl border border-[#3E4249]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stat 1: Masa Orbit
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
                            letterSpacing = 1.4.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = planet.period,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextWhite,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            // Stat 2: Kecepatan Orbit Animasi
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .testTag("stat_card_speed"),
                shape = RoundedCornerShape(16.dp),
                color = ElegantCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (planet.isSun) "ROTASI" else "KECEPATAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ElegantTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.4.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (planet.dur > 0) "${planet.dur.toInt()}s / orbit" else "Pusat tata surya",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = ElegantTextWhite,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stat 3: Kandungan Planet Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("stat_card_comp"),
            shape = RoundedCornerShape(16.dp),
            color = ElegantCardBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "KANDUNGAN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElegantTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.4.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = planet.comp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = ElegantTextWhite,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Special Section for Earth (Bumi): Elegant Milky Way Insight Card & Easter Egg
        if (planet.isEarth) {
            Spacer(modifier = Modifier.height(12.dp))

            // Elegant Dark insight card: bg-[#3D2F1D] border border-[#6B4F27] p-4 rounded-2xl mb-2 flex items-start gap-3
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("easter_egg_card")
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        easterEggTapCount++
                    },
                shape = RoundedCornerShape(16.dp),
                color = ElegantInsightBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantInsightBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Circular icon badge: w-8 h-8 rounded-full bg-[#6B4F27] flex items-center justify-center
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElegantInsightBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = ElegantInsightGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Milky Way Insight",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ElegantInsightGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )

                            if (easterEggTapCount > 0) {
                                Text(
                                    text = "Ketuk: $easterEggTapCount x",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElegantInsightGold.copy(alpha = 0.8f),
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\"${planet.milkyWayInfo} ${planet.easterEggBody}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ElegantInsightText,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontStyle = FontStyle.Italic
                            )
                        )

                        if (easterEggTapCount >= 3) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Akses Khusus: Observatorium dan catatan astronomi galaksi terbuka.",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ElegantInsightGold,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
