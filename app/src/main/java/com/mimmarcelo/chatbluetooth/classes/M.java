package com.mimmarcelo.chatbluetooth.classes;

public abstract class M {
    public static final class requestCode{
        public static final int permissions = 1;
        public static final int bluetooth = 2;
        public static final int openService = 3;
        public static final int search = 4;
    }
    public static final class extra {
        public static final String device = "device";
        public static final String bluetoothManager = "bluetoothManager";
        public static final String isServer = "isBoolean";
    }
}
