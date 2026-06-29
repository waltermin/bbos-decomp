package net.rim.device.apps.api.addressbook;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.utility.general.Copyable;

public class AddressReference implements Persistable, EncryptableProvider {
   protected PersistableRIMModel _modelData;
   protected int _hash;
   private static int[] _reverseLookupKeys = new int[1];

   protected AddressReference() {
   }

   private final int getReverseLookupCode(Object address) {
      if (address instanceof KeyProvider) {
         synchronized (_reverseLookupKeys) {
            return ((KeyProvider)address).getKeys(null, _reverseLookupKeys, 0, -4145532165335996154L) >= 1 ? _reverseLookupKeys[0] : 0;
         }
      } else {
         return 0;
      }
   }

   public PersistableRIMModel getInsideModel() {
      PersistableRIMModel model = this._modelData;
      if (model instanceof AddressCardModel) {
         AddressCardModel addressCard = (AddressCardModel)model;

         for (int i = addressCard.size() - 1; i >= 0; i--) {
            Object element = addressCard.getAt(i);
            if (this._hash == this.getReverseLookupCode(element)) {
               return (PersistableRIMModel)element;
            }
         }
      }

      return model;
   }

   public void setInsideModel(PersistableRIMModel newModel, Object addressCardHint) {
      if (newModel != null) {
         Object entry = null;
         if (addressCardHint instanceof AddressCardModel) {
            entry = addressCardHint;
         } else if (addressCardHint != null) {
            Object[] entries = AddressBookServices.reverseLookup(newModel, null);
            if (entries != null) {
               for (int i = entries.length - 1; i >= 0; i--) {
                  if (entries[i] == addressCardHint) {
                     entry = addressCardHint;
                     break;
                  }
               }
            }
         }

         if (!(entry instanceof AddressCardModel)) {
            entry = AddressBookServices.reverseLookup(newModel);
         }

         if (entry instanceof AddressCardModel) {
            this._modelData = (PersistableRIMModel)entry;
            this._hash = this.getReverseLookupCode(newModel);
            return;
         }
      }

      if ((ObjectGroup.isInGroup(newModel) || newModel instanceof GroupAddressCardModel) && newModel instanceof Copyable) {
         this._modelData = (PersistableRIMModel)((Copyable)newModel).copy();
      } else {
         this._modelData = newModel;
      }

      this._hash = 0;
   }

   public PersistableRIMModel getAddressBookEntry() {
      return this.getAddressBookEntry(true);
   }

   protected PersistableRIMModel getAddressBookEntry(boolean lookupIfNecessary) {
      if (this._modelData instanceof AddressCardModel) {
         return this._modelData;
      }

      if (!lookupIfNecessary) {
         return null;
      }

      PersistableRIMModel model = null;
      if (this._modelData != null) {
         model = (PersistableRIMModel)AddressBookServices.reverseLookup(this._modelData);
      }

      return model;
   }

   public long getAddressBookEntryLUID() {
      return getAddressBookEntryLUID(this.getAddressBookEntry());
   }

   protected static long getAddressBookEntryLUID(RIMModel addressBookEntry) {
      return !(addressBookEntry instanceof UniqueIDProvider) ? 0 : ((UniqueIDProvider)addressBookEntry).getLUID(null);
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      if (!(this._modelData instanceof EncryptableProvider)) {
         return false;
      }

      EncryptableProvider encryptable = (EncryptableProvider)this._modelData;
      return encryptable.checkCrypt(compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      if (this._modelData instanceof EncryptableProvider) {
         EncryptableProvider encryptable = (EncryptableProvider)this._modelData;
         PersistableRIMModel newModelData = (PersistableRIMModel)encryptable.reCrypt(compress, encrypt);
         if (newModelData != null) {
            this._modelData = newModelData;
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return this._modelData.toString();
   }
}
