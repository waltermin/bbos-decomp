package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface RemoveWhenSendingModel extends PersistableRIMModel {
   boolean removeBeforeSending();

   boolean removeAfterSending();
}
