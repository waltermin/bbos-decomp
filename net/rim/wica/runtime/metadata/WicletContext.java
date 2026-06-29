package net.rim.wica.runtime.metadata;

import net.rim.wica.runtime.persistence.WicletStore;

public interface WicletContext {
   long getId();

   String getUri();

   String getName();

   int getEntryPoint();

   boolean getPersistenceMode();

   boolean getProcessMsgsInBackground();

   WicletStore getWicletStore();

   int getExternalAccessType();

   void stopStarted();

   void stopCompleted();

   String setProperty(String var1, String var2);

   String getProperty(String var1);
}
