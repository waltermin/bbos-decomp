package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.VerticalSpacerField;
import net.rim.vm.Array;

final class CallBarringOption extends VoiceOptionsListItem implements QuerySSOptions$Listener, PhoneOptionsItem {
   private CallBarringOption$CallBarringProfile[] _incomingCallProfiles;
   private CallBarringOption$CallBarringProfile[] _outgoingCallProfiles;
   private SSOptionListField _incomingCallsListField;
   private SSOptionListField _outgoingCallsListField;
   private boolean _barIncomingAvailable;
   private boolean _barOutgoingAvailable;
   private boolean _ssTestingHackEnabled;
   private int[] _incomingStatusFlags = new int[2];
   private int[] _outgoingStatusFlags = new int[3];
   static final int ALL_INCOMING = 0;
   static final int INCOMING_ROAM = 1;
   static final int ALL_OUTGOING = 2;
   static final int OUTGOING_INTL = 3;
   static final int OUTGOING_INTL_PLMN = 4;
   static final int NUM_TYPES = 5;
   static final int INVALID_TYPE = -1;
   static final int NUM_INCOMING_TYPES = 2;
   static final int NUM_OUTGOING_TYPES = 3;
   static final int[] INCOMING_TYPES = new int[]{0, 1, -804651003, 0, 1, 2, 3, 4};
   static final int[] OUTGOING_TYPES = new int[]{2, 3, 4, -804651007, 3, -804651006, 3, 20, -804651006, 3, 28, -804651004};
   static final int[] ALL_TYPES = new int[]{0, 1, 2, 3, 4, -804651005, 1, 2, 3, -804651005, 2, 3, 4, -804651007, 3, -804651006, 3, 20, -804651006, 3};
   static final int[] SS_TYPES_MAP = new int[]{
      154,
      155,
      146,
      147,
      148,
      -804782058,
      21824438,
      21824888,
      21825288,
      65536000,
      21824438,
      21824888,
      21825288,
      65536000,
      21824438,
      21824888,
      21825288,
      -804782078,
      16384440,
      -804782074
   };
   static final int[] CSP_TYPES_MAP = new int[]{
      16, 8, 128, 64, 32, -804651004, 33, 41, 42, 43, 51, -804651005, 58, 17, 106, -804651007, 64, -804651007, 65, -804782078
   };
   public static final int BARRING_PASSWORD_MIN_LENGTH = 4;
   public static final int BARRING_PASSWORD_MAX_LENGTH = 4;
   public static final int INCOMING = 0;
   public static final int OUTGOING = 1;
   private static EnableSSOptionProfileVerb _enableProfileVerb = new EnableSSOptionProfileVerb();
   private static ChangePasswordVerb _changePasswordVerb = new ChangePasswordVerb();

   CallBarringOption(Object context) {
      super(PhoneResources.getString(182), context);
      this._ssTestingHackEnabled = PhoneUtilities.getDebugFlag(5709883646465653063L, false);
   }

   @Override
   public final void onQueryFinished(int[] ssOptionFlags, boolean success) {
      if (!success) {
         if (ssOptionFlags != null && ssOptionFlags.length > 0 && ssOptionFlags[0] == 100) {
            Application.getApplication().invokeLater(new CallBarringOption$1(this));
         }
      } else {
         this._incomingStatusFlags[0] = ssOptionFlags[0];
         this._incomingStatusFlags[1] = ssOptionFlags[1];
         this._barIncomingAvailable = isProvisioned(this._incomingStatusFlags[0]) || isProvisioned(this._incomingStatusFlags[1]);
         this._outgoingStatusFlags[0] = ssOptionFlags[2];
         this._outgoingStatusFlags[1] = ssOptionFlags[3];
         this._outgoingStatusFlags[2] = ssOptionFlags[4];
         this._barOutgoingAvailable = isProvisioned(this._outgoingStatusFlags[0])
            || isProvisioned(this._outgoingStatusFlags[1])
            || isProvisioned(this._outgoingStatusFlags[2]);
         if (!this._barIncomingAvailable && !this._barOutgoingAvailable) {
            Application.getApplication().invokeLater(new CallBarringOption$2(this));
         } else {
            Application.getApplication().invokeLater(new CallBarringOption$3(this));
         }
      }
   }

   @Override
   public final void onOpen() {
      if (RadioInfo.getState() != 1) {
         Dialog.alert(CommonResources.getString(9153) + ' ' + PhoneResources.getString(142));
      } else {
         this._barIncomingAvailable = false;
         this._barOutgoingAvailable = false;
         int[] barringTypes = ALL_TYPES;
         int[] ssTypes = SS_TYPES_MAP;
         int[] cspTypes = CSP_TYPES_MAP;
         new QuerySSOptions(barringTypes, ssTypes, 1, cspTypes, this).start();
      }
   }

