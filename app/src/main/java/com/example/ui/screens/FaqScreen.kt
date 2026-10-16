package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FaqAndGuidesData
import com.example.model.FaqItem
import com.example.ui.components.SkeuoCard
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SkyBlue100
import com.example.ui.theme.SkyBlue50
import com.example.ui.theme.SkyBlue600
import com.example.ui.theme.SkyBlue700
import com.example.ui.theme.SkyBlue900
import com.example.ui.theme.SlateMuted
import com.example.ui.theme.SlateText

@Composable
fun FaqScreen() {
    val faqList = FaqAndGuidesData.faqList

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(SkyBlue100, SkyBlue50, PureWhite)
                )
            ),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = SkyBlue600,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pertanyaan Sering Diajukan (FAQ)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SkyBlue900
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Informasi lengkap mengenai keaslian voucher, redeem game, dan operasional RzzInfo.",
                        fontSize = 12.sp,
                        color = SlateMuted
                    )
                }
            }
        }

        // FAQ Items Accordion
        items(faqList, key = { it.id }) { item ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                FaqAccordionItem(item = item)
            }
        }
    }
}

@Composable
private fun FaqAccordionItem(item: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    SkeuoCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("faq_item_${item.id}"),
        elevation = if (isExpanded) 4.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isExpanded) SkyBlue700 else SlateText,
                    modifier = Modifier.weight(1f),
                    lineHeight = 20.sp
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Tutup" else "Buka",
                    tint = SkyBlue600,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = SkyBlue100)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = item.answer,
                        fontSize = 13.sp,
                        color = SlateText,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
