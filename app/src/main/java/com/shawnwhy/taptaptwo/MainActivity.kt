package com.shawnwhy.taptaptwo

import android.os.Bundle
import android.transition.Explode
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
                   MainContainer()
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
    var visible : MutableState<Boolean> = mutableStateOf(true),
    val points : Int,
    val splashVisible : MutableState<Boolean> = mutableStateOf(false),
    val scoreVisible : MutableState<Boolean> = mutableStateOf(false),
    val color:Color
)

data class BoxLocation(
    val id : Int,
    val offsetX: Float,
    val offsetY:Float,
)
//@ExperimentalFoundationApi


@Composable
fun MainContainer(


){
    val score = remember { mutableStateOf(0) }
    val fallNumber = 8

    var boxItems = remember { mutableStateListOf<BoxItem>() }

    for (i in 0..fallNumber){
        val randomSizeNumber = Random.nextInt(50)+50
        var newBox = BoxItem(
            id = i,
            offsetY = remember{mutableStateOf(0.0.toFloat())},
            offsetX = Random.nextFloat()*300+100,
            size = randomSizeNumber.dp,
            points = randomSizeNumber,
            color = Color(
                    Random.nextFloat(),
                    Random.nextFloat(),
                    Random.nextFloat(),
                    1f
                )
        )
        boxItems.add(newBox)
    }



    var animationTriggers = remember { mutableStateListOf<Boolean>()}
    for (i in 0..fallNumber){
        animationTriggers.add(false)
    }

    TapTapHome(animationTriggers = animationTriggers, boxItems = boxItems, score = score )


}
@Composable
fun TapTapHome(

    animationTriggers: MutableList<Boolean>,
    boxItems:MutableList<BoxItem>,
    score:MutableState<Int>

) {


    val color = remember {
        mutableStateOf(Color.Yellow)
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White)
            .padding(10.dp)
            .align(Alignment.TopCenter)


        ){
            Text(
                text = score.value.toString()
            )
            }

        for ((index,value) in boxItems.withIndex()) {
            FallingButton(
                modifier = Modifier,
                offsetXInput = boxItems[index].offsetX,
                time = 5000,
                boxlist = boxItems,
                delay =index*4,
                id = index,
                animationTriggers = animationTriggers,
                score = score

            ) {
                color.value = it
            }
        }
    }
}



