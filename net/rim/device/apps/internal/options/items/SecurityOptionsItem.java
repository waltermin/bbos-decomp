package net.rim.device.apps.internal.options.items;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.crypto.Digest;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentInternal;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SecurityDialog;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.system.FIPSPolicy;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;
import net.rim.vm.Memory;

public final class SecurityOptionsItem extends SaveableMainScreenOptionsListItem implements FieldChangeListener, GlobalEventListener {
   private Security _security;
   private Calendar _calendar;
   private long GMT_TIME = 0;
   private BooleanChoiceField _passwordEnabledDisabledField;
   private NumericChoiceField _maximumPasswordAttemptsField;
   private TimeChoiceField _securityTimeoutField;
   private BooleanChoiceField _passwordPromptOnInstallField;
   private BooleanChoiceField _lockWhenHolsteredField;
   private BooleanChoiceField _allowOutgoingCallWhileLockedField;
   private BooleanChoiceField _enableContentEncryptionField;
   private String[] _contentEncryptionStrengthChoices;
   private ObjectChoiceField _contentEncryptionStrengthField;
   private int _minContentEncryptionStrength;
   private BooleanChoiceField _includeAddressBookInCPField;
   private BooleanChoiceField _enableContentCompressionField;
   private ObjectChoiceField _securityITPolicyServiceColourField;
   private ObjectChoiceField _securityOtherServiceColourField;
   private EditField _policyNameField;
   private DateField _policyDateTimeField;
   private DateField _policyTimestampField;
   private ObjectListField _servicesList;
   private BooleanChoiceField _userAuthenticatorField;
   private VerticalIndentFieldManager _authenticatorVFM;
   private RichTextField _isInitializedValueField;
   private BooleanChoiceField _numericPasswordEntryField;
   private Field _userAuthenticatorCustomField;
   private FieldProvider _userAuthenticatorCustomFieldProvider;
   private Verb _changePasswordVerb;
   private Verb _masterRadioResetVerb;
   private Verb _runSecurityTestsVerb;
   private Verb _wipeHandheldVerb;
   private String[][][] _regenerationUIDs;
   private static long MASTER_RADIO_RESET_VERB = 6773290853044830796L;
   private static final int ONE_MINUTE = 60;
   private static final int INDENT_PIXELS = 12;
   private static long[] _timeouts;
   private static SecurityOptionsItem$SecurityServiceColour[] _securityServiceColours = new SecurityOptionsItem$SecurityServiceColour[]{
      new SecurityOptionsItem$SecurityServiceColour(-1, 2007),
      new SecurityOptionsItem$SecurityServiceColour(16744576, 2010),
      new SecurityOptionsItem$SecurityServiceColour(16760960, 2011),
      new SecurityOptionsItem$SecurityServiceColour(16777088, 2012),
      new SecurityOptionsItem$SecurityServiceColour(8454016, 2013),
      new SecurityOptionsItem$SecurityServiceColour(8421631, 2014),
      new SecurityOptionsItem$SecurityServiceColour(16744703, 2015)
   };

