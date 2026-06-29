package net.rim.device.apps.internal.security;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.internal.ui.component.PopupDialog;

final class SecurityApp$SendForEmergencyCallDialog extends PopupDialog implements Runnable {
   private SecurityApp _securityApp;
   private boolean _visible;
   private final SecurityApp this$0;
   private static final long DIALOG_TIMEOUT = 20000L;

   SecurityApp$SendForEmergencyCallDialog(SecurityApp _1) {
      super(new DialogFieldManager(), 33554432);
      this.this$0 = _1;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setIcon(new BitmapField(Bitmap.getPredefinedBitmap(2), 51539607552L));
      dfm.setMessage(new RichTextField(_1._rb.getString(748), 36028797018963968L));
      this.setStatusPriority(-2147483647);
      this._securityApp = (SecurityApp)Application.getApplication();
      this._securityApp.invokeLater(this, 20000, false);
      this._visible = true;
      Backlight.setTimeout(10);
   }

   private final synchronized void doClose() {
      if (this._visible) {
         this._visible = false;
         this.close(-1);
      }
   }

   @Override
   public final void run() {
      Backlight.setTimeout(5);
      this.doClose();
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      this.doClose();
      if (Keypad.key(keycode) == 17) {
         this.this$0._callHandler.makeEmergencyCall(false);
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this.doClose();
      return false;
   }
}
