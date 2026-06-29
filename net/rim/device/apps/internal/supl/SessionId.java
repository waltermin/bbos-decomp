package net.rim.device.apps.internal.supl;

final class SessionId {
   SetSessionId setSessionId;
   SlpSessionId slpSessionId;
   byte optionals;
   static final byte SUPL_MASK_SET_SESSION_ID = 2;
   static final byte SUPL_MASK_SLP_SESSION_ID = 1;
   static final byte SUPL_SESSION_ID_OPTIONALS_BIT_SIZE = 2;

   SessionId() {
   }

   SessionId(SetSessionId setSessionId) {
      this.optionals = 2;
      this.setSessionId = setSessionId;
   }

   final void decode(Nibbler nib) {
      this.optionals = (byte)nib.getBitsLarge(2);
      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         this.setSessionId = new SetSessionId();
         this.setSessionId.decode(nib);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         this.slpSessionId = new SlpSessionId();
         this.slpSessionId.decode(nib);
      }
   }

   final void encode(Stuffer stuff) {
      stuff.putBits(this.optionals, 2);
      if (Nibbler.IsBitSet(this.optionals, (byte)2)) {
         this.setSessionId.encode(stuff);
      }

      if (Nibbler.IsBitSet(this.optionals, (byte)1)) {
         this.slpSessionId.encode(stuff);
      }
   }

   final boolean equals(SessionId sessionId) {
      boolean retVal = true;
      if ((this.optionals & 2) == 2) {
         retVal = this.setSessionId.equals(sessionId.setSessionId);
      }

      if (retVal && (this.optionals & 1) == 1) {
         retVal = this.slpSessionId.equals(sessionId.slpSessionId);
      }

      return retVal;
   }

   final void addSlpSessionId(SlpSessionId slpSessionId) {
      this.optionals = (byte)(this.optionals | 1);
      this.slpSessionId = slpSessionId;
   }

   final void print() {
      System.out.println("Session ID: ");
      System.out.println(((StringBuffer)(new Object("Optionals: "))).append(this.optionals).toString());
      if ((this.optionals & 2) == 2) {
         this.setSessionId.print();
      }

      if ((this.optionals & 1) == 1) {
         this.slpSessionId.print();
      }
   }
}
