package ru.mobilab.gamesample;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
  SurfaceHolder.Callback {

 private static final String TAG = MainGamePanel.class.getSimpleName();

 private MainThread thread;
 private Droid droid;
 private ElaineAnimated elaine;
 public MainGamePanel(Context context) {
  super(context);
  // Сообщаем, что обработчик событий от поверхности будет реализован
  // в этом классе.
  getHolder().addCallback(this);
  //создаем анимированный персонаж
  elaine = new ElaineAnimated(
			BitmapFactory.decodeResource(getResources(), R.drawable.walk_elaine)
			, 10, 50	// начальное положение
			, 30, 47	// ширина и высота спрайта
			, 5, 5);	// FPS и число кадров в анимации
  // создаем объект для робота
  droid = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1), 50, 50);

  // создаем поток для игрового цикла
  thread = new MainThread(getHolder(), this);

  // делаем GamePanel способной обрабатывать фокус и события
  setFocusable(true);
 }

 @Override
 public void surfaceChanged(SurfaceHolder holder, int format, int width,
   int height) {
 }

 @Override
 public void surfaceCreated(SurfaceHolder holder) {
  // В этой точке поверхность уже создана и мы можем
  // безопасно запустить игровой цикл в потоке
  thread.setRunning(true);
  thread.start();
 }

 @Override
 public void surfaceDestroyed(SurfaceHolder holder) {
  Log.d(TAG, "Surface is being destroyed");
  //посылаем потоку команду на закрытие и дожидаемся, 
  //пока поток не будет закрыт.
  boolean retry = true;
  while (retry) {
   try {
    thread.join();
    retry = false;
   } catch (InterruptedException e) {
    // пытаемся снова остановить поток thread
   }
  }
  Log.d(TAG, "Thread was shut down cleanly");
 }

 @Override
 public boolean onTouchEvent(MotionEvent event) {
  if (event.getAction() == MotionEvent.ACTION_DOWN) {
   // вызываем метод handleActionDown, куда передаем координаты касания
   droid.handleActionDown((int)event.getX(), (int)event.getY());

   // если щелчок по нижней области экрана, то выходим
   if (event.getY() > getHeight() - 50) {
    thread.setRunning(false);
    ((Activity)getContext()).finish();
   } else {
    Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
   }
  } if (event.getAction() == MotionEvent.ACTION_MOVE) {
   // перемещение
   if (droid.isTouched()) {
    // робот находится в состоянии перетаскивания, 
	// поэтому изменяем его координаты
    droid.setX((int)event.getX());
    droid.setY((int)event.getY());
   }
  } if (event.getAction() == MotionEvent.ACTION_UP) {
   // Обрабатываем отпускание
   if (droid.isTouched()) {
    droid.setTouched(false);
   }
  }
  return true;
 }

 @Override
 protected void onDraw(Canvas canvas) {
	// Заливаем canvas черным цветом
	canvas.drawColor(Color.BLACK);	
	//рисуем анимированный спрайт
	elaine.draw(canvas);
	// Вызываем метод, который выводит рисунок робота
	droid.draw(canvas);
 }

 public void update() {
	    elaine.update(System.currentTimeMillis());

	 	// проверяем столкновение с правой стеной
		if (droid.getX() + droid.getBitmap().getWidth() / 2 >= getWidth()) {
			droid.getSpeed().toggleXDirection();
		}
		// проверяем столкновение с левой стеной
		if ( droid.getX() - droid.getBitmap().getWidth() / 2 <= 0) {
			droid.getSpeed().toggleXDirection();
		}
		// проверяем столкновение с нижней стеной
		if (droid.getY() + droid.getBitmap().getHeight() / 2 >= getHeight()) {
			droid.getSpeed().toggleYDirection();
		}
		// проверяем столкновение с верхней стеной
		if (droid.getY() - droid.getBitmap().getHeight() / 2 <= 0) {
			droid.getSpeed().toggleYDirection();
		}
		// Обновляем координаты робота
		droid.update();
	}
}
