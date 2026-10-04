package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.TaqadomButton
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymDarkOutline
import com.example.ui.theme.GymDarkSurface
import com.example.ui.theme.GymGreenAccent
import com.example.ui.theme.GymGreenContainer
import com.example.ui.theme.GymGreenDark
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.GymTextMuted
import com.example.ui.theme.GymTextWhite
import com.example.viewmodel.TaqadomViewModel

data class OnboardingStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badgeText: String
)

@Composable
fun OnboardingScreen(viewModel: TaqadomViewModel) {
    val steps = listOf(
        OnboardingStep(
            title = "تابع أداءك بدقة في الجيم",
            description = "سجّل كل جلسة تدريبية ومجموعاتك وعداتك، وشاهد معدل التزامك الأسبوعي وإنجازك خطوة بخطوة.",
            icon = Icons.Default.FitnessCenter,
            badgeText = "الأداء والتمرين"
        ),
        OnboardingStep(
            title = "سجّل أوزانك وتكراراتك لحظياً",
            description = "تحكم في أوزان كل مجموعة، مؤقت الراحة التلقائي بين المجموعات لحرق دهون وبناء عضلي فائق.",
            icon = Icons.Default.TrendingUp,
            badgeText = "الأوزان والحجم التدريبي"
        ),
        OnboardingStep(
            title = "سجّل يومك (نومك، طاقتك، وجباتك)",
            description = "اربط بين ساعات نومك ومستوى طاقتك ووجباتك وبين تطور قوتك وأوزانك في صالة الحديد.",
            icon = Icons.Default.Bedtime,
            badgeText = "أسلوب الحياة والاستشفاء"
        ),
        OnboardingStep(
            title = "اختر نظامك التدريبي المناسب",
            description = "نظام Push Pull Legs، أو Arnold Split، أو Upper/Lower، أو Full Body مع تمارين مترجمة بالكامل.",
            icon = Icons.Default.FormatListNumbered,
            badgeText = "الأنظمة التدريبية"
        )
    )

    var currentStepIndex by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step progress indicators
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == currentStepIndex) 28.dp else 12.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    if (index == currentStepIndex) GymGreenPrimary
                                    else GymDarkOutline
                                )
                        )
                    }
                }

                TextButton(onClick = { viewModel.completeOnboarding() }) {
                    Text(
                        text = "تخطي",
                        color = GymTextMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Center Illustration & Content
            AnimatedContent(
                targetState = currentStepIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_step"
            ) { index ->
                val step = steps[index]
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (index == 0) {
                        // Visual generated artwork
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_fitness_onboard_1790861396833),
                                contentDescription = "تطبيق تقدم",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Animated Hero Icon Box
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(GymGreenContainer, GymDarkSurface)
                                    )
                                )
                                .border(2.dp, GymGreenPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = GymGreenPrimary,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GymGreenContainer)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = step.badgeText,
                            color = GymGreenAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = step.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GymTextWhite,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = step.description,
                        fontSize = 15.sp,
                        color = GymTextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom Actions
            Column(modifier = Modifier.fillMaxWidth()) {
                TaqadomButton(
                    text = if (currentStepIndex == steps.size - 1) "ابدأ الآن" else "التالي",
                    onClick = {
                        if (currentStepIndex < steps.size - 1) {
                            currentStepIndex++
                        } else {
                            viewModel.completeOnboarding()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
