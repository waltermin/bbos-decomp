package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;

class ContactTreeField$SortedBranch extends Branch {
   private static final byte TYPE_SYMBOL = 1;
   private static final byte TYPE_DIGIT = 2;
   private static final byte TYPE_LETTER = 3;

   private byte getType(char ch) {
      byte type = 3;
      if (!CharacterUtilities.isSymbol(ch) && ch != '@') {
         if (CharacterUtilities.isDigit(ch)) {
            type = 2;
         }

         return type;
      } else {
         return 1;
      }
   }

   private int compareTypes(byte typeKey, byte typeContact) {
      int result = 0;
      switch (typeKey) {
         case 1:
         default:
            return typeContact == 2 ? -1 : 1;
         case 2:
            return typeContact == 1 ? 1 : -1;
         case 3:
            result = 1;
         case 0:
            return result;
      }
   }

   int compare(PeerContact key, Field field) {
      if (!(field instanceof ContactTreeField$ContactLeaf)) {
         return 1;
      }

      ContactTreeField$ContactLeaf contactLeaf = (ContactTreeField$ContactLeaf)field;
      PeerContact contact = contactLeaf.getContact();
      String nameKey = key.getDisplayName();
      String nameContact = contact.getDisplayName();
      byte typeKey = this.getType(nameKey.charAt(0));
      byte typeContact = this.getType(nameContact.charAt(0));
      int result = typeKey == typeContact ? StringUtilities.compareToIgnoreCase(nameKey, nameContact) : this.compareTypes(typeKey, typeContact);
      return result == 0 ? key.getIdHash() - contact.getIdHash() : result;
   }

   int binarySearch(PeerContact key) {
      int low = 1;
      int high = this.getFieldCount() - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         int result = this.compare(key, this.getField(mid));
         if (result < 0) {
            high = mid - 1;
         } else {
            if (result <= 0) {
               return mid;
            }

            low = mid + 1;
         }
      }

      return -(low + 1);
   }

   @Override
   public void add(Field field) {
      if (!(field instanceof ContactTreeField$ContactLeaf)) {
         super.add(field);
      } else {
         ContactTreeField$ContactLeaf contactLeaf = (ContactTreeField$ContactLeaf)field;
         PeerContact contact = contactLeaf.getContact();
         int index = this.binarySearch(contact);
         if (index <= 0) {
            super.insert(contactLeaf, -index - 1);
            return;
         }
      }
   }
}