   public SecurityOptionsItem() {
      super(OptionsResources.getString(1956), 5294015899860238835L);
      ContextObject.put(super._context, 244, "security");
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      Object radioResetObject = appRegistry.get(MASTER_RADIO_RESET_VERB);
      if (radioResetObject instanceof Object) {
         this._masterRadioResetVerb = (Verb)radioResetObject;
      }

      VerticalIndentFieldManager vifm = (VerticalIndentFieldManager)(new Object());
      mainScreen.add(vifm);
      this._passwordEnabledDisabledField = (BooleanChoiceField)(new Object(OptionsResources.getString(1403), 2, this._security.isPasswordEnabled(), 268435456));
      this._passwordEnabledDisabledField.setChangeListener(this);
      vifm.add(this._passwordEnabledDisabledField);
      this._maximumPasswordAttemptsField = (NumericChoiceField)(new Object(OptionsResources.getString(1993), 3, ITPolicy.getInteger(22, 2, 10), 1));
      this._maximumPasswordAttemptsField.setSelectedValue(this._security.getMaxPasswordAttempts());
      vifm.add(this._maximumPasswordAttemptsField, 12);
      UserAuthenticator[] registeredAuthenticators = this._security.getRegisteredUserAuthenticators();
      UserAuthenticator userAuthenticator = this._security.getUserAuthenticator();
      if (userAuthenticator == null) {
         for (int i = 0; i < registeredAuthenticators.length; i++) {
            try {
               if (registeredAuthenticators[i].isInitializationPossible()) {
                  userAuthenticator = registeredAuthenticators[i];
                  break;
               }
            } finally {
               continue;
            }
         }
      }

      this._securityTimeoutField = (TimeChoiceField)(new Object(OptionsResources.getString(1412), 268435456));
      vifm.add(this._securityTimeoutField, 12);
      this._passwordPromptOnInstallField = (BooleanChoiceField)(new Object(
         OptionsResources.getString(2047), 0, this._security.getPasswordRequiredForAppInstall(), 268435456
      ));
      vifm.add(this._passwordPromptOnInstallField, 12);
      vifm.add((Field)(new Object()));
      if (userAuthenticator != null) {
         this._userAuthenticatorField = (BooleanChoiceField)(new Object(OptionsResources.getString(1810), 2, userAuthenticator.isInitialized(), 268435456));
         this._userAuthenticatorField.setChangeListener(this);
         vifm.add(this._userAuthenticatorField);
         this._numericPasswordEntryField = (BooleanChoiceField)(new Object(OptionsResources.getString(1990), 2, this._security.getSmartPasswordEntry()));
         vifm.add(this._numericPasswordEntryField, 12);
         if (userAuthenticator instanceof Object) {
            this._userAuthenticatorCustomFieldProvider = (FieldProvider)userAuthenticator;
            this._userAuthenticatorCustomField = this._userAuthenticatorCustomFieldProvider.getField(null);
            vifm.add(this._userAuthenticatorCustomField, 12);
         }

         vifm.add((Field)(new Object()));
      }

      this._lockWhenHolsteredField = (BooleanChoiceField)(new Object(OptionsResources.getString(1419), 0, this._security.getLockWhenHolstered(), 268435456));
      if (InternalServices.isHolsterSupported()) {
         vifm.add(this._lockWhenHolsteredField);
      }

      this._allowOutgoingCallWhileLockedField = (BooleanChoiceField)(new Object(
         OptionsResources.getString(1420), 0, this._security.getAllowOutgoingCallWhileLocked(), 268435456
      ));
      vifm.add(this._allowOutgoingCallWhileLockedField);
      vifm.add((Field)(new Object()));
      this._enableContentCompressionField = (BooleanChoiceField)(new Object(OptionsResources.getString(1488), 2, PersistentContent.isCompressionEnabled()));
      this._enableContentCompressionField.setChangeListener(this);
      vifm.add(this._enableContentCompressionField);
      this._enableContentEncryptionField = (BooleanChoiceField)(new Object(
         OptionsResources.getString(1437), 2, PersistentContent.isEncryptionEnabled(), 268435456
      ));
      this._enableContentEncryptionField.setChangeListener(this);
      vifm.add(this._enableContentEncryptionField);
      this._contentEncryptionStrengthChoices = OptionsResources.getStringArray(2005);
      int encryptionStrength = 0;
      if (!this._security.isContentProtectionPending()) {
         encryptionStrength = PersistentContent.getEncryptionStrength();
      } else {
         encryptionStrength = this._security.getEncryptionStrength();
      }

      this._contentEncryptionStrengthField = (ObjectChoiceField)(new Object(
         OptionsResources.getString(2006), this._contentEncryptionStrengthChoices, encryptionStrength, 402653184
      ));
      this._contentEncryptionStrengthField.setChangeListener(this);
      vifm.add(this._contentEncryptionStrengthField, 12);
      this._minContentEncryptionStrength = 0;
      this._includeAddressBookInCPField = (BooleanChoiceField)(new Object(
         OptionsResources.getString(2016), 0, !this._security.getExcludeAddressBookFromContentProtection(), 268435456
      ));
      this._includeAddressBookInCPField.setChangeListener(this);
      vifm.add(this._includeAddressBookInCPField, 12);
      if (ITPolicyInternal.isITPolicyEnabled() || !InternalServices.isDeviceSecure()) {
         vifm.add((Field)(new Object()));
         vifm.add((Field)(new Object(OptionsResources.getString(1980))));
         this._securityITPolicyServiceColourField = new SecurityOptionsItem$SecurityServiceColourChoiceField(OptionsResources.getString(2008));
         this._securityITPolicyServiceColourField.setChangeListener(this);
         vifm.add(this._securityITPolicyServiceColourField, 12);
         this._securityOtherServiceColourField = new SecurityOptionsItem$SecurityServiceColourChoiceField(OptionsResources.getString(2009));
         this._securityOtherServiceColourField.setChangeListener(this);
         vifm.add(this._securityOtherServiceColourField, 12);
      }

      boolean fieldsAdded = false;
      fieldsAdded |= this.addITPolicyFields(mainScreen);
      fieldsAdded |= this.addApplicationDownloadControlFields(mainScreen);
      fieldsAdded |= this.addKeyRegenerationManager(mainScreen);
      fieldsAdded |= this.addSecurityIndicatorFields(mainScreen);
      fieldsAdded |= this.addUserAuthenticatorFields(mainScreen);
      if (fieldsAdded) {
         mainScreen.add((Field)(new Object()));
      }

      this.updateFields(true);
      UiApplication.getUiApplication().addGlobalEventListener(this);
   }

