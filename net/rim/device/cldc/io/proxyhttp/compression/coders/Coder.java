package net.rim.device.cldc.io.proxyhttp.compression.coders;

import java.io.InputStream;
import java.io.OutputStream;

public interface Coder {
   void encode(String var1, OutputStream var2);

   String decode(InputStream var1);
}
