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
