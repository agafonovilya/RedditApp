package ru.agafonovilya.redditapp.utils.imageloader

interface IImageLoader<T> {
    fun loadInto(url: String, container: T, onErrorCallback: () -> Unit)
}