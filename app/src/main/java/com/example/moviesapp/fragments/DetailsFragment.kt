package com.example.moviesapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.moviesapp.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailsFragment : Fragment(R.layout.fragment_details) {
    companion object{
        const val DATA = "Data"
        const val SELECTED_ID = "SelectedId"
    }

    private lateinit var likeCheckBox : CheckBox
    private lateinit var commentTextView : EditText
    private lateinit var backButton : Button
    private var movieId : Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieId = arguments?.getInt(SELECTED_ID) ?: -1

        val movie = DataAccess.Movies.firstOrNull {
            it.Id == movieId
        }

        movie?.let {
            val posterImageView = view.findViewById<ImageView>(R.id.PosterImageView)
            val titleTextView = view.findViewById<TextView>(R.id.TitleTextView)

            // Проставляем заголовок
            titleTextView.text = movie.Title

            // Грузим постер
            Glide
                .with(posterImageView.context)
                .load(movie.Poster)
                .into(posterImageView)
        }

        val inviteButton = view.findViewById<FloatingActionButton>(R.id.InviteButton)
        inviteButton?.let {
            inviteButton.setOnClickListener {
                val intent= Intent()
                intent.action=Intent.ACTION_SEND
                if (movie != null)
                    intent.putExtra(Intent.EXTRA_TEXT, movie.Title)
                intent.type="text/plain"
                startActivity(Intent.createChooser(intent,  getString(R.string.InviteText)))
            }
        }

        likeCheckBox = view.findViewById<CheckBox>(R.id.LikeCheckBox)
        commentTextView = view.findViewById<EditText>(R.id.CommentEditText)
        backButton = view.findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            backListener?.onBackRequested()
        }
    }

    fun onBack(isFromButton: Boolean = false){
        parentFragmentManager.setFragmentResult(
            DATA,
            Bundle().apply {
                if (isFromButton){
                    putInt(MainActivity.SELECTED_MOVIE, movieId)
                }

                putInt(MainActivity.SELECTED_MOVIE, movieId)
                putString(MainActivity.COMMENT, commentTextView.text.toString())
                putInt(MainActivity.LIKE, if (likeCheckBox.isChecked) 1 else 0)
            })
    }

    interface BackRequestedListener {
        fun onBackRequested()
    }

    private var backListener: BackRequestedListener? = null

    fun setBackRequestedListener(listener: BackRequestedListener) {
        backListener = listener
    }
}