package xyz.akimlc.themetool.ui.compoent

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.TextField


@Composable
fun LabeledTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Done,
    onDone: () -> Unit = {}
){
    val focusManager = LocalFocusManager.current

    TextField(
        label = label,
        useLabelAsPlaceholder = true,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone()
                focusManager.clearFocus()
            }
        )
    )
}