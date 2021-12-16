package com.example.moviesapp.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesapp.DataAccess
import com.example.moviesapp.FavoriteMovieAdapter
import com.example.moviesapp.MyMovie
import com.example.moviesapp.R
import com.google.android.material.snackbar.Snackbar

class FavoriteListFragment : Fragment(R.layout.fragment_favorite_list) {

    private var favoritesRecyclerView : RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesRecyclerView = view.findViewById(R.id.FavoritesMovieListView)
        favoritesRecyclerView?.let {
            initList(it)
            updateItems()
        }
    }

    private fun initList(rView: RecyclerView) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rView.layoutManager = LinearLayoutManager(context)
        } else {
            rView.layoutManager = GridLayoutManager(context, 3)
        }

        val dividerLineHorizontal = context?.getDrawable(R.drawable.dark_line_1dp)
        val dividerLineVertical = context?.getDrawable(R.drawable.dark_line_1dp_vertical)
        if (dividerLineHorizontal != null
            && dividerLineVertical != null) {
            val dividerH = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            val dividerV = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            dividerH.setDrawable(dividerLineHorizontal)
            dividerV.setDrawable(dividerLineVertical)
            rView.addItemDecoration(dividerH)
            rView.addItemDecoration(dividerV)
        }
    }

    fun itemChanged(item: MyMovie) {
        val index = DataAccess.Movies.indexOf(item)
        favoritesRecyclerView?.adapter?.notifyItemChanged(index)
    }

    fun updateItems() {
        favoritesRecyclerView?.let {
            val adapter = FavoriteMovieAdapter(DataAccess.Movies.filter {
                it.isFavorite
            }.toMutableList(), this::itemChanged, this::showSnack)
            it.adapter = adapter
        }
    }

    fun showSnack(index: Int, item: MyMovie, onUndo: (Int, MyMovie) -> Unit){
        favoritesRecyclerView?.let {
            val snackbar = Snackbar.make(it, "Ой, я ошибся! Хочу вернуть фильм",
                Snackbar.LENGTH_LONG).setAction("Вернуть", View.OnClickListener {
                onUndo(index, item)
            })
            val snackbarView = snackbar.view
//        snackbarView.setBackgroundColor(Color.LTGRAY)
            val textView =
                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
//        textView.setTextColor(Color.BLUE)
//            textView.textSize = 28f
            snackbar.show()
        }
    }
}