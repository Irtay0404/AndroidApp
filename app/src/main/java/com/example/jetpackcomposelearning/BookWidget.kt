package com.example.jetpackcomposelearning

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class BookWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val books = listOf(
            R.drawable.harrypotter,
            R.drawable.winr,
            R.drawable.arka
        )
        val randomBook = books[Random.nextInt(books.size)]

        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Случайная книга",
                    style = TextStyle(color = ColorProvider(R.color.black))
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Image(
                    provider = ImageProvider(randomBook),
                    contentDescription = "Обложка книги",
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Button(
                    text = "Обновить",
                    onClick = actionRunCallback<UpdateBookAction>()
                )
            }
        }
    }
}

class UpdateBookAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        BookWidget().updateAll(context)
    }
}

class BookWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = BookWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == intent.action) {
            CoroutineScope(Dispatchers.IO).launch {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(BookWidget::class.java)
                glanceIds.forEach { glanceAppWidget.update(context, it) }
            }
        }
    }
}
