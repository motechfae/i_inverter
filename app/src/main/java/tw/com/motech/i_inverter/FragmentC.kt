package tw.com.motech.i_inverter

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.androidbuts.multispinnerfilter.KeyPairBoolData
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATitle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAYAxis
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_a.view.*
import kotlinx.android.synthetic.main.fragment_c.*
import kotlinx.android.synthetic.main.fragment_c.view.*
import okhttp3.*
import java.io.IOException
import java.util.*

class FragmentC : Fragment() {
    private lateinit var v:View
    private lateinit var listInverterChkList: List<InverterChkList>
    private lateinit var listParameterChkList: MutableList<ParameterChkList>
    private lateinit var listSiteData: List<SiteData>
    private lateinit var listInvStringData: List<InvStringData>
    private lateinit var mapInvStringData : MutableMap<String, MutableList<InvStringData>> // key是SNID, value是InvStringData的陣列

    private var selectedInv : String = ""
    private var selectedPara : String = ""
    private lateinit var listSelectedPara : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_c, container, false)

        mapInvStringData = mutableMapOf()
        listSelectedPara = mutableListOf()

        initParaList()
        initInvMultiSpinner()
        initParaMultiSpinner()

        // 日期
        v.btndate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(v.context, { _, year, month, day ->
                run {
                    val d = setDateFormat(year, month, day)
                    v.txtdate.text = d
                }
            }, year, month, day).show()
        }

        // 確認
        v.btnQuery.setOnClickListener{
            var valid : Boolean = true

            if (v.txtdate.text.toString().length == 0) {
                Toast.makeText(v.context, "請選擇日期", Toast.LENGTH_SHORT).show()
                valid = false
            }

            // 除了nHi和nTmp以外，如果有勾以下參數要檢查inverter有沒有選
            if (selectedPara.contains("nEa") ||
                selectedPara.contains("nPpv") ||
                selectedPara.contains("nOVol") ||
                selectedPara.contains("nVpv") ||
                selectedPara.contains("nOCur") ||
                selectedPara.contains("nIpv")   ) {
                if (selectedInv.length == 0) {
                    Toast.makeText(v.context, "請至少選擇一台inverter", Toast.LENGTH_SHORT).show()
                    valid = false
                }
            }

            if (valid) {
                getSiteData(v.txtdate.text.toString())
            }
        }

        return v
    }

    private fun initParaList() {
        listParameterChkList = mutableListOf<ParameterChkList>()
        listParameterChkList.add(ParameterChkList("交流側", "nEa", "發電功率"))
        listParameterChkList.add(ParameterChkList("交流側", "nOVol", "電壓"))
        listParameterChkList.add(ParameterChkList("交流側", "nOCur", "電流"))

        listParameterChkList.add(ParameterChkList("直流側", "nPpv", "輸入功率"))
        listParameterChkList.add(ParameterChkList("直流側", "nVpv_A", "輸入電壓-A串"))
        listParameterChkList.add(ParameterChkList("直流側", "nVpv_B", "輸入電壓-B串"))
        listParameterChkList.add(ParameterChkList("直流側", "nVpv_C", "輸入電壓-C串"))
        listParameterChkList.add(ParameterChkList("直流側", "nVpv_D", "輸入電壓-D串"))
        listParameterChkList.add(ParameterChkList("直流側", "nVpv_E", "輸入電壓-E串"))
        listParameterChkList.add(ParameterChkList("直流側", "nVpv_F", "輸入電壓-F串"))
        listParameterChkList.add(ParameterChkList("直流側", "nIpv_A", "輸入電流-A串"))
        listParameterChkList.add(ParameterChkList("直流側", "nIpv_B", "輸入電流-B串"))
        listParameterChkList.add(ParameterChkList("直流側", "nIpv_C", "輸入電流-C串"))
        listParameterChkList.add(ParameterChkList("直流側", "nIpv_D", "輸入電流-D串"))
        listParameterChkList.add(ParameterChkList("直流側", "nIpv_E", "輸入電流-E串"))
        listParameterChkList.add(ParameterChkList("直流側", "nIpv_F", "輸入電流-F串"))

        listParameterChkList.add(ParameterChkList("感測器", "nHi", "日照計"))
        listParameterChkList.add(ParameterChkList("感測器", "nTmp", "溫度計"))
    }

    private fun initInvMultiSpinner() {

        v.invMultiSpinner.isSearchEnabled = true
        v.invMultiSpinner.isShowSelectAllButton = true
        v.invMultiSpinner.setSearchHint("搜尋 inverter")
        v.invMultiSpinner.setEmptyTitle("找不到 inverter!")
        v.invMultiSpinner.setClearText("關閉")

        getInverterList()
    }

    private fun initParaMultiSpinner() {
        v.paraMultiSpinner.isSearchEnabled = true
        v.paraMultiSpinner.setSearchHint("搜尋 parameter")
        v.paraMultiSpinner.setEmptyTitle("找不到 parameter!")
        v.paraMultiSpinner.setClearText("關閉")
        getParameterList()
    }

    private fun getInverterList() {
        var funCode = "V01_MySolarToday09"
        var funValues = "'$sSiteNo_GLB';'$sZoneNo_GLB'"

        val formBody = FormBody.Builder()
            .add("FunCode", funCode)
            .add("FunValues", funValues)
            .build()

        val request = Request.Builder()
            .url(BaseUrl)
            .post(formBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val json = response.body!!.string() // 資料只能抓一次
                    listInverterChkList = Gson().fromJson(json, Array<InverterChkList>::class.java).toList()

                    val listArray0: MutableList<KeyPairBoolData> = ArrayList()
                    for (l in listInverterChkList) {
                        val h = KeyPairBoolData()
                        h.id = (l.nRS485ID).toLong()
                        h.name = l.sSNID + ":" + l.nRS485ID
                        h.isSelected = false
                        listArray0.add(h)
                    }

                    getActivity()?.runOnUiThread {
                        v.invMultiSpinner.setItems(
                            listArray0
                        ) { items ->
                            selectedInv = ""
                            mapInvStringData = mutableMapOf()
                            for (i in items.indices) {
                                if (items[i].isSelected) {
                                    val inv = items[i].name.split(":")[0]
                                    // 組sql字串
                                    // AND B.sSNID in ('O1Y19804686WF','O1Y19804682WF','O1Y19804684WF')
                                    selectedInv = selectedInv + "'" + inv + "', "
                                    mapInvStringData[inv] = mutableListOf()
                                }
                            }

                            if (selectedInv.length > 2) {
                                selectedInv = "AND B.sSNID in (" + selectedInv.substring(0, selectedInv.length - 2) + ")"
                                println(selectedInv)
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

    private fun getParameterList() {

        val listArray0: MutableList<KeyPairBoolData> = ArrayList()
        for (i in listParameterChkList.indices) {
            val h = KeyPairBoolData()
            h.id = (i + 1).toLong()
            h.name = listParameterChkList[i].sType + "_" + listParameterChkList[i].sName2
            h.isSelected = false
            listArray0.add(h)
        }

        v.paraMultiSpinner.setItems(
            listArray0
        ) { items ->
            selectedPara = ""
            listSelectedPara = mutableListOf()
            for (i in items.indices) {
                if (items[i].isSelected) {
                    val p = listParameterChkList.filter {
                        it.sName2 == items[i].name.split("_")[1]
                    }
                    if (p.isNotEmpty()) {
                        // 組sql字串
                        // " ,ROUND((B.nPac)/1000,2) as nEa ,B.nOVol ,B.nOCur ,ROUND((B.nPpv)/1000,2) as nPpv...
                        if (p[0].sName == "nEa") {
                            selectedPara += "ROUND((B.nPac)/1000,2) as nEa, "
                        } else if (p[0].sName == "nPpv") {
                            selectedPara += "ROUND((B.nPpv)/1000,2) as nPpv, "
                        }  else  {
                            selectedPara = selectedPara + "B." + p[0].sName + ", "
                        }
                        listSelectedPara.add(p[0].sName)
                    }
                }
            }

            if (selectedPara.length > 2) {
                selectedPara = " ," + selectedPara.substring(0, selectedPara.length - 2)
                //println(selectedPara)
            }

        }
    }

    private fun getSiteData(d: String) {
        var funCode = "V01_SettingQuerypara01"
        var funValues = "'$sSiteNo_GLB';'$sZoneNo_GLB';'${d} 04:00';'${d} 19:59'"

        val formBody = FormBody.Builder()
            .add("FunCode", funCode)
            .add("FunValues", funValues)
            .build()

        val request = Request.Builder()
            .url(BaseUrl)
            .post(formBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val json = response.body!!.string() // 資料只能抓一次
                    listSiteData = Gson().fromJson(json, Array<SiteData>::class.java).toList()

                    if (selectedPara.length == 0 && selectedInv.length == 0) {
                        getActivity()?.runOnUiThread {
                            showAAChart()
                        }
                    } else {
                        // 取得Site Data後，再去抓Inv Data
                        getInverterStringData()
                    }

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("查詢失敗", "$e")
            }
        })
    }

    private fun getInverterStringData() {

        var funCode = "V01_MySolarToday03"
        var funValues = "$selectedPara;'$sSiteNo_GLB';'$sZoneNo_GLB';'${v.txtdate.text.toString()} 04:00';'${v.txtdate.text.toString()} 19:59';${selectedInv}"

        println(funValues)

        val formBody = FormBody.Builder()
            .add("FunCode", funCode)
            .add("FunValues", funValues)
            .build()

        val request = Request.Builder()
            .url(BaseUrl)
            .post(formBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val json = response.body!!.string() // 資料只能抓一次
                    listInvStringData = Gson().fromJson(json, Array<InvStringData>::class.java).toList()

                    // 抓完Inv Data後，確保listSiteData和listInvStringData都抓到資料，再進行合併
                    alignSiteDateAndInvStringData()
                    getActivity()?.runOnUiThread {
                        showAAChart()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("查詢失敗", "$e")
            }
        })
    }

    private fun alignSiteDateAndInvStringData() {

        // 看有幾台inverter
        for ((snid, v) in mapInvStringData) {

            mapInvStringData[snid] = mutableListOf()

            // 以siteData的sDataKey為主，找出InvStringData對應的sDataKey
            for (siteData in listSiteData) {


                val isInvExistDataKey = listInvStringData.filter {
                    it.sSNID == snid && it.sDataKey == siteData.sDataKey
                }

                // 如果用SiteData的sDataKey找不到Inverter對應DataKey的資料，就塞空值
                if (isInvExistDataKey.isEmpty()) {
                    val isInvExistSnid = listInverterChkList.filter {
                        it.sSNID == snid
                    }
                    var emptyInvStringData = InvStringData(siteData.sDataKey, isInvExistSnid[0].nRS485ID, snid, null, null, null, null, null,null,null,null,null,null,null,null,null,null,null,null)
                    mapInvStringData[snid]?.add(emptyInvStringData)
                }
                else
                {
                    // Inverter DataKey應該只會符合一筆，所以直接取0
                    mapInvStringData[snid]?.add(isInvExistDataKey[0])
                }
            }
        }

    }

    private fun showAAChart() {

        val xCategory = listSiteData.map { it.sDataKey.substring(8..9) + ":" + it.sDataKey.substring(10..11) }.toTypedArray()

        // 以SiteData的sDataKey當X軸
        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title(sSite_Name_GLB + " 串列分析")
            .subtitle(v.txtdate.text.toString())
            .animationType(AAChartAnimationType.EaseInOutExpo)
            .animationDuration(0)
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(true)
            .categories(xCategory)

        // 初始化左右Y軸
        val aaYAxisArray = mutableListOf<AAYAxis>()
        initLeftYAxis(aaYAxisArray)
        initRightYAxis(aaYAxisArray)

        // 塞左右Y軸資料
        val aaSeriesElementArray = mutableListOf<AASeriesElement>()
        addLeftYSeries(aaSeriesElementArray, xCategory)
        addRightYSeries(aaSeriesElementArray, xCategory)

        var aaOptions = aaChartModel.aa_toAAOptions()
        aaOptions.yAxisArray(aaYAxisArray.toTypedArray())
            .series(aaSeriesElementArray.toTypedArray())

        v.aa_chart_view2.aa_drawChartWithChartModel(aaChartModel)
        v.aa_chart_view2.aa_drawChartWithChartOptions(aaOptions)
    }

    private fun addRightYSeries(
        aaSeriesElementArray: MutableList<AASeriesElement>,
        xCategory: Array<String>
    ) {

        // for 案場
        for (i in listSelectedPara.indices) {
            var pData: Array<Any>
            pData = emptyArray<Any>()
            var yAxisIndex = 3
            var sName = ""
            when (listSelectedPara[i]) {
                "nHi"    ->  {
                    pData = listSiteData.map { it.nHi }.toTypedArray()
                    yAxisIndex = 3
                    sName = "案場日照計"
                }
                "nTmp"   -> {
                    pData = listSiteData.map { it.nTmp }.toTypedArray()
                    yAxisIndex = 4
                    sName = "案場溫度計"
                }
            }
            if (pData.isNotEmpty()) {
                val aaSeriesElement = AASeriesElement()
                    .name(sName)
                    .type(AAChartType.Spline)
                    .data(pData)
                    .yAxis(yAxisIndex)
                aaSeriesElementArray.add(aaSeriesElement)
            }

        }

        // for inverter
        for ((snid, listInv) in mapInvStringData) {
            for (i in listSelectedPara.indices) {
                var pData: Array<Any?>
                pData = emptyArray<Any?>()
                when (listSelectedPara[i]) {
                    "nOVol" -> pData = listInv.map { it.nOVol }.toTypedArray()
                    "nOCur" -> pData = listInv.map { it.nOCur }.toTypedArray()
                    "nVpv_A" -> pData = listInv.map { it.nVpv_A }.toTypedArray()
                    "nVpv_B" -> pData = listInv.map { it.nVpv_B }.toTypedArray()
                    "nVpv_C" -> pData = listInv.map { it.nVpv_C }.toTypedArray()
                    "nVpv_D" -> pData = listInv.map { it.nVpv_D }.toTypedArray()
                    "nVpv_E" -> pData = listInv.map { it.nVpv_E }.toTypedArray()
                    "nVpv_F" -> pData = listInv.map { it.nVpv_F }.toTypedArray()
                    "nIpv_A" -> pData = listInv.map { it.nVpv_A }.toTypedArray()
                    "nIpv_B" -> pData = listInv.map { it.nVpv_B }.toTypedArray()
                    "nIpv_C" -> pData = listInv.map { it.nVpv_C }.toTypedArray()
                    "nIpv_D" -> pData = listInv.map { it.nVpv_D }.toTypedArray()
                    "nIpv_E" -> pData = listInv.map { it.nVpv_E }.toTypedArray()
                    "nIpv_F" -> pData = listInv.map { it.nVpv_F }.toTypedArray()
                }

                if (pData.isNotEmpty()) {
                    var yAxisIndex = 1
                    if (listSelectedPara[i].contains("nOVol") || listSelectedPara[i].contains("nVpv")) {
                        yAxisIndex = 1
                    } else if (listSelectedPara[i].contains("nOCur") || listSelectedPara[i].contains("nIpv")) {
                        yAxisIndex = 2
                    }

                    // AAChart處理空值的方法
                    /*
                        val pData : MutableList<MutableList<Any?>> = mutableListOf()
                        pData.add(arrayListOf(1, 5))
                        pData.add(arrayListOf(2, null))
                        pData.add(arrayListOf(3, 7))
                     */
                    val pDataWithNull : MutableList<MutableList<Any?>> = mutableListOf()
                    for (i in xCategory.indices) {
                        pDataWithNull.add(mutableListOf(xCategory[i], pData[i]))
                    }

                    val aaSeriesElement = AASeriesElement()
                        .name(snid + " : " + listInv[0].nRS485ID + " " + listSelectedPara[i])
                        .type(AAChartType.Spline)
                        .data(pDataWithNull.toTypedArray())
                        .yAxis(yAxisIndex)
                    aaSeriesElementArray.add(aaSeriesElement)
                }
            }
        }
    }

    private fun addLeftYSeries(
        aaSeriesElementArray: MutableList<AASeriesElement>,
        xCategory: Array<String>
    ) {
        aaSeriesElementArray.add(
            AASeriesElement()
                .name("案場發電功率")
                .data(listSiteData.map { it.nEa }.toTypedArray())
                .yAxis(0)
        )

        for ((snid, listInv) in mapInvStringData) {
            for (i in listSelectedPara.indices) {
                var pData: Array<Any?>
                pData = emptyArray<Any?>()

                when (listSelectedPara[i]) {
                    "nEa" -> pData = listInv.map { it.nEa }.toTypedArray()
                    "nPpv" -> pData = listInv.map { it.nPpv }.toTypedArray()
                }

                if (pData.isNotEmpty()) {
                    var p = listParameterChkList.filter {
                        it.sName == listSelectedPara[i]
                    }

                    val pDataWithNull : MutableList<MutableList<Any?>> = mutableListOf()
                    for (i in xCategory.indices) {
                        pDataWithNull.add(mutableListOf(xCategory[i], pData[i]))
                    }

                    aaSeriesElementArray.add(
                        AASeriesElement()
                            .name(snid + " : " + listInv[0].nRS485ID + " " + p[0].sName2.toString())
                            .type(AAChartType.Spline)
                            .data(pDataWithNull.toTypedArray())
                            .yAxis(0)
                    )
                }
            }
        }
    }

    private fun initRightYAxis(aaYAxisArray: MutableList<AAYAxis>) {
        // 伏特(V)
        val aaYAxis1 = AAYAxis()
            .visible(false)
            .labels(
                AALabels()
                    .enabled(true)//设置 y 轴是否显示数字
            )
            .opposite(true)
            .title(AATitle().text("伏特(V)"))
            .min(0f)
        if (listSelectedPara.contains("nOVol") || listSelectedPara.contains("nVpv")) {
            aaYAxis1.visible = true
        }
        aaYAxisArray.add(aaYAxis1)

        // 安培(A)
        val aaYAxis2 = AAYAxis()
            .visible(false)
            .labels(
                AALabels()
                    .enabled(true)//设置 y 轴是否显示数字
            )
            .opposite(true)
            .title(AATitle().text("安培(A)"))
            .min(0f)
        if (listSelectedPara.contains("nOCur") || listSelectedPara.contains("nIpv")) {
            aaYAxis2.visible = true
        }
        aaYAxisArray.add(aaYAxis2)

        // W/㎡
        val aaYAxis3 = AAYAxis()
            .visible(false)
            .labels(
                AALabels()
                    .enabled(true)//设置 y 轴是否显示数字
            )
            .opposite(true)
            .title(AATitle().text("W/㎡"))
            .min(0f)
        if (listSelectedPara.contains("nHi")) {
            aaYAxis3.visible = true
        }
        aaYAxisArray.add(aaYAxis3)

        // ℃
        val aaYAxis4 = AAYAxis()
            .visible(false)
            .labels(
                AALabels()
                    .enabled(true)//设置 y 轴是否显示数字
            )
            .opposite(true)
            .title(AATitle().text("℃"))
            .min(0f)
        if (listSelectedPara.contains("nTmp")) {
            aaYAxis4.visible = true
        }
        aaYAxisArray.add(aaYAxis4)
    }

    private fun initLeftYAxis(aaYAxisArray: MutableList<AAYAxis>) {
        // 左邊Y軸設定 (Site nEa, Inv nEa, Inv nPpv 在左邊)
        val aaYAxis0 = AAYAxis()
            .visible(true)
            .labels(
                AALabels()
                    .enabled(true)//设置 y 轴是否显示数字
                    .style(
                        AAStyle()
                            .color("#ff0000")//yAxis Label font color
                            .fontSize(12f)//yAxis Label font size
                            .fontWeight(AAChartFontWeightType.Bold)//yAxis Label font weight
                    )
            )
            .gridLineWidth(0f)// Y 轴网格线宽度
            .title(AATitle().text("即時發電功率(kW)"))//Y 轴标题
            .min(0f)


        aaYAxisArray.add(aaYAxis0)
    }

    private fun setDateFormat(year: Int, month: Int, day: Int): String {
        var strMonth : String
        var strDay : String
        if (month < 10) {
            strMonth = "0" + (month+1).toString()
        } else {
            strMonth = (month+1).toString()
        }

        if(day < 10){
            strDay  = "0" + day.toString()
        } else {
            strDay = day.toString()
        }

        return "$year-${strMonth}-$strDay"
    }
}