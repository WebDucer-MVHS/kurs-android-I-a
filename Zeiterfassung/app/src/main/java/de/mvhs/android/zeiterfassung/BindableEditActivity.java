package de.mvhs.android.zeiterfassung;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import de.mvhs.android.zeiterfassung.databinding.ActivityEditGridBindingBinding;

public class BindableEditActivity extends AppCompatActivity {
    EditViewModel _viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding zwischen View und ViewModel
        ActivityEditGridBindingBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_edit_grid_binding);

        _viewModel = new EditViewModel();

        binding.setEditModel(_viewModel);
    }

    @Override
    protected void onResume() {
        super.onResume();

        _viewModel.setComment("Hallo Binding World!");
        _viewModel.setPause(30);
    }
}
