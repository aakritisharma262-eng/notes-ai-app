package com.aakriti.notesnative.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NotesHeader(
  title: String,
  subtitle: String,
  rightIcon: ImageVector?,
  onRightIconClick: (() -> Unit)?,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Column(verticalArrangement = Arrangement.Center) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineLarge,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    if (rightIcon != null && onRightIconClick != null) {
      IconButton(onClick = onRightIconClick) {
        Icon(imageVector = rightIcon, contentDescription = "Settings")
      }
    }
  }
}

