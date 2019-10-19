package com.sofps.inspirationalquotes

/**
 * Copied from https://github.com/googlesamples/android-architecture/blob/todo-mvp/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/BaseView.java
 */
interface BaseView<T> {

    fun setPresenter(presenter: T)
}
