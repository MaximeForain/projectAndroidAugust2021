package com.example.myapplication.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.model.*
import com.example.myapplication.ui.viewModel.HomeViewModel
import java.util.*
import kotlin.collections.ArrayList


// .---------------------------------------------------------------------.
// |                            HomeFragment                             |
// '---------------------------------------------------------------------'
class HomeFragment: Fragment() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private lateinit var binding : FragmentHomeBinding
    private lateinit var sharedPref : SharedPreferences
    private lateinit var homeViewModel : HomeViewModel
    private lateinit var recyclerview: RecyclerView
    private lateinit var adapter: CustomAdapter
    private lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Initialize the view
        initView()

        // Get preferences
        sharedPref  = requireActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)
        val token   = sharedPref.getString(getString(R.string.token), "")!!
        val exp     = sharedPref.getLong(getString(R.string.exp_date_payload), 0)
        val expDate = Date(exp * 1000)
        userId      = sharedPref.getString(getString(R.string.user_id), "")!!

        val jwt =  Token(expDate, token, userId)

        // Observe the Error comming of ViewModel
        homeViewModel.error.observe(viewLifecycleOwner) {
            error: Error -> this.displayErrorScreen(error)
        }

        // Initialization of recyclerview
        recyclerview = binding.recyclerview                             // Getting the recyclerview by its id
        recyclerview.layoutManager = LinearLayoutManager(this.context)  // This creates a vertical layout Manager

        // Get the data to display them as follows
        homeViewModel.setUserId(userId)
        homeViewModel.getBars(jwt)

        // Initializes the recyclerView
        initRecyclerViewAdapter()

        // Observe and acces to data in view model
        homeViewModel.isShowingBetterBars.observe(viewLifecycleOwner) { value ->
            println(value)
            if (value) showBetterBars() else showAllBars()
        }

        homeViewModel.isShowingFavoriteBars.observe(viewLifecycleOwner) { value ->
            println(value)
            if (value) showFavoriteBars() else showAllBars()
        }

        // Gestion of the research
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                homeViewModel.filledBars.observe(viewLifecycleOwner) { bars ->
                    val barslist: List<Bar> = bars.filter {
                        bar -> bar.barname.toLowerCase(Locale.ROOT).startsWith(searchText.toString().toLowerCase(Locale.ROOT))
                    }

                    binding.noBarFound.visibility = if (barslist.isEmpty()) View.VISIBLE else View.GONE

                    adapter.setBars(barslist)
                }

                return false
            }
        })

        // Observe the button used to show the menu that filters the bars
        binding.filterButton.setOnClickListener { showPopupFilter(binding.filterButton) }

        return binding.root
    }

    // .---------------------------------------------------------------------.
    // |                             initView                                |
    // '---------------------------------------------------------------------'
    private fun initView() {
        binding.barsList.visibility     = View.GONE
        binding.errorLayout.visibility  = View.GONE
        binding.progressBar.visibility  = View.VISIBLE
        binding.noBarFound.visibility   = View.GONE
    }

    // .---------------------------------------------------------------------.
    // |                            showPopupFilter                          |
    // '---------------------------------------------------------------------'
    private fun showPopupFilter(view: View) {
        val popup = PopupMenu(activity, view)
        popup.inflate(R.menu.filter_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.header1 -> {
                    setStateOfDisplayBar(isShowingBetterBars = false, isShowingFavoriteBars = true)
                    showFavoriteBars()
                }
                R.id.header2 -> {
                    setStateOfDisplayBar(isShowingBetterBars = true, isShowingFavoriteBars = false)
                    showBetterBars()
                }
                R.id.header3 -> {
                    setStateOfDisplayBar(isShowingBetterBars = false, isShowingFavoriteBars = false)
                    showAllBars()
                }
            }
            true
        }

        popup.show()
    }

    private fun showBetterBars() {
        homeViewModel.filledBars.observe(viewLifecycleOwner) { bars ->
            val barslist: List<Bar> = bars.filter { bar -> bar.average > 3 }

            binding.noBarFound.visibility = if (barslist.isEmpty()) View.VISIBLE else View.GONE

            adapter.setBars(barslist)
        }
    }

    private fun showFavoriteBars() {
        homeViewModel.filledBars.observe(viewLifecycleOwner) {  bars ->
            val barslist: List<Bar> = bars.filter { bar -> bar.isFavorite }

            binding.noBarFound.visibility = if (barslist.isEmpty()) View.VISIBLE else View.GONE

            adapter.setBars(barslist)
        }
    }

    private fun showAllBars() {
        homeViewModel.filledBars.observe(viewLifecycleOwner) { bars ->
            adapter.setBars(bars)
        }
    }

    // Keep the display state in the model view
    private fun setStateOfDisplayBar(isShowingBetterBars: Boolean, isShowingFavoriteBars: Boolean) {
        homeViewModel.setIsShowingBetterBars(isShowingBetterBars)
        homeViewModel.setIsShowingFavoriteBars(isShowingFavoriteBars)
    }


    // .---------------------------------------------------------------------.
    // |                Fill the recyclerview and add listener               |
    // '---------------------------------------------------------------------'
    private fun initRecyclerViewAdapter() {
        adapter = CustomAdapter()
        recyclerview.adapter = adapter

        // Set on item click listener for all items in recyclerView
        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {

                val barToPass: Bar = adapter.getBars()[position]

                val barFragment: Bundle = BarFragment.newArguments(
                        barToPass.bar_id, barToPass.barname, barToPass.webaddress, barToPass.phonenumber, barToPass.hashtags,
                        barToPass.description, barToPass.address, barToPass.numberOfReview, barToPass.average,
                        barToPass.isAlreadyEvaluatedByUseur, barToPass.sumDegrees, barToPass.isFavorite
                )

                NavHostFragment.findNavController(this@HomeFragment).navigate(R.id.action_homeFragment_to_barFragment, barFragment)
            }
        })
    }


    // .---------------------------------------------------------------------.
    // |                               Error                                 |
    // '---------------------------------------------------------------------'
    private fun displayErrorScreen(error: Error){
        binding.progressBar.visibility = View.GONE
        when(error){
            Error.NO_ERROR -> { assignVisibility(barsList = true, errorLayout = false, connectivityError = false,
                        connectivityErrorImage = false, requestError = false, technicalError = false, errorImage = false)
            }
            Error.REQUEST_ERROR -> { assignVisibility(barsList = false, errorLayout = true, connectivityError = false,
                        connectivityErrorImage = false, requestError = true, technicalError = false, errorImage = true)
            }
            Error.TECHNICAL_ERROR -> { assignVisibility(barsList = false, errorLayout = true, connectivityError = false,
                        connectivityErrorImage = false, requestError = false, technicalError = true, errorImage = true)
            }
            else -> { assignVisibility(barsList = false, errorLayout = true, connectivityError = true,
                        connectivityErrorImage = true, requestError = false, technicalError = false, errorImage = false)
            }
        }
    }

    private fun assignVisibility(
            barsList: Boolean, errorLayout: Boolean, connectivityError: Boolean,
            connectivityErrorImage: Boolean, requestError: Boolean,
            technicalError: Boolean, errorImage: Boolean
    ) {
        binding.barsList.visibility                 = if (barsList) View.VISIBLE else View.GONE
        binding.errorLayout.visibility              = if (errorLayout) View.VISIBLE else View.GONE
        binding.connectivityError.visibility        = if (connectivityError) View.VISIBLE else View.GONE
        binding.connectivityErrorImage.visibility   = if (connectivityErrorImage) View.VISIBLE else View.GONE
        binding.requestError.visibility             = if (requestError) View.VISIBLE else View.GONE
        binding.technicalError.visibility           = if (technicalError) View.VISIBLE else View.GONE
        binding.errorImage.visibility               = if (errorImage) View.VISIBLE else View.GONE
    }


    // .---------------------------------------------------------------------.
    // |                              Adapter                                |
    // '---------------------------------------------------------------------'
    inner class CustomAdapter : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        private var bars: List<Bar> = ArrayList()
        private lateinit var mListener: OnItemClickListener

        fun setOnItemClickListener(listener: OnItemClickListener) { mListener = listener }

        // create new views
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // inflates the card_view_design view
            // that is used to hold list item
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_bar, parent, false)

            return ViewHolder(view, mListener)
        }

        // binds the list items to a view
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val bar: Bar = bars[position]

            // sets the text to the textview from our itemHolder class
            holder.barnameTextView.text     = bar.barname
            holder.phonenumberTextView.text = bar.phonenumber
            holder.webAdresseTextView.text  = bar.webaddress
            holder.adresseTextView.text     = bar.address
            holder.descriptionTextView.text = bar.description
            holder.imageFavorite.visibility = View.GONE
            holder.imageFavorite.visibility = if (bar.isFavorite) View.VISIBLE else View.GONE

            setRatingBar(holder, bar)
        }

        // Calculate average score and set ratingBar
        private fun setRatingBar(holder: ViewHolder, bar: Bar) {
            holder.scorTextView.text = if (bar.average == 0f) "" else String.format("%.1f",  bar.average)
            holder.ratingBar.rating = bar.average
            holder.nbReviewTextView.text = getString(R.string.number_of_review, bar.numberOfReview.toString())
        }

        // return the number of the items in the list
        override fun getItemCount(): Int { return bars.size }

        // Holds the views for adding it to image and text
        inner class ViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

            val barnameTextView: TextView       = itemView.findViewById(R.id.barnameTextView)
            val phonenumberTextView: TextView   = itemView.findViewById(R.id.phonenumberTextView)
            val webAdresseTextView: TextView    = itemView.findViewById(R.id.webAdresseTextView)
            val adresseTextView: TextView       = itemView.findViewById(R.id.adresseTextView)
            val descriptionTextView: TextView   = itemView.findViewById(R.id.descriptionTextView)
            val scorTextView: TextView          = itemView.findViewById(R.id.scorTextView)
            val nbReviewTextView: TextView      = itemView.findViewById(R.id.nbReviewTextView)
            val imageFavorite: ImageView        = itemView.findViewById(R.id.imageFavorite)

            val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)

            init {
                itemView.setOnClickListener{
                    listener.onItemClick(absoluteAdapterPosition)
                }
            }
        }

        fun setBars(value: List<Bar>) {
            this.bars = value
            notifyDataSetChanged()
        }

        fun getBars(): List<Bar> { return this.bars }
    }
}

