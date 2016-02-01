package jhernandez.gameclock.alarm.creation;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import jhernandez.gameclock.R;

/**
 * Created by Jason on 1/24/2016.
 */
public class NumberRangePicker extends LinearLayout implements View.OnClickListener, TextWatcher, View.OnFocusChangeListener, View.OnTouchListener {

    LayoutInflater mInflater;
    int max, min, current, baseLength;
    boolean hasBaseLength;
    EditText numberDisplay;
    View rootView;
    ImageButton upButton, downButton;
    private boolean incrementing;

    public NumberRangePicker(Context context) {
        super(context);
        init(context);

    }
    public NumberRangePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NumberRangePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = mInflater.inflate(R.layout.number_range_picker, this, true);
        if (mInflater == null) {
            return;
        }
        numberDisplay = (EditText) rootView.findViewById(R.id.current_time);
        upButton = (ImageButton) rootView.findViewById(R.id.time_up);
        downButton = (ImageButton) rootView.findViewById(R.id.time_down);
        setMode(1, 12, 12);
//        upButton.setOnClickListener(this);
        upButton.setOnTouchListener(this);
//        downButton.setOnClickListener(this);
        downButton.setOnTouchListener(this);
        numberDisplay.addTextChangedListener(this);
    }

    public void setMode(int min, int max, int current) {
        setMode(min, max, current, -1);
    }
    public void setMode(int min, int max, int current, int baseLength) {
        this.max = max;
        this.min = min;
        this.current = current;
        numberDisplay.setText(String.format("%02d", current));
        if (baseLength < 0) {
            hasBaseLength = false;
            this.baseLength = 0;
        } else {
            this.baseLength = baseLength;
            hasBaseLength = true;
            numberDisplay.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.time_up) {
            current = current >= max ? min : current + 1;
        } else {
            current = current <= min ? max : current - 1;
        }
        numberDisplay.setText(String.format("%02d", current));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isInteger(s)) {
            int newTime = Integer.valueOf(s.toString());
            if (s.length() > 2 || newTime > max || newTime < min) {
                applyNewText(s.subSequence(0, s.length() - 1));
            }
            else {
                current = newTime;
            }
        }
        else if (s.length() != 0) {
            applyNewText(s.subSequence(0, s.length() - 1));
        }
    }

    private boolean isInteger(CharSequence s) {
        if (s == null) {
            return false;
        }
        int length = s.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (s.charAt(0) == '-') {
            if (length == 1)
                return false;
            i = 1;
        }
        for (; i < length; i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    private void applyNewText(CharSequence s) {
        numberDisplay.setText(s);
        numberDisplay.setSelection(numberDisplay.getText().length());

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus && hasBaseLength) {
            numberDisplay.setText(String.format("%"+baseLength+"s", numberDisplay.getText().toString()).replace(' ', '0'));
        }
    }

    public int getTime() {
        return current;
    }

    public void setCurrentTime(int time) {
        this.current = time;
        numberDisplay.setText(String.valueOf(time));
        if (hasBaseLength)
            numberDisplay.setText(String.format("%"+baseLength+"s", numberDisplay.getText().toString()).replace(' ', '0'));

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int a = event.getAction();

        if (v.getId() == R.id.time_up) {
            if (a == MotionEvent.ACTION_DOWN) {
                //startIncrementing(1);
            }
            else if (a == MotionEvent.ACTION_UP) {
                //stopIncrementing();
            }
        }
        else if (v.getId() == R.id.time_down) {
            if (a == MotionEvent.ACTION_DOWN) {
                //startIncrementing(-1);
            }
            else if (a == MotionEvent.ACTION_UP) {
                //stopIncrementing();
            }
        }
        return true;
    }

}