   private final boolean addITPolicyFields(MainScreen mainScreen) {
      String policyName = ITPolicy.getString(5);
      if (ITPolicyInternal.isITPolicyEnabled() && policyName != null) {
         mainScreen.add((Field)(new Object()));
         this._policyNameField = (EditField)(new Object(OptionsResources.getString(1421), policyName, 128, 9007203549708288L));
         mainScreen.add(this._policyNameField);
         this._policyDateTimeField = (DateField)(new Object(OptionsResources.getString(1422), ITPolicyInternal.getProcessedTimeStamp(), 9007199254741046L));
         mainScreen.add(this._policyDateTimeField);
         long itAdminTimeStamp = ITPolicyInternal.getITAdminTimeStamp();
         if (itAdminTimeStamp != 0) {
            itAdminTimeStamp = this.GMT_TIME + (long)ITPolicyInternal.getITAdminTimeStamp() * 1000;
            ((CalendarExtensions)this._calendar).setTimeLong(itAdminTimeStamp);
            this._policyTimestampField = (DateField)(new Object(
               OptionsResources.getString(1896), ((CalendarExtensions)this._calendar).getTimeLong(), 9007199254741046L
            ));
            mainScreen.add(this._policyTimestampField);
         }

         return true;
      } else {
         return false;
      }
   }

   private final boolean addUserAuthenticatorFields(MainScreen mainScreen) {
      UserAuthenticator userAuthenticator = this._security.getUserAuthenticator();
      if (userAuthenticator == null) {
         return false;
      }

      this._authenticatorVFM = (VerticalIndentFieldManager)(new Object());
      this._authenticatorVFM.add((Field)(new Object()));
      this._authenticatorVFM.add((Field)(new Object(OptionsResources.getString(1810))));
      this.addLabelAndValue(this._authenticatorVFM, OptionsResources.getString(1811), userAuthenticator.getName());
      boolean initialized = userAuthenticator.isInitialized();
      String[] yesNoArray = CommonResources.getYesNoArray(0);
      this._isInitializedValueField = this.addLabelAndValue(
         this._authenticatorVFM, OptionsResources.getString(1812), initialized ? yesNoArray[0] : yesNoArray[1]
      );
      Class authenticatorClass = userAuthenticator.getClassInAuthenticatorModule();
      int moduleHandle = CodeModuleManager.getModuleHandleForClass(authenticatorClass);
      this.addLabelAndValue(this._authenticatorVFM, OptionsResources.getString(1813), CodeModuleManager.getModuleName(moduleHandle));
      byte[] moduleHash = CodeModuleManager.getModuleHash(moduleHandle);
      StringBuffer sb = (StringBuffer)(new Object(moduleHash.length * 3));

      for (int i = 0; i < moduleHash.length; i++) {
         sb.append(NumberUtilities.intToUpperHexDigit(moduleHash[i] >>> 4));
         sb.append(NumberUtilities.intToUpperHexDigit(moduleHash[i]));
         if ((i & 1) == 1) {
            sb.append(' ');
         }
      }

      this.addLabelAndValue(this._authenticatorVFM, OptionsResources.getString(1472), sb.toString());
      mainScreen.add(this._authenticatorVFM);
      return true;
   }

   public final void updateUserAuthenticatorFields() {
      UserAuthenticator userAuthenticator = this._security.getUserAuthenticator();
      if (userAuthenticator != null && this._isInitializedValueField != null) {
         boolean initialized = userAuthenticator.isInitialized();
         String[] yesNoArray = CommonResources.getYesNoArray(0);
         this._isInitializedValueField.setText(initialized ? yesNoArray[0] : yesNoArray[1]);
      }
   }

   private final RichTextField addLabelAndValue(VerticalIndentFieldManager manager, String label, String value) {
      if (label != null && value != null) {
         LabelField labelField = (LabelField)(new Object(label, 64));
         RichTextField valueField = (RichTextField)(new Object(value, 9007199254740992L));
         manager.add(labelField, 12);
         manager.add(valueField, 24);
         return valueField;
      } else {
         return null;
      }
   }

   private final boolean addSecurityIndicatorFields(MainScreen mainScreen) {
      boolean secureGC = Memory.getSecureOldObjects();
      boolean contentProtectedMasterKeys = CryptoBlock.areMasterKeysEncrypted();
      boolean isDeviceSecure = InternalServices.isDeviceSecure();
      if (!secureGC && !contentProtectedMasterKeys) {
         return false;
      }

      mainScreen.add((Field)(new Object()));
      ObjectListField list = (ObjectListField)(new Object(6));
      if (secureGC) {
         list.insert(0, OptionsResources.getString(1457));
      }

      if (contentProtectedMasterKeys) {
         list.insert(0, OptionsResources.getString(1951));
      }

      int vendorId = Branding.getVendorId();
      if (vendorId == 1 || vendorId == 163 || vendorId == -1) {
         if (isDeviceSecure) {
            list.insert(0, OptionsResources.getString(1507));
         } else {
            list.insert(0, OptionsResources.getString(1508));
         }
      }

      mainScreen.add(list);
      return true;
   }

