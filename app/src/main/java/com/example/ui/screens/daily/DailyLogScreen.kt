package com.example.ui.screens.daily

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.MealItem
import com.example.ui.components.SectionHeader
import com.example.ui.components.TaqadomButton
import com.example.ui.components.TaqadomOutlinedButton
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
fun DailyLogScreen(viewModel: TaqadomViewModel) {
    val dateDisplay by viewModel.dateDisplayArabic.collectAsState()
    val dailyLog by viewModel.dailyLog.collectAsState()
    val dailyMeals by viewModel.dailyMeals.collectAsState()
    val workoutSets by viewModel.workoutSets.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAddMealDialog by remember { mutableStateOf(false) }

    // Editable state
    var sleepHours by remember(dailyLog) { mutableFloatStateOf(dailyLog?.sleepHours ?: 7.5f) }
    var sleepQuality by remember(dailyLog) { mutableIntStateOf(dailyLog?.sleepQuality ?: 4) }
    var energyLevel by remember(dailyLog) { mutableIntStateOf(dailyLog?.energyLevel ?: 8) }
    var workHours by remember(dailyLog) { mutableFloatStateOf(dailyLog?.workHours ?: 8f) }
    var workExertion by remember(dailyLog) { mutableStateOf(dailyLog?.workExertion ?: "متوسط") }
    var workoutTime by remember(dailyLog) { mutableStateOf(dailyLog?.workoutTime ?: "05:00 م") }
    var notes by remember(dailyLog) { mutableStateOf(dailyLog?.notes ?: "") }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Workout summary for today
    val completedSets = workoutSets.count { it.isCompleted }
    val totalVolume = workoutSets.filter { it.isCompleted }.sumOf { (it.weight * it.reps).toDouble() }.toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp)
        ) {
            // Header with Date Switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GymDarkSurface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.changeDayBy(1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GymDarkSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "اليوم التالي",
                            tint = GymGreenPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سجل يومك",
                            fontSize = 18.sp,
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

                    IconButton(
                        onClick = { viewModel.changeDayBy(-1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GymDarkSurfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "اليوم السابق",
                            tint = GymGreenPrimary
                        )
                    }
                }
            }

            // Cross-Analysis Insight Card
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GymGreenContainer),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(GymGreenPrimary, GymGreenDark))
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GymGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = GymDarkBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "تحليل تأثير اليوم على أدائك في الجيم:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GymGreenAccent
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when {
                                        sleepHours >= 7.5f && energyLevel >= 7 ->
                                            "نومك ممتاز (${sleepHours}س) ومستوى طاقتك عالٍ ($energyLevel/10)! هذا يفسر إكمال $completedSets مجموعات بحجم تدريبي $totalVolume كجم."
                                        sleepHours < 6.5f ->
                                            "نومك أقل من 6.5 ساعات. قد تشعر بثقل الأوزان وصعوبة في الاستشفاء العصبي؛ احرص على القيلولة أو زيادة الكارب."
                                        workExertion == "شاق" ->
                                            "مجهود العمل الشاق قد يستنزف طاقتك؛ ركّز على وجبة غنية بالبروتين والمغنيسيوم قبل النوم."
                                        else ->
                                            "بيانات يومك متوازنة وتدعم استمرارية تقدمك الرياضي وبناء العضلات."
                                    },
                                    fontSize = 12.sp,
                                    color = GymTextWhite,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Sleep Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Bedtime, contentDescription = null, tint = GymBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ساعات النوم", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GymTextWhite)
                            }
                            Text(
                                text = "${String.format("%.1f", sleepHours)} ساعة",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = GymBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = sleepHours,
                            onValueChange = {
                                sleepHours = it
                                viewModel.updateSleepAndEnergy(it, energyLevel, sleepQuality)
                            },
                            valueRange = 3f..12f,
                            steps = 17, // 0.5 steps
                            colors = SliderDefaults.colors(
                                thumbColor = GymBlue,
                                activeTrackColor = GymBlue,
                                inactiveTrackColor = GymDarkSurfaceVariant
                            )
                        )

                        // Sleep Quality rating
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("جودة النوم والراحة:", fontSize = 12.sp, color = GymTextMuted)
                            Row {
                                for (star in 1..5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (star <= sleepQuality) GymGold else GymDarkOutline,
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clickable {
                                                sleepQuality = star
                                                viewModel.updateSleepAndEnergy(sleepHours, energyLevel, star)
                                            }
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Energy Level Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = GymGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("مستوى الطاقة والنشاط", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GymTextWhite)
                            }
                            Text(
                                text = "$energyLevel / 10",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = GymGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1 to 10 selector buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (lvl in 1..10) {
                                val isSel = energyLevel == lvl
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (isSel) GymGold else GymDarkSurfaceVariant)
                                        .clickable {
                                            energyLevel = lvl
                                            viewModel.updateSleepAndEnergy(sleepHours, lvl, sleepQuality)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$lvl",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) GymDarkBackground else GymTextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Work & Exertion Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Work, contentDescription = null, tint = GymPurple)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ساعات العمل والمجهود", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GymTextWhite)
                            }
                            Text(
                                text = "${workHours.toInt()} ساعات",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GymTextWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Slider(
                            value = workHours,
                            onValueChange = { workHours = it },
                            valueRange = 0f..16f,
                            steps = 15,
                            colors = SliderDefaults.colors(
                                thumbColor = GymPurple,
                                activeTrackColor = GymPurple,
                                inactiveTrackColor = GymDarkSurfaceVariant
                            )
                        )

                        Text("مستوى الإجهاد البدني بالعمل:", fontSize = 12.sp, color = GymTextMuted)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("خفيف", "متوسط", "شاق").forEach { exert ->
                                val isSel = workExertion == exert
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) GymPurple else GymDarkSurfaceVariant)
                                        .clickable { workExertion = exert }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = exert,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) GymDarkBackground else GymTextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Meals Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    SectionHeader(
                        title = "الوجبات الغذائية بالترتيب",
                        subtitle = "${dailyMeals.size} وجبات مسجلة اليوم",
                        actionText = "+ إضافة وجبة",
                        onActionClick = { showAddMealDialog = true }
                    )
                }
            }

            if (dailyMeals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GymDarkSurface)
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لم تسجل وجبات اليوم بعد. انقر على '+ إضافة وجبة' لتوثيق تغذيتك",
                            fontSize = 12.sp,
                            color = GymTextMuted
                        )
                    }
                }
            } else {
                items(dailyMeals, key = { it.id }) { meal ->
                    MealRowCard(
                        meal = meal,
                        onDelete = { viewModel.deleteMeal(meal.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // Workout Time & Notes
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "وقت التمرين وملاحظات اليوم")

                    OutlinedTextField(
                        value = workoutTime,
                        onValueChange = { workoutTime = it },
                        label = { Text("وقت بدء التمرين") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymGreenPrimary,
                            unfocusedBorderColor = GymDarkOutline,
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظاتك عن أداء اليوم والمشاعر") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GymGreenPrimary,
                            unfocusedBorderColor = GymDarkOutline,
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TaqadomButton(
                        text = "حفظ ومزامنة سجل اليوم",
                        onClick = {
                            viewModel.updateWorkAndNotes(workHours, workExertion, workoutTime, notes)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Add Meal Dialog
        if (showAddMealDialog) {
            AddMealDialog(
                onDismiss = { showAddMealDialog = false },
                onAdd = { title, time, content, cal ->
                    viewModel.addMeal(title, time, content, cal)
                    showAddMealDialog = false
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MealRowCard(
    meal: MealItem,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(GymGreenContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = GymGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = meal.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GymTextWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = meal.time, fontSize = 11.sp, color = GymGreenAccent)
                    }
                    Text(text = meal.content, fontSize = 12.sp, color = GymTextMuted)
                    if (meal.calories > 0) {
                        Text(text = "${meal.calories} سعرة حرارية", fontSize = 10.sp, color = GymGold)
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف الوجبة",
                    tint = GymTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AddMealDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, Int) -> Unit
) {
    var title by remember { mutableStateOf("وجبة بعد التمرين") }
    var time by remember { mutableStateOf("07:30 م") }
    var content by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("600") }

    val presets = listOf("وجبة إفطار", "غداء", "وجبة قبل التمرين", "وجبة بعد التمرين", "عشاء", "سناك بروتين")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إضافة وجبة غذائية", fontWeight = FontWeight.Bold, color = GymTextWhite, fontSize = 16.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Presets row
                Text("نوع الوجبة:", fontSize = 12.sp, color = GymTextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    presets.chunked(3).forEach { rowList ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            rowList.forEach { p ->
                                val isSel = title == p
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) GymGreenPrimary else GymDarkSurfaceVariant)
                                        .clickable { title = p }
                                        .padding(vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = p,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) GymDarkBackground else GymTextWhite
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("وقت تناول الوجبة") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GymTextWhite,
                        unfocusedTextColor = GymTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("محتوى الوجبة (مثال: صدور دجاج، أرز، سلطة)") },
                    minLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GymTextWhite,
                        unfocusedTextColor = GymTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    label = { Text("السعرات التقريبية (اختياري)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GymTextWhite,
                        unfocusedTextColor = GymTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TaqadomButton(
                text = "إضافة الوجبة",
                onClick = {
                    if (content.isNotBlank()) {
                        val cal = caloriesText.toIntOrNull() ?: 0
                        onAdd(title, time, content, cal)
                    }
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = GymTextMuted)
            }
        },
        containerColor = GymDarkSurface
    )
}
