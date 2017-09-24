package com.sofps.inspirationalquotes;

/**
 * Copied from https://github.com/googlesamples/android-architecture/blob/todo-mvp/todoapp/app/src/main/java/com/example/android/architecture/blueprints/todoapp/BaseView.java
 */
public interface BaseView<T> {

    void setPresenter(T presenter);
}
