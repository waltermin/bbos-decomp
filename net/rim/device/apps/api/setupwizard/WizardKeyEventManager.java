package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.ribbon.ConvenienceKeyOptionsProvider;

public class WizardKeyEventManager {
   private WizardKeyEventManager() {
   }

   public static boolean processKeyEvent(int keyEvent, int keycode, int time, boolean warnOnSoftKeys) {
      return handleKeyEvent(keyEvent, keycode, warnOnSoftKeys);
   }

   private static boolean handleKeyEvent(int event, int keycode, boolean warnOnSoftKeys) {
      if (warnOnSoftKeys) {
         int resourceID;
         resourceID = -1;
         label31:
         switch (event) {
            case 512:
               break;
            case 513:
               switch (Keypad.key(keycode)) {
                  case 17:
                     break label31;
                  case 18:
                  default:
                     resourceID = 12;
                     break label31;
                  case 19:
                     if (!isConvenienceKeySet(Keypad.key(keycode))) {
                        return true;
                     }

                     resourceID = 17;
                     break label31;
               }
            case 514:
               return warnOnSoftKeys;
            case 515:
            default:
               switch (Keypad.key(keycode)) {
                  case 17:
                     resourceID = 11;
                     break;
                  case 21:
                     if (!isConvenienceKeySet(Keypad.key(keycode))) {
                        return true;
                     }

                     resourceID = 17;
               }
         }

         if (resourceID != -1 && promptUserForKeyUseExit(resourceID) == -1) {
            return true;
         }
      }

      return false;
   }

   private static boolean isConvenienceKeySet(int keycode) {
      ConvenienceKeyOptionsProvider convKeyProvider = ConvenienceKeyOptionsProvider.getInstance();
      String keyOwner = keycode == 19 ? convKeyProvider.getConvenienceKey1Owner() : convKeyProvider.getConvenienceKey2Owner();
      return keyOwner != null && !keyOwner.equals("null") && !keyOwner.equals("net_rim_application_menu");
   }

   public static int promptUserForKeyUseExit(int wizardResourceKeySpecificText) {
      return WizardExitDialog.createExitDialog(wizardResourceKeySpecificText).doModal();
   }

   private static int promptUserForRestart() {
      return WizardExitDialog.createExitDialog().doModal();
   }
}
