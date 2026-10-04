package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.data.repository.TaqadomRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaqadomViewModel(application: Application) : AndroidViewModel(application) {

    val repository = TaqadomRepository(application)

    val currentUser: StateFlow<User?> = repository.currentUserFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Current navigation state
    private val _currentScreen = MutableStateFlow("splash") // splash, login, register, onboarding, main, plan_setup
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Bottom Navigation tab: 0=Home, 1=Workouts, 2=DailyLog, 3=Progress, 4=Profile
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // -------------------------------------------------------------
    // Date & Calendar Handling for Gym workouts & Daily logs
    // -------------------------------------------------------------
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val arabicDisplayFormat = SimpleDateFormat("EEEE d MMMM", Locale("ar"))

    private val _currentDate = MutableStateFlow(dateFormat.format(Date()))
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()

    private val _dateDisplayArabic = MutableStateFlow(arabicDisplayFormat.format(Date()))
    val dateDisplayArabic: StateFlow<String> = _dateDisplayArabic.asStateFlow()

    private val _currentDayOfWeek = MutableStateFlow(getCalendarDayIndex(Date()))
    val currentDayOfWeek: StateFlow<Int> = _currentDayOfWeek.asStateFlow()

    // Workout sets for selected date
    private val _workoutSets = MutableStateFlow<List<WorkoutSetEntity>>(emptyList())
    val workoutSets: StateFlow<List<WorkoutSetEntity>> = _workoutSets.asStateFlow()

    private val _workoutSession = MutableStateFlow<WorkoutSessionEntity?>(null)
    val workoutSession: StateFlow<WorkoutSessionEntity?> = _workoutSession.asStateFlow()

    // Daily log for selected date
    private val _dailyLog = MutableStateFlow<DailyLog?>(null)
    val dailyLog: StateFlow<DailyLog?> = _dailyLog.asStateFlow()

    private val _dailyMeals = MutableStateFlow<List<MealItem>>(emptyList())
    val dailyMeals: StateFlow<List<MealItem>> = _dailyMeals.asStateFlow()

    // Rest timer state
    private val _restTimerSeconds = MutableStateFlow(60)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()
    private var timerJob: Job? = null

    // Plan setup state
    private val _selectedSplitId = MutableStateFlow("push_pull_legs")
    val selectedSplitId: StateFlow<String> = _selectedSplitId.asStateFlow()

    private val _planExercises = MutableStateFlow<List<PlanDayExercise>>(emptyList())
    val planExercises: StateFlow<List<PlanDayExercise>> = _planExercises.asStateFlow()

    // Analytics state
    private val _weeklyMetric = MutableStateFlow<ProgressMetric?>(null)
    val weeklyMetric: StateFlow<ProgressMetric?> = _weeklyMetric.asStateFlow()

    private val _weeklyComparison = MutableStateFlow<ComparisonResult?>(null)
    val weeklyComparison: StateFlow<ComparisonResult?> = _weeklyComparison.asStateFlow()

    private val _monthlyComparison = MutableStateFlow<ComparisonResult?>(null)
    val monthlyComparison: StateFlow<ComparisonResult?> = _monthlyComparison.asStateFlow()

    private val _athletesProgress = MutableStateFlow<List<com.example.data.model.AthleteProgressItem>>(emptyList())
    val athletesProgress: StateFlow<List<com.example.data.model.AthleteProgressItem>> = _athletesProgress.asStateFlow()

    private val _lowestDay = MutableStateFlow<LowestDayInsight?>(null)
    val lowestDay: StateFlow<LowestDayInsight?> = _lowestDay.asStateFlow()

    private val _selectedExerciseForChart = MutableStateFlow("بنش برس مستوي بالبار")
    val selectedExerciseForChart: StateFlow<String> = _selectedExerciseForChart.asStateFlow()

    private val _exerciseHistory = MutableStateFlow<List<ExerciseHistoryPoint>>(emptyList())
    val exerciseHistory: StateFlow<List<ExerciseHistoryPoint>> = _exerciseHistory.asStateFlow()

    val factorCorrelations: List<FactorCorrelation> = repository.getFactorsCorrelations()

    // Toast/Alert message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    init {
        checkSession()
        refreshForSelectedDate(_currentDate.value)
        loadAnalytics()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val user = repository.currentUserFlow.first()
            if (user != null && user.isLoggedIn) {
                _selectedSplitId.value = user.plan
                if (!user.onboardingCompleted) {
                    _currentScreen.value = "onboarding"
                } else {
                    _currentScreen.value = "main"
                }
            } else {
                _currentScreen.value = "login"
            }
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // -------------------------------------------------------------
    // Authentication
    // -------------------------------------------------------------
    fun login(email: String, pass: String) {
        if (email.isBlank() || !email.contains("@")) {
            _userMessage.value = "يرجى إدخال بريد إلكتروني صحيح"
            return
        }
        if (pass.length < 6) {
            _userMessage.value = "كلمة السر يجب أن تكون 6 أحرف على الأقل"
            return
        }
        viewModelScope.launch {
            val result = repository.loginUser(email)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user?.onboardingCompleted == false) {
                    _currentScreen.value = "onboarding"
                } else {
                    _currentScreen.value = "main"
                }
            }
        }
    }

    fun register(name: String, email: String, pass: String, birthDate: String, age: Int, weight: Float) {
        if (name.isBlank()) {
            _userMessage.value = "يرجى إدخال اسمك الكريم"
            return
        }
        if (email.isBlank() || !email.contains("@")) {
            _userMessage.value = "يرجى إدخال بريد إلكتروني صالح"
            return
        }
        if (pass.length < 6) {
            _userMessage.value = "كلمة السر يجب ألا تقل عن 6 أحرف"
            return
        }
        if (weight <= 20f) {
            _userMessage.value = "يرجى إدخال وزن صحيح بالكيلوجرام"
            return
        }
        viewModelScope.launch {
            val result = repository.registerUser(
                name = name,
                email = email,
                birthDate = birthDate,
                age = age,
                weight = weight,
                selectedPlan = _selectedSplitId.value
            )
            if (result.isSuccess) {
                _currentScreen.value = "onboarding"
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            val user = repository.currentUserFlow.first { it?.isLoggedIn == true }
            if (user == null) return@launch
            repository.completeOnboarding(user.id)
            _currentScreen.value = "main"
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _currentScreen.value = "login"
        }
    }

    fun uploadProfileAvatar(uri: Uri) {
        val user = currentUser.value ?: return
        _isUploadingImage.value = true
        viewModelScope.launch {
            val result = repository.uploadProfileImage(user.id, uri)
            _isUploadingImage.value = false
            if (result.isSuccess) {
                _userMessage.value = "تم حفظ الصورة الشخصية بنجاح!"
            } else {
                _userMessage.value = "حدث خطأ أثناء رفع الصورة"
            }
        }
    }

    // -------------------------------------------------------------
    // Date & Workout Navigation
    // -------------------------------------------------------------
    fun selectDate(dateString: String) {
        try {
            val parsed = dateFormat.parse(dateString) ?: Date()
            _currentDate.value = dateString
            _dateDisplayArabic.value = arabicDisplayFormat.format(parsed)
            _currentDayOfWeek.value = getCalendarDayIndex(parsed)
            refreshForSelectedDate(dateString)
        } catch (e: Exception) {
            // ignore
        }
    }

    fun changeDayBy(offset: Int) {
        try {
            val cal = Calendar.getInstance()
            val parsed = dateFormat.parse(_currentDate.value) ?: Date()
            cal.time = parsed
            cal.add(Calendar.DAY_OF_YEAR, offset)
            val newDate = cal.time
            val newStr = dateFormat.format(newDate)
            _currentDate.value = newStr
            _dateDisplayArabic.value = arabicDisplayFormat.format(newDate)
            _currentDayOfWeek.value = getCalendarDayIndex(newDate)
            refreshForSelectedDate(newStr)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun refreshForSelectedDate(date: String) {
        viewModelScope.launch {
            val plan = currentUser.value?.plan ?: _selectedSplitId.value
            repository.initializeWorkoutForDate(date, _currentDayOfWeek.value, plan)

            // Observe sets
            repository.getSetsForDateFlow(date).collect { sets ->
                _workoutSets.value = sets
            }
        }
        viewModelScope.launch {
            repository.getSessionFlow(date).collect { session ->
                _workoutSession.value = session
            }
        }
        viewModelScope.launch {
            repository.getDailyLogFlow(date).collect { log ->
                _dailyLog.value = log
                _dailyMeals.value = repository.parseMeals(log?.mealsJson.orEmpty())
            }
        }
    }

    fun toggleSet(set: WorkoutSetEntity) {
        viewModelScope.launch {
            repository.toggleSetCompleted(set)
            if (!set.isCompleted) {
                // User just completed a set, offer to start rest timer!
                startRestTimer(60)
            }
            loadAnalytics()
        }
    }

    fun updateSet(set: WorkoutSetEntity, weight: Float, reps: Int) {
        viewModelScope.launch {
            repository.updateSetWeightAndReps(set, weight, reps)
            loadAnalytics()
        }
    }

    fun addSet(exerciseId: String, exerciseName: String, weight: Float, reps: Int) {
        viewModelScope.launch {
            repository.addSetToWorkout(_currentDate.value, exerciseId, exerciseName, weight, reps)
            loadAnalytics()
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            repository.deleteSet(setId)
            loadAnalytics()
        }
    }

    fun markWorkoutStatus(status: String) {
        viewModelScope.launch {
            repository.markDayStatus(_currentDate.value, status)
            _userMessage.value = when (status) {
                "completed" -> "كفو! تم تسجيل إكمال تمرين اليوم بنجاح 💪"
                "rest" -> "تم تسجيل يوم راحة واستشفاء عضلات 🧘"
                "skipped" -> "تم تحديد اليوم كتمرين لم يتم"
                else -> "تم التحديث"
            }
            loadAnalytics()
        }
    }

    // -------------------------------------------------------------
    // Rest Timer
    // -------------------------------------------------------------
    fun startRestTimer(seconds: Int) {
        timerJob?.cancel()
        _restTimerSeconds.value = seconds
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                delay(1000)
                _restTimerSeconds.value -= 1
            }
            _isTimerRunning.value = false
        }
    }

    fun stopRestTimer() {
        timerJob?.cancel()
        _isTimerRunning.value = false
    }

    fun resetRestTimer(seconds: Int = 60) {
        timerJob?.cancel()
        _restTimerSeconds.value = seconds
        _isTimerRunning.value = false
    }

    // -------------------------------------------------------------
    // Daily Log Updates
    // -------------------------------------------------------------
    fun updateSleepAndEnergy(sleepHours: Float, energyLevel: Int, sleepQuality: Int) {
        val current = _dailyLog.value ?: DailyLog(date = _currentDate.value)
        val updated = current.copy(
            sleepHours = sleepHours,
            energyLevel = energyLevel,
            sleepQuality = sleepQuality
        )
        viewModelScope.launch {
            repository.saveDailyLog(updated)
            _dailyLog.value = updated
        }
    }

    fun updateWorkAndNotes(workHours: Float, exertion: String, workoutTime: String, notes: String) {
        val current = _dailyLog.value ?: DailyLog(date = _currentDate.value)
        val updated = current.copy(
            workHours = workHours,
            workExertion = exertion,
            workoutTime = workoutTime,
            notes = notes
        )
        viewModelScope.launch {
            repository.saveDailyLog(updated)
            _dailyLog.value = updated
            _userMessage.value = "تم حفظ سجل اليوم بنجاح!"
        }
    }

    fun addMeal(title: String, time: String, content: String, calories: Int) {
        val currentMeals = _dailyMeals.value.toMutableList()
        currentMeals.add(
            MealItem(
                id = "meal_${System.currentTimeMillis()}",
                title = title,
                time = time,
                content = content,
                calories = calories
            )
        )
        _dailyMeals.value = currentMeals
        saveCurrentMeals(currentMeals)
    }

    fun deleteMeal(mealId: String) {
        val currentMeals = _dailyMeals.value.filter { it.id != mealId }
        _dailyMeals.value = currentMeals
        saveCurrentMeals(currentMeals)
    }

    private fun saveCurrentMeals(meals: List<MealItem>) {
        val current = _dailyLog.value ?: DailyLog(date = _currentDate.value)
        val updated = current.copy(mealsJson = repository.serializeMeals(meals))
        viewModelScope.launch {
            repository.saveDailyLog(updated)
            _dailyLog.value = updated
        }
    }

    // -------------------------------------------------------------
    // Plan Setup
    // -------------------------------------------------------------
    fun selectSplit(splitId: String) {
        _selectedSplitId.value = splitId
        viewModelScope.launch {
            repository.seedDefaultPlanIfNeeded(splitId)
            loadPlanExercises(splitId)
        }
    }

    fun saveUserPlan(splitId: String) {
        _selectedSplitId.value = splitId
        val user = currentUser.value
        if (user != null) {
            viewModelScope.launch {
                repository.updatePlan(user.id, splitId)
                _userMessage.value = "تم تطبيق النظام التدريبي الجديد بنجاح!"
                refreshForSelectedDate(_currentDate.value)
            }
        }
    }

    fun loadPlanExercises(splitId: String) {
        viewModelScope.launch {
            repository.getAllPlanExercisesForSplit(splitId).collect { list ->
                _planExercises.value = list
            }
        }
    }

    fun addExerciseToPlan(
        dayOfWeek: Int,
        dayNameArabic: String,
        exerciseId: String,
        exerciseNameArabic: String,
        muscleArabic: String,
        targetSets: Int,
        targetReps: Int
    ) {
        viewModelScope.launch {
            repository.addCustomExerciseToDay(
                splitId = _selectedSplitId.value,
                dayOfWeek = dayOfWeek,
                dayNameArabic = dayNameArabic,
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                muscleArabic = muscleArabic,
                targetSets = targetSets,
                targetReps = targetReps
            )
            _userMessage.value = "تمت إضافة التمرين لجدول يوم $dayNameArabic"
        }
    }

    fun removeExerciseFromPlan(id: Long) {
        viewModelScope.launch {
            repository.removePlanExercise(id)
        }
    }

    // -------------------------------------------------------------
    // Progress & Analytics
    // -------------------------------------------------------------
    fun loadAnalytics() {
        viewModelScope.launch {
            val (metric, comparison) = repository.getWeeklyVolumeMetrics()
            _weeklyMetric.value = metric
            _weeklyComparison.value = comparison
            _monthlyComparison.value = repository.getMonthlyVolumeMetrics()
            _lowestDay.value = repository.getLowestDayInsight()
            _athletesProgress.value = repository.getAthletesProgressList(currentUser.value)
            loadExerciseChart(_selectedExerciseForChart.value)
        }
    }

    fun selectExerciseForChart(exerciseName: String) {
        _selectedExerciseForChart.value = exerciseName
        loadExerciseChart(exerciseName)
    }

    private fun loadExerciseChart(exerciseName: String) {
        viewModelScope.launch {
            val points = repository.getExerciseProgression(exerciseName)
            _exerciseHistory.value = points
        }
    }

    private fun getCalendarDayIndex(date: Date): Int {
        val cal = Calendar.getInstance().apply { time = date }
        // Calendar.SUNDAY = 1, SATURDAY = 7
        // We want 0 = Saturday, 1 = Sunday, 2 = Monday, 3 = Tuesday, 4 = Wednesday, 5 = Thursday, 6 = Friday
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }
    }
}
