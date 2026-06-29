package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.apps.internal.phone.pattern.WorldPhoneInfo;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PhoneNumberEditField;
import net.rim.vm.Array;

final class SmartDialingOption extends VoiceOptionsListItem {
   private SmartDialingOption$CountryCodeChoiceField _countryCodeField;
   private PhoneNumberEditField _areaCodeField;
   private NumericChoiceField _nationalNumberLengthField;
   private PhoneNumberEditField _defaultPhoneNumberField;
   private ChoiceField _tonesChoiceField;
   private ChoiceField _stdTonesChoiceField;
   private BooleanChoiceField _autoAppendNddField;
   private ChoiceField _extLengthField;
   private int _networkType = RadioInfo.getNetworkType();
   private int _origCountryCode;
   private String _origAreaCode;
   private int _origNationalNumberLength;
   private String _origDefaultPhoneNumber;
   private Verb _resetVerb = new SmartDialingOption$ResetSmartDialingVerb(this);
   private static final int NNL_MIN = 0;
   private static final int NNL_MAX = 32;
   private static final int MAX_AREA_CODE_LENGTH = 40;
   private static final int MIN_EXT_LENGTH = 2;
   private static final int MAX_EXT_LENGTH = 6;
   private static final String[] ADDITIONAL_TONES = new String[]{"!", ",", ",,", ",,,", ""};

   public SmartDialingOption(Object context) {
      super(PhoneResources.getString(192), context);
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      this._origCountryCode = smartDialingOptions.getCountryCode();
      this._countryCodeField = new SmartDialingOption$1(this, PhoneResources.getString(193), this._origCountryCode);
      this._origAreaCode = smartDialingOptions.getAreaCode();
      this._areaCodeField = new SmartDialingOption$RestrictedPhoneNumberEditField(PhoneResources.getString(194), this._origAreaCode);
      this._origDefaultPhoneNumber = smartDialingOptions.getCorporatePhoneNumber();
      ContextObject context = (ContextObject)(new Object());
      PhoneUtilities.setPrivateFlag(context, 80);
      this._defaultPhoneNumberField = (PhoneNumberEditField)(new Object(PhoneResources.getString(307), this._origDefaultPhoneNumber, context));
      this._origNationalNumberLength = Math.max(0, Math.min(32, smartDialingOptions.getNationalPhoneNumberLength()));
      this._nationalNumberLengthField = (NumericChoiceField)(new Object(PhoneResources.getString(149), 0, 32, 1, this._origNationalNumberLength));
      this._autoAppendNddField = null;
      if (!PhoneUtilities.canDialPlus()) {
         char[] ndd = WorldPhoneInfo.getNationalDialingDigits(this._origCountryCode);
         if (ndd != null && ndd.length > 0) {
            this._autoAppendNddField = (BooleanChoiceField)(new Object(
               MessageFormat.format(PhoneResources.getString(2), new Object[]{new Object(ndd)}), 0, smartDialingOptions.autoAppendNDDForDialing()
            ));
         }
      }

      this._tonesChoiceField = getTonesChoiceField(
         getAdditionalTonesChoice(smartDialingOptions.getAdditionalTonesForCorporateExtensions(this._networkType)), true
      );
      this._stdTonesChoiceField = getTonesChoiceField(getAdditionalTonesChoice(smartDialingOptions.getAdditionalTonesForExtensions(this._networkType)), false);
      this._extLengthField = getExtensionLengthChoiceField(smartDialingOptions.getCorporateExtensionLength());
      screen.add(this._countryCodeField);
      screen.add(this._areaCodeField);
      screen.add(this._nationalNumberLengthField);
      if (this._autoAppendNddField != null) {
         screen.add(this._autoAppendNddField);
      }

      screen.add((Field)(new Object()));
      screen.add((Field)(new Object(PhoneResources.getString(6047), null, null, null, 36028797018963968L)));
      screen.add(this._defaultPhoneNumberField);
      screen.add(this._tonesChoiceField);
      screen.add(this._extLengthField);
      screen.add((Field)(new Object()));
      screen.add((Field)(new Object(PhoneResources.getString(6031), null, null, null, 36028797018963968L)));
      screen.add(this._stdTonesChoiceField);
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      verbToMenu.addVerb(this._resetVerb);
      super.addScreenVerbs(verbToMenu, instance);
   }

