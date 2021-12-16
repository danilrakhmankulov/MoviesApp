package com.example.moviesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(private val items : Array<MyMovie>, private val selectMovie: (movie : MyMovie) -> Unit, private var selectedId: Int = -1) : RecyclerView.Adapter<MovieItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        return MovieItemViewHolder(inflater.inflate(R.layout.listitem_movie, parent, false), selectMovie)
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        holder.Bind(items[position], selectedId)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class MovieItemViewHolder(itemView: View, private val selectMovie: (movie : MyMovie) -> Unit) : RecyclerView.ViewHolder(itemView){
    private val titleTextView : TextView = itemView.findViewById(R.id.MovieTitle)
    private val posterImageView : ImageView = itemView.findViewById(R.id.MoviePoster)
    private val detailsButton : Button = itemView.findViewById(R.id.DetailButton)
    private val likeTogglButton : ToggleButton = itemView.findViewById(R.id.LikeToggleButton)
    private val layoutRoot : RelativeLayout = itemView.findViewById(R.id.LayoutRoot)

    fun Bind(item: MyMovie, selectedId: Int){
        // Выставляем заголовок
        titleTextView.text = item.Title

        // Грузим постер
        Glide
            .with(posterImageView.context)
            .load(item.Poster)
            .into(posterImageView)

        // Добавляем все необходимое кнопочке
        detailsButton.setOnClickListener {
            selectMovie(item)
            SizeAnimationHelper.animateTap(detailsButton)
        }

        likeTogglButton.setOnCheckedChangeListener { buttonView, isChecked ->
            item.isFavorite = isChecked
            if (isChecked){
                Toast.makeText(itemView.context, "Вам понравился фильм", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(itemView.context, "Вам больше не нравится фильм :(", Toast.LENGTH_SHORT).show()
            }
        }
        likeTogglButton.setOnClickListener {
            SizeAnimationHelper.animateTap(likeTogglButton)
        }

        likeTogglButton.isChecked = item.isFavorite

        if (DataAccess.Movies[position].Id == selectedId){
            setSelected()
        }
        else {
            setNotSelected()
        }
    }

    fun setSelected(){
        layoutRoot.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.selectedListItemBackground))
    }

    fun setNotSelected(){
        layoutRoot.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.defaultListItemBackground))
    }
}



