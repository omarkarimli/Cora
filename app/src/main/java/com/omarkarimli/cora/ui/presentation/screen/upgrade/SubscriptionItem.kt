package com.omarkarimli.cora.ui.presentation.screen.upgrade

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.omarkarimli.cora.R // Import R class for string resources
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.ui.presentation.common.widget.component.WideButton
import com.omarkarimli.cora.ui.theme.AppTypography
import com.omarkarimli.cora.ui.theme.Dimens

@Composable
fun SubscriptionItem(
    subscriptionModel: SubscriptionModel,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(Dimens.ElevationMedium),
        shape = RoundedCornerShape(Dimens.CornerRadiusLarge),
        border = if (isCurrent) BorderStroke(Dimens.StrokeWidthExtraSmall, MaterialTheme.colorScheme.onSurface) else null,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
        ) {
            TopContent(subscriptionModel)
            FeatureList(subscriptionModel.maxUsageData, subscriptionModel.adsEnabled)
            WideButton(
                modifier = Modifier.padding(start = Dimens.PaddingMedium, end = Dimens.PaddingMedium, bottom = Dimens.PaddingMedium),
                text = if (isCurrent) stringResource(R.string.current_plan) else stringResource(R.string.select_plan),
                onClick = onClick,
                enabled = !isCurrent
            )
        }
    }
}

@Composable
fun FeatureList(usageData: UsageDataModel, isAdsEnabled: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        FeatureRow(text = stringResource(R.string.subscription_feature_searches, usageData.webSearches))
        FeatureRow(text = stringResource(R.string.subscription_feature_attaches, usageData.attaches))
        FeatureRow(text = stringResource(R.string.subscription_feature_message_chars, usageData.messageChars))
        FeatureRow(text = stringResource(R.string.subscription_feature_web_images, usageData.webSearchResultCount))
        if (isAdsEnabled) FeatureRow(text = stringResource(R.string.ads_enabled))
    }
}

@Composable
private fun TopContent(subscriptionModel: SubscriptionModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimens.PaddingMedium,
                end = Dimens.PaddingMedium,
                top = Dimens.PaddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = subscriptionModel.title, // Assuming title comes from backend and is already localized or fine as is
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = if (subscriptionModel.price == 0.0) stringResource(R.string.free) else "$${subscriptionModel.price}",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun FeatureRow(text: String) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.PaddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = AppTypography.bodyLarge
        )
    }
}