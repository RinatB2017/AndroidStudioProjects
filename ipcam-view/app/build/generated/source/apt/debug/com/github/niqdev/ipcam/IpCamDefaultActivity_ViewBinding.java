// Generated code from Butter Knife. Do not modify!
package com.github.niqdev.ipcam;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.github.niqdev.mjpeg.MjpegView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class IpCamDefaultActivity_ViewBinding implements Unbinder {
  private IpCamDefaultActivity target;

  @UiThread
  public IpCamDefaultActivity_ViewBinding(IpCamDefaultActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public IpCamDefaultActivity_ViewBinding(IpCamDefaultActivity target, View source) {
    this.target = target;

    target.mjpegView = Utils.findRequiredViewAsType(source, R.id.mjpegViewDefault, "field 'mjpegView'", MjpegView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    IpCamDefaultActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mjpegView = null;
  }
}
