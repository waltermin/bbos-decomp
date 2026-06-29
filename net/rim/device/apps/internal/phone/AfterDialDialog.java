package net.rim.device.apps.internal.phone;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.api.ui.ButtonContainer;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;

final class AfterDialDialog extends PopupDialog {
   private LabelField _titleField;
   private ButtonField _dialButton;
   private ButtonField _skipButton;
   private ButtonField _endCallButton;
   private int _userResponse;
   private Font _font;
   private boolean _isDisplaying;
   public static final int DIAL = 0;
   public static final int SKIP = 1;
   public static final int ENDCALL = 2;

   AfterDialDialog(PopupDialogClosedListener listener) {
      super((Manager)(new Object(1152921504606846976L)), 33554432);
      this.setPopupDialogClosedListener(listener);
      this.setStatusPriority(-2147483645);
      this._font = Font.getDefault();
      if (this._font.getHeight(3) < 12) {
         this._font = this._font.derive(this._font.getStyle(), 12, 3);
      }

      this._userResponse = 0;
   }

   public final void show(String tones) {
      this._titleField = (LabelField)(new Object(PhoneResources.getString(205)));
      this.setFont(this._titleField, this._font);
      HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object());
      hfm.add((Field)(new Object(Bitmap.getPredefinedBitmap(1))));
      hfm.add((Field)(new Object(4)));
      hfm.add(this._titleField);
      this.add(hfm);
      this._endCallButton = (ButtonField)(new Object(PhoneResources.getString(402), 0));
      if (tones != null && tones.length() > 0) {
         String label = MessageFormat.format(PhoneResources.getString(252), new Object[]{tones});
         this._dialButton = (ButtonField)(new Object(label, 0));
         this._skipButton = (ButtonField)(new Object(PhoneResources.getString(460), 0));
      } else {
         this._dialButton = (ButtonField)(new Object(CommonResources.getString(800), 0));
         this._skipButton = null;
      }

      this.add((Field)(new Object(8)));
      ButtonContainer container = (ButtonContainer)(new Object(this._font));
      container.addButton(this._dialButton);
      if (this._skipButton != null) {
         container.addButton(this._skipButton);
      }

      container.addButton(this._endCallButton);
      this.add(container);
      this.add((Field)(new Object(4)));
      this._dialButton.setFocus();
      this._isDisplaying = true;
      super.show();
   }

   public final void dismiss() {
      this.close(-1);
   }

   final void dismissContinue() {
      this._userResponse = 0;
      this.close(0);
   }

   final boolean isDisplaying() {
      return this._isDisplaying;
   }

   private final void setFont(Field field, Font font) {
      if (font != null && field != null) {
         field.setFont(font);
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return this.invokeAction(1);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this._userResponse = this.getCurrentUserResponse();
            this.close(0);
            return true;
         default:
            return false;
      }
   }

   @Override
   protected final void close(int reason) {
      this._isDisplaying = false;
      super.close(reason);
   }

   private final int getCurrentUserResponse() {
      Field field = this.getLeafFieldWithFocus();
      if (field == this._dialButton) {
         return 0;
      } else if (field == this._skipButton) {
         return 1;
      } else {
         return field == this._endCallButton ? 2 : 1;
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 18) {
         this._userResponse = 2;
         this.close(0);
         return true;
      }

      if (PhoneUtilities.isMuteKey(keycode)) {
         LiveCall call = (LiveCall)CallManager.getInstance().getCurrentCall();
         if (call != null) {
            call.mute();
         }

         return true;
      } else if (PhoneUtilities.isSpeakerPhoneKey(keycode)) {
         LiveCall call = (LiveCall)CallManager.getInstance().getCurrentCall();
         if (call != null) {
            call.toggleSpeakerphone();
         }

         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\n':
            this._userResponse = this.getCurrentUserResponse();
            this.close(0);
         default:
            return true;
         case '\u001b':
            this._userResponse = 1;
            this.close(0);
            return true;
      }
   }

   public final int getUserResponse() {
      return this._userResponse;
   }
}
