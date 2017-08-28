package com.boss.bluetoothserver;

/**
 * Created by cyrusmith
 * All rights reserved
 * http://interosite.ru
 * info@interosite.ru
 */
interface Communicator {
    void startCommunication();
    void write(String message);
    void stopCommunication();
}
