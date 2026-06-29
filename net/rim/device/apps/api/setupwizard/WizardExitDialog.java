package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.apps.api.localeremoval.LocaleRemovalUtility;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.ui.component.ProgressDialog;

public class WizardExitDialog extends Dialog {
   ProgressDialog _progressDialog;
   boolean _deleting = false;
   public static final int RESTART_NOW;
   public static final int RESTART_LATER;
   public static final String WIZARD_RESTART_PROMPT;
   private static final long WIZARD_RESCHEDULE_RESTART_IN_MS;
   private static final long WIZARD_RESTART_SCHEDULED;

   public static WizardExitDialog createExitDialog(boolean restartOnly) {
      return createExitDialog(-1, restartOnly);
   }

   public static WizardExitDialog createExitDialog(int wizardResourceKeySpecificText) {
      return createExitDialog(wizardResourceKeySpecificText, false);
   }

   public static WizardExitDialog createExitDialog() {
      return createExitDialog(-1, false);
   }

   private static WizardExitDialog createExitDialog(int wizardResourceKeySpecificText, boolean globalRestartOnly) {
      StringBuffer buffer = (StringBuffer)(new Object());
      if (wizardResourceKeySpecificText >= 0) {
         buffer.append(((StringBuffer)(new Object())).append(SetupWizardAPIResources.getString(wizardResourceKeySpecificText)).append(" ").toString());
      }

      ButtonField[] buttons = new Object[3];
      int[] values = new int[2];
      if ((LocaleRemovalUtility.getMultiLanguageBuildType(false) & 1) != 0 && !LocaleRemovalUtility.isLocaleRemovalComplete()) {
         buffer.append(((StringBuffer)(new Object())).append(SetupWizardAPIResources.getString(19)).append(" ").toString());
      }

      buffer.append(SetupWizardAPIResources.getString(13));
      buttons[0] = WizardDialog.createDialogButtonField(CommonResources.getString(101));
      buttons[1] = WizardDialog.createDialogButtonField(CommonResources.getString(100));
      values[0] = -1;
      values[1] = 4;
      WizardExitDialog dialog = new WizardExitDialog(values);
      DialogFieldManager dialogManager = (DialogFieldManager)dialog.getDelegate();
      dialogManager.setMessage(WizardDialog.createDialogRichTextField(buffer.toString(), true, 36028797019226112L));

      for (int i = 0; i <= buttons.length - 1; i++) {
         if (buttons[i] != null) {
            buttons[i].setChangeListener(dialog);
            dialogManager.addCustomField(buttons[i]);
         }
      }

      return dialog;
   }

   private WizardExitDialog(int[] values) {
      super("", null, values, 0, null, 1152921504606846976L);
   }

   @Override
   protected void select() {
      super.select();
      int selectedValue = this.getSelectedValue();
      switch (selectedValue) {
         case 101:
            scheduledRestart();
      }
   }

   public static void scheduledRestart() {
      if (ApplicationRegistry.getApplicationRegistry().get(-2647672557108466860L) == null) {
         ApplicationRegistry.getApplicationRegistry().put(-2647672557108466860L, new Object());
         ApplicationDescriptor descriptor = (ApplicationDescriptor)(new Object(
            ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"Wizard-Restart-Prompt"}
         ));
         ApplicationManager.getApplicationManager().scheduleApplication(descriptor, System.currentTimeMillis() + 86400000, true);
      }
   }

   public static void clearRestartFlag() {
      ApplicationRegistry.getApplicationRegistry().remove(-2647672557108466860L);
   }

   @Override
   public int doModal() {
      int returnValue = super.doModal();
      return returnValue == 101 ? 4 : returnValue;
   }

   @Override
   public boolean keyDown(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 16:
         case 20:
            return super.keyDown(keycode, time);
         case 17:
         case 18:
         case 19:
         case 21:
         default:
            return true;
      }
   }

   @Override
   public boolean keyUp(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 16:
         case 20:
            return false;
         case 17:
         case 18:
         case 19:
         case 21:
         default:
            return true;
      }
   }
}
