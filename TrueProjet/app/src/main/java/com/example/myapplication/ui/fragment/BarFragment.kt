package com.example.myapplication.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBarBinding
import com.example.myapplication.model.*
import com.example.myapplication.ui.viewModel.BarViewModel
import java.util.*
import kotlin.properties.Delegates


// .---------------------------------------------------------------------.
// |                            BarFragment                              |
// '---------------------------------------------------------------------'
class BarFragment: Fragment() {

    private lateinit var binding: FragmentBarBinding
    private lateinit var barViewModel: BarViewModel
    private lateinit var sharedPref: SharedPreferences
    private var reviewToSend: ReviewToSend = ReviewToSend(null, null)
    private var newValueDegree by Delegates.notNull<Int>()
    private var isFavoriteBar: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentBarBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        barViewModel = ViewModelProvider(this).get(BarViewModel::class.java)

        // Get preferences
        sharedPref  = requireActivity().getSharedPreferences(getString(R.string.sharedPref), Context.MODE_PRIVATE)
        val token   = sharedPref.getString(getString(R.string.token), "")!!
        val exp     = sharedPref.getLong(getString(R.string.exp_date_payload), 0)
        val expDate = Date(exp * 1000)
        val userId  = sharedPref.getString(getString(R.string.user_id), "")!!

        val jwt =  Token(expDate, token, userId)

        // Allows you to manage the rollback to the parent fragment
        val callbacks = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                NavHostFragment.findNavController(this@BarFragment).navigate(R.id.action_barFragment_to_homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callbacks)

        // Observe the Error comming of ViewModel
        barViewModel.error.observe(viewLifecycleOwner) {
            error: Error -> this.displayErrorScreen(error)
        }

        // Initialize the view
        initView()

        // Observe if if appreciation button is press and update view
        binding.appreciationButton.setOnClickListener {
            binding.bar.gravity = Gravity.NO_GRAVITY
            binding.appreciationButton.visibility = View.GONE
            binding.giveAppreciation.visibility = View.VISIBLE
        }

        // Observe and retrieve the value of ratingBar
        binding.getRatingBar.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener {
            _, valueRating, _ ->
                newValueDegree = valueRating.toInt()
                reviewToSend = ReviewToSend(valueRating.toInt(),requireArguments().getString(ARG_BARID)!!.toInt())
        }

        binding.favoriteButton.setOnClickListener {
            val color: Int

            if (isFavoriteBar)  {
                barViewModel.deleteToFavorite(jwt, requireArguments().getString(ARG_BARID)!!.toInt())
                color = R.color.midDarck
                isFavoriteBar = false
            } else {
                barViewModel.addToFavorite(jwt, requireArguments().getString(ARG_BARID)!!.toInt())
                color = R.color.myYellow
                isFavoriteBar = true
            }

            binding.favoriteButton.setColorFilter(ContextCompat.getColor(this.requireContext(), color))
        }

        // Observe if validation button of appreciation is press
        binding.validatButton.setOnClickListener {
            if (reviewToSend.reviewdegree != null) {
                barViewModel.addReview(reviewToSend, jwt)
                binding.giveAppreciation.visibility = View.GONE
                updateRatingBar(newValueDegree)
            } else {
                Toast.makeText(activity, R.string.validation_warning, Toast.LENGTH_SHORT).show()
            }
        }

        //
        binding.cencelButton.setOnClickListener {
            binding.appreciationButton.visibility = View.VISIBLE
            binding.giveAppreciation.visibility = View.GONE
        }

