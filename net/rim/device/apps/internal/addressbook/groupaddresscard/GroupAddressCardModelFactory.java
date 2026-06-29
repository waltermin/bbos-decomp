package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class GroupAddressCardModelFactory extends RIMModelFactory {
   private Verb[] _groupAddressBookVerbs;

   @Override
   public final Object createInstance(Object initialData) {
      GroupAddressCardModelImpl groupAddressCard = null;
      if (ContextObject.getFlag(initialData, 18) && ContextObject.getFlag(initialData, 19)) {
         try {
            SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
            if (syncBuffer == null) {
               return null;
            }

            String groupName = syncBuffer.getString(32, true);
            if (groupName == null) {
               return null;
            }

            groupAddressCard = new GroupAddressCardModelImpl();
            groupAddressCard.setName(groupName);
            int uid = syncBuffer.getUID();
            groupAddressCard.setUID(uid != 0 ? uid : UIDGenerator.getUID());
            int pos = syncBuffer.getPosition();
            int numMembers = syncBuffer.getInt(46, true);
            syncBuffer.setPosition(pos);

            for (int i = 0; i < numMembers; i++) {
               byte[] groupMemberInfo = syncBuffer.getBytes(52, true);
               if (groupMemberInfo != null && groupMemberInfo.length >= 5) {
                  DataBuffer infoBuff = (DataBuffer)(new Object(groupMemberInfo, 0, groupMemberInfo.length, false));
                  int addressCardUid = infoBuff.readInt();
                  byte index = (byte)(infoBuff.readByte() - 1);
                  int syncFieldId = infoBuff.readInt();
                  if (syncFieldId == 0) {
                     syncFieldId = 1;
                  }

                  GroupAddressCardMember member = new GroupAddressCardMember(addressCardUid, index, syncFieldId);
                  groupAddressCard.add(member);
               }
            }
         } finally {
            ;
         }
      } else {
         groupAddressCard = new GroupAddressCardModelImpl(initialData);
      }

      return groupAddressCard;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof Object) {
         return ((GroupAddressCardModel)object).isValid();
      }

      if (ContextObject.getFlag(object, 18) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         if (syncBuffer == null) {
            return false;
         }

         try {
            if (syncBuffer.containsType(44)) {
               byte[] typeByte = syncBuffer.getBytes(44, false);
               if (typeByte.length > 0 && typeByte[0] != 71) {
                  return false;
               }
            }
         } finally {
            ;
         }

         return syncBuffer.containsType(32, true);
      } else {
         return false;
      }
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (!ContextObject.getFlag(context, 81) && ContextObject.getFlag(context, 18) && !ContextObject.getFlag(context, 108)) {
         if (this._groupAddressBookVerbs == null) {
            this._groupAddressBookVerbs = new Object[]{new GroupAddressCardVerb(0, null)};
         }

         return this._groupAddressBookVerbs;
      } else {
         return new Object[0];
      }
   }
}
