package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.MailingAddressModel;
import net.rim.wica.common.builtindata.componentdefn.AddressCompDef;
import net.rim.wica.runtime.metadata.component.DataCollection;
import net.rim.wica.runtime.metadata.internal.WicletEx;

public class AddressCollection extends AccessInnerDataCollection {
   private static Factory _mailingAddressFactory;

   public AddressCollection(WicletEx wiclet) {
      super(wiclet, AddressCompDef.getInstance());
      this.initFieldHandlers();
   }

   private void initFieldHandlers() {
      super._objectFieldHandlers = new IntHashtable(9);
      super._objectFieldHandlers.put(0, new AddressCollection$CountryHandler(null));
      super._objectFieldHandlers.put(1, new AddressCollection$ExtraHandler(null));
      super._objectFieldHandlers.put(2, new AddressCollection$LocalityHandler(null));
      super._objectFieldHandlers.put(3, new AddressCollection$PostalCodeHandler(null));
      super._objectFieldHandlers.put(4, new AddressCollection$RegionHandler(null));
      super._objectFieldHandlers.put(5, new AddressCollection$StreetHandler(null));
   }

   @Override
   protected void loadItem(long dataHandle) {
      MailingAddressModel mailingAddressModel = (MailingAddressModel)this.getDBItemFromHandle(dataHandle);
      if (mailingAddressModel != null) {
         super._loadedItems.addElement(this.getHandle(dataHandle));
         this.loadItemFromModel(dataHandle, mailingAddressModel);
      }
   }

   public void loadItemFromModel(long addressHandle, MailingAddressModel model) {
      this.setObjectFieldValue(addressHandle, 0, model.getCountry());
      this.setObjectFieldValue(addressHandle, 1, model.getAddressLine2());
      this.setObjectFieldValue(addressHandle, 2, model.getCity());
      this.setObjectFieldValue(addressHandle, 3, model.getZipOrPostalCode());
      this.setObjectFieldValue(addressHandle, 4, model.getArea());
      this.setObjectFieldValue(addressHandle, 5, model.getAddressLine1());
   }

   public void saveAddressInModel(long addressHandle, AddressCardModel model, int addressType) {
      MailingAddressModel addressModel = (MailingAddressModel)_mailingAddressFactory.createInstance(null);
      addressModel.setAddressLine1((String)this.getObjectFieldValue(addressHandle, 5));
      addressModel.setAddressLine2((String)this.getObjectFieldValue(addressHandle, 1));
      addressModel.setArea((String)this.getObjectFieldValue(addressHandle, 4));
      addressModel.setCity((String)this.getObjectFieldValue(addressHandle, 2));
      addressModel.setCountry((String)this.getObjectFieldValue(addressHandle, 0));
      addressModel.setZipOrPostalCode((String)this.getObjectFieldValue(addressHandle, 3));
      addressModel.setType(addressType);
      model.add(addressModel);
   }

   @Override
   public Object getDBItemFromHandle(long dataHandle) {
      if (super._owners != null) {
         AccessInnerDataCollection$OwnerField owner = (AccessInnerDataCollection$OwnerField)super._owners.get(this.getHandle(dataHandle));
         if (owner != null) {
            DataCollection ownerDC = super._wiclet.getDataCollection((int)(owner._dataHandle >> 32));
            if (ownerDC instanceof ContactCollection) {
               Object ownerInstance = ((ContactCollection)ownerDC).getDBItemFromHandle(owner._dataHandle);
               if (ownerInstance instanceof AddressCardModel) {
                  int addressType = ((ContactCollection)ownerDC).getMailingAddressType(owner._fieldID);
                  AddressCardModel model = (AddressCardModel)ownerInstance;
                  int size = model.size();

                  for (int i = 0; i < size; i++) {
                     Object subModel = model.getAt(i);
                     if (subModel instanceof MailingAddressModel && ((MailingAddressModel)subModel).getType() == addressType) {
                        return subModel;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _mailingAddressFactory = (Factory)ar.waitFor(-7593463283570535867L);
   }
}
