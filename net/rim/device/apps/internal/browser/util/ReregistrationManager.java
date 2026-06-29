package net.rim.device.apps.internal.browser.util;

public interface ReregistrationManager {
   long ID;

   void addReregistrationListener(ReregistrationListener var1);

   void removeReregistrationListener(ReregistrationListener var1);
}
