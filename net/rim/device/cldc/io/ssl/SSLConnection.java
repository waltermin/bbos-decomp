package net.rim.device.cldc.io.ssl;

import javax.microedition.io.SecureConnection;

public interface SSLConnection extends SecureConnection {
   void setOverrideConnectionOptions(SSLConnectionOptions var1);
}
