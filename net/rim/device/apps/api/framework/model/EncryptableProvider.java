package net.rim.device.apps.api.framework.model;

public interface EncryptableProvider {
   long ENCODING_PRIVATE_CONTEXT_FLAGS = 4567630869418996525L;
   int SKIP_ENCRYPTION_ON_CONSTRUCTION = 0;

   boolean checkCrypt(boolean var1, boolean var2);

   Object reCrypt(boolean var1, boolean var2);
}
