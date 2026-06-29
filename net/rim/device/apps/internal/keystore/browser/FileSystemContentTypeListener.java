package net.rim.device.apps.internal.keystore.browser;

public interface FileSystemContentTypeListener {
   boolean isTypeSupported(String var1);

   boolean parseCertificatesKeys(String var1);
}
