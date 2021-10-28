package com.example.moviesapp

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListActivity : AppCompatActivity() {
    companion object {
        const val LIKE = "Like"
        const val COMMENT = "Comment"
        const val SELECTED_MOVIE = "Selected_Movie"
        const val RESULT_CODE = 111
    }

    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.MovieListView) }
    private val favoritesRecyclerView by lazy { findViewById<RecyclerView>(R.id.FavoritesMovieListView) }
    private val navigationView by lazy { findViewById<BottomNavigationView>(R.id.bottomNavigationView) }

    private lateinit var adapter: MovieAdapter
    private lateinit var listView : ListView

    var selectedMovie : MyMovie? = null
        get() = field
        set(value) {
            val oldValue = field
            field = value
            if (value != null)
            {
                setSelection(oldValue, false)
                setSelection(value, true)
            }
        }

    private fun setSelection(value: MyMovie?, selected : Boolean) {
        if (value == null){
            return
        }
        val position = DataAccess.Movies.indexOf(value)
        val tempHolder = recyclerView.findViewHolderForAdapterPosition(position)
        tempHolder?.let {
            val holder = tempHolder as MovieItemViewHolder
            holder?.let {
                if (selected) {
                    it.setSelected()
                }
                else {
                    it.setNotSelected()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        savedInstanceState?.let { loadSelectedMovie(it) }

        initList(recyclerView)
        initList(favoritesRecyclerView)

        adapter = MovieAdapter(DataAccess.Movies, this::OnSelectMovie)
        recyclerView.adapter = adapter

        initNavigation()
    }

    private fun initList(rView: RecyclerView) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rView.layoutManager = LinearLayoutManager(this)
        } else {
            rView.layoutManager = GridLayoutManager(this, 3)
        }

        val dividerLineHorizontal = getDrawable(R.drawable.dark_line_1dp)
        val dividerLineVertical = getDrawable(R.drawable.dark_line_1dp_vertical)
        if (dividerLineHorizontal != null
            && dividerLineVertical != null) {
            val dividerH = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            val dividerV = DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
            dividerH.setDrawable(dividerLineHorizontal)
            dividerV.setDrawable(dividerLineVertical)
            rView.addItemDecoration(dividerH)
            rView.addItemDecoration(dividerV)
        }
    }

    private fun initNavigation() {
        navigationView.setOnNavigationItemSelectedListener {
            if (it.title == getString(R.string.action_main)) {
                recyclerView.visibility = VISIBLE
                favoritesRecyclerView.visibility = GONE
            } else {
                val adapter = FavoriteMovieAdapter(DataAccess.Movies.filter {
                     it.isFavorite
                }.toMutableList(), this::itemChanged)
                favoritesRecyclerView.adapter = adapter

                recyclerView.visibility = GONE
                favoritesRecyclerView.visibility = VISIBLE
            }
            true
        }
    }

    fun itemChanged(item: MyMovie) {
        val index = DataAccess.Movies.indexOf(item)
        recyclerView.adapter?.notifyItemChanged(index)
    }

    private fun OnSelectMovie(value: MyMovie) {
        selectedMovie = value
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra(DetailsActivity.SELECTED_ID, value.Id)
        startActivityForResult(intent, RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CODE
            && resultCode == RESULT_OK) {
            data?.let {
                checkMovieLike(it)
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkMovieLike(data: Intent) {
        val isLike = data.getIntExtra(LIKE, -1)
        val comment = data.getStringExtra(COMMENT)
        val movie = selectedMovie

        if (isLike > -1
            && movie != null) {
            Log.println(Log.DEBUG, null, "Пользователю ${if (isLike == 1) "понравился" else "не понравился"} фильм ${movie.Title}${if (comment != null && comment != "") "и он написал: $comment" else ""}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        selectedMovie?.let {
            outState.putInt(SELECTED_MOVIE,  it.Id)
        }
    }

    private fun loadSelectedMovie(bundle: Bundle) {
        val id = bundle.getInt(SELECTED_MOVIE)

        selectedMovie = DataAccess.Movies.firstOrNull {
            it.Id == id
        }
    }

    override fun onBackPressed() {
        ExitDialog().show(supportFragmentManager, "Exit")
    }
}

internal class RoundCornersDecoration(private val radius: Float) : RecyclerView.ItemDecoration() {
    private val defaultRectToClip: RectF
    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val rectToClip = getRectToClip(parent)

        // has no items with ViewType == `R.layout.item_image`
        if (rectToClip == defaultRectToClip) {
            return
        }
        val path = Path()
        path.addRoundRect(rectToClip, radius, radius, Path.Direction.CW)
        canvas.clipPath(path)
    }

    private fun getRectToClip(parent: RecyclerView): RectF {
        val rectToClip = RectF(defaultRectToClip)
        val childRect = Rect()

        for (i in 0 until parent.childCount) {
            val child: View = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, childRect)
            rectToClip.left = childRect.left.toFloat()
            rectToClip.top = childRect.top.toFloat()
            rectToClip.right = childRect.right.toFloat()
            rectToClip.bottom = childRect.bottom.toFloat()
        }
        return rectToClip
    }

    init {
        defaultRectToClip = RectF(Float.MAX_VALUE, Float.MAX_VALUE, 0f, 0f)
    }
}