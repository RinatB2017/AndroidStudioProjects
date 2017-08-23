package multiplelay.android.stanislav.com.cloudtasks.db;


import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import multiplelay.android.stanislav.com.cloudtasks.db.migration.RealmMigration;
import multiplelay.android.stanislav.com.cloudtasks.db.models.TaskRealModel;
import rx.Observable;

/**
 * Created by LasVegas on 25.04.2017.
 */

public class DBservise {

    
  private RealmConfiguration mConfig = new RealmConfiguration.Builder()
          .schemaVersion(1)
          .migration(new RealmMigration())
          .build();

    public <T extends RealmObject> Observable<T> save(T object, Class<T> clazz) {
        Realm realm = Realm.getInstance(mConfig);

        long id;

        try {
            id = realm.where(clazz).max("id").intValue() + 1;
        } catch (Exception e) {
            id = 0L;
        }

        ((TaskRealModel) object).setId(id);

        return Observable.just(object)
                .flatMap(t -> Observable.just(t)
                .doOnSubscribe(realm::beginTransaction)
                .doOnUnsubscribe(() -> {
                    realm.commitTransaction();
                    realm.close();
                })
                .doOnNext(realm::copyToRealm)
                );
    }

    public <T extends RealmObject> Observable<List<T>> getAll (Class<T> clazz) {
        Realm realm = Realm.getInstance(mConfig);

        return Observable.just(clazz)
                .flatMap(t-> Observable.just(t)
                        .doOnSubscribe(realm::beginTransaction)
                        .doOnUnsubscribe(() -> {
                            realm.commitTransaction();
                            realm.close();
                        })
                        .map(type -> realm.where(type).findAll())
                );
    }
 }
