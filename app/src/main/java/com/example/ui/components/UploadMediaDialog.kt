package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueSecondary

@Composable
fun UploadMediaDialog(
    onUploadPicture: (title: String, uri: Uri, note: String) -> Unit,
    onUploadPdf: (title: String, uri: Uri, note: String) -> Unit,
    onUploadLink: (title: String, url: String, note: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Picture, 1: PDF, 2: Link
    val tabs = listOf("Picture", "PDF", "Link")

    // Picture state
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var pictureTitle by remember { mutableStateOf("") }
    var pictureNote by remember { mutableStateOf("") }

    // PDF state
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfTitle by remember { mutableStateOf("") }
    var pdfNote by remember { mutableStateOf("") }

    // Link state
    var linkUrl by remember { mutableStateOf("") }
    var linkTitle by remember { mutableStateOf("") }
    var linkNote by remember { mutableStateOf("") }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            if (pictureTitle.isBlank()) {
                pictureTitle = "AI Robotics Image ${System.currentTimeMillis() % 1000}"
            }
        }
    }

    // PDF document picker launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPdfUri = uri
            if (pdfTitle.isBlank()) {
                pdfTitle = "Robotics Specification Doc.pdf"
            }
        }
    }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = SkyBluePrimary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Upload to HxAI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = SkyBluePrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.SemiBold) },
                            icon = {
                                when (index) {
                                    0 -> Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                    1 -> Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                    else -> Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // PICTURE UPLOAD
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Picker trigger box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(
                                        1.5.dp,
                                        if (selectedImageUri != null) SkyBluePrimary else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .testTag("select_photo_box"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Selected picture preview",
                                        modifier = Modifier.fillMaxWidth().height(140.dp)
                                    )
                                } else {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = SkyBluePrimary,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Tap to choose Picture from device",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = pictureTitle,
                                onValueChange = { pictureTitle = it },
                                label = { Text("Picture Title") },
                                placeholder = { Text("e.g. Robot Vision Camera Feed") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("picture_title_input")
                            )

                            OutlinedTextField(
                                value = pictureNote,
                                onValueChange = { pictureNote = it },
                                label = { Text("Description / Notes (Optional)") },
                                placeholder = { Text("e.g. Captured sensor diagram") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    1 -> {
                        // PDF UPLOAD
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .border(
                                        1.5.dp,
                                        if (selectedPdfUri != null) SkyBluePrimary else MaterialTheme.colorScheme.outlineVariant,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        pdfPickerLauncher.launch("application/pdf")
                                    }
                                    .testTag("select_pdf_box"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (selectedPdfUri != null) Icons.Default.CheckCircle else Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = if (selectedPdfUri != null) SkyBluePrimary else Color(0xFFE63946),
                                        modifier = Modifier.size(34.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (selectedPdfUri != null) "PDF Document Selected!" else "Tap to choose PDF from device",
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedPdfUri != null) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = pdfTitle,
                                onValueChange = { pdfTitle = it },
                                label = { Text("Document Title") },
                                placeholder = { Text("e.g. Python Robotics Manual.pdf") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("pdf_title_input")
                            )

                            OutlinedTextField(
                                value = pdfNote,
                                onValueChange = { pdfNote = it },
                                label = { Text("Description / Notes (Optional)") },
                                placeholder = { Text("e.g. Technical specifications sheet") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    2 -> {
                        // LINK UPLOAD
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = linkUrl,
                                onValueChange = { linkUrl = it },
                                label = { Text("URL Link *") },
                                placeholder = { Text("https://example.com/ai-robotics") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = SkyBluePrimary)
                                },
                                modifier = Modifier.fillMaxWidth().testTag("link_url_input")
                            )

                            OutlinedTextField(
                                value = linkTitle,
                                onValueChange = { linkTitle = it },
                                label = { Text("Link Title") },
                                placeholder = { Text("e.g. Java AI Robotics Library") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("link_title_input")
                            )

                            OutlinedTextField(
                                value = linkNote,
                                onValueChange = { linkNote = it },
                                label = { Text("Description / Notes (Optional)") },
                                placeholder = { Text("e.g. Documentation for Python AI model") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Quick sample links for convenient testing
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        linkUrl = "https://python.org"
                                        linkTitle = "Official Python AI & Robotics Docs"
                                        linkNote = "Python resources for autonomous intelligent robotics."
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Python", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        linkUrl = "https://openjdk.org"
                                        linkTitle = "Java Enterprise Robotics Hub"
                                        linkNote = "Concurrent robot telemetry and embedded JVM control."
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Java", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when (selectedTab) {
                        0 -> {
                            val uri = selectedImageUri
                            if (uri != null) {
                                onUploadPicture(
                                    pictureTitle.ifBlank { "Picture ${System.currentTimeMillis() % 1000}" },
                                    uri,
                                    pictureNote
                                )
                            }
                        }
                        1 -> {
                            val uri = selectedPdfUri
                            if (uri != null) {
                                onUploadPdf(
                                    pdfTitle.ifBlank { "PDF Document ${System.currentTimeMillis() % 1000}.pdf" },
                                    uri,
                                    pdfNote
                                )
                            }
                        }
                        2 -> {
                            if (linkUrl.isNotBlank()) {
                                onUploadLink(
                                    linkTitle.ifBlank { linkUrl },
                                    linkUrl,
                                    linkNote
                                )
                            }
                        }
                    }
                },
                enabled = when (selectedTab) {
                    0 -> selectedImageUri != null
                    1 -> selectedPdfUri != null
                    2 -> linkUrl.isNotBlank()
                    else -> false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkyBluePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("upload_media_submit_button")
            ) {
                Text("Upload to Vault", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    )
}
