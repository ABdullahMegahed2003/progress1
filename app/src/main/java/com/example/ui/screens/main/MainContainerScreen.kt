package com.example.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.screens.daily.DailyLogScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.plans.PlanSetupScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressScreen
import com.example.ui.screens.workout.WorkoutTrackingScreen
import com.example.ui.theme.GymDarkBackground
import com.example.ui.theme.GymDarkSurface
import com.example.ui.theme.GymGreenAccent
import com.example.ui.theme.GymGreenContainer
import com.example.ui.theme.GymGreenPrimary
import com.example.ui.theme.GymTextMuted
import com.example.ui.theme.GymTextWhite
import com.example.viewmodel.TaqadomViewModel

data class NavTabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun MainContainerScreen(viewModel: TaqadomViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    var isInsidePlanSetup by remember { mutableStateOf(false) }

    // Intercept back button when in plan setup
    if (isInsidePlanSetup) {
        BackHandler {
            isInsidePlanSetup = false
        }
    } else if (selectedTab != 0) {
        BackHandler {
            viewModel.selectTab(0)
        }
    }

    val navTabs = listOf(
        NavTabItem(
            title = "الرئيسية",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        NavTabItem(
            title = "تماريني",
            selectedIcon = Icons.Filled.FitnessCenter,
            unselectedIcon = Icons.Outlined.FitnessCenter,
            testTag = "nav_workouts"
        ),
        NavTabItem(
            title = "سجل يومك",
            selectedIcon = Icons.Filled.CalendarMonth,
            unselectedIcon = Icons.Outlined.CalendarMonth,
            testTag = "nav_daily_log"
        ),
        NavTabItem(
            title = "التقدم",
            selectedIcon = Icons.Filled.TrendingUp,
            unselectedIcon = Icons.Outlined.TrendingUp,
            testTag = "nav_progress"
        ),
        NavTabItem(
            title = "حسابي",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "nav_profile"
        )
    )

    Scaffold(
        bottomBar = {
            if (!isInsidePlanSetup) {
                NavigationBar(
                    containerColor = GymDarkSurface,
                    contentColor = GymTextWhite
                ) {
                    navTabs.forEachIndexed { index, item ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(index) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GymGreenAccent,
                                unselectedIconColor = GymTextMuted,
                                selectedTextColor = GymGreenAccent,
                                unselectedTextColor = GymTextMuted,
                                indicatorColor = GymGreenContainer
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        containerColor = GymDarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isInsidePlanSetup) {
                PlanSetupScreen(viewModel = viewModel)
            } else {
                when (selectedTab) {
                    0 -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToWorkouts = { viewModel.selectTab(1) },
                        onNavigateToDailyLog = { viewModel.selectTab(2) },
                        onNavigateToProgress = { viewModel.selectTab(3) },
                        onNavigateToPlanSetup = { isInsidePlanSetup = true }
                    )
                    1 -> WorkoutTrackingScreen(viewModel = viewModel)
                    2 -> DailyLogScreen(viewModel = viewModel)
                    3 -> ProgressScreen(viewModel = viewModel)
                    4 -> ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToPlanSetup = { isInsidePlanSetup = true }
                    )
                }
            }
        }
    }
}
