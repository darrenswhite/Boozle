package com.darrenswhite.boozle.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Darren White
 */
public class ProgressBar extends View {

	private static final float WAVE_FREQEUENCY = 1.5f;
	private static final float WAVE_PHASE_SHIFT = -0.15f;
	private static final float WAVE_DENSITY = 5.0f;

	private final Path wavePath = new Path();
	private float wavePhase;

	private float progress;

	private int bg;
	private int fg;

	private Paint bgPaint;
	private Paint fgPaint;

	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void drawWave(Canvas canvas, float height) {
		float width = canvas.getWidth();
		float amplitude = (20f + progress / 3f) * 0.75f;

		wavePath.moveTo(width, height);
		wavePath.lineTo(width, canvas.getHeight());
		wavePath.lineTo(0, canvas.getHeight());
		wavePath.lineTo(0, height);

		for (float x = 0; x < width + WAVE_DENSITY; x += WAVE_DENSITY) {
			float y = (float) (amplitude * Math.sin(2 * Math.PI * (x / width) * WAVE_FREQEUENCY + wavePhase) + height);

			wavePath.lineTo(x, y);
		}

		canvas.drawPath(wavePath, fgPaint);
		wavePath.reset();
		wavePhase += WAVE_PHASE_SHIFT;
	}

	public int getBackgroundColor() {
		return bg;
	}

	public int getForegroundColor() {
		return fg;
	}

	public float getProgress() {
		return progress;
	}

	private void init(Context context, AttributeSet attrs) {
		bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		bgPaint.setColor(bg);
		bgPaint.setStyle(Paint.Style.FILL);

		fgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		fgPaint.setColor(fg);
		fgPaint.setStyle(Paint.Style.FILL);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), bgPaint);

		float height = canvas.getHeight() * (1f - progress / 100f);

		drawWave(canvas, height);

		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int min = Math.min(width, height);

		setMeasuredDimension(min, min);
	}

	public void setBackgroundColor(int color) {
		bg = color;
		bgPaint.setColor(color);
		invalidate();
		requestLayout();
	}

	public void setForegroundColor(int color) {
		fg = color;
		fgPaint.setColor(color);
		invalidate();
		requestLayout();
	}

	public void setProgress(float progress) {
		this.progress = progress;
		invalidate();
	}
}



















