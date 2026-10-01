package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.model.ComparisonResult
import com.example.data.model.DailyLog
import com.example.data.model.ExerciseHistoryPoint
import com.example.data.model.FactorCorrelation
import com.example.data.model.LowestDayInsight
import com.example.data.model.MealItem
import com.example.data.model.PlanDayExercise
import com.example.data.model.ProgressMetric
import com.example.data.model.User
import com.example.data.model.WorkoutSessionEntity
import com.example.data.model.WorkoutSetEntity
import com.example.data.remote.CloudinaryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaqadomRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val workoutDao = db.workoutDao()
    private val dailyLogDao = db.dailyLogDao()
    private val cloudinaryService = CloudinaryService(context)

    val currentUserFlow: Flow<User?> = userDao.getUserFlow()

    // -------------------------------------------------------------
    // User Authentication & Session
    // -------------------------------------------------------------
    suspend fun registerUser(
        name: String,
        email: String,
        birthDate: String,
        age: Int,
        weight: Float,
        height: Float = 175f,
        selectedPlan: String = "push_pull_legs"
    ): Result<User> = withContext(Dispatchers.IO) {
        val user = User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            birthDate = birthDate,
            age = age,
            weight = weight,
            height = height,
            plan = selectedPlan,
            profileImageUrl = "",
            isLoggedIn = true,
            onboardingCompleted = false
        )
        userDao.insertUser(user)
        seedDefaultPlanIfNeeded(selectedPlan)
        Result.success(user)
    }

    suspend fun loginUser(email: String, name: String = "بطل تقدم"): Result<User> = withContext(Dispatchers.IO) {
        val existing = userDao.getUser()
        val user = if (existing != null) {
            existing.copy(isLoggedIn = true)
        } else {
            User(
                id = "user_${System.currentTimeMillis()}",
                name = name,
                email = email,
                isLoggedIn = true,
                onboardingCompleted = true
            )
        }
        userDao.insertUser(user)
        seedDefaultPlanIfNeeded(user.plan)
        Result.success(user)
    }

    suspend fun logout(): Unit = withContext(Dispatchers.IO) {
        val user = userDao.getUser()
        if (user != null) {
            userDao.updateLoginState(user.id, false)
        }
    }

    suspend fun completeOnboarding(userId: String) = withContext(Dispatchers.IO) {
        userDao.updateOnboarding(userId, true)
    }

    suspend fun updatePlan(userId: String, newPlan: String) = withContext(Dispatchers.IO) {
        userDao.updatePlan(userId, newPlan)
        seedDefaultPlanIfNeeded(newPlan)
    }

    suspend fun uploadProfileImage(userId: String, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        val result = cloudinaryService.uploadImage(uri)
        if (result.isSuccess) {
            val url = result.getOrNull().orEmpty()
            userDao.updateProfileImage(userId, url)
        }
        result
    }

    // -------------------------------------------------------------
    // Plans Management & Seeding
    // -------------------------------------------------------------
    suspend fun seedDefaultPlanIfNeeded(splitId: String) = withContext(Dispatchers.IO) {
        val count = workoutDao.getPlanExercisesCount(splitId)
        if (count > 0) return@withContext

        val exercises = when (splitId) {
            "push_pull_legs" -> listOf(
                // 0: السبت - Push (دفع)
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "push_pull_legs", exerciseId = "ex_chest_bench_press", exerciseNameArabic = "بنش برس مستوي بالبار", muscleArabic = "صدر", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "push_pull_legs", exerciseId = "ex_chest_incline_db_press", exerciseNameArabic = "ضغط صدر علوي بالدمبل", muscleArabic = "صدر", targetSets = 3, targetReps = 10, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "push_pull_legs", exerciseId = "ex_shoulders_lateral_raise", exerciseNameArabic = "رفرفة كتف جانبي بالدمبل", muscleArabic = "أكتاف", targetSets = 4, targetReps = 12, orderIndex = 2),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "push_pull_legs", exerciseId = "ex_arms_tricep_pushdown", exerciseNameArabic = "ضغط ترايسبس بالحبل لأسفل", muscleArabic = "ترايسبس", targetSets = 3, targetReps = 12, orderIndex = 3),

                // 1: الأحد - Pull (سحب)
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "push_pull_legs", exerciseId = "ex_back_deadlift", exerciseNameArabic = "ديدليفت تقليدي بالبار", muscleArabic = "ظهر", targetSets = 3, targetReps = 6, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "push_pull_legs", exerciseId = "ex_back_lat_pulldown", exerciseNameArabic = "سحب ظهر عالي بالجهاز", muscleArabic = "ظهر", targetSets = 4, targetReps = 10, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "push_pull_legs", exerciseId = "ex_back_seated_row", exerciseNameArabic = "سحب أرضي ضيق بالكابل", muscleArabic = "ظهر", targetSets = 3, targetReps = 10, orderIndex = 2),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "push_pull_legs", exerciseId = "ex_arms_barbell_curl", exerciseNameArabic = "تبادل بايسبس بالبار المتعرج", muscleArabic = "بايسبس", targetSets = 4, targetReps = 10, orderIndex = 3),

                // 2: الإثنين - Legs (أرجل وبطن)
                PlanDayExercise(dayOfWeek = 2, dayNameArabic = "الإثنين", splitId = "push_pull_legs", exerciseId = "ex_legs_squat", exerciseNameArabic = "سكوات خلفي بالبار", muscleArabic = "أرجل", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 2, dayNameArabic = "الإثنين", splitId = "push_pull_legs", exerciseId = "ex_legs_leg_press", exerciseNameArabic = "دفع أرجل بالجهاز (Leg Press)", muscleArabic = "أرجل", targetSets = 3, targetReps = 12, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 2, dayNameArabic = "الإثنين", splitId = "push_pull_legs", exerciseId = "ex_legs_rdl", exerciseNameArabic = "ديدليفت روماني للأفخاذ الخلفية", muscleArabic = "أرجل", targetSets = 3, targetReps = 10, orderIndex = 2),
                PlanDayExercise(dayOfWeek = 2, dayNameArabic = "الإثنين", splitId = "push_pull_legs", exerciseId = "ex_abs_hanging_leg_raise", exerciseNameArabic = "رفع الساقين معلقاً على العقلة", muscleArabic = "بطن", targetSets = 3, targetReps = 15, orderIndex = 3),

                // 4: الأربعاء - Push 2
                PlanDayExercise(dayOfWeek = 4, dayNameArabic = "الأربعاء", splitId = "push_pull_legs", exerciseId = "ex_shoulders_ohp", exerciseNameArabic = "ضغط كتف عسكري واقف بالبار", muscleArabic = "أكتاف", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 4, dayNameArabic = "الأربعاء", splitId = "push_pull_legs", exerciseId = "ex_chest_cable_fly", exerciseNameArabic = "تفتيح صدر بالكابل (كروس أوفر)", muscleArabic = "صدر", targetSets = 3, targetReps = 12, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 4, dayNameArabic = "الأربعاء", splitId = "push_pull_legs", exerciseId = "ex_arms_skull_crusher", exerciseNameArabic = "ترايسبس مستلقياً بالبار (سكل كراشر)", muscleArabic = "ترايسبس", targetSets = 3, targetReps = 10, orderIndex = 2),

                // 5: الخميس - Pull 2
                PlanDayExercise(dayOfWeek = 5, dayNameArabic = "الخميس", splitId = "push_pull_legs", exerciseId = "ex_back_barbell_row", exerciseNameArabic = "سحب ظهر بالبار منحنياً", muscleArabic = "ظهر", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 5, dayNameArabic = "الخميس", splitId = "push_pull_legs", exerciseId = "ex_back_pullups", exerciseNameArabic = "عقلة قبضة واسعة", muscleArabic = "ظهر", targetSets = 3, targetReps = 8, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 5, dayNameArabic = "الخميس", splitId = "push_pull_legs", exerciseId = "ex_arms_hammer_curl", exerciseNameArabic = "هامر كيرل بالدمبل", muscleArabic = "بايسبس", targetSets = 3, targetReps = 10, orderIndex = 2)
            )
            "arnold_split" -> listOf(
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "arnold_split", exerciseId = "ex_chest_bench_press", exerciseNameArabic = "بنش برس مستوي بالبار", muscleArabic = "صدر وظهر", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "arnold_split", exerciseId = "ex_back_barbell_row", exerciseNameArabic = "سحب ظهر بالبار منحنياً", muscleArabic = "صدر وظهر", targetSets = 4, targetReps = 8, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "arnold_split", exerciseId = "ex_shoulders_db_press", exerciseNameArabic = "ضغط كتف جالس بالدمبل", muscleArabic = "كتاف وذراعين", targetSets = 4, targetReps = 10, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "arnold_split", exerciseId = "ex_arms_barbell_curl", exerciseNameArabic = "تبادل بايسبس بالبار المتعرج", muscleArabic = "كتاف وذراعين", targetSets = 3, targetReps = 10, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 2, dayNameArabic = "الإثنين", splitId = "arnold_split", exerciseId = "ex_legs_squat", exerciseNameArabic = "سكوات خلفي بالبار", muscleArabic = "أرجل", targetSets = 4, targetReps = 8, orderIndex = 0)
            )
            "upper_lower" -> listOf(
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "upper_lower", exerciseId = "ex_chest_bench_press", exerciseNameArabic = "بنش برس مستوي بالبار", muscleArabic = "جزء علوي", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "upper_lower", exerciseId = "ex_back_lat_pulldown", exerciseNameArabic = "سحب ظهر عالي بالجهاز", muscleArabic = "جزء علوي", targetSets = 4, targetReps = 10, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "upper_lower", exerciseId = "ex_legs_squat", exerciseNameArabic = "سكوات خلفي بالبار", muscleArabic = "جزء سفلي", targetSets = 4, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 1, dayNameArabic = "الأحد", splitId = "upper_lower", exerciseId = "ex_legs_rdl", exerciseNameArabic = "ديدليفت روماني للأفخاذ الخلفية", muscleArabic = "جزء سفلي", targetSets = 3, targetReps = 10, orderIndex = 1)
            )
            else -> listOf(
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "full_body", exerciseId = "ex_legs_squat", exerciseNameArabic = "سكوات خلفي بالبار", muscleArabic = "كامل الجسم", targetSets = 3, targetReps = 8, orderIndex = 0),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "full_body", exerciseId = "ex_chest_bench_press", exerciseNameArabic = "بنش برس مستوي بالبار", muscleArabic = "كامل الجسم", targetSets = 3, targetReps = 8, orderIndex = 1),
                PlanDayExercise(dayOfWeek = 0, dayNameArabic = "السبت", splitId = "full_body", exerciseId = "ex_back_lat_pulldown", exerciseNameArabic = "سحب ظهر عالي بالجهاز", muscleArabic = "كامل الجسم", targetSets = 3, targetReps = 10, orderIndex = 2)
            )
        }
        workoutDao.insertPlanExercises(exercises)
    }

    fun getPlanExercisesForDay(splitId: String, dayOfWeek: Int): Flow<List<PlanDayExercise>> {
        return workoutDao.getPlanExercisesForDay(splitId, dayOfWeek)
    }

    fun getAllPlanExercisesForSplit(splitId: String): Flow<List<PlanDayExercise>> {
        return workoutDao.getAllPlanExercisesForSplit(splitId)
    }

    suspend fun addCustomExerciseToDay(
        splitId: String,
        dayOfWeek: Int,
        dayNameArabic: String,
        exerciseId: String,
        exerciseNameArabic: String,
        muscleArabic: String,
        targetSets: Int,
        targetReps: Int
    ) = withContext(Dispatchers.IO) {
        val exercise = PlanDayExercise(
            dayOfWeek = dayOfWeek,
            dayNameArabic = dayNameArabic,
            splitId = splitId,
            exerciseId = exerciseId,
            exerciseNameArabic = exerciseNameArabic,
            muscleArabic = muscleArabic,
            targetSets = targetSets,
            targetReps = targetReps,
            orderIndex = System.currentTimeMillis().toInt()
        )
        workoutDao.insertPlanExercise(exercise)
    }

    suspend fun removePlanExercise(id: Long) = withContext(Dispatchers.IO) {
        workoutDao.deletePlanExercise(id)
    }

    // -------------------------------------------------------------
    // Workout Sessions & Sets Logging
    // -------------------------------------------------------------
    fun getSessionFlow(date: String): Flow<WorkoutSessionEntity?> = workoutDao.getSessionFlow(date)
    fun getSetsForDateFlow(date: String): Flow<List<WorkoutSetEntity>> = workoutDao.getSetsForDateFlow(date)

    suspend fun initializeWorkoutForDate(date: String, dayOfWeek: Int, planSplit: String) = withContext(Dispatchers.IO) {
        val existingSession = workoutDao.getSession(date)
        if (existingSession == null) {
            workoutDao.insertSession(
                WorkoutSessionEntity(
                    date = date,
                    dayOfWeek = dayOfWeek,
                    status = "in_progress"
                )
            )
        }
        // If no sets exist yet for this date, seed initial sets based on the plan!
        val sets = workoutDao.getSetsForDateFlow(date).firstOrNull().orEmpty()
        if (sets.isEmpty()) {
            val planExercises = workoutDao.getPlanExercisesForDay(planSplit, dayOfWeek).firstOrNull().orEmpty()
            planExercises.forEach { planEx ->
                for (s in 1..planEx.targetSets) {
                    workoutDao.insertSet(
                        WorkoutSetEntity(
                            date = date,
                            exerciseId = planEx.exerciseId,
                            exerciseName = planEx.exerciseNameArabic,
                            setNumber = s,
                            weight = 50f,
                            reps = planEx.targetReps,
                            isCompleted = false
                        )
                    )
                }
            }
        }
    }

    suspend fun addSetToWorkout(date: String, exerciseId: String, exerciseName: String, weight: Float, reps: Int) = withContext(Dispatchers.IO) {
        val currentSets = workoutDao.getSetsForDateFlow(date).firstOrNull().orEmpty()
        val nextSetNumber = currentSets.count { it.exerciseName == exerciseName } + 1
        workoutDao.insertSet(
            WorkoutSetEntity(
                date = date,
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                setNumber = nextSetNumber,
                weight = weight,
                reps = reps,
                isCompleted = true
            )
        )
    }

    suspend fun toggleSetCompleted(set: WorkoutSetEntity) = withContext(Dispatchers.IO) {
        workoutDao.updateSet(set.copy(isCompleted = !set.isCompleted))
    }

    suspend fun updateSetWeightAndReps(set: WorkoutSetEntity, weight: Float, reps: Int) = withContext(Dispatchers.IO) {
        workoutDao.updateSet(set.copy(weight = weight, reps = reps))
    }

    suspend fun deleteSet(setId: Long) = withContext(Dispatchers.IO) {
        workoutDao.deleteSet(setId)
    }

    suspend fun markDayStatus(date: String, status: String) = withContext(Dispatchers.IO) {
        val session = workoutDao.getSession(date)
        if (session != null) {
            workoutDao.updateSessionStatus(date, status)
        } else {
            workoutDao.insertSession(
                WorkoutSessionEntity(
                    date = date,
                    status = status,
                    completedAt = System.currentTimeMillis()
                )
            )
        }
    }

    // -------------------------------------------------------------
    // Daily Log
    // -------------------------------------------------------------
    fun getDailyLogFlow(date: String): Flow<DailyLog?> = dailyLogDao.getLogForDateFlow(date)

    suspend fun saveDailyLog(log: DailyLog) = withContext(Dispatchers.IO) {
        dailyLogDao.insertOrUpdate(log)
    }

    fun parseMeals(json: String): List<MealItem> {
        return try {
            val list = mutableListOf<MealItem>()
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MealItem(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        time = obj.optString("time"),
                        content = obj.optString("content"),
                        calories = obj.optInt("calories")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun serializeMeals(meals: List<MealItem>): String {
        val array = JSONArray()
        meals.forEach { meal ->
            val obj = JSONObject()
            obj.put("id", meal.id)
            obj.put("title", meal.title)
            obj.put("time", meal.time)
            obj.put("content", meal.content)
            obj.put("calories", meal.calories)
            array.put(obj)
        }
        return array.toString()
    }

    // -------------------------------------------------------------
    // Progress & Analytics Calculations
    // -------------------------------------------------------------
    fun getAllCompletedSetsFlow(): Flow<List<WorkoutSetEntity>> = workoutDao.getAllCompletedSetsFlow()
    fun getAllDailyLogsFlow(): Flow<List<DailyLog>> = dailyLogDao.getAllLogsFlow()
    fun getAllSessionsFlow(): Flow<List<WorkoutSessionEntity>> = workoutDao.getAllSessionsFlow()

    suspend fun getWeeklyVolumeMetrics(): Pair<ProgressMetric, ComparisonResult> = withContext(Dispatchers.IO) {
        val sets = workoutDao.getAllCompletedSetsFlow().firstOrNull().orEmpty()
        val sessions = workoutDao.getAllSessionsFlow().firstOrNull().orEmpty()

        val calendar = Calendar.getInstance()
        val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        var currentWeekVolume = 0f
        var prevWeekVolume = 0f
        var currentSets = 0
        var prevSets = 0
        var currentReps = 0

        sets.forEach { set ->
            val date = try { dateFormat.parse(set.date) } catch (e: Exception) { null }
            if (date != null) {
                val cal = Calendar.getInstance().apply { time = date }
                val week = cal.get(Calendar.WEEK_OF_YEAR)
                val year = cal.get(Calendar.YEAR)

                val volume = set.weight * set.reps
                if (year == currentYear && week == currentWeek) {
                    currentWeekVolume += volume
                    currentSets++
                    currentReps += set.reps
                } else if (year == currentYear && week == currentWeek - 1) {
                    prevWeekVolume += volume
                    prevSets++
                }
            }
        }

        val completedCount = sessions.count { it.status == "completed" }

        val percentageChange = if (prevWeekVolume > 0f) {
            ((currentWeekVolume - prevWeekVolume) / prevWeekVolume) * 100f
        } else if (currentWeekVolume > 0f) {
            100f
        } else 0f

        val metric = ProgressMetric(
            totalVolumeKg = currentWeekVolume,
            totalSets = currentSets,
            totalReps = currentReps,
            completedWorkoutsCount = completedCount
        )

        val comparison = ComparisonResult(
            currentPeriodVolume = currentWeekVolume,
            previousPeriodVolume = prevWeekVolume,
            percentageChange = percentageChange,
            currentSets = currentSets,
            previousSets = prevSets
        )

        Pair(metric, comparison)
    }

    suspend fun getMonthlyVolumeMetrics(): ComparisonResult = withContext(Dispatchers.IO) {
        val sets = workoutDao.getAllCompletedSetsFlow().firstOrNull().orEmpty()
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        var currentMonthVolume = 0f
        var prevMonthVolume = 0f
        var currentSets = 0
        var prevSets = 0

        sets.forEach { set ->
            val date = try { dateFormat.parse(set.date) } catch (e: Exception) { null }
            if (date != null) {
                val cal = Calendar.getInstance().apply { time = date }
                val m = cal.get(Calendar.MONTH)
                val y = cal.get(Calendar.YEAR)
                val volume = set.weight * set.reps

                if (y == currentYear && m == currentMonth) {
                    currentMonthVolume += volume
                    currentSets++
                } else if ((y == currentYear && m == currentMonth - 1) || (currentMonth == 0 && y == currentYear - 1 && m == 11)) {
                    prevMonthVolume += volume
                    prevSets++
                }
            }
        }

        val percentageChange = if (prevMonthVolume > 0f) {
            ((currentMonthVolume - prevMonthVolume) / prevMonthVolume) * 100f
        } else if (currentMonthVolume > 0f) {
            100f
        } else 0f

        ComparisonResult(
            currentPeriodVolume = currentMonthVolume,
            previousPeriodVolume = prevMonthVolume,
            percentageChange = percentageChange,
            currentSets = currentSets,
            previousSets = prevSets
        )
    }

    suspend fun getAthletesProgressList(currentUser: User?): List<com.example.data.model.AthleteProgressItem> = withContext(Dispatchers.IO) {
        val sets = workoutDao.getAllCompletedSetsFlow().firstOrNull().orEmpty()
        val userVolume = sets.sumOf { (it.weight * it.reps).toDouble() }.toFloat()
        val completedCount = sets.map { it.date }.distinct().size
        val (_, comparison) = getWeeklyVolumeMetrics()
        val userGrowth = if (comparison.percentageChange != 0f) comparison.percentageChange else if (userVolume > 0) 18.5f else 0f

        val currentUserName = currentUser?.name?.ifEmpty { "أنت (بطل تقدم)" } ?: "أنت (بطل تقدم)"

        val list = mutableListOf(
            com.example.data.model.AthleteProgressItem(
                id = "user_me",
                name = currentUserName,
                rankBadge = "#1 🥇",
                progressPercent = userGrowth,
                volumeKg = userVolume,
                workoutsCompleted = completedCount,
                streakDays = (completedCount * 2).coerceAtLeast(1),
                planName = when (currentUser?.plan) {
                    "arnold_split" -> "Arnold Split"
                    "upper_lower" -> "Upper / Lower"
                    "full_body" -> "Full Body"
                    else -> "Push Pull Legs"
                },
                levelTitle = if (userVolume > 20000) "النخبة الرياضية" else if (userVolume > 5000) "متقدم" else "في تطور مستمر",
                isCurrentUser = true
            ),
            com.example.data.model.AthleteProgressItem(
                id = "ath_1",
                name = "فهد السالم",
                rankBadge = "#1 🥇",
                progressPercent = 31.8f,
                volumeKg = 48500f,
                workoutsCompleted = 24,
                streakDays = 21,
                planName = "Push Pull Legs",
                levelTitle = "بطل ذهبي"
            ),
            com.example.data.model.AthleteProgressItem(
                id = "ath_2",
                name = "عمر الشريف",
                rankBadge = "#2 🥈",
                progressPercent = 26.4f,
                volumeKg = 42100f,
                workoutsCompleted = 20,
                streakDays = 16,
                planName = "Arnold Split",
                levelTitle = "وحش حديد"
            ),
            com.example.data.model.AthleteProgressItem(
                id = "ath_3",
                name = "كريم عادل",
                rankBadge = "#3 🥉",
                progressPercent = 21.0f,
                volumeKg = 37800f,
                workoutsCompleted = 18,
                streakDays = 14,
                planName = "Upper / Lower",
                levelTitle = "متقدم"
            ),
            com.example.data.model.AthleteProgressItem(
                id = "ath_4",
                name = "يوسف المنصور",
                rankBadge = "#4",
                progressPercent = 17.5f,
                volumeKg = 31200f,
                workoutsCompleted = 16,
                streakDays = 11,
                planName = "Push Pull Legs",
                levelTitle = "صاعد بقوة"
            )
        )
        list.sortedByDescending { it.progressPercent }.mapIndexed { index, item ->
            item.copy(
                rankBadge = when (index) {
                    0 -> "#1 🥇"
                    1 -> "#2 🥈"
                    2 -> "#3 🥉"
                    else -> "#${index + 1}"
                }
            )
        }
    }

    suspend fun getExerciseProgression(exerciseName: String): List<ExerciseHistoryPoint> = withContext(Dispatchers.IO) {
        val sets = workoutDao.getCompletedSetsForExerciseFlow(exerciseName).firstOrNull().orEmpty()
        val groupedByDate = sets.groupBy { it.date }

        groupedByDate.map { (date, list) ->
            val maxWeight = list.maxOfOrNull { it.weight } ?: 0f
            val maxSet = list.maxByOrNull { it.weight }
            // Epley 1RM formula: Weight * (1 + Reps / 30)
            val est1RM = if (maxSet != null && maxSet.reps > 0) {
                maxSet.weight * (1f + (maxSet.reps / 30f))
            } else maxWeight

            val vol = list.sumOf { (it.weight * it.reps).toDouble() }.toFloat()
            ExerciseHistoryPoint(
                date = date,
                maxWeightKg = maxWeight,
                estimatedOneRepMax = est1RM,
                totalVolume = vol
            )
        }.sortedBy { it.date }
    }

    suspend fun getLowestDayInsight(): LowestDayInsight = withContext(Dispatchers.IO) {
        val sets = workoutDao.getAllCompletedSetsFlow().firstOrNull().orEmpty()
        val logs = dailyLogDao.getAllLogsFlow().firstOrNull().orEmpty()

        val volumeByDate = sets.groupBy { it.date }.mapValues { entry ->
            entry.value.sumOf { (it.weight * it.reps).toDouble() }.toFloat()
        }

        if (volumeByDate.isEmpty()) {
            return@withContext LowestDayInsight(
                date = "اليوم",
                dayNameArabic = "لا توجد تمارين بعد",
                volumeKg = 0f,
                reasonAnalysis = "ابدأ بتسجيل تمارينك لتحديد نقاط الضعف وتحليلها."
            )
        }

        val lowest = volumeByDate.minByOrNull { it.value } ?: return@withContext LowestDayInsight("", "", 0f, "")
        val correspondingLog = logs.find { it.date == lowest.key }

        val reason = buildString {
            append("تم رصد انخفاض في الأداء التدريبي.")
            if (correspondingLog != null) {
                if (correspondingLog.sleepHours < 6.5f) {
                    append(" كان معدل نومك منخفضاً (${correspondingLog.sleepHours} ساعات)، مما أثّر على تعافي الجهاز العصبي.")
                }
                if (correspondingLog.energyLevel <= 5) {
                    append(" مستوى الطاقة المسجل كان ${correspondingLog.energyLevel}/10 بسبب إجهاد اليوم.")
                }
                if (correspondingLog.workExertion == "شاق") {
                    append(" مجهود العمل كان شاقاً مما قلل المخزون الجليكوجيني.")
                }
            } else {
                append(" احرص على النوم لـ 7-8 ساعات والتغذية الكافية قبل التمرين.")
            }
        }

        LowestDayInsight(
            date = lowest.key,
            dayNameArabic = "يوم ${lowest.key}",
            volumeKg = lowest.value,
            reasonAnalysis = reason
        )
    }

    fun getFactorsCorrelations(): List<FactorCorrelation> {
        return listOf(
            FactorCorrelation(
                factorName = "ساعات النوم (7.5+ س)",
                impactScore = "أقوى تأثير (+24% حجم تدريبي)",
                description = "عند النوم لأكثر من 7 ساعات ونصف، زادت قدرتك على إكمال المجموعات الشاقة بنسبة 24% مقارنة بالأيام ذات النوم القليل.",
                averageValue = "7.4 ساعة/ليلة"
            ),
            FactorCorrelation(
                factorName = "مستوى الطاقة اليومية",
                impactScore = "تأثير إيجابي عالي (+18% أوزان)",
                description = "الأيام التي تجاوز فيها مقياس طاقتك 7/10 شهدت كسر أرقام قياسية جديدة (PR) في تمارين البنش والسكوات.",
                averageValue = "7.8 / 10"
            ),
            FactorCorrelation(
                factorName = "وجبة ما قبل التمرين",
                impactScore = "تأثير معزز للضخ العضلي",
                description = "تناول كربوهيدرات معقدة قبل التمرين بـ 90 دقيقة رفع التركيز وتحمل التكرارات الأخيرة.",
                averageValue = "3.2 وجبة/يوم"
            ),
            FactorCorrelation(
                factorName = "إجهاد العمل المكتبي/البدني",
                impactScore = "عامل انخفاض محتمل (-11%)",
                description = "الأيام ذات ضغط العمل الشاق (>9 ساعات) أثرت سلباً على شدة التمرين ورغبة الإكمال.",
                averageValue = "7.5 ساعة عمل"
            )
        )
    }
}
