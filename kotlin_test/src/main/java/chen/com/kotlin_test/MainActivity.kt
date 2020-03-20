package chen.com.kotlin_test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        async {
//            Log.i("MainActivity", "async" + Thread.currentThread().name)
//        }.then {
//            Log.i("MainActivity", "then" + Thread.currentThread().name)
//        }
    }
    
}

fun main() {
    
    println(ceil(1.2).toInt() + (ceil(1.2).toInt()%2))
    println(floor(2.2).toInt() + (floor(2.2).toInt()%2))
    
}
