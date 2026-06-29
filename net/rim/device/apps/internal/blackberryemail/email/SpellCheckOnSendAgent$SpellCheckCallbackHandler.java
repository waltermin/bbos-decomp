package net.rim.device.apps.internal.blackberryemail.email;

class SpellCheckOnSendAgent$SpellCheckCallbackHandler implements Runnable {
   private int _spellCheckBitFields;
   private final SpellCheckOnSendAgent this$0;

   SpellCheckOnSendAgent$SpellCheckCallbackHandler(SpellCheckOnSendAgent _1, int spellCheckBitFields) {
      this.this$0 = _1;
      this._spellCheckBitFields = spellCheckBitFields;
   }

   @Override
   public void run() {
      if ((this._spellCheckBitFields & 3) == 0) {
         this.this$0._currentField = null;
         this.this$0.resumeSpellCheck(true);
      } else {
         this.this$0.handleTermination(this._spellCheckBitFields);
      }
   }
}