   private final boolean addApplicationDownloadControlFields(MainScreen mainScreen) {
      CodeSigningKey ADCKey = CodeModuleManager.getADCCodeSigningKey();
      if (ADCKey == null) {
         return false;
      }

      mainScreen.add((Field)(new Object()));
      mainScreen.add((Field)(new Object(OptionsResources.getString(1443))));
      String id = ADCKey.getSignerId();
      EditField idField = (EditField)(new Object(OptionsResources.getString(1444), id, 4, 9007203549708288L));
      mainScreen.add(idField);
      String description = ADCKey.getDescription();
      if (description.length() == 0) {
         description = OptionsResources.getString(1423);
      }

      EditField descriptionField = (EditField)(new Object(OptionsResources.getString(1445), description, 256, 9007203549708288L));
      mainScreen.add(descriptionField);
      Digest digest = (Digest)(new Object());
      digest.update(ADCKey.getPublicKey());
      byte[] hash = digest.getDigest();
      StringBuffer hashBuffer = (StringBuffer)(new Object());
      int offset = 0;

      while (offset + 1 < hash.length) {
         int i = (hash[offset] & 255) << 8 | hash[offset + 1] & 255;
         offset += 2;
         hashBuffer.append(Integer.toHexString(i));
         hashBuffer.append(' ');
      }

      EditField hashField = (EditField)(new Object(OptionsResources.getString(1446), hashBuffer.toString(), 256, 9007203549708288L));
      mainScreen.add(hashField);
      return true;
   }

   private final boolean addKeyRegenerationManager(MainScreen mainScreen) {
      if (!ActivationService.isActivationServiceAvailable()) {
         return false;
      }

      ActivationService service = ActivationService.getInstance();
      this._regenerationUIDs = service.getRegenerationUIDs();
      if (this._regenerationUIDs == null) {
         return false;
      }

      this._servicesList = (ObjectListField)(new Object(70));
      this._servicesList.setEmptyString(OptionsResources.getString(1815), 68);
      String[] s = new Object[this._regenerationUIDs[0].length];

      for (int i = this._regenerationUIDs[0].length - 1; i >= 0; i--) {
         StringBuffer buffer = (StringBuffer)(new Object(this._regenerationUIDs[1][i]));
         buffer.append(" [");
         buffer.append(this._regenerationUIDs[0][i]);
         buffer.append(']');
         byte algorithm = CryptoBlock.getKeyAlgorithmForUID(this._regenerationUIDs[0][i]);
         if (algorithm == 1) {
            buffer.append(OptionsResources.getString(1845));
         } else if (algorithm == 2) {
            buffer.append(OptionsResources.getString(1844));
         }

         s[i] = buffer.toString();
      }

      this._servicesList.set(s);
      mainScreen.add((Field)(new Object()));
      mainScreen.add((Field)(new Object(OptionsResources.getString(1814), 64)));
      mainScreen.add(this._servicesList);
      return true;
   }

