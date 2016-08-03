package com.balloonmail.app.balloonmailapp.async;

/**
 * Created by Reem Hamdy on 7/20/2016.
 */
public interface PostHandler<T> {
    void handle(T data);
}
