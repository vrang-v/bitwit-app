package com.app.bitwit.view.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import com.app.bitwit.R;
import com.app.bitwit.databinding.ActivityFrameBinding;
import com.app.bitwit.view.fragment.AccountFragment;
import com.app.bitwit.view.fragment.HomeFragment;
import com.app.bitwit.view.fragment.PostFragment;
import com.app.bitwit.viewmodel.common.SnackbarViewModel;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

import static com.app.bitwit.util.livedata.LiveDataUtils.observeHasText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class FrameActivity extends AppCompatActivity {
    
    private final FragmentManager fragmentManager = getSupportFragmentManager( );
    
    private final HomeFragment    homeFragment    = new HomeFragment( );
    private final PostFragment    postFragment    = new PostFragment( );
    private final AccountFragment accountFragment = new AccountFragment( );
    
    private ActivityFrameBinding binding;
    
    private long lastBackKeydownTimeMillis;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_frame);
        binding.setActivity(this);
        binding.executePendingBindings( );
        setOnKeyboardStatusChangeEvent( );
        
        observeHasText(this, SnackbarViewModel.MESSAGE, this::makeSnackbar);
        
        changeFragment(homeFragment);
        
        binding.navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId( )) {
                case R.id.home:
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    changeFragment(homeFragment);
                    break;
                case R.id.post:
                    changeFragment(postFragment);
                    break;
                case R.id.account:
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    changeFragment(accountFragment);
                    break;
                default:
                    break;
            }
            return true;
        });
        
        binding.navigationView.setOnNavigationItemReselectedListener(item -> {
            switch (item.getItemId( )) {
                case R.id.home:
                    homeFragment.onNavigationItemReselected(item);
                    break;
                case R.id.post:
                    postFragment.onNavigationItemReselected(item);
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
    
    private void setNavigationViewBehavior(Behavior behavior) {
        ((CoordinatorLayout.LayoutParams)binding.navigationView.getLayoutParams( ))
                .setBehavior(behavior);
    }
    
    private void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction( )
                       .replace(R.id.frameLayout, fragment)
                       .commit( );
    }
    
    private void makeSnackbar(String message) {
        Snackbar.make(binding.getRoot( ), message, LENGTH_SHORT)
                .setAnchorView(binding.navigationView)
                .show( );
    }
    
    private void setOnKeyboardStatusChangeEvent( ) {
        binding.getRoot( ).getViewTreeObserver( ).addOnGlobalLayoutListener(( ) -> {
            int     rootHeight = binding.getRoot( ).getRootView( ).getHeight( );
            int     height     = binding.getRoot( ).getHeight( );
            boolean keyboardOn = (double)height / rootHeight < 0.8;
            if (! keyboardOn) {
                Transition transition = new Slide(Gravity.BOTTOM);
                transition.setDuration(300L);
                transition.addTarget(binding.navigationView);
                TransitionManager.beginDelayedTransition((ViewGroup)binding.getRoot( ), transition);
            }
            binding.navigationView.setVisibility(keyboardOn ? View.GONE : View.VISIBLE);
        });
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragmentManager.getBackStackEntryCount( ) > 0) {
                fragmentManager.popBackStack( );
                return true;
            }
            if (System.currentTimeMillis( ) - lastBackKeydownTimeMillis < 2000) {
                finish( );
            }
            lastBackKeydownTimeMillis = System.currentTimeMillis( );
            makeSnackbar("???????????? ????????? ????????? ?????? ???????????????.");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
