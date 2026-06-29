package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class ProgressDialog extends PopupScreen {
   private GaugeField _gaugeField = new GaugeField();

   public ProgressDialog(String label) {
      super(new VerticalFieldManager(1152921504606846976L));
      this.add(new LabelField(label, 12884901952L));
      this.add(this._gaugeField);
   }

   public final void show() {
      UiApplication.getUiApplication().pushScreen(this);
   }

   public final void dismiss() {
      UiApplication.getUiApplication().popScreen(this);
   }

   public final void setProgress(int percentage) {
      this._gaugeField.setValue(percentage);
   }
}