   private final void updateFields(boolean resetSelectedTimeout) {
      this._passwordEnabledDisabledField.setEditable(!FIPSPolicy.isDevicePasswordRequired() && !FileSystemOptions.isDevicePasswordRequired());
      NumericChoiceField oldField = this._maximumPasswordAttemptsField;
      this._maximumPasswordAttemptsField = (NumericChoiceField)(new Object(OptionsResources.getString(1993), 3, ITPolicy.getInteger(22, 2, 10), 1));
      this._maximumPasswordAttemptsField.setSelectedValue(oldField.getSelectedValue());
      boolean hadFocus = oldField.isFocus();
      VerticalIndentFieldManager manager = (VerticalIndentFieldManager)oldField.getManager();
      int index = oldField.getIndex();
      manager.delete(oldField);
      manager.insert(this._maximumPasswordAttemptsField, index, 12);
      if (hadFocus) {
         this._maximumPasswordAttemptsField.setFocus();
      }

      if (this._userAuthenticatorField != null) {
         this._userAuthenticatorField.setEditable(!ITPolicy.getBoolean(24, 2, false) || !FIPSPolicy.isDevicePasswordRequired());
         this._numericPasswordEntryField.setEditable(!ITPolicy.getBoolean(24, 62, false));
      }

      this.populateTimeouts(resetSelectedTimeout);
      this._securityTimeoutField.setEditable(ITPolicy.getBoolean(12, true));
      this._passwordPromptOnInstallField.setAffirmative(this._security.getPasswordRequiredForAppInstall());
      this._passwordPromptOnInstallField.setEditable(!ITPolicy.getBoolean(24, 75, false));
      this._lockWhenHolsteredField.setAffirmative(this._security.getLockWhenHolstered());
      this._lockWhenHolsteredField.setEditable(!ITPolicy.getBoolean(24, 12, false));
      boolean isContentProtectionForced = ITPolicy.getInteger(24, 18, -1) != -1 && FIPSPolicy.isDevicePasswordRequired();
      if (isContentProtectionForced) {
         this._enableContentEncryptionField.setAffirmative(true);
         this._enableContentEncryptionField.setEditable(false);
      } else {
         this._enableContentEncryptionField.setEditable(true);
      }

      int currentContentEncryptionStrength = this._contentEncryptionStrengthField.getSelectedIndex() + this._minContentEncryptionStrength;
      int numStrengthChoices = this._contentEncryptionStrengthChoices.length;
      int minStrength = MathUtilities.clamp(0, ITPolicy.getInteger(24, 18, 0), numStrengthChoices - 1);
      numStrengthChoices -= minStrength;
      if (this._contentEncryptionStrengthField.getSize() != numStrengthChoices) {
         String[] choices = new Object[numStrengthChoices];
         System.arraycopy(this._contentEncryptionStrengthChoices, minStrength, choices, 0, numStrengthChoices);
         this._contentEncryptionStrengthField.setChoices(choices);
         this._contentEncryptionStrengthField.setSelectedIndex(Math.max(currentContentEncryptionStrength - minStrength, 0));
         this._minContentEncryptionStrength = minStrength;
      }

      if (ITPolicy.getBoolean(24, 55, false)) {
         this._includeAddressBookInCPField.setAffirmative(true);
         this._includeAddressBookInCPField.setEditable(false);
      } else {
         this._includeAddressBookInCPField.setEditable(true);
      }

      if (ITPolicy.getBoolean(24, 40, true)) {
         this._allowOutgoingCallWhileLockedField.setEditable(true);
      } else {
         this._allowOutgoingCallWhileLockedField.setAffirmative(false);
         this._allowOutgoingCallWhileLockedField.setEditable(false);
      }

      if (this._securityITPolicyServiceColourField != null) {
         if (ITPolicy.getString(24, 42) != null) {
            SecurityOptionsItem$SecurityServiceColour colour = new SecurityOptionsItem$SecurityServiceColour(
               this._security.getSecurityITPolicyServiceColour(), 2017
            );
            this._securityITPolicyServiceColourField.setChoices(new SecurityOptionsItem$SecurityServiceColour[]{colour});
            this._securityITPolicyServiceColourField.setSelectedIndex(0);
            this._securityITPolicyServiceColourField.setEditable(false);
            colour = new SecurityOptionsItem$SecurityServiceColour(this._security.getSecurityOtherServiceColour(), 2017);
            this._securityOtherServiceColourField.setChoices(new SecurityOptionsItem$SecurityServiceColour[]{colour});
            this._securityOtherServiceColourField.setSelectedIndex(0);
            this._securityOtherServiceColourField.setEditable(false);
         } else if (this._securityITPolicyServiceColourField.getSize() <= 1) {
            this._securityITPolicyServiceColourField.setChoices(_securityServiceColours);
            this._securityITPolicyServiceColourField
               .setSelectedIndex(new SecurityOptionsItem$SecurityServiceColour(this._security.getSecurityITPolicyServiceColour(), -1));
            this._securityITPolicyServiceColourField.setEditable(true);
            this._securityOtherServiceColourField.setChoices(_securityServiceColours);
            this._securityOtherServiceColourField
               .setSelectedIndex(new SecurityOptionsItem$SecurityServiceColour(this._security.getSecurityOtherServiceColour(), -1));
            this._securityOtherServiceColourField.setEditable(true);
         }
      }

      if (this._policyNameField != null) {
         String policyName = ITPolicy.getString(5);
         if (policyName == null) {
            policyName = OptionsResources.getString(1423);
         }

         this._policyNameField.setText(policyName);
         this._policyDateTimeField.setDate(ITPolicyInternal.getProcessedTimeStamp());
         if (this._policyTimestampField != null) {
            long itAdminTimeStamp = ITPolicyInternal.getITAdminTimeStamp();
            if (itAdminTimeStamp != 0) {
               itAdminTimeStamp = this.GMT_TIME + (long)ITPolicyInternal.getITAdminTimeStamp() * 1000;
               ((CalendarExtensions)this._calendar).setTimeLong(itAdminTimeStamp);
               this._policyTimestampField.setDate(((CalendarExtensions)this._calendar).getTimeLong());
            }
         }
      }
   }

