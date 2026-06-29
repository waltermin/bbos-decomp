package net.rim.device.apps.internal.security;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardSecurityListener;
import net.rim.device.api.system.SIMCardStatusListener;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.apps.api.idlescreen.IdleScreenManager;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ribbon.system.StandbyManager;
import net.rim.device.apps.api.ui.SIMCodeDialog;
import net.rim.device.apps.api.ui.SecurityDialog;
import net.rim.device.apps.internal.profiles.ProfileQuickToggle;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.SecurityCallHandler;
import net.rim.device.internal.system.SmartCardUserAuthenticatorFacade;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.UiSettings;
import net.rim.vm.Array;
import net.rim.vm.Message;
import net.rim.vm.Process;

public final class SecurityApp
   extends UiApplication
   implements StylusListener,
   KeyListener,
   SIMCardStatusListener,
   SIMCardSecurityListener,
   GlobalEventListener,
   RadioStatusListener,
   PhoneEventListener {
   private SecurityApp$MyDialog _promptDialog;
   private ResourceBundle _rb;
   private Security _security;
   private boolean _simUnlockInProgress;
   private boolean _simRequestInProgress;
   private boolean _unlockOnStartup;
   private boolean _postEventAfterPasswordAttempt;
   private SIMCodeDialog _simCodeDialog;
   private Field _bannerField;
   private boolean _inSecurityDialogCode;
   private boolean _processPolicySetting;
   private SecurityApp$OwnerInfoManager _owner;
   private SecurityCallHandler _callHandler = null;
   private boolean _hotkeyInit;
   private boolean _keyLockSequence;
   private Screen _lockedScreen;
   private Manager _lockedScreenLayout;
   private SecurityApp$CloseDialogOnIdleThread _closeThread;
   private Screen _securityStatusScreen;
   private boolean _turnBacklightOff;
   private static final long EVENT_LOGGER_GUID = -1148210079122251014L;
   private static final int KEYPAD_LOCKED = -1;
   private static final int UNLOCK_DEVICE = 0;
   private static final int PLACE_CALL = 1;
   private static final int MAKE_EMERGENCY_CALL = 2;
   private static final int CANCEL_LOCK_SCREEN = 3;
   private static final int PRESS_SEND_TO_UNLOCK = 4;
   private static final String EMPTY = "";
   private static final char NULLCHAR = '\u0000';
   private static String _lockArg = "lock";
   private static final int SECURITY_BACKLIGHT_TIMEOUT = 5;
   private static final int CHANGE_PASSWORD_TIMEOUT = 30;

   public static final void main(String[] args) {
      EventLogger.register(-1148210079122251014L, "SecurityApp", 2);
      if (args != null && args.length == 1 && args[0].equals(_lockArg)) {
         if (!isSIMLocked()) {
            IdleScreenManager idleScreenManager = IdleScreenManager.getInstance();
            if (idleScreenManager != null) {
               idleScreenManager.show();
            }
         }

         RIMGlobalMessagePoster.postGlobalEvent(-7131874474196788121L);
         if (PersistentContent.isEncryptionEnabled() && closeOpenGlobalScreens()) {
            closeOpenLocalScreens();
         }

         SecurityApp app = new SecurityApp();
         app.enableKeyUpEvents(true);
         app.enterEventDispatcher();
      } else {
         ApplicationDescriptor descriptor = ApplicationDescriptor.currentApplicationDescriptor();
         new SecurityManagerImpl((ApplicationDescriptor)(new Object(descriptor, new Object[]{_lockArg})));
      }
   }

   private static final boolean closeOpenGlobalScreens() {
      int currentScreenCount = Ui.getGlobalScreenCount();
      if (currentScreenCount == 0) {
         return true;
      }

      ApplicationManagerInternal appManagerInternal = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      char ch = 27;
      int keycode = Keypad.keycode(ch, 0);
      Message keyDown = (Message)(new Object(2, 513, ch, keycode, 0));
      Message keyUp = (Message)(new Object(2, 515, ch, keycode, 0));

      int previousScreenCount;
      do {
         previousScreenCount = currentScreenCount;
         Application foregroundApp = appManagerInternal.getForegroundApplication();
         if (foregroundApp != null) {
            int foregroundProcessId = foregroundApp.getProcessId();
            appManagerInternal.postMessage(foregroundProcessId, keyDown);
            appManagerInternal.postMessage(foregroundProcessId, keyUp);

            while (foregroundApp.isHandlingEvents() && foregroundApp.getMessageQueueSize() > 0 && foregroundApp.isAlive()) {
               Process.waitForIdle(250);
            }
         }

         currentScreenCount = Ui.getGlobalScreenCount();
         if (currentScreenCount == 0) {
            return true;
         }
      } while (currentScreenCount < previousScreenCount);

      return false;
   }

   private static final void closeOpenLocalScreens() {
      SecurityApp$OpenScreenCloser openScreenCloser = new SecurityApp$OpenScreenCloser(null);
      ApplicationManagerInternal appManagerInternal = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();

      for (ApplicationProcess process : appManagerInternal.getProcesses()) {
         if (process.acceptsForeground()) {
            Application app = process.getApplication();
            if (app instanceof Object && ((UiApplication)app).getScreenCount() > 0) {
               String appClassName = app.getClass().getName();
               if (!appClassName.equals("net.rim.device.apps.internal.ribbon.RibbonLauncherApp")) {
                  app.invokeLater(openScreenCloser);
               }
            }
         }
      }
   }

   private final Manager getLayout() {
      Theme theme = ThemeManager.getActiveTheme();
      if (theme == null) {
         return null;
      }

      ThemeAttributeSet attributes = theme.getAttributeSet(Tag.create("idle"));
      return attributes == null ? null : attributes.getLayout(null);
   }

   final Screen createScreen() {
      Screen screen = new SecurityApp$LockedScreen(this);
      this.createScreenFields(screen);
      return screen;
   }

   final void createScreenFields(Screen screen) {
      this._lockedScreenLayout = this.getLayout();
      if (this._lockedScreenLayout != null) {
         screen.add(this._lockedScreenLayout);
      } else {
         RibbonBanner ribbonBar = RibbonBanner.getInstance();
         if (ribbonBar != null) {
            this._bannerField = ribbonBar.getStatusBanner("", 1);
            screen.add(this._bannerField);
         }

         this._owner = new SecurityApp$OwnerInfoManager(this, null);
         screen.add(this._owner);
      }
   }

   private SecurityApp() {
      this._rb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
      this._security = Security.getInstance();
      this._lockedScreen = this.createScreen();
      this._lockedScreen.addKeyListener(this);
      this._lockedScreen.addStylusListener(this);
      VoiceServices.addPhoneEventListener(this);
      IdleScreenManager idleScreenManager = IdleScreenManager.getInstance();
      if (idleScreenManager != null) {
         idleScreenManager.hook(this);
      }

      this.pushScreen(this._lockedScreen);
      SIMCard.addListener(this, this);
      this.addGlobalEventListener(this);
      this.addRadioListener(this);
      this._securityStatusScreen = new SecurityApp$SecurityStatusScreen();
      this.pushGlobalScreen(this._securityStatusScreen, -1073741824, 2);
      this._callHandler = Security.getInstance().getCallHandler();
      if (this._callHandler == null) {
         this._callHandler = new SecurityApp$DefaultCallHandler(this);
      }

      if (this.lockedDeviceEmergencyCallSupported()) {
         this.addLockedKeyListener(new SecurityApp$LockedKeyboardKeyListener(this));
      }

      Backlight.setTimeout(5);
   }

   private final boolean lockedDeviceEmergencyCallSupported() {
      int vendorID = Branding.getVendorId();
      switch (vendorID) {
         case 1:
         case 102:
            return true;
         default:
            byte[] data = Branding.getData(21);
            return data != null && data.length > 0 && data[0] != 0;
      }
   }

   private final void shutdown() {
      if (!this._processPolicySetting) {
         if (!this._security.isPasswordEnabled()) {
            PersistentContentInternal.unlock(null);
         }

         Backlight.setTimeout(UiSettings.getBacklightTimeout());
         VoiceServices.removePhoneEventListener(this);
         this._security.incrementUnlockCounter();
         RIMGlobalMessagePoster.postGlobalEvent(6345609069135580235L);
         IdleScreenManager idleScreenManager = IdleScreenManager.getInstance();
         if (idleScreenManager != null) {
            idleScreenManager.unhook();
         }

         this.popScreen(this._securityStatusScreen);
         System.exit(0);
      }
   }

   @Override
   public final void activate() {
      super.activate();
      this.invokeLater(new SecurityApp$1(this));
   }

   private final void unlock() {
      this.unlock('\u0000', true, false, null);
   }

   private final void unlock(char key) {
      this.unlock(key, true, false, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void unlock(char key, boolean allowDismiss, boolean disableKeypadUnlockDialog, String prompt) {
      if (!isSIMLocked() || this.unblockAndUnlockSIM(key)) {
         if (!this._security.isPasswordEnabled() && this._keyLockSequence) {
            this.shutdown();
         } else if (!this._security.isPasswordEnabled() && !disableKeypadUnlockDialog) {
            this.displayPromptDialog(false, '\u0000');
         } else if (!this._inSecurityDialogCode && this._promptDialog == null) {
            if (!this._inSecurityDialogCode) {
               boolean var8 = false /* VF: Semaphore variable */;

               try {
                  var8 = true;
                  this._inSecurityDialogCode = true;
                  boolean result = SecurityDialog.challengeUser(prompt, false, allowDismiss, key, false);
                  if (this._postEventAfterPasswordAttempt) {
                     this._postEventAfterPasswordAttempt = false;
                     RIMGlobalMessagePoster.postGlobalEvent(-3502867315182341539L, result ? 1 : 0, 0);
                  }

                  if (result) {
                     this.shutdown();
                  }

                  Backlight.setTimeout(5);
                  var8 = false;
               } finally {
                  if (var8) {
                     this._inSecurityDialogCode = false;
                  }
               }

               this._inSecurityDialogCode = false;
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void displayPromptDialog(boolean forceEmergencyCallChoiceDialogInsteadOfKeypadUnlockPrompt, char key) {
      if (!this._processPolicySetting) {
         if (this._simCodeDialog == null && this._promptDialog == null && !this._inSecurityDialogCode) {
            boolean havePhone = this._callHandler.isEnabled();
            if (!this._security.isPasswordEnabled() || havePhone && !this.validateChar(key)) {
               int[] values = new int[0];
               String[] choices = new Object[0];
               if (!this._security.isPasswordEnabled() && Keypad.hasSendEndKeys() && !forceEmergencyCallChoiceDialogInsteadOfKeypadUnlockPrompt) {
                  Font font = Font.getDefault();
                  ActiveRichTextField field = this.getHintField(HintStringPattern.getHintStringPattern(), font);
                  field.setFont(font);
                  field.setText("", this._rb.getString(707), new Object[]{font, font}, null);
                  this._promptDialog = new SecurityApp$MyDialog(this, field, choices, values, -1, null, 0);
               } else {
                  int defaultChoice = this.getSecurityChoices(values, choices);
                  this._promptDialog = new SecurityApp$MyDialog(this, this._rb.getString(400), choices, values, defaultChoice, null, 0);
               }

               Image image = null;
               boolean var11 = false /* VF: Semaphore variable */;

               label86:
               try {
                  var11 = true;
                  image = ThemeManager.getThemeAwareImage("lock_screen_icon");
                  var11 = false;
               } finally {
                  if (var11) {
                     image = ThemeManager.getThemeAwareImage("net_rim_LockSystem");
                     break label86;
                  }
               }

               this._promptDialog.setIcon(image);
               this._promptDialog.setEscapeEnabled(true);
               synchronized (this) {
                  if (this._closeThread == null) {
                     this._closeThread = new SecurityApp$CloseDialogOnIdleThread(this, this);
                     this._closeThread.start();
                  }
               }

               int ret = this._promptDialog.doModal();
               this._promptDialog = null;
               switch (ret) {
                  case 0:
                  default:
                     this.unlock();
                     return;
                  case 1:
                     this._callHandler.placeCall();
                     return;
                  case 2:
                     this._callHandler.makeEmergencyCall();
                  case -1:
               }
            } else {
               this.unlock(key);
            }
         }
      }
   }

   private final int getSecurityChoices(int[] values, String[] choices) {
      int numChoices = 4;
      Array.resize(values, numChoices);
      Array.resize(choices, numChoices);
      int index = 0;
      values[index] = 0;
      choices[index++] = this.createButtonText(this._rb.getString(401));
      if (this._callHandler.outgoingCallSupported()) {
         values[index] = 1;
         choices[index++] = this.createButtonText(this._rb.getString(219));
      }

      if (this._callHandler.emergencyCallSupported()) {
         values[index] = 2;
         choices[index++] = this.createButtonText(this._rb.getString(402));
      }

      values[index] = 3;
      choices[index++] = this.createButtonText(this._rb.getString(706));
      Array.resize(values, index);
      Array.resize(choices, index);
      return 0;
   }

   private final String createButtonText(String buttonText) {
      return buttonText;
   }

   private final boolean validateChar(char key) {
      switch (key) {
         case '\u0000':
         case '\b':
         case '\n':
         case '\u007f':
         case '\u0095':
            return false;
         default:
            return true;
      }
   }

   private final String getSIMCode(char key, boolean puk, String prompt) {
      this._simCodeDialog = (SIMCodeDialog)(new Object(puk ? 3 : 1));
      if (puk) {
         if (Character.isDigit(key) || key == '*') {
            this._simCodeDialog.setText(((StringBuffer)(new Object(""))).append(key).toString());
         }
      } else if (key != 0) {
         char alted = Keypad.getAltedChar(key);
         if (alted != 0) {
            key = alted;
         }

         if (Character.isDigit(key)) {
            this._simCodeDialog.setText(((StringBuffer)(new Object(""))).append(key).toString());
         }
      }

      this._simCodeDialog.show(prompt);
      String code = this._simCodeDialog.getText();
      this._simCodeDialog = null;
      return code;
   }

   private final boolean unblockSIM(char key) {
      if (!SIMCard.isPUKRequired(1)) {
         return false;
      }

      String pin = null;
      String pinConfirmation = null;
      String puk = this.getSIMCode(
         key,
         true,
         MessageFormat.format(
            ((StringBuffer)(new Object())).append(this._rb.getString(217)).append(this._rb.getString(212)).toString(),
            new Object[]{new Object(SIMCard.getPUKRetriesRemaining(1))}
         )
      );
      if (puk != null && puk.length() != 0) {
         if (!puk.startsWith("**05*")) {
            while (true) {
               pin = this.getSIMCode('\u0000', false, this._rb.getString(205));
               if (pin == null || pin.length() == 0) {
                  return true;
               }

               pinConfirmation = this.getSIMCode('\u0000', false, this._rb.getString(207));
               if (pinConfirmation == null || pinConfirmation.length() == 0) {
                  return true;
               }

               if (pin.equals(pinConfirmation)) {
                  break;
               }

               Dialog.alert(this._rb.getString(209));
            }
         } else {
            String input = puk;
            input = input.substring(5);
            int i = input.indexOf(42);
            if (i == -1) {
               Dialog.alert(this._rb.getString(211));
               return true;
            }

            puk = input.substring(0, i);
            input = input.substring(i + 1);
            i = input.indexOf(42);
            if (i == -1) {
               Dialog.alert(this._rb.getString(211));
               return true;
            }

            pin = input.substring(0, i);
            input = input.substring(i + 1);
            if (!pin.equals(input)) {
               Dialog.alert(this._rb.getString(209));
               return true;
            }
         }

         return this.sendUnblockOrUnlockRequest(puk, pin);
      } else {
         return true;
      }
   }

   private final boolean unlockSIM(char key) {
      if (!SIMCard.isPINRequired(1)) {
         return false;
      }

      String pin = this.getSIMCode(
         key,
         false,
         MessageFormat.format(
            ((StringBuffer)(new Object())).append(this._rb.getString(214)).append(this._rb.getString(212)).toString(),
            new Object[]{new Object(SIMCard.getPINRetriesRemaining(1))}
         )
      );
      return pin != null && pin.length() != 0 ? this.sendUnblockOrUnlockRequest(null, pin) : true;
   }

   private final boolean sendUnblockOrUnlockRequest(String puk, String pin) {
      boolean validSIM = false;

      label37:
      try {
         validSIM = SIMCard.isValid();
      } finally {
         break label37;
      }

      if (!validSIM) {
         return false;
      }

      if (puk != null) {
         if (SIMCard.isPINRequired(1)) {
            SIMCard.sendPUK(puk.getBytes(), pin.getBytes());
         } else {
            SIMCard.requestChangePIN(1, puk.getBytes(), null, pin.getBytes());
         }
      } else {
         SIMCard.sendPIN(pin.getBytes());
      }

      this._simRequestInProgress = true;
      this.repaint();
      return true;
   }

   private final boolean unblockAndUnlockSIM(char param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 04: ifeq 09
      // 07: bipush 0
      // 08: ireturn
      // 09: aload 0
      // 0a: bipush 1
      // 0b: putfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 0e: aload 0
      // 0f: iload 1
      // 10: invokespecial net/rim/device/apps/internal/security/SecurityApp.unblockSIM (C)Z
      // 13: ifeq 1f
      // 16: bipush 0
      // 17: istore 2
      // 18: aload 0
      // 19: bipush 0
      // 1a: putfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 1d: iload 2
      // 1e: ireturn
      // 1f: aload 0
      // 20: iload 1
      // 21: invokespecial net/rim/device/apps/internal/security/SecurityApp.unlockSIM (C)Z
      // 24: ifeq 30
      // 27: bipush 0
      // 28: istore 2
      // 29: aload 0
      // 2a: bipush 0
      // 2b: putfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 2e: iload 2
      // 2f: ireturn
      // 30: aload 0
      // 31: bipush 0
      // 32: putfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 35: bipush 1
      // 36: ireturn
      // 37: astore 2
      // 38: aload 0
      // 39: bipush 0
      // 3a: putfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 3d: bipush 1
      // 3e: ireturn
      // 3f: astore 3
      // 40: aload 0
      // 41: bipush 0
      // 42: putfield net/rim/device/apps/internal/security/SecurityApp._simUnlockInProgress Z
      // 45: aload 3
      // 46: athrow
      // try (8 -> 14): 35 null
      // try (19 -> 25): 35 null
      // try (8 -> 14): 41 null
      // try (19 -> 25): 41 null
      // try (35 -> 36): 41 null
      // try (41 -> 42): 41 null
   }

   static final boolean isSIMLocked() {
      if (!SIMCard.isSupported()) {
         return false;
      }

      try {
         if (SIMCard.isPUKRequired(1) || SIMCard.isPINRequired(1)) {
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   private final void processSIMEvent(String statusMessage, boolean useStatus) {
      this.processSIMEvent(statusMessage, useStatus, 2);
   }

   private final void processSIMEvent(String statusMessage, boolean useStatus, int secondsForStatus) {
      if (this._simRequestInProgress) {
         this._simRequestInProgress = false;
         if (useStatus) {
            Status.show(statusMessage, Bitmap.getPredefinedBitmap(0), secondsForStatus * 1000, 0, false, true, 0);
            return;
         }

         Dialog.alert(statusMessage);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void processPolicySettings() {
      boolean securitySettingPassword = false;
      Boolean securitySettingPasswordBoolean = (Boolean)ApplicationRegistry.getApplicationRegistry().get(5862813030521710644L);
      if (securitySettingPasswordBoolean != null) {
         securitySettingPassword = securitySettingPasswordBoolean;
      }

      boolean securitySettingContentProtection = false;
      Boolean securitySettingContentProtectionBoolean = (Boolean)ApplicationRegistry.getApplicationRegistry().get(-746144713976755387L);
      if (securitySettingContentProtectionBoolean != null) {
         securitySettingContentProtection = securitySettingContentProtectionBoolean;
      }

      boolean securitySettingFileSystemEncryption = false;
      Boolean securitySettingFileSystemEncryptionBoolean = (Boolean)ApplicationRegistry.getApplicationRegistry().get(-2975703265270751603L);
      if (securitySettingFileSystemEncryptionBoolean != null) {
         securitySettingFileSystemEncryption = securitySettingFileSystemEncryptionBoolean;
      }

      if (ITPolicyInternal.policyHasChanged() || securitySettingPassword || securitySettingContentProtection || securitySettingFileSystemEncryption) {
         boolean passwordValidated = false;
         boolean policyAppliedCompletely = false;
         boolean var15 = false /* VF: Semaphore variable */;

         try {
            var15 = true;
            this._processPolicySetting = true;
            if (ITPolicy.getBoolean(21, 2, false)) {
               Dialog.alert(this._rb.getString(301));
            }

            SmartCardUserAuthenticatorFacade.checkITPolicy();
            int strength = ITPolicy.getInteger(24, 18, -1);
            if (FIPSPolicy.isDevicePasswordRequired() && strength != -1 || securitySettingContentProtection) {
               if (strength == -1) {
                  this._security.setContentProtection(true, this._security.getEncryptionStrength());
               } else {
                  this._security.setContentProtection(true, strength);
               }
            }

            if (!this._inSecurityDialogCode) {
               boolean var18 = false /* VF: Semaphore variable */;

               try {
                  var18 = true;
                  this._inSecurityDialogCode = true;
                  if (!this._security.isPasswordEnabled()
                        && (FIPSPolicy.isDevicePasswordRequired() || FileSystemOptions.isDevicePasswordRequired() || securitySettingFileSystemEncryption)
                     || securitySettingPassword) {
                     Backlight.setTimeout(30);
                     passwordValidated = SecurityDialog.changePassword(null, false, false, false, '\u0000');
                     Backlight.setTimeout(5);
                  }

                  if (SmartCardUserAuthenticatorFacade.isInitializationRequired()) {
                     passwordValidated &= SecurityDialog.initializeAuthenticator(true);
                  }

                  UserAuthenticator userAuthenticator = this._security.getUserAuthenticator();
                  if (userAuthenticator != null) {
                     if (!userAuthenticator.isConfigured()) {
                        userAuthenticator.configure();
                        var18 = false;
                     } else {
                        var18 = false;
                     }
                  } else {
                     var18 = false;
                  }
               } finally {
                  if (var18) {
                     this._inSecurityDialogCode = false;
                  }
               }

               this._inSecurityDialogCode = false;
               policyAppliedCompletely = true;
            }

            if (securitySettingPassword) {
               ApplicationRegistry.getApplicationRegistry().replace(5862813030521710644L, Boolean.FALSE);
            }

            if (securitySettingContentProtection) {
               ApplicationRegistry.getApplicationRegistry().replace(-746144713976755387L, Boolean.FALSE);
            }

            if (securitySettingFileSystemEncryption) {
               ApplicationRegistry.getApplicationRegistry().replace(-2975703265270751603L, Boolean.FALSE);
               var15 = false;
            } else {
               var15 = false;
            }
         } finally {
            if (var15) {
               this._processPolicySetting = false;
            }
         }

         this._processPolicySetting = false;
         if (policyAppliedCompletely) {
            ITPolicyInternal.markUpdate();
         }

         if (passwordValidated) {
            this.shutdown();
         }
      }
   }

   @Override
   public final boolean stylusDown(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public final boolean stylusUp(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public final boolean stylusDrag(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public final boolean stylusTap(int x, int y, int status, int time) {
      this.displayPromptDialog(true, '\u0000');
      return true;
   }

   @Override
   public final boolean stylusDoubleTap(int x, int y, int status, int time) {
      this.displayPromptDialog(true, '\u0000');
      return true;
   }

   @Override
   public final boolean stylusTapHold(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (StandbyManager.getInstance().isInStandby()) {
         return false;
      }

      if ((!this._security.isPasswordEnabled() || key == 137) && !Keypad.hasSendEndKeys()) {
         return this._hotkeyInit;
      }

      switch (key) {
         case '\u001b':
         case '\u0080':
         case '\u0081':
         case '\u0082':
         case '\u0083':
         case '\u0084':
         case '\u0090':
            this.displayPromptDialog(false, '\u0000');
            return true;
         case '\u0096':
            return false;
         case '\u0097':
            return false;
         default:
            this.displayPromptDialog(false, key);
            return true;
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (!Phone.isPhoneActive()) {
         if (Trackball.isSupported() && key == 261 || !Trackball.isSupported() && (key == 273 || key == 261)) {
            ProfileQuickToggle.handleKeyDown(time);
            return true;
         }

         if (StandbyManager.getInstance().isInStandby()) {
            return false;
         }
      }

      switch (key) {
         case 16:
         case 20:
            this._hotkeyInit = false;
            char keyChar = Keypad.map(keycode);
            if (Keypad.hasSendEndKeys() && (keyChar == '*' || Keypad.getAltedChar(keyChar) == '*')) {
               this._hotkeyInit = true;
               if (!this._security.isPasswordEnabled()) {
                  return true;
               }
            }

            return false;
         case 17:
            if (this._hotkeyInit) {
               this._keyLockSequence = true;
               this.unlock();
               return true;
            }

            this.displayPromptDialog(false, '\u0000');
            return true;
         case 18:
            this.displayPromptDialog(false, '\u0000');
            this._hotkeyInit = false;
            return true;
         case 19:
         case 21:
         default:
            this.displayPromptDialog(false, '\u0000');
            this._hotkeyInit = false;
            return true;
      }
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return Keypad.key(keycode) == 21 && !StandbyManager.getInstance().isInStandby();
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      int key = Keypad.key(keycode);
      return Phone.isPhoneActive() || (key != 273 && key != 261 || Trackball.isSupported()) && (key != 261 || !Trackball.isSupported())
         ? false
         : ProfileQuickToggle.handleKeyRepeat(time);
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 1597563888101360867L) {
         String prompt = (String)(!(object0 instanceof Object) ? null : object0);
         this.unlock('\u0000', false, false, prompt);
      } else {
         if (guid != 8508406279413621091L && guid != -594020114676189989L) {
            if (guid == -8392006003204551101L) {
               if (this._owner != null) {
                  this._owner.updateOwnerInfo();
                  return;
               }
            } else if (guid == -5577605925563127340L) {
               if (!this._security.isPasswordEnabled()) {
                  this._keyLockSequence = true;
                  this.unlock();
                  return;
               }
            } else {
               if (guid == 1981938861510850567L) {
                  this._keyLockSequence = true;
                  this._postEventAfterPasswordAttempt = true;
                  this._unlockOnStartup = true;
                  this.requestForeground();
                  this.unlock();
                  return;
               }

               if (guid == 2573494863350550132L && this._lockedScreen != null) {
                  if (this._lockedScreenLayout != null) {
                     this._lockedScreen.delete(this._lockedScreenLayout);
                  }

                  if (this._bannerField != null) {
                     this._lockedScreen.delete(this._bannerField);
                  }

                  if (this._owner != null) {
                     this._lockedScreen.delete(this._owner);
                  }

                  this.createScreenFields(this._lockedScreen);
               }
            }
         } else if (!this._processPolicySetting) {
            this.processPolicySettings();
            return;
         }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      if (eventId != 1000 && eventId != 1100) {
         if (eventId == 1002) {
            Backlight.setTimeout(5);
         }
      } else {
         Backlight.setTimeout(UiSettings.getBacklightTimeout());
      }
   }

   @Override
   public final void cardInserted() {
   }

   @Override
   public final void cardReady() {
   }

   @Override
   public final void cardUpdated() {
   }

   @Override
   public final void cardInvalid(int reason, int subReason) {
      if (reason == 2) {
         if (this._simCodeDialog != null) {
            this._simCodeDialog.cancel();
            return;
         }

         if (this._simRequestInProgress) {
            this._simRequestInProgress = false;
         }
      }
   }

   @Override
   public final void cardFault(int code) {
   }

   @Override
   public final void smsEFFull() {
   }

   @Override
   public final void responseDeleteSMS(int status, int packetId) {
   }

   @Override
   public final void responseMarkSMSAsRead(int status, int packetId) {
   }

   @Override
   public final void requestSendPIN(int retriesRemaining) {
      if (this._simCodeDialog == null) {
         this.processSIMEvent(this._rb.getString(211), false);
         this.unlock();
      }
   }

   @Override
   public final void pinValid() {
      if (this._simCodeDialog == null) {
         this.processSIMEvent(this._rb.getString(216), true);
         this.unlock('\u0000', true, true, null);
      }
   }

   @Override
   public final void requestSendPUK(int retriesRemaining) {
      if (this._simCodeDialog == null) {
         this.processSIMEvent(this._rb.getString(211), false);
         this.unlock();
      }
   }

   @Override
   public final void responseEnablePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseDisablePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseChangePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseValidatePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseDeactivateMEP(boolean success) {
   }

   @Override
   public final void wtlsKeyWriteComplete(int status) {
   }

   @Override
   public final void signalLevel(int level) {
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.networkServiceChange(networkId, service);
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      if (!this._processPolicySetting) {
         this.processPolicySettings();
      }
   }

   private final ActiveRichTextField getHintField(HintStringPattern pattern, Font font) {
      pattern.setEmoticonSize(font.getHeight());
      return (ActiveRichTextField)(new Object(
         "", null, null, new Object[]{Font.getDefault()}, null, null, 36028797018963968L, (StringPatternContainer)(new Object(new Object[]{pattern}))
      ));
   }
}
