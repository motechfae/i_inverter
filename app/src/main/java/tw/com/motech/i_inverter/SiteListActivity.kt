package tw.com.motech.i_inverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_site_list.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class SiteListActivity : AppCompatActivity() {
    private lateinit var adapter: SiteListAdapterActivity
    private lateinit var siteresults: List<SiteResult>
    private lateinit var siteresults_all: List<SiteResult>
    private var type = "1"
    private var typeName = "北"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = layoutInflater.inflate(R.layout.activity_site_list, null)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val actionBarSize = obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                .getDimensionPixelSize(0, 0)

            val topPadding = statusBarHeight + (actionBarSize / 2)

            v.setPadding(0, topPadding, 0, navBarHeight)

            insets
        }

        setContentView(root)
        getSiteInfo()


        siteresults_search.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        // 北中南切頁事件
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position) {
                    0 -> {
                        type = "1"
                        typeName = "北"
                    }
                    1 -> {
                        type = "2"
                        typeName = "中"
                    }
                    2 -> {
                        type = "3"
                        typeName = "南"
                    }
                }
                getPartSiteInfo()
            }

        })
    }

    private fun setBarTitle() {
        this@SiteListActivity.supportActionBar!!.title = "${typeName}部案場 (${adapter.itemCount})"
    }

    private fun getSiteInfo(){
        Thread(){
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val requestBody = FormBody.Builder()
                .add("FunCode", "V02_RwdDashboard04")
                .add("FunValues", "'${UserName}'")
                .build()

            val request = Request.Builder().url(BaseUrl)
                .post(requestBody).build()

            val response = client.newCall(request).execute()
            val responsestr = response.body?.string()

            siteresults_all = Gson().fromJson(responsestr, Array<SiteResult>::class.java).toList()
            siteresults = siteresults_all.filter { it.sSiteType == type }

            runOnUiThread {
                showRecycleView()
                setBarTitle()
            }
        }.start()
    }

    private fun getPartSiteInfo(){
        siteresults = siteresults_all.filter { it.sSiteType == type }
        showRecycleView()
        setBarTitle()
    }

    private fun  showRecycleView(){
        /*
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager
        adapter = MyAdapter(siteresults)
        recyclerView.adapter = adapter
        */
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = gridLayoutManager
        adapter = SiteListAdapterActivity(siteresults)
        recyclerView.adapter = adapter
    }
}