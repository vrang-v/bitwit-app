package com.app.bitwit.view.adapter.common;

import android.view.View;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdapterEvent<T, E extends AdapterEventType> {
    
    private T    item;
    private E    event;
    private View view;
    private int  position;
    
}
