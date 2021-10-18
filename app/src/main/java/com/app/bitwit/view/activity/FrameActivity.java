package com.app.bitwit.view.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityFrameBinding;
import com.app.bitwit.view.fragment.HotPostFragment;
import com.app.bitwit.view.fragment.MainFragment;
import com.app.bitwit.view.fragment.PostFragment;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import lombok.var;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class FrameActivity extends AppCompatActivity {
    
    private final FragmentManager fragmentManager = getSupportFragmentManager( );
    
    private final MainFragment    mainFragment    = new MainFragment( );
    private final PostFragment    postFragment    = new PostFragment( );
    private final HotPostFragment hotPostFragment = new HotPostFragment( );
    
    private ActivityFrameBinding binding;
    
    private long backButtonKeyDownTime;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_frame);
        binding.setActivity(this);
        binding.executePendingBindings( );
        
        var transaction = fragmentManager.beginTransaction( );
        transaction.replace(R.id.frameLayout, mainFragment).commitNowAllowingStateLoss( );
        
        binding.navigationView.setOnNavigationItemSelectedListener(item -> {
                    switch (item.getItemId( )) {
                        case R.id.home:
                            changeFragment(mainFragment);
                            ((CoordinatorLayout.LayoutParams)binding.navigationView.getLayoutParams( ))
                                    .setBehavior(new HideBottomViewOnScrollBehavior<>( ));
                            break;
                        case R.id.post:
                            changeFragment(postFragment);
                            ((CoordinatorLayout.LayoutParams)binding.navigationView.getLayoutParams( ))
                                    .setBehavior(null);
                            break;
                        case R.id.account:
                            changeFragment(hotPostFragment);
                            ((CoordinatorLayout.LayoutParams)binding.navigationView.getLayoutParams( ))
                                    .setBehavior(null);
                            break;
                        default:
                            break;
                    }
                    return true;
                }
        );
        binding.navigationView.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId( )) {
                case R.id.home:
                    mainFragment.initRecyclerViewPosition( );
                    break;
                case R.id.post:
                    postFragment.initRecyclerViewPosition( );
                    break;
                case R.id.account:
                    break;
                default:
                    break;
            }
        });
        findViewById(R.id.home).setOnLongClickListener(v -> true);
        findViewById(R.id.post).setOnLongClickListener(v -> true);
        findViewById(R.id.account).setOnLongClickListener(v -> true);
    }
    
    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager( ).beginTransaction( ).replace(R.id.frameLayout, fragment).commit( );
    }
    
    public void makeSnackbar(Snackbar snackbar) {
        snackbar.setAnchorView(binding.navigationView).show( );
    }
    
    public void makeSnackbar(String message) {
        Snackbar.make(binding.getRoot( ), message, LENGTH_SHORT)
                .setAnchorView(binding.navigationView)
                .show( );
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis( ) - backButtonKeyDownTime > 2000) {
                makeSnackbar("뒤로가기 버튼을 누르면 앱이 종료됩니다");
                backButtonKeyDownTime = System.currentTimeMillis( );
            }
            else {
                finish( );
            }
        }
        else {
            result = super.onKeyDown(keyCode, event);
        }
        return result;
    }
}
