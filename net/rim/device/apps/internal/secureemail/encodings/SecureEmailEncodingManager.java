package net.rim.device.apps.internal.secureemail.encodings;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongIntHashtable;

public class SecureEmailEncodingManager {
   private LongIntHashtable _encodingPrioritiesByUID = (LongIntHashtable)(new Object());
   private static final long ID = -7375709277351116640L;
   private static SecureEmailEncodingManager _instance;

   public static SecureEmailEncodingManager getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         SecureEmailEncodingManager secureEmailEncodingManager = (SecureEmailEncodingManager)applicationRegistry.getOrWaitFor(-7375709277351116640L);
         if (secureEmailEncodingManager == null) {
            secureEmailEncodingManager = new SecureEmailEncodingManager();
            applicationRegistry.put(-7375709277351116640L, secureEmailEncodingManager);
         }

         _instance = secureEmailEncodingManager;
      }

      return _instance;
   }

   private SecureEmailEncodingManager() {
   }

   public void register(long encodingUID, int encodingPriority) {
      if (encodingPriority < 0) {
         throw new Object();
      }

      this._encodingPrioritiesByUID.put(encodingUID, encodingPriority);
   }

   public int getEncodingPriority(long encodingUID) {
      return this._encodingPrioritiesByUID.get(encodingUID);
   }

   public boolean isRegistered(long encodingUID) {
      return this._encodingPrioritiesByUID.containsKey(encodingUID);
   }
}
