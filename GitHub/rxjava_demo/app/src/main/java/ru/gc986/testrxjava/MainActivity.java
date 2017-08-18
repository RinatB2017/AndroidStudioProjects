package ru.gc986.testrxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    String TAG = "MyApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //rxTest_();

        //rxTest();

        //rxTest1();

        //rxTest2();

        //rxTest3("1","2","3");

        //rxTest4();

        //rxTest5();

        //rxTest6();

        //rxTest7();

        //rxJava8();

        //rxJava9();

        //rxJava10();

        //rxJava11();

        //rxJava12();

        //rxJava13();

        //rxJava14();
    }

    void rxTest_(){

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
            public void onNext(String s) { Log.i("MyApp",s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };

        myObservable.subscribe(mySubscriber);
    }

    void rxTest(){

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
            public void onNext(String s) { Log.i("MyApp","1 - " + s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };

        Subscriber<String> mySubscriber2 = new Subscriber<String>() {
            @Override
            public void onNext(String s) { Log.i("MyApp","2 - " + s); }

            @Override
            public void onCompleted() { }

            @Override
            public void onError(Throwable e) { }
        };



        myObservable.subscribe(mySubscriber2);
        myObservable.subscribe(mySubscriber1);

    }

    void rxTest1(){

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

    void rxTest2(){
        Observable.just("My_Rx_Message")
                .subscribe(s -> Log.i("MyApp",s));
    }

    void rxTest3(String... collection){
        Observable.from(collection)
                .subscribe(s -> Log.i("MyApp","Collect - " + s));
    }

    void rxTest4(){
        String[] arr = {"1","2","3"};
        Observable.just(arr)
                .flatMap(a -> Observable.from(a))
                .subscribe(s -> Log.i("MyApp","Item - " + s));
    }

    void rxTest5(){
        String[] arr = {"Один","Два","Три","Четыре","Пять"};
        Observable.from(arr)
                .flatMap(s -> forTest5(s))
                .subscribe(i -> Log.i("MyApp","length = "+i));
    }

    Observable<Integer> forTest5(String str){
        return Observable.just(str)
                .map(s -> s.length());
    };

    void rxTest6(){
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .filter(s -> !s.equals("4"))
                .subscribe(s -> Log.i("MyApp","item = " + s));
    }

    void rxTest7(){
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .take(3)
                .subscribe(s -> Log.i("MyApp","item = " + s));
    }

    void rxJava8(){
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .doOnNext(s1 -> Log.i("MyApp",s1))
                .filter(s -> !s.equals("4"))
                .doOnNext(s1 -> Log.i("MyApp","-- "+s1))
                .subscribe(s2 -> Log.i("MyApp","item = "+s2));
    }

    void rxJava9(){
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .doOnCompleted(() -> Log.i("MyApp","End"))
                .subscribe(s -> Log.i("MyApp","item = " + s));
    }

    void rxJava10(){
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .map(s -> errMap(s))
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.i("MyApp","Complete!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("MyApp","Err >>> " + e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("MyApp","OnNext - " + s);
                    }
                });

    }

    String errMap(String s){
        return (s.equals("4") ? s.substring(s.length(),s.length()+1) : s);
    }

    void rxJava11(){
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .first()
                .subscribe(s -> Log.i("MyApp","Item => " + s));
    }

    void rxJava12(){
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .last()
                .subscribe(s -> Log.i("MyApp","Item => " + s));
    }

    void rxJava13(){
        String[] arr = {"1","2","3","4","5"};

        Observable.from(arr)
                .last()
                .first()
                .subscribe(s -> Log.i("MyApp","Item ? " + s));
    }

    void rxJava14(){
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .map(s->("-"+s+"-"))
                .subscribe(s->Log.i(TAG,s));

    }

    void rxJava15(){
        String[] arr = {"1","2","3","4","5"};
        Observable.from(arr)
                .map(s->("-"+s+"-"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s->Log.i(TAG,s));
    }

}
