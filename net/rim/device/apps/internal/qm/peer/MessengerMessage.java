package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.RIMModel;

public interface MessengerMessage extends RIMModel {
   String getSender();

   String getText();

   Field getField(Object var1);

   long getTime();

   boolean isIncoming();

   boolean isSystem();
}
