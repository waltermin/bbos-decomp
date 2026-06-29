package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

class ModuleInformation {
   private int _moduleHandle;
   protected MainScreen _mainScreen;
   private Font _defaultFont = Font.getDefault();
   private int _screenWidth = Display.getWidth();
   protected static final int INDENT_SIZE;
   private static ResourceBundle _prb = ResourceBundle.getBundle(8732645638888225014L, "net.rim.device.internal.resource.PlatformSecurity");

   public ModuleInformation(int moduleHandle) {
      this();
      this._moduleHandle = moduleHandle;
   }

   public ModuleInformation() {
   }

   public void open() {
      this.createMainScreen(CodeModuleManager.getModuleName(this._moduleHandle));
      UiApplication.getUiApplication().pushModalScreen(this._mainScreen);
   }

   protected MainScreen createMainScreen(String title) {
      this._mainScreen = new InfoMainScreen(this._defaultFont, 0);
      this.createModuleInformationItems();
      String[] values = new Object[]{title};
      String formattedTitle = MessageFormat.format(OptionsResources.getString(1461), values);
      this._mainScreen.setTitle((Field)(new Object(formattedTitle, 36028797018963968L)));
      return this._mainScreen;
   }

   protected void createModuleInformationItems() {
      String value;
      ApplicationDescriptor[] applicationDescriptors;
      if (CodeModuleManager.isLibrary(this._moduleHandle)) {
         value = OptionsResources.getString(712);
         applicationDescriptors = null;
      } else {
         value = OptionsResources.getString(711);
         applicationDescriptors = CodeModuleManager.getApplicationDescriptors(this._moduleHandle);
      }

      this.addProperty(708, value);
      value = CodeModuleManager.getModuleDescription(this._moduleHandle);
      this.addProperty(705, value);
      this.addProperty(703, trimVersion(CodeModuleManager.getModuleVersion(this._moduleHandle)));
      this.addProperty(
         709, ((StringBuffer)(new Object())).append(CodeModuleManager.getModuleCodeSize(this._moduleHandle)).append(OptionsResources.getString(406)).toString()
      );
      long timestamp = CodeModuleManager.getModuleTimestamp(this._moduleHandle);
      if (timestamp != -1) {
         value = DateFormat.getInstance(54).formatLocal(timestamp);
      } else {
         value = null;
      }

      this.addProperty(707, value);
      value = CodeModuleManager.getModuleVendor(this._moduleHandle);
      this.addProperty(704, value);
      if (applicationDescriptors != null) {
         this.addLabelField(710);

         for (int i = 0; i < applicationDescriptors.length; i++) {
            this.addIndentedField(applicationDescriptors[i].getName());
         }
      }

      byte[] hash = CodeModuleManager.getModuleHash(this._moduleHandle);
      this.addHashInformation(hash);
      this.addApplicationControlInformation(hash);
      this.addSignerIdInformation();
   }

   protected void addProperty(int resourceID, String text) {
      String label = OptionsResources.getString(resourceID);
      if (text == null) {
         text = "";
      }

      if (this._defaultFont.getAdvance(label) + this._defaultFont.getAdvance(text) > this._screenWidth) {
         this.addLabelField(label);
         this.addIndentedField(text);
      } else {
         this._mainScreen.add((Field)(new Object(label, text, 36028797018963968L)));
      }
   }

   protected void addLabelField(int resourceID) {
      this.addLabelField(OptionsResources.getString(resourceID));
   }

   protected void addLabelField(String label) {
      LabelField lf = (LabelField)(new Object(label));
      this._mainScreen.add(lf);
   }

   private void addRestrictionField(boolean isAllowed, int resourceID) {
      if (!isAllowed) {
         this.addIndentedField(_prb.getString(resourceID));
      }
   }

   private void addRestrictionField(int restricted, int resourceID) {
      if (restricted == 2) {
         StringBuffer buffer = (StringBuffer)(new Object(_prb.getString(resourceID)));
         buffer.append(OptionsResources.getString(1897));
         this.addIndentedField(buffer.toString());
      }

      if (restricted == 1) {
         this.addIndentedField(_prb.getString(resourceID));
      }
   }

   protected void addIndentedField(int resourceID, int indent) {
      this.addIndentedField(OptionsResources.getString(resourceID), indent);
   }