   @Override
   protected final boolean save() {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      boolean commitRequired = false;
      String newValue = null;
      if (this._countryCodeField.isDirty()) {
         int newCC = this._countryCodeField.getCountryCode();
         if (this._origCountryCode != newCC) {
            smartDialingOptions.setCountryCode(newCC);
            commitRequired = true;
         }
      }

      if (this._areaCodeField.isDirty()) {
         newValue = this._areaCodeField.getText().trim();
         if (this.valueChanged(this._origAreaCode, newValue)) {
            smartDialingOptions.setAreaCode(newValue);
            commitRequired = true;
         }
      }

      if (this._nationalNumberLengthField.isDirty()) {
         int newLength = 0 + this._nationalNumberLengthField.getSelectedIndex();
         if (newLength != this._origNationalNumberLength) {
            smartDialingOptions.setNationalPhoneNumberLength(newLength);
            commitRequired = true;
         }
      }

      if (this._defaultPhoneNumberField.isDirty()) {
         newValue = this._defaultPhoneNumberField.getText().trim();
         if (this.valueChanged(this._origDefaultPhoneNumber, newValue)) {
            smartDialingOptions.setCorporatePhoneNumber(newValue);
            commitRequired = true;
         }
      }

      if (this._tonesChoiceField.isDirty()) {
         smartDialingOptions.setAdditionalTonesForCorporateExtensions(this._networkType, ADDITIONAL_TONES[this._tonesChoiceField.getSelectedIndex()]);
         commitRequired = true;
      }

      if (this._stdTonesChoiceField.isDirty()) {
         smartDialingOptions.setAdditionalTonesForExtensions(this._networkType, ADDITIONAL_TONES[this._stdTonesChoiceField.getSelectedIndex()]);
         commitRequired = true;
      }

      if (this._autoAppendNddField != null && this._autoAppendNddField.isDirty()) {
         smartDialingOptions.setAutoAppendNDDForDialing(this._autoAppendNddField.isAffirmative());
         commitRequired = true;
      }

      if (this._extLengthField != null && this._extLengthField.isDirty()) {
         int extIndex = this._extLengthField.getSelectedIndex();
         int extLength;
         if (extIndex == 0) {
            extLength = 0;
         } else {
            extLength = 2 + extIndex - 1;
         }

         smartDialingOptions.setCorporateExtensionLength(extLength);
         commitRequired = true;
         if (extLength == 0) {
            smartDialingOptions.setCorporateExtensionLengthExclusions(null);
         }
      }

      if (commitRequired) {
         smartDialingOptions.commit();
      }

      return super.save();
   }

   private static final int getAdditionalTonesChoice(String tones) {
      for (int i = ADDITIONAL_TONES.length - 1; i >= 0; i--) {
         if (tones.equals(ADDITIONAL_TONES[i])) {
            return i;
         }
      }

      return 0;
   }

   private static final ObjectChoiceField getTonesChoiceField(int choice, boolean allowDirectDial) {
      String[] additionalTonesLabels = new Object[ADDITIONAL_TONES.length];
      String fmtString = PhoneResources.getString(309);
      Object[] fmtParams = new Object[1];
      int idx = 0;
      additionalTonesLabels[idx++] = PhoneResources.getString(308);
      fmtParams[0] = new Object(3);
      additionalTonesLabels[idx++] = MessageFormat.format(fmtString, fmtParams);
      fmtParams[0] = new Object(5);
      additionalTonesLabels[idx++] = MessageFormat.format(fmtString, fmtParams);
      fmtParams[0] = new Object(7);
      additionalTonesLabels[idx++] = MessageFormat.format(fmtString, fmtParams);
      if (allowDirectDial) {
         additionalTonesLabels[idx++] = PhoneResources.getString(6128);
      }

      Array.resize(additionalTonesLabels, idx);
      return (ObjectChoiceField)(new Object(PhoneResources.getString(311), additionalTonesLabels, choice));
   }

   private static final ObjectChoiceField getExtensionLengthChoiceField(int length) {
      int choice;
      if (length == 0) {
         choice = 0;
      } else {
         choice = Math.max(2, Math.min(6, length)) - 2 + 1;
      }

      String[] lengthsLabels = new Object[6];
      lengthsLabels[0] = PhoneResources.getString(6048);
      String fmtString = PhoneResources.getString(6049);
      Object[] fmtParams = new Object[1];

      for (int len = 2; len <= 6; len++) {
         fmtParams[0] = Integer.toString(len);
         lengthsLabels[len - 2 + 1] = MessageFormat.format(fmtString, fmtParams);
      }

      return (ObjectChoiceField)(new Object(PhoneResources.getString(6050), lengthsLabels, choice));
   }

   private final boolean valueChanged(String originalNumber, String newNumber) {
      if (originalNumber != null) {
         int origLength = originalNumber.length();
         int newLength = newNumber.length();
         return origLength != newLength ? true : !originalNumber.equals(newNumber);
      } else {
         return true;
      }
   }

   @Override
   public final int getOptionsScreenOrder() {
      return 8000;
   }
}
