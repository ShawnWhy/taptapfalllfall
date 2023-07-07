package com.shawnwhy.taptaptwo.ui

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.input.pointer.PointerInputChange
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
                time = 5000,
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
    time:Int = 1000,

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

    var offsetY = animateIntAsState(targetValue = if(animationPlayed) offsetYInput else 0,

        keyframes {
            durationMillis = time
//            delayMillis = 500

        })
    var rotation = animateIntAsState(targetValue = if(rotationPlayed) 360 else 0,
            animationSpec = infiniteRepeatable((
            tween(500)
            )))


    var splashAnimation1X = animateDpAsState(targetValue = if(splashPlayed) 200.dp else 0.dp, animationSpec = tween(durationMillis = 1500) )
//    var splashAnimation1Y = animateDpAsState(targetValue = if(splashPlayed) pathMeasure.length.dp else 0.dp, animationSpec = tween(durationMillis = 1500) )

    val splashAnimation1Y = animateDpAsState(
        targetValue = if(splashPlayed) -20.dp else 0.dp,
        animationSpec = keyframes {
            durationMillis = 1500
            0.dp at 0


            -200.dp at 900

            -20.dp at 1500
        }
    )





    LaunchedEffect(Unit) {
        delay(timeMillis = delay.toLong()*100)
        animationPlayed= true
        delay(timeMillis = delay.toLong()*100 + time)
        splashPlayed = true
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier

            .offset(offsetX.value.dp+ dragOffset.value.x.dp, offsetY.value.dp+dragOffset.value.y.dp)





    ){
        Box(

            modifier = Modifier
                .height(size.value.dp)
                .width(size.value.dp)
                .background(color)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->

                        dragOffset.value += dragAmount
                    }
                }


//                .clickable {
//                    sizeState = Random.nextInt(30, 200)
//
//                }
                .offset(splashAnimation1X.value / 2, splashAnimation1Y.value / 2)


        ){
//            Box(
//                modifier = Modifier
//                    .height(size.value.dp / 3)
//                    .width(size.value.dp / 3)
//                    .background(color)
//
//            )

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
                    splashPlayed = splashPlayed



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
    splashPlayed:Boolean

){

    var splashAnimation1X = animateDpAsState(targetValue = if(splashPlayed) length else 0.dp, animationSpec = tween(durationMillis = 1500) )

    val splashAnimation1Y = animateDpAsState(
        targetValue = if(splashPlayed) height else 0.dp,
        animationSpec = keyframes {
            durationMillis = 1500
            0.dp at 0


            height at 900

            -20.dp at 1500
        }
    )

    Box(
        modifier= Modifier
            .size(size)
            .offset(splashAnimation1X.value, splashAnimation1Y.value)
            .background(color)

    ){


    }

}
data class Drop(
    val animationState:String,
    val xOffset:Float,
    val yOffset:Float
)




