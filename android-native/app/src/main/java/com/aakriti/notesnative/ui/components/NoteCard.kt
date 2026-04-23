package com.aakriti.notesnative.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aakriti.notesnative.ui.model.Note

@Composable
fun NoteCard(
  note: Note,
  background: Color,
  pinnedIcon: ImageVector?,
  overflowIcon: ImageVector?,
  onOverflow: (() -> Unit)?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val pressed = remember { MutableInteractionSource() }
  val scale = animateFloatAsState(targetValue = 1f, label = "cardScale")

  Card(
    modifier = modifier
      .scale(scale.value)
      .clickable(
        interactionSource = pressed,
        indication = null,
        onClick = onClick,
      ),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = background),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        if (note.pinned && pinnedIcon != null) {
          Icon(
            imageVector = pinnedIcon,
            contentDescription = "Pinned",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface,
          )
        } else {
          Spacer(modifier = Modifier.width(18.dp))
        }

        if (overflowIcon != null && onOverflow != null) {
          IconButton(onClick = onOverflow, modifier = Modifier.size(36.dp)) {
            Icon(imageVector = overflowIcon, contentDescription = "More")
          }
        }
      }

      Spacer(modifier = Modifier.size(6.dp))

      Text(
        text = if (note.title.isBlank()) "Untitled" else note.title,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
      )

      Spacer(modifier = Modifier.size(8.dp))

      Text(
        text = note.content.ifBlank { " " },
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
      )

      Spacer(modifier = Modifier.size(12.dp))

      Text(
        text = com.aakriti.notesnative.ui.util.relativeDayLabel(note.updatedAt),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
      )
    }
  }
}

