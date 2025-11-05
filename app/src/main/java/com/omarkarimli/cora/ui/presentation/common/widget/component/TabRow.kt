package com.omarkarimli.cora.ui.presentation.common.widget.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.omarkarimli.cora.domain.models.TabModel
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.utils.noRippleClickable
import com.omarkarimli.cora.utils.performHaptic

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    tabs: List<TabModel>,
    selectedTab: TabModel,
    onTabSelected: (TabModel) -> Unit
) {
    val cornerRadius = Dimens.CornerRadiusExtraLarge

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(cornerRadius)
            )
            .padding(Dimens.PaddingExtraSmall / 2)
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                RoundedCornerShape(cornerRadius)
            )
            .padding(Dimens.PaddingExtraSmall)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab.key == tab.key
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.surface
                        else Color.Transparent
                    )
                    .noRippleClickable(
                        onClick = {
                            if (!isSelected) onTabSelected(tab)
                        }.performHaptic()
                    )
                    .padding(vertical = Dimens.PaddingSmall)
            ) {
                Text(
                    text = tab.value,
                    style = AppTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}