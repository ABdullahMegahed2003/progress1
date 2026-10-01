package com.example.ui.screens.auth

import android.app.DatePickerDialog
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TaqadomButton
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymDarkOutline
import com.example.ui.theme.GymDarkSurface
import com.example.ui.theme.GymGold
import com.example.ui.theme.GymGreenAccent
import com.example.ui.theme.GymGreenContainer
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.GymRed
import com.example.ui.theme.GymTextMuted
import com.example.ui.theme.GymTextWhite
import com.example.viewmodel.TaqadomViewModel
import java.util.Calendar

@Composable
fun RegisterScreen(
    viewModel: TaqadomViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }

    // BirthDate & Auto Calculated Age
    var birthDateStr by remember { mutableStateOf("2000-01-01") }
    var calculatedAge by remember { mutableIntStateOf(26) }

    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                birthDateStr = formatted
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val age = (currentYear - year).coerceAtLeast(14)
                calculatedAge = age
            },
            2000, 0, 1
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(GymDarkSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "رجوع",
                        tint = GymGreenPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "إنشاء حساب جديد",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextWhite
                    )
                    Text(
                        text = "انضم إلى أبطال تقدم وابدأ مشوارك",
                        fontSize = 13.sp,
                        color = GymTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GymDarkSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GymDarkOutline))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("الاسم الكامل") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GymGreenPrimary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymGreenPrimary,
                            unfocusedBorderColor = GymDarkOutline,
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite,
                            focusedContainerColor = GymDarkBackground,
                            unfocusedContainerColor = GymDarkBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Birthdate Picker & Age Display
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, GymDarkOutline, RoundedCornerShape(12.dp))
                            .clickable { datePickerDialog.show() },
                        color = GymDarkBackground
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = GymGreenPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "تاريخ الميلاد (اضغط للاختيار)",
                                        fontSize = 12.sp,
                                        color = GymTextMuted
                                    )
                                    Text(
                                        text = birthDateStr,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GymTextWhite
                                    )
                                }
                            }

                            // Auto calculated Age Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GymGreenContainer)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "$calculatedAge سنة",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymGreenAccent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Weight in kg
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it },
                        label = { Text("الوزن الحالي (كجم)") },
                        leadingIcon = { Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = GymGreenPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymGreenPrimary,
                            unfocusedBorderColor = GymDarkOutline,
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite,
                            focusedContainerColor = GymDarkBackground,
                            unfocusedContainerColor = GymDarkBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_weight_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("البريد الإلكتروني") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = GymGreenPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymGreenPrimary,
                            unfocusedBorderColor = GymDarkOutline,
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite,
                            focusedContainerColor = GymDarkBackground,
                            unfocusedContainerColor = GymDarkBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("كلمة المرور (6 أحرف أو أكثر)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GymGreenPrimary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymGreenPrimary,
                            unfocusedBorderColor = GymDarkOutline,
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite,
                            focusedContainerColor = GymDarkBackground,
                            unfocusedContainerColor = GymDarkBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password_input")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    TaqadomButton(
                        text = "إنشاء الحساب وبدء التقدم",
                        onClick = {
                            val weight = weightText.toFloatOrNull() ?: 70f
                            viewModel.register(
                                name = name.trim(),
                                email = email.trim(),
                                pass = password,
                                birthDate = birthDateStr,
                                age = calculatedAge,
                                weight = weight
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
