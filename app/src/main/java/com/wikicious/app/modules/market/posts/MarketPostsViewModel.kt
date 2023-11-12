package com.wikicious.app.modules.market.posts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wikicious.app.R
import com.wikicious.app.core.providers.Translator
import com.wikicious.app.core.subscribeIO
import com.wikicious.app.entities.ViewState
import io.horizontalsystems.core.helpers.DateHelper
import io.reactivex.disposables.CompositeDisposable

class MarketPostsViewModel(private val service: MarketPostService) : ViewModel() {

    val itemsLiveData = MutableLiveData<List<MarketPostsModule.PostViewItem>>()
    val viewStateLiveData = MutableLiveData<ViewState>(ViewState.Loading)
    val isRefreshingLiveData = MutableLiveData<Boolean>()

    private val disposables = CompositeDisposable()

    private fun getTimeAgo(timestamp: Long): String {
        val secondsAgo = DateHelper.getSecondsAgo(timestamp * 1000)
        val minutesAgo = secondsAgo / 60
        val hoursAgo = minutesAgo / 60

        return when {
            // interval from post in minutes
            minutesAgo < 60 -> Translator.getString(R.string.Market_MinutesAgo, minutesAgo)
            hoursAgo < 24 -> Translator.getString(R.string.Market_HoursAgo, hoursAgo)
            else -> {
                val daysAgo = hoursAgo / 24
                Translator.getString(R.string.Market_DaysAgo, daysAgo)
            }
        }
    }

    init {
        service.stateObservable
            .subscribeIO { state ->
                isRefreshingLiveData.postValue(false)

                state.dataOrNull?.let { posts ->
                    val postViewItems = posts.map {
                        MarketPostsModule.PostViewItem(
                            source = it.source.replaceFirstChar(Char::titlecase),
                            title = it.title,
                            body = it.body,
                            timeAgo = getTimeAgo(it.timestamp),
                            url = it.url
                        )
                    }
                    itemsLiveData.postValue(postViewItems)
                }

                state.viewState?.let {
                    viewStateLiveData.postValue(it)
                }
            }
            .let {
                disposables.add(it)
            }

        service.start()
    }

    override fun onCleared() {
        service.stop()
        disposables.clear()
    }

    fun onErrorClick() {
        service.refresh()
    }

    fun refresh() {
        isRefreshingLiveData.postValue(true)
        service.refresh()
    }
}
