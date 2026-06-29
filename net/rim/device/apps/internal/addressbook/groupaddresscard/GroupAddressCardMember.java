package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncFieldIDProvider;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.api.utility.general.Copyable;

final class GroupAddressCardMember implements Persistable, Copyable {
   private int _acmUID;
   private byte _index;
   private int _addressCardSyncFieldId;

   GroupAddressCardMember(int acmUID, byte index, int addressCardSyncFieldId) {
      this._acmUID = acmUID;
      if (addressCardSyncFieldId == 1) {
         this._index = index;
      } else {
         this._index = -1;
      }

      this._addressCardSyncFieldId = addressCardSyncFieldId;
   }

   public final RIMModel getAddressModel() {
      AddressCardModel acm = (AddressCardModel)this.getAddressCardModel();
      if (acm == null) {
         return null;
      }

      int total = acm.size();
      int localIndex = 0;
      ContextObject context = new ContextObject(18);

      for (int i = 0; i < total; i++) {
         Object o = acm.getAt(i);
         if (o instanceof SyncFieldIDProvider) {
            int localId = ((SyncFieldIDProvider)o).getSyncFieldId(context);
            if (localId == this._addressCardSyncFieldId) {
               if (localId != 1 || this._index == localIndex) {
                  return (RIMModel)o;
               }

               localIndex++;
            }
         }
      }

      return null;
   }

   public static final Object[] getGroupAddressableRIMModels(Object object, byte type) {
      if (!(object instanceof ReadableList)) {
         return null;
      }

      ReadableList model = (ReadableList)object;
      switch (type) {
         case 0:
            return SubmemberUtilities.getSubmembers(model, RecognizerRepository.getRecognizers(-2985347935260258684L));
         case 1:
            return SubmemberUtilities.getSubmembers(model, RecognizerRepository.getRecognizers(4246852237058296601L));
         case 2:
         default:
            return SubmemberUtilities.getSubmembers(model, RecognizerRepository.getRecognizers(3797587162219887872L));
      }
   }

   public final Object getAddressCardModel() {
      return AddressBookServices.getAddressCard(this._acmUID);
   }

   public final int getUID() {
      return this._acmUID;
   }

   public final int getAddressCardSyncFieldId() {
      return this._addressCardSyncFieldId;
   }

   public final byte getEmailIndex() {
      return this._index;
   }

   public final byte getType() {
      switch (this._addressCardSyncFieldId) {
         case 1:
         case 11:
         case 12:
         case 13:
         case 15:
            return 0;
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 14:
         case 16:
         case 17:
         case 18:
         default:
            return 2;
         case 10:
            return 1;
      }
   }

   @Override
   public final Object copy() {
      return new GroupAddressCardMember(this._acmUID, this._index, this._addressCardSyncFieldId);
   }
}
