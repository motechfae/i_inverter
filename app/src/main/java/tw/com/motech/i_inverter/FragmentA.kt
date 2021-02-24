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
import kotlinx.android.synthetic.main.fragment_a.view.*
import tw.com.motech.i_inverter.R

class FragmentA : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_a, container, false)
        //v.txtfrag1.setText("Fragment A {$sSiteNo_GLB}")
        //v.txtfrag2.setText("Hello A {$sSite_Name_GLB}")
        /*
        val txt1:TextView = v.findViewById(R.id.txtfrag1) as TextView
        val txt2:TextView = v.findViewById(R.id.txtfrag2) as TextView
        txt1.setText("Fragment A {$sSiteNo_GLB}")
        txt2.setText("Hello A {$sSite_Name_GLB}")
         */

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title(sSite_Name_GLB)
            .subtitle(sSiteNo_GLB)
            .animationType(AAChartAnimationType.EaseInOutExpo)
            .animationDuration(0)
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(true)
            .categories(arrayOf("一月","二月","三月"))
            .series(arrayOf(
                AASeriesElement()
                    .name("發電量1")
                    .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
                AASeriesElement()
                    .name("發電量2")
                    .type(AAChartType.Spline)
                    .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5))
            )
            )

        var aaOptions = aaChartModel.aa_toAAOptions()

        val aaYAxis1 = AAYAxis()
            .visible(true)
            .labels(AALabels()
                .enabled(true)//设置 y 轴是否显示数字
                .style(AAStyle()
                    .color("#ff0000")//yAxis Label font color
                    .fontSize(12f)//yAxis Label font size
                    .fontWeight(AAChartFontWeightType.Bold)//yAxis Label font weight
                ))
            .gridLineWidth(0f)// Y 轴网格线宽度
            .title(AATitle()
                .text("發電量"))//Y 轴标题

        val aaYAxis2 = AAYAxis()
            .visible(true)
            .opposite(true)
            .title(AATitle().text("日照量"))

        val aaYAxis3 = AAYAxis()
            .visible(true)
            .opposite(true)
            .title(AATitle().text("溫度"))

        aaOptions.yAxisArray(arrayOf(aaYAxis1, aaYAxis2, aaYAxis3))

        v.aa_chart_view.aa_drawChartWithChartModel(aaChartModel)
        v.aa_chart_view.aa_drawChartWithChartOptions(aaOptions)

        return v
    }
}