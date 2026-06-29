package net.rim.device.apps.internal.explorer.file.options;

import javax.microedition.io.file.FileSystemListener;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.SecurityDialog;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemInfo;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.io.file.RootRegister;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.USBPortInternal;

public final class MediaCardOptionsItem extends SaveableMainScreenOptionsListItem implements GlobalEventListener, FieldChangeListener, FileSystemListener {
   private VerticalFieldManager _vfm;
   private BooleanChoiceField _externalMemoryEnabledChoice;
   private ObjectChoiceField _encryptionModeChoice;
   private BooleanChoiceField _encryptionMediaFilesChoice;
   private BooleanChoiceField _usbMassStorageModeChoice;
   private ObjectChoiceField _usbMassStoragePromptChoice;
   private ObjectChoiceField _safelyRemovePromptChoice;
   private MediaCardOptionsItem$EnableUSBMassStorageVerb _enableUSBMassStorageVerb;
   private MediaCardOptionsItem$FormatCardVerb _formatCardVerb;
   private MediaCardOptionsItem$SafelyRemoveCardVerb _safelyRemoveCardVerb;
   private MediaCardOptionsItem$MountCardVerb _mountCardVerb;
   private static final long ONE_GIGABYTE = 1073741824L;
   private static final long ONE_MEGABYTE = 1048576L;

   public MediaCardOptionsItem() {
      super(ExplorerResources.getResourceBundleFamily(), 52, 1888231790844671165L);
      ContextObject.put(super._context, 244, new Object(33881));
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._vfm = (VerticalFieldManager)(new Object());
      mainScreen.add(this._vfm);
      this._externalMemoryEnabledChoice = (BooleanChoiceField)(new Object(
         ExplorerResources.getString(56), 1, false, getRestrictedStyle(ITPolicy.getBoolean(24, 58, false))
      ));
      this._encryptionModeChoice = (ObjectChoiceField)(new Object(ExplorerResources.getString(53), ExplorerResources.getStringArray(54)));
      this._encryptionMediaFilesChoice = (BooleanChoiceField)(new Object(ExplorerResources.getString(55), 0, false));
      this._usbMassStorageModeChoice = (BooleanChoiceField)(new Object(
         ExplorerResources.getString(60), 1, false, getRestrictedStyle(ITPolicy.getBoolean(24, 59, false))
      ));
      this._usbMassStoragePromptChoice = (ObjectChoiceField)(new Object(ExplorerResources.getString(61), ExplorerResources.getStringArray(62), 0));
      this._safelyRemovePromptChoice = (ObjectChoiceField)(new Object(ExplorerResources.getString(127), ExplorerResources.getStringArray(62), 0));
      this.updateScreenItems(true);
      this.addScreenItems();
      this._externalMemoryEnabledChoice.setChangeListener(this);
      this._encryptionModeChoice.setChangeListener(this);
      this._encryptionMediaFilesChoice.setChangeListener(this);
      this._usbMassStorageModeChoice.setChangeListener(this);
      this._formatCardVerb = new MediaCardOptionsItem$FormatCardVerb();
      this._safelyRemoveCardVerb = new MediaCardOptionsItem$SafelyRemoveCardVerb();
      this._enableUSBMassStorageVerb = new MediaCardOptionsItem$EnableUSBMassStorageVerb();
      this._mountCardVerb = new MediaCardOptionsItem$MountCardVerb();
      UiApplication app = UiApplication.getUiApplication();
      app.addGlobalEventListener(this);
      app.addFileSystemListener(this);
   }

   private static final long getRestrictedStyle(boolean value) {
      return value ? 268435456 : 0;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean close = super.confirm(verb, context);
      if (close) {
         UiApplication app = UiApplication.getUiApplication();
         app.removeGlobalEventListener(this);
         app.removeFileSystemListener(this);
      }

      return close;
   }

