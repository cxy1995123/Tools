package chen.com.kotlin_test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

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
    
    val list = mutableListOf<Int>()
    
    for (index in 0..100) {
        list.add(index)
    }
    
    for (i in list) {
        println(i)
        if (i == 10) {
            continue
        }
        
    }
    
    
    println(100)
    
    
}
