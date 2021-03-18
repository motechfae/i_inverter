package tw.com.motech.i_inverter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tw.com.motech.i_inverter.R
import kotlinx.android.synthetic.main.fragment_c.*
import kotlinx.android.synthetic.main.fragment_c.view.*

class FragmentC : Fragment() {
    private lateinit var v:View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_c, container, false)

        return v
    }
}