package net.rim.device.apps.internal.phone.model;

import net.rim.device.apps.api.framework.verb.RIMModelFactoryCreateVerb;

final class AddPhoneNumberVerb extends RIMModelFactoryCreateVerb {
   public AddPhoneNumberVerb(PhoneNumberModelFactory factory, int ordering, int typeIndex) {
      super(factory, ordering, 2699923441625099942L, "net.rim.device.apps.internal.resource.Phone", 0);
      switch (typeIndex) {
         case 1:
         default:
            super._rbKey = 1100;
            return;
         case 2:
            super._rbKey = 6083;
            return;
         case 3:
            super._rbKey = 1101;
            return;
         case 4:
            super._rbKey = 6084;
            return;
         case 5:
            super._rbKey = 1102;
            return;
         case 6:
            super._rbKey = 1103;
            return;
         case 7:
            super._rbKey = 1104;
            return;
         case 8:
            super._rbKey = 6082;
         case 0:
      }
   }
}