   private final void populateTimeouts(boolean resetSelectedTimeout) {
      _timeouts = new long[]{
         60000L,
         120000L,
         300000L,
         600000L,
         900000L,
         1200000L,
         1800000L,
         3600000L,
         7200000L,
         14400000L,
         28800000L,
         2267000562651431168L,
         7308057372952396430L,
         7152109195346968832L,
         7308057372952370292L,
         7741536031140942156L,
         2050372863122755429L,
         -4200449974624517888L,
         5006258338766454984L,
         7567173426031518062L,
         4750206242868227103L,
         432373126602041205L,
         7278498720816366188L,
         -9027055691608570815L,
         5290360362767183955L,
         9113357136763027533L,
         1956822836369033216L,
         5566290981688444705L,
         5722985219443484674L,
         32695112136092258L,
         1171814885545486344L,
         99480991430394734L,
         3210402826027123L,
         2809775727514692360L,
         7885591021088698412L,
         7543930060546399505L,
         9970793982084468L,
         -5669180194766438136L,
         532340683585979502L,
         3339554495877161L,
         3192601033048277256L,
         7330017838163820645L,
         7152006481272710482L,
         5361645297973620L,
         7517745525184217352L,
         825403658278561083L,
         7089031285155070574L,
         7929559577212223627L,
         3339553515588738L,
         5513089889331855624L,
         8389714658460919041L,
         463674444593307497L,
         54832028285208576L,
         7297171483857535240L,
         32397236435575667L,
         2399788364068831496L,
         -3643288673933325369L,
         4685995608210120898L,
         6009278969704248436L,
         7108766536395390978L,
         576587630461406334L,
         166209198439891009L,
         5556531043435747703L,
         -3043235945572530577L,
         7791020116458028879L,
         2552284575466874967L,
         576487608788584448L,
         -5877351857253448894L,
         8722192720804184071L,
         1243565245540492620L,
         4758053422768917364L,
         825404143667738400L,
         7885596362877571139L,
         8029709262759592289L,
         7251480679412670846L,
         8388149410269757555L,
         38963446756734318L,
         4758053549171163656L,
         8233271061788781485L,
         4758054060437039281L,
         -120343297030067283L,
         -5953076875905990409L,
         -6881096244873658261L,
         -1770966851823570079L,
         8449606123652149090L,
         6997595864955711259L,
         4695048409894617133L,
         -4806157664959977611L
      };
      long initialTimeout = resetSelectedTimeout ? this._security.getTimeout() * 1000 : this._securityTimeoutField.getSelectedTimeInMillis();
      long maxTimeout = ITPolicy.getInteger(10, 60) * 60000;
      boolean dirtyTimeout = this._securityTimeoutField.isDirty();
      this._securityTimeoutField.setTimeChoicesIncludeMaxTimeChoice(_timeouts, 0, maxTimeout);
      this._securityTimeoutField.setSelectedTimeInMillis(initialTimeout, false);
      this._securityTimeoutField.setDirty(dirtyTimeout);
   }

   @Override
   protected final void initialize() {
      super.initialize();
      if (this._security == null) {
         this._security = Security.getInstance();
      }

      if (this._calendar == null) {
         this._calendar = Calendar.getInstance();
         this._calendar.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         this._calendar.set(5, 1);
         this._calendar.set(2, 0);
         this._calendar.set(1, 2000);
         this._calendar.set(10, 0);
         this._calendar.set(12, 0);
         this._calendar.set(13, 0);
         this._calendar.set(14, 0);
         this._calendar.set(9, 0);
         this.GMT_TIME = ((CalendarExtensions)this._calendar).getTimeLong();
      }
   }