   protected void addIndentedField(String text) {
      this.addIndentedField(text, 10);
   }

   protected void addIndentedField(String text, int indent) {
      if (text != null) {
         RichTextField rtf = (RichTextField)(new Object(text, 36028797018963968L));
         rtf.setPadding(0, 0, 0, indent);
         this._mainScreen.add(rtf);
      }
   }

   private void addApplicationControlInformation(byte[] hash) {
      int moduleHandle = CodeModuleManager.getModuleHandle(hash);
      if (ApplicationControl.isRequiredApp(moduleHandle)) {
         this.addLabelField(1853);
      }

      if (ApplicationControl.isExcludedApp(moduleHandle)) {
         this.addLabelField(2002);
         CodeModuleManager.verifyApplicationControlModules();
      }

      if (!ControlledAccess.verifyCodeModuleSignature(this._moduleHandle, 51)
         || !InternalServices.isDeviceSecure() && !CodeModuleManager.getModuleName(this._moduleHandle).startsWith("net_rim")) {
         this._mainScreen.addMenuItem(new ModuleInformation$ApplicationControlMenuItem(this, this._moduleHandle));
         boolean localConnectionsAllowed = ApplicationControl.isLocalConnectionAllowed(moduleHandle);
         boolean blueToothSerialProfileAllowed = ApplicationControl.isBluetoothSerialProfileAllowed(moduleHandle);
         int phoneAllowed = ApplicationControl.isPhoneAllowed(moduleHandle);
         int locationApiAllowed = ApplicationControl.isLocationApiAllowed(moduleHandle);
         int internalConnectionsAllowed = ApplicationControl.isInternalConnectionAllowed(moduleHandle);
         int externalConnectionsAllowed = ApplicationControl.isExternalConnectionAllowed(moduleHandle);
         int wifiAllowed = ApplicationControl.isWiFiAllowed(moduleHandle);
         boolean ipcAllowed = ApplicationControl.isIPCAllowed(moduleHandle);
         int deviceSettingsAllowed = ApplicationControl.isChangeDeviceSettingsAllowed(moduleHandle);
         int mediaAllowed = ApplicationControl.isMediaAllowed(moduleHandle);
         boolean moduleManagementAllowed = ApplicationControl.isCMMApiAllowed(moduleHandle);
         boolean themeDataAllowed = ApplicationControl.isThemeDataAllowed(moduleHandle);
         boolean eventInjectorAllowed = ApplicationControl.isEventInjectorAllowed(moduleHandle);
         boolean browserFiltersAllowed = ApplicationControl.isBrowserFilterAllowed(moduleHandle);
         int screenCaptureAllowed = ApplicationControl.isScreenCaptureAllowed(moduleHandle);
         int timerResetAllowed = ApplicationControl.isIdleTimerAllowed(moduleHandle);
         boolean emailAllowed = ApplicationControl.isEmailAllowed(moduleHandle);
         boolean pimAllowed = ApplicationControl.isPIMAllowed(moduleHandle);
         boolean filesAllowed = ApplicationControl.isFileApiAllowed(moduleHandle);
         boolean handheldKeyStoreAllowed = ApplicationControl.isHandheldKeyStoreAllowed(moduleHandle);
         boolean keyStoreMediumSecurityAllowed = ApplicationControl.isKeyStoreMediumSecurityAllowed(moduleHandle);
         boolean anyRestrictions = !localConnectionsAllowed
            || !blueToothSerialProfileAllowed
            || phoneAllowed != 0
            || locationApiAllowed != 0
            || internalConnectionsAllowed != 0
            || externalConnectionsAllowed != 0
            || wifiAllowed != 0
            || !ipcAllowed
            || deviceSettingsAllowed != 0
            || mediaAllowed != 0
            || !moduleManagementAllowed
            || !themeDataAllowed
            || !eventInjectorAllowed
            || !browserFiltersAllowed
            || screenCaptureAllowed != 0
            || timerResetAllowed != 0
            || !emailAllowed
            || !pimAllowed
            || !filesAllowed
            || !handheldKeyStoreAllowed
            || !keyStoreMediumSecurityAllowed;
         if (anyRestrictions) {
            this.addLabelField(1854);
            this.addRestrictionField(localConnectionsAllowed, 19);
            if (BluetoothME.isSupported()) {
               this.addRestrictionField(blueToothSerialProfileAllowed, 0);
            }

            if (Phone.isSupported()) {
               this.addRestrictionField(phoneAllowed, 15);
            }

            this.addRestrictionField(locationApiAllowed, 12);
            if (this._moduleHandle == 0 || ITPolicyInternal.isITPolicyEnabled()) {
               this.addConnection(internalConnectionsAllowed, 9, ApplicationControl.getInternalConnectionDomains(hash));
            }

            this.addConnection(externalConnectionsAllowed, 8, ApplicationControl.getExternalConnectionDomains(hash));
            if (RadioInfo.areWAFsSupported(4)) {
               this.addRestrictionField(wifiAllowed, 20);
            }

            this.addRestrictionField(ipcAllowed, 7);
            this.addRestrictionField(deviceSettingsAllowed, 2);
            this.addRestrictionField(mediaAllowed, 13);
            this.addRestrictionField(moduleManagementAllowed, 14);
            this.addRestrictionField(themeDataAllowed, 18);
            this.addRestrictionField(eventInjectorAllowed, 4);
            this.addConnection(browserFiltersAllowed ? 0 : 1, 1, ApplicationControl.getBrowserFilterConnectionDomains(hash));
            this.addRestrictionField(screenCaptureAllowed, 17);
            this.addRestrictionField(timerResetAllowed, 6);
            this.addRestrictionField(emailAllowed, 3);
            this.addRestrictionField(pimAllowed, 16);
            this.addRestrictionField(filesAllowed, 5);
            this.addRestrictionField(handheldKeyStoreAllowed, 10);
            this.addRestrictionField(keyStoreMediumSecurityAllowed, 11);
         }
      }
   }

