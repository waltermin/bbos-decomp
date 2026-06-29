package net.rim.plazmic.mediaengine.io;

import java.io.InputStream;

public interface Connector {
   InputStream getInputStream(String var1, ConnectionInfo var2);

   void releaseConnection(ConnectionInfo var1);

   void setProperty(String var1, String var2);
}
