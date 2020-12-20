package com.heavy.crudapp.entidades;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.heavy.crudapp.R;

public class VolleySingleton {

    private static VolleySingleton obInstanceVolley;
    private RequestQueue request;
    private static Context ctx;

    private VolleySingleton(Context context) {
        ctx = context;
        request = getRequestQueue();
    }

    public static synchronized VolleySingleton getObInstanceVolley(Context ctx) {
        if(obInstanceVolley == null){
            obInstanceVolley = new VolleySingleton(ctx);
        }
        return obInstanceVolley;
    }

    private RequestQueue getRequestQueue() {
        if(this.request ==  null){
            this.request = Volley.newRequestQueue(ctx.getApplicationContext());
        }

        return this.request;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

}
