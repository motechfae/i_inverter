package tw.com.motech.i_inverter

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fun1 = findViewById<CardView>(R.id.fun1)
        fun1.setOnClickListener{
            ShowToast("跳轉到功能1頁面")
        }

        var fun2 = findViewById<CardView>(R.id.fun2)
        fun2.setOnClickListener{
            ShowToast("跳轉到功能2頁面")
        }

        var fun3 = findViewById<CardView>(R.id.fun3)
        fun3.setOnClickListener{
            ShowToast("跳轉到功能3頁面")
        }

        var fun4 = findViewById<CardView>(R.id.fun4)
        fun4.setOnClickListener{
            ShowToast("跳轉到功能4頁面")
        }

    }

    private fun ShowToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}