//this is the button class that calls and is controled

    @Composable
    fun FallingButton(
        id:Int,
        modifier:Modifier,
        offsetXInput:Float,
        time:Int,
        delay:Int,
        boxlist : List<BoxItem>,
        animationTriggers : MutableList<Boolean>,
        score : MutableState<Int>,

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
        var size = animateFloatAsState(targetValue = boxlist[id].size.value)
        var offsetX = offsetXInput.dp
        var visibility by remember{
            mutableStateOf(true)
        }
        var fade by remember { mutableStateOf(false)}


        boxlist[id].offsetY = animateFloatAsState(targetValue = if(animationTriggers[id]) 600.toFloat() else 0.toFloat(),

            keyframes {
                durationMillis = time
                delayMillis = 500
            }
        )
        var rotation = animateFloatAsState(targetValue = if(rotationPlayed) 360f else 0f,
    //            animationSpec = infiniteRepeatable((
    //            tween(500)
    //            ))
            keyframes {
                durationMillis = 500

            }
        )
    //time the animations





        LaunchedEffect(Unit) {
            while (true) {
                delay(timeMillis = delay.toLong() * 100 + (id * 100))
                if (animationTriggers[id] == false) {
                    animationTriggers[id] = true
                }
                delay(timeMillis = delay.toLong() * 100 + time - 100 + id * 100)
    //            if(boxlist[id].visible.value == true) {
                    boxlist[id].visible.value = false
                    boxlist[id].exploded.value = true
                    animationTriggers[id] = false
                    dragOffset.value = Offset.Zero
                    rotationPlayed = false
                    boxlist[id].splashVisible.value = true
    //            }
                delay(timeMillis = delay.toLong() * 100 + time - 100 + id * 100 + 400)
                animationTriggers[id] = true
                boxlist[id].visible.value = true
                boxlist[id].exploded.value = false
                boxlist[id].splashVisible.value = false
    //          boxlist[id].offsetY.value = 0.0.toFloat()

    //            if(boxlist[id].scoreVisible.value == true){
    //                delay(timeMillis = delay.toLong() * 100)
    //                boxlist[id].scoreVisible.value = false
    //            }

            }

        }

        var scoreBoard = remember { mutableStateOf(false) }

        LaunchedEffect(boxlist[id].scoreVisible.value) {
            Log.d("scoreboard1", boxlist[id].toString())
            Log.d("scoreboard1-2", scoreBoard.toString())

            if(boxlist[id].scoreVisible.value){

                delay(1000)
                boxlist[id].scoreVisible.value = false

                scoreBoard.value = false
                Log.d("scoreboard2", boxlist[id].toString())

            }
        }

        Box(contentAlignment = Alignment.Center,
            modifier = modifier
                .offset(
                    offsetX.value.dp + (dragOffset.value.x / 2).dp,
                    boxlist[id].offsetY.value.dp + (dragOffset.value.y / 2).dp
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {

                            rotationPlayed = true
                        },
                        onDragCancel = {
                            rotationPlayed = false

                            boxlist[id].exploded.value = true
                            boxlist[id].visible.value = false
                            boxlist[id].splashVisible.value = true
                            boxlist[id].scoreVisible.value = true

                            score.value = score.value + boxlist[id].points


                        },
                        onDragEnd = {
                            rotationPlayed = false
                            boxlist[id].exploded.value = true
                            boxlist[id].visible.value = false
                            boxlist[id].splashVisible.value = true
                            boxlist[id].scoreVisible.value = true

                            score.value = score.value + boxlist[id].points


                        }


                    ) { change, dragAmount ->
                        change.consume()

                        if (boxlist[id].visible.value == true) {
                            dragOffset.value += Offset(dragAmount.x, dragAmount.y)
                        }

                        val newoffSetX = offsetX.value + dragOffset.value.x / 2
                        val newoffsetY = boxlist[id].offsetY.value + (dragOffset.value.y / 2)
                        for ((index, value) in boxlist.withIndex()) {
                            if (boxlist[index].offsetY.value > newoffsetY - 5 && boxlist[index].offsetY.value < newoffsetY + size.value + 5
                                && boxlist[index].offsetX > newoffSetX - 5 && boxlist[index].offsetX < newoffSetX + size.value + 5 && index != id
                                && !boxlist[index].exploded.value
                            ) {


                                boxlist[index].exploded.value = true;
                                boxlist[index].visible.value = false

                                boxlist[id].exploded.value = true;
                                boxlist[id].visible.value = false
                                boxlist[id].splashVisible.value = true

                                rotationPlayed = false
                                boxlist[id].scoreVisible.value = true
                                boxlist[index].scoreVisible.value = true

                                score.value = score.value + boxlist[id].points
                                score.value = score.value + boxlist[index].points
                                scoreBoard.value = true
//                                triggerStuff(scoreBoard)


                            }
                        }
                    }


                }

        ){



            Box(

                modifier = Modifier
                    .height(boxlist[id].size)
                    .width(boxlist[id].size)
                    .rotate(if (boxlist[id].visible.value) rotation.value else 0f)
                    .background(if (boxlist[id].visible.value) boxlist[id].color else Color.Transparent)

            ){


                Text(
                    color = if(boxlist[id].scoreVisible.value) boxlist[id].color else Color.Transparent,
                    text = boxlist[id].points.toString(),
                )

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
                        visibility = boxlist[id].visible.value,
                        isExplode = boxlist[id].exploded.value,
                        id = id,
                        splasherVisible = boxlist[id].splashVisible.value

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
    visibility:Boolean,
    splasherVisible:Boolean

){




    var splashAnimation1X = animateDpAsState(targetValue = if(isExplode) length else 0.dp, animationSpec =
    keyframes {
        durationMillis=500

    } )

    val splashAnimation1Y = animateDpAsState(
        targetValue = if(isExplode)height else 0.dp,
        animationSpec = keyframes {
            durationMillis = 500
        }
    )

    Box(
        modifier= Modifier
            .size(size)
            .offset(splashAnimation1X.value, splashAnimation1Y.value)
            .background(if (!visibility && splasherVisible) color else Color.Transparent)

    ){

    }

}
data class Drop(
    val animationState:String,
    val xOffset:Float,
    val yOffset:Float
)


//@Composable
//fun triggerStuff (unit:MutableState<Boolean>){
//    LaunchedEffect(unit){
//        delay(200)
//        Log.d("triggered", "something")
//
//
//    }
//
//}

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

