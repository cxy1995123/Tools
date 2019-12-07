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
    
    val list: List<String>? = mutableListOf("1", "@", "#")

    val iterator1 = list?.iterator()

    val iterator:MutableList<String> = mutableListOf()

    iterator?.forEach {
        if (it == "1") {
            iterator.remove(it)
        }
    }

    System.out.println(list)
}
