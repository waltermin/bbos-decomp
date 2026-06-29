package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.TTY;
import net.rim.vm.Array;

public final class HACOption extends VoiceOptionsListItem {
   private ObjectChoiceField _hacChoiceField;
   private String[] _HACoptionsArray = new String[1];
   private int _previousChoice;
   static final int OFF = 0;
   static final int TELECOIL = 1;

   HACOption(Object context) {
      super(PhoneResources.getString(6250), context);
      this._HACoptionsArray[0] = CommonResources.getString(101);
      if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
         Array.resize(this._HACoptionsArray, this._HACoptionsArray.length + 1);
         this._HACoptionsArray[1] = PhoneResources.getString(6298);
      }
   }

   public static final boolean hasDisplayableFields() {
      return (Phone.getInstance().getNetworkFeatures() & 524288) != 0;
   }

   public static final void updateHACMode() {
      if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
         AudioInternal.requestHACModeChange(PhoneOptions.getOptions().getHACMode() != 0);
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      HACRibbonIndicator.getInstance().updateIndicator();
      int initialChoiceIndex = AudioInternal.getHACMode() ? 1 : 0;
      this._hacChoiceField = new ObjectChoiceField(PhoneResources.getString(6280), this._HACoptionsArray, initialChoiceIndex);
      this._previousChoice = initialChoiceIndex;
      screen.add(this._hacChoiceField);
   }

   @Override
   protected final boolean save() {
      int currentChoice = this._hacChoiceField.getSelectedIndex();
      if (this._previousChoice != currentChoice) {
         this._previousChoice = currentChoice;
         if (AudioInternal.requestHACModeChange(this._previousChoice != 0)) {
            HACRibbonIndicator.getInstance().responseHACModeChange(true, this._previousChoice != 0);
            if (TTY.getMode() != 3) {
               TTYRibbonIndicator.getInstance().responseTTYModeChange(TTY.requestModeChange(3), 3);
            }
         }
      }

      return super.save();
   }
}
