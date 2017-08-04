// Generated code from Butter Knife. Do not modify!
package com.github.niqdev.ipcam;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.niqdev.mjpeg.MjpegView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class IpCamSnapshotActivity_ViewBinding implements Unbinder {
  private IpCamSnapshotActivity target;

  @UiThread
  public IpCamSnapshotActivity_ViewBinding(IpCamSnapshotActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public IpCamSnapshotActivity_ViewBinding(IpCamSnapshotActivity target, View source) {
    this.target = target;

    target.mjpegView = Utils.findRequiredViewAsType(source, R.id.mjpegViewSnapshot, "field 'mjpegView'", MjpegView.class);
    target.imageView = Utils.findRequiredViewAsType(source, R.id.imageView, "field 'imageView'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    IpCamSnapshotActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mjpegView = null;
    target.imageView = null;
  }
}
