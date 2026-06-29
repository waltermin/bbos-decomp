package net.rim.device.apps.internal.browser.cod;

import java.io.InputStream;
import java.util.Vector;

public interface JARCompiler {
   Vector compile(String var1, String var2, InputStream var3, int var4, ApplicationDownloadListener var5);

   int getResultCode();

   String extractManifest(InputStream var1);

   String getError();

   byte[] getSignerCertEncoding();
}
