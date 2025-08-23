package com.temp.mail.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.ui.components.AppDrawer
import com.temp.mail.ui.components.ShowSnackbar
import com.temp.mail.ui.viewmodel.EmailListViewModel
import com.temp.mail.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(ClipboardManager::class.java)
    

    // This will hold the action to refresh the currently visible email list.
    var onRefreshAction by remember { mutableStateOf({}) }

    // 收集数据流
    val emailAddresses by mainViewModel.emailAddresses.collectAsState()
    val selectedEmailAddress by mainViewModel.selectedEmailAddress.collectAsState()
    val error by mainViewModel.error.collectAsState()

    // Error Dialog
    if (error != null) {
        AlertDialog(
            onDismissRequest = { mainViewModel.clearError() },
            title = { Text(stringResource(id = R.string.error_title)) },
            text = { Text(error!!) },
            confirmButton = {
                Button(onClick = { mainViewModel.clearError() }) {
                    Text(stringResource(id = R.string.ok))
                }
            }
        )
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                emailAddresses = emailAddresses,
                selectedEmailAddress = selectedEmailAddress,
                onEmailAddressSelected = { emailAddress ->
                    mainViewModel.selectEmailAddress(emailAddress)
                    scope.launch { drawerState.close() }
                },
                onAddEmailClick = {
                    mainViewModel.addEmailAddress()
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        val currentAddress = selectedEmailAddress?.address
                        var showSnackBar by remember { mutableStateOf(false) }

                        if (showSnackBar) {
                            ShowSnackbar(
                                snackBarHostState = snackBarHostState,
                                message = stringResource(id = R.string.copied_to_clipboard),
                                onDismiss = { showSnackBar = false }
                            )
                        }

                        Text(
                            text = currentAddress ?: stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = if (currentAddress != null) {
                                Modifier.clickable {
                                    scope.launch {
                                        val clip = ClipData.newPlainText("Email Address", currentAddress!!)
                                        clipboardManager?.setPrimaryClip(clip)
                                        showSnackBar = true
                                    }
                                }
                            } else {
                                Modifier
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(id = R.string.navigation_drawer_open)
                            )
                        }
                    },
                    actions = {
                        // Show refresh button only when an email address is selected
                        if (selectedEmailAddress != null) {
                            IconButton(onClick = onRefreshAction) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = stringResource(id = R.string.refresh)
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                val currentEmail = selectedEmailAddress
                if (currentEmail != null) {
                    val emailListViewModel: EmailListViewModel = koinViewModel(key = currentEmail.address)
                    
                    // Update the refresh action and reset scroll behavior whenever the selected email changes.
                    LaunchedEffect(currentEmail, scrollBehavior) {
                        onRefreshAction = { emailListViewModel.refreshEmails(isManual = true) }
                        // Reset the scroll behavior to ensure the TopAppBar is visible
                        scrollBehavior.state.heightOffset = 0f
                        scrollBehavior.state.contentOffset = 0f
                    }

                    EmailListScreen(
                        emailAddress = currentEmail.address,
                        viewModel = emailListViewModel
                    )
                } else {
                    // 空状态
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.select_email_prompt),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}