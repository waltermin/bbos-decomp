package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface CompanyInfoModel extends PersistableRIMModel {
   String getCompanyName();

   void setCompanyName(String var1);

   String getCompanyNameYOMI();

   void setCompanyNameYOMI(String var1);
}
