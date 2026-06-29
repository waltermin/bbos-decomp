package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;

public final class PrefNetworkListOptions$StatusScreen extends PopupScreen {
   private UiApplication _app;
   private LabelField _statusField;
   private BitmapField _iconField;
   private int _timeoutTimerId = -1;
   private boolean _timeoutExpired;
   private static final long DEFAULT_TIMEOUT;

   public PrefNetworkListOptions$StatusScreen() {
      super((Manager)(new Object()), 0);
      this._app = UiApplication.getUiApplication();
      this._statusField = (LabelField)(new Object());
      this._iconField = (BitmapField)(new Object(Bitmap.getPredefinedBitmap(3)));
      this._iconField.setPadding(0, 3, 0, 0);
      this.add(this._iconField);
      this.add(this._statusField);
   }

   public final void setStatus(String statusStr) {
      this._statusField.setText(statusStr);
      this.open();
   }

   private final synchronized void open() {
      Runnable closeRunner = new PrefNetworkListOptions$StatusScreen$1(this);
      this._timeoutTimerId = this._app.invokeLater(closeRunner, 5000, false);
      this._app.pushScreen(this);
   }

   @Override
   public final void close() {
      if (!this._timeoutExpired && this._timeoutTimerId != -1) {
         this._app.cancelInvokeLater(this._timeoutTimerId);
      }

      try {
         this._app.popScreen(this);
      } finally {
         return;
      }
   }
}
