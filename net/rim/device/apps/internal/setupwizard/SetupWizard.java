package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.localeremoval.LocaleRemovalUtility;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.setupwizard.WizardDialog;
import net.rim.device.apps.api.setupwizard.WizardExitDialog;
import net.rim.device.apps.api.setupwizard.WizardIdleHelper;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.setupwizard.logging.LogManagerImpl;
import net.rim.vm.DebugSupport;

public final class SetupWizard extends UiApplication implements DialogClosedListener {
   private Dialog _welcomeDialog;
   private Dialog _exitDialog;
   private Dialog _langRemovalDialog;
   private Dialog _lowMemoryDialog;
   private static final long LAUNCHED_FROM_DIALOG_KEY = 655214998864757855L;
   private static final long GUID_WIZARD_SCHEDULED_RUN_FAILED = 4002776916212129330L;
   public static final int MODE_NORMAL = 0;
   public static final int MODE_AUTORUN = 1;
   public static final int MODE_DIALOG = 2;
   static final long DIALOG_KEY = 6098263789715893904L;
   private static final int REMINDER_INTERVAL_MS = 86400000;
   private static final int REMINDER_IDLE_SECONDS = 120;
   private static int _mode = 0;
   private static final String SHOW_RESTART_DIALOG = "show-restart-dialog";

   public static final void main(String[] args) {
      if (args != null && args.length > 0) {
         if (!"init".equals(args[0])) {
            if (!"offer-wizard".equals(args[0])) {
               if ("exit-dialog".equals(args[0])) {
                  offerExitDialog();
                  return;
               }

               if ("low-memory".equals(args[0])) {
                  offerLowMemoryDialogWhenIdle();
                  return;
               }

               if ("offer-low-memory".equals(args[0])) {
                  offerLowMemoryDialog();
                  return;
               }

               if ("Wizard-Restart-Prompt".equals(args[0])) {
                  offerLanguageRemovalDialogWhenIdle();
                  return;
               }

               if ("show-restart-dialog".equals(args[0])) {
                  offerRestartDialog();
                  return;
               }

               new SetupWizard().enterEventDispatcher();
               return;
            }

            offerSetupWizard(args.length > 1 && "check-idle".equals(args[1]));
            return;
         }

         SetupWizardOptions.getOptions().enableSynchronization();
         if (!RibbonLauncher.getInstance().isFirstBoot()) {
            offerSetupWizard(false);
            return;
         }
      } else {
         SetupWizard app = new SetupWizard();
         app.enterEventDispatcher();
      }
   }

