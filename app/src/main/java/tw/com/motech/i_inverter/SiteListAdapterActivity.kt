package tw.com.motech.i_inverter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class SiteListAdapterActivity(private val siteresults:List<SiteResult>) : RecyclerView.Adapter<SiteListAdapterActivity.ViewHolder>(), Filterable {
    var siteresultsFilterList = emptyList<SiteResult>()

    init {
        siteresultsFilterList = siteresults
        //siteresultsFilterList
    }

    class ViewHolder(v: View):RecyclerView.ViewHolder(v){
        val img_sitestate = v.findViewById<ImageButton>(R.id.img_SiteState)
        val txt_sitename = v.findViewById<TextView>(R.id.txt_SiteName)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.activity_site_list_adapter_row, viewGroup,false)
        return  ViewHolder(v)
    }

    // override fun getItemCount() = siteresults.count()
    override fun getItemCount() : Int{
        return siteresultsFilterList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (siteresultsFilterList[position].nSHI) {
            1 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_good)
                holder.img_sitestate.setBackgroundResource(R.drawable.higood_background_color)
            }
            2 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_good_new)
                holder.img_sitestate.setBackgroundResource(R.drawable.higood_background_color)
            }
            -3 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_alert)
                holder.img_sitestate.setBackgroundResource(R.drawable.hialert_background_color)
            }
            -4 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_alert_new)
                holder.img_sitestate.setBackgroundResource(R.drawable.hialert_background_color)
            }
            -5 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_bad)
                holder.img_sitestate.setBackgroundResource(R.drawable.hibad_background_color)
            }
            -6 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_bad_new)
                holder.img_sitestate.setBackgroundResource(R.drawable.hibad_background_color)
            }
            0 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_skip)
                holder.img_sitestate.setBackgroundResource(R.drawable.hiskip_background_color)
            }
            -1 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_err)
                holder.img_sitestate.setBackgroundResource(R.drawable.hierr_background_color)
            }
            3 -> {
                holder.img_sitestate.setImageResource(R.drawable.hi_sleep)
                holder.img_sitestate.setBackgroundResource(R.drawable.hisleep_background_color)
            }
        }
        holder.txt_sitename.text = siteresultsFilterList[position].sSite_Name
        holder.img_sitestate.setOnClickListener {
            sSiteNo_GLB = siteresultsFilterList[position].sSiteNo
            sSite_Name_GLB = siteresultsFilterList[position].sSite_Name
            //Toast.makeText(holder.itemView.context, "切到${siteresultsFilterList[position].sSiteNo}案場", Toast.LENGTH_SHORT).show()
            holder.itemView.context.startActivity(Intent(holder.itemView.context, SiteFuncActivity::class.java))
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    siteresultsFilterList = siteresults
                } else {
                    var resultList = siteresultsFilterList.filter { it.sSite_Name.contains(charSearch) }
                    siteresultsFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = siteresultsFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                siteresultsFilterList = results?.values as List<SiteResult>
                notifyDataSetChanged()

            }

        }
    }
}