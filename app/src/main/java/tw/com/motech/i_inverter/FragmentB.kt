package tw.com.motech.i_inverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_b.*
import kotlinx.android.synthetic.main.fragment_b.view.*
import okhttp3.*
import java.util.*
import java.util.concurrent.TimeUnit

class FragmentB : Fragment() {
    private lateinit var adapter_inv: InverterListAdapterActivity
    private lateinit var inverterresults: List<InverterResult>
    private lateinit var v:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_b, container, false)
        getInverterData()
        return v
    }

    private fun getInverterData() {
        Thread(){
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val requestBody = FormBody.Builder()
                .add("FunCode", "V01_MySolarToday08")
                .add("FunValues", "'${sSiteNo_GLB}','${sZoneNo_GLB}'")
                .build()

            val request = Request.Builder().url(BaseUrl)
                .post(requestBody).build()

            val response = client.newCall(request).execute()
            val responsestr = response.body?.string()

            inverterresults = Gson().fromJson(responsestr, Array<InverterResult>::class.java).toList()
            getActivity()?.runOnUiThread {
                if(inverterresults.count() > 0) {
                    showRecycleView()
                }
            }
        }.start()
    }

    private fun  showRecycleView(){

        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        v.invrecview.layoutManager = linearLayoutManager
        adapter_inv = InverterListAdapterActivity(inverterresults)
        v.invrecview.adapter = adapter_inv

    }
}