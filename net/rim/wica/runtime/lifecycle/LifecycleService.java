package net.rim.wica.runtime.lifecycle;

import net.rim.wica.runtime.persistence.WicletStore;

public interface LifecycleService {
   Wiclet getWiclet(long var1);

   Wiclet getWiclet(String var1);

   Wiclet[] getWiclets();

   Wiclet[] getWicletsByAg(long var1);

   boolean hasWiclet(long var1);

   boolean hasWiclet(String var1);

   Wiclet installApplication(WicletStore var1);

   int installationType(String var1, String var2);

   int installationType(WicletInfo var1);

   void startWiclet(long var1);

   void startWiclet(String var1);

   void stopApplication(long var1);

   void stopApplication(String var1);

   void processIncomingMessage(long var1, int var3);

   void sendStatusMessage(long var1, String var3, String var4, String var5, int var6, int var7);

   void sendStatusMessage(long var1, String var3, String var4, String var5, int var6, int var7, long var8);

   void sendStatusMessage(long var1, long var3, String var5, String var6, String var7, int var8, int var9);

   void sendStatusMessage(long var1, long var3, String var5, String var6, String var7, int var8, int var9, long var10);

   void loadWiclet(String var1);

   void uninstallWiclet(long var1);

   void uninstallWicletsByAg(long var1);

   void upgradeWiclet(long var1);
}
