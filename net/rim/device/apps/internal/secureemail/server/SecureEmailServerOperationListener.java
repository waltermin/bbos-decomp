package net.rim.device.apps.internal.secureemail.server;

import javax.microedition.io.Connection;

public interface SecureEmailServerOperationListener {
   void updateServerOperationProgress(String var1);

   void setServerConnection(Connection var1);

   void clearServerConnection();

   boolean wasServerConnectionAborted();
}
