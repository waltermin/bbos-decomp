package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class PhoneNumberInput$AddDelayVerb extends Verb {
   private int _type;
   private final PhoneNumberInput this$0;
   static final int STOP;
   static final int PAUSE;

   public PhoneNumberInput$AddDelayVerb(PhoneNumberInput _1, int type) {
      super(16864261);
      this.this$0 = _1;
      this._type = type;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0._fieldState = 1;
      this.this$0._fieldStateChangeLength = this.this$0._bufferedString.length() + 1;
      if (this._type == 0) {
         this.this$0.echo('\uf402', 0);
         return null;
      } else {
         this.this$0.echo('\uf3fe', 0);
         return null;
      }
   }

   @Override
   public final String toString() {
      switch (this._type) {
         case -1:
            return "";
         case 0:
         default:
            return PhoneResources.getString(446);
         case 1:
            return PhoneResources.getString(445);
      }
   }
}
