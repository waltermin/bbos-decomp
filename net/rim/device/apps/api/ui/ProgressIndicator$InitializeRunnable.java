package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;

final class ProgressIndicator$InitializeRunnable implements Runnable {
   private LabelField _labelField;
   private String _popupLabel;
   private GaugeField _gaugeField;
   private String _gaugeLabel;
   private int _bottom;
   private int _top;
   private int _start;
   private PopupScreen _popupScreen;

   private ProgressIndicator$InitializeRunnable() {
   }

   final void setParameters(
      LabelField labelField, String popupLabel, GaugeField gaugeField, String gaugeLabel, int bottom, int top, int start, PopupScreen popupScreen
   ) {
      this._bottom = bottom;
      this._top = top;
      this._start = start;
      this._gaugeField = gaugeField;
      this._gaugeLabel = gaugeLabel;
      this._labelField = labelField;
      this._popupLabel = popupLabel;
      this._popupScreen = popupScreen;
   }

   @Override
   public final void run() {
      this._gaugeField.reset(this._gaugeLabel, this._bottom, this._top, this._start);
      this._labelField.setText(this._popupLabel);
      UiApplication.getUiApplication().pushScreen(this._popupScreen);
   }

   ProgressIndicator$InitializeRunnable(ProgressIndicator$1 x0) {
      this();
   }
}
