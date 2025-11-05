package com.omarkarimli.cora.utils

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BubbleChart
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.omarkarimli.cora.BuildConfig.EMAIL
import com.omarkarimli.cora.R
import com.omarkarimli.cora.domain.models.CarouselModel
import com.omarkarimli.cora.domain.models.CategoryModel
import com.omarkarimli.cora.domain.models.ClothModel
import com.omarkarimli.cora.domain.models.GuidelineModel
import com.omarkarimli.cora.domain.models.ImageModel
import com.omarkarimli.cora.domain.models.ItemAnalysisModel
import com.omarkarimli.cora.domain.models.JournalModel
import com.omarkarimli.cora.domain.models.SearchImageResponse
import com.omarkarimli.cora.domain.models.StandardListItemModel
import com.omarkarimli.cora.domain.models.SubscriptionModel
import com.omarkarimli.cora.ui.theme.Dimens
import com.omarkarimli.cora.ui.theme.Durations
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
                    FormatType.UNDERLINE -> pushStyle(SpanStyle(textDecoration = TextDecoration.Underline, color = primaryLight))
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

fun Modifier.animatedGradientBorder(
    enabled: Boolean = true,
    cornerRadius: Dp = Dimens.CornerRadiusExtraLarge + Dimens.CornerRadiusMedium,
    strokeWidth: Dp = Dimens.StrokeWidthLarge,
    durationMillis: Int = Durations.GRADIENT_ANIM,
    gradientColors: List<Color> = Dimens.gradientColors1
) = composed {
    if (enabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "gradientTransition")
        val animatedProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "gradientProgress"
        )

        this.then(
            Modifier
                .drawWithContent {
                    val brush = Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset(size.width * animatedProgress, 0f),
                        end = Offset(size.width * (1 - animatedProgress), size.height)
                    )

                    drawRoundRect(
                        brush = brush,
                        style = Stroke(width = strokeWidth.toPx()),
                        cornerRadius = CornerRadius(
                            x = cornerRadius.toPx(),
                            y = cornerRadius.toPx()
                        )
                    )

                    drawContent()
                }
        )
    } else {
        this.then(Modifier)
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
fun Modifier.shimmer(cornerRadius: Dp = Dimens.ZeroDp): Modifier {
    val tint = MaterialTheme.colorScheme.onSurface

    val shimmerColors = listOf(
        tint.copy(alpha = 0.1f),
        tint.copy(alpha = 0.3f),
        tint.copy(alpha = 0.1f)
    )

    val animationRange = remember { mutableStateOf(0f to 0f) }
    val (initialX, singleTargetX) = animationRange.value

    val transition = rememberInfiniteTransition(label = "Shimmer")

    val doubleTargetX = singleTargetX + (singleTargetX - initialX)

    val translateAnim by if (singleTargetX != 0f) {
        transition.animateFloat(
            initialValue = initialX,
            targetValue = doubleTargetX,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = Durations.SHIMMER * 2,
                    easing = LinearEasing
                )
            )
        )
    } else {
        // Return a stable initial value before the size is known
        remember { mutableFloatStateOf(0f) }
    }

    return this
        // 1. Capture the size (in pixels) and update the animation range state.
        .onSizeChanged { layoutCoordinates ->
            val contentWidth = layoutCoordinates.width.toFloat()

            // Your original gradient width definition
            val gradientWidth = contentWidth / 1.5f

            // Dynamic start: fully off-screen left
            val newInitialX = -gradientWidth
            // Dynamic end for ONE sweep: fully off-screen right
            val newSingleTargetX = contentWidth + gradientWidth

            // Update the state only if values change to prevent unnecessary recomposition
            if (animationRange.value.first != newInitialX || animationRange.value.second != newSingleTargetX) {
                animationRange.value = newInitialX to newSingleTargetX
            }
        }
        // 2. Use drawWithCache with the resolved animation value.
        .drawWithCache {
            val contentWidth = size.width
            val gradientWidth = contentWidth / 1.5f

            val brush = Brush.linearGradient(
                colors = shimmerColors,
                // The animated value smoothly drives the start position
                start = Offset(translateAnim, 0f),
                // The end offset defines the physical width of the *visible* gradient band
                end = Offset(translateAnim + gradientWidth, size.height)
            )
            val cornerPx = cornerRadius.toPx()

            onDrawWithContent {
                // Step 1: Draw the original content first
                drawContent()

                // Step 2: Then, draw the shimmering effect on top
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(cornerPx, cornerPx),
                    size = size,
                    alpha = 0.8f
                )
            }
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
            ?.split("/")?.getOrNull(0)

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

fun ClothModel.toSubtitle(): String {
    return "${this.color} | ${this.size}".capitalize()
}

fun List<ImageModel>.toSharedUrls(): String {
    return if (this.isEmpty()) {
        return ""
    } else {
        this.fold("") { acc, image ->
            acc + image.imageUrl + "\n" + image.sourceUrl + "\n"
        }
    }
}

fun ItemAnalysisModel.toSharedText(): String {
    return "Check out: ${this.title}\n\n"
        .plus(this.parts.fold("") { acc, cloth ->
            acc + "${cloth.toTitle()} - ${cloth.toSubtitle()}\n" + cloth.imageModels.toSharedUrls()
        })
}

fun ClothModel.toPriceString(): String {
    return if (this.price.toDoubleOrNull() != null
        && this.price.toDouble() > 0.0) {
        "$${this.price}"
    } else {
        ""
    }
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

fun ItemAnalysisModel.toTotalPriceString(): String {
    val totalPrice = this.parts.fold(0.0) { acc, part ->
        acc.plus(
            if (part.price.toDoubleOrNull() != null && part.price.toDouble() > 0.0) {
                part.price.toDouble()
            } else {
                0.0
            }
        )
    }

    return if (totalPrice > 0) {
        val priceString = if (totalPrice % 1.0 == 0.0) {
            totalPrice.toInt().toString()
        } else {
            totalPrice.toString()
        }
        // Add the dollar sign here
        "$$priceString"
    } else {
        ""
    }
}

fun List<CategoryModel>.toCarouselItems(): List<CarouselModel> {
    return this.mapNotNull { category ->
        // Use getOrNull() to safely access the first image model
        val imageModel = category.imageModels.getOrNull(0)

        // If there's no image model, this category will be skipped by mapNotNull
        if (imageModel != null) {
            CarouselModel(
                id = category.id,
                title = category.title,
                imagePath = imageModel.imageUrl
            )
        } else {
            null // Return null to skip this category
        }
    }
}

fun JournalModel.toStandardListItemModel(): StandardListItemModel {
    return StandardListItemModel(
        id = id,
        title = title,
        description = description,
        images = images,
        leadingIcon = Icons.Rounded.BubbleChart
    )
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