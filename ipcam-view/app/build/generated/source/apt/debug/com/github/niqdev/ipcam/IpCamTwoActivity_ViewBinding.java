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

public class IpCamTwoActivity_ViewBinding implements Unbinder {
  private IpCamTwoActivity target;

  @UiThread
  public IpCamTwoActivity_ViewBinding(IpCamTwoActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public IpCamTwoActivity_ViewBinding(IpCamTwoActivity target, View source) {
    this.target = target;

    target.mjpegView1 = Utils.findRequiredViewAsType(source, R.id.mjpegViewDefault1, "field 'mjpegView1'", MjpegView.class);
    target.mjpegView2 = Utils.findRequiredViewAsType(source, R.id.mjpegViewDefault2, "field 'mjpegView2'", MjpegView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    IpCamTwoActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mjpegView1 = null;
    target.mjpegView2 = null;
  }
}
