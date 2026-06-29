package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.RIMKeyStore;
import net.rim.device.api.system.ApplicationRegistry;

public class PGPUniversalEnrollmentKeyStore extends RIMKeyStore {
   private static final long ID = 4730272560307327500L;
   private static PGPUniversalEnrollmentKeyStore _instance;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static PGPUniversalEnrollmentKeyStore getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         PGPUniversalEnrollmentKeyStore pgpUniversalEnrollmentKeyStore = (PGPUniversalEnrollmentKeyStore)applicationRegistry.getOrWaitFor(4730272560307327500L);
         if (pgpUniversalEnrollmentKeyStore == null) {
            boolean var4 = false /* VF: Semaphore variable */;

            try {
               var4 = true;
               pgpUniversalEnrollmentKeyStore = new PGPUniversalEnrollmentKeyStore();
               applicationRegistry.put(4730272560307327500L, pgpUniversalEnrollmentKeyStore);
               var4 = false;
            } finally {
               if (var4) {
                  throw new Object();
               }
            }
         }

         _instance = pgpUniversalEnrollmentKeyStore;
      }

      return _instance;
   }

   private PGPUniversalEnrollmentKeyStore() {
      super("PGP Universal Enrollment Key Store");
   }

   @Override
   public KeyStoreTicket getTicket() {
      return null;
   }

   @Override
   public boolean checkTicket(KeyStoreTicket ticket) {
      return true;
   }
}
