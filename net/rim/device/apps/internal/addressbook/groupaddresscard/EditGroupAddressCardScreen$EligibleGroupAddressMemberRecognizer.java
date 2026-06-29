package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.apps.api.framework.model.Recognizer;

final class EditGroupAddressCardScreen$EligibleGroupAddressMemberRecognizer implements Recognizer {
   @Override
   public final boolean recognize(Object o) {
      if (o instanceof Object) {
         Object[] members = GroupAddressCardMember.getGroupAddressableRIMModels(o, (byte)0);
         if (members != null && members.length > 0) {
            return true;
         }

         members = GroupAddressCardMember.getGroupAddressableRIMModels(o, (byte)2);
         if (members != null && members.length > 0) {
            return true;
         }

         members = GroupAddressCardMember.getGroupAddressableRIMModels(o, (byte)1);
         if (members != null && members.length > 0) {
            return true;
         }
      }

      return false;
   }
}
