package com.aakriti.notesnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun NotesSearchBar(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  leadingIcon: ImageVector,
  modifier: Modifier = Modifier,
) {
  val shape = RoundedCornerShape(16.dp)
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .height(52.dp)
      .background(MaterialTheme.colorScheme.surface, shape),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier.fillMaxWidth(),
      placeholder = { androidx.compose.material3.Text(placeholder) },
      leadingIcon = { Icon(leadingIcon, contentDescription = "Search") },
      singleLine = true,
      shape = shape,
      colors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surface,
        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
        disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
      ),
    )
  }
}

