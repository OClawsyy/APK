package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.ElegantHeaderBtnBg
import com.example.ui.theme.ElegantHeaderBtnBorder
import com.example.ui.theme.ElegantTextSecondary

@Composable
fun PlanetQuickSelector(
    selectedPlanet: Planet?,
    onPlanetSelected: (Planet) -> Unit,
    modifier: Modifier = Modifier
) {
    val allBodies = listOf(SunBody) + SolarSystemData

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("planet_quick_selector"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(allBodies, key = { it.id }) { item ->
            val isSelected = selectedPlanet?.id == item.id
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) ElegantBlueBadgeBg else ElegantHeaderBtnBg,
                label = "chip_bg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) ElegantBlueAccent else ElegantHeaderBtnBorder,
                label = "chip_border"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(100.dp))
                    .clickable { onPlanetSelected(item) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("planet_chip_${item.id}"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color Swatch Dot
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(item.secondaryColor, item.color)
                            )
                        )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isSelected) ElegantBlueBadgeText else ElegantTextSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}