   @Override
   protected final void addRepositoryVerbs(VerbToMenu verbToMenu, int instance) {
      VerbRepository verbRepository = VerbRepository.getVerbRepository(7371528850151275800L);
      Verb[] factoryVerbs = verbRepository.getVerbs(null);
      if (factoryVerbs != null && factoryVerbs.length > 0) {
         verbToMenu.addVerbs(factoryVerbs);
      }
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (this._masterRadioResetVerb != null) {
         verbToMenu.addVerb(this._masterRadioResetVerb);
      }

      if (this._security.isPasswordEnabled()) {
         if (this._changePasswordVerb == null) {
            this._changePasswordVerb = new SecurityOptionsItem$ChangePasswordVerb(this);
         }

         verbToMenu.addVerb(this._changePasswordVerb);
      }

      if (this._runSecurityTestsVerb == null) {
         this._runSecurityTestsVerb = new SecurityOptionsItem$RunSecurityTestsVerb();
      }

      verbToMenu.addVerb(this._runSecurityTestsVerb);
      if (this._wipeHandheldVerb == null) {
         this._wipeHandheldVerb = (Verb)(new Object(0));
      }

      verbToMenu.addVerb(this._wipeHandheldVerb);
      Field f = UiApplication.getUiApplication().getActiveScreen().getLeafFieldWithFocus();
      if (f == this._servicesList) {
         int i = this._servicesList.getSelectedIndex();
         if (i != -1) {
            String uid = this._regenerationUIDs[0][i];
            SecurityOptionsItem$ReKeyVerb rekeyVerb = new SecurityOptionsItem$ReKeyVerb(uid);
            verbToMenu.setDefaultVerb(rekeyVerb);
            verbToMenu.addVerb(rekeyVerb);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final boolean save() {
      boolean validPassword = false;
      boolean var6 = false /* VF: Semaphore variable */;

      int otherServiceColour;
      label360: {
         boolean enablePassword;
         label359: {
            label358: {
               label357: {
                  label356: {
                     label355: {
                        try {
                           var6 = true;
                           if (this._enableContentEncryptionField.isDirty() || this._contentEncryptionStrengthField.isDirty()) {
                              this._security
                                 .setContentProtection(
                                    this._enableContentEncryptionField.isAffirmative(),
                                    this._contentEncryptionStrengthField.getSelectedIndex() + this._minContentEncryptionStrength
                                 );
                           }

                           if (this._passwordEnabledDisabledField.isDirty()) {
                              enablePassword = this._passwordEnabledDisabledField.isAffirmative();
                              if (this._security.isPasswordEnabled() != enablePassword) {
                                 if (!SecurityDialog.changePassword(null, false, true, !enablePassword, '\u0000')) {
                                    otherServiceColour = 0;
                                    var6 = false;
                                    break label360;
                                 }

                                 validPassword = true;
                                 RibbonLauncher launcher = RibbonLauncher.getInstance();
                                 if (launcher != null) {
                                    launcher.updateRegisteredAction("net.rim.LockSystem");
                                 }
                              }
                           }

                           if (!this._securityTimeoutField.isDirty()
                              && !this._passwordPromptOnInstallField.isDirty()
                              && !this._lockWhenHolsteredField.isDirty()
                              && !this._enableContentEncryptionField.isDirty()
                              && !this._contentEncryptionStrengthField.isDirty()
                              && !this._includeAddressBookInCPField.isDirty()
                              && !this._enableContentCompressionField.isDirty()
                              && !this._allowOutgoingCallWhileLockedField.isDirty()
                              && (this._userAuthenticatorField == null || !this._userAuthenticatorField.isDirty())
                              && (this._userAuthenticatorCustomField == null || !this._userAuthenticatorCustomField.isDirty())
                              && (this._numericPasswordEntryField == null || !this._numericPasswordEntryField.isDirty())
                              && (this._maximumPasswordAttemptsField == null || !this._maximumPasswordAttemptsField.isDirty())
                              && (this._securityITPolicyServiceColourField == null || !this._securityITPolicyServiceColourField.isDirty())
                              && (this._securityOtherServiceColourField == null || !this._securityOtherServiceColourField.isDirty())) {
                              enablePassword = super.save();
                              var6 = false;
                              break label358;
                           }

                           if (!validPassword && !SecurityDialog.challengeUser(null, false, true, '\u0000', true)) {
                              enablePassword = (boolean)0;
                              var6 = false;
                              break label359;
                           }

                           if (this._userAuthenticatorField != null && this._userAuthenticatorField.isDirty()) {
                              UserAuthenticator userAuthenticator = this._security.getUserAuthenticator();
                              if (userAuthenticator != null && !this._userAuthenticatorField.isAffirmative() && userAuthenticator.isInitialized()) {
                                 this._security.uninitializeUserAuthenticator();
                              } else if (userAuthenticator == null && this._userAuthenticatorField.isAffirmative()) {
                                 if (!SecurityDialog.initializeAuthenticator(false)) {
                                    otherServiceColour = 0;
                                    var6 = false;
                                    break label357;
                                 }

                                 this.updateUserAuthenticatorFields();
                              }
                           }

                           if (this._userAuthenticatorCustomField != null && this._userAuthenticatorCustomField.isDirty()) {
                              UserAuthenticator userAuthenticator = this._security.getUserAuthenticator();
                              if (userAuthenticator instanceof Object) {
                                 this._userAuthenticatorCustomFieldProvider = (FieldProvider)userAuthenticator;
                              }

                              if (!this._userAuthenticatorCustomFieldProvider.grabDataFromField(this._userAuthenticatorCustomField, null)) {
                                 otherServiceColour = 0;
                                 var6 = false;
                                 break label356;
                              }
                           }

                           if (this._maximumPasswordAttemptsField != null && this._maximumPasswordAttemptsField.isDirty()) {
                              enablePassword = (boolean)this._maximumPasswordAttemptsField.getSelectedValue();
                              this._security.setMaxPasswordAttempts(enablePassword);
                           }

                           if (this._securityTimeoutField.isDirty()) {
                              enablePassword = (boolean)((int)(this._securityTimeoutField.getSelectedTimeInMillis() / 1000));
                              if (!this._security.setTimeout(enablePassword)) {
                                 Dialog.alert(OptionsResources.getString(1413));
                                 this.populateTimeouts(true);
                                 otherServiceColour = 0;
                                 var6 = false;
                                 break label355;
                              }
                           }

                           if (this._passwordPromptOnInstallField.isDirty()) {
                              this._security.setPasswordRequiredForAppInstall(this._passwordPromptOnInstallField.isAffirmative());
                           }

                           if (this._lockWhenHolsteredField.isDirty()) {
                              this._security.setLockWhenHolstered(this._lockWhenHolsteredField.isAffirmative());
                           }

                           if (this._allowOutgoingCallWhileLockedField.isDirty()) {
                              this._security.setAllowOutgoingCallWhileLocked(this._allowOutgoingCallWhileLockedField.isAffirmative());
                              PersistentContent.requestReEncode();
                           }

                           if (this._enableContentEncryptionField.isDirty() || this._enableContentCompressionField.isDirty()) {
                              PersistentContentInternal.setContentCompression(this._enableContentCompressionField.isAffirmative());
                           }

                           if (this._numericPasswordEntryField != null && this._numericPasswordEntryField.isDirty()) {
                              this._security.setSmartPasswordEntry(this._numericPasswordEntryField.isAffirmative());
                           }

                           if (this._includeAddressBookInCPField.isDirty()) {
                              this._security.setExcludeAddressBookFromContentProtection(!this._includeAddressBookInCPField.isAffirmative());
                              PersistentContent.requestReEncode();
                           }

                           if (this._securityITPolicyServiceColourField != null
                              && (this._securityITPolicyServiceColourField.isDirty() || this._securityOtherServiceColourField.isDirty())) {
                              enablePassword = (boolean)((SecurityOptionsItem$SecurityServiceColour)this._securityITPolicyServiceColourField
                                    .getChoice(this._securityITPolicyServiceColourField.getSelectedIndex()))
                                 .getColour();
                              otherServiceColour = ((SecurityOptionsItem$SecurityServiceColour)this._securityOtherServiceColourField
                                    .getChoice(this._securityOtherServiceColourField.getSelectedIndex()))
                                 .getColour();
                              this._security.setSecurityServiceColours(enablePassword, otherServiceColour);
                           }

                           enablePassword = super.save();
                           var6 = false;
                        } finally {
                           if (var6) {
                              this._security.clearContentProtection();
                           }
                        }

                        this._security.clearContentProtection();
                        return enablePassword;
                     }

                     this._security.clearContentProtection();
                     return (boolean)otherServiceColour;
                  }

                  this._security.clearContentProtection();
                  return (boolean)otherServiceColour;
               }

               this._security.clearContentProtection();
               return (boolean)otherServiceColour;
            }

            this._security.clearContentProtection();
            return enablePassword;
         }

         this._security.clearContentProtection();
         return enablePassword;
      }

      this._security.clearContentProtection();
      return (boolean)otherServiceColour;
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean close = super.confirm(verb, context);
      if (close) {
         UiApplication.getUiApplication().removeGlobalEventListener(this);
      }

      return close;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._enableContentEncryptionField) {
         if (this._enableContentEncryptionField.isAffirmative() && !this._passwordEnabledDisabledField.isAffirmative()) {
            if (SimpleChoiceDialog.askYesNoQuestion(OptionsResources.getString(1441))) {
               this._passwordEnabledDisabledField.setAffirmative(true);
               this._passwordEnabledDisabledField.setDirty(true);
               return;
            }

            this._enableContentEncryptionField.setAffirmative(false);
            this._enableContentEncryptionField.setDirty(true);
            return;
         }
      } else if (field == this._userAuthenticatorField) {
         if (this._userAuthenticatorField.isAffirmative() && !this._passwordEnabledDisabledField.isAffirmative()) {
            if (SimpleChoiceDialog.askYesNoQuestion(OptionsResources.getString(1899))) {
               this._passwordEnabledDisabledField.setAffirmative(true);
               this._passwordEnabledDisabledField.setDirty(true);
               return;
            }

            this._userAuthenticatorField.setAffirmative(false);
            this._userAuthenticatorField.setDirty(true);
            return;
         }
      } else if (field == this._passwordEnabledDisabledField && !this._passwordEnabledDisabledField.isAffirmative()) {
         boolean contentProtectionEnabled = this._enableContentEncryptionField.isAffirmative();
         boolean userAuthenticatorEnabled = this._userAuthenticatorField != null && this._userAuthenticatorField.isAffirmative();
         if (contentProtectionEnabled || userAuthenticatorEnabled) {
            String message;
            if (contentProtectionEnabled && userAuthenticatorEnabled) {
               message = OptionsResources.getString(1901);
            } else if (contentProtectionEnabled) {
               message = OptionsResources.getString(1442);
            } else {
               message = OptionsResources.getString(1900);
            }

            if (SimpleChoiceDialog.askYesNoQuestion(message)) {
               if (contentProtectionEnabled) {
                  this._enableContentEncryptionField.setAffirmative(false);
                  this._enableContentEncryptionField.setDirty(true);
               }

               if (userAuthenticatorEnabled) {
                  this._userAuthenticatorField.setAffirmative(false);
                  this._userAuthenticatorField.setDirty(true);
                  return;
               }
            } else {
               this._passwordEnabledDisabledField.setAffirmative(true);
               this._passwordEnabledDisabledField.setDirty(true);
            }
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L || guid == -594020114676189989L) {
         this._passwordEnabledDisabledField.setChangeListener(null);
         if (FIPSPolicy.isDevicePasswordRequired() || FileSystemOptions.isDevicePasswordRequired()) {
            this._passwordEnabledDisabledField.setAffirmative(true);
         }

         this._passwordEnabledDisabledField.setDirty(false);
         this._passwordEnabledDisabledField.setChangeListener(this);
         this.updateFields(true);
      }
   }
}
