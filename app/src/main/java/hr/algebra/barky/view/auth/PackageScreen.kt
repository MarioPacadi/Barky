package hr.algebra.barky.view.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
import hr.algebra.barky.state.PackageState

@Composable
fun PackageScreen(packageState: PackageState, modifier: Modifier=Modifier){

    val options = packageState.packages
    var expanded by remember { mutableStateOf(false) }
    //var selected by remember { mutableStateOf(packageState.selected) }

    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    val icon = if (expanded)
        Icons.Filled.ArrowDropUp //it requires androidx.compose.material:material-icons-extended
    else
        Icons.Filled.ArrowDropDown


    Column() {
        OutlinedTextField(
            value = "${packageState.selected.Name!!} (${packageState.selected.Price} €)",
            onValueChange = { packageState.selected = options.find { p -> p.Name.equals(it) }!! },
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textfieldSize = coordinates.size.toSize()
                },
            label = {Text("Package")},
            trailingIcon = {
                Icon(icon,"contentDescription",
                    modifier.clickable { expanded = !expanded })
            },
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color(packageState.selected.Color),
                backgroundColor = Color.Unspecified
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            options.forEach { label ->
                DropdownMenuItem(onClick = {
                    packageState.selected = label
                    expanded=false
                }) {
                    Text(text = "${label.Name!!} (${label.Price} €)")
                }
            }
        }
    }
}