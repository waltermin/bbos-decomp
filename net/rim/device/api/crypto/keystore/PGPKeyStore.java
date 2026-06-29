package net.rim.device.api.crypto.keystore;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.crypto.pgp.PGPUtilities;

public final class PGPKeyStore extends SyncableRIMKeyStore {
   private static final long PGP = -948358821779644380L;
   private static PGPKeyStore _keyStore;

   private PGPKeyStore() {
      super("PGP", PGPUtilities.getResourceBundle().getString(8002), -948358821779644380L, null, new PGPPersistableRIMKeyStoreFactory(), null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final PGPKeyStore getInstance() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      _keyStore = (PGPKeyStore)appRegistry.getOrWaitFor(-948358821779644380L);
      if (_keyStore == null) {
         boolean var3 = false /* VF: Semaphore variable */;

         try {
            var3 = true;
            _keyStore = new PGPKeyStore();
            appRegistry.put(-948358821779644380L, _keyStore);
            var3 = false;
         } finally {
            if (var3) {
               throw new RuntimeException();
            }
         }
      }

      return _keyStore;
   }

   @Override
   public final String getName() {
      return PGPUtilities.getResourceBundle().getString(8002);
   }
}
