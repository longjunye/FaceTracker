/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.face.facetracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import com.google.android.gms.samples.vision.face.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic extends GraphicOverlay.Graphic {
	private static final float FACE_POSITION_RADIUS = 10.0f;
	private static final float ID_TEXT_SIZE = 40.0f;
	private static final float ID_Y_OFFSET = 50.0f;
	private static final float ID_X_OFFSET = -50.0f;
	private static final float BOX_STROKE_WIDTH = 5.0f;

	private static final int COLOR_CHOICES[] = {
		Color.BLUE,
		Color.CYAN,
		Color.GREEN,
		Color.MAGENTA,
		Color.RED,
		Color.WHITE,
		Color.YELLOW
	};
	private static int mCurrentColorIndex = 0;

	private Paint mFacePositionPaint;
	private Paint mIdPaint;
	private Paint mBoxPaint;

	private volatile Face mFace;
	private int mFaceId;
	private float mFaceHappiness;
	private Bitmap decoration;
	private Context context;

	FaceGraphic(GraphicOverlay overlay, Context context) {
		super(overlay);
		this.context = context;

		mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
		final int selectedColor = Color.WHITE;

		mFacePositionPaint = new Paint();
		mFacePositionPaint.setColor(selectedColor);

		mIdPaint = new Paint();
		mIdPaint.setColor(selectedColor);
		mIdPaint.setTextSize(ID_TEXT_SIZE);

		mBoxPaint = new Paint();
		mBoxPaint.setColor(selectedColor);
		mBoxPaint.setStyle(Paint.Style.STROKE);
		mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
		decoration = BitmapFactory.decodeResource(context.getResources(), R.drawable.glasses_classic);
	}

	void setId(int id) {
		mFaceId = id;
	}

	/**
	 * Updates the face instance from the detection of the most recent frame.  Invalidates the
	 * relevant portions of the overlay to trigger a redraw.
	 */
	void updateFace(Face face) {
		mFace = face;
		postInvalidate();
	}

	/**
	 * Draws the face annotations for position on the supplied canvas.
	 */
	@Override
	public void draw(Canvas canvas) {
		Face face = mFace;
		if (face == null) {
			return;
		}

		// Draws a circle at the position of the detected face, with the face's track id below.
		float x = translateX(face.getPosition().x + face.getWidth() / 2);
		float y = translateY(face.getPosition().y + face.getHeight() / 2);
//		Matrix matrix = new Matrix();
//		matrix.postRotate(face.getEulerY());
		Bitmap bitmap = Bitmap.createScaledBitmap(decoration,
			(int) (face.getWidth()),
			(int) (face.getWidth()),
			false);
//		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		Paint p = new Paint();
		p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
		canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y - bitmap.getHeight() / 2, p);
//		canvas.drawBitmap(rotatedBitmap, x - rotatedBitmap.getWidth() / 2, y - rotatedBitmap.getHeight() / 2, p);
		// Draws a bounding box around the face.
//		float xOffset = scaleX(face.getWidth() / 2.0f);
//		float yOffset = scaleY(face.getHeight() / 2.0f);
//		float left = x - xOffset;
//		float top = y - yOffset;
//		float right = x + xOffset;
//		float bottom = y + yOffset;
//		canvas.drawRect(left, top, right, bottom, mBoxPaint);
	}

	/**
	 * Finds a specific landmark position, or approximates the position based on past observations
	 * if it is not present.
	 */
	private PointF getLandmarkPosition(Face face, int landmarkId) {
		for (Landmark landmark : face.getLandmarks()) {
			if (landmark.getType() == landmarkId) {
				return landmark.getPosition();
			}
		}
		return null;
	}
}
