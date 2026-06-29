package net.rim.device.api.crypto.tls.wtls20;

import javax.microedition.io.SecurityInfo;
import net.rim.device.cldc.io.ssl.TLSException;

public interface WTLSLayerConnection {
   void makeConnection(WTLSDataTransport var1, String var2, String var3, boolean var4, int var5, String var6, int var7, int var8, boolean var9);

   byte getErrorDescription(TLSException var1);

   SecurityInfo getRIMSecurityInfo();

   void write(byte[] var1, int var2, int var3);

   int read(byte[] var1, int var2);
}
