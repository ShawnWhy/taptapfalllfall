package com.shawnwhy.taptaptwo

import android.os.Bundle
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shawnwhy.taptaptwo.ui.theme.TapTaptwoTheme
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TapTaptwoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   TapTapHome()
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun TapTapHome() {

//    1. Update your `BoxItem` model to include the size:
//    ```kotlin
    data class BoxItem(
    val offset: Float,
    val size: Dp, 
    val isAnimating: MutableState<Boolean> = mutableStateOf(false))
//    ```

//
//    2. Modify your mutable list to hold the box data items:
//    ```kotlin
    val boxItems = remember { mutableStateListOf<BoxItem>() }
//    ```
//
//    3. Update your Composable function to use the data items and animated offset:
//    ```kotlin
//    Column {
//        boxItems.forEachIndexed { index, boxItem -&gt;
//            Box(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .offset { IntOffset(boxItem.offset.value.roundToInt(), 0) }
//                    .background(Color.Red)
//                    .size(boxItem.size)
//                    .draggable(
//                        state = DraggableState(
//                            onDragEnd = { dragDistance -&gt;
//                                // Update the state when the drag ends
//                                boxItem.isAnimating.value = false
//                            },
//                            onDragStart = {
//                                // Update the state when the drag starts
//                                boxItem.isAnimating.value = true
//                            }
//                        ),
//                        orientation = Orientation.Horizontal
//                    )
//            )
//        }
//    }
//    ```
//
//    4. To access the mutated state during the drag action, use `remember` and `derivedStateOf` functions:
//    ```kotlin
//    val draggedBoxData = remember(draggedBoxIndex.value) {
//        derivedStateOf {
//            boxItems.getOrNull(draggedBoxIndex.value)
//        }
//    }
//    ```
//
//    5. Implement the collision detection logic between the dragged box and others:
//    ```kotlin
//    if (draggedBoxData.value != null) {
//        boxItems.forEachIndexed { index, boxItem -&gt;
//            if (index != draggedBoxIndex.value &amp;&amp; checkCollision(draggedBoxData.value!!, boxItem)) {
//                // Collision detected! Perform desired actions here
//            }
//        }
//    }
//    ```


    var list by remember{
        mutableStateOf(mutableListOf(

        "hah","sadsa","asada","sadada","4543535","wrwrwRW","2242"
    ))
    }

    val color = remember {
        mutableStateOf(Color.Yellow)
    }
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
//        TapButton(color = color.value,
//            modifier= Modifier
//                .offset(122.dp, 300.dp)
//                .fillMaxSize()
//        ){
//            color.value=it
//        }

        for ((index,value) in list.withIndex()) {
            FallingButton(
                modifier = Modifier,
                offsetXInput = Random.nextInt(100,400),
                offsetYInput = Random.nextInt(500,700),
                time = 3000,
                color = Color.Red,
                list = list,
                delay =index

            ) {

                color.value = it
            }
        }


    }

}




@Composable
fun TapButton(
    modifier:Modifier = Modifier,
    time:Int = 3000,

    color: Color,
    updateColor : (Color) -> Unit,

){

    Box(modifier = modifier
        .background(color)
        .clickable {

            updateColor(
                Color(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat(),
                    1f

                )

            )

        }



    ){
        Text(text = "Click Brah")

    }
}

@Composable
fun FallingButton(
    modifier:Modifier,
    offsetXInput:Int,
    offsetYInput:Int,
    time:Int,
    delay:Int,
    color: Color,
    list : List<String>,

    updateColor : (Color) -> Unit

){
    val dragOffset = remember { mutableStateOf(Offset.Zero) }



    var animationPlayed by remember {
        mutableStateOf(false)

    }

    var rotationPlayed by remember {
        mutableStateOf(false)
    }

    var splashPlayed by remember{
        mutableStateOf(false)
    }

    var sizeState by remember { mutableStateOf(40) }
    var size = animateIntAsState(targetValue = sizeState)
    var offsetX = animateIntAsState(targetValue = offsetXInput,
        keyframes {
            durationMillis = time
        }
    )

    var visibility by remember{
        mutableStateOf(true)
    }

    var offsetY = animateIntAsState(targetValue = if(animationPlayed) offsetYInput else 0,

        keyframes {
            durationMillis = time
//            delayMillis = 500

        })
    var rotation = animateFloatAsState(targetValue = if(rotationPlayed) 360f else 0f,
            animationSpec = infiniteRepeatable((
            tween(500)
            )))
//time the animations
    LaunchedEffect(Unit) {
        delay(timeMillis = delay.toLong()*100)
//        animationPlayed= true
        delay(timeMillis = delay.toLong()*100 + time -100)
//        visibility=false
//        splashPlayed = true
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .offset(offsetX.value.dp+ dragOffset.value.x.dp, offsetY.value.dp+dragOffset.value.y.dp)




    ){
        Box(

            modifier = Modifier
                .height(size.value.dp)
                .width(size.value.dp)

                .rotate(if(visibility)rotation.value else 0f)


                .background(if(visibility)color else Color.Transparent)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            rotationPlayed = true
                            animationPlayed = false

                        },
                        onDragCancel = {
                            rotationPlayed = false
                            animationPlayed = true

                        },
                        onDragEnd = {
                            rotationPlayed = false
                            animationPlayed = true


                        }



                    ) { change, dragAmount ->
                        change.consume()


                        dragOffset.value += Offset(dragAmount.x, dragAmount.y)
                    }
                }

        ){

            for (i in 1..5) {
                Splasher(
                    size = size.value.dp/3,
                    height =Random.nextInt(-400,-100).dp,
                    length =Random.nextInt(-400,400).dp,
                    color =                 Color(
                        Random.nextFloat(),
                        Random.nextFloat(),
                        Random.nextFloat(),
                        1f

                    ),
                    splashPlayed = splashPlayed,
                    visibility = visibility



                    )

            }

        }

    }
}


@Composable
fun Splasher(
    size:Dp,
    height: Dp,
    length:Dp,
    color:Color,
    splashPlayed:Boolean,
    visibility:Boolean

){

    var splashAnimation1X = animateDpAsState(targetValue = if(splashPlayed) length else 0.dp, animationSpec =
    keyframes {
        durationMillis=1500
        0.dp at 0
        length/2 at 750
        length at 1500
    } )

    val splashAnimation1Y = animateDpAsState(
        targetValue = if(splashPlayed)-20.dp else 0.dp,
        animationSpec = keyframes {
            durationMillis = 1500
            0.dp at 0

            height/2 at 450

            height at 900

            height/2 at 750

            -20.dp at 1500
        }
    )

    Box(
        modifier= Modifier
            .size(size)
            .offset(splashAnimation1X.value, splashAnimation1Y.value)
            .background(if(!visibility)color else Color.Transparent)

    ){


    }

}
data class Drop(
    val animationState:String,
    val xOffset:Float,
    val yOffset:Float
)




//@Composable
//val mutableList = remember { mutableStateListOf(<ListBoxData>)}
//
//fun detectCollision(
//    var boxList :List<DropBoxData>
//
//){
//
//
//}


