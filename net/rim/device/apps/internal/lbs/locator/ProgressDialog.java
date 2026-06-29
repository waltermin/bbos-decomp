package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;

public final class ProgressDialog extends PopupScreen implements FieldChangeListener {
   private ButtonField _cancelField;
   private FlowFieldManager _hfm = (FlowFieldManager)(new Object(12884901888L));
   public GaugeField _statusGauge;
   public ProgressDialog$ProgressThread _progressThread;
   public Request$Listener _listener;
   private boolean _cancelPressed = false;

   public final void doModal() {
      Ui.getUiEngine().pushModalScreen(this);
   }

   public final boolean isCancelPressed() {
      return this._cancelPressed;
   }

   public final void close(boolean result) {
      if (!result) {
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (this.isDisplayed()) {
         if (field == this._cancelField) {
            this._cancelPressed = true;
            this.close();
         }
      }
   }

   private final void addButtons() {
      this._cancelField = (ButtonField)(new Object(CommonResources.getString(9042), 65536));
      this._cancelField.setChangeListener(this);
      this._hfm.add(this._cancelField);
      this.add(this._hfm);
   }

   public ProgressDialog(Request$Listener listener, String title, int offset, int timeout) {
      super((Manager)(new Object(1153220571769602048L)), 196608);
      this._listener = listener;
      this.applyTheme();
      this.addTitle(title);
      this.addGauge(offset, timeout);
      this.addButtons();
      this._cancelField.setFocus();
      this._progressThread = new ProgressDialog$ProgressThread(this, this, this._statusGauge.getValue(), this._statusGauge.getValueMax());
      this._progressThread.start();
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int size = Ui.convertSize(FontRegistry.DEFAULT_SIZE, 3, 0);
      this.setFont(null);
      Font font = this.getFont();
      if (font.getHeight() > size && size > 0) {
         this.setFont(font.derive(font.getStyle(), size));
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._cancelPressed = true;
         this.close();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   private final void addGauge(int offset, int timeout) {
      this._statusGauge = (GaugeField)(new Object(null, 0, timeout, offset, 65536));
      this.add(this._statusGauge);
   }

   private final void addTitle(String title) {
      if (title != null && title.length() > 0) {
         LabelField labelField = (LabelField)(new Object(title));
         labelField.setFont(Font.getDefault().derive(1));
         this.add(labelField);
      }
   }
}
