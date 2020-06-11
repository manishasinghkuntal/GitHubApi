package com.jeevansatya.githubsearcher.Adapter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.jeevansatya.githubsearcher.Interface.ApiInterface
import com.jeevansatya.githubsearcher.Model.RepoResponse
import com.jeevansatya.githubsearcher.Model.UserListResponse
import com.jeevansatya.githubsearcher.Utils.UtilsClass
import com.manishakumari.githubrepositaries.R
import com.manishakumari.githubrepositaries.Screens.DetailsActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class ItemsListAdapter(private val context: Context?,
                       var itemList: ArrayList<UserListResponse>?): RecyclerView.Adapter<ItemsListAdapter.ItemViewHolder>(),
    Filterable {

    val REGULAR_ITEM = 0
    val FOOTER_ITEM = 1
    var retrofit: Retrofit? = null
    val BASEURL = " https://api.github.com"
    var responseBody:List<RepoResponse>? = null
    lateinit var intent:Intent
    var itemListFilter = ArrayList<UserListResponse>()
    init {
        itemListFilter = itemList!!
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.activity_items_list_adapter, viewGroup, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemListFilter!!.size
    }

    override fun onBindViewHolder(myViewHolder: ItemViewHolder, position: Int) {

        if(itemListFilter!!.size == position){

        }

        Picasso.get().load(itemListFilter!![position].avatarUrl)
            .into(myViewHolder.profileImg)

       myViewHolder.userName.text = itemListFilter!![position].login

        retrofit =Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit!!.create(ApiInterface::class.java)
        val call = service.getRepoList(itemListFilter!![position].login.toString(),UtilsClass.authTokenforapicall)

        call.enqueue(object : Callback<List<RepoResponse>> {
            override fun onResponse(call: Call<List<RepoResponse>>, response: Response<List<RepoResponse>>) {
                if (response.code() == 200 && response.body()!=null) {
                    responseBody = response.body()
                    myViewHolder.repoCount.text = responseBody!!.size.toString()
                }
                else{
                     Toast.makeText(context, "Unable to fetch repo data :(", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<RepoResponse>>, t: Throwable) {
                  Toast.makeText(context, "Please check your internet connection and try again :(", Toast.LENGTH_LONG).show()
            }
        })

        myViewHolder.recyclerCard.setOnClickListener {
            intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra("name", itemListFilter!![position].login)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        }

    }

    inner class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var profileImg: CircleImageView
        var userName: TextView
        var repoCount: TextView
        var recyclerCard: CardView

        init {
            userName = itemView.findViewById(R.id.user_name)
            repoCount = itemView.findViewById(R.id.repo_count)
            profileImg = itemView.findViewById(R.id.profile_img)
            recyclerCard = itemView.findViewById(R.id.recycler_card)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    itemListFilter = itemList!!
                } else {
                    val resultList = ArrayList<UserListResponse>()
                    for (row in itemList!!) {
                        if (row.login!!.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(
                                Locale.ROOT))) {
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
                itemListFilter = results?.values as ArrayList<UserListResponse>
                notifyDataSetChanged()
            }

        }
    }

}
