package com.omarkarimli.cora.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.omarkarimli.cora.BuildConfig.EMAIL
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.ClothModel
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.SearchImageResponse
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.ui.theme.Dimens
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.models.ValidatableField
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.serper.SearchTextResponse
import com.omarkarimli.cora.ui.theme.outlineLight
import com.omarkarimli.cora.ui.theme.primaryLight

fun String.isWebUrl(): Boolean {
    return this.contains("http") || this.contains("www")
}

fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun String.toAnnotatedString(): AnnotatedString {
    // **...** content is group 2 bold
    // *...* content is group 4 italic
    // _..._ content is group 6 underline
    val regex = Regex("(?<BOLD>\\*\\*(.*?)\\*\\*)|(?<ITALIC>\\*(.*?)\\*)|(?<UNDERLINE>_(.*?)_)")

    return buildAnnotatedString {
        var currentIndex = 0

        // Find all matches for any of the three patterns
        regex.findAll(this@toAnnotatedString).forEach { matchResult ->
            // 1. Append the text *before* the current formatted segment
            append(this@toAnnotatedString.substring(currentIndex, matchResult.range.first))

            // 2. Determine the style and content based on which named group matched
            val (formatType, content) = when {
                matchResult.groups["BOLD"] != null ->
                    Pair(FormatType.BOLD, matchResult.groups[2]?.value)
                matchResult.groups["ITALIC"] != null ->
                    Pair(FormatType.ITALIC, matchResult.groups[4]?.value)
                matchResult.groups["UNDERLINE"] != null ->
                    Pair(FormatType.UNDERLINE, matchResult.groups[6]?.value)
                else ->
                    Pair(null, null)
            }

            // 3. Apply the corresponding style and append the content
            content?.let { text ->
                when (formatType) {
                    FormatType.BOLD -> pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    FormatType.ITALIC -> pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = outlineLight))
                    FormatType.UNDERLINE -> {
                        pushStringAnnotation("URL", text)
                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = primaryLight))
                    }
                    null -> {} // Should not happen
                }
                append(text)
                pop() // End the style
            }

            // 4. Update the index to the position *after* the current full match
            currentIndex = matchResult.range.last + 1
        }

        // 5. Append any remaining text *after* the last formatted segment
        if (currentIndex < this@toAnnotatedString.length) {
            append(this@toAnnotatedString.substring(currentIndex))
        }
    }
}

@Composable
fun (() -> Unit).performHaptic(
    defaultHaptic: HapticFeedbackType = HapticFeedbackType.ContextClick,
    toggleState: Boolean? = null
): () -> Unit {
    val haptic = LocalHapticFeedback.current
    return {
        haptic.performHapticFeedback(
            toggleState?.let {
                if (it) HapticFeedbackType.ToggleOff
                else HapticFeedbackType.ToggleOn
            } ?: defaultHaptic
        )
        this.invoke()
    }
}

@Composable
fun List<ValidatableField>.validateFields(): Boolean {
    return this.all { it.validate().isValid }
}

fun Context.sendEmail(
    appName: String,
    to: String = EMAIL,
    subject: String = "Asking for help with $appName",
    body: String = "This message contains..."
) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    try {
        this.startActivity(emailIntent)
    } catch (e: Exception) {
        Log.e("sendEmail", "Error sending email: $e")
        this.showToast("Something went wrong")
    }
}

fun Context.onShare(body: String, imagePaths: List<String> = emptyList()) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        if (imagePaths.isNotEmpty()) {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imagePaths.map { it.toUri() }))
        }
        putExtra(Intent.EXTRA_TEXT, body)
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    val shareIntent = Intent.createChooser(sendIntent, "Voux")
    this.startActivity(shareIntent)
}

fun TextStyle.toGradientText(colors: List<Color> = Dimens.gradientColors1): TextStyle {
    return this.copy(brush = Brush.linearGradient(colors))
}

@Composable
fun Modifier.verticalFade(
    tint: Color = MaterialTheme.colorScheme.surface,
    alpha: Float = 0.8f
): Modifier {
    val colors = listOf(Color.Transparent, tint.copy(alpha = alpha), tint)

    return this.then(
        Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = colors,
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    )
}

@Composable
fun Modifier.boxShadow(
    shadowColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
    shape: Shape = RoundedCornerShape(Dimens.CornerRadiusExtraLarge)
): Modifier = this
    .dropShadow(
        shape = shape,
        shadow = Shadow(
            radius = Dimens.ElevationMedium,
            spread = Dimens.ElevationSmall,
            color = shadowColor
        )
    )

fun Modifier.noRippleClickable(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
): Modifier = composed {
    this.combinedClickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

fun Modifier.dashedBorder(
    brush: Brush,
) = this.drawWithContent {
    val outline = RoundedCornerShape(Dimens.CornerRadiusLarge)
        .createOutline(size, layoutDirection, density = this)
    val dashedStroke = Stroke(
        cap = StrokeCap.Round,
        width = Dimens.StrokeWidthMedium.value,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(Dimens.DashLength.value, Dimens.GapLength.value)
        )
    )

    drawContent()
    drawOutline(
        outline = outline,
        brush = brush,
        style = dashedStroke,
    )
}

