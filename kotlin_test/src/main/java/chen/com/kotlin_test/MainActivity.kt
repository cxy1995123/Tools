package chen.com.kotlin_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        async {
            Log.i("MainActivity", "async" + Thread.currentThread().name)
        }.then {
            Log.i("MainActivity", "then" + Thread.currentThread().name)
        }
        
    }
}

fun main() {
    
    val x = 1
    val y = 2
    val z = 4
    
    val zz = 6
    
    println(zz.and(x))
    println(zz.and(y))
    println(zz.and(z))
    
    
}
