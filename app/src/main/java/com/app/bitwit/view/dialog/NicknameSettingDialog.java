package com.app.bitwit.view.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.app.bitwit.R;
import com.app.bitwit.databinding.DialogSetNicknameBinding;
import com.app.bitwit.domain.Account;
import com.app.bitwit.viewmodel.NicknameSettingDialogViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.var;

import static com.app.bitwit.util.LiveDataUtils.observe;

@AndroidEntryPoint
public class NicknameSettingDialog extends View {
    
    private AlertDialog base;
    
    private Consumer<Account>   onSuccess  = account -> { };
    private Consumer<Throwable> onError    = e -> { };
    private Runnable            onNotValid = ( ) -> { };
    
    public NicknameSettingDialog(Context context) {
        super(context);
    }
    
    public static NicknameSettingDialog builder(AppCompatActivity activity) {
        var dialog = new NicknameSettingDialog(activity);
        dialog.base = new Builder(activity).create( );
        
        DialogSetNicknameBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_set_nickname, null, false);
        
        var viewModel = new ViewModelProvider(activity).get(NicknameSettingDialogViewModel.class);
        binding.setLifecycleOwner(activity);
        binding.setViewModel(viewModel);
        binding.executePendingBindings( );
    
        observe(activity, viewModel.getNickname( ), s -> {
            binding.confirmBtn.setEnabled(viewModel.getValid( ).getValue( ));
            if (viewModel.isValidFormat(s)) {
                viewModel.checkForDuplicateNickname(s);
            }
        });
        
        binding.confirmBtn.setOnClickListener(v -> {
            dialog.base.dismiss( );
            viewModel.changeNickname(dialog.onSuccess, dialog.onError, dialog.onNotValid);
        });
        dialog.base.setView(binding.getRoot( ));
        
        dialog.base.getWindow( ).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.base.getWindow( ).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
    
    public AlertDialog build( ) {
        return base;
    }
    
    public NicknameSettingDialog doOnSuccess(Consumer<Account> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }
    
    public NicknameSettingDialog doOnError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }
    
    public NicknameSettingDialog doOnNotValid(Runnable onNotValid) {
        this.onNotValid = onNotValid;
        return this;
    }
}
