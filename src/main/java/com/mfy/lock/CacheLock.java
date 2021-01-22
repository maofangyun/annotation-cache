package com.mfy.lock;

import java.util.concurrent.TimeUnit;

public interface CacheLock {

    String getName();

    void lock(long leaseTime, TimeUnit unit,String key);

    void unlock();
}
