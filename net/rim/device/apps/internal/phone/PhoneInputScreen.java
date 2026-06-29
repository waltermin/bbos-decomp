package net.rim.device.apps.internal.phone;

interface PhoneInputScreen {
   long MIN_TIME_BEFORE_CONTINUATION_ACTION;

   void onRollOffPhoneNumberInput();

   void onInputFieldCleared();

   void onInputFieldNonEmpty(int var1);
}
