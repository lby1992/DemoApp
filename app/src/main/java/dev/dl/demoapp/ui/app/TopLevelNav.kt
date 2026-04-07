package dev.dl.demoapp.ui.app

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import dev.dl.demoapp.R
import dev.dl.demoapp.core.designsystem.icon.AppIcons
import dev.dl.demoapp.core.designsystem.icon.Dashboard
import dev.dl.demoapp.core.designsystem.icon.DashboardOutlined
import dev.dl.demoapp.core.designsystem.icon.Expense
import dev.dl.demoapp.core.designsystem.icon.ExpenseOutlined
import dev.dl.demoapp.core.designsystem.icon.Settings
import dev.dl.demoapp.core.designsystem.icon.SettingsOutlined
import dev.dl.demoapp.core.designsystem.icon.Todo
import dev.dl.demoapp.core.designsystem.icon.TodoOutlined
import dev.dl.demoapp.feature.expense.ExpenseNavKey
import dev.dl.demoapp.feature.dashboard.DashboardNavKey
import dev.dl.demoapp.feature.settings.SettingsNavKey
import dev.dl.demoapp.feature.todos.TodosNavKey

data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @get:StringRes val titleTextId: Int,
)

val DASHBOARD = TopLevelNavItem(
    selectedIcon = AppIcons.Dashboard,
    unselectedIcon = AppIcons.DashboardOutlined,
    titleTextId = R.string.nav_dashboard,
)

val TODOS = TopLevelNavItem(
    selectedIcon = AppIcons.Todo,
    unselectedIcon = AppIcons.TodoOutlined,
    titleTextId = R.string.nav_todos,
)

val EXPENSE = TopLevelNavItem(
    selectedIcon = AppIcons.Expense,
    unselectedIcon = AppIcons.ExpenseOutlined,
    titleTextId = R.string.nav_expense,
)

val SETTINGS = TopLevelNavItem(
    selectedIcon = AppIcons.Settings,
    unselectedIcon = AppIcons.SettingsOutlined,
    titleTextId = R.string.nav_settings,
)

val TOP_LEVEL_NAV_ITEMS = mapOf(
    DashboardNavKey to DASHBOARD,
    TodosNavKey to TODOS,
    ExpenseNavKey to EXPENSE,
    SettingsNavKey to SETTINGS,
)