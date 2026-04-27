package com.fajary.todolist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fajary.todolist.ui.theme.TodolistappTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
data class InsertTaskDTO(
    var title: String,
    var isCompleted: Boolean = false,
    var deadline: String
)
data class UpdateTaskDTO(
    var title: String? = null,
    var isCompleted: Boolean? = null,
    var deadline: String? = null
)

fun SnapshotStateList<Task>.insertTask(task: InsertTaskDTO) {
    val newId = (maxOfOrNull { it.id } ?: 0) + 1
    add(Task(
        id = newId,
        title = task.title,
        isCompleted = task.isCompleted,
        deadline = task.deadline
    ))
}
fun SnapshotStateList<Task>.updateTask(id: Int, data: UpdateTaskDTO)
{
    val index = indexOfFirst { it.id == id }
    if(index != -1)
    {
        var copy = this[index].copy(
            title = data.title ?: this[index].title,
            isCompleted = data.isCompleted ?: this[index].isCompleted,
            deadline = data.deadline ?: this[index].deadline
        )

        this[index] = copy
    }
}
fun SnapshotStateList<Task>.deleteTask(id: Int)
{
    val index = indexOfFirst { it.id == id }
    if(index != -1)
    {
        this.removeAt(index);
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoAppMain()
{
    val context = LocalContext.current
    val currentColorScheme = MaterialTheme.colorScheme;
    val currentTypography = MaterialTheme.typography;
    val currentShapes = MaterialTheme.shapes;

    var todoItemsList = remember {
        mutableStateListOf<Task>(
        )
    }
    LaunchedEffect (Unit) {
        if (todoItemsList.isEmpty()) {
            todoItemsList.insertTask(
                InsertTaskDTO(title = "Pay electricity bill", deadline = "14 April 2026")
            )
            todoItemsList.insertTask(
                InsertTaskDTO(title = "Call mom", deadline = "16 April 2026")
            )
            todoItemsList.insertTask(
                InsertTaskDTO(title = "Read a book", deadline = "18 April 2026")
            )
            todoItemsList.insertTask(
                InsertTaskDTO(title = "Exercise for 30 minutes", deadline = "17 April 2026")
            )
            todoItemsList.insertTask(
                InsertTaskDTO(title = "Organize desk", deadline = "19 April 2026")
            )
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedDeadlineMillis by remember { mutableStateOf<Long?>(null) }   // store date as millis
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            Box(
                modifier = Modifier.padding(12.dp)
            )
            {
                FloatingActionButton(
                    onClick = {
                        newTaskTitle = ""
                        selectedDeadlineMillis = null
                        showAddDialog = true
                    },
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
            modifier = Modifier.padding(innerPadding),
            innerPadding = innerPadding,
            todoItemsList = todoItemsList
        )
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(
                text = "Add New Task",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            ) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
                    val deadlineDisplay = selectedDeadlineMillis?.let { dateFormat.format(Date(it)) } ?: ""

                    Button(
                        onClick = {
                            showDatePicker = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentColorScheme.onPrimary,
                            contentColor = currentColorScheme.surface
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date Picker",
                            tint = currentColorScheme.surface,
                            modifier = Modifier.size(24.dp)
                        )

                        if(selectedDeadlineMillis != null)
                        {
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = deadlineDisplay
                            )
                        }
                        else
                        {
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = "Select a Deadline"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newTaskTitle.isNotBlank() && selectedDeadlineMillis != null) {
                            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            val deadlineStr = selectedDeadlineMillis?.let { dateFormat.format(Date(it)) } ?: ""

                            todoItemsList.insertTask(
                                InsertTaskDTO(
                                    title = newTaskTitle.trim(),
                                    isCompleted = false,
                                    deadline = deadlineStr
                                )
                            )
                            showAddDialog = false
                        }
                        else
                        {
                            Toast.makeText(context, "Please fill the required form!", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Add", color = currentColorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = currentColorScheme.onPrimary
                    )
                }
            }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDeadlineMillis ?: System.currentTimeMillis()
        )

        DatePickerDialog (
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDeadlineMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("Ok", color = currentColorScheme.onPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = currentColorScheme.onPrimary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ToDoApp(modifier: Modifier = Modifier, innerPadding: PaddingValues, todoItemsList: SnapshotStateList<Task>) {
    val currentColorScheme = MaterialTheme.colorScheme;
    val currentTypography = MaterialTheme.typography;
    val currentShapes = MaterialTheme.shapes;

    val statusFilters = listOf<String>("All", "Incomplete", "Complete");
    var selectedStatusFilter by remember {
        mutableStateOf("All")
    }

    val sortFilters = listOf<String>("Ascending", "Descending");
    var selectedSortFilter by remember {
        mutableStateOf("Ascending")
    }

    val filteredToDoItemsList = todoItemsList.filter { task ->
        if(selectedStatusFilter == "Incomplete")
        {
            !task.isCompleted;
        }
        else if(selectedStatusFilter == "Complete")
        {
            task.isCompleted;
        }
        else
        {
            true;
        }
    }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun parseDeadline(deadline: String): Date? {
        return if (deadline.isBlank()) null
        else try { dateFormat.parse(deadline) } catch (e: ParseException) { null }
    }

    val deadlineComparator = Comparator<Task> { t1, t2 ->
        val date1 = parseDeadline(t1.deadline)
        val date2 = parseDeadline(t2.deadline)

        if(date1 == null || date2 == null)
        {
            0
        }
        else
        {
            date1.compareTo(date2)
        }
    }

    val sortedToDoItemsList = when (selectedSortFilter) {
        "Ascending"  -> filteredToDoItemsList.sortedWith(deadlineComparator)
        "Descending" -> filteredToDoItemsList.sortedWith(deadlineComparator.reversed())
        else         -> filteredToDoItemsList
    }

    val completedCount = todoItemsList.count { it.isCompleted }
    val totalCount = todoItemsList.size
    val progress = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
    val remainingCount = totalCount - completedCount;
    val progressPercentage = progress * 100f;

    Box(

    )
    {
        val infiniteTransition = rememberInfiniteTransition(label = "sway")
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 10000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "swayAngle"
        )

        Image(
            painter = painterResource(id = R.drawable.branch),
            contentDescription = "Branch Decoration",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.padding(
                top = 200.dp + innerPadding.calculateTopPadding()
                )
                .graphicsLayer {
                    rotationZ = angle
                    translationY = angle * 5
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                }
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
                            text = "$remainingCount Tasks remaining",
                            color = currentColorScheme.onSurface,
                            style = currentTypography.bodyLarge
                        )
                    }

                    Icon(
                        imageVector = if (remainingCount == 0) Icons.Default.CheckCircle else Icons.Default.Info,
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
                            text = String.format("%.1f%%", progressPercentage),
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
            horizontalArrangement = Arrangement.Start
        )
        {
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    if(selectedSortFilter == "Ascending")
                    {
                        selectedSortFilter = "Descending"
                    }
                    else
                    {
                        selectedSortFilter = "Ascending"
                    }
                },
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
                        modifier = Modifier.size(24.dp).rotate(if (selectedSortFilter == "Ascending") 90f else 270f)
                    )
                }
            }
        }


        LazyColumn(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedToDoItemsList)
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
                                onCheckedChange = { todoItemsList.updateTask(item.id, UpdateTaskDTO(
                                    isCompleted = !item.isCompleted
                                )) },
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
                                        todoItemsList.deleteTask(item.id)
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