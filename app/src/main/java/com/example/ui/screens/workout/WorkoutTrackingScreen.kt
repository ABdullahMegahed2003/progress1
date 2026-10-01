package com.example.ui.screens.workout

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseCatalogItem
import com.example.data.model.WorkoutSetEntity
import com.example.data.remote.ExerciseRepository
import com.example.ui.components.RestTimerBar
import com.example.ui.components.TaqadomButton
import com.example.ui.components.TaqadomOutlinedButton
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
fun WorkoutTrackingScreen(
    viewModel: TaqadomViewModel
) {
    val dateDisplay by viewModel.dateDisplayArabic.collectAsState()
    val workoutSets by viewModel.workoutSets.collectAsState()
    val session by viewModel.workoutSession.collectAsState()
    val restSeconds by viewModel.restTimerSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val completedCount = workoutSets.count { it.isCompleted }
    val totalCount = workoutSets.size
    val completionPercent = if (totalCount > 0) (completedCount * 100) / totalCount else 0

    // Group sets by exercise name
    val exerciseGroups = workoutSets.groupBy { it.exerciseName }

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
            // Header with Date and Day Switcher
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GymDarkSurface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                text = dateDisplay,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymTextWhite
                            )
                            Text(
                                text = when (session?.status) {
                                    "completed" -> "مكتمل بالكامل (100%)"
                                    "rest" -> "يوم راحة معتمد"
                                    "skipped" -> "تمرين لم يتم"
                                    else -> "إنجاز اليوم: $completionPercent% ($completedCount/$totalCount)"
                                },
                                fontSize = 12.sp,
                                color = when (session?.status) {
                                    "completed" -> GymGreenAccent
                                    "rest" -> GymGold
                                    "skipped" -> GymRed
                                    else -> GymTextMuted
                                }
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days of week pills (السبت إلى الجمعة)
                    val daysLabels = listOf(
                        "السبت" to 0,
                        "الأحد" to 1,
                        "الإثنين" to 2,
                        "الثلاثاء" to 3,
                        "الأربعاء" to 4,
                        "الخميس" to 5,
                        "الجمعة" to 6
                    )
                    val currentDayIndex by viewModel.currentDayOfWeek.collectAsState()

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(daysLabels) { (label, idx) ->
                            val isSelected = currentDayIndex == idx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) GymGreenPrimary else GymDarkBackground
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) GymGreenAccent else GymDarkOutline,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        // Shift current date to selected weekday index
                                        val offset = idx - currentDayIndex
                                        if (offset != 0) viewModel.changeDayBy(offset)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) GymDarkBackground else GymTextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Rest Timer Floating Bar
            item {
                Box(modifier = Modifier.padding(16.dp)) {
                    RestTimerBar(
                        secondsRemaining = restSeconds,
                        isRunning = isTimerRunning,
                        onStart = { sec -> viewModel.startRestTimer(sec) },
                        onStop = { viewModel.stopRestTimer() }
                    )
                }
            }

            // Quick Status Buttons Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusButton(
                        text = "يوم راحة 🧘",
                        isSelected = session?.status == "rest",
                        color = GymGold,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.markWorkoutStatus("rest")
                    }

                    StatusButton(
                        text = "تمرين لم يتم ❌",
                        isSelected = session?.status == "skipped",
                        color = GymRed,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.markWorkoutStatus("skipped")
                    }

                    StatusButton(
                        text = "إكمال التمرين 💪",
                        isSelected = session?.status == "completed",
                        color = GymGreenPrimary,
                        modifier = Modifier.weight(1f)
                    ) {
                        viewModel.markWorkoutStatus("completed")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Empty state if no exercises for today
            if (exerciseGroups.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = GymGreenPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد تمارين مسجلة لهذا اليوم",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GymTextWhite
                            )
                            Text(
                                text = "أضف تمارين مخصصة أو انقر على الزر بالأسفل للبدء",
                                fontSize = 12.sp,
                                color = GymTextMuted,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TaqadomOutlinedButton(
                                text = "إضافة تمرين لليوم",
                                onClick = { showAddExerciseDialog = true }
                            )
                        }
                    }
                }
            } else {
                // Exercise Cards
                items(exerciseGroups.entries.toList(), key = { it.key }) { (exerciseName, sets) ->
                    val firstSet = sets.firstOrNull()
                    ExerciseSetsCard(
                        exerciseName = exerciseName,
                        sets = sets,
                        onToggleSet = { set -> viewModel.toggleSet(set) },
                        onUpdateSet = { set, w, r -> viewModel.updateSet(set, w, r) },
                        onAddSet = {
                            val lastSet = sets.lastOrNull()
                            viewModel.addSet(
                                exerciseId = firstSet?.exerciseId ?: "custom",
                                exerciseName = exerciseName,
                                weight = lastSet?.weight ?: 50f,
                                reps = lastSet?.reps ?: 10
                            )
                        },
                        onDeleteSet = { setId -> viewModel.deleteSet(setId) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                // Add More Exercises Button
                item {
                    Box(modifier = Modifier.padding(16.dp)) {
                        TaqadomOutlinedButton(
                            text = "+ إضافة تمرين إضافي لليوم",
                            onClick = { showAddExerciseDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Add Exercise Dialog
        if (showAddExerciseDialog) {
            AddExerciseDialog(
                onDismiss = { showAddExerciseDialog = false },
                onExerciseSelected = { catalogItem, targetSets, targetReps, initialWeight ->
                    for (i in 1..targetSets) {
                        viewModel.addSet(
                            exerciseId = catalogItem.id,
                            exerciseName = catalogItem.nameArabic,
                            weight = initialWeight,
                            reps = targetReps
                        )
                    }
                    showAddExerciseDialog = false
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
private fun StatusButton(
    text: String,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (isSelected) color else GymDarkOutline,
                RoundedCornerShape(10.dp)
            ),
        color = if (isSelected) color.copy(alpha = 0.2f) else GymDarkSurface
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else GymTextMuted
            )
        }
    }
}

@Composable
private fun ExerciseSetsCard(
    exerciseName: String,
    sets: List<WorkoutSetEntity>,
    onToggleSet: (WorkoutSetEntity) -> Unit,
    onUpdateSet: (WorkoutSetEntity, Float, Int) -> Unit,
    onAddSet: () -> Unit,
    onDeleteSet: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = GymDarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GymDarkOutline))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GymGreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = exerciseName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GymTextWhite
                    )
                }

                IconButton(
                    onClick = onAddSet,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GymGreenContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "إضافة مجموعة",
                        tint = GymGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "مجموعة", fontSize = 11.sp, color = GymTextMuted, modifier = Modifier.width(50.dp))
                Text(text = "الوزن (كجم)", fontSize = 11.sp, color = GymTextMuted, modifier = Modifier.weight(1f))
                Text(text = "العدات", fontSize = 11.sp, color = GymTextMuted, modifier = Modifier.weight(1f))
                Text(text = "تمت", fontSize = 11.sp, color = GymTextMuted, modifier = Modifier.width(44.dp))
                Spacer(modifier = Modifier.width(28.dp))
            }

            // Sets rows
            sets.forEach { set ->
                SetRowItem(
                    set = set,
                    onToggle = { onToggleSet(set) },
                    onUpdate = { w, r -> onUpdateSet(set, w, r) },
                    onDelete = { onDeleteSet(set.id) }
                )
            }
        }
    }
}

