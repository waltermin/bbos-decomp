package net.rim.wica.runtime.persistence;

import net.rim.device.api.util.Persistable;

public interface CredentialStore {
   void storeCredentials(long var1, Persistable[] var3);

   Persistable[] loadCredentials(long var1);

   Persistable[] loadCredentialsForWrite(long var1);

   void deleteCredentials(long var1);

   void deleteAllCredentials();
}
