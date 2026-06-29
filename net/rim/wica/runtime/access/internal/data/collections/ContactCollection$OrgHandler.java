package net.rim.wica.runtime.access.internal.data.collections;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.wica.runtime.access.internal.data.handlers.ObjectFieldHandler;

final class ContactCollection$OrgHandler implements ObjectFieldHandler {
   private ContactCollection$OrgHandler() {
   }

   @Override
   public final Object getValue(Object item) {
      if (item instanceof AddressCardModel) {
         CompanyInfoModel compInfo = ((AddressCardModel)item).getCompanyInfo();
         if (compInfo != null) {
            return compInfo.getCompanyName();
         }
      }

      return null;
   }

   ContactCollection$OrgHandler(ContactCollection$1 x0) {
      this();
   }
}
