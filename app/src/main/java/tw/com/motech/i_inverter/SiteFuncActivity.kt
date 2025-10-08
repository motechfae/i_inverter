package tw.com.motech.i_inverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_site_func.*

class SiteFuncActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = layoutInflater.inflate(R.layout.activity_site_func, null)
        setContentView(root)

        val flFragment = root.findViewById<FrameLayout>(R.id.flFragment)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val actionBarSize = obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
                .getDimensionPixelSize(0, 0)

            v.setPadding(0, statusBarHeight + (actionBarSize / 2), 0, 0)

            val params = flFragment.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = navBarHeight + actionBarSize
            flFragment.layoutParams = params

            insets
        }

        setContentView(root)

        // 螢幕旋轉後都預設為0 = fragmentA
        savedInstanceState?.putInt("menu_index", 0)

        var bottom_nav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val fragmentA = FragmentA()
        val fragmentB = FragmentB()
        val fragmentC = FragmentC()
        val fragmentD = FragmentD()
        val fragmentE = FragmentE()
        setCurrentFragment(fragmentA)
        bottom_nav.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page_1 -> setCurrentFragment(fragmentA)
                R.id.page_2 -> setCurrentFragment(fragmentB)
                R.id.page_3 -> setCurrentFragment(fragmentC)
                /*
                R.id.page_4 -> setCurrentFragment(fragmentD)
                R.id.page_5 -> setCurrentFragment(fragmentE)

                 */
            }
            true
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Read the state of item position
        var menu_index = savedInstanceState.getInt("menu_index")
        bottom_nav.menu.getItem(menu_index).isChecked =  true
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}