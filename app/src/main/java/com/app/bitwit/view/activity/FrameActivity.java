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
import com.app.bitwit.util.SnackbarViewModel;
import com.app.bitwit.view.fragment.HotPostFragment;
import com.app.bitwit.view.fragment.MainFragment;
import com.app.bitwit.view.fragment.PostFragment;
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;

import static com.app.bitwit.util.LiveDataUtils.observeHasText;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class FrameActivity extends AppCompatActivity {
    
    private final FragmentManager fragmentManager = getSupportFragmentManager( );
    
    private final MainFragment    mainFragment    = new MainFragment( );
    private final PostFragment    postFragment    = new PostFragment( );
    private final HotPostFragment hotPostFragment = new HotPostFragment( );
    
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
        
        changeFragment(mainFragment);
        
        binding.navigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId( )) {
                case R.id.home:
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    changeFragment(mainFragment);
                    setNavigationViewBehavior(new HideBottomViewOnScrollBehavior<>( ));
                    break;
                case R.id.post:
                    changeFragment(postFragment);
                    setNavigationViewBehavior(null);
                    break;
                case R.id.account:
                    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    changeFragment(hotPostFragment);
                    setNavigationViewBehavior(null);
                    break;
                default:
                    break;
            }
            return true;
        });
        
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
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        }
        
        if (fragmentManager.getBackStackEntryCount( ) > 0) {
            fragmentManager.popBackStack( );
            return true;
        }
        
        if (System.currentTimeMillis( ) - lastBackKeydownTimeMillis > 2000) {
            makeSnackbar("뒤로가기 버튼을 누르면 앱이 종료됩니다");
            lastBackKeydownTimeMillis = System.currentTimeMillis( );
        }
        else {
            finish( );
        }
        return true;
    }
}
