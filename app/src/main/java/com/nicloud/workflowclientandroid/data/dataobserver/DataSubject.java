package com.nicloud.workflowclientandroid.data.dataobserver;

/**
 * @author Danny Lin
 * @since 2015/10/6.
 */
public interface DataSubject {
    void registerDataObserver(DataObserver o);
    void removeDataObserver(DataObserver o);
    void notifyDataObservers();
}
