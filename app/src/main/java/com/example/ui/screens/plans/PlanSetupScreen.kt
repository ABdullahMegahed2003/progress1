package com.example.ui.screens.plans

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlanDayExercise
import com.example.data.remote.ExerciseRepository
import com.example.ui.components.SectionHeader
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

data class SplitOption(
    val id: String,
    val title: String,
    val description: String,
    val daysBadge: String
)

@Composable
fun PlanSetupScreen(viewModel: TaqadomViewModel) {
    val selectedSplitId by viewModel.selectedSplitId.collectAsState()
    val planExercises by viewModel.planExercises.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedDayOfWeek by remember { mutableIntStateOf(0) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSplitId) {
        viewModel.loadPlanExercises(selectedSplitId)
    }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val splits = listOf(
        SplitOption(
            id = "push_pull_legs",
            title = "Push Pull Legs (PPL)",
            description = "دفع (صدر، كتاف، تراي) / سحب (ظهر، باي) / أرجل وبطن",
            daysBadge = "6 أيام تمرين"
        ),
        SplitOption(
            id = "arnold_split",
            title = "Arnold Split (أرنولد)",
            description = "صدر وظهر معاً / كتاف وذراعين / أرجل وبطن",
            daysBadge = "6 أيام مكثفة"
        ),
        SplitOption(
            id = "upper_lower",
            title = "Upper / Lower (علوي وسفلي)",
            description = "جسم علوي / جسم سفلي مع يومين راحة للتعافي المثالي",
            daysBadge = "4 أيام تمرين"
        ),
        SplitOption(
            id = "full_body",
            title = "Full Body (كامل الجسم)",
            description = "تمارين كامل عضلات الجسم 3 أيام بالأسبوع",
            daysBadge = "3 أيام تمرين"
        )
    )

    val daysLabels = listOf(
        "السبت" to 0,
        "الأحد" to 1,
        "الإثنين" to 2,
        "الثلاثاء" to 3,
        "الأربعاء" to 4,
        "الخميس" to 5,
        "الجمعة" to 6
    )

    val exercisesForDay = planExercises.filter { it.dayOfWeek == selectedDayOfWeek }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 90.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "إعداد وتخصيص النظام التدريبي",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = GymTextWhite
                )
                Text(
                    text = "اختر تقسيمة التمرين وجهّز تمارين كل يوم حسب أهدافك",
                    fontSize = 13.sp,
                    color = GymTextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Split Selection Cards
            items(splits) { split ->
                val isSelected = selectedSplitId == split.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.selectSplit(split.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) GymGreenContainer else GymDarkSurface
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            if (isSelected) listOf(GymGreenPrimary, GymGreenDark)
                            else listOf(GymDarkOutline, GymDarkOutline)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = split.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GymTextWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) GymGreenPrimary else GymDarkSurfaceVariant)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = split.daysBadge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GymDarkBackground else GymTextMuted
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = split.description,
                                fontSize = 12.sp,
                                color = if (isSelected) GymGreenAccent else GymTextMuted
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(GymGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = GymDarkBackground,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Apply Plan Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                TaqadomButton(
                    text = "اعتماد هذا النظام لجدولي",
                    onClick = { viewModel.saveUserPlan(selectedSplitId) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Day by Day Exercise Setup
            item {
                SectionHeader(
                    title = "تجهيز تمارين كل يوم",
                    subtitle = "حدد اليوم لإضافة أو تعديل التمارين المقترحة"
                )

                // Day pills
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(daysLabels) { (label, idx) ->
                        val isSel = selectedDayOfWeek == idx
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) GymGreenPrimary else GymDarkSurface)
                                .clickable { selectedDayOfWeek = idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) GymDarkBackground else GymTextWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Exercises for selected day
            if (exercisesForDay.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GymDarkSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "لا توجد تمارين مسجلة لهذا اليوم (يوم راحة أو أضف تمارين)",
                                fontSize = 13.sp,
                                color = GymTextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TaqadomOutlinedButton(
                                text = "+ إضافة تمرين لليوم",
                                onClick = { showAddExerciseDialog = true }
                            )
                        }
                    }
                }
            } else {
                items(exercisesForDay, key = { it.id }) { ex ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = GymGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = ex.exerciseNameArabic,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GymTextWhite
                                    )
                                    Text(
                                        text = "${ex.muscleArabic} • ${ex.targetSets} مجموعات × ${ex.targetReps} عدات",
                                        fontSize = 12.sp,
                                        color = GymGreenAccent
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.removeExerciseFromPlan(ex.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = GymRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    TaqadomOutlinedButton(
                        text = "+ إضافة تمرين إضافي لليوم",
                        onClick = { showAddExerciseDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Add Exercise Dialog
        if (showAddExerciseDialog) {
            val currentDayName = daysLabels.find { it.second == selectedDayOfWeek }?.first ?: "السبت"
            AddExerciseToPlanDialog(
                dayNameArabic = currentDayName,
                onDismiss = { showAddExerciseDialog = false },
                onAdd = { catalogItem, sets, reps ->
                    viewModel.addExerciseToPlan(
                        dayOfWeek = selectedDayOfWeek,
                        dayNameArabic = currentDayName,
                        exerciseId = catalogItem.id,
                        exerciseNameArabic = catalogItem.nameArabic,
                        muscleArabic = catalogItem.muscleArabic,
                        targetSets = sets,
                        targetReps = reps
                    )
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
private fun AddExerciseToPlanDialog(
    dayNameArabic: String,
    onDismiss: () -> Unit,
    onAdd: (com.example.data.model.ExerciseCatalogItem, Int, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sets by remember { mutableIntStateOf(4) }
    var reps by remember { mutableIntStateOf(10) }

    val exercises = remember(searchQuery) {
        if (searchQuery.isNotBlank()) ExerciseRepository.search(searchQuery)
        else ExerciseRepository.allExercises
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("إضافة تمرين لجدول $dayNameArabic", fontWeight = FontWeight.Bold, color = GymTextWhite, fontSize = 16.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("ابحث عن التمرين") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = GymTextWhite,
                        unfocusedTextColor = GymTextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = "$sets",
                        onValueChange = { sets = it.toIntOrNull() ?: 3 },
                        label = { Text("المجموعات") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = "$reps",
                        onValueChange = { reps = it.toIntOrNull() ?: 10 },
                        label = { Text("العدات") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = GymTextWhite,
                            unfocusedTextColor = GymTextWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(exercises) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onAdd(item, sets, reps) },
                            color = GymDarkSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.nameArabic, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GymTextWhite)
                                    Text("${item.muscleArabic} • ${item.equipmentArabic}", fontSize = 11.sp, color = GymGreenAccent)
                                }
                                Icon(Icons.Default.Add, contentDescription = null, tint = GymGreenPrimary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = GymTextMuted)
            }
        },
        containerColor = GymDarkSurface
    )
}
