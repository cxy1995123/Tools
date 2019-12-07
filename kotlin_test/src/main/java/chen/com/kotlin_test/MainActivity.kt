package chen.com.kotlin_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast

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

    fun onClick(view: View) {
        Toast.makeText(this, "123", Toast.LENGTH_SHORT).show()
    }
}

