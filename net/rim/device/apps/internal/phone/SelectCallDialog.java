package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.api.ui.BaseVoiceAppPopupDialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class SelectCallDialog extends BaseVoiceAppPopupDialog {
   private ConferenceCall _conferenceCall;
   private LiveCall _selectedCall;
   private MemberCallsListField _memberCallsListField;
   private ManageConferenceVerb _owner;

   SelectCallDialog(ConferenceCall conferenceCall, ManageConferenceVerb owner) {
      super('\u0000', 2, true, null, null);
      this._conferenceCall = conferenceCall;
      this._owner = owner;
      this.add((Field)(new Object(PhoneResources.getString(114))));
      this.add((Field)(new Object()));
      this._memberCallsListField = new MemberCallsListField(this._conferenceCall.getMembers());
      this.add(this._memberCallsListField);
   }

   @Override
   protected final void addFields() {
   }

   @Override
   public final void close(int reason) {
      super.close(reason);
      if (reason == 0) {
         this._owner.onCallSelected(this._selectedCall);
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
            this.handleCallSelection();
            return true;
         default:
            return false;
      }
   }

   private final LiveCall getSelectedCall() {
      int index = this._memberCallsListField.getSelectedIndex();
      return index >= 0 && index < this._conferenceCall.memberCount() ? this._conferenceCall.getMember(index) : null;
   }

   private final void handleCallSelection() {
      this._selectedCall = this.getSelectedCall();
      this.close(0);
   }

   @Override
   public final boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      if (Keypad.key(keycode) == 18) {
         this.close(-1);
         return false;
      } else {
         return super.dispatchKeyEvent(event, key, keycode, time);
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char keyChar = Keypad.map(keycode);
      if (keyChar == '\n') {
         this.handleCallSelection();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }

   @Override
   protected final void onEvent(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1008:
            if (this._conferenceCall.memberCount() < 2) {
               this.close(-1);
               return;
            }

            this._memberCallsListField.updateList(this._conferenceCall.getMembers());
            UiApplication.getUiApplication().relayout();
            return;
         default:
            super.onEvent(eventId, callId, context);
      }
   }
}
