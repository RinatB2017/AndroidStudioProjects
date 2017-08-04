// Generated code from Butter Knife. Do not modify!
package com.github.niqdev.ipcam;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  private View view2131492958;

  private View view2131492961;

  private View view2131492959;

  private View view2131492960;

  private View view2131492962;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.buttonDefault, "field 'buttonDefault' and method 'onClickDefault'");
    target.buttonDefault = Utils.castView(view, R.id.buttonDefault, "field 'buttonDefault'", Button.class);
    view2131492958 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickDefault();
      }
    });
    view = Utils.findRequiredView(source, R.id.buttonNative, "field 'buttonNative' and method 'onClickNative'");
    target.buttonNative = Utils.castView(view, R.id.buttonNative, "field 'buttonNative'", Button.class);
    view2131492961 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickNative();
      }
    });
    view = Utils.findRequiredView(source, R.id.buttonTwoCamera, "method 'onClickTwoCamera'");
    view2131492959 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickTwoCamera();
      }
    });
    view = Utils.findRequiredView(source, R.id.buttonSnapshot, "method 'onClickSnapshot'");
    view2131492960 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickSnapshot();
      }
    });
    view = Utils.findRequiredView(source, R.id.buttonSettings, "method 'onClickSettings'");
    view2131492962 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClickSettings();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.buttonDefault = null;
    target.buttonNative = null;

    view2131492958.setOnClickListener(null);
    view2131492958 = null;
    view2131492961.setOnClickListener(null);
    view2131492961 = null;
    view2131492959.setOnClickListener(null);
    view2131492959 = null;
    view2131492960.setOnClickListener(null);
    view2131492960 = null;
    view2131492962.setOnClickListener(null);
    view2131492962 = null;
  }
}
