package net.rim.device.apps.internal.phone.api;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface CallerIDProvider extends PersistableRIMModel {
   PersistableRIMModel getNumber();

   PersistableRIMModel getAddress();

   void setFriendlyName(String var1);

   String getFriendlyName();

   boolean validRIMAddress();
}