   private void addHashInformation(byte[] hash) {
      StringBuffer sb = (StringBuffer)(new Object(hash.length * 3));

      for (int i = 0; i < hash.length; i++) {
         sb.append(NumberUtilities.intToUpperHexDigit(hash[i] >>> 4));
         sb.append(NumberUtilities.intToUpperHexDigit(hash[i]));
         if ((i & 1) == 1) {
            sb.append(' ');
         }
      }

      String s = sb.toString();
      int length = s.length();
      int halfLength = length >> 1;
      this.addLabelField(1472);
      this.addIndentedField(s.substring(0, halfLength));
      this.addIndentedField(s.substring(halfLength, length));
   }

   private void addSignerIdInformation() {
      int index = 0;
      int[] signerArray = new int[10];
      int signerIDint = CodeModuleManager.getModuleSignerId(this._moduleHandle, index);
      if (signerIDint != -1) {
         this.addLabelField(1944);

         while (signerIDint != -1) {
            if (index == signerArray.length) {
               Array.resize(signerArray, index + 10);
            }

            signerArray[index] = signerIDint;
            signerIDint = CodeModuleManager.getModuleSignerId(this._moduleHandle, ++index);
         }

         Arrays.sort(signerArray, 0, index);
         StringBuffer msg = (StringBuffer)(new Object());

         for (int i = 0; i < index; i++) {
            msg.append(CodeSigningKey.convert(signerArray[i]));
            msg.append(',');
            msg.append(' ');
         }

         msg.setLength(msg.length() - 2);
         this.addIndentedField(msg.toString());
      }
   }

   private void addConnection(int setting, int resourceId, String domains) {
      if (setting != 0) {
         if (domains != null) {
            this.addIndentedField(_prb.getString(resourceId));
            if (setting == 1) {
               this.addIndentedField(1868, 20);
            } else {
               this.addIndentedField(1869, 20);
            }

            this.addIndentedField(1870, 20);
            StringTokenizer st = (StringTokenizer)(new Object(domains, ';'));

            while (st.hasMoreElements()) {
               String token = st.nextToken();
               if (token != null) {
                  this.addIndentedField(token, 30);
               }
            }
         } else {
            StringBuffer buffer = (StringBuffer)(new Object(_prb.getString(resourceId)));
            if (setting == 2) {
               buffer.append(OptionsResources.getString(1897));
            }

            this.addIndentedField(buffer.toString());
         }
      }
   }

   static String trimVersion(String version) {
      int strIndex = -1;
      int count = 0;

      while ((strIndex = version.indexOf(46, strIndex + 1)) > 0) {
         count++;
      }

      return count == 4 ? version.substring(0, version.lastIndexOf(46)) : version;
   }
}
