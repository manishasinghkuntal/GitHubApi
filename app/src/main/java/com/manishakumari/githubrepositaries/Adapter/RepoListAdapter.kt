package com.jeevansatya.githubsearcher.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jeevansatya.githubsearcher.Model.RepoResponse
import com.manishakumari.githubrepositaries.R
import retrofit2.Retrofit
import java.util.*


class RepoListAdapter (private val context: Context?,
                         var itemList: ArrayList<RepoResponse>?): RecyclerView.Adapter<RepoListAdapter.ItemViewHolder>(),Filterable {
    var retrofit: Retrofit? = null
    val BASEURL = " https://api.github.com"
    var responseBody: List<RepoResponse>? = null
    lateinit var intent: Intent
    var itemListFilter = ArrayList<RepoResponse>()
    init {
        itemListFilter = itemList!!
    }



    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.activity_repo_list_adapter, viewGroup, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemListFilter!!.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.repoName.text = itemListFilter!![position].name
        holder.fork.text = "Forks : "+itemListFilter!![position].forks.toString()
        holder.stars.text = "Stars : "+itemListFilter!![position].watchers.toString()
        holder.card.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(""+itemListFilter!![position].htmlUrl))
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(browserIntent)
        }
    }



    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var repoName: TextView
        var fork: TextView
        var stars: TextView
        var card: CardView

        init {
            repoName = itemView.findViewById(R.id.reponame)
            fork = itemView.findViewById(R.id.fork)
            stars = itemView.findViewById(R.id.stars)
            card = itemView.findViewById(R.id.repocard)
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    itemListFilter = itemList!!
                } else {
                    val resultList = ArrayList<RepoResponse>()
                    for (row in itemList!!) {
                        if (row.name!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    itemListFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = itemListFilter
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemListFilter = results?.values as ArrayList<RepoResponse>
                notifyDataSetChanged()
            }

        }
    }
}
