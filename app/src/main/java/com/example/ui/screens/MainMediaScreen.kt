package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItemEntity
import com.example.ui.components.AiRoboticsBanner
import com.example.ui.components.DescriptionDialog
import com.example.ui.components.MediaItemCard
import com.example.ui.components.MediaViewerDialog
import com.example.ui.components.PasswordAuthDialog
import com.example.ui.components.UploadMediaDialog
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueSecondary
import com.example.ui.viewmodel.HxAIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMediaScreen(
    viewModel: HxAIViewModel,
    modifier: Modifier = Modifier
) {
    val mediaList by viewModel.mediaList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedMediaType.collectAsState()

    val showUploadAuth by viewModel.showUploadAuthDialog.collectAsState()
    val showUploadMedia by viewModel.showUploadMediaDialog.collectAsState()
    val showDeleteAuth by viewModel.showDeleteAuthDialog.collectAsState()
    val pendingDelete by viewModel.pendingDeleteItem.collectAsState()
    val showDescription by viewModel.showDescriptionDialog.collectAsState()
    val viewingItem by viewModel.viewingItem.collectAsState()
    val actionError by viewModel.actionErrorMessage.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Bottom bar with Setting option situated at the bottom left corner
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left corner bottom side: Setting option to open "Description" and "Contact us"
                    FilledTonalButton(
                        onClick = { viewModel.openDescriptionDialog() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SkyBluePrimary.copy(alpha = 0.15f),
                            contentColor = SkyBlueSecondary
                        ),
                        modifier = Modifier.testTag("bottom_left_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settings / Description",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // Re-lock app button for security testing
                    IconButton(
                        onClick = { viewModel.lockApp() },
                        modifier = Modifier.testTag("lock_system_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock App",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Top Section: Bold sky-blue line with "AI & Robotics" and below it "Media" with Plus (+) button
            AiRoboticsBanner(
                itemCount = mediaList.size,
                selectedType = selectedType,
                onSelectType = { viewModel.setSelectedMediaType(it) },
                onPlusClick = { viewModel.onPlusClicked() }
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("search_media_input"),
                placeholder = { Text("Search pictures, PDFs, links...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = SkyBlueSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkyBluePrimary,
                    cursorColor = SkyBluePrimary
                )
            )

            // Media list
            if (mediaList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = SkyBluePrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No media matching \"$searchQuery\"" else "No media items yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap the Plus (+) button above to upload pictures, PDFs, or links with security key.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, start = 20.dp, end = 20.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("media_items_list"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = mediaList,
                        key = { it.id }
                    ) { item ->
                        MediaItemCard(
                            item = item,
                            onViewItem = { viewModel.openItemViewer(it) },
                            onDeleteItem = { viewModel.onRequestDelete(it) }
                        )
                    }
                }
            }
        }
    }

    // 1. Password Verification Dialog for Uploading (Password: 4201451963H)
    if (showUploadAuth) {
        PasswordAuthDialog(
            title = "Upload Authorization",
            subtitle = "Please enter authorization passcode to upload pictures, PDFs, or links to HxAI.",
            errorMessage = actionError,
            onConfirm = { password -> viewModel.verifyUploadPassword(password) },
            onDismiss = { viewModel.dismissUploadAuthDialog() }
        )
    }

    // 2. Upload Media Dialog (Pictures, PDFs, Links)
    if (showUploadMedia) {
        UploadMediaDialog(
            onUploadPicture = { title, uri, note ->
                viewModel.uploadPicture(title, uri, note)
            },
            onUploadPdf = { title, uri, note ->
                viewModel.uploadPdf(title, uri, note)
            },
            onUploadLink = { title, url, note ->
                viewModel.uploadLink(title, url, note)
            },
            onDismiss = { viewModel.dismissUploadMediaDialog() }
        )
    }

    // 3. Password Verification Dialog for Deleting (Password: 4201451963H)
    if (showDeleteAuth && pendingDelete != null) {
        PasswordAuthDialog(
            title = "Delete Authorization",
            subtitle = "Enter passcode 4201451963H to permanently delete \"${pendingDelete?.title}\".",
            errorMessage = actionError,
            onConfirm = { password -> viewModel.verifyDeletePassword(password) },
            onDismiss = { viewModel.dismissDeleteAuthDialog() }
        )
    }

    // 4. Description & Settings Dialog (with "Contact us - hariomk6992@gmail.com")
    if (showDescription) {
        DescriptionDialog(
            onDismiss = { viewModel.closeDescriptionDialog() }
        )
    }

    // 5. Open Media Viewer (pictures, PDFs, links can be viewed without password)
    viewingItem?.let { item ->
        MediaViewerDialog(
            item = item,
            onDismiss = { viewModel.closeItemViewer() }
        )
    }
}
