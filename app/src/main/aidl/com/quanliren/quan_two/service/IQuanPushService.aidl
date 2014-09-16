// IQuanPushService.aidl
package com.quanliren.quan_two.service;

// Declare any non-default types here with import statements

interface IQuanPushService {

              void sendMessage(String str);
                boolean getServerSocket();
                void closeAll();
}
