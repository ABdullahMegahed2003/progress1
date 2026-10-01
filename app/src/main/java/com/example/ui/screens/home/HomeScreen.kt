package com.example.ui.screens.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.SectionHeader
import com.example.ui.components.TaqadomButton
import com.example.ui.theme.GymBlue
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymDarkOutline
import com.example.ui.theme.GymDarkSurface
import com.example.ui.theme.GymDarkSurfaceVariant
import com.example.ui.theme.GymGold
import com.example.ui.theme.GymGreenAccent
import com.example.ui.theme.GymGreenContainer
import com.example.ui.theme.GymGreenDark
import com.example.ui.theme.GymGreenLightBg
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.GymPurple
import com.example.ui.theme.GymRed
import com.example.ui.theme.GymTextMuted
import com.example.ui.theme.GymTextWhite
import com.example.viewmodel.TaqadomViewModel

@Composable
fun HomeScreen(
    viewModel: TaqadomViewModel,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToDailyLog: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToPlanSetup: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val dateDisplay by viewModel.dateDisplayArabic.collectAsState()
    val workoutSets by viewModel.workoutSets.collectAsState()
    val session by viewModel.workoutSession.collectAsState()
    val dailyLog by viewModel.dailyLog.collectAsState()
    val weeklyMetric by viewModel.weeklyMetric.collectAsState()

    // Calculations
    val completedSetsCount = workoutSets.count { it.isCompleted }
    val totalSetsCount = workoutSets.size
    val completionRatio = if (totalSetsCount > 0) completedSetsCount.toFloat() / totalSetsCount else 0f

    // Readiness score based on sleep & energy
    val sleepHrs = dailyLog?.sleepHours ?: 7.5f
    val energy = dailyLog?.energyLevel ?: 8
    val readinessScore = (((sleepHrs / 8f).coerceAtMost(1f) * 0.5f) + ((energy / 10f) * 0.5f)) * 100f

    val splitNameArabic = when (user?.plan ?: "push_pull_legs") {
        "push_pull_legs" -> "Push Pull Legs (دفع / سحب / أرجل)"
        "arnold_split" -> "Arnold Split (أرنولد كلاسيك)"
        "upper_lower" -> "Upper / Lower (علوي / سفلي)"
        else -> "Full Body (جسم كامل)"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Hero Top Banner with Greeting
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_gym_banner_1790861384137),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                GymDarkBackground.copy(alpha = 0.4f),
                                GymDarkBackground.copy(alpha = 0.85f),
                                GymDarkBackground
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مرحباً يا بطل، ${user?.name?.ifEmpty { "الرياضي" } ?: "الرياضي"}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = GymTextWhite
                        )
                        Text(
                            text = dateDisplay,
                            fontSize = 13.sp,
                            color = GymGreenAccent,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Readiness score badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GymGreenContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GymGreenPrimary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = GymGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "جاهزية: ${readinessScore.toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymGreenAccent
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Spacer(modifier = Modifier.height(8.dp))

            // Today's Workout Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onNavigateToWorkouts() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GymDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GymGreenDark, GymDarkOutline))
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GymGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = GymGreenPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "تمرين اليوم",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymTextWhite
                                )
                                Text(
                                    text = when (session?.status) {
                                        "completed" -> "تم إنهاء تمرين اليوم بالكامل ✅"
                                        "rest" -> "يوم راحة واستشفاء 🧘"
                                        "skipped" -> "تمرين لم يتم"
                                        else -> if (totalSetsCount > 0) "قيد الإنجاز ($completedSetsCount/$totalSetsCount مجموعة)" else "اضغط لتسجيل أول مجموعة"
                                    },
                                    fontSize = 12.sp,
                                    color = if (session?.status == "completed") GymGreenAccent else GymTextMuted
                                )
                            }
                        }

                        // Circular progress indicator
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { completionRatio },
                                modifier = Modifier.size(46.dp),
                                color = GymGreenPrimary,
                                trackColor = GymDarkSurfaceVariant,
                                strokeWidth = 4.dp
                            )
                            Text(
                                text = "${(completionRatio * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymTextWhite
                            )
                        }
                    }

                    if (workoutSets.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(GymDarkBackground)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val distinctExercises = workoutSets.map { it.exerciseName }.distinct()
                            Text(
                                text = "التمارين: ${distinctExercises.take(2).joinToString(" • ")}${if (distinctExercises.size > 2) " +${distinctExercises.size - 2}" else ""}",
                                fontSize = 12.sp,
                                color = GymTextMuted
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = GymGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStatCard(
                    title = "حجم الأسبوع",
                    value = "${weeklyMetric?.totalVolumeKg?.toInt() ?: 0} كجم",
                    subtitle = "إجمالي الأوزان المرفوعة",
                    icon = Icons.Default.TrendingUp,
                    accentColor = GymGreenPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToProgress
                )
                QuickStatCard(
                    title = "نوم البارحة",
                    value = "${sleepHrs} س",
                    subtitle = "طاقة اليوم: $energy/10",
                    icon = Icons.Default.Bedtime,
                    accentColor = GymBlue,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDailyLog
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Shortcuts Grid
            SectionHeader(
                title = "اختصارات سريعة",
                subtitle = "الوصول المباشر لإدارة تمرينك"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShortcutButton(
                    title = "سجل تمرينك",
                    subtitle = "تسجيل الأوزان",
                    icon = Icons.Default.PlayArrow,
                    color = GymGreenPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWorkouts
                )
                ShortcutButton(
                    title = "سجل يومك",
                    subtitle = "النوم والوجبات",
                    icon = Icons.Default.Restaurant,
                    color = GymGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToDailyLog
                )
                ShortcutButton(
                    title = "نظامك",
                    subtitle = "تخصيص الجدول",
                    icon = Icons.Default.SportsGymnastics,
                    color = GymPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPlanSetup
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Athletes Progression Slider
            val athletesProgress by viewModel.athletesProgress.collectAsState()
            com.example.ui.components.AthletesProgressionSlider(
                athletes = athletesProgress,
                onCardClick = { onNavigateToProgress() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Active Plan Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "نظامك الحالي:",
                            fontSize = 12.sp,
                            color = GymTextMuted
                        )
                        Text(
                            text = splitNameArabic,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GymTextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GymGreenLightBg)
                            .clickable { onNavigateToPlanSetup() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "تعديل الجدول",
                            color = GymGreenAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GymDarkOutline))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = GymTextMuted)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = GymTextWhite
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = GymTextMuted
            )
        }
    }
}

@Composable
private fun ShortcutButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GymDarkOutline))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GymTextWhite
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = GymTextMuted
            )
        }
    }
}