   private final void addScreenItems() {
      this._vfm.add(this._externalMemoryEnabledChoice);
      if (this._externalMemoryEnabledChoice.isAffirmative()) {
         this._vfm.add(this._encryptionModeChoice);
         if (this._encryptionModeChoice.getSelectedIndex() != 0) {
            this._vfm.add(this._encryptionMediaFilesChoice);
         }

         this._vfm.add(this._usbMassStorageModeChoice);
         if (this._usbMassStorageModeChoice.isAffirmative()) {
            this._vfm.add(this._usbMassStoragePromptChoice);
         }

         if (InternalServices.isDeviceCapable(24)) {
            this._vfm.add(this._safelyRemovePromptChoice);
         }

         this._vfm.add((Field)(new Object()));
         if (this._externalMemoryEnabledChoice.isDirty()) {
            this._vfm.add((Field)(new Object(ExplorerResources.getString(111))));
            return;
         }

         RootRegister register = RootRegister.getInstance();
         if (register.isCardInserted()) {
            if (register.isMassStorageActive()) {
               HorizontalFieldManager hfm = (HorizontalFieldManager)(new Object(1152921504606846976L));
               hfm.add((Field)(new Object(Bitmap.getPredefinedBitmap(2))));
               hfm.add((Field)(new Object(ExplorerResources.getString(64))));
               this._vfm.add(hfm);
               return;
            }

            FileSystemInfo info = (FileSystemInfo)(new Object());
            int status = FileSystem.getFileSystemInfo(1, info);
            if (status == 0) {
               this._vfm.add((Field)(new Object(ExplorerResources.getString(59), FileUtilities.sizeToString(info.getTotalSpace()))));
               this._vfm.add((Field)(new Object(ExplorerResources.getString(58), FileUtilities.sizeToString(info.getFreeSpace()))));
               return;
            }

            if (register.isBatteryDoorOpen()) {
               this._vfm.add((Field)(new Object(ExplorerResources.getString(151))));
               return;
            }

            this._vfm.add((Field)(new Object(ExplorerResources.getString(51))));
            return;
         }

         this._vfm.add((Field)(new Object(ExplorerResources.getString(114))));
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field != this._externalMemoryEnabledChoice && field != this._usbMassStorageModeChoice) {
         if (field == this._encryptionModeChoice || field == this._encryptionMediaFilesChoice) {
            int encryptionLevel = this.calculateEncryptionValue();
            int itPolicyValue = ITPolicy.getInteger(24, 60, 0);
            boolean userSettingLess = false;
            switch (itPolicyValue) {
               case 0:
               default:
                  userSettingLess = false;
                  break;
               case 1:
                  userSettingLess = encryptionLevel != 1 && encryptionLevel != 2 && encryptionLevel != 5 && encryptionLevel != 6;
                  break;
               case 2:
                  userSettingLess = encryptionLevel != 2 && encryptionLevel != 5 && encryptionLevel != 6;
                  break;
               case 3:
                  userSettingLess = encryptionLevel != 3 && encryptionLevel != 4 && encryptionLevel != 5 && encryptionLevel != 6;
                  break;
               case 4:
                  userSettingLess = encryptionLevel != 4 && encryptionLevel != 5 && encryptionLevel != 6;
                  break;
               case 5:
                  userSettingLess = encryptionLevel != 5 && encryptionLevel != 6;
                  break;
               case 6:
                  userSettingLess = encryptionLevel != 6;
            }

            if (userSettingLess) {
               this.updateScreenItems(true);
               Status.show(ExplorerResources.getString(78));
               return;
            }

            this._vfm.deleteAll();
            this.addScreenItems();
            field.setFocus();
         }
      } else {
         this._vfm.deleteAll();
         this.addScreenItems();
         field.setFocus();
      }
   }

