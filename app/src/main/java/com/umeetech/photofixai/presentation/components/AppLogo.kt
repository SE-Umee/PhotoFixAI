package com.umeetech.photofixai.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.umeetech.photofixai.R
import com.umeetech.photofixai.presentation.theme.Dimens

/**
 * The PhotoFix AI brand mark. Two variants share one source of truth
 * (the [R.drawable.ic_app_logo] / [R.drawable.ic_app_logo_foreground] vectors):
 *
 *  - [AppLogo]           the full gradient badge (great on light surfaces / top bars)
 *  - [AppLogoMark]       the white photo-card mark only (great on brand-color surfaces)
 *  - [AppLogoWordmark]   badge + "PhotoFix AI" wordmark for headers/branding
 */
@Composable
fun AppLogo(modifier: Modifier = Modifier, size: Dp = 40.dp) {
    Image(
        painter = painterResource(R.drawable.ic_app_logo),
        contentDescription = "PhotoFix AI",
        modifier = modifier.size(size)
    )
}

@Composable
fun AppLogoMark(modifier: Modifier = Modifier, size: Dp = 96.dp) {
    Image(
        painter = painterResource(R.drawable.ic_app_logo_foreground),
        contentDescription = "PhotoFix AI",
        modifier = modifier.size(size)
    )
}

@Composable
fun AppLogoWordmark(
    modifier: Modifier = Modifier,
    logoSize: Dp = 40.dp,
    textColor: Color = Color.Unspecified
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM)
    ) {
        AppLogo(size = logoSize)
        Text(
            text = "PhotoFix AI",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
