package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface PersonNameModel extends PersistableRIMModel {
   String getFirstName();

   void setFirstName(String var1);

   String getLastName();

   void setLastName(String var1);

   String getSalutation();

   void setSalutation(String var1);

   void setNames(String var1, String var2, String var3);

   String getFirstNameYOMI();

   String getLastNameYOMI();

   void setFullNameYOMI(String var1, String var2);
}
