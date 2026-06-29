package net.rim.device.api.smartcard;

class SmartCardCache$Data {
   SmartCardID _id = null;
   AnswerToReset _atr = null;
   int _maxLoginAttempts = -1;
   int _remainingLoginAttempts = -1;

   private SmartCardCache$Data() {
   }

   SmartCardCache$Data(SmartCardCache$1 x0) {
      this();
   }
}
