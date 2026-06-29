package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public interface IBrowserContext extends Persistable {
   IBrowserContext clone();

   boolean serialize(SyncBuffer var1);

   @Override
   String toString();
}
