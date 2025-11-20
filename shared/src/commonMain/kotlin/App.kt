import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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