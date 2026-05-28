package com.example.universityjava;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

public class CustomDropdown extends FrameLayout {

    private AutoCompleteTextView dropdown;
    private ImageView arrow;

    private String[] labels;
    private String[] values;

    private boolean isOpen = false;
    private Animator animUp;
    private Animator animDown;

    public CustomDropdown(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomDropdown(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomDropdown(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_custom_dropdown, this, true);

        dropdown = findViewById(R.id.dropdown);
        arrow = findViewById(R.id.arrow);
        animUp = AnimatorInflater.loadAnimator(getContext(), R.animator.arrow_flip_up);
        animDown = AnimatorInflater.loadAnimator(getContext(), R.animator.arrow_flip_down);
        animUp.setTarget(arrow);
        animDown.setTarget(arrow);

//        dropdown.setOnClickListener(v -> toggleDropdown());
//
//        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) closeDropdown();
//        });
//
//        dropdown.setOnItemClickListener((parent, view, position, id) -> {
//            closeDropdown();
//        });
        dropdown.setOnTouchListener((v, event) -> {
            dropdown.showDropDown();
            animateArrow(true);
            return false;
        });

        dropdown.setOnDismissListener(() -> {
            animateArrow(false);
        });

        dropdown.setOnItemClickListener((parent, view, position, id) -> {
            animateArrow(false);
        });
    }

    // ---------- PUBLIC API ----------

    public void setItems(String[] labels, String[] values) {
        this.labels = labels;
        this.values = values;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                R.layout.custom_dropdown_item,
                labels
        );

        dropdown.setAdapter(adapter);
    }

    public void setOnValueChanged(Consumer<String> callback) {
        dropdown.setOnItemClickListener((parent, view, position, id) -> {
            closeDropdown();
            if (values != null && position < values.length) {
                callback.accept(values[position]);
            }
        });
    }

    public void setSelectedValue(String value) {
        if (values == null) return;

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                dropdown.setText(labels[i], false);
                break;
            }
        }
    }

    public String getSelectedValue() {
        if (values == null) return null;

        String text = dropdown.getText().toString();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i].equals(text)) {
                return values[i];
            }
        }
        return null;
    }

    // ---------- INTERNAL BEHAVIOR ----------

    private void toggleDropdown() {
        if (!isOpen) {
            dropdown.showDropDown();
            isOpen = true;
            animateArrow(true);
        } else {
            closeDropdown();
        }
    }

    private void closeDropdown() {
        isOpen = false;
        animateArrow(false);
    }

    private void animateArrow(boolean open) {
        if (!AnimationSettings.areAnimationsEnabled())
            return;
        if (open)
            animDown.start();
        else
            animUp.start();
//        arrow.animate()
//                .rotation(open ? 180f : 0f)
//                .setDuration(200)
//                .start();


    }
}
