package ru.mobilab.gamesample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class ElaineAnimated {
	private static final String TAG = ElaineAnimated.class.getSimpleName();

	private Bitmap bitmap;		// Картинка с анимационной последовательностью
	private Rect sourceRect;	// Прямоугольная область в bitmap,  которую нужно нарисовать
	private int frameNr;		// Число кадров в анимации
	private int currentFrame;	// Текущий кадр
	private long frameTicker;	// время обновления последнего кадра
	private int framePeriod;	// сколько миллисекунд должно пройти перед сменой кадра (1000/fps)

	private int spriteWidth;	// ширина спрайта (одного кадра)
	private int spriteHeight;	// высота спрайта

	private int x;				// X координата спрайта (верхний левый угол картинки)
	private int y;				// Y координата спрайта (верхний левый угол картинки)
	
	public ElaineAnimated(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		currentFrame = 0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth() / frameCount;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		framePeriod = 1000 / fps;
		frameTicker = 0l;
	}
	
	public void update(long gameTime) {
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			// увеличиваем номер текущего кадра
			currentFrame++;
			if (currentFrame >= frameNr) {
				//если текущий кадр преышает номер последнего кадра в 
				// анимационной последовательности, то переходим на нулевой кадр
				currentFrame = 0; 
			}
		}
		// Определяем область на рисунке с раскадровкой, соответствующую текущему кадру
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
	}
	
	public void draw(Canvas canvas) {
		// область, где рисуется спрайт
		Rect destRect = new Rect(x, y, x + spriteWidth, y + spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);

		canvas.drawBitmap(bitmap, 20, 150, null);
		Paint paint = new Paint();
		paint.setARGB(50, 0, 255, 0);
		canvas.drawRect(20 + (currentFrame * destRect.width()), 150, 20 + (currentFrame * destRect.width()) + destRect.width(), 150 + destRect.height(),  paint);
		
	}
}
