package net.rim.device.apps.internal.options.items;

import java.util.Vector;
import net.rim.device.internal.system.SIMPhoneNumberReader$PhoneNumberList;

final class SIMCardOptionsItem$1 implements SIMPhoneNumberReader$PhoneNumberList {
   private final Vector val$phoneNumberList;
   private final SIMCardOptionsItem this$0;

   SIMCardOptionsItem$1(SIMCardOptionsItem _1, Vector _2) {
      this.this$0 = _1;
      this.val$phoneNumberList = _2;
   }

   @Override
   public final void addPhoneNumber(int recordID, String name, String phoneNumber, byte extRecID, byte tonAndNPI) {
      StringBuffer sbuf = new StringBuffer(phoneNumber);
      this.val$phoneNumberList.addElement(sbuf.toString());
   }
}