@Composable
private fun SetRowItem(
    set: WorkoutSetEntity,
    onToggle: () -> Unit,
    onUpdate: (Float, Int) -> Unit,
    onDelete: () -> Unit
) {
    var weightVal by remember(set.weight) { mutableFloatStateOf(set.weight) }
    var repsVal by remember(set.reps) { mutableIntStateOf(set.reps) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (set.isCompleted) GymGreenContainer.copy(alpha = 0.4f) else GymDarkBackground)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number badge
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(GymDarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${set.setNumber}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (set.isCompleted) GymGreenAccent else GymTextWhite
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Weight Stepper
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newW = (weightVal - 2.5f).coerceAtLeast(0f)
                    weightVal = newW
                    onUpdate(newW, repsVal)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = GymTextMuted, modifier = Modifier.size(14.dp))
            }
            Text(
                text = "$weightVal",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GymTextWhite
            )
            IconButton(
                onClick = {
                    val newW = weightVal + 2.5f
                    weightVal = newW
                    onUpdate(newW, repsVal)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = GymTextMuted, modifier = Modifier.size(14.dp))
            }
        }

        // Reps Stepper
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val newR = (repsVal - 1).coerceAtLeast(1)
                    repsVal = newR
                    onUpdate(weightVal, newR)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = null, tint = GymTextMuted, modifier = Modifier.size(14.dp))
            }
            Text(
                text = "$repsVal",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GymTextWhite
            )
            IconButton(
                onClick = {
                    val newR = repsVal + 1
                    repsVal = newR
                    onUpdate(weightVal, newR)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = GymTextMuted, modifier = Modifier.size(14.dp))
            }
        }

        // Completed Checkbox
        Checkbox(
            checked = set.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = GymGreenPrimary,
                checkmarkColor = GymDarkBackground,
                uncheckedColor = GymDarkOutline
            ),
            modifier = Modifier.size(36.dp)
        )

        // Delete set icon
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(26.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "حذف المجموعة",
                tint = GymTextMuted.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onExerciseSelected: (ExerciseCatalogItem, Int, Int, Float) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("الكل") }
    var targetSets by remember { mutableIntStateOf(3) }
    var targetReps by remember { mutableIntStateOf(10) }
    var initialWeight by remember { mutableFloatStateOf(40f) }

    val muscleTabs = listOf("الكل", "صدر", "ظهر", "أرجل", "أكتاف", "بايسبس", "ترايسبس", "بطن")

    val exercises = remember(searchQuery, selectedMuscle) {
        val list = if (selectedMuscle == "الكل") ExerciseRepository.allExercises
        else ExerciseRepository.allExercises.filter { it.muscleArabic == selectedMuscle }

        if (searchQuery.isNotBlank()) {
            list.filter { it.nameArabic.contains(searchQuery, ignoreCase = true) || it.name.contains(searchQuery, ignoreCase = true) }
        } else list
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "اختيار تمرين من قاعدة التمارين",
                fontWeight = FontWeight.Bold,
                color = GymTextWhite,
                fontSize = 16.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("ابحث عن اسم التمرين بالعربي أو الإنجليزي") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GymGreenPrimary,
                        unfocusedBorderColor = GymDarkOutline,
                        focusedTextColor = GymTextWhite,
                        unfocusedTextColor = GymTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Muscle Category Filter
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(muscleTabs) { m ->
                        val isSel = selectedMuscle == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) GymGreenPrimary else GymDarkSurfaceVariant)
                                .clickable { selectedMuscle = m }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = m,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) GymDarkBackground else GymTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Exercise List
                LazyColumn(modifier = Modifier.height(220.dp)) {
                    items(exercises) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    onExerciseSelected(item, targetSets, targetReps, initialWeight)
                                },
                            color = GymDarkSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.nameArabic,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GymTextWhite
                                    )
                                    Text(
                                        text = "${item.muscleArabic} • ${item.equipmentArabic}",
                                        fontSize = 11.sp,
                                        color = GymGreenAccent
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = GymGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "إلغاء", color = GymTextMuted)
            }
        },
        containerColor = GymDarkSurface
    )
}
