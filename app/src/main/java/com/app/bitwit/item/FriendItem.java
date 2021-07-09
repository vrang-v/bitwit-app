package com.app.bitwit.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendItem
{
    private Integer resourceId;
    private String  name;
    private String  message;
}