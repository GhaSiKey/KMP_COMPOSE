package com.gaoshiqi.kmp.ui.lenticular

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * 倾斜方向可视化组件
 *
 * 圆形区域内的圆点，位置映射：
 * - roll → X 轴（向右倾斜 = 圆点右移）
 * - pitch → Y 轴（向前倾斜 = 圆点上移）
 *
 * 外圈标注 ±90° 范围，十字线标注原点，中间圆环标注 45°。
 *
 * @param pitch 前后倾斜角度（-90° ~ +90°）
 * @param roll 左右倾斜角度（-90° ~ +90°）
 * @param modifier Modifier
 * @param dotColor 圆点颜色
 * @param guideColor 参考线和外框颜色
 * @param labelColor 轴向标签颜色
 */
@Composable
fun TiltVisualizer(
    pitch: Float,
    roll: Float,
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    guideColor: Color = MaterialTheme.colorScheme.outlineVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // 外圆边框
            drawCircle(
                color = guideColor,
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // 十字线
            drawLine(
                color = guideColor,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = guideColor,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 1.dp.toPx()
            )

            // 中间圆环（45° 参考线）
            drawCircle(
                color = guideColor.copy(alpha = 0.3f),
                radius = radius / 2f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )

            // 指示圆点：将 ±90° 映射到 ±radius
            val maxAngle = 90f
            val dotX = center.x + (roll / maxAngle) * radius
            val dotY = center.y - (pitch / maxAngle) * radius // Y 轴反转：向前倾=上移

            drawCircle(
                color = dotColor,
                radius = 12.dp.toPx(),
                center = Offset(dotX, dotY)
            )

            // 圆点到中心的连线
            drawLine(
                color = dotColor.copy(alpha = 0.4f),
                start = center,
                end = Offset(dotX, dotY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 轴向标签
        Text(
            text = "pitch+",
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
        )
        Text(
            text = "pitch-",
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
        )
        Text(
            text = "roll-",
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
        )
        Text(
            text = "roll+",
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp)
        )
    }
}
