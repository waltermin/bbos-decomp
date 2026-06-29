package net.rim.wica.runtime.authentication.internal.credentialstore;

public interface Credentials {
   int getScheme();

   String getUsername();

   String getDomain();

   byte[] getSecretToken();
}
