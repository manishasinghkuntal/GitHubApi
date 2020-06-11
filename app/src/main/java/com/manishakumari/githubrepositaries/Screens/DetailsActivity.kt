package com.manishakumari.githubrepositaries.Screens

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeevansatya.githubsearcher.Adapter.RepoListAdapter
import com.jeevansatya.githubsearcher.Interface.ApiInterface
import com.jeevansatya.githubsearcher.Model.RepoResponse
import com.jeevansatya.githubsearcher.Model.UserDetailsResponse
import com.jeevansatya.githubsearcher.Utils.UtilsClass
import com.manishakumari.githubrepositaries.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {

    var nameData:String? = null
    var retrofit: Retrofit? = null
    var responseBody:UserDetailsResponse? = null
    var repoResponseBody:List<RepoResponse>? = null
    var repoArrayList = ArrayList<RepoResponse>()
    var recyclerView: RecyclerView?=null
    var repoListAdapter: RepoListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setupUI()
        apiCall()
        repoapicall()

        reposearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                repoListAdapter!!.filter.filter(newText)
                return false
            }

        })
    }

    private fun repoapicall() {
        retrofit =Retrofit.Builder()
            .baseUrl(UtilsClass.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit!!.create(ApiInterface::class.java)
        val call = service.getRepoList(nameData!!,UtilsClass.authTokenforapicall)

        call.enqueue(object : Callback<List<RepoResponse>> {
            override fun onResponse(call: Call<List<RepoResponse>>, response: Response<List<RepoResponse>>) {
                if (response.code() == 200 && response.body()!=null) {
                    repoResponseBody = response.body()
                    repoArrayList.clear()
                    for (i in 0.. repoResponseBody!!.size-1) {
                        repoArrayList.add(repoResponseBody!![i])
                    }
                    loadingdetails.visibility = View.INVISIBLE
                    repoListAdapter = RepoListAdapter(applicationContext,repoArrayList)
                    recyclerView!!.adapter = repoListAdapter

                }
                else{
                    Toast.makeText(applicationContext, "Unable to fetch repo data :(", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<RepoResponse>>, t: Throwable) {
                Toast.makeText(applicationContext, "Please check your internet connection and try again :(", Toast.LENGTH_LONG).show()
            }
        })

    }



    private fun apiCall() {

        retrofit =Retrofit.Builder()
            .baseUrl(UtilsClass.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit!!.create(ApiInterface::class.java)
        val call = service.getUserDetails(nameData!!, UtilsClass.authTokenforapicall)

        call.enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                if (response.code() == 200 && response.body()!=null) {
                    responseBody = response.body()

                    username_details.text = "UserName : "+ responseBody!!.name
                    Picasso.get().load(responseBody!!.avatarUrl)
                        .into(userimage_details)
                    email_details.text = "Email : "+responseBody!!.email
                    location_details.text = "Location : "+responseBody!!.location
                    joindate_details.text = "Join Date : "+responseBody!!.createdAt
                    follower_details.text = "Followers : "+ responseBody!!.followers.toString()
                    following_details.text = "Following : "+responseBody!!.following.toString()

                }
                else{
                    Toast.makeText(applicationContext, "Unable to fetch user data :(", Toast.LENGTH_LONG).show()
                    loadingdetails.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Please check your internet connection and try again :(", Toast.LENGTH_LONG).show()
                loadingdetails.visibility = View.INVISIBLE
            }
        })
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewdetails)
        val searchIcon = reposearch.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.GRAY)
        val cancelIcon = reposearch.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.GRAY)
        val textView = reposearch.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(Color.GRAY)
        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        nameData = intent.getStringExtra("name")
    }


}
