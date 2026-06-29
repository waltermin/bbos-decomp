package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface FriendlyNameAddressModel extends PersistableRIMModel {
   void setFriendlyName(String var1);

   String getFriendlyName();

   void setAddress(String var1);

   String getAddress();

   void setAddressAndFriendlyName(String var1, String var2);

   boolean isFreeForm();

   void setFreeForm(boolean var1);
}
