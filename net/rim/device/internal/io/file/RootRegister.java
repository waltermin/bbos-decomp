package net.rim.device.internal.io.file;

import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.SystemListener3;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.cldc.io.file.FileSystemEncryption;
import net.rim.device.cldc.io.file.MasterKeyFile;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.store.AAALibrary;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.Expansion;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.USBPortInternal;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;
import net.rim.device.resources.Resource;

public final class RootRegister implements SystemListener3, GlobalEventListener, PopupDialogClosedListener, DialogClosedListener {
   private Dialog _promptDialog;
   private RootRegister$USBMSPasswordDialog _usbMsPasswordDialog;
   private RootRegister$MasterKeyPasswordDialog _masterKeyPasswordDialog;
   private Dialog _upgradeLockDialog;
   private Dialog _changePwdDialog;
   private Dialog _safelyRemoveDialog;
   private String _tempPwd;
   private boolean _asked;
   private boolean _askedForSDPassword;
   private boolean _cardInserted;
   private boolean _massStorageActive;
   private boolean _cardMounted;
   private KeyProvider _keyProvider;
   private long _cachedKeyLastModTime;
   private byte[] _cachedKey;
   private int _cachedLockType;
   private boolean _attemptedMountOnInsert;
   private boolean _poweredOff;
   private boolean _batteryDoorRemoved;
   private static final long FILE_SYSTEM_ROOT_REGISTER;
   public static final long FILE_SYSTEM_USB_MS_CHANGED;
   private static final int DEVICE_KEY_LENGTH;
   private static RootRegister _rootRegister;
   private static String _knownPassword;
   static Class class$net$rim$device$cldc$io$file$PosixFileConnection;

   public final KeyProvider getKeyProvider() {
      return this._keyProvider;
   }

   public final synchronized byte[] getCachedMasterKey(long timestamp) {
      return this._cachedKey != null && timestamp == this._cachedKeyLastModTime ? this._cachedKey : null;
   }

   public final synchronized int getCachedLockType() {
      return this._cachedLockType;
   }

   public final synchronized void setCachedMasterKey(byte[] cacheKey, long timestamp, int lockType) {
      this._cachedKeyLastModTime = timestamp;
      this._cachedKey = cacheKey;
      this._cachedLockType = lockType;
   }

   public final boolean isCardInserted() {
      return this._cardInserted;
   }

   public final boolean testIfCardInserted() {
      if (this._cardInserted) {
         return true;
      }

      for (int i = 0; i < 2; i++) {
         long expansionResult = Expansion.getExpansionType(i);
         if ((expansionResult & 4294967295L) == 0 && expansionResult >> 32 == 1) {
            return true;
         }
      }

      return false;
   }

   public final boolean isBatteryDoorOpen() {
      return this._batteryDoorRemoved;
   }

   public final synchronized boolean isMassStorageActive() {
      return this._massStorageActive;
   }

   public final synchronized boolean isCardMounted() {
      return this._cardMounted;
   }

   public final void mountSDCard() {
      if (!this._poweredOff && this._cardInserted && FileSystemOptions.getExternalMemoryEnabled() && !this._cardMounted) {
         int result = FileSystem.mount(
            1,
            FileSystem.SDCARD_ROOT_STR,
            class$net$rim$device$cldc$io$file$PosixFileConnection == null
               ? (class$net$rim$device$cldc$io$file$PosixFileConnection = class$("net.rim.device.cldc.io.file.PosixFileConnection"))
               : class$net$rim$device$cldc$io$file$PosixFileConnection
         );
         if (result == 19) {
            FileSystem.unmount(1, FileSystem.SDCARD_ROOT_STR);
            result = FileSystem.mount(
               1,
               FileSystem.SDCARD_ROOT_STR,
               class$net$rim$device$cldc$io$file$PosixFileConnection == null
                  ? (class$net$rim$device$cldc$io$file$PosixFileConnection = class$("net.rim.device.cldc.io.file.PosixFileConnection"))
                  : class$net$rim$device$cldc$io$file$PosixFileConnection
            );
         }

         if (result == 0) {
            synchronized (this) {
               this._massStorageActive = false;
               this._cardMounted = true;
               this._cachedKey = null;
               this._cachedLockType = 0;
               this._cachedKeyLastModTime = 0;
            }

            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/");
            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/ringtones/");
            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/pictures/");
            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/music/");
            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/videos/");
            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/voicenotes/");
            FileUtilities.ensureDirectoryExists("file:///SDCard/BlackBerry/system/");
            FileUtilities.setHidden("file:///SDCard/BlackBerry/system/", true);
            this.checkKeyFile(ApplicationManager.getApplicationManager().isSystemLocked());
            return;
         }

         if (result == 22) {
            BackgroundDialog.showMessage(CommonResource.getString(10129));
            return;
         }

         if (result == 18) {
            BackgroundDialog.showMessage(CommonResource.getString(10136));
         }
      }
   }

