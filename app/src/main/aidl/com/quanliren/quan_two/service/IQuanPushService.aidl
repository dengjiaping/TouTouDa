// IQuanPushService.aidl
package com.quanliren.quan_two.service;

// Declare any non-default types here with import statements

interface IQuanPushService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

              void sendMessage(String str);
                boolean getServerSocket();
                void closeAll();
}
