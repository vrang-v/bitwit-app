package com.app.bitwit.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Page<T> {
    
    @SerializedName("content")
    private List<T> content;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("totalElements")
    private int totalElements;
    
    @SerializedName("last")
    private boolean last;
    
    @SerializedName("size")
    private int size;
    
    @SerializedName("number")
    private int number;
}