fun Context.showToast(message: String) {
    if (message.trim().isNotBlank()) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.copyToClipboard(text: String) {
    // Get the ClipboardManager from the system service
    val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Create a new ClipData object.
    // The label is a user-visible description of the clip's content.
    val clipData = ClipData.newPlainText(Constants.CLIPBOARD_LABEL, text)

    // Set the clip data to the clipboard.
    clipboardManager.setPrimaryClip(clipData)

    // Toast
    this.showToast("Copied to clipboard")
}

fun Context.openUrl(urlText: String) {
    val intent = Intent(Intent.ACTION_VIEW, urlText.toUri())
    try {
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e("openUrl", "Error opening URL: $e")
        this.showToast("Something went wrong")
    }
}

fun Long.toDateTimeString(): String {
    val dateFormat = SimpleDateFormat("h:mm a dd.MM.yyyy", Locale.getDefault())
    val formattedTime = dateFormat.format(this)
    return formattedTime ?: ""
}

fun String.convertDriveUrlToDirectDownload(): String {
    return if (this.contains("drive.google.com/file/d/")) {
        val fileId = this
            .split("/file/d/").getOrNull(1)
            ?.split("/")[0]

        if (fileId != null) {
            "https://drive.google.com/uc?export=download&id=$fileId"
        } else {
            this
        }
    } else {
        this // Return original if parsing fails
    }
}

fun ClothModel.toTitle(): String {
    return "${this.material} ${this.type}".capitalize()
}

fun Double.formatPrice(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }
    return formatter.format(this)
}

@Composable
fun Double.toPriceString(): String {
    return if (this > 0.01) this.formatPrice()
        else stringResource(R.string.free)
}

fun GuidelineModel.toStandardListItemModel(): StandardListItemModel {
    return StandardListItemModel(
        id = id,
        title = title,
        description = description,
        leadingIcon = Icons.Rounded.Check
    )
}

@Composable
fun SubscriptionModel.toStandardListItemModel(index: Int = 0): StandardListItemModel {
    return StandardListItemModel(
        id = index,
        title = title,
        description = purchasedTime.toDateTimeString(),
        leadingIcon = Icons.Rounded.Subscriptions,
        endingText = price.toPriceString()
    )
}

fun DocumentSnapshot.toSubscriptionModelsList(): List<SubscriptionModel> {
    val subscriptionMaps = this.get(FirebaseConstants.SUBSCRIPTIONS) as? List<Map<String, Any>> ?: emptyList()

    return subscriptionMaps.map { map ->
        val usageDataMap = map[FirebaseConstants.MAX_USAGE_DATA] as? Map<String, Any> ?: emptyMap()
        val usageData = UsageDataModel(
            webSearches = (usageDataMap[FirebaseConstants.WEB_SEARCHES] as? Long)?.toInt() ?: 0,
            attaches = (usageDataMap[FirebaseConstants.ATTACHES] as? Long)?.toInt() ?: 0,
            messageChars = (usageDataMap[FirebaseConstants.MESSAGE_CHARS] as? Long)?.toInt() ?: 0,
            webSearchResultCount = (usageDataMap[FirebaseConstants.WEB_SEARCH_RESULT_COUNT] as? Long)?.toInt() ?: 0
        )

        SubscriptionModel(
            title = map[FirebaseConstants.TITLE] as? String ?: "",
            price = map[FirebaseConstants.PRICE] as? Double ?: 0.0,
            subscriptionType = map[FirebaseConstants.SUBSCRIPTION_TYPE] as? String ?: "",
            maxUsageData = usageData,
            adsEnabled = map[FirebaseConstants.ADS_ENABLED] as? Boolean ?: true,
            purchasedTime = map[FirebaseConstants.PURCHASED_TIME] as? Long ?: 0
        )
    }
}

fun SearchImageResponse.toImageModels(): List<ImageModel> {
    return this.images.map { imageResult ->
        ImageModel(
            imageUrl = imageResult.imageUrl,
            sourceUrl = imageResult.link
        )
    }
}

fun SearchTextResponse.toMessageModel(): MessageModel {
    // **...** content is group 2 bold
    // *...* content is group 4 italic
    // _..._ content is group 6 underline
    var text = ""
    text += if (this.knowledgeGraph.title.isNotEmpty()) { "**${this.knowledgeGraph.title}**\n" } else ""
    text += if (this.knowledgeGraph.description.isNotEmpty()) { "${this.knowledgeGraph.description}\n" } else ""

    if (this.organic.isNotEmpty()) {
        text += "\n\n- Explores:\n"
        text += this.organic.forEach { item ->
            item?.title?.let {
                text += "**${it}**\n"
            }
            item?.snippet?.let {
                text += "$it\n"
            }
            text += item?.link?.let { "_${it}_" }
        }
    }

    if (this.peopleAlsoAsk.isNotEmpty()) {
        text += "\n\n- People also ask:\n"
        text += this.peopleAlsoAsk.forEach { item ->
            item?.question?.let {
                text += "*${it}*\n"
            }
            item?.link?.let {
                text += "_${it}_\n"
            }
        }
    }

    if (this.relatedSearches.isNotEmpty()) {
        text += "\n\n- Related searches:\n"
        text += this.relatedSearches.forEach { item ->
            text += item?.query.let { "*${it}*" }
        }
    }

    val images = mutableListOf<ImageModel>()
    if (this.knowledgeGraph.imageUrl.isNotEmpty())
        images.add(ImageModel(this.knowledgeGraph.imageUrl, this.knowledgeGraph.imageUrl))

    return MessageModel(
        text = text,
        images = images
    )
}

fun Long?.isEarlierThan(expiredTime: Long?): Boolean {
    // A null 'this' (the receiver) means there's no time to compare, so it can't be earlier.
    // If 'this' is not null, the comparison can only happen if 'expiredTime' is also not null.
    // If both are not null, we compare if 'this' is strictly less than 'expiredTime'.
    return this != null && expiredTime != null && this < expiredTime
}