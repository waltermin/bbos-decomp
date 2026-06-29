package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.Locale;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class SmartDialingOption$ResetSmartDialingVerb extends Verb {
   private final SmartDialingOption this$0;

   public SmartDialingOption$ResetSmartDialingVerb(SmartDialingOption _1) {
      super(200965, PhoneResources.getResourceBundle(), 6087);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      int cc = SmartDialingOptions.getCountryCodeFromIsoName(Locale.getDefaultCountry());
      int nnl = SmartDialingOptions.getDefaultNationalPhoneNumberLength(cc);
      String areaCode = null;
      if (cc == -2) {
         cc = SmartDialingOptions.getCountryCodeFromITPolicy();
         nnl = SmartDialingOptions.getNationalPhoneNumberLengthFromITPolicy();
         areaCode = SmartDialingOptions.getAreaCodeFromITPolicy();
      }

      if (cc == -2) {
         int newCC = SmartDialingOptions.getCountryCodeFromDevicePhoneNumber();
         if (newCC > 0) {
            cc = newCC;
            nnl = SmartDialingOptions.getDefaultNationalPhoneNumberLength(cc);
         }
      }

      this.this$0._countryCodeField.reset(cc);
      this.this$0._countryCodeField.setMuddy(true);
      this.this$0._countryCodeField.setDirty(true);
      if (nnl >= 0 && nnl <= 32) {
         this.this$0._nationalNumberLengthField.setSelectedIndex(nnl - 0);
         this.this$0._nationalNumberLengthField.setDirty(true);
      }

      this.this$0._areaCodeField.setText(areaCode == null ? "" : areaCode);
      this.this$0._areaCodeField.setDirty(true);
      if (this.this$0._autoAppendNddField != null) {
         this.this$0._autoAppendNddField.setAffirmative(SmartDialingOptions.getDefaultAutoAppendNDDForDialing());
         this.this$0._autoAppendNddField.setDirty(true);
      }

      return null;
   }
}