   private static final boolean isProvisioned(int ssFlags) {
      return SSManager.isSSOptionProvisioned(ssFlags);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      int count = 0;
      if (this._barIncomingAvailable) {
         this._incomingCallProfiles = new CallBarringOption$CallBarringProfile[2];
         int barAllIncomingFlags = this._incomingStatusFlags[0];
         int barIncomingWhenRoamingFlags = this._incomingStatusFlags[1];
         if (isProvisioned(barAllIncomingFlags)) {
            this._incomingCallProfiles[count++] = new CallBarringOption$CallBarringProfile(this, PhoneResources.getString(210), 154, barAllIncomingFlags, true);
         }

         if (isProvisioned(barIncomingWhenRoamingFlags)) {
            this._incomingCallProfiles[count++] = new CallBarringOption$CallBarringProfile(
               this, PhoneResources.getString(211), 155, barIncomingWhenRoamingFlags, true
            );
         }

         Array.resize(this._incomingCallProfiles, count);
         this._incomingCallsListField = new SSOptionListField(this._incomingCallProfiles);
      }

      count = 0;
      if (this._barOutgoingAvailable) {
         this._outgoingCallProfiles = new CallBarringOption$CallBarringProfile[3];
         int barAllOutgoing = this._outgoingStatusFlags[0];
         int barOutgoingIntl = this._outgoingStatusFlags[1];
         int barOutgoingExHomePLMN = this._outgoingStatusFlags[2];
         if (isProvisioned(barAllOutgoing)) {
            this._outgoingCallProfiles[count++] = new CallBarringOption$CallBarringProfile(this, PhoneResources.getString(212), 146, barAllOutgoing, false);
         }

         if (isProvisioned(barOutgoingIntl)) {
            this._outgoingCallProfiles[count++] = new CallBarringOption$CallBarringProfile(this, PhoneResources.getString(213), 147, barOutgoingIntl, false);
         }

         if (isProvisioned(barOutgoingExHomePLMN)) {
            this._outgoingCallProfiles[count++] = new CallBarringOption$CallBarringProfile(
               this, PhoneResources.getString(214), 148, barOutgoingExHomePLMN, false
            );
         }

         Array.resize(this._outgoingCallProfiles, count);
         this._outgoingCallsListField = new SSOptionListField(this._outgoingCallProfiles);
      }

      if (this._barIncomingAvailable) {
         screen.add(new LabelField(PhoneResources.getString(203)));
         screen.add(new SeparatorField());
         screen.add(this._incomingCallsListField);
         screen.add(new VerticalSpacerField(4));
         screen.add(new SeparatorField());
      }

      if (this._barOutgoingAvailable) {
         screen.add(new LabelField(PhoneResources.getString(204)));
         screen.add(new SeparatorField());
         screen.add(this._outgoingCallsListField);
      }
   }

   private final void refresh() {
      if (this._incomingCallProfiles != null) {
         for (int i = 0; i < this._incomingCallProfiles.length; i++) {
            this._incomingCallProfiles[i]._statusFlags = this._incomingStatusFlags[i];
         }
      }

      if (this._outgoingCallProfiles != null) {
         for (int i = 0; i < this._outgoingCallProfiles.length; i++) {
            this._outgoingCallProfiles[i]._statusFlags = this._outgoingStatusFlags[i];
         }
      }

      super._mainScreen.invalidate();
   }

   @Override
   protected final Verb getSaveVerb() {
      return null;
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      verbToMenu.addVerb(_changePasswordVerb);
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu menuVerbs, int instance) {
      SSOptionProfile profile = this.getSelectedProfile();
      if (this._ssTestingHackEnabled) {
         int flags = 6;
         EnableSSOptionProfileVerb enableVerb = new EnableSSOptionProfileVerb(flags);
         enableVerb.setProfile(profile);
         menuVerbs.addVerb(enableVerb);
         int var7 = 10;
         EnableSSOptionProfileVerb disableVerb = new EnableSSOptionProfileVerb(var7);
         disableVerb.setProfile(profile);
         menuVerbs.addVerb(disableVerb);
         return enableVerb;
      }

      if (profile != null) {
         menuVerbs.addVerb(_enableProfileVerb.setProfile(profile));
      }

      return _enableProfileVerb;
   }

   private final SSOptionProfile getSelectedProfile() {
      Field focusedField = super._mainScreen.getLeafFieldWithFocus();
      if (focusedField == this._incomingCallsListField) {
         return this._incomingCallProfiles[this._incomingCallsListField.getSelectedIndex()];
      } else {
         return focusedField == this._outgoingCallsListField ? this._outgoingCallProfiles[this._outgoingCallsListField.getSelectedIndex()] : null;
      }
   }

   private final void closeScreen() {
      super._mainScreen.close();
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      boolean result = super.keyChar(key, time, status);
      if (!result && key == ' ' && !this._ssTestingHackEnabled) {
         SSOptionProfile profile = this.getSelectedProfile();
         if (profile != null) {
            _enableProfileVerb.setProfile(profile).invoke(null);
         }

         result = true;
      }

      return result;
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 5000;
   }

   static final void access$000(CallBarringOption x0) {
      x0.open();
   }
}
