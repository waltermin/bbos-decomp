package net.rim.device.apps.internal.phone;

import java.util.Vector;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.DTMFEchoField;
import net.rim.device.internal.ui.Image;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.awt.im.spi.InputModeChangeListener;

class ActivePhoneUI extends Manager implements InputModeChangeListener {
   private UiApplication _app;
   private Screen _ownerScreen;
   private int _inputMode = 0;
   private boolean _keypadLetterMode;

   protected Screen getOwnerScreen() {
      return this._ownerScreen;
   }

   protected FieldProvider getRibbonComponentFieldProvider() {
      return (FieldProvider)this._app;
   }

   void callerIDUpdated(int _1, Object _2) {
      throw null;
   }

   DTMFEchoField getDTMFEchoField() {
      throw null;
   }

   void ECAUpdated() {
      throw null;
   }

   void onKeypadStatusUpdated(int status) {
      boolean letterMode = (status & 1) != 0 || (status & 2) != 0 || (status & 4) != 0;
      if (letterMode != this._keypadLetterMode) {
         this._keypadLetterMode = letterMode;
         this.updateDtmfCursor();
      }
   }

   void onDTMFBufferCleared() {
      DTMFEchoField dtmf = this.getDTMFEchoField();
      if (dtmf != null) {
         dtmf.onDTMFBufferCleared();
      }
   }

   String getDTMFString() {
      DTMFEchoField dtmf = this.getDTMFEchoField();
      return dtmf != null ? dtmf.getDTMFString() : null;
   }

   void addFields(Vector _1, LiveCall _2, Object _3) {
      throw null;
   }

   protected Object getCallsToDisplay(Vector currentCalls, Object newlyAnsweredCall) {
      if (newlyAnsweredCall != null) {
         if (currentCalls != null) {
            int size = currentCalls.size();
            switch (size) {
               case 0:
                  return newlyAnsweredCall;
               default:
                  return currentCalls;
            }
         } else {
            return newlyAnsweredCall;
         }
      } else {
         return currentCalls;
      }
   }

   void updateCalls(Vector _1) {
      throw null;
   }

   void setIncomingCall(LiveCall call) {
   }

   void onCallInitiated() {
      throw null;
   }

   void updateNumber() {
      throw null;
   }

   @Override
   public int inputModeChanged(int mode) {
      this._inputMode = mode;
      this.updateDtmfCursor();
      return 0;
   }

   @Override
   protected void onVisibilityChange(boolean visible) {
      InputContext inputContext = InputContext.getInstance();
      if (inputContext != null) {
         if (visible) {
            if (inputContext.setListener(this) == 3) {
               this.inputModeChanged(0);
            }
         } else {
            if (inputContext.getListener() == this) {
               inputContext.setListener(null);
            }

            this.inputModeChanged(0);
            this._keypadLetterMode = false;
         }
      }

      super.onVisibilityChange(visible);
   }

   @Override
   protected void sublayout(int _1, int _2) {
      throw null;
   }

   private void updateDtmfCursor() {
      DTMFEchoField dtmfEchoField = this.getDTMFEchoField();
      if (dtmfEchoField != null) {
         LiveCall call = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
         Image image;
         if (call != null && !call.acceptingDTMF()) {
            image = null;
         } else if (this._inputMode != 2 && !this._keypadLetterMode) {
            image = null;
         } else {
            image = PhoneNumberInput.getReturnKeyImage(2);
         }

         dtmfEchoField.setCursor(image);
      }
   }

   public ActivePhoneUI(UiApplication app, Screen screen) {
      super(0);
      this.setTag(Tag.create("client"));
      this.setId("activecall");
      this._app = app;
      this._ownerScreen = screen;
   }
}
