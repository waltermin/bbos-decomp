package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface MailingAddressModel extends PersistableRIMModel {
   int WORK_ADDRESS = 0;
   int HOME_ADDRESS = 1;

   String getAddressLine1();

   void setAddressLine1(String var1);

   String getAddressLine2();

   void setAddressLine2(String var1);

   String getCity();

   void setCity(String var1);

   String getArea();

   void setArea(String var1);

   String getZipOrPostalCode();

   void setZipOrPostalCode(String var1);

   String getCountry();

   void setCountry(String var1);

   int getType();

   void setType(int var1);

   boolean hasData();
}
