package dev.guruprasath.feeledger.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import dev.guruprasath.feeledger.AppContainer
import dev.guruprasath.feeledger.FeeLedgerApp

/** Creates a ViewModel scoped to the current navigation entry, wired from the [AppContainer]. */
@Composable
inline fun <reified VM : ViewModel> appViewModel(
    key: String? = null,
    crossinline create: (AppContainer) -> VM,
): VM {
    val container = (LocalContext.current.applicationContext as FeeLedgerApp).container
    return viewModel(key = key, factory = viewModelFactory { initializer { create(container) } })
}
