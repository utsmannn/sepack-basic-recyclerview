package com.utsman.sepack

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.utsman.sepack.data.state.NetworkState
import com.utsman.sepack.domain.Result
import com.utsman.sepack.ext.logi
import com.utsman.sepack.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    // call viewmodel instance by Koin
    private val viewModel: MainViewModel by viewModel()

    private val userAdapter = UserAdapter()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_users.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userAdapter
        }

        // fetch users
        viewModel.users()

        // observing user as result case
        viewModel.users.observe(this) {
            when (it) {
                is Result.Idle -> {
                    // idle
                    logi("idle..")
                    userAdapter.updateNetworkState(NetworkState.LOADED)
                }
                is Result.Loading -> {
                    // loading
                    logi("loading..")
                    userAdapter.updateNetworkState(NetworkState.LOADING)
                }
                is Result.Error -> {
                    // error
                    logi("error..")
                    val throwable = it.th
                    throwable.printStackTrace()
                    userAdapter.updateNetworkState(NetworkState.failed(throwable.message))
                }
                is Result.Success -> {
                    // success
                    logi("success..")
                    val data = it.data
                    userAdapter.updateNetworkState(NetworkState.LOADED)
                    userAdapter.updateList(data)
                }
            }
        }
    }
}