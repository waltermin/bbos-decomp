package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.apps.internal.profiles.Profiles;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.TTY;

public final class TTYOption extends VoiceOptionsListItem {
   private ObjectChoiceField _ttyChoiceField;
   private static final long TTY_CHANGE_ALLOWED_GUID;
   private static final int NO;
   private static final int YES;

   TTYOption(Object context) {
      super(PhoneResources.getString(6023), context);
   }

   static final void enableTTYOption() {
      ApplicationRegistry.getApplicationRegistry().put(8978995195591215860L, new Object());
   }

   public static final boolean hasDisplayableFields() {
      return (Phone.getInstance().getNetworkFeatures() & 65536) == 0 ? false : ApplicationRegistry.getApplicationRegistry().get(8978995195591215860L) != null;
   }

   public static final void updateTTYMode() {
      if ((Phone.getInstance().getNetworkFeatures() & 65536) != 0 && ApplicationRegistry.getApplicationRegistry().get(8978995195591215860L) == null) {
         TTY.requestModeChange(PhoneOptions.getOptions().getTTYMode());
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      int initialChoiceIndex = TTY.getMode() == 3 ? 0 : 1;
      TTYRibbonIndicator.getInstance().updateIndicator();
      this._ttyChoiceField = (ObjectChoiceField)(new Object(PhoneResources.getString(6024), CommonResources.getYesNoArray(1), initialChoiceIndex, 134217728));
      screen.add(this._ttyChoiceField);
   }

   @Override
   protected final boolean save() {
      if (this._ttyChoiceField.isDirty()) {
         int choiceIndex = this._ttyChoiceField.getSelectedIndex();
         int ttyMode = choiceIndex == 0 ? 3 : 0;
         if (TTY.requestModeChange(ttyMode)) {
            TTYRibbonIndicator.getInstance().responseTTYModeChange(true, ttyMode);
            if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0 && AudioInternal.getHACMode()) {
               HACRibbonIndicator.getInstance().responseHACModeChange(AudioInternal.requestHACModeChange(false), false);
            }

            if (ttyMode == 0) {
               Profiles profile = Profiles.getInstance();
               if (profile.getEnabled().getIdentifier() != 1) {
                  String message = PhoneResources.getString(6335);
                  Dialog dlg = (Dialog)(new Object(3, message, 0, Bitmap.getPredefinedBitmap(1), 0));
                  if (dlg.doModal() == 4) {
                     profile.enable(profile.getByIdentifier((byte)1));
                  }
               }
            }
         }
      }

      return super.save();
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 9000;
   }
}
