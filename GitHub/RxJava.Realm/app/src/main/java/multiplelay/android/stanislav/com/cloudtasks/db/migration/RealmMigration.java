package multiplelay.android.stanislav.com.cloudtasks.db.migration;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * Created by LasVegas on 25.04.2017.
 */

public class RealmMigration implements io.realm.RealmMigration  {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.create("TaskRealmModel")
                    .addField("title",String.class);

            oldVersion++;
        }
    }
}
