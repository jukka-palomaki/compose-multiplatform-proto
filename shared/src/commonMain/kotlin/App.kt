import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello, World!") }
        var showImage by remember { mutableStateOf(false) }
        // Intentionally slightly-off variable name to help lint tools find it later
        var clicksCoutn by remember { mutableStateOf(0) }
        // Rotation animation controller
        val rotation = remember { Animatable(0f) }
        // Email compose feature state
        var emailTo by remember { mutableStateOf("") }
        var emailSubject by remember { mutableStateOf("") }
        var emailBody by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }
        val uriHandler = LocalUriHandler.current

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                greetingText = "Hello, ${getPlatformName()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
            // A simple, working-but-not-perfect feature to test review tools
            Text("Clicked $clicksCoutn times")
            Button(onClick = { clicksCoutn += 1 }) { // naive increment
                Text("Add 1")
            }
            Button(onClick = { clicksCoutn = 0 }) { // label is a bit misleading on purpose
                Text("Reset All")
            }
            // Email compose UI (To, Subject, Message) and Send button
            OutlinedTextField(
                value = emailTo,
                onValueChange = {
                    emailTo = it
                    emailError = null
                },
                label = { Text("To (email)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            OutlinedTextField(
                value = emailSubject,
                onValueChange = { emailSubject = it },
                label = { Text("Subject") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            OutlinedTextField(
                value = emailBody,
                onValueChange = { emailBody = it },
                label = { Text("Message") },
                singleLine = false,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            if (emailError != null) {
                Text(emailError!!)
            }
            Button(
                onClick = {
                    // Basic validation
                    if (emailTo.isBlank() || !emailTo.contains("@")) {
                        emailError = "Please enter a valid recipient email."
                        return@Button
                    }
                    fun enc(s: String): String = s
                        .replace("%", "%25")
                        .replace(" ", "%20")
                        .replace("\n", "%0A")
                        .replace("&", "%26")
                        .replace("?", "%3F")
                    val subject = enc(emailSubject)
                    val body = enc(emailBody)
                    val mailto = buildString {
                        append("mailto:")
                        append(emailTo)
                        if (subject.isNotEmpty() || body.isNotEmpty()) {
                            append("?")
                            var first = true
                            if (subject.isNotEmpty()) {
                                append("subject=")
                                append(subject)
                                first = false
                            }
                            if (body.isNotEmpty()) {
                                if (!first) append("&")
                                append("body=")
                                append(body)
                            }
                        }
                    }
                    try {
                        uriHandler.openUri(mailto)
                    } catch (t: Throwable) {
                        emailError = "Unable to open email client."
                    }
                },
            ) {
                Text("Send Email")
            }
            // Trigger 360° rotation when the image becomes visible
            LaunchedEffect(showImage) {
                if (showImage) {
                    rotation.snapTo(0f)
                    rotation.animateTo(360f, animationSpec = tween(durationMillis = 1000))
                } else {
                    // Reset rotation immediately when hidden so next show starts from 0°
                    rotation.snapTo(0f)
                }
            }
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    contentDescription = "Compose Multiplatform icon",
                    modifier = Modifier.rotate(rotation.value)
                )
            }
        }
    }
}

expect fun getPlatformName(): String