package com.app.bitwit.dto;

import com.app.bitwit.domain.Post;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostPreviewItem {
    
    Long id;
    
    String title;
    
    int viewCount;
    
    int starCount;
    
    boolean star;
    
    
    public PostPreviewItem(Post post) {
        this.id        = post.getId( );
        this.title     = post.getTitle( );
        this.viewCount = post.getViewCount( );
        this.starCount = post.getLikeCount( );
        this.star      = post.isLike( );
    }
}
