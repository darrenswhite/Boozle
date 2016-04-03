package com.darrenswhite.boozle.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * @author Darren White
 */
public class ProgressBar extends View {

	private static final float WAVE_FREQEUENCY = 1.5f;
	private static final float WAVE_PHASE_SHIFT = -0.15f;
	private static final float WAVE_DENSITY = 10f;

	private static final Random rnd = new Random();

	private final Path wavePath = new Path();
	private Particle[] particles;

	private float wavePhase;
	private float progress;

	private int foreground;
	private int particle;

	private Paint foregroundPaint;
	private Paint particlePaint;

	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		foregroundPaint.setColor(foreground);
		foregroundPaint.setStyle(Paint.Style.FILL);

		particlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		particlePaint.setColor(particle);
		particlePaint.setStyle(Paint.Style.FILL);
	}

	private void drawParticles(Canvas canvas, float height) {
		if (particles == null) {
			particles = new Particle[canvas.getWidth() * canvas.getHeight() / 10000];

			for (int i = 0; i < particles.length; i++) {
				Particle p = new Particle();

				p.x = canvas.getWidth() * rnd.nextFloat();
				p.y = canvas.getHeight() * rnd.nextFloat();
				p.gravity = -(rnd.nextFloat() * 9.8f);
				p.radius = rnd.nextInt(3);

				particles[i] = p;
			}
		}

		for (Particle p : particles) {
			p.draw(canvas);
			p.tick();

			if (p.getY() < 0) {
				p.x = canvas.getWidth() * rnd.nextFloat();
				p.y = canvas.getHeight() - (height > canvas.getHeight() - 20 ? 0 : rnd.nextInt(20));
				p.gravity = -(rnd.nextFloat() * 9.8f);
				p.radius = rnd.nextInt(3);
			}
		}
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

		canvas.drawPath(wavePath, foregroundPaint);
		wavePath.reset();
		wavePhase += WAVE_PHASE_SHIFT;
	}

	public int getForegroundColor() {
		return foreground;
	}

	public int getParticleColor() {
		return particle;
	}

	public float getProgress() {
		return progress;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float height = canvas.getHeight() * (1f - progress / 100f);

		drawWave(canvas, height);
		drawParticles(canvas, height);

		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
		int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
		int min = Math.min(width, height);

		setMeasuredDimension(min, min);
	}

	public void setForegroundColor(int color) {
		foreground = color;
		foregroundPaint.setColor(color);
		invalidate();
		requestLayout();
	}

	public void setParticleColor(int color) {
		particle = color;
		particlePaint.setColor(color);
		invalidate();
		requestLayout();
	}

	public void setProgress(float progress) {
		this.progress = progress;
		invalidate();
	}

	private class Particle {

		private float gravity;
		private int radius;
		private float x, y;

		private void draw(Canvas canvas) {
			canvas.drawCircle(x, y, radius, particlePaint);
		}

		private float getX() {
			return x;
		}

		private float getY() {
			return y;
		}

		private void tick() {
			y += gravity;
		}
	}
}