   private static final void offerSetupWizard(boolean checkIdle) {
      if (!SetupWizardOptions.getOptions().getSetupWizardDesired() || DebugSupport.getenv("NoTutorial") != null) {
         clearPowerUpListener();
      } else if (canShowDialog()) {
         if (checkIdle) {
            WizardIdleHelper.startApplicationWhenIdle(
               new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"offer-wizard"})
            );
         } else {
            _mode = 2;
            SetupWizard app = new SetupWizard();
            if (app.showWelcomeDialog()) {
               app.enterEventDispatcher();
            }
         }
      }

      System.exit(0);
   }

   private static final void offerLanguageRemovalDialogWhenIdle() {
      ApplicationDescriptor descriptor = new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"show-restart-dialog"});
      WizardIdleHelper.startApplicationWhenIdle(descriptor);
   }

   private static final void offerRestartDialog() {
      _mode = 2;
      SetupWizard app = new SetupWizard();
      app.showLanguageRemovalDialog();
      app.enterEventDispatcher();
      System.exit(0);
   }

   private static final void offerExitDialog() {
      _mode = 2;
      SetupWizard app = new SetupWizard();
      app.showExitDialog();
      app.enterEventDispatcher();
      System.exit(0);
   }

   private static final void offerLowMemoryDialogWhenIdle() {
      ApplicationDescriptor desc = new ApplicationDescriptor(ApplicationDescriptor.currentApplicationDescriptor(), new String[]{"offer-low-memory"});
      WizardIdleHelper.startApplication(desc);
   }

   private static final void offerLowMemoryDialog() {
      _mode = 2;
      SetupWizard app = new SetupWizard();
      app.showLowMemoryDialog();
      app.enterEventDispatcher();
      System.exit(0);
   }

   SetupWizard() {
      if (_mode == 0) {
         Object launchedFromDialog = ApplicationRegistry.getApplicationRegistry().remove(655214998864757855L);
         if (launchedFromDialog != null) {
            _mode = 1;
         }
      }

      if (_mode != 2) {
         this.launchWizard();
      }
   }

   protected final void launchWizard() {
      this.invokeLater(new SetupWizard$1(this));
   }

   private final boolean showWelcomeDialog() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         appRegistry.put(6098263789715893904L, new Object());
      }

      this._welcomeDialog = createWelcomeDialog();
      this.showDialog(this._welcomeDialog);
      return true;
   }

   private static final boolean canShowDialog() {
      if (cdmaActivationRequired()) {
         scheduleReminder(true);
         return false;
      }

      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         return appRegistry.get(6098263789715893904L) == null;
      }
   }

   private final void showExitDialog() {
      this._exitDialog = createExitDialog();
      this.showDialog(this._exitDialog);
   }

   private final void showLanguageRemovalDialog() {
      this._langRemovalDialog = WizardExitDialog.createExitDialog(true);
      WizardExitDialog.clearRestartFlag();
      this.showDialog(this._langRemovalDialog);
   }

   private final void showLowMemoryDialog() {
      this._lowMemoryDialog = this.createLowMemoryDialog();
      this.showDialog(this._lowMemoryDialog);
   }

   private final Dialog createLowMemoryDialog() {
      Dialog dialog = new Dialog(null, null, null, 0, null, 1152921504606846976L);
      dialog.setEscapeEnabled(false);
      dialog.setGateInput(true);
      RichTextField textField = dialog.getLabel();
      if (textField != null) {
         Manager textManager = textField.getManager();
         if (textManager != null) {
            textManager.delete(textField);
         }
      }

      textField = WizardDialog.createDialogRichTextField(SetupWizardResources.getString(28), true, 18014398509481984L);
      textField.setEditable(false);
      dialog.add(textField);
      DialogFieldManager dialogManager = (DialogFieldManager)dialog.getDelegate();
      ButtonField button = WizardDialog.createDialogButtonField(SetupWizardResources.getString(7));
      button.setChangeListener(dialog);
      dialogManager.addCustomField(button);
      button = WizardDialog.createDialogButtonField(SetupWizardResources.getString(9));
      button.setChangeListener(dialog);
      dialogManager.addCustomField(button);
      textField.setFocus();
      return dialog;
   }

   private final void showDialog(Dialog dialog) {
      dialog.setDialogClosedListener(this);
      Ui.getUiEngine().pushGlobalScreen(dialog, 100, 2);
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      boolean canExit = false;
      if (dialog == this._exitDialog) {
         canExit = true;
      } else if (dialog == this._langRemovalDialog) {
         switch (choice) {
            case -1:
               canExit = true;
               this.welcomeDialogRunHelper(false);
               break;
            case 4:
               this.dismissReminder();
               this.showExitDialog();
         }
      } else if (dialog == this._lowMemoryDialog) {
         canExit = true;
         if (choice == 1) {
            this.welcomeDialogRunHelper(false);
         }
      } else if (dialog == this._welcomeDialog) {
         if (choice != 2 && choice != 3) {
            canExit = false;
            if ((LocaleRemovalUtility.getMultiLanguageBuildType(false) & 1) != 0) {
               this.showLanguageRemovalDialog();
            } else {
               this.dismissReminder();
               this.showExitDialog();
            }
         } else {
            canExit = true;
            if (choice == 2) {
               this.welcomeDialogRunHelper(false);
            } else {
               this.welcomeDialogRunHelper(true);
               scheduleReminder(false);
            }
         }
      }

      if (canExit) {
         System.exit(0);
      }
   }

   protected static final Dialog createWelcomeDialog() {
      Dialog dialog = new Dialog("", null, null, 0, null, 1152921504606846976L);
      int fontSize = Ui.convertSize(7, 3, 0);
      Font normalFont = Font.getDefault().derive(0, fontSize);
      Font boldFont = normalFont.derive(1, fontSize);
      dialog.setEscapeEnabled(true);
      DialogFieldManager dialogManager = (DialogFieldManager)dialog.getDelegate();
      dialogManager.setMessage(WizardDialog.createDialogRichTextField(SetupWizardResources.getString(2), true, 36028797018963968L));
      int normalTextResId;
      int boldTextResId;
      if (Trackball.isSupported()) {
         normalTextResId = 26;
         boldTextResId = 27;
      } else {
         normalTextResId = 3;
         boldTextResId = 4;
      }

      String boldText = SetupWizardResources.getString(boldTextResId);
      String messageText = SetupWizardResources.getString(normalTextResId);
      RichTextField message = createFormattedRichTextField(messageText, boldText, normalFont, boldFont);
      message.setBorder(0, 0, 5, 0);
      dialogManager.addCustomField(message);
      byte var11;
      byte var12;
      if (Trackball.isSupported()) {
         var11 = 25;
         var12 = 24;
      } else {
         var11 = 5;
         var12 = 6;
      }

      boldText = SetupWizardResources.getString(var12);
      messageText = SetupWizardResources.getString(var11);
      message = createFormattedRichTextField(messageText, boldText, normalFont, boldFont);
      message.setBorder(0, 0, 5, 0);
      dialogManager.addCustomField(message);
      ButtonField button = WizardDialog.createDialogButtonField(SetupWizardResources.getString(7));
      button.setChangeListener(dialog);
      dialogManager.addCustomField(button);
      button = WizardDialog.createDialogButtonField(SetupWizardResources.getString(8));
      button.setChangeListener(dialog);
      dialogManager.addCustomField(button);
      button = WizardDialog.createDialogButtonField(SetupWizardResources.getString(9));
      button.setChangeListener(dialog);
      dialogManager.addCustomField(button);
      return dialog;
   }

   static final Dialog createExitDialog() {
      Dialog dialog = new Dialog("", null, null, 0, null, 1152921504606846976L);
      DialogFieldManager manager = (DialogFieldManager)dialog.getDelegate();
      manager.setMessage(WizardDialog.createDialogRichTextField(SetupWizardResources.getString(10), true, 36028797018963968L));
      ButtonField button = WizardDialog.createDialogButtonField(CommonResources.getString(117));
      button.setChangeListener(dialog);
      manager.addCustomField(button);
      return dialog;
   }

   static final void scheduleReminder(boolean useCdmaRadioListener) {
      ScheduledAppLauncher scheduledAppLauncher = null;
      ApplicationDescriptor currentApp = ApplicationDescriptor.currentApplicationDescriptor();
      ScheduledAppLauncher.register(new ApplicationDescriptor(currentApp, currentApp.getName(), new String[]{"offer-wizard"}), useCdmaRadioListener);
      if (!useCdmaRadioListener) {
         ApplicationManager.getApplicationManager()
            .scheduleApplication(
               new ApplicationDescriptor(currentApp, currentApp.getName(), new String[]{"offer-wizard", "check-idle"}),
               System.currentTimeMillis() + 86400000,
               true
            );
      }
   }

   private final void dismissReminder() {
      clearPowerUpListener();
      SetupWizardOptions options = SetupWizardOptions.getOptions();
      if (options.getSetupWizardDesired()) {
         options.setSetupWizardDesired(false);
         options.commit();
      }
   }

   private static final void clearPowerUpListener() {
      ScheduledAppLauncher.unregister();
   }

   @Override
   protected final boolean acceptsForeground() {
      return _mode != 2;
   }

   private final LogManagerImpl startLog() {
      return SetupWizardOptions.getOptions().getLoggingEnabled() ? new LogManagerImpl() : null;
   }

   private final void stopLog(LogManagerImpl logManager) {
      if (logManager != null) {
         logManager.getCategory("SetupWizard").log("SHUTDOWN");

         try {
            logManager.save();
         } finally {
            return;
         }
      }
   }

   private final void welcomeDialogRunHelper(boolean clearOnly) {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      synchronized (appRegistry) {
         appRegistry.remove(6098263789715893904L);
      }

      if (!clearOnly) {
         ApplicationRegistry.getApplicationRegistry().put(655214998864757855L, new Object());
         launchApplication(null);
      }
   }

   static final void launchApplication(String[] params) {
      try {
         ApplicationDescriptor currentApp = ApplicationDescriptor.currentApplicationDescriptor();
         ApplicationDescriptor wizardApp = new ApplicationDescriptor(currentApp, currentApp.getName(), params);
         ApplicationManager.getApplicationManager().runApplication(wizardApp, true);
      } finally {
         return;
      }
   }

   public static final RichTextField createFormattedRichTextField(String message, String boldText, Font normalFont, Font boldFont) {
      int boldStart = message.indexOf("{0}");
      int boldEnd = boldStart + boldText.length();
      String fullText = MessageFormat.format(message, new String[]{boldText});
      int fullTextLength = fullText.length();
      Font[] fonts = new Font[]{normalFont, boldFont};
      int[] offsets;
      byte[] attributes;
      if (boldStart == 0 && boldEnd == fullTextLength) {
         offsets = new int[]{0, fullTextLength};
         attributes = new byte[]{1};
      } else if (boldStart > 0) {
         if (boldEnd == fullTextLength) {
            offsets = new int[]{0, boldStart, boldEnd};
            attributes = new byte[]{0, 1};
         } else {
            offsets = new int[]{0, boldStart, boldEnd, fullTextLength};
            attributes = new byte[]{0, 1, 0};
         }
      } else if (boldStart == 0) {
         offsets = new int[]{0, boldEnd, fullTextLength};
         attributes = new byte[]{1, 0};
      } else {
         offsets = new int[]{0, fullTextLength};
         attributes = new byte[]{0};
      }

      return new RichTextField(fullText, offsets, attributes, fonts, 36028797018963968L);
   }

   static final Bitmap getAppIcon() {
      ApplicationDescriptor app = ApplicationDescriptor.currentApplicationDescriptor();
      String moduleName = app.getModuleName();
      Bitmap appIcon = null;
      EncodedImage image = ThemeManager.getActiveTheme().getImage(moduleName + "." + app.getName(), true);
      if (image != null) {
         appIcon = image.getBitmap();
      }

      if (appIcon == null) {
         appIcon = Bitmap.getBitmapResource(moduleName, "SetupWizardIcon.png");
      }

      return appIcon;
   }

   static final boolean cdmaActivationRequired() {
      if (RadioInfo.getNetworkType() == 4) {
         String deviceNumber = null;

         label104:
         try {
            deviceNumber = Phone.getInstance().getNumber(0);
         } finally {
            break label104;
         }

         if (deviceNumber != null) {
            int mdnLength = deviceNumber.length();
            if (mdnLength != 10) {
               return false;
            }

            for (int i = 0; i < 6; i++) {
               char c = deviceNumber.charAt(i);
               if (c != '0') {
                  return false;
               }
            }

            int mdn = 0;

            label93:
            try {
               mdn = Integer.valueOf(deviceNumber.substring(6));
            } finally {
               break label93;
            }

            int esn = CDMAInfo.getESN();
            if (mdn == esn % 10000) {
               return true;
            }

            return false;
         }
      }

      return false;
   }
}