   private final void updateScreenItems(boolean updateAll) {
      int index = 0;
      boolean encryptMedia = false;
      switch (FileSystemOptions.getExternalEncryptionLevel()) {
         case 0:
            break;
         case 1:
            index = 2;
            break;
         case 2:
            index = 2;
            encryptMedia = true;
            break;
         case 3:
         default:
            index = 1;
            break;
         case 4:
            index = 1;
            encryptMedia = true;
            break;
         case 5:
            index = 3;
            break;
         case 6:
            index = 3;
            encryptMedia = true;
      }

      this._encryptionModeChoice.setSelectedIndex(index);
      this._encryptionModeChoice.setDirty(false);
      this._encryptionMediaFilesChoice.setAffirmative(encryptMedia);
      this._encryptionMediaFilesChoice.setDirty(false);
      if (updateAll) {
         this._externalMemoryEnabledChoice.setAffirmative(FileSystemOptions.getExternalMemoryEnabled());
         this._externalMemoryEnabledChoice.setDirty(false);
         this._externalMemoryEnabledChoice.setEditable(!ITPolicy.getBoolean(24, 58, false));
         this._usbMassStorageModeChoice.setAffirmative(FileSystemOptions.getUSBMassStorageMode());
         this._usbMassStorageModeChoice.setDirty(false);
         this._usbMassStorageModeChoice.setEditable(!ITPolicy.getBoolean(24, 59, false));
         this._usbMassStoragePromptChoice.setSelectedIndex(FileSystemOptions.getAutoEnableUSBMassStorageMode());
         this._usbMassStoragePromptChoice.setDirty(false);
         this._safelyRemovePromptChoice.setSelectedIndex(FileSystemOptions.getSafelyRemoveMode());
         this._safelyRemovePromptChoice.setDirty(false);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 8508406279413621091L && guid != -594020114676189989L) {
         if (guid == -278191449801390253L) {
            this._vfm.deleteAll();
            this.addScreenItems();
         }
      } else {
         this.updateScreenItems(true);
      }
   }

   @Override
   public final void rootChanged(int action, String rootName) {
      this._vfm.deleteAll();
      this.addScreenItems();
   }

   private final int calculateEncryptionValue() {
      boolean encryptMedia = this._encryptionMediaFilesChoice.isAffirmative();
      switch (this._encryptionModeChoice.getSelectedIndex()) {
         case 0:
         default:
            return 0;
         case 1:
            if (encryptMedia) {
               return 4;
            }

            return 3;
         case 2:
            if (encryptMedia) {
               return 2;
            }

            return 1;
         case 3:
            return encryptMedia ? 6 : 5;
      }
   }

   @Override
   protected final boolean save() {
      int encryptionLevel = this.calculateEncryptionValue();
      boolean enablePassword = false;
      switch (encryptionLevel) {
         case 1:
         case 2:
         case 5:
         case 6:
         default:
            enablePassword = true;
         case 0:
         case 3:
         case 4:
            if (enablePassword && !Security.getInstance().isPasswordEnabled()) {
               Dialog.ask(0, ExplorerResources.getString(79));
               if (!SecurityDialog.changePassword(null, false, true, false, '\u0000')) {
                  return false;
               }

               RibbonLauncher launcher = RibbonLauncher.getInstance();
               if (launcher != null) {
                  launcher.updateRegisteredAction("net.rim.LockSystem");
               }
            }

            FileSystemOptions.setExternalMemoryEnabled(this._externalMemoryEnabledChoice.isAffirmative());
            FileSystemOptions.setUSBMassStorageMode(this._usbMassStorageModeChoice.isAffirmative());
            FileSystemOptions.setAutoEnableUSBMassStorageMode(this._usbMassStoragePromptChoice.getSelectedIndex());
            FileSystemOptions.setSafelyRemoveMode(this._safelyRemovePromptChoice.getSelectedIndex());
            FileSystemOptions.setExternalEncryptionLevel(encryptionLevel);
            FileSystemOptions.save();
            return super.save();
      }
   }

   @Override
   protected final void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      super.populateMenuVerbs(verbToMenu, instance);
      RootRegister register = RootRegister.getInstance();
      if (register.isCardInserted() && FileSystemOptions.getExternalMemoryEnabled()) {
         if (register.isMassStorageActive() || register.isCardMounted()) {
            verbToMenu.addVerb(this._safelyRemoveCardVerb);
         }

         if (!register.isMassStorageActive()) {
            verbToMenu.addVerb(this._formatCardVerb);
            if (FileSystemOptions.getUSBMassStorageMode()) {
               int state = USBPortInternal.getConnectionState();
               if (state != -1 && (state & 16) != 0) {
                  verbToMenu.addVerb(this._enableUSBMassStorageVerb);
               }
            }

            if (!register.isCardMounted()) {
               verbToMenu.addVerb(this._mountCardVerb);
            }
         }
      }
   }
}
