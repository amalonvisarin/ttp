package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Calculator", "Converters")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(top = 48.dp, bottom = 16.dp)
            ) {
                Text(
                    text = "Colorful Calculator",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                PrimaryTabRow(
                    selectedTabIndex = selectedTabIndex,
                    indicator = { tabPositions ->
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTabIndex])
                                .clip(MaterialTheme.shapes.large)
                        )
                    ),
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { selectedTabIndex = index },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when (selectedTabIndex) {
                0 -> CalculatorScreen(modifier = Modifier.fillMaxSize())
                else -> ConverterScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var displayText by remember { mutableStateOf("0") }
    var storedValue by remember { mutableStateOf<Double?>(null) }
    var currentOperation by remember { mutableStateOf<((Double, Double) -> Double)?>(null) }
    var shouldResetInput by remember { mutableStateOf(false) }

    val buttons = listOf(
        listOf("C", "⌫", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    Column(
        modifier = modifier
            .padding(24.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .padding(24.dp)
        ) {
            Text(
                text = displayText,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { label ->
                    CalculatorButton(
                        label = label,
                        modifier = Modifier.weight(
                            if (label == "0" && row.size == 3) 2f else 1f
                        )
                    ) {
                        when (label) {
                            "C" -> {
                                displayText = "0"
                                storedValue = null
                                currentOperation = null
                                shouldResetInput = false
                            }

                            "⌫" -> {
                                displayText = if (displayText.length > 1) {
                                    displayText.dropLast(1)
                                } else {
                                    "0"
                                }
                            }

                            "÷", "×", "−", "+" -> {
                                val currentValue = displayText.toDoubleOrNull() ?: 0.0
                                if (storedValue != null && currentOperation != null && !shouldResetInput) {
                                    val result = currentOperation?.invoke(storedValue!!, currentValue) ?: currentValue
                                    storedValue = result
                                    displayText = formatResult(result)
                                } else {
                                    storedValue = currentValue
                                }
                                currentOperation = when (label) {
                                    "÷" -> { a, b -> a / b }
                                    "×" -> { a, b -> a * b }
                                    "−" -> { a, b -> a - b }
                                    "+" -> { a, b -> a + b }
                                    else -> null
                                }
                                shouldResetInput = true
                            }

                            "=" -> {
                                val currentValue = displayText.toDoubleOrNull() ?: 0.0
                                val result = if (storedValue != null && currentOperation != null) {
                                    currentOperation?.invoke(storedValue!!, currentValue)
                                } else currentValue
                                result?.let {
                                    displayText = formatResult(it)
                                    storedValue = it
                                }
                                shouldResetInput = true
                            }

                            "." -> {
                                if (!displayText.contains(".")) {
                                    displayText += "."
                                    shouldResetInput = false
                                }
                            }

                            else -> {
                                if (shouldResetInput || displayText == "0") {
                                    displayText = label
                                    shouldResetInput = false
                                } else {
                                    displayText += label
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val (containerColor, contentColor) = when (label) {
        "C", "⌫" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "÷", "×", "−", "+", "=" -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        Text(text = label, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PercentConverterCard()
        WeightConverterCard()
        LengthConverterCard()
    }
}

@Composable
private fun PercentConverterCard() {
    var percentInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }

    ConversionCard(title = "Percent Calculator", accentColor = MaterialTheme.colorScheme.primary) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ConversionTextField(
                label = "Percent (e.g. 20)",
                value = percentInput,
                onValueChange = { percentInput = it.filterInput() }
            )
            ConversionTextField(
                label = "Value (e.g. 150)",
                value = valueInput,
                onValueChange = { valueInput = it.filterInput() }
            )
            val percent = percentInput.toDoubleOrNull()
            val value = valueInput.toDoubleOrNull()
            val result = if (percent != null && value != null) value * percent / 100 else null
            ResultRow(resultText = result?.let { formatResult(it) } ?: "—")
        }
    }
}

@Composable
private fun WeightConverterCard() {
    var poundsInput by remember { mutableStateOf("") }

    ConversionCard(title = "Pounds → Kilograms", accentColor = MaterialTheme.colorScheme.secondary) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ConversionTextField(
                label = "Pounds",
                value = poundsInput,
                onValueChange = { poundsInput = it.filterInput() }
            )
            val pounds = poundsInput.toDoubleOrNull()
            val kilograms = pounds?.let { it * 0.45359237 }
            ResultRow(resultText = kilograms?.let { formatResult(it) } ?: "—", unit = "kg")
        }
    }
}

@Composable
private fun LengthConverterCard() {
    var decimetersInput by remember { mutableStateOf("") }

    ConversionCard(title = "Decimeters → Centimeters", accentColor = MaterialTheme.colorScheme.tertiary) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ConversionTextField(
                label = "Decimeters",
                value = decimetersInput,
                onValueChange = { decimetersInput = it.filterInput() }
            )
            val decimeters = decimetersInput.toDoubleOrNull()
            val centimeters = decimeters?.let { it * 10 }
            ResultRow(resultText = centimeters?.let { formatResult(it) } ?: "—", unit = "cm")
        }
    }
}

@Composable
private fun ConversionCard(
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.15f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ConversionTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
private fun ResultRow(resultText: String, unit: String = "") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Result", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = if (unit.isNotBlank() && resultText != "—") "$resultText $unit" else resultText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun String.filterInput(): String {
    return buildString {
        var hasDecimal = false
        for (char in this@filterInput) {
            when {
                char.isDigit() -> append(char)
                char == '.' && !hasDecimal -> {
                    append(char)
                    hasDecimal = true
                }
            }
        }
    }
}

private fun formatResult(value: Double): String {
    val rounded = round(value * 1_000_000) / 1_000_000
    return if (rounded % 1.0 == 0.0) {
        rounded.toLong().toString()
    } else {
        rounded.toString()
    }
}