   final void enableUSBMassStorageNoPrompt() {
      if (!this._poweredOff && this._cardInserted && FileSystemOptions.getExternalMemoryEnabled() && FileSystemOptions.getUSBMassStorageMode()) {
         this.unmountSDCard(true);

         try {
            Expansion.setUSBMassStorageProperties(1);
            synchronized (this) {
               this._massStorageActive = true;
            }

            RIMGlobalMessagePoster.postGlobalEvent(-278191449801390253L);
         } finally {
            ;
         }
      }
   }

   public final void enableUSBMassStoragePromptIfNecessary() {
      if (!Security.getInstance().isPasswordEnabled()) {
         this.enableUSBMassStorageNoPrompt();
      } else {
         this.showUsbMsPasswordDialog();
      }
   }

   public final void safelyRemoveCard() {
      this.unmountSDCard(true);
      this.disableUSBMassStorage();
   }

   public final boolean formatSDCard() {
      if (this._cardInserted && FileSystemOptions.getExternalMemoryEnabled()) {
         this.unmountSDCard(false);
         int status = FileSystem.format(1);
         this.mountSDCard();
         return status == 0;
      } else {
         return false;
      }
   }

   @Override
   public final void usbConnectionStateChange(int state) {
      this.usbConnectionStateChange(state, false);
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (dialog == this._promptDialog) {
         int optionChoice = -1;
         switch (choice) {
            case -1:
               optionChoice = 1;
               break;
            case 4:
               optionChoice = 0;
               this.enableUSBMassStoragePromptIfNecessary();
         }

         if (optionChoice != -1 && this._promptDialog.isDontAskAgainChecked()) {
            FileSystemOptions.setAutoEnableUSBMassStorageMode(optionChoice);
            FileSystemOptions.save();
         }

         this._promptDialog = null;
      } else if (dialog == this._upgradeLockDialog) {
         if (choice == 4) {
            FileSystemEncryption.lockTypeChanged(this._tempPwd);
         }

         this._upgradeLockDialog = null;
         this._tempPwd = null;
      } else if (dialog == this._changePwdDialog) {
         if (choice == 4) {
            FileSystemEncryption.changePassword(this._tempPwd, null);
            this._tempPwd = null;
         }

         this._changePwdDialog = null;
         MasterKeyFile keyFile = FileSystemEncryption.getKeyFile();
         if (keyFile != null && !FileSystemEncryption.lockTypeSufficient(keyFile.getLockType())) {
            this.showUpgradeLockDialog();
         } else {
            this._tempPwd = null;
         }
      } else {
         if (dialog == this._safelyRemoveDialog) {
            if (choice == 4) {
               this.unmountSDCard(true);
               this.disableUSBMassStorage();
            }

            if ((choice == 4 || choice == -1) && dialog.isDontAskAgainChecked()) {
               FileSystemOptions.setSafelyRemoveMode(choice == 4 ? 0 : 1);
            }

            this._safelyRemoveDialog = null;
         }
      }
   }

