package tw.com.motech.i_inverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATitle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAYAxis
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_a.view.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import tw.com.motech.i_inverter.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FragmentA : Fragment() {
    var sitedata = emptyList<SiteData>()
    private lateinit var v:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         v = inflater.inflate(R.layout.fragment_a, container, false)
        //val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        /*
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        val dateStr: String = currentDateAndTime + " 05:00"
        val dateEnd: String = currentDateAndTime + " 18:59"

        v.txtfrag1.setText("Fragment A SiteNo:{$sSiteNo_GLB}, ZoneNo:{$sZoneNo_GLB}")
        v.txtfrag2.setText("Hello A {$sSite_Name_GLB}, dateStr= ${dateStr}, dateEnd= ${dateEnd}")

         */

        /*
        val txt1:TextView = v.findViewById(R.id.txtfrag1) as TextView
        val txt2:TextView = v.findViewById(R.id.txtfrag2) as TextView
        txt1.setText("Fragment A {$sSiteNo_GLB}")
        txt2.setText("Hello A {$sSite_Name_GLB}")
         */
        getSiteData()
        return v
    }

    private fun getSiteData() {
        Thread(){
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val currentDateAndTime: String = simpleDateFormat.format(Date())
            val dateStart: String = currentDateAndTime + " 05:00"
            val dateEnd: String = currentDateAndTime + " 18:59"

            val requestBody = FormBody.Builder()
                .add("FunCode", "V01_SettingQuerypara01")
                .add("FunValues", "'${sSiteNo_GLB}','${sZoneNo_GLB}','${dateStart}','${dateEnd}'")
                .build()

            val request = Request.Builder().url(BaseUrl)
                .post(requestBody).build()

            val response = client.newCall(request).execute()
            val responsestr = response.body?.string()

            sitedata = Gson().fromJson(responsestr, Array<SiteData>::class.java).toList()
            getActivity()?.runOnUiThread {
                showAAChart()
            }
        }.start()
    }

    private fun showAAChart() {
        val siteX: Array<String>
        val SiteY1: String //發電
        val SiteY2: String //日照
        val SiteY3: String //溫度
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title(sSite_Name_GLB)
            .subtitle(sSiteNo_GLB)
            .animationType(AAChartAnimationType.EaseInOutExpo)
            .animationDuration(0)
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(true)
            .categories(sitedata.map { it.sDataKey }.toTypedArray())


        var aaOptions = aaChartModel.aa_toAAOptions()
        val aaYAxis0 = AAYAxis()
            .visible(true)
            .labels(AALabels()
                .enabled(true)//设置 y 轴是否显示数字
                .style(AAStyle()
                    .color("#ff0000")//yAxis Label font color
                    .fontSize(12f)//yAxis Label font size
                    .fontWeight(AAChartFontWeightType.Bold)//yAxis Label font weight
                ))
            .gridLineWidth(0f)// Y 轴网格线宽度
            .title(AATitle().text("發電量"))//Y 轴标题
            .min(0f)

        val aaYAxis1 = AAYAxis()
            .visible(true)
            .labels(AALabels()
                .enabled(true)//设置 y 轴是否显示数字
            )
            .opposite(true)
            .title(AATitle().text("日照量"))
            .min(0f)

        val aaYAxis2 = AAYAxis()
            .visible(true)
            .labels(AALabels()
                .enabled(true)//设置 y 轴是否显示数字
            )
            .opposite(true)
            .title(AATitle().text("溫度"))
            .min(0f)

        aaOptions.yAxisArray(arrayOf(aaYAxis0, aaYAxis1, aaYAxis2))
            .series(arrayOf(
                AASeriesElement()
                    .name("發電量1")
                    .data(sitedata.map { it.nEa }.toTypedArray())
                    .yAxis(0),
                AASeriesElement()
                    .name("日照量1")
                    .type(AAChartType.Spline)
                    .data(sitedata.map { it.nHi }.toTypedArray())
                    .yAxis(1),
                AASeriesElement()
                    .name("溫度1")
                    .type(AAChartType.Spline)
                    .data(sitedata.map { it.nTmp }.toTypedArray())
                    .yAxis(2)
            )
            )

        v.aa_chart_view.aa_drawChartWithChartModel(aaChartModel)
        v.aa_chart_view.aa_drawChartWithChartOptions(aaOptions)

    }
}