package com.manishakumari.githubrepositaries.Screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.jeevansatya.githubsearcher.Adapter.ItemsListAdapter
import com.jeevansatya.githubsearcher.Interface.ApiInterface
import com.jeevansatya.githubsearcher.Model.UserListResponse
import com.jeevansatya.githubsearcher.Utils.UtilsClass
import com.jeevansatya.githubsearcher.Utils.UtilsClass.BASEURL
import com.manishakumari.githubrepositaries.R
import com.wang.avi.AVLoadingIndicatorView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    var retrofit: Retrofit? = null
    var userListarrayList = ArrayList<UserListResponse>()
    var responseBody:List<UserListResponse>? = null
    var recyclerView: RecyclerView?=null
    var itemsListAdapter: ItemsListAdapter? = null
    var loading: AVLoadingIndicatorView?=null
    lateinit var alertDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupUI()
        apiCall()

        usersearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemsListAdapter!!.filter.filter(newText)
                return false
            }

        })

    }

    private fun apiCall() {

        retrofit = Retrofit.Builder()
            .baseUrl(UtilsClass.BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit!!.create(ApiInterface::class.java)
        val call = service.getUsersList(UtilsClass.authTokenforapicall)


        call.enqueue(object : Callback<List<UserListResponse>> {
            override fun onResponse(call: Call<List<UserListResponse>>, response: Response<List<UserListResponse>>) {
                if (response.code() == 200 && response.body()!=null) {
                    responseBody = response.body()
                    userListarrayList.clear()
                    for (i in 0.. responseBody!!.size-1) {
                        userListarrayList.add(responseBody!![i])
                    }
                    loading!!.visibility = View.INVISIBLE
                    itemsListAdapter = ItemsListAdapter(applicationContext,userListarrayList)
                    recyclerView!!.adapter = itemsListAdapter
                }
                else{
                    loading!!.visibility = View.INVISIBLE
                    alertDialog = AlertDialog.Builder(this@MainActivity)
                    alertDialog.setTitle("OOPS!")
                    alertDialog.setMessage("Unable to fetch the data :(")
                    alertDialog.setNeutralButton("Try again"){dialogInterface , which ->
                        apiCall()
                    }
                    alertDialog.setNegativeButton("Cancel"){dialogInterface , which ->
                        clickme.visibility = View.VISIBLE
                    }
                    alertDialog.show()
                    // Toast.makeText(applicationContext, "Unable to fetch the data :(", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<UserListResponse>>, t: Throwable) {
                loading!!.visibility = View.INVISIBLE
                alertDialog = AlertDialog.Builder(this@MainActivity)
                alertDialog.setTitle("OOPS!")
                alertDialog.setMessage("Please check your internet connection and try again :(")
                alertDialog.setNeutralButton("Try again"){dialogInterface , which ->
                    apiCall()
                }
                alertDialog.setNegativeButton("Cancel"){dialogInterface , which ->
                    clickme.visibility = View.VISIBLE
                }
                alertDialog.show()

                //  Toast.makeText(applicationContext, "Please check your internet connection and try again :(", Toast.LENGTH_LONG).show()
            }
        })


    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView!!.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        loading = findViewById(R.id.loading)

        clickme.setOnClickListener {
            loading!!.visibility = View.VISIBLE
            clickme.visibility = View.INVISIBLE
            apiCall()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                startActivity(
                    SignInActivity.getLaunchIntent(
                        this
                    )
                )
                overridePendingTransition(0,0)
                FirebaseAuth.getInstance().signOut()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
