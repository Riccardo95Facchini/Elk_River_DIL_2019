package facchini.riccardo.Elk_River_DIL_2019;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class Fragment_TimePicker extends DialogFragment
{
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), 0, 0, true);
    }
}
