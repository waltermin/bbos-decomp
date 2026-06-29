package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.ribbon.indicators.VoicemailIconManager;
import net.rim.device.apps.api.ui.ButtonContainer;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.ui.PhoneAwareScreen;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.VerticalSpacerField;

final class MissedCallPopup extends PopupDialog implements PhoneEventListener, GlobalEventListener {
   private int _missedCallCount;
   private RichTextField _missedCallField;
   private RichTextField _newVoicemailField;
   private ButtonField _closeButton;
   private ButtonField _viewMissedCallsButton;
   private int _speedDialKeycode;
   private MissedCallIndicator _mci;

   MissedCallPopup(MissedCallIndicator mci) {
      super(new VerticalFieldManager(1153220571769602048L), 33554432);
      this.setStatusPriority(-1073741814);
      this._mci = mci;
      this._missedCallCount = 1;
      this.add(this._missedCallField = new RichTextField(this.getMissedCallText(), 36028797018963968L));
      VoicemailIconManager vmailIconMgr = VoicemailIconManager.getInstance();
      if (vmailIconMgr.isIndicatorOn()) {
         int count = vmailIconMgr.getVoicemailCount();
         this._newVoicemailField = new RichTextField(this.getNewVoicemailText(count), 36028797018963968L);
         this.add(new VerticalSpacerField(4));
         this.add(this._newVoicemailField);
      }

      this.add(new VerticalSpacerField(8));
      ButtonContainer buttons = new ButtonContainer();
      String view = PhoneResources.getString(6256);
      this._viewMissedCallsButton = new ButtonField(view, 0);
      this._closeButton = PhoneUtilities.getCloseButton();
      buttons.addButton(this._viewMissedCallsButton);
      buttons.addButton(this._closeButton);
      this.add(buttons);
      VoiceServices.getUiApplication().addGlobalEventListener(this);
      VoiceServices.addPhoneEventListener(this);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      this._speedDialKeycode = keycode;
      switch (key) {
         case 16:
            return super.keyDown(keycode, time);
         case 17:
         default:
            this.close(0);
            return true;
         case 18:
            this.close(-1);
            return true;
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (QuickContactList.isValidQuickContactKey(this._speedDialKeycode)) {
         return true;
      }

      if (DeviceInfo.isSimulator() && Character.toLowerCase(key) == 'c') {
         VoicemailIconManager.getInstance();
         VoicemailIconManager.setState(0, 2);
      }

      if (key == 27) {
         this.close(-1);
         return true;
      } else if (key == '\n') {
         this.acceptInput();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean keyUp(int keycode, int time) {
      this._speedDialKeycode = 0;
      return super.keyUp(keycode, time);
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      if (keycode == this._speedDialKeycode) {
         this._speedDialKeycode = 0;
         QuickContactItem qci = QuickContactList.getInstance().getQuickContactItem(keycode);
         if (qci != null) {
            this.close(-1);
            qci.invoke();
            return true;
         }
      }

      return super.keyRepeat(keycode, time);
   }

   private final void acceptInput() {
      Field focus = this.getLeafFieldWithFocus();
      if (focus == this._closeButton) {
         this.close(-1);
      } else {
         if (focus == this._viewMissedCallsButton) {
            VoiceApplication voiceApp = VoiceServices.getVoiceApplication();
            if (voiceApp.inForeground()) {
               UiEngine ui = this.getUiEngine();

               for (Screen activeScreen = ui.getActiveScreen(); !(activeScreen instanceof PhoneAwareScreen); activeScreen = ui.getActiveScreen()) {
                  try {
                     ui.popScreen(activeScreen);
                  } finally {
                     continue;
                  }
               }

               this.close(0);
               return;
            }

            ContextObject context = new ContextObject();
            PhoneUtilities.setPrivateFlag(context, 72);
            VoiceServices.getVoiceApplication().requestForeground(null, context);
            voiceApp.requestForeground(null, context);
            this.close(0);
         }
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.acceptInput();
      return true;
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.acceptInput();
            return true;
         default:
            return false;
      }
   }

   private final String getMissedCallText() {
      return this._missedCallCount > 1
         ? MessageFormat.format(PhoneResources.getString(6252), new String[]{"" + this._missedCallCount})
         : PhoneResources.getString(6251);
   }

   private final String getNewVoicemailText(int count) {
      if (count > 0) {
         return count == 1 ? PhoneResources.getString(6255) : MessageFormat.format(PhoneResources.getString(6253), new String[]{"" + count});
      } else {
         return PhoneResources.getString(6254);
      }
   }

   @Override
   public final void close(int reason) {
      super.close(reason);
      if (this._mci != null) {
         this._mci.onPopupClosed();
      }

      VoiceServices.getUiApplication().removeGlobalEventListener(this);
      VoiceServices.removePhoneEventListener(this);
   }

   private final void updateMissedCallLabel() {
      this._missedCallField.setText(this.getMissedCallText());
   }

   final void onMissedCall() {
      this._missedCallCount++;
      this.updateMissedCallLabel();
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1110:
            this.close(-1);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6291453494459897456L) {
         VoicemailIconManager mgr = VoicemailIconManager.getInstance();
         if (mgr.isIndicatorOn()) {
            int count = mgr.getVoicemailCount();
            String text = this.getNewVoicemailText(count);
            if (this._newVoicemailField == null) {
               this._newVoicemailField = new RichTextField(text, 36028797018963968L);
               int index = this._missedCallField.getIndex();
               this.insert(this._newVoicemailField, index + 1);
               return;
            }

            this._newVoicemailField.setText(text);
            return;
         }

         if (this._newVoicemailField != null) {
            this.delete(this._newVoicemailField);
            this._newVoicemailField = null;
         }
      }
   }
}
