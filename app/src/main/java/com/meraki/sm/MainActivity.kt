package com.meraki.sm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meraki.sm.databinding.MainActivityBinding
import com.meraki.sm.ui.main.MainFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private val binding by lazy { MainActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        Timber.e("Error Message")
        Timber.d("Debug Message")

        Timber.tag("Some Different tag").e("And error message")
    }
}