        //
        binding.accessCartbutton.setOnClickListener {
            Toast.makeText(activity, R.string.not_yet_accessible_warning, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    // .---------------------------------------------------------------------.
    // |                             initView                                |
    // '---------------------------------------------------------------------'
    private fun initView() {
        fillBarView()

        // init visibility
        binding.appreciationButton.visibility   = View.VISIBLE
        binding.giveAppreciation.visibility     = View.GONE
        binding.errorLayout.visibility          = View.GONE
        binding.progressBar.visibility          = View.GONE

        //
        isFavoriteBar = requireArguments().getBoolean(ARG_ISFAVORITEBAR)
        val color: Int  = if (isFavoriteBar) R.color.myYellow else R.color.midDarck
        binding.favoriteButton.setColorFilter(ContextCompat.getColor(this.requireContext(), color))

        // if the user has already given an appreciation
        if (requireArguments().getBoolean(ARG_ISALREADYEVALUATED)) binding.appreciationButton.visibility = View.GONE
    }

    // .---------------------------------------------------------------------.
    // |                         updateRatingBar                             |
    // '---------------------------------------------------------------------'
    private fun updateRatingBar(newValueDegree: Int) {
        val sumDegree = requireArguments().getInt(ARG_SUMDEGREES) + newValueDegree
        val numberOfReview: Int = requireArguments().getInt(ARG_NUMBEROFREVIEW) + 1

        val average: Float = sumDegree.toFloat() / numberOfReview
        binding.scorTextView.text = if (average == 0f) "" else String.format("%.1f", average)
        binding.ratingBar.rating = average
        binding.nbReviewTextView.text = getString(R.string.number_of_review, numberOfReview.toString())
    }

    // .---------------------------------------------------------------------.
    // |                         Companion d'objet                           |
    // '---------------------------------------------------------------------'
    companion object {
        private const val ARG_BARID             = "ARG_BARID"
        private const val ARG_BARNAME           = "ARG_BARNAME"
        private const val ARG_WEBADDRESS        = "ARG_WEBADDRESS"
        private const val ARG_PHONUMBER         = "ARG_PHONUMBER"
        private const val ARG_HASHTAGS          = "ARG_HASHTAGS"
        private const val ARG_DESCRIPTION       = "ARG_DESCRIPTION"
        private const val ARG_ADDRESS           = "ARG_ADDRESS"
        private const val ARG_NUMBEROFREVIEW    = "ARG_NUMBEROFREVIEW"
        private const val ARG_AVERAGE           = "ARG_AVERAGE"
        private const val ARG_ISALREADYEVALUATED = "ARG_ISALREADYEVALUATED"
        private const val ARG_SUMDEGREES        = "ARG_SUMDEGREES"
        private const val ARG_ISFAVORITEBAR     = "ARG_ISFAVORITEBAR"

        fun newArguments(bar_id: String, barname: String, webaddress: String, phonenumber: String,
                         hashtags: String, description: String, address: String, numberOfReview: Int,
                         average: Float, isAlreadyEvaluated: Boolean, sumDegrees: Int, isFavoriteBar: Boolean): Bundle {
            val args = Bundle()
            args.putString(ARG_BARID, bar_id)
            args.putString(ARG_BARNAME, barname)
            args.putString(ARG_WEBADDRESS, webaddress)
            args.putString(ARG_PHONUMBER, phonenumber)
            args.putString(ARG_HASHTAGS, hashtags)
            args.putString(ARG_DESCRIPTION, description)
            args.putString(ARG_ADDRESS, address)
            args.putInt(ARG_NUMBEROFREVIEW, numberOfReview)
            args.putFloat(ARG_AVERAGE, average)
            args.putBoolean(ARG_ISALREADYEVALUATED, isAlreadyEvaluated)
            args.putInt(ARG_SUMDEGREES, sumDegrees)
            args.putBoolean(ARG_ISFAVORITEBAR, isFavoriteBar)

            return args
        }
    }

    // Fill view with companion object
    private fun fillBarView() {
        binding.scorTextView.text           = if (requireArguments().getFloat(ARG_AVERAGE) == 0f) "" else String.format("%.1f", requireArguments().getFloat(ARG_AVERAGE))
        binding.ratingBar.rating            = requireArguments().getFloat(ARG_AVERAGE)
        binding.nbReviewTextView.text       = getString(R.string.number_of_review, requireArguments().getInt(ARG_NUMBEROFREVIEW).toString())

        binding.barnameTextView.text        = requireArguments().getString(ARG_BARNAME)
        binding.hashtagView.text            = requireArguments().getString(ARG_HASHTAGS)
        binding.phonenumberTextView.text    = requireArguments().getString(ARG_PHONUMBER)
        binding.webAdresseTextView.text     = requireArguments().getString(ARG_WEBADDRESS)
        binding.adresseTextView.text        = requireArguments().getString(ARG_ADDRESS)
        binding.descriptionTextView.text    = requireArguments().getString(ARG_DESCRIPTION)
    }

    // .---------------------------------------------------------------------.
    // |                               Error                                 |
    // '---------------------------------------------------------------------'
    private fun displayErrorScreen(error: Error){
        binding.progressBar.visibility = View.GONE
        when(error){
            Error.NO_ERROR -> {
                assignVisibility(bar = true, errorLayout = false, connectivityError = false,
                    connectivityErrorImage = false, requestError = false, technicalError = false, errorImage = false)
            }
            Error.REQUEST_ERROR -> {
                assignVisibility(bar = false, errorLayout = true, connectivityError = false,
                    connectivityErrorImage = false, requestError = true, technicalError = false, errorImage = true)
            }
            Error.TECHNICAL_ERROR -> {
                assignVisibility(bar = false, errorLayout = true, connectivityError = false,
                    connectivityErrorImage = false, requestError = false, technicalError = true, errorImage = true)
            }
            else -> {
                assignVisibility(bar = false, errorLayout = true, connectivityError = true,
                    connectivityErrorImage = true, requestError = false, technicalError = false, errorImage = false)
            }
        }
    }

    private fun assignVisibility(
            bar: Boolean, errorLayout: Boolean, connectivityError: Boolean,
            connectivityErrorImage: Boolean, requestError: Boolean,
            technicalError: Boolean, errorImage: Boolean
    ) {
        binding.bar.visibility                      = if (bar) View.VISIBLE else View.GONE
        binding.errorLayout.visibility              = if (errorLayout) View.VISIBLE else View.GONE
        binding.connectivityError.visibility        = if (connectivityError) View.VISIBLE else View.GONE
        binding.connectivityErrorImage.visibility   = if (connectivityErrorImage) View.VISIBLE else View.GONE
        binding.requestError.visibility             = if (requestError) View.VISIBLE else View.GONE
        binding.technicalError.visibility           = if (technicalError) View.VISIBLE else View.GONE
        binding.errorImage.visibility               = if (errorImage) View.VISIBLE else View.GONE
        binding.layout.gravity                      = if (errorLayout) Gravity.CENTER_VERTICAL else Gravity.NO_GRAVITY
    }
}