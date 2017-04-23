package com.home.aleksandrnazarenko.yandextranslator;

/**
 * Created by aleksandrnazarenko on 18.04.17.
 */

public class FragmentDataSync {
    public final String message;
    public final int position;

    public final String source;
    public final String translate;
    public final String direction;
    public final int favorite;




    public FragmentDataSync(String message,int position,String source,String translate,String direction,int favorite) {
        this.position = position;
        this.message = message;
        this.source = source;
        this.translate = translate;
        this.direction = direction;
        this.favorite = favorite;
    }

}
