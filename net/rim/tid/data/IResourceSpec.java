package net.rim.tid.data;

import java.io.OutputStream;
import net.rim.tid.io.ContinuousInputStream;

public interface IResourceSpec {
   ContinuousInputStream getInputStream(String var1, Class var2, boolean var3);

   String getProtocol();

   OutputStream getOutputStream(String var1);
}
