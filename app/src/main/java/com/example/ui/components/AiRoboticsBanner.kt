package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaType
import com.example.ui.theme.SkyBlueAccent
import com.example.ui.theme.SkyBlueDark
import com.example.ui.theme.SkyBlueLight
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SkyBlueSecondary

@Composable
fun AiRoboticsBanner(
    itemCount: Int,
    selectedType: MediaType?,
    onSelectType: (MediaType?) -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Bold Sky-Blue Line / Banner with "AI & Robotics" written attractively
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .testTag("ai_robotics_banner"),
            color = SkyBluePrimary
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0077B6),
                                Color(0xFF00B4D8),
                                Color(0xFF48CAE4),
                                Color(0xFF00B4D8)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PrecisionManufacturing,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI & Robotics",
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = Color(0xFFE0F7FA),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Below the sky-blue line: "Media" title on left and Plus (+) sign on the right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Media",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("media_title")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = CircleShape,
                    color = SkyBluePrimary.copy(alpha = 0.2f),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "$itemCount",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlueSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Plus (+) sign button on the right side of the Media section
            FloatingActionButton(
                onClick = onPlusClick,
                containerColor = SkyBluePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("add_media_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Upload picture, PDF or link",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter chips for fast navigation (All, Pictures, PDFs, Links)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedType == null,
                onClick = { onSelectType(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBluePrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedType == MediaType.PICTURE,
                onClick = { onSelectType(if (selectedType == MediaType.PICTURE) null else MediaType.PICTURE) },
                label = { Text("Pictures") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBluePrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedType == MediaType.PDF,
                onClick = { onSelectType(if (selectedType == MediaType.PDF) null else MediaType.PDF) },
                label = { Text("PDFs") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBluePrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedType == MediaType.LINK,
                onClick = { onSelectType(if (selectedType == MediaType.LINK) null else MediaType.LINK) },
                label = { Text("Links") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SkyBluePrimary,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}
