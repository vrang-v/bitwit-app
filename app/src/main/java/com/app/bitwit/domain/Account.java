package com.app.bitwit.domain;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    
    @SerializedName("accountId")
    Long id;
    
    String name;
    
    String email;
    
    String password;
    
    String accountType;
    
    Boolean emailVerified;
    
    @SerializedName("profileImage")
    String profileImageUrl;
    
    public String getKoreanAccountType( ) {
        switch( accountType ) {
            case "EMAIL":
                return "이메일";
            case "GOOGLE":
                return "구글";
            default:
                return "";
        }
    }
}