   @Override
   public final void dialogClosed(PopupDialog dialog, int closeReason) {
      if (dialog == this._usbMsPasswordDialog) {
         switch (closeReason) {
            case 0:
               this.enableUSBMassStorageNoPrompt();
            default:
               this._usbMsPasswordDialog = null;
         }
      } else {
         if (dialog == this._masterKeyPasswordDialog) {
            if (closeReason == 0) {
               if (Security.getInstance().isPasswordEnabled()) {
                  this.showChangePwdDialog();
               } else if (!FileSystemEncryption.lockTypeSufficient(this._masterKeyPasswordDialog.getKeyFile().getLockType())) {
                  this.showUpgradeLockDialog();
               }
            }

            this._masterKeyPasswordDialog = null;
         }
      }
   }

   @Override
   public final void powerOff() {
      this.safelyRemoveCard();
      this._poweredOff = true;
      this._asked = false;
   }

   @Override
   public final void powerUp() {
      this._poweredOff = false;
      this.mountOrUSBEnable();
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void fastReset() {
      synchronized (this) {
         this._askedForSDPassword = false;
         this._cardMounted = false;
         this._cachedKey = null;
         this._cachedLockType = 0;
         this._cachedKeyLastModTime = 0;
      }

      FileSystem.closeAllStreams(1);
      this.mountOrUSBEnable();
   }

   @Override
   public final void batteryDoorOpened() {
      this._batteryDoorRemoved = true;
      this._attemptedMountOnInsert = false;
      if (this._cardInserted
         && FileSystemOptions.getExternalMemoryEnabled()
         && InternalServices.isDeviceCapable(20)
         && (this._cardMounted || this._massStorageActive)) {
         switch (FileSystemOptions.getSafelyRemoveMode()) {
            case 0:
               this.safelyRemoveCard();
               break;
            case 2:
               this.showSafelyRemoveCardDialog();
               return;
         }
      }
   }

   @Override
   public final void batteryDoorClosed() {
      this._batteryDoorRemoved = false;
      this.dismissSafelyRemoveDialog();
      if (!this._attemptedMountOnInsert
         && this._cardInserted
         && FileSystemOptions.getExternalMemoryEnabled()
         && InternalServices.isDeviceCapable(20)
         && !this._cardMounted
         && !this._massStorageActive) {
         int usbState = USBPortInternal.getConnectionState();
         if ((usbState & 16) != 0) {
            this.usbConnectionStateChange(usbState, true);
         }

         if (!this._massStorageActive) {
            this.mountSDCard();
         }
      }
   }

   @Override
   public final void usbMSMediumChanged(int type) {
      if (type == 0) {
         this.disableUSBMassStorage();
         this.mountSDCard();
      }
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void powerOffRequested(int reason) {
      FileSystem.flushAllStreams(1);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L && FileSystemOptions.getExternalMemoryEnabled() && this._cardInserted && !this._massStorageActive) {
         if (!this._asked && FileSystemOptions.getUSBMassStorageMode()) {
            int mode = FileSystemOptions.getAutoEnableUSBMassStorageMode();
            int state = USBPortInternal.getConnectionState();
            if (state != -1 && (state & 16) != 0) {
               switch (mode) {
                  case 0:
                     if (Security.getInstance().isPasswordEnabled()) {
                        this.showUsbMsPasswordDialog();
                     }
                     break;
                  case 2:
                     this.showPromptDialog();
               }
            }
         }

         this.checkKeyFile(false);
      } else if (guid == -7131874474196788121L) {
         this.dismissUsbMSDialogs();
         this.dismissKeyDialogs();
         this._keyProvider.clearPassword();
      } else {
         if (guid != 8508406279413621091L && guid != -594020114676189989L && guid != -3054176912083292366L) {
            if (guid == -1270659756336956134L) {
               FileSystem.checkForDeadListeners();
            }
         } else {
            if (guid != -3054176912083292366L) {
               data0 = -1;
            }

            if (data0 == 1) {
               return;
            }

            if (!FileSystemOptions.getExternalMemoryEnabled()) {
               this.unmountSDCard(true);
               this.disableUSBMassStorage();
            } else {
               if (!FileSystemOptions.getUSBMassStorageMode()) {
                  this.disableUSBMassStorage();
               }

               this.mountSDCard();
            }

            FileSystemEncryption.lockTypeChanged(null);
            if (guid == -3054176912083292366L) {
               AAALibrary.optionsChanged();
               return;
            }
         }
      }
   }

   private final void showSafelyRemoveCardDialog() {
      if (this._safelyRemoveDialog == null) {
         Backlight.enable(true);
         byte[] midiData = Resource.getResourceClass().getResource("safely_remove.mid");
         if (midiData != null) {
            Alert.startMIDI(midiData, 2);
         }

         this._safelyRemoveDialog = (Dialog)(new Object(3, CommonResource.getString(10132), -1, Bitmap.getPredefinedBitmap(1), 33554432, true));
         this._safelyRemoveDialog.setDialogClosedListener(this);
         this._safelyRemoveDialog.show(-2147483646);
      }
   }

   private final void dismissUsbMSDialogs() {
      if (this._promptDialog != null) {
         this._promptDialog.cancel();
         this._asked = false;
      }

      if (this._usbMsPasswordDialog != null) {
         this._usbMsPasswordDialog.close(-1);
         this._asked = false;
      }
   }

   private final void dismissKeyDialogs() {
      if (this._masterKeyPasswordDialog != null) {
         this._masterKeyPasswordDialog.close(-1);
         this._askedForSDPassword = false;
      }

      if (this._upgradeLockDialog != null) {
         this._upgradeLockDialog.cancel();
      }

      if (this._changePwdDialog != null) {
         this._changePwdDialog.cancel();
      }

      this._tempPwd = null;
   }

   private final void dismissSafelyRemoveDialog() {
      if (this._safelyRemoveDialog != null) {
         this._safelyRemoveDialog.cancel();
         this._safelyRemoveDialog = null;
      }
   }

   private final void showUpgradeLockDialog() {
      if (this._upgradeLockDialog == null) {
         this._upgradeLockDialog = (Dialog)(new Object(3, CommonResource.getString(10123), 4, Bitmap.getPredefinedBitmap(1), 33554432));
         this._upgradeLockDialog.setDialogClosedListener(this);
         this._upgradeLockDialog.show();
      }
   }

   private final void mountOrUSBEnable() {
      int usbState = USBPortInternal.getConnectionState();
      if ((usbState & 16) != 0) {
         this.usbConnectionStateChange(usbState, true);
      }

      if (!this._massStorageActive) {
         this.mountSDCard();
      }
   }

   private final void showPromptDialog() {
      if (this._promptDialog == null) {
         this._promptDialog = (Dialog)(new Object(3, CommonResource.getString(10119), -1, Bitmap.getPredefinedBitmap(1), 33554432, true));
         this._promptDialog.setDialogClosedListener(this);
         this._promptDialog.show();
      }
   }

   private final void usbConnectionStateChange(int state, boolean immediate) {
      if ((state & 16) != 0) {
         if (this._cardInserted
            && FileSystemOptions.getExternalMemoryEnabled()
            && FileSystemOptions.getUSBMassStorageMode()
            && !ApplicationManager.getApplicationManager().isSystemLocked()) {
            this._asked = true;
            switch (FileSystemOptions.getAutoEnableUSBMassStorageMode()) {
               case 0:
                  this.enableUSBMassStoragePromptIfNecessary();
                  return;
               case 2:
                  this.showPromptDialog();
                  return;
            }
         }
      } else if ((state & 4) != 0) {
         Runnable r = new RootRegister$2(this);
         if (immediate) {
            r.run();
            return;
         }

         Application.getApplication().invokeLater(r, 1000, false);
      }
   }

   private final void checkKeyFile(boolean deviceLocked) {
      if (!this._askedForSDPassword) {
         MasterKeyFile keyFile = FileSystemEncryption.getKeyFile();
         if (keyFile != null) {
            if (keyFile.isDeviceLocked() && !keyFile.keyFileMatchesDevice()) {
               int resourceId;
               if (keyFile.pinMatchesDevice()) {
                  resourceId = 10128;
               } else {
                  resourceId = 10120;
               }

               this._askedForSDPassword = true;
               BackgroundDialog.showMessage(CommonResource.getString(resourceId));
            } else if (keyFile.isPasswordLocked() && !deviceLocked) {
               if (!keyFile.checkUserKey()) {
                  this._askedForSDPassword = true;
                  this.showMasterKeyPasswordDialog();
               } else {
                  FileSystem.rootChanged(FileSystem.SDCARD_ROOT_STR);
               }
            }

            if (!this._askedForSDPassword && !FileSystemEncryption.lockTypeSufficient(keyFile.getLockType())) {
               this.showUpgradeLockDialog();
            }
         }
      }
   }

   public static final RootRegister getInstance() {
      return _rootRegister;
   }

   private final void registerInternal() {
      FileTransfer.register();
      if (FileSystem.isFileSystemSupported(1)) {
         EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
         synchronized (dispatchManager) {
            if (dispatchManager.getDispatcher(15) == null) {
               dispatchManager.setDispatcher(15, new RootRegister$ExpansionPortEventDispatcher(this));
            }
         }

         Proxy proxy = Proxy.getInstance();
         proxy.addListener(15, this);
         proxy.addSystemListener(this);
         proxy.addGlobalEventListener(this);
         proxy.invokeLater(new RootRegister$1(this));
      }
   }

   private final void unmountSDCard(boolean flush) {
      if (flush) {
         FileSystem.flushAllStreams(1);
      }

      FileSystem.unmount(1, FileSystem.SDCARD_ROOT_STR);
      this.dismissKeyDialogs();
      this.dismissSafelyRemoveDialog();
      synchronized (this) {
         this._askedForSDPassword = false;
         this._cardMounted = false;
         this._cachedKey = null;
         this._cachedLockType = 0;
         this._cachedKeyLastModTime = 0;
      }
   }

   private final void showChangePwdDialog() {
      if (this._changePwdDialog == null) {
         this._changePwdDialog = (Dialog)(new Object(3, CommonResource.getString(10122), 0, Bitmap.getPredefinedBitmap(1), 33554432));
         this._changePwdDialog.setDialogClosedListener(this);
         this._changePwdDialog.show();
      }
   }

   private final void showUsbMsPasswordDialog() {
      if (this._usbMsPasswordDialog == null) {
         this._usbMsPasswordDialog = new RootRegister$USBMSPasswordDialog();
         this._usbMsPasswordDialog.setPopupDialogClosedListener(this);
         this._usbMsPasswordDialog.show();
      }
   }

   public static final void register() {
      getInstance().registerInternal();
   }

   private RootRegister() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial java/lang/Object.<init> ()V
      // 04: aload 0
      // 05: ldc_w "net.rim.device.apps.internal.file.FileSystemKeyProvider"
      // 08: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 0b: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0e: checkcast net/rim/device/internal/io/file/KeyProvider
      // 11: putfield net/rim/device/internal/io/file/RootRegister._keyProvider Lnet/rim/device/internal/io/file/KeyProvider;
      // 14: return
      // 15: astore 1
      // 16: return
      // 17: astore 1
      // 18: return
      // 19: astore 1
      // 1a: new java/lang/Object
      // 1d: dup
      // 1e: ldc_w "net_rim_os requires net_rim_bb_apps_framework"
      // 21: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 24: athrow
      // try (2 -> 8): 9 null
      // try (2 -> 8): 11 null
      // try (2 -> 8): 13 null
   }

   private final void disableUSBMassStorage() {
      synchronized (this) {
         if (this._massStorageActive) {
            try {
               Expansion.setUSBMassStorageProperties(0);
               this._massStorageActive = false;
               RIMGlobalMessagePoster.postGlobalEvent(-278191449801390253L);
            } finally {
               return;
            }
         }
      }
   }

   private final void showMasterKeyPasswordDialog() {
      if (this._masterKeyPasswordDialog == null) {
         this._masterKeyPasswordDialog = new RootRegister$MasterKeyPasswordDialog(this);
         this._masterKeyPasswordDialog.setPopupDialogClosedListener(this);
         this._masterKeyPasswordDialog.show();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }

   static {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      Object obj = registry.getOrWaitFor(6719885707613225690L);
      if (!(obj instanceof RootRegister)) {
         _rootRegister = new RootRegister();
         registry.put(6719885707613225690L, _rootRegister);
      } else {
         _rootRegister = (RootRegister)obj;
      }

      _knownPassword = "blackberry";
   }
}
