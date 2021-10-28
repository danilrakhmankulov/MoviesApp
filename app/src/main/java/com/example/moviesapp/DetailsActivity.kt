package com.example.moviesapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import java.io.FileInputStream

class DetailsActivity : AppCompatActivity() {
    companion object{
        const val SELECTED_ID = "SelectedId"
    }

    private val result = Intent().apply {
        putExtra(ListActivity.LIKE,  0)
        putExtra(ListActivity.COMMENT, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val movieId = intent.getIntExtra(SELECTED_ID, -1)
        val movie = DataAccess.Movies.firstOrNull {
            it.Id == movieId
        }

        movie?.let {
            val posterImageView = findViewById<ImageView>(R.id.PosterImageView)
            val titleTextView = findViewById<TextView>(R.id.TitleTextView)

            // Проставляем заголовок
            titleTextView.text = movie.Title

            // Грузим постер
            val imageURL = movie.Poster
            DownloadImageFromInternet(posterImageView).execute(imageURL)
        }

        val inviteButton = findViewById<Button>(R.id.InviteButton)
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

        setResult(RESULT_OK, result)

        val likeCheckBox = findViewById<CheckBox>(R.id.LikeCheckBox)
        likeCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            result.putExtra(ListActivity.LIKE,  if (likeCheckBox.isChecked) 1 else 0)
        }

        val commentTextView = findViewById<EditText>(R.id.CommentEditText)
        commentTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                result.putExtra(ListActivity.COMMENT, commentTextView.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}