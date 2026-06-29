package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.internal.phone.pattern.WorldPhoneInfo;

class SmartDialingOption$1 extends SmartDialingOption$CountryCodeChoiceField {
   private final SmartDialingOption this$0;

   SmartDialingOption$1(SmartDialingOption _1, String x0, int x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   protected void notifyCountryCodeUpdated(int newCC) {
      this.setDirty(true);
      int nnl = WorldPhoneInfo.getDefaultNationalPhoneNumberLength(newCC);
      if (nnl > 0 && nnl >= 0 && nnl <= 32 && this.this$0._nationalNumberLengthField != null) {
         this.this$0._nationalNumberLengthField.setSelectedIndex(nnl - 0);
         this.this$0._nationalNumberLengthField.setDirty(true);
      }
   }
}
