package com.example.moviesapp

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import com.example.moviesapp.fragments.DetailsFragment
import com.example.moviesapp.fragments.FavoriteListFragment
import com.example.moviesapp.fragments.ListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    companion object{
        const val LIKE = "Like"
        const val COMMENT = "Comment"
        const val SELECTED_MOVIE = "Selected_Movie"
        const val SELECTED_VIEW = "Selected_Navigation_View"
        const val RESULT_CODE = 111
        const val NAVIGATION = "Navigation"
    }

    private var navigation = Navigation.List
    private val listFragment = ListFragment()
    private val favoriteFragment = FavoriteListFragment()
    private val detailsFragment = DetailsFragment()
    private val mainFrame by lazy { findViewById<FrameLayout>(R.id.mainFrame) }
    private val navigationViewPortrait by lazy { findViewById<BottomNavigationView>(R.id.bottomNavigationView) }
    private val navigationViewLandscape by lazy { findViewById<NavigationView>(R.id.navigationView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            loadNavigation(it)
            loadSelectedMovie(it)
        }

        listFragment.selectMovie = this::onSelectMovie

        initBackAction()
        initNavigation()
        initResultListener()

        if (navigation == Navigation.Details
            && listFragment.selectedMovie != null
        ) {
            onSelectMovie(listFragment.selectedMovie!!)
        } else {
            setFragment(navigation)
        }
    }

    private fun initBackAction() {
        detailsFragment.setBackRequestedListener(object : DetailsFragment.BackRequestedListener {
            @Override
            override fun onBackRequested() {
                onBackPressed()
            }
        })
    }

    private fun initResultListener() {
        supportFragmentManager.setFragmentResultListener(
            DetailsFragment.DATA,
            this,
            FragmentResultListener { _, result ->
                val selectedMovieId = result.getInt(SELECTED_MOVIE)
                val comment = result.getString(COMMENT)
                val isLike = result.getInt(LIKE)

                val movie = DataAccess.Movies.firstOrNull {
                    it.Id == selectedMovieId
                }

                if (isLike > -1
                    && movie != null) {
                    Log.println(Log.DEBUG, null, "Пользователю ${if (isLike == 1) "понравился" else "не понравился"} фильм ${movie.Title}${if (comment != null && comment != "") "и он написал: $comment" else ""}")
                }

                listFragment.selectedMovie = movie
            })
    }

    private fun initNavigation(){
        val orientation = resources.configuration.orientation
        if (orientation == ORIENTATION_LANDSCAPE) {
            initNavigationLandscape()
        } else {
            initNavigationPortrait()
        }
    }

    private fun initNavigationPortrait() {
        navigationViewPortrait.setOnNavigationItemSelectedListener {
            if (it.title == getString(R.string.action_main)) {
                setFragment(Navigation.List)
            } else {
                setFragment(Navigation.Favorite)
                favoriteFragment.updateItems()
            }
            true
        }
    }

    private fun initNavigationLandscape() {
        navigationViewLandscape.setNavigationItemSelectedListener {
            if (it.title == getString(R.string.action_main)) {
                setFragment(Navigation.List)
            } else {
                setFragment(Navigation.Favorite)
                favoriteFragment.updateItems()
            }
            true
        }
    }

    private fun setFragment(nav : Navigation){
        navigation = nav

        when(navigation){
            Navigation.Details -> setFragment(detailsFragment)
            Navigation.Favorite -> setFragment(favoriteFragment)
            Navigation.List -> setFragment(listFragment)
        }
    }

    private fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, fragment)
            .commit()
    }

    private fun onSelectMovie(value: MyMovie) {
        detailsFragment.arguments = Bundle().apply {
            putInt(DetailsFragment.SELECTED_ID, value.Id)
        }

        val orientation = resources.configuration.orientation
        if (orientation == ORIENTATION_LANDSCAPE) {
            navigationViewLandscape.visibility = GONE
        } else {
            navigationViewPortrait.visibility = GONE
            setFrameLayoutMargin(0)
        }

        setFragment(Navigation.Details)
    }

    private fun loadNavigation(bundle: Bundle) {
        val value = bundle.getString(NAVIGATION)
        value?.let {
            navigation = Navigation.valueOf(it)

            val orientation = resources.configuration.orientation
            if (orientation == ORIENTATION_PORTRAIT){
                when(navigation){
                    Navigation.List -> navigationViewPortrait.selectedItemId = R.id.action_main
                    Navigation.Favorite -> navigationViewPortrait.selectedItemId = R.id.action_favorite
                }
            }
        }
    }

    private fun loadSelectedMovie(bundle: Bundle) {
        val id = bundle.getInt(SELECTED_MOVIE)

        listFragment.selectedMovie = DataAccess.Movies.firstOrNull {
            it.Id == id
        }
    }

    override fun onBackPressed() {
        if (navigation == Navigation.Details){
            detailsFragment.onBack()
            setFragment(Navigation.List)

            val orientation = resources.configuration.orientation
            if (orientation == ORIENTATION_LANDSCAPE) {
                navigationViewLandscape.visibility = VISIBLE
            } else {
                navigationViewPortrait.visibility = VISIBLE
                setFrameLayoutMargin(resources.getDimensionPixelSize(R.dimen.nav_height))
            }
        }
        else{
            ExitDialog().show(supportFragmentManager, "Exit")
        }
    }

    private fun setFrameLayoutMargin(bottom: Int) {
        val parameter = mainFrame.getLayoutParams() as ConstraintLayout.LayoutParams
        parameter.setMargins(
            parameter.leftMargin,
            parameter.topMargin,
            parameter.rightMargin,
            bottom
        )
        mainFrame.setLayoutParams(parameter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        listFragment.selectedMovie?.let {
            outState.putInt(SELECTED_MOVIE,  it.Id)
        }

        outState.putString(NAVIGATION,  navigation.toString())
    }
}