package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CallLogsOption extends VoiceOptionsListItem {
   private RadioButtonGroup _rbGroup;
   private RadioButtonField _noneRb;
   private RadioButtonField _missedCallsRb;
   private RadioButtonField _allCallsRb;
   private RadioButtonField _allCallsNoDirectConnectRb;
   private int _originalSelectedIndex;
   private static final int MISSED_CALLS_RADIO_BTN_INDEX = 0;
   private static final int ALL_CALLS_RADIO_BTN_INDEX = 1;
   private static final int ALL_CALLS_NO_DC_RADIO_BTN_INDEX = 2;
   private static final int NONE_RADIO_BTN_INDEX_WITH_NO_DC_SUPPORT = 2;
   private static final int NONE_RADIO_BTN_INDEX_WITH_DC_SUPPORT = 3;

   public CallLogsOption(Object context) {
      super(PhoneResources.getString(187), context);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      screen.add((Field)(new Object(PhoneResources.getString(454), 36028797018963968L)));
      this._rbGroup = (RadioButtonGroup)(new Object());
      this._originalSelectedIndex = PhoneOptions.getOptions().getShowCallLogsOption();
      if (this._originalSelectedIndex == 3 && !DirectConnect.isSupported()) {
         this._originalSelectedIndex = 1;
      }

      this._missedCallsRb = (RadioButtonField)(new Object(PhoneResources.getString(6243), this._rbGroup, this._originalSelectedIndex == 0));
      this._allCallsRb = (RadioButtonField)(new Object(PhoneResources.getString(220), this._rbGroup, this._originalSelectedIndex == 1));
      if (DirectConnect.isSupported()) {
         this._allCallsNoDirectConnectRb = (RadioButtonField)(new Object(PhoneResources.getString(6260), this._rbGroup, this._originalSelectedIndex == 3));
      }

      this._noneRb = (RadioButtonField)(new Object(PhoneResources.getString(6017), this._rbGroup, this._originalSelectedIndex == 2));
      screen.add(this._missedCallsRb);
      screen.add(this._allCallsRb);
      if (this._allCallsNoDirectConnectRb != null) {
         screen.add(this._allCallsNoDirectConnectRb);
      }

      screen.add(this._noneRb);
   }

   @Override
   protected final boolean save() {
      if (super._mainScreen.isDirty()) {
         int index = this._rbGroup.getSelectedIndex();
         if (!DirectConnect.isSupported()) {
            ((PhoneOptions)super._phoneOptions).setShowCallLogsOption(index);
         } else {
            switch (index) {
               case -1:
                  break;
               case 0:
               case 1:
               default:
                  ((PhoneOptions)super._phoneOptions).setShowCallLogsOption(index);
                  break;
               case 2:
                  ((PhoneOptions)super._phoneOptions).setShowCallLogsOption(3);
                  break;
               case 3:
                  ((PhoneOptions)super._phoneOptions).setShowCallLogsOption(2);
            }
         }
      }

      return super.save();
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 4000;
   }
}
