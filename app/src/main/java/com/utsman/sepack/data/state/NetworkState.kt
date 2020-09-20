package com.utsman.sepack.data.state

class NetworkState(val status: Status,val message: String?) {
    companion object {
        val LOADED: NetworkState = NetworkState(Status.SUCCESS, "Loaded")
        val LOADING: NetworkState = NetworkState(Status.RUNNING, "Loading")
        fun failed(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}