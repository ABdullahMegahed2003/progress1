package com.example.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.SectionHeader
import com.example.ui.components.TaqadomButton
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymDarkOutline
import com.example.ui.theme.GymDarkSurface
import com.example.ui.theme.GymDarkSurfaceVariant
import com.example.ui.theme.GymGold
import com.example.ui.theme.GymGreenAccent
import com.example.ui.theme.GymGreenContainer
import com.example.ui.theme.GymGreenDark
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.GymRed
import com.example.ui.theme.GymTextMuted
import com.example.ui.theme.GymTextWhite
import com.example.viewmodel.TaqadomViewModel

@Composable
fun ProfileScreen(
    viewModel: TaqadomViewModel,
    onNavigateToPlanSetup: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val weeklyMetric by viewModel.weeklyMetric.collectAsState()
    val isUploading by viewModel.isUploadingImage.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Android PhotoPicker (Google Play compliant, zero broad permissions)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadProfileAvatar(uri)
        }
    }

    val planTitle = when (user?.plan ?: "push_pull_legs") {
        "push_pull_legs" -> "Push Pull Legs (دفع / سحب / أرجل)"
        "arnold_split" -> "Arnold Split (صدر وظهر / كتاف وذراعين)"
        "upper_lower" -> "Upper / Lower (علوي / سفلي)"
        else -> "Full Body (جسم كامل)"
    }

    // Weekly accomplishment calculation
    val weeklyRate = if ((weeklyMetric?.completedWorkoutsCount ?: 0) >= 4) 100
    else ((weeklyMetric?.completedWorkoutsCount ?: 0) * 25).coerceIn(0, 100)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "الملف الرياضي وحسابي",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = GymTextWhite
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar with Cloudinary badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(GymGreenContainer)
                        .border(3.dp, GymGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.profileImageUrl.isNullOrEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GymGreenPrimary,
                            modifier = Modifier.size(60.dp)
                        )
                    } else {
                        AsyncImage(
                            model = user?.profileImageUrl,
                            contentDescription = "صورة الحساب الشخصي",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(GymDarkBackground.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = GymGreenPrimary
                            )
                        }
                    }
                }

                // Camera button for photo upload
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GymGreenPrimary)
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "تغيير الصورة الشخصية",
                        tint = GymDarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = user?.name?.ifEmpty { "بطل تقدم" } ?: "بطل تقدم",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = GymTextWhite
            )

            Text(
                text = user?.email.orEmpty(),
                fontSize = 13.sp,
                color = GymTextMuted
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Weekly Achievement Badge Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GymGreenContainer),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = Brush.horizontalGradient(listOf(GymGreenPrimary, GymGreenDark))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(GymGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = GymDarkBackground,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "نسبة إنجاز الأسبوع",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GymTextWhite
                            )
                            Text(
                                text = "${weeklyMetric?.completedWorkoutsCount ?: 0} جلسات تمرين مكتملة",
                                fontSize = 12.sp,
                                color = GymGreenAccent
                            )
                        }
                    }

                    Text(
                        text = "$weeklyRate%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GymGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Athlete Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "البيانات البدنية")

                    StatRow(label = "السن المحسوب:", value = "${user?.age ?: 24} سنة")
                    StatRow(label = "تاريخ الميلاد:", value = user?.birthDate?.ifEmpty { "2000-01-01" } ?: "2000-01-01")
                    StatRow(label = "الوزن المسجل:", value = "${user?.weight ?: 75f} كجم")
                    StatRow(label = "الطول:", value = "${user?.height?.toInt() ?: 175} سم")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Training Split Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onNavigateToPlanSetup() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GymDarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsGymnastics,
                                contentDescription = null,
                                tint = GymGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "النظام التدريبي",
                                fontSize = 12.sp,
                                color = GymTextMuted
                            )
                            Text(
                                text = planTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymTextWhite
                            )
                        }
                    }

                    Text(
                        text = "تعديل",
                        color = GymGreenAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, GymRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable { showLogoutDialog = true }
                    .testTag("logout_button"),
                color = GymDarkSurface
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = GymRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تسجيل الخروج",
                        color = GymRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("تسجيل الخروج", fontWeight = FontWeight.Bold, color = GymTextWhite) },
                text = { Text("هل تود بالتأكيد تسجيل الخروج من حسابك في تطبيق تقدم؟", color = GymTextMuted) },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    }) {
                        Text("نعم، خروج", color = GymRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("إلغاء", color = GymTextWhite)
                    }
                },
                containerColor = GymDarkSurface
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = GymTextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GymTextWhite)
    }
}
