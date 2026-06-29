package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.AudioInternal;

public class EnhancedAcousticsOption extends VoiceOptionsListItem {
   private BooleanChoiceField _avcChoiceField;

   public EnhancedAcousticsOption(Object context) {
      super(PhoneResources.getString(6068), context);
   }

   public static boolean hasDisplayableFields() {
      return AudioInternal.getAVCMode() != 0;
   }

   @Override
   protected void populateMainScreen(MainScreen screen) {
      this._avcChoiceField = (BooleanChoiceField)(new Object(PhoneResources.getString(6069), 2, AudioInternal.getAVCMode() == 2));
      screen.add(this._avcChoiceField);
   }

   @Override
   protected boolean save() {
      AudioInternal.requestAVCModeChange(this._avcChoiceField.isAffirmative() ? 2 : 1);
      PhoneOptions.getOptions().commit();
      return super.save();
   }
}
