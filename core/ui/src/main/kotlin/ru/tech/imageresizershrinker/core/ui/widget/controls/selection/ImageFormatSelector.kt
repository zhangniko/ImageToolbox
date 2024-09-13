/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.widget.controls.selection

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Architecture
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImageFormatSelector(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified,
    entries: List<ImageFormatGroup> = ImageFormatGroup.entries,
    forceEnabled: Boolean = false,
    value: ImageFormat,
    onValueChange: (ImageFormat) -> Unit
) {
    val enabled = !LocalSettingsState.current.overwriteFiles || forceEnabled
    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()

    val cannotChangeFormat: () -> Unit = {
        scope.launch {
            toastHostState.showToast(
                context.getString(R.string.cannot_change_image_format),
                Icons.Rounded.Architecture
            )
        }
    }

    val allFormats by remember(entries) {
        derivedStateOf {
            entries.flatMap { it.formats }
        }
    }

    LaunchedEffect(value, allFormats) {
        if (value !in allFormats) {
            onValueChange(
                if (ImageFormat.Png.Lossless in allFormats) {
                    ImageFormat.Png.Lossless
                } else allFormats.first()
            )
        }
    }

    Box {
        Column(
            modifier = modifier
                .container(
                    shape = RoundedCornerShape(24.dp),
                    color = backgroundColor
                )
                .alpha(if (enabled) 1f else 0.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.image_format),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(entries.filtered()) { items ->
                FlowRow(
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .container(
                            color = MaterialTheme.colorScheme.surface
                        )
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    items.forEach {
                        EnhancedChip(
                            onClick = {
                                if (enabled) {
                                    onValueChange(it.formats[0])
                                } else cannotChangeFormat()
                            },
                            selected = value in it.formats,
                            label = {
                                Text(text = it.title)
                            },
                            selectedColor = MaterialTheme.colorScheme.tertiary,
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            val formats by remember(value) {
                derivedStateOf {
                    entries.firstOrNull {
                        value in it.formats
                    }?.formats ?: emptyList()
                }
            }
            AnimatedContent(formats.filteredFormats()) { items ->
                if (items.size > 1) {
                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 4.dp)
                            .container(
                                color = MaterialTheme.colorScheme.surface,
                                resultPadding = 0.dp
                            )
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.compression_type),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        FlowRow(
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterVertically
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterHorizontally
                            )
                        ) {
                            items.forEach {
                                EnhancedChip(
                                    onClick = {
                                        if (enabled) {
                                            onValueChange(it)
                                        } else cannotChangeFormat()
                                    },
                                    selected = value == it,
                                    label = {
                                        Text(text = it.title)
                                    },
                                    selectedColor = MaterialTheme.colorScheme.tertiary,
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        if (!enabled) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            cannotChangeFormat()
                        }
                    }
            ) {}
        }
    }
}

@Composable
private fun List<ImageFormatGroup>.filtered(): List<ImageFormatGroup> = remember(this) {
    if (Build.VERSION.SDK_INT <= 24) toMutableList().apply {
        removeAll(ImageFormatGroup.highLevelFormats)
    }
    else this
}

@Composable
private fun List<ImageFormat>.filteredFormats(): List<ImageFormat> = remember(this) {
    if (Build.VERSION.SDK_INT <= 24) toMutableList().apply {
        removeAll(ImageFormat.highLevelFormats)
    }
    else this
}