package com.app.bitwit.viewmodel;

import androidx.lifecycle.MutableLiveData;
import com.app.bitwit.data.repository.PostRepository;
import com.app.bitwit.domain.Post;
import com.app.bitwit.util.StringUtils;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Getter
@HiltViewModel
public class SearchablePostListViewModel extends PostListViewModel {
    
    private final MutableLiveData<String> searchWord = new MutableLiveData<>( );
    
    @Inject
    protected SearchablePostListViewModel(PostRepository postRepository) {
        super(postRepository);
    }
    
    @Override
    public int getPageSize( ) {
        return 20;
    }
    
    @Override
    protected Single<List<Post>> loadPosts( ) {
        if (! StringUtils.hasText(searchWord.getValue( ))) {
            return Single.just(Collections.emptyList( ));
        }
        return postRepository.searchPostPageByKeyword(searchWord.getValue( ), postPage, getPageSize( ));
    }
    
    public void setSearchWord(String searchWord) {
        this.searchWord.postValue(searchWord);
    }
}
