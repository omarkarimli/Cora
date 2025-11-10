package com.omarkarimli.cora.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.omarkarimli.cora.BuildConfig.EMAIL
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.MessageModel
import com.omarkarimli.cora.domain.models.SearchImageResponse
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.domain.models.UsageDataModel
import com.omarkarimli.cora.domain.models.ValidatableField
import com.omarkarimli.cora.domain.models.serper.SearchTextResponse
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.outlineLight
import com.omarkarimli.cora.ui.theme.primaryLight
import com.omarkarimli.cora.utils.NotificationConstants.ACTION_SHOW_NOTIFICATION
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.graphics.Color

fun String.isWebUrl(): Boolean {
    // A check for a web URL
    return this.startsWith("http://", ignoreCase = true) ||
            this.startsWith("https://", ignoreCase = true) ||
            this.contains("www.")
}

fun String.toAnnotatedString(): AnnotatedString {

    // **...** (Group 2) -> BOLD
    // *...* (Group 4) -> ITALIC
    // [text](url) (Groups 6 and 7) -> TOP-LEVEL LINK (Tolerates spaces/newlines between ] and ( )
    // _..._ (Group 9) -> UNDERLINE/RAW URL

    val regex = Regex(
        "(?<BOLD>\\*\\*(.*?)\\*\\*)|" +
                "(?<ITALIC>\\*(.*?)\\*)|" +
                "(?<LINK>\\[(.*?)]\\s*?\\((.*?)\\))|" + // Robust link pattern (Groups 6 and 7)
                "(?<UNDERLINE>_(.*?)_)"
        , RegexOption.MULTILINE)

    return buildAnnotatedString {
        var currentIndex = 0

        regex.findAll(this@toAnnotatedString).forEach { matchResult ->
            // Append text before the match
            append(this@toAnnotatedString.substring(currentIndex, matchResult.range.first))

            val (formatType, content) = when {
                matchResult.groups["BOLD"] != null -> "BOLD" to matchResult.groups[2]?.value
                matchResult.groups["ITALIC"] != null -> "ITALIC" to matchResult.groups[4]?.value
                matchResult.groups["LINK"] != null -> "LINK" to null
                matchResult.groups["UNDERLINE"] != null -> "UNDERLINE" to matchResult.groups[9]?.value
                else -> null to null
            }

            // --- Format Application ---
            when (formatType) {
                "BOLD" -> {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(content ?: "")
                    pop()
                }
                "ITALIC" -> {
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = outlineLight))
                    append(content ?: "")
                    pop()
                }
                "LINK" -> { // Handles [text](url) - Groups 6 (text) and 7 (url)
                    val displayText = matchResult.groups[6]?.value ?: ""
                    val url = matchResult.groups[7]?.value ?: ""

                    if (url.isNotEmpty()) {
                        pushStringAnnotation("URL", url)
                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = primaryLight))

                        append(displayText.takeIf { it.isNotEmpty() } ?: url)

                        pop()
                        pop()
                    } else {
                        append(displayText.takeIf { it.isNotEmpty() } ?: matchResult.value)
                    }
                }
                "UNDERLINE" -> { // Handles raw URLs or simple underlined text (content is group 9)
                    val text = content ?: ""

                    if (text.isWebUrl()) {
                        pushStringAnnotation("URL", text)
                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))

                        val displayedText = if (text.length > 50) {
                            text.take(47) + "..."
                        } else {
                            text
                        }

                        append(displayedText)

                        pop() // style
                        pop() // annotation
                    } else { // Just underlined text
                        pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                        append(text)
                        pop()
                    }
                }
                // Fallback for BOLD, ITALIC, and UNDERLINE (when content is not null)
                else -> content?.let { append(it) }
            }

            // Update the index to after the full match
            currentIndex = matchResult.range.last + 1
        }

        // Append remaining text after the last match
        if (currentIndex < this@toAnnotatedString.length) {
            append(this@toAnnotatedString.substring(currentIndex))
        }
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
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

fun Context.onShare(
    context: Context,
    body: String = context.getString(R.string.share_body),
    imagePaths: List<String> = emptyList()
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        if (imagePaths.isNotEmpty()) {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(imagePaths.map { it.toUri() }))
        }
        putExtra(Intent.EXTRA_TEXT, body)
        type = "image/*"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.app_name))
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

fun Context.showToast(message: String) {
    if (message.trim().isNotBlank()) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.copyToClipboard(text: String) {
    val clipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(Constants.CLIPBOARD_LABEL, text)
    clipboardManager.setPrimaryClip(clipData)
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
    return dateFormat.format(this)
}

@Composable
fun Double.toPriceString(): String {
    return if (this == 0.0) stringResource(R.string.free) else "$$this"
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
    return this.images.mapNotNull { imageResult ->
        if (imageResult.imageUrl.isNotBlank()) {
            ImageModel(imageUrl = imageResult.imageUrl, sourceUrl = imageResult.link)
        } else null
    }
}

fun String.returnIfAvailable(): String {
    val replacedText = this
        .replace("*", "")
        .replace("_", "")
        .replace("[", "")
        .replace("]", "")
        .replace("(", "")
        .replace(")", "")

    return if (replacedText.trim().isNotBlank()
        && replacedText.any { it.isLetter() }) this
    else ""
}

fun SearchTextResponse.toMessageModel(maxResult: Int = 1): MessageModel {
    val text = buildString {
        append("**${knowledgeGraph.title}**\n".returnIfAvailable())
        append("${knowledgeGraph.description}\n".returnIfAvailable())

        if (organic.isNotEmpty()) {
            append("\n\n**Explores:**\n")
            organic
                .take(maxResult)
                .forEach { item ->
                append("\n")
                append("**${item.title}**\n".returnIfAvailable())
                append("${item.snippet}\n".returnIfAvailable())

                if (item.link.isNotBlank()) append("_[${item.title}](${item.link})_\n".returnIfAvailable())
            }
        }

        if (peopleAlsoAsk.isNotEmpty()) {
            append("\n\n**People also ask:**\n")
            peopleAlsoAsk
                .take(maxResult)
                .forEach { item ->
                append("_[${item.question}](${item.link})_\n".returnIfAvailable())
            }
        }

        if (relatedSearches.isNotEmpty()) {
            append("\n\n**Related searches:**\n")
            relatedSearches
                .take(maxResult)
                .forEach { item ->
                append("*${item.query}*\n".returnIfAvailable())
            }
        }
    }

    var images = mutableListOf<ImageModel>()
    if (knowledgeGraph.imageUrl.isNotBlank()) {
        images.add(ImageModel(knowledgeGraph.imageUrl, knowledgeGraph.imageUrl))
        images = images.take(maxResult).toMutableList()
    }

    return MessageModel(
        text = text.trim(),
        images = images
    )
}

fun Long?.isEarlierThan(expiredTime: Long?): Boolean {
    return this != null && expiredTime != null && this < expiredTime
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts(Constants.PACKAGE, this.packageName, null)
    intent.data = uri
    this.startActivity(intent)
}

fun Context.getActionShowNotification(): String {
    return this.packageName + ".$ACTION_SHOW_NOTIFICATION"
}