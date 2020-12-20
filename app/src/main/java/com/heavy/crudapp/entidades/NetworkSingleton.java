package com.heavy.crudapp.entidades;

import android.content.Context;

import com.heavy.crudapp.R;

public class NetworkSingleton {

    private static NetworkSingleton obInstanceNetwork;
    private static Context ctx;

    private static String ip;
    private static String protocol;
    private static String port;

    private NetworkSingleton (Context context){
        ctx = context;
        ip = ctx.getString(R.string.ip);
        protocol = ctx.getString(R.string.protocol);
        port = ctx.getString(R.string.port);
    }

    public static synchronized NetworkSingleton getObInstanceNetwork(Context ctx) {
        if(obInstanceNetwork == null){
            obInstanceNetwork = new NetworkSingleton(ctx);
        }
        return obInstanceNetwork;
    }

    public static String getIp() {
        return ip;
    }

    public static String getProtocol() {
        return protocol;
    }

    public static String getPort() {
        return port;
    }
}
