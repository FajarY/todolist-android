package com.fajary.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fajary.todolist.ui.theme.TodolistappTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodolistappTheme(dynamicColor = false) {
                ToDoAppMain()
            }
        }
    }
}

data class Task(
    var id: Int,
    var title: String,
    var isCompleted: Boolean = false,
    var deadline: String
)

@Composable
fun ToDoAppMain()
{
    val currentColorScheme = MaterialTheme.colorScheme;
    val currentTypography = MaterialTheme.typography;
    val currentShapes = MaterialTheme.shapes;

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Box(
                modifier = Modifier.padding(12.dp)
            )
            {
                FloatingActionButton(
                    onClick = {  },
                    containerColor = currentColorScheme.onPrimary,
                    contentColor = currentColorScheme.surface,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        }

    ) { innerPadding ->
        ToDoApp(
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ToDoApp(modifier: Modifier = Modifier) {
    val currentColorScheme = MaterialTheme.colorScheme;
    val currentTypography = MaterialTheme.typography;
    val currentShapes = MaterialTheme.shapes;

    var progress by remember {
        mutableFloatStateOf(0.5f)
    }

    var selectedStatusFilter by remember {
        mutableStateOf("All")
    }
    val statusFilters = listOf<String>("All", "Incomplete", "Complete");

    var todoItemsList = remember {
        mutableListOf(
            Task(1, "Buy groceries", deadline = "27 Apr 2026"),
            Task(2, "Finish report", isCompleted = true, deadline = "25 Apr 2026"),
            Task(3, "Walk the dog", deadline = "27 Apr 2026"),
            Task(4, "Read 20 pages", deadline = "27 Apr 2026")
        )
    }

    Column(
        modifier = modifier.padding(24.dp)
    )
    {
        Box(
            modifier = Modifier.background(
                color = currentColorScheme.surface,
                shape = currentShapes.medium
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            )
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Today's Focus",
                            color = currentColorScheme.onPrimary,
                            style = currentTypography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "4 Tasks remaining",
                            color = currentColorScheme.onSurface,
                            style = currentTypography.bodyLarge
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Focus Illustration",
                        modifier = Modifier.size(48.dp),
                        tint = currentColorScheme.onPrimary
                    )
                }

                Column(
                    modifier = Modifier.padding(top = 24.dp)
                )
                {
                    Row(

                    ) {
                        Text(
                            text = "Progress",
                            color = currentColorScheme.onBackground,
                            style = currentTypography.bodyLarge
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "100%",
                            color = currentColorScheme.onPrimary,
                            style = currentTypography.bodyLarge
                        )
                    }
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        color = currentColorScheme.onPrimary,
                        trackColor = currentColorScheme.surfaceVariant
                    )
                    {

                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxHeight().fillMaxWidth()
            )
            {
                Row (
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth(),
                ) {
                    statusFilters.forEachIndexed { index, status ->
                        val shape = when {
                            index == 0
                                -> RoundedCornerShape(
                                topStart = 12.dp,
                                bottomStart = 12.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            )
                            index == statusFilters.size - 1
                                -> RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 12.dp,
                                bottomEnd = 12.dp
                            )
                            else
                                -> RectangleShape
                        }

                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            label = { Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = status,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            ) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLabelColor = MaterialTheme.colorScheme.surface,

                                ),
                            shape = shape
                        )
                    }
                }
            }


        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        )
        {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentColorScheme.onPrimary,
                    contentColor = currentColorScheme.surface
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                shape = currentShapes.medium
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Text(
                        text = "Sort",
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        fontWeight = FontWeight.Bold,
                        color = currentColorScheme.surface
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Sort Button",
                        modifier = Modifier.size(24.dp).rotate(90f)
                    )
                }
            }
        }


        LazyColumn(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(todoItemsList)
            {
                    item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = currentColorScheme.surface,
                            shape = currentShapes.medium
                        )
                )
                {
                    Box(
                        modifier = Modifier.padding(12.dp)
                    )
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isCompleted,
                                onCheckedChange = { item.isCompleted = true },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = currentColorScheme.onPrimary,
                                    checkmarkColor = currentColorScheme.surface,
                                    uncheckedColor = currentColorScheme.onPrimary
                                )
                            )
                            Column(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    color = currentColorScheme.onPrimary
                                )
                                Text(
                                    text = item.deadline,
                                    color = currentColorScheme.onBackground,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(
                                modifier = Modifier.weight(1f)
                            )

                            Box(
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            {
                                IconButton(
                                    onClick = {

                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Button",
                                        modifier = Modifier.size(24.dp),
                                        tint = currentColorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoAppPreview() {
    TodolistappTheme(dynamicColor = false) {
        ToDoAppMain()
    }
}