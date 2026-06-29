package net.rim.device.cldc.io.proxyhttp.compression;

import java.io.InputStream;
import java.io.OutputStream;

public interface MessageEncoder {
   int getVersion();

   void encodeHeader(String var1, String var2, OutputStream var3);

   void decodeHeader(InputStream var1, String[] var2);

   void encodeRequestLine(String var1, OutputStream var2);

   String decodeRequestLine(InputStream var1);

   void encodeResponseLine(String var1, OutputStream var2);

   String decodeResponseLine(InputStream var1);
}
