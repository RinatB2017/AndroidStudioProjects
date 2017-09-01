package ru.gc986.testrxjava;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MyApp";

    String[] data = {
            "mytest0",
            "mytest1",
            "mytest2",
            "rxTest0" ,
            "rxTest" ,
            "rxTest1" ,
            "rxTest2" ,
            "rxTest3" ,
            "rxTest4" ,
            "rxTest5" ,
            "rxTest6" ,
            "rxTest7" ,
            "rxJava8" ,
            "rxJava9" ,
            "rxJava10" ,
            "rxJava11" ,
            "rxJava12" ,
            "rxJava13" ,
            "rxJava14"
    };

    static TextView log_main;
    Spinner spinner;
    Button btn_run;

    EditText editText;

    private static CheckBox cb_open;
    private static CheckBox cb_read;
    private static CheckBox cb_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_run = (Button)findViewById(R.id.btn_run);
        spinner = (Spinner)findViewById(R.id.spinner);
        log_main = (TextView)findViewById(R.id.log_main);

        cb_open = (CheckBox)findViewById(R.id.cb_open);
        cb_read = (CheckBox)findViewById(R.id.cb_read);
        cb_close = (CheckBox)findViewById(R.id.cb_close);

        editText = (EditText) findViewById(R.id.editText);
        Observable<String> emailObservable = RxEditText.getTextWatcherObservable(editText);

        //---
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        //---
    }

    void clean_log() {
        log_main.setText("");
    }

    static void logging(String text) {
        log_main.append(text + "\n");
        Log.i("MyApp", text);
    }

    //---------------------------------------------------------------------------------------------
    void mytest0(long param) {

        logging("mytest0");
        Observable.just(10)
                .map(MainActivity::test)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        logging("Завершено успешно!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logging(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer r) {
                    }
                });
    }
    private static int test(int value) {
        int temp = value;
        while(temp > 0) {
            logging(String.valueOf(temp));
            temp--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

//        CatTask cat = new CatTask();
//        cat.execute();

        return temp;
    }
    static class CatTask extends AsyncTask<Void, Integer, Void> {

        MainActivity activity;

        // получаем ссылку на MainActivity
        void link(MainActivity act) {
            activity = act;
        }

        // обнуляем ссылку
        void unLink() {
            activity = null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                int counter = 0;

                for (int i = 0; i < 100; i++) {
                    getFloor(counter);
                    publishProgress(++counter);
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            logging("Залез");
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            super.onProgressUpdate(values);

            logging("Этаж: " + values[0]);
        }

        private void getFloor(int floor) throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }
    }
    //---------------------------------------------------------------------------------------------
    void mytest1(long param) {

        Observable.just("Hello, world!")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        int a = 6;
                        int b = 0;
                        int c = a / b;
                        return s.hashCode() + c;
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer s) {
                        return "YES";
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        logging("Завершено успешно!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logging(e.getMessage());
                    }

                    @Override
                    public void onNext(String r) {
                    }
                });

        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) { logging(s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };
        myObservable.subscribe(mySubscriber);

        Observable.just("Hello, world2!")
                .subscribe(s -> logging(s));

        Observable.just(5)
                .map(integer -> { return integer / 2; } )
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        logging("Завершено успешно!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logging(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer r) {
                        if(r != 1)
                            logging(String.valueOf(r));
                    }
                });

        Observable.just(5)
                .map(integer -> { return integer / 3.0; } )
                .subscribe(integer -> { logging(String.valueOf(integer)); });

        Observable.just(5)
                .map(integer -> { return integer / 3.0; } )
                .subscribe(integer -> { logging(String.valueOf(integer)); });

        Observable.just("Мой Мега тест!")
                .map(string -> {
                    string = "0";
                    logging("text 0");
                    return string;
                })
                .map(string -> {
                    logging("text 1");
                    return string;
                })
                .map(string -> {
                    logging("text 2");
                    return string;
                })
                .subscribe(string -> { logging(string); });
    }
    //---------------------------------------------------------------------------------------------
    void mytest2(long param) {
        long timeout= System.currentTimeMillis();

        Observable.range(1, 10)
                .map(MainActivity::doubleValue)
                //.filter(integer -> integer == 6)
                .map(MainActivity::addValue)
                .timeout(1100, TimeUnit.MILLISECONDS)
                //.subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        logging("Completed!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logging("Error: " + e.toString());
                        logging(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer value) {
                        printOut(value);
                    }
                });

        timeout = System.currentTimeMillis() - timeout;
        logging(String.valueOf(timeout / 1000.0 + " s"));
    }

    private static void printOut(int value) {
        logging(Thread.currentThread().getName() + ": " + value);
    }

    private static int addValue(int value) {
        return value + 1;
    }

    private static int doubleValue(int value) {
        if(value == 5)
            throw new RuntimeException("xxx");
        return value * 2;
    }

    private static int sleeping_doubleValue(int value) {
        try {
            logging(String.valueOf(Thread.currentThread().getName() + ": doubling value"));
            Thread.sleep(2000);
            return value * 2;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //---------------------------------------------------------------------------------------------
    void mytest3(long param) {

        //---
        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        sub.onNext("Hello, world!");
                        sub.onCompleted();
                    }
                }
        );
        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) { logging(s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };
        myObservable.subscribe(mySubscriber);
        //---
        /*
        Observable.just(1)
                .map(MainActivity::step0)
                .map(MainActivity::step1)
                .map(MainActivity::step2)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        logging("Завершено успешно!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logging(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer r) {
                        if(r != 1)
                            logging(String.valueOf(r));
                    }
                });
        */
    }

    private static int step0(int value) {
        logging("Подключение к серверу");
        if(check_open())
            throw new RuntimeException("Ошмбка подключения к серверу");
        return value;
    }
    private static int step1(int value) {
        logging("Запрос данных");
        if(check_data())
            throw new RuntimeException("Ошмбка получения данных");
        return value;
    }
    private static int step2(int value) {
        logging("Отключение от сервера");
        if(check_close())
            throw new RuntimeException("Ошмбка отключения от сервера");
        return value;
    }


    public static boolean check_open() {
        return !cb_open.isChecked();
    }
    public static boolean check_data() {
        return !cb_read.isChecked();
    }
    public static boolean check_close() {
        return !cb_close.isChecked();
    }
    //---------------------------------------------------------------------------------------------
    void rxTest(long param) {

        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        for(int i = 0 ; i < 10 ; i++)
                            sub.onNext("Message " + i);
                        sub.onCompleted();
                    }
                }
        );

        Subscriber<String> mySubscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) { logging(s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };

        myObservable.subscribe(mySubscriber);
    }
    //---------------------------------------------------------------------------------------------
    void rxTest0(long param) {

        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        for(int i = 0 ; i < 20 ; i++)
                            sub.onNext("Message " + i);
                        sub.onCompleted();
                    }
                }
        );

        Subscriber<String> mySubscriber1 = new Subscriber<String>() {
            @Override
            public void onNext(String s) { logging("1 - " + s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };

        Subscriber<String> mySubscriber2 = new Subscriber<String>() {
            @Override
            public void onNext(String s) { logging("2 - " + s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };

        myObservable.subscribe(mySubscriber2);
        myObservable.subscribe(mySubscriber1);
    }
    //---------------------------------------------------------------------------------------------
    void rxTest1(long param) {

        Observable<String> myObservable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        for(int i = 0 ; i < 20 ; i++)
                            sub.onNext("Message " + i);
                        sub.onCompleted();
                    }
                }
        );

        Observable<String> myObservable2 = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        for(int i = 0 ; i < 20 ; i++)
                            sub.onNext("Message2 " + i);
                        sub.onCompleted();
                    }
                }
        );

        Subscriber<String> mySubscriber1 = new Subscriber<String>() {
            @Override
            public void onNext(String s) { Log.i("MyApp",s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };

        myObservable.subscribe(mySubscriber1);
        myObservable2.subscribe(mySubscriber1);

    }
    //---------------------------------------------------------------------------------------------
    void rxTest2(long param) {
        Observable.just("My_Rx_Message")
                .subscribe(s -> logging(s));
    }
    //---------------------------------------------------------------------------------------------
    void rxTest3(String... collection){
        Observable.from(collection)
                .subscribe(s -> logging("Collect - " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxTest4(long param) {
        String[] arr = {"1","2","3"};
        Observable.just(arr)
                .flatMap(a -> Observable.from(a))
                .subscribe(s -> logging("Item - " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxTest5(long param) {
        String[] arr = {"Один","Два","Три","Четыре","Пять"};
        Observable.from(arr)
                .flatMap(s -> forTest5(s))
                .subscribe(i -> logging("length = "+i));
    }

    Observable<Integer> forTest5(String str){
        return Observable.just(str)
                .map(s -> s.length());
    };
    //---------------------------------------------------------------------------------------------
    void rxTest6(long param) {
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .filter(s -> !s.equals("4"))
                .subscribe(s -> logging("item = " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxTest7(long param) {
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .take(3)
                .subscribe(s -> logging("item = " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava8(long param) {
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .doOnNext(s1 -> logging(s1))
                .filter(s -> !s.equals("4"))
                .doOnNext(s1 -> logging("-- "+s1))
                .subscribe(s2 -> logging("item = "+s2));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava9(long param) {
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .doOnCompleted(() -> logging("End"))
                .subscribe(s -> logging("item = " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava10(long param) {
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .map(s -> errMap(s))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        logging("Complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        logging("Err >>> " + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        logging("OnNext - " + s);
                    }
                });

    }
    //---------------------------------------------------------------------------------------------
    String errMap(String s){
        return (s.equals("4") ? s.substring(s.length(),s.length()+1) : s);
    }
    //---------------------------------------------------------------------------------------------
    void rxJava11(long param) {
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .first()
                .subscribe(s -> logging("Item => " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava12(long param) {
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .last()
                .subscribe(s -> logging("Item => " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava13(long param) {
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .last()
                .first()
                .subscribe(s -> logging("Item ? " + s));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava14(long param) {
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .map(s->("-"+s+"-"))
                .subscribe(s->logging(s));
    }
    //---------------------------------------------------------------------------------------------
    void rxJava15(long param) {
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .map(s->("-"+s+"-"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s->logging(s));
    }
    //---------------------------------------------------------------------------------------------
    public void run(View view) {
        btn_run.setEnabled(false);

        clean_log();

        String methodName = spinner.getSelectedItem().toString();

        Method method;
        try {
            method = this.getClass().getDeclaredMethod(methodName, long.class);
            method.invoke(this, 0);
        }
        catch (NoSuchMethodException e) {
            logging(e.toString());
        }
        catch (IllegalAccessException e) {
            logging(e.toString());
        }
        catch (InvocationTargetException e) {
            logging(e.toString());
        }
        catch (IllegalArgumentException e) {
            logging(e.toString());
        }

        btn_run.setEnabled(true);
    }
    //---------------------------------------------------------------------------------------------
}
