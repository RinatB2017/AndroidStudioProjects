package multiplelay.android.stanislav.com.cloudtasks.db.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by LasVegas on 25.04.2017.
 */

public class TaskRealModel extends RealmObject {

    @PrimaryKey
    private long id;

    private String title;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
