package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AthleteProgressItem
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymDarkOutline
import com.example.ui.theme.GymDarkSurface
import com.example.ui.theme.GymDarkSurfaceVariant
import com.example.ui.theme.GymGold
import com.example.ui.theme.GymGreenAccent
import com.example.ui.theme.GymGreenContainer
import com.example.ui.theme.GymGreenDark
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.GymPurple
import com.example.ui.theme.GymRed
import com.example.ui.theme.GymTextMuted
import com.example.ui.theme.GymTextWhite

@Composable
fun AthletesProgressionSlider(
    athletes: List<AthleteProgressItem>,
    modifier: Modifier = Modifier,
    onCardClick: ((AthleteProgressItem) -> Unit)? = null
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filters = listOf(
        "الأعلى تطوراً 🚀",
        "الأكثر التزاماً 🔥",
        "أبطال الحجم 🏋️"
    )

    val sortedList = remember(athletes, selectedFilterIndex) {
        when (selectedFilterIndex) {
            0 -> athletes.sortedByDescending { it.progressPercent }
            1 -> athletes.sortedByDescending { it.streakDays }
            2 -> athletes.sortedByDescending { it.volumeKg }
            else -> athletes
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = "أبطال تقدم حسب التطور",
            subtitle = "سلايدر يعرض تصنيف المتدربين وفق معدل التطور والإنجاز"
        )

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEachIndexed { index, title ->
                val isSelected = selectedFilterIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) GymGreenPrimary else GymDarkSurface)
                        .border(
                            1.dp,
                            if (isSelected) GymGreenAccent else GymDarkOutline,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedFilterIndex = index }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) GymDarkBackground else GymTextWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Carousel / Slider of Athletes
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            itemsIndexed(sortedList) { index, athlete ->
                AthleteCardItem(
                    athlete = athlete,
                    displayRank = index + 1,
                    onClick = { onCardClick?.invoke(athlete) }
                )
            }
        }
    }
}

@Composable
private fun AthleteCardItem(
    athlete: AthleteProgressItem,
    displayRank: Int,
    onClick: () -> Unit
) {
    val rankColor = when (displayRank) {
        1 -> GymGold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> GymGreenAccent
    }

    val rankIconText = when (displayRank) {
        1 -> "#1 🥇"
        2 -> "#2 🥈"
        3 -> "#3 🥉"
        else -> "#$displayRank"
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (athlete.isCurrentUser) GymGreenContainer else GymDarkSurface
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                if (athlete.isCurrentUser) listOf(GymGreenPrimary, GymGold)
                else listOf(GymDarkOutline, rankColor.copy(alpha = 0.5f))
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Rank badge & "أنت" indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(rankColor.copy(alpha = 0.2f))
                        .border(1.dp, rankColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = rankIconText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = rankColor
                    )
                }

                if (athlete.isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GymGreenPrimary)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "أنت ⭐",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = GymDarkBackground
                        )
                    }
                } else {
                    Text(
                        text = athlete.planName,
                        fontSize = 11.sp,
                        color = GymTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Athlete Info Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GymDarkSurfaceVariant)
                        .border(2.dp, if (athlete.isCurrentUser) GymGreenPrimary else rankColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = athlete.name.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GymTextWhite
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = athlete.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextWhite
                    )
                    Text(
                        text = athlete.levelTitle,
                        fontSize = 11.sp,
                        color = GymGreenAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Growth Metric Banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp)),
                color = GymDarkBackground.copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = GymGreenPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "نسبة التطور:",
                            fontSize = 12.sp,
                            color = GymTextMuted
                        )
                    }

                    Text(
                        text = "+${String.format("%.1f", athlete.progressPercent)}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GymGreenPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mini stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${athlete.volumeKg.toInt()} كجم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GymTextWhite
                    )
                    Text(text = "حجم الأوزان", fontSize = 10.sp, color = GymTextMuted)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${athlete.workoutsCompleted} تمرين",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GymTextWhite
                    )
                    Text(text = "الجلسات", fontSize = 10.sp, color = GymTextMuted)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${athlete.streakDays} يوم",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GymGold
                    )
                    Text(text = "استمرارية", fontSize = 10.sp, color = GymTextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tier Progress Bar
            val tierProgress = (athlete.progressPercent / 40f).coerceIn(0.1f, 1f)
            LinearProgressIndicator(
                progress = { tierProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GymGreenPrimary,
                trackColor = GymDarkSurfaceVariant
            )
        }
    }
}
