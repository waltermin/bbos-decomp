package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CallWaitingOption extends VoiceOptionsListItem implements PhoneOptionsItem, QuerySSOptions$Listener {
   private int _callWaitingStatusFlags;
   private ObjectChoiceField _callWaitingEnabledChoiceField;
   private boolean _ssTestingHackEnabled;
   private int _originalSelectedIndex = -1;
   static final int NO = 0;
   static final int YES = 1;

   CallWaitingOption(Object context) {
      super(PhoneResources.getString(254), context);
      this._ssTestingHackEnabled = PhoneUtilities.getDebugFlag(5709883646465653063L, false);
   }

   @Override
   public final void onQueryFinished(int[] ssOptionFlags, boolean success) {
      if (!success) {
         if (ssOptionFlags != null && ssOptionFlags.length > 0 && ssOptionFlags[0] == 100) {
            Application.getApplication().invokeLater(new CallWaitingOption$1(this));
         }
      } else {
         this._callWaitingStatusFlags = ssOptionFlags[0];
         if (!this.isAvailable(this._callWaitingStatusFlags)) {
            Application.getApplication().invokeLater(new CallWaitingOption$2(this));
         } else {
            Application.getApplication().invokeLater(new CallWaitingOption$3(this));
         }
      }
   }

   private final boolean isAvailable(int flags) {
      return (flags & 1) != 0 || (flags & 2) != 0;
   }

   @Override
   public final void onOpen() {
      if (RadioInfo.getState() != 1) {
         Dialog.alert(CommonResources.getString(9153) + ' ' + PhoneResources.getString(255));
      } else if (this._ssTestingHackEnabled) {
         this.open();
      } else {
         QuerySSOptions query = new QuerySSOptions(null, new int[]{65, -804782078, 19661400, -804782066}, 3, new int[]{64, -804651007, 65, -804782078}, this);
         query.start();
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      super.populateMainScreen(screen);
      boolean enabled = (this._callWaitingStatusFlags & 2) != 0;
      this._callWaitingEnabledChoiceField = new ObjectChoiceField(PhoneResources.getString(257), CommonResources.getYesNoArray(1), enabled ? 1 : 0);
      this._originalSelectedIndex = this._callWaitingEnabledChoiceField.getSelectedIndex();
      screen.add(this._callWaitingEnabledChoiceField);
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      return this.changesMade() ? super.confirm(verb, context) : true;
   }

   private final boolean changesMade() {
      return PhoneUtilities.cdmaWAFActive() || this._callWaitingEnabledChoiceField.getSelectedIndex() != this._originalSelectedIndex;
   }

   @Override
   protected final boolean save() {
      if (this.changesMade()) {
         boolean enable = this._callWaitingEnabledChoiceField.getSelectedIndex() == 1;
         new CallWaitingOption$EnableCallWaiting(enable).start();
      }

      return super.save();
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 7000;
   }

   static final void access$000(CallWaitingOption x0) {
      x0.open();
   }
}
