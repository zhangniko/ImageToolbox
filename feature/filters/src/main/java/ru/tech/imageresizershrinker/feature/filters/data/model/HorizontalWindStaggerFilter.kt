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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import com.awxkee.aire.Aire
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.toAbgr
import ru.tech.imageresizershrinker.core.data.image.utils.ColorUtils.toModel
import ru.tech.imageresizershrinker.core.domain.model.ColorModel
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.ui.theme.toColor

internal class HorizontalWindStaggerFilter(
    override val value: Triple<Float, Int, ColorModel> = Triple(
        first = 0.2f,
        second = 90,
        third = Color.Transparent.toModel()
    )
) : Transformation<Bitmap>, Filter.HorizontalWindStagger {

    override val cacheKey: String
        get() = value.hashCode().toString()

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = Aire.horizontalWindStagger(
        bitmap = input,
        windStrength = value.first,
        streamsCount = value.second,
        clearColor = value.third.colorInt.toColor().toAbgr()
    )

}