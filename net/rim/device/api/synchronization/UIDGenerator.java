package net.rim.device.api.synchronization;

import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.vm.PersistentInteger;

public class UIDGenerator {
   private int _handle = PersistentInteger.getId(-4724165278185536481L, 0);
   private int _nextUID = PersistentInteger.get(this._handle);
   private static final long KEY;
   private static UIDGenerator _generator;
   private static final int BLOCK_SIZE;

   public static int getUniqueScopingValue() {
      int valToReturn;
      do {
         valToReturn = RandomSource.getInt();
      } while (valToReturn == 0);

      return valToReturn;
   }

   public static int getUID() {
      return _generator.internalGetUID();
   }

   public static int getUID(int scope) {
      return _generator.internalGetUID();
   }

   public static void resetSeed() {
      _generator.internalResetSeed();
   }

   public static long makeLUID(int scope, int value) {
      long highPart = scope & 4294967295L;
      long lowPart = value & 4294967295L;
      return highPart << 32 | lowPart;
   }

   private UIDGenerator() {
      if (this._nextUID == 0) {
         this.internalResetSeed();
      } else {
         this.startNewUIDBlock(this._nextUID + 1024);
      }
   }

   private synchronized int startNewUIDBlock(int next) {
      next &= Integer.MAX_VALUE;
      if (next == 0) {
         next++;
      }

      PersistentInteger.set(this._handle, next);
      this._nextUID = next;
      return next;
   }

   private void internalResetSeed() {
      this.startNewUIDBlock(RandomSource.getInt());
   }

   private synchronized int internalGetUID() {
      int result = this._nextUID;
      int begBlock = PersistentInteger.get(this._handle);
      int endBlock = begBlock + 1024 & 2147483647;
      int next = this._nextUID + 1 & 2147483647;
      if (next == 0) {
         next++;
      }

      if (begBlock < endBlock) {
         if (next >= endBlock) {
            next = this.startNewUIDBlock(next);
         }
      } else if (next >= endBlock && next < begBlock) {
         next = this.startNewUIDBlock(next);
      }

      this._nextUID = next;
      return result;
   }

   static {
      ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
      _generator = (UIDGenerator)reg.getOrWaitFor(-4724165278185536481L);
      if (_generator == null) {
         _generator = new UIDGenerator();
         reg.put(-4724165278185536481L, _generator);
      }
   }
}
