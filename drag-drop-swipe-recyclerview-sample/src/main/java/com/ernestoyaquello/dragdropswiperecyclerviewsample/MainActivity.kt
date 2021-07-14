package com.ernestoyaquello.dragdropswiperecyclerviewsample

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import android.view.MenuItem
import android.view.View
import com.ernestoyaquello.dragdropswiperecyclerviewsample.config.local.ListFragmentType
import com.ernestoyaquello.dragdropswiperecyclerviewsample.config.local.currentListFragmentType
import com.ernestoyaquello.dragdropswiperecyclerviewsample.data.source.IceCreamRepository
import com.ernestoyaquello.dragdropswiperecyclerviewsample.databinding.ActivityMainBinding
import com.ernestoyaquello.dragdropswiperecyclerviewsample.feature.managelists.view.GridListFragment
import com.ernestoyaquello.dragdropswiperecyclerviewsample.feature.managelists.view.HorizontalListFragment
import com.ernestoyaquello.dragdropswiperecyclerviewsample.feature.managelists.view.VerticalListFragment
import com.ernestoyaquello.dragdropswiperecyclerviewsample.feature.managelists.view.base.BaseListFragment
import com.ernestoyaquello.dragdropswiperecyclerviewsample.feature.managelog.view.LogFragment
import com.ernestoyaquello.dragdropswiperecyclerviewsample.util.Logger
import com.google.android.material.navigation.NavigationBarView

/**
 * Main Activity of the app. Handles the navigation to the list sample screens and to the log screen.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val onBottomItemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
        tryNavigateToListFragment(item.itemId)
    }

    private val onLogButtonClickedListener = View.OnClickListener {
        navigateToLogFragment()
    }

    private val onLogUpdatedListener = object: Logger.OnLogUpdateListener {
        override fun onLogUpdated() = refreshLogButtonText()
    }

    private val onFabClickedListener = View.OnClickListener {
        // When in the log fragment, the FAB clears the log; when in a list fragment, it adds an item
        if (isLogFragmentOpen())
            Logger.reset()
        else
            IceCreamRepository.getInstance().generateNewItem()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.elevation = 0f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.navigationBarColor = Color.BLACK

        setupLog()
        setupBottomNavigation()
        setupFab()
        refreshLogButtonText()
        navigateToListFragment()
    }

    private fun setupLog() {
        // Initialise log and subscribe to log changes
        Logger.init(onLogUpdatedListener)

        // If the user clicks on the log button, we open the log fragment
        binding.seeLogButton.setOnClickListener(onLogButtonClickedListener)
    }

    private fun setupBottomNavigation() {
        binding.navigation.setOnItemSelectedListener(onBottomItemSelectedListener)
    }

    private fun setupFab() {
        binding.fab.setOnClickListener(onFabClickedListener)
    }

    private fun refreshLogButtonText() {
        val numItemsOnLog = Logger.instance?.messages?.size ?: 0
        binding.seeLogButtonText.text = getString(R.string.seeLogMessagesTitle, numItemsOnLog)
    }

    private fun tryNavigateToListFragment(itemId: Int): Boolean {
        val listFragmentType: ListFragmentType? = when (itemId) {
            R.id.navigation_vertical_list -> ListFragmentType.VERTICAL
            R.id.navigation_horizontal_list -> ListFragmentType.HORIZONTAL
            R.id.navigation_grid_list -> ListFragmentType.GRID
            else -> null
        }

        if (listFragmentType != null && (listFragmentType != currentListFragmentType || isLogFragmentOpen())) {
            navigateToListFragment(listFragmentType)

            return true
        }

        return false
    }

    private fun navigateToListFragment(listFragmentType: ListFragmentType = currentListFragmentType) {
        currentListFragmentType = listFragmentType

        val fragment: BaseListFragment = when (listFragmentType) {
            ListFragmentType.VERTICAL -> VerticalListFragment.newInstance()
            ListFragmentType.HORIZONTAL -> HorizontalListFragment.newInstance()
            ListFragmentType.GRID -> GridListFragment.newInstance()
        }
        replaceFragment(fragment, listFragmentType.tag)
        onNavigatedToListFragment()
    }

    private fun navigateToLogFragment() {
        replaceFragment(LogFragment.newInstance(), LogFragment.TAG)
        onNavigatedToLogFragment()
    }

    private fun onNavigatedToListFragment() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
        binding.seeLogButton.visibility = View.VISIBLE
        binding.fab.setImageDrawable(AppCompatResources.getDrawable(applicationContext, R.drawable.ic_new_item))
    }

    private fun onNavigatedToLogFragment() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        binding.seeLogButton.visibility = View.GONE
        binding.fab.setImageDrawable(AppCompatResources.getDrawable(applicationContext, R.drawable.ic_clear_items))
    }

    private fun isLogFragmentOpen() = supportFragmentManager.findFragmentByTag(LogFragment.TAG) != null

    private fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.content_frame, fragment, tag)
        }.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (isLogFragmentOpen()) {
                    navigateToListFragment()

                    return true
                }

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (isLogFragmentOpen())
            navigateToListFragment()
        else
            super.onBackPressed()
    }
}
