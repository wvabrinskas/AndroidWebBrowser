package com.test.wvabrinskas.testapplication;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by wvabrinskas on 6/20/16.
 */

class CustomObservable extends Observable {
    // To force notifications to be sent
    public void notifyObservers(Object data) {
        setChanged();
        super.notifyObservers(data);
    }
}

public class ThreadObserver {
    private static ThreadObserver ourInstance = new ThreadObserver();

    public static ThreadObserver getInstance() {
        return ourInstance;
    }

    public static HashMap<String, CustomObservable> observables;

    private ThreadObserver() {
        observables = new HashMap<String, CustomObservable>();
    }
    public static void addObserver(String notification, Observer observer) {
        CustomObservable observable = observables.get(notification);
        if (observable==null) {
            observable = new CustomObservable();
            observables.put(notification, observable);
        }
        observable.addObserver(observer);
    }

    public static void removeObserver(String notification, Observer observer) {
        CustomObservable observable = observables.get(notification);
        if (observable!=null) {
            observable.deleteObserver(observer);
        }
    }

    public static void postNotification(String notification, Object object) {
        CustomObservable observable = observables.get(notification);
        if (observable!=null) {
            observable.notifyObservers(object);
        }
    }
}
