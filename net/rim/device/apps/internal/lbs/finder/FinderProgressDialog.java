package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.protocol.Request$Listener;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class FinderProgressDialog extends PopupScreen implements FieldChangeListener {
   private FinderProgressDialog$ProgressThread _progressThread;
   private GaugeField _statusGauge = new GaugeField(null, 0, 45, 2, 65536);
   private ButtonField _statusButton;
   private boolean _cancelled;
   private Request$Listener _listener;

   public final void start() {
      if (!this._cancelled) {
         UiApplication.getUiApplication().pushModalScreen(this);
      }
   }

   public final void stop() {
      this.stop(false);
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._statusButton) {
         this.stop(true);
      }
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (c != 27 && c != '\n') {
         return false;
      }

      this.fieldChanged(this._statusButton, 0);
      return true;
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._progressThread.start();
         this._statusButton.setFocus();
      }
   }

   public FinderProgressDialog(Request$Listener listener) {
      super(new VerticalFieldManager());
      this.add(new RichTextField(MessageFormat.format(LBSResources.getString(47), new String[]{"..."}), 36028797018963968L));
      this.add(this._statusGauge);
      this._statusButton = new ButtonField(CommonResources.getString(9042), 12884967424L);
      this.add(this._statusButton);
      this._statusButton.setChangeListener(this);
      this._progressThread = new FinderProgressDialog$ProgressThread(this, this._statusGauge.getValue(), this._statusGauge.getValueMax());
      this._listener = listener;
   }

   private final void stop(boolean notifyListener) {
      if (!this._cancelled) {
         this._progressThread.stopProgress();
         this._progressThread = null;
         this._cancelled = true;
         synchronized (Application.getEventLock()) {
            this.close();
         }

         if (notifyListener) {
            this._listener.requestComplete(null);
         }
      }
   }
}
