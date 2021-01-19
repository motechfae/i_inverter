package tw.com.motech.i_inverter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fun1 = findViewById<CardView>(R.id.fun1)
        fun1.setOnClickListener{
            showToast("跳轉到功能1頁面")
        }

        var fun2 = findViewById<CardView>(R.id.fun2)
        fun2.setOnClickListener{
            showToast("跳轉到功能2頁面")
        }

        var fun3 = findViewById<CardView>(R.id.fun3)
        fun3.setOnClickListener{
            showToast("跳轉到功能3頁面")
        }

        var fun4 = findViewById<CardView>(R.id.fun4)
        fun4.setOnClickListener{
            showToast("跳轉到功能4頁面")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var item = menu?.findItem(R.id.usernmae)
        item?.title = "你好，${UserName}"
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
        }
        return true
    }

    private fun logout() {
        val editor =
            getSharedPreferences(LoginSP, Context.MODE_PRIVATE).edit()
        editor.clear()
        editor.commit()

        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}