package net.rim.wica.runtime.persistence;

import net.rim.device.api.system.PersistentContentListener;

public interface PersistentContentHelper {
   void addListener(PersistentContentListener var1);

   void removeListener(PersistentContentListener var1);
}
