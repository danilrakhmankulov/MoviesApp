package com.example.moviesapp.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviesapp.*

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: MovieAdapter

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

    var selectMovie : ((MyMovie) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.MovieListView)
        initList(recyclerView)

        val id = selectedMovie?.Id ?: -1;
        adapter = MovieAdapter(DataAccess.Movies, this::OnSelectMovie, id)
        recyclerView.adapter = adapter
    }

    private fun setSelection(value: MyMovie?, selected : Boolean) {
        if (value == null){
            return
        }
        if (!this::recyclerView.isInitialized){
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

    private fun OnSelectMovie(value: MyMovie) {
        selectedMovie = value

        selectMovie?.let {
            it(selectedMovie!!)
        }
//        val intent = Intent(this, DetailsActivity::class.java)
//        intent.putExtra(DetailsActivity.SELECTED_ID, value.Id)
//        startActivityForResult(intent, ListActivity.RESULT_CODE)
    }
}