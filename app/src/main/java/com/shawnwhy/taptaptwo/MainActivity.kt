package com.shawnwhy.taptaptwo

import android.os.Bundle
import android.transition.Explode
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
import androidx.compose.runtime.State
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

//TODO have the box location objects in and use it to measure collision



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
data class BoxItem(
    val id:Int,
    var offsetY: State<Float>,
    val offsetX : Float,
    val size: Dp,
    var isAnimating: MutableState<Boolean> = mutableStateOf(false),
    var exploded : MutableState<Boolean> = mutableStateOf(false),
)

data class BoxLocation(
    val id : Int,
    val offsetX: Float,
    val offsetY:Float,
)


//var boxItems = remember { mutableStateListOf<BoxItem>() }
//for (i in 0..5){
//    var boxisAnimating =  remember{ mutableStateOf(false)}
//    var newBox = BoxItem(
//        id = i,
//        isAnimating = boxisAnimating,
//        offset = animateFloatAsState(targetValue = if(boxItems[i].isAnimating.value) 360f else 0f,
//            animationSpec = infiniteRepeatable((
//                    tween(500)
//                    ))),
//        size = 50.dp,
//    )
//    boxItems.add(newBox)
//}
@ExperimentalFoundationApi
@Composable
fun TapTapHome() {


    val fallNumber = 4
    var animationTriggers = remember { mutableStateListOf<Boolean>()}
    for (i in 0..fallNumber){
        animationTriggers.add(false)
    }

    var boxLocations = remember { mutableStateListOf<BoxLocation>()}
    for (i in 0..fallNumber){
        boxLocations.add(BoxLocation( id=i, offsetX = 0.0.toFloat(), offsetY= 0.0.toFloat()))
    }


    var boxItems = remember { mutableStateListOf<BoxItem>() }
    for (i in 0..fallNumber){
        var newBox = BoxItem(
            id = i,
            offsetY = remember{mutableStateOf(0.0.toFloat())},
            offsetX = Random.nextFloat()*700+100,
            size = 50.dp,
        )
        boxItems.add(newBox)
    }

    val color = remember {
        mutableStateOf(Color.Yellow)
    }
    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        for ((index,value) in boxItems.withIndex()) {
            FallingButton(
                modifier = Modifier,
                offsetXInput = boxItems[index].offsetX,
                time = 3000,
                color = Color.Red,
                boxlist = boxItems,
                delay =index,
                id = index,
                animationTriggers = animationTriggers
            ) {
                color.value = it
            }
        }
    }
}

//

@Composable
fun FallingButton(
    id:Int,
    modifier:Modifier,
    offsetXInput:Float,
    time:Int,
    delay:Int,
    color: Color,
    boxlist : List<BoxItem>,
    animationTriggers : MutableList<Boolean>,
    updateColor : (Color) -> Unit
){
    val dragOffset = remember { mutableStateOf(Offset.Zero) }

    var rotationPlayed by remember {
        mutableStateOf(false)
    }
    var splashPlayed by remember{
        mutableStateOf(false)
    }
    var sizeState by remember { mutableStateOf(40) }
    var size = animateIntAsState(targetValue = sizeState)
    var offsetX = offsetXInput.dp
    var visibility by remember{
        mutableStateOf(true)
    }
    var fade by remember { mutableStateOf(false)}


    boxlist[id].offsetY = animateFloatAsState(targetValue = if(animationTriggers[id]) 500.toFloat() else 0.toFloat(),

        keyframes {
            durationMillis = time
            delayMillis = 500

        })
    var rotation = animateFloatAsState(targetValue = if(rotationPlayed) 360f else 0f,
            animationSpec = infiniteRepeatable((
            tween(500)
            )))
//time the animations
    LaunchedEffect(Unit) {
        delay(timeMillis = delay.toLong()*100+(id*100))
       if(animationTriggers[id] == false){
           animationTriggers[id]= true
       }
        delay(timeMillis = delay.toLong()*100 + time -100+id*100)

        visibility=false
        boxlist[id].exploded.value = true
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .offset(offsetX.value.dp+ (dragOffset.value.x/2).dp, boxlist[id].offsetY.value.dp+(dragOffset.value.y/2).dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        rotationPlayed = true
                    },
                    onDragCancel = {
                        rotationPlayed = false

                        boxlist[id].exploded.value = true
                        visibility=false

                    },
                    onDragEnd = {
                        rotationPlayed = false
                        boxlist[id].exploded.value = true
                        visibility=false
                    }



                ) { change, dragAmount ->
                    change.consume()

                    if(visibility==true){
                    dragOffset.value += Offset(dragAmount.x, dragAmount.y)}
                }
                val newoffSetX = offsetX.value+ dragOffset.value.x/2
                val newoffsetY = boxlist[id].offsetY.value+(dragOffset.value.y/2)
                for ((index,value) in boxlist.withIndex()) {

                    if(boxlist[index].offsetY.value<newoffsetY+50 && boxlist[index].offsetY.value>newoffsetY-50
                        && boxlist[index].offsetX < newoffSetX + 50 &&  boxlist[index].offsetX > newoffSetX - 50 ){

                        boxlist[index].exploded.value = true;
                        boxlist[id].exploded.value = true;
                    }
                }


            }

    ){
        Box(

            modifier = Modifier
                .height(size.value.dp)
                .width(size.value.dp)
                .rotate(if(visibility)rotation.value else 0f)
                .background(if(visibility)color else Color.Transparent)

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
                    visibility = visibility,
                    isExplode = boxlist[id].exploded.value,
                    id = id

                    )
            }
        }
    }
}


@Composable
fun Splasher(
    id:Int,
    isExplode:Boolean,
    size:Dp,
    height: Dp,
    length:Dp,
    color:Color,
    visibility:Boolean

){

    var splashAnimation1X = animateDpAsState(targetValue = if(isExplode) length else 0.dp, animationSpec =
    keyframes {
        durationMillis=1500
        0.dp at 0
        length/2 at 750
        length at 1500
    } )

    val splashAnimation1Y = animateDpAsState(
        targetValue = if(isExplode)-20.dp else 0.dp,
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


//@Composable
//fun TapButton(
//    modifier:Modifier = Modifier,
//    time:Int = 3000,
//    color: Color,
//    updateColor : (Color) -> Unit,
//){
//    Box(modifier = modifier
//        .background(color)
//        .clickable {
//            updateColor(
//                Color(
//                    Random.nextFloat(),
//                    Random.nextFloat(),
//                    Random.nextFloat(),
//                    1f
//                )
//            )
//        }
//    ){
//        Text(text = "Click Brah")
//    }
//}

