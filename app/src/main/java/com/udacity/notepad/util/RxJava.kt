package com.udacity.notepad.util

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

inline fun <T> Single<T>.succes(
    scheduler: Scheduler = Schedulers.io(),
    androidScheduler: Scheduler = AndroidSchedulers.mainThread(),
    crossinline function: (T) -> Unit
): Single<T> {
    return subscribeOn(scheduler)
        .observeOn(androidScheduler)
        .doOnSuccess { t -> function(t) }
}

inline fun <T> Single<T>.error(
    crossinline function: (Throwable) -> Unit
): Disposable? {
    return this.doOnError {
        function(it)
    }.subscribe()
}