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

public class IpCamNativeActivity_ViewBinding implements Unbinder {
  private IpCamNativeActivity target;

  @UiThread
  public IpCamNativeActivity_ViewBinding(IpCamNativeActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public IpCamNativeActivity_ViewBinding(IpCamNativeActivity target, View source) {
    this.target = target;

    target.mjpegView = Utils.findRequiredViewAsType(source, R.id.mjpegViewNative, "field 'mjpegView'", MjpegView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    IpCamNativeActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mjpegView = null;
  }
}
