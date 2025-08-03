package com.temp.mail.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.temp.mail.R
import com.temp.mail.ui.components.AppDrawer
import org.koin.androidx.compose.koinViewModel
import com.temp.mail.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    // 收集数据流
    val emailAddresses by viewModel.emailAddresses.collectAsState()
    val selectedEmailAddress by viewModel.selectedEmailAddress.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                emailAddresses = emailAddresses,
                selectedEmailAddress = selectedEmailAddress,
                onEmailAddressSelected = { emailAddress ->
                    viewModel.selectEmailAddress(emailAddress)
                    scope.launch { drawerState.close() }
                },
                onAddEmailClick = {
                    viewModel.addEmailAddress()
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = selectedEmailAddress?.address 
                                ?: stringResource(id = R.string.app_name)
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
                    // 显示选中邮箱的内容
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.current_email, currentEmail.address),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        
                        items(10) { index ->
                            Text(
                                text = stringResource(id = R.string.sample_email_content, index),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
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