package com.kravchenko.denys.githubviewer.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kravchenko.denys.githubviewer.BuildConfig
import com.kravchenko.denys.githubviewer.R

private const val enableSignInThreshold = 2

@Composable
fun SignInScreen(
    screenModifier: Modifier,
    onClick: () -> Unit
) {
    var githubToken by remember { mutableStateOf(BuildConfig.GITHUB_TOKEN) }
    var clickButtonState by remember { mutableStateOf(false) }

    Row(

        modifier = screenModifier.padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1f).padding(end = 10.dp),
            value = githubToken,
            onValueChange = {
                clickButtonState = it.length > enableSignInThreshold
                githubToken = it
            },
            label = { Text(stringResource(R.string.type_github_token)) },
            singleLine = true
        )

        Button(onClick = onClick) {
            Text(text = stringResource(R.string.sign_in))
        }
    }
}