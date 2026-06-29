package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

public class PhoneEventListenerStatusDialog extends PopupScreen implements PhoneEventListener, Runnable {
   protected UiApplication _app;
   private RichTextField _statusField;
   private BitmapField _iconField;
   private boolean _modal;
   private long _timeout;
   private int _closeEvent = 0;
   private int _timeoutTimerId = -1;
   private boolean _timeoutExpired;
   private ButtonField _closeButton;

   protected void setModal(boolean modal) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void showModal() {
      this._modal = true;
      this.show();
   }

   public synchronized void show() {
      if (this._timeout > 0) {
         this._timeoutTimerId = this._app.invokeLater(this, this._timeout, false);
      }

      VoiceServices.addPhoneEventListener(this);
      if (this._modal) {
         this._app.pushModalScreen(this);
      } else {
         this._app.pushScreen(this);
      }
   }

   public int getCloseEvent() {
      return this._closeEvent;
   }

   protected void close(int closeEvent) {
      if (!this._timeoutExpired && this._timeoutTimerId != -1) {
         this._app.cancelInvokeLater(this._timeoutTimerId);
      }

      this._closeEvent = closeEvent;

      label36:
      try {
         synchronized (this._app.getAppEventLock()) {
            this._app.popScreen(this);
         }
      } finally {
         break label36;
      }

      VoiceServices.removePhoneEventListener(this);
   }

   protected void onTimeout() {
      this._timeoutExpired = true;
      this.close(150000);
   }

   protected void onCloseButton() {
      this.close(0);
   }

   protected void onEvent(int _1, int _2, Object _3) {
      throw null;
   }

   @Override
   public void run() {
      this.onTimeout();
   }

   @Override
   public void phoneEventNotify(int eventId, int param, Object context) {
      this.onEvent(eventId, param, context);
   }

   @Override
   public void close() {
      this.close(0);
   }

   public PhoneEventListenerStatusDialog(String title, boolean modal, boolean closeButton, int timeoutInSeconds) {
      super(new VerticalFieldManager(), 0);
      this._app = UiApplication.getUiApplication();
      this._modal = modal;
      this._timeout = timeoutInSeconds * 1000;
      this._iconField = new BitmapField(Bitmap.getPredefinedBitmap(3));
      this._iconField.setPadding(0, 3, 0, 0);
      this._statusField = new RichTextField(title, 36028797018964032L);
      HorizontalFieldManager hfm = new HorizontalFieldManager(1152921504606846976L);
      hfm.add(this._iconField);
      hfm.add(this._statusField);
      this.add(hfm);
      if (closeButton) {
         this._closeButton = PhoneUtilities.getCloseButton();
         PhoneEventListenerStatusDialog$HorizontalCentringManager hcm = new PhoneEventListenerStatusDialog$HorizontalCentringManager();
         hcm.add(this._closeButton);
         this.add(hcm);
      }
   }

   public PhoneEventListenerStatusDialog(String title, boolean modal, int timeoutInSeconds) {
      this(title, modal, false, timeoutInSeconds);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      if (this._closeButton != null) {
         Field focus = this.getLeafFieldWithFocus();
         if (focus == this._closeButton) {
            this.onCloseButton();
            return true;
         }
      }

      return super.trackwheelClick(status, time);
   }
}
