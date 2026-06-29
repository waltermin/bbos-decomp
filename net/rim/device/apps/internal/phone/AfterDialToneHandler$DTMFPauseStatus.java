package net.rim.device.apps.internal.phone;

import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class AfterDialToneHandler$DTMFPauseStatus extends PopupScreen implements Runnable {
   private AfterDialToneHandler _toneHandler;
   private boolean _isDisplaying;
   private BitmapField _iconField;
   private RichTextField _messageField;
   private int _timerId;
   private final AfterDialToneHandler this$0;

   public AfterDialToneHandler$DTMFPauseStatus(AfterDialToneHandler _1, AfterDialToneHandler toneHandler) {
      super((Manager)(new Object()), 0);
      this.this$0 = _1;
      this._timerId = -1;
      this._iconField = (BitmapField)(new Object(Bitmap.getPredefinedBitmap(3)));
      this._iconField.setPadding(0, 3, 0, 0);
      this._messageField = (RichTextField)(new Object(null, 36028797018963968L));
      this.add(this._iconField);
      this.add(this._messageField);
      this._toneHandler = toneHandler;
   }

   public final void display(String message, int time) {
      this._messageField.setText(message);
      this._timerId = -1;
      this._isDisplaying = true;
      this._timerId = this.this$0._app.invokeLater(this, time, false);
      if (this._timerId == -1) {
         throw new Object("no timers available for pause status");
      }

      this.this$0._app.pushGlobalScreen(this, -2147483645, 2);
   }

   private final synchronized void dismiss(boolean timeout) {
      if (this._isDisplaying) {
         this._isDisplaying = false;
         this.this$0._app.popScreen(this);
         if (this._timerId != -1) {
            this.this$0._app.cancelInvokeLater(this._timerId);
         }
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (PhoneUtilities.isSpeakerPhoneKey(keycode)) {
         AudioPathControl control = AudioRouter.getInstance().getAudioPathControl(0);
         control.toggleSpeakerphone();
      }

      return super.keyDown(keycode, time);
   }

   public final void dismiss() {
      this.dismiss(false);
   }

   public final boolean isDisplaying() {
      return this._isDisplaying;
   }

   @Override
   public final void run() {
      this.dismiss(true);
      if (this._toneHandler != null) {
         this._toneHandler.pauseStatusDismissed();
      }
   }
}
