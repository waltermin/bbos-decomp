package net.rim.device.internal.system;

import net.rim.vm.PersistentInteger;

public final class VoiceDataUsage {
   public static final int DATA_MAX = 256000;
   public static final int VOICE_MAX = 3600;
   public static final int DATA_COMMIT = 65536;
   public static final int VOICE_COMMIT = 120;
   public static final int ILLEGAL_VALUE = -1;
   private static final long DATA_PERSIST_KEY = 5479242016585988378L;
   private static final long VOICE_PERSIST_KEY = 4462726683010157038L;
   private static int _persistDataHandle = PersistentInteger.getId(5479242016585988378L, -1);
   private static int _persistVoiceHandle = PersistentInteger.getId(4462726683010157038L, -1);

   private VoiceDataUsage() {
   }

   public static final synchronized void addVoiceSeconds(int seconds) {
      if (!itPolicyEnabled()) {
         if (!exceededVoiceLimit()) {
            addValue(seconds, _persistVoiceHandle, 37, 120);
         }
      }
   }

   public static final synchronized void addDataBytes(int bytes) {
      if (!itPolicyEnabled()) {
         if (!exceededDataLimit()) {
            addValue(bytes, _persistDataHandle, 38, 65536);
         }
      }
   }

   public static final synchronized int getVoiceSeconds() {
      return getValue(_persistVoiceHandle, 37);
   }

   public static final synchronized int getDataBytes() {
      return getValue(_persistDataHandle, 38);
   }

   public static final synchronized boolean exceededVoiceLimit() {
      return getVoiceSeconds() > 3600;
   }

   public static final synchronized boolean exceededDataLimit() {
      return getDataBytes() > 256000;
   }

   public static final synchronized boolean itPolicyEnabled() {
      return ITPolicyInternal.isITPolicyEnabled();
   }

   public static final synchronized void reset() {
      PersistentInteger.set(_persistVoiceHandle, -1);
      PersistentInteger.set(_persistDataHandle, -1);
      NvStore.deleteData(37);
      NvStore.deleteData(38);
   }

   private static final int getValue(int pHandle, int nvHandle) {
      int value = PersistentInteger.get(pHandle);
      if (value == -1) {
         value = getFromNvStore(nvHandle);
         if (value == -1) {
            value = 0;
         }

         PersistentInteger.set(pHandle, value);
      }

      return value;
   }

   private static final int getFromNvStore(int nvHandle) {
      return NvStore.readInt(nvHandle, -1);
   }

   private static final void addValue(int value, int pHandle, int nvHandle, int commit) {
      int old = getValue(pHandle, nvHandle);
      int newVal = old + value;
      if (newVal / commit != old / commit) {
         commitToNvStore(nvHandle, newVal);
      }

      PersistentInteger.set(pHandle, newVal);
   }

   private static final boolean commitToNvStore(int nvHandle, int value) {
      return NvStore.writeInt(nvHandle, value);
   }
}
