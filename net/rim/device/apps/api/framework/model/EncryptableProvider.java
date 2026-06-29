package net.rim.device.apps.api.framework.model;

public interface EncryptableProvider {
   long ENCODING_PRIVATE_CONTEXT_FLAGS;
   int SKIP_ENCRYPTION_ON_CONSTRUCTION;

   boolean checkCrypt(boolean var1, boolean var2);

   Object reCrypt(boolean var1, boolean var2);
}
