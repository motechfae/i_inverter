package tw.com.motech.i_inverter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

private val client = OkHttpClient()

class LoginResult(
    val sAccount: String,
    val sPassword: String,
    val sAccountName: String,
    val sEMail: String,
    val sTEL: String,
    val sPhone: String,
    val sAuthority: String
)

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 使用先前存的username password登入
        var sharedPreference = getSharedPreferences(LoginSP, Context.MODE_PRIVATE)
        val username = sharedPreference.getString("username", null)
        val password = sharedPreference.getString("password", null)

        var et_username = findViewById<EditText>(R.id.et_username)
        var et_password = findViewById<EditText>(R.id.et_password)
        if (username != null && password != null) {
            et_username.setText(username)
            et_password.setText(password)
            postLogin(username, password)
        }

        // 使用按鈕登入
        var btn_login = findViewById<Button>(R.id.btn_login)
        btn_login.setOnClickListener {
            postLogin(et_username.text.toString(), et_password.text.toString())
        }
    }

    private fun postLogin(username: String?, password: String?) {
        val formBody = FormBody.Builder()
            .add("FunCode", "V01_Login01")
            .add("FunValues", "'${username}';'${password}'")
            .build()
        val request = Request.Builder()
            .url(BaseUrl)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val json = response.body!!.string() // 資料只能抓一次
                    val list: List<LoginResult> =
                        Gson().fromJson(json, Array<LoginResult>::class.java).toList()

                    if (list.count() == 0) {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, "帳密錯誤，登入失敗!", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        for (l in list) {
                            println("sAccount: ${l.sAccount}, sPassword: ${l.sPassword}")
                        }
                        if (username.equals(list[0].sAccount) && password.equals(list[0].sPassword)) {

                            UserName = list[0].sAccount
                            startActivity(Intent(this@LoginActivity, SiteListActivity::class.java))
                            /*
                            runOnUiThread{
                                Toast.makeText(this@LoginActivity, "登入成功，跳轉至功能頁面!", Toast.LENGTH_SHORT).show()
                            }
                            */
                            // 存到SharedPreferences裡面，下次開啟APP直接登入
                            var chk_RememberMe = findViewById<CheckBox>(R.id.chk_RememberMe)
                            if (chk_RememberMe.isChecked) {
                                val editor =
                                    getSharedPreferences(LoginSP, Context.MODE_PRIVATE).edit()
                                editor.putString("username", list[0].sAccount)
                                editor.putString("password", list[0].sPassword)
                                editor.commit()
                            }
                        } else{
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "帳密不正確，登入失敗!", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("查詢失敗", "$e")
            }
        })
    }
}