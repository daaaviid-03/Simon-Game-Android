package com.example.simongame.game

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simongame.ConfirmExitDialogInformationEndGame
import com.example.simongame.LEVEL_NAME
import com.example.simongame.UpperBarControl
import com.example.simongame.confirmExitDialog
import com.example.simongame.db.DBViewModel

/**
 * End game screen layout
 */
@Composable
fun EndGameState(
    context: Context,
    dbVM: DBViewModel,
    sequenceLen: Int,
    difficulty: Int,
    onclick: (name: String) -> Unit
) {
    // list of recent nicknames from db
    var recentRecordNames by rememberSaveable {
        mutableStateOf(
            listOf<String>()
        )
    }
    // observe last 5 names from db
    dbVM.last5Names.observe(LocalContext.current as ComponentActivity) {
        recentRecordNames = it!!
    }
    // actual nickname selected
    val userName = rememberSaveable { mutableStateOf("") }
    // whether to display error message or not
    val withError = rememberSaveable { mutableStateOf(false) }

    /**
     * confirm nickname check for empty values
     */
    val tryToConfirm: (name: String) -> Unit = {
        if (it.isNotEmpty())
            onclick(it)
        else
            withError.value = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        UpperBarControl(context, "GAME OVER") {
            confirmExitDialog(context, ConfirmExitDialogInformationEndGame)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(5.dp))
            // display sequence length obtained
            Text(
                text = "You have reached $sequenceLen steps.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            // display difficulty level
            Text(
                text = "Playing at: ${LEVEL_NAME[difficulty - 1]} level.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Choose your nickname to save the record:", fontSize = 16.sp)
            NickNameEntry(userName, tryToConfirm)
            // error message
            if (withError.value) {
                Text(
                    text = "You can't leave the field empty. Please enter a nickname",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Recent nicknames used:", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            // list of recent nicknames used
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(recentRecordNames) {
                    Text(
                        text = it,
                        fontSize = 30.sp,
                        modifier = Modifier.clickable {
                            userName.value = it
                        }
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(10.dp)
            ) {
                // cancel button
                Button(
                    onClick = {
                        confirmExitDialog(context, ConfirmExitDialogInformationEndGame)
                    },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Don't Save", fontSize = 20.sp)
                }
                // save button
                Button(
                    onClick = {
                        tryToConfirm(userName.value)
                    },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text("Save", fontSize = 20.sp)
                }
            }
        }
    }
}

// nickname entry layout
@Composable
fun NickNameEntry(userName: MutableState<String>, onclick: (userNameStr: String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = userName.value,
        onValueChange = {
            userName.value = it
        },
        placeholder = {
            Text(text = "Nickname")
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusManager.clearFocus()
            onclick(userName.value)
        }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        trailingIcon = {
            Icon(
                Icons.Rounded.Done,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp, 24.dp)
                    .clickable {
                        keyboardController?.hide()
                        onclick(userName.value)
                    }
            )
        }
    )
}