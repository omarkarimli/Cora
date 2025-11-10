package com.omarkarimli.cora.ui.presentation.screen.usage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.ui.presentation.common.widget.component.StandardListItemUi
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun UsageDataRow(item: HashMap<StandardListItemModel, Float>) {
    val standardListItemModel = item.keys.first()
    val progress = item.values.first()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        StandardListItemUi(item = standardListItemModel)
        if (standardListItemModel.endingIcon == null) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ProgressIndicatorHeight)
                    .padding(horizontal = Dimens.PaddingSmall),
                progress = { progress },
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )
        }
    }
}