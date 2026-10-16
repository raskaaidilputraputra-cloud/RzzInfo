package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Skeuomorphic Card with embossed 3D tactile elevation, realistic soft dual shadows,
 * glossy highlight rim, and crisp rounded corners.
 */
@Composable
fun SkeuoCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = CardSurfaceWhite,
    borderColor: Color = SkyBlue200,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = SkeuoShadowDark,
                spotColor = SkeuoShadowDeep
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PureWhite,
                        borderColor.copy(alpha = 0.6f)
                    )
                ),
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PureWhite,
                        backgroundColor
                    )
                ),
                shape = shape
            )
            .clip(shape)
    ) {
        content()
    }
}

/**
 * Skeuomorphic Tactile Button with 3D extruded appearance, glossy bevel highlight,
 * realistic push-down scale & shadow spring feedback on tap.
 */
@Composable
fun SkeuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "skeuo_button",
    icon: ImageVector? = null,
    isPrimary: Boolean = true,
    enabled: Boolean = true,
    backgroundColor: Color = if (isPrimary) SkyBlue600 else PureWhite,
    contentColor: Color = if (isPrimary) PureWhite else SkyBlue700
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .testTag(testTag)
            .shadow(
                elevation = if (isPressed) 2.dp else 5.dp,
                shape = shape,
                ambientColor = if (isPrimary) SkyBlue700.copy(alpha = 0.35f) else SkeuoShadowDark,
                spotColor = if (isPrimary) SkyBlue900.copy(alpha = 0.5f) else SkeuoShadowDeep
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = if (isPrimary) listOf(SkyBlue400, SkyBlue700)
                    else listOf(PureWhite, SkyBlue200)
                ),
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPrimary) {
                        listOf(
                            SkyBlue400,
                            backgroundColor,
                            SkyBlue700
                        )
                    } else {
                        listOf(
                            PureWhite,
                            SkyBlue50,
                            SkyBlue100
                        )
                    }
                ),
                shape = shape
            )
            .clip(shape)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            onClick()
                        }
                    )
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 0.3.sp
            )
        }
    }
}

/**
 * Skeuomorphic Coupon Code Ticket Slot with perforated coupon border look,
 * embossed code text, and one-tap copy button with instant success feedback.
 */
@Composable
fun CouponSlot(
    code: String,
    onCopied: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "KODE REDEEM / VOUCHER"
) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, shape, ambientColor = SkeuoShadowDark, spotColor = SkeuoShadowDeep)
            .border(
                width = 1.dp,
                color = SkyBlue300,
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SkyBlue50,
                        SkyBlue100.copy(alpha = 0.7f)
                    )
                ),
                shape = shape
            )
            .clip(shape)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyBlue700,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (code.isNotBlank()) code else "Klaim Langsung via Link",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = SlateText,
                    letterSpacing = 1.2.sp
                )
            }

            if (code.isNotBlank()) {
                val buttonShape = RoundedCornerShape(10.dp)
                Box(
                    modifier = Modifier
                        .shadow(1.dp, buttonShape)
                        .border(1.dp, if (copied) Color(0xFF10B981) else SkyBlue400, buttonShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (copied) {
                                    listOf(Color(0xFF34D399), Color(0xFF059669))
                                } else {
                                    listOf(SkyBlue500, SkyBlue600)
                                }
                            ),
                            shape = buttonShape
                        )
                        .clip(buttonShape)
                        .clickable {
                            clipboardManager.setText(AnnotatedString(code))
                            copied = true
                            onCopied(code)
                            scope.launch {
                                delay(2000)
                                copied = false
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (copied) Icons.Default.CheckCircle else Icons.Default.ContentCopy,
                            contentDescription = "Salin Kode",
                            tint = PureWhite,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (copied) "Disalin!" else "Salin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}

/**
 * Skeuomorphic Embossed Pill Badge for Categories and Status
 */
@Composable
fun SkeuoBadge(
    text: String,
    badgeColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .shadow(1.dp, shape, ambientColor = SkeuoShadowDark)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(PureWhite.copy(alpha = 0.8f), badgeColor.copy(alpha = 0.5f))
                ),
                shape = shape
            )
            .background(
                brush = Brush.verticalGradient(
                    listOf(badgeColor.copy(alpha = 0.15f), badgeColor.copy(alpha = 0.28f))
                ),
                shape = shape
            )
            .clip(shape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
