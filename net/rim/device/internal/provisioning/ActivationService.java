package net.rim.device.internal.provisioning;

import java.util.Hashtable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongIntHashtable;

public class ActivationService {
   public Hashtable _listeners = new Hashtable(1);
   protected static final long ACTIVATION_SERVICE_ID = -1320069024724775836L;
   protected static final long ACTIVATION_KEY_ID = 6247846804872834637L;
   public static final byte OTAKEYGEN_ABORT_REASON_NONE = 0;
   public static final byte OTAKEYGEN_ABORT_REASON_GENERAL = 1;
   public static final byte OTAKEYGEN_ABORT_REASON_NO_LONGTERM_KEY = 2;
   public static final byte OTAKEYGEN_ABORT_REASON_SERVICE_RESET = 3;
   public static final byte OTAKEYGEN_ABORT_REASON_MAX_RETRY = 4;
   public static final byte OTAKEYGEN_ABORT_REASON_KEY_VALIDATION = 5;
   public static final byte OTAKEYGEN_ABORT_REASON_USER_ALREADY_ACTIVE = 6;
   public static final byte OTAKEYGEN_ABORT_REASON_USER = 7;
   public static final byte OTAKEYGEN_ABORT_REASON_TIMEOUT = 8;
   public static final byte OTAKEYGEN_ABORT_REASON_NO_PASSWORD = 9;
   public static final byte OTAKEYGEN_ABORT_PIN_ALREADY_REGISTERED = 10;
   public static final byte OTAKEYGEN_ABORT_ITPOLICY_REJECTED = 11;
   public static final byte OTAKEYGEN_ABORT_NOT_AUTHORIZED = 12;
   public static final byte OTAKEYGEN_ABORT_REASON_SEND_ERROR = 13;
   public static final String PROVISIONING_CID = "PROVISIONING";

   public int attemptActivation(String _1, String _2, String _3) {
      throw null;
   }

   public void abortTransaction(int _1, byte _2) {
      throw null;
   }

   public void abortTransaction(String _1, byte _2) {
      throw null;
   }

   public int regenerateKey(String _1, boolean _2) {
      throw null;
   }

   public String getUIDbyEmailAddress(String _1) {
      throw null;
   }

   public boolean isActivationPending(String _1) {
      throw null;
   }

   public boolean isTransactionInProgress(String _1) {
      throw null;
   }

   public boolean isAnyTransactionInProgress() {
      throw null;
   }

   public String[][] getRegenerationUIDs() {
      throw null;
   }

   public static ActivationService getInstance() {
      return (ActivationService)ApplicationRegistry.getApplicationRegistry().waitFor(-1320069024724775836L);
   }

   public static ActivationService getInstanceNoWait() {
      return (ActivationService)ApplicationRegistry.getApplicationRegistry().get(-1320069024724775836L);
   }

   public static boolean isActivationServiceAvailable() {
      return ApplicationRegistry.getApplicationRegistry().get(-1320069024724775836L) != null;
   }

   public static boolean hasThisDeviceBeenActivated() {
      return getActivatedServices().length > 0;
   }

   public static long[] getActivatedServices() {
      long[] activatedServices = new long[0];
      LongIntHashtable serviceCompletionHash = getOrUpdateHashtable(-1, 0);
      LongEnumeration serviceEnum = serviceCompletionHash.keys();

      while (serviceEnum.hasMoreElements()) {
         long currentService = serviceEnum.nextElement();
         if (serviceCompletionHash.get(currentService) != 0) {
            Arrays.add(activatedServices, currentService);
         }
      }

      return activatedServices;
   }

   public static void activationComplete(boolean completed, long serviceId) {
      getOrUpdateHashtable(serviceId, completed ? System.currentTimeMillis() : 0);
   }

   protected static void activationComplete(long completedTime, long serviceId) {
      getOrUpdateHashtable(serviceId, completedTime);
   }

   protected static void serviceIdChanged(long oldServiceId, long newServiceId) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(6247846804872834637L);
      LongIntHashtable serviceCompletionHash = (LongIntHashtable)persistentObject.getContents();
      if (serviceCompletionHash != null) {
         if (serviceCompletionHash.containsKey(oldServiceId)) {
            int value = serviceCompletionHash.get(oldServiceId);
            serviceCompletionHash.remove(oldServiceId);
            serviceCompletionHash.put(newServiceId, value);
         }

         persistentObject.setContents(serviceCompletionHash, 51);
         persistentObject.commit();
      }
   }

   private static LongIntHashtable getOrUpdateHashtable(long serviceId, long completedTime) {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(6247846804872834637L);
      LongIntHashtable serviceCompletionHash = (LongIntHashtable)persistentObject.getContents();
      if (serviceCompletionHash == null) {
         serviceCompletionHash = new LongIntHashtable();
      }

      if (serviceId != -1) {
         if (completedTime > 0) {
            serviceCompletionHash.put(serviceId, DateTimeUtilities.convertMillisecondsToEpoch(completedTime));
         } else {
            serviceCompletionHash.remove(serviceId);
         }

         persistentObject.setContents(serviceCompletionHash, 51);
         persistentObject.commit();
      }

      return serviceCompletionHash;
   }

   public static long getLastSuccessfulActivationDate(long serviceId) {
      int value = getOrUpdateHashtable(-1, 0).get(serviceId);
      return value > 0 ? DateTimeUtilities.convertEpochToMilliseconds(value) : 0;
   }

   public void createOtaKeyGenSR(String _1, String _2, String _3) {
      throw null;
   }

   public void resetOtaKeyGenSR() {
      throw null;
   }

   public SyncCollection[] getCollections() {
      throw null;
   }

   public boolean isActivationInProgress() {
      throw null;
   }

   public boolean addActivationStatusListener(ActivationStatusListener listener) {
      if (!this._listeners.containsKey(listener.getCollectionName())) {
         this._listeners.put(listener.getCollectionName(), listener);
         return true;
      } else {
         return false;
      }
   }

   public void removeActivationStatusListener(ActivationStatusListener listener) {
      this._listeners.remove(listener.getCollectionName());
   }

   protected void notifyStatusListener(String collectionName, boolean errorsOccured) {
      if (this._listeners.containsKey(collectionName)) {
         ((ActivationStatusListener)this._listeners.get(collectionName)).activationComplete(errorsOccured);
      }
   }
}
