package com.example.moviesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoriteMovieAdapter(private var items : MutableList<MyMovie>, private val itemChanged: (MyMovie) -> Unit) : RecyclerView.Adapter<FavoriteMovieItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieItemViewHolder {
        var inflater = LayoutInflater.from(parent.context)
        return FavoriteMovieItemViewHolder(
            inflater.inflate(
                R.layout.listitem_favoritemovie,
                parent,
                false
            ), this::removeItem
        )
    }

    override fun onBindViewHolder(holder: FavoriteMovieItemViewHolder, position: Int) {
        holder.Bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(item: MyMovie){
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)

        itemChanged(item)
    }
}

class FavoriteMovieItemViewHolder(itemView: View, private val removeItem: (MyMovie) -> Unit) : RecyclerView.ViewHolder(itemView){
    private val _titleTextView : TextView = itemView.findViewById(R.id.MovieTitle)
    private val _posterImageView : ImageView = itemView.findViewById(R.id.MoviePoster)
    private val _deleteButton : Button = itemView.findViewById(R.id.DeleteButton)

    fun Bind(item: MyMovie){
        // Выставляем заголовок
        _titleTextView.text = item.Title

        // Грузим постер
        DownloadImageFromInternet(_posterImageView).execute(item.Poster)

        // Добавляем все необходимое кнопочке
        _deleteButton.setOnClickListener {
            item.isFavorite = false
            removeItem(item)
            SizeAnimationHelper.animateTap(_deleteButton)
        }
    }
}