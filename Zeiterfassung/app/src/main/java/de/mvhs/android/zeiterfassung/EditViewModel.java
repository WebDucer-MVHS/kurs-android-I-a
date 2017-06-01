package de.mvhs.android.zeiterfassung;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.widget.TextView;

import de.mvhs.android.zeiterfassung.BR;

public class EditViewModel extends BaseObservable {
    private String _comment;
    private int _pause = 0;

    @Bindable
    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        // Prüfung auf Gleichheit
        if(_comment == null && comment == null){
            return;
        }

        if (_comment != null && _comment.equals(comment)){
            return;
        }

        _comment = comment;
        notifyPropertyChanged(BR.comment);
    }

    @Bindable
    public int getPause() {
        return _pause;
    }

    public void setPause(int pause) {
        if (pause == _pause){
            return;
        }

        _pause = pause;
        notifyPropertyChanged(BR.pause);
    }

    @BindingAdapter("binding:number")
    public static void setText(TextView view, int number){
        String output = number == 0 ? "" : Integer.toString(number);
        if (output.equals(view.getText().toString())){
            return;
        }
        view.setText(output);
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static int getText(TextView view){
        if(view.getText() == null || view.getText().length() == 0){
            return 0;
        }

        String number = view.getText().toString();
        int returnValue = 0;
        try{
            returnValue = Integer.parseInt(number);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }

        return returnValue;
    }



















}
