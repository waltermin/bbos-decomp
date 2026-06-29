package net.rim.device.cldc.io.ssl;

import javax.microedition.io.SecureConnection;
import javax.microedition.io.StreamConnection;

public interface ConnectionFactory {
   SecureConnection createInstance(String var1, StreamConnection var2, String var3, boolean var4);

   SecureConnection createServerInstance(StreamConnection var1, String var2, Object var3, Object var4);
}
