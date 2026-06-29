package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CallAlertingOption extends VoiceOptionsListItem {
   private ObjectChoiceField _privacyAlertField;
   private ObjectChoiceField _roamingAlertField;
   private static final String[] CHOICE_ARRAY = new Object[]{PhoneResources.getString(6017), PhoneResources.getString(6016)};
   private static final int NONE;
   private static final int TONE;

   public CallAlertingOption(Object context) {
      super(PhoneResources.getString(6014), context);
   }

   static final boolean hasDisplayableFields() {
      return PhoneUtilities.platformNotifiesOnPrivacyChanges() || PhoneUtilities.platformNotifiesOnRoamingChanges();
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      if (PhoneUtilities.platformNotifiesOnPrivacyChanges()) {
         int initialPrivacyIndex = ((PhoneOptions)super._phoneOptions).getBooleanOption(1024) ? 1 : 0;
         this._privacyAlertField = (ObjectChoiceField)(new Object(PhoneResources.getString(6015), CHOICE_ARRAY, initialPrivacyIndex));
         screen.add(this._privacyAlertField);
      }

      if (PhoneUtilities.platformNotifiesOnRoamingChanges()) {
         int initialRoamingIndex = ((PhoneOptions)super._phoneOptions).getBooleanOption(2048) ? 1 : 0;
         this._roamingAlertField = (ObjectChoiceField)(new Object(PhoneResources.getString(6018), CHOICE_ARRAY, initialRoamingIndex));
         screen.add(this._roamingAlertField);
      }
   }

   @Override
   protected final boolean save() {
      boolean commitRequired = false;
      if (this._privacyAlertField != null && this._privacyAlertField.isDirty()) {
         ((PhoneOptions)super._phoneOptions).setBooleanOption(1024, this._privacyAlertField.getSelectedIndex() == 1);
         commitRequired = true;
      }

      if (this._roamingAlertField != null && this._roamingAlertField.isDirty()) {
         ((PhoneOptions)super._phoneOptions).setBooleanOption(2048, this._roamingAlertField.getSelectedIndex() == 1);
         commitRequired = true;
      }

      if (commitRequired) {
         ((OptionsBase)super._phoneOptions).commit();
      }

      return super.save();
   }
}
