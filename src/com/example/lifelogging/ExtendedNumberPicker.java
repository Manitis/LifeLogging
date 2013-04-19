package com.example.lifelogging;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.NumberPicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ExtendedNumberPicker extends NumberPicker {

	public ExtendedNumberPicker(Context context) {
		super(context);
	}

	public ExtendedNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		processAttributeSet(attrs);
	}

	public ExtendedNumberPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		processAttributeSet(attrs);
	}

	private void processAttributeSet(AttributeSet attrs) {
		this.setValue(attrs.getAttributeIntValue(null, "value", 5));
		this.setMinValue(attrs.getAttributeIntValue(null, "min_value", 0));
		this.setMaxValue(attrs.getAttributeIntValue(null, "max_value", 1));
		this.setWrapSelectorWheel(attrs.getAttributeBooleanValue(null, "wrap_wheel", true));
	}
}