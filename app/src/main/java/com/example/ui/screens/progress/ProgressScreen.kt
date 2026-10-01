package com.example.ui.screens.progress

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ComparisonResult
import com.example.data.model.FactorCorrelation
import com.example.data.model.LowestDayInsight
import com.example.data.model.ProgressMetric
import com.example.ui.components.ExerciseProgressionChart
import com.example.ui.components.SectionHeader
import com.example.ui.theme.GymBlue
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
import com.example.viewmodel.TaqadomViewModel

@Composable
fun ProgressScreen(viewModel: TaqadomViewModel) {
    val weeklyMetric by viewModel.weeklyMetric.collectAsState()
    val weeklyComparison by viewModel.weeklyComparison.collectAsState()
    val monthlyComparison by viewModel.monthlyComparison.collectAsState()
    val athletesProgress by viewModel.athletesProgress.collectAsState()
    val lowestDay by viewModel.lowestDay.collectAsState()
    val selectedExercise by viewModel.selectedExerciseForChart.collectAsState()
    val exerciseHistory by viewModel.exerciseHistory.collectAsState()
    val correlations = viewModel.factorCorrelations

    val popularExercises = listOf(
        "بنش برس مستوي بالبار",
        "سكوات خلفي بالبار",
        "ديدليفت تقليدي بالبار",
        "ضغط كتف عسكري واقف بالبار",
        "سحب ظهر عالي بالجهاز",
        "تبادل بايسبس بالبار المتعرج"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "التقدم والتحليلات الرياضية",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = GymTextWhite
            )
            Text(
                text = "متابعة الحجم التدريبي وتأثير العوامل اليومية على أدائك",
                fontSize = 13.sp,
                color = GymTextMuted,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Top Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "الحجم التدريبي",
                    value = "${weeklyMetric?.totalVolumeKg?.toInt() ?: 0} كجم",
                    subtitle = "الوزن × العدات × المجموعات",
                    color = GymGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "إجمالي المجموعات",
                    value = "${weeklyMetric?.totalSets ?: 0}",
                    subtitle = "تكرارات: ${weeklyMetric?.totalReps ?: 0}",
                    color = GymGold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Comparison Cards (Week over Week & Month over Month)
        item {
            SectionHeader(
                title = "المقارنات الزمنية",
                subtitle = "مقارنة حجم التمارين بين الفترات"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ComparisonCard(
                    periodTitle = "الأسبوع الحالي vs السابق",
                    currentVal = "${weeklyComparison?.currentPeriodVolume?.toInt() ?: 0} كجم",
                    prevVal = "${weeklyComparison?.previousPeriodVolume?.toInt() ?: 0} كجم",
                    changePercent = weeklyComparison?.percentageChange ?: 0f,
                    modifier = Modifier.weight(1f)
                )

                ComparisonCard(
                    periodTitle = "الشهر الحالي vs السابق",
                    currentVal = "${monthlyComparison?.currentPeriodVolume?.toInt() ?: 0} كجم",
                    prevVal = "${monthlyComparison?.previousPeriodVolume?.toInt() ?: 0} كجم",
                    changePercent = monthlyComparison?.percentageChange ?: 0f,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Lowest Performing Day Insight
        item {
            lowestDay?.let { insight ->
                SectionHeader(
                    title = "تحليل أقل يوم أداءً",
                    subtitle = "رصد أسباب التراجع لتفاديها مستقبلاً"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GymDarkSurface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(GymRed.copy(alpha = 0.5f), GymDarkOutline))
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(GymRed.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = GymRed, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = insight.dayNameArabic,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = GymTextWhite
                                )
                            }
                            Text(
                                text = "${insight.volumeKg.toInt()} كجم فقط",
                                color = GymRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = insight.reasonAnalysis,
                            fontSize = 12.sp,
                            color = GymTextMuted,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Exercise Progression Chart Section
        item {
            SectionHeader(
                title = "تطور القوة حسب كل تمرين",
                subtitle = "منحنى زيادة الأوزان والتكرارات عبر الزمن"
            )

            // Exercise Selector chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularExercises) { exName ->
                    val isSel = selectedExercise == exName
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) GymGreenPrimary else GymDarkSurface)
                            .clickable { viewModel.selectExerciseForChart(exName) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = exName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) GymDarkBackground else GymTextWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ExerciseProgressionChart(
                points = exerciseHistory,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Athletes Progression Slider
            com.example.ui.components.AthletesProgressionSlider(
                athletes = athletesProgress
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Monthly Correlation Analysis
        item {
            SectionHeader(
                title = "التحليل الشهري للعوامل المؤثرة",
                subtitle = "ارتباط التطور بالنوم، الطاقة، الوجبات، والمجهود"
            )
        }

        items(correlations) { factor ->
            FactorCorrelationCard(factor = factor, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 12.sp, color = GymTextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = GymTextMuted)
        }
    }
}

@Composable
private fun ComparisonCard(
    periodTitle: String,
    currentVal: String,
    prevVal: String,
    changePercent: Float,
    modifier: Modifier = Modifier
) {
    val isPositive = changePercent >= 0
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = periodTitle, fontSize = 11.sp, color = GymTextMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isPositive) GymGreenPrimary else GymRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format("%.1f", changePercent)}%",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = if (isPositive) GymGreenPrimary else GymRed
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "الحالي: $currentVal", fontSize = 11.sp, color = GymTextWhite)
            Text(text = "السابق: $prevVal", fontSize = 10.sp, color = GymTextMuted)
        }
    }
}

@Composable
private fun FactorCorrelationCard(
    factor: FactorCorrelation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = factor.factorName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GymTextWhite
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GymGreenContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = factor.impactScore,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymGreenAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = factor.description,
                fontSize = 12.sp,
                color = GymTextMuted,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "المعدل المسجل: ${factor.averageValue}",
                fontSize = 11.sp,
                color = GymGold,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
