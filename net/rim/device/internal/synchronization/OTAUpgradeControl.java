package net.rim.device.internal.synchronization;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.lowmemory.LowMemoryFailedListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionStatusProvider;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongIntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.compress.CompressUtilities;
import net.rim.device.internal.compress.Deflater;
import net.rim.device.internal.compress.Inflater;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;
import net.rim.vm.EventLog;
import net.rim.vm.FlashInputStream;
import net.rim.vm.FlashOutputStream;
import net.rim.vm.Memory;
import net.rim.vm.OTAUpgrade;
import net.rim.vm.Process;

public final class OTAUpgradeControl implements LowMemoryFailedListener {
   private OTAUpgradeControlCallback _control;
   private boolean _compressBWT = true;
   private boolean _encryptStream = PersistentContent.isEncryptionEnabled();
   private boolean _testResets;
   private long _compressionTime;
   private long _encryptionTime;
   private long _gcTime;
   private long _streamTime;
   private long _totalTime;
   private int _compressedSize;
   private int _uncompressedSize;
   private int _flashFree;
   private int _itemsRemoved;
   private int _progress;
   private long _progressTotal;
   private long _progressCurrent;
   private boolean _serviceBookLegacyMode;
   private boolean _lmmFailed;
   private int _status;
   public static final int OTA_OK = 0;
   public static final int OTA_FLASH_FULL = 1;
   private static final boolean DATA_BUFFER_ENDIANESS = false;
   private static final int CURRENT_VERSION = 65537;
   private static final int CHUNK_HEADER_LEN = 8;
   private static final int CHUNK_FLAGS_NULL = 0;
   private static final int CHUNK_FLAGS_ZLIB = 1;
   private static final int CHUNK_FLAGS_SECURE = 2;
   private static final int CHUNK_FLAGS_USED = 3;
   private static final int ITEM_FLAGS_NULL = 0;
   private static final int ITEM_FLAGS_DIRTY = 1;
   private static final long GUID_BACKUP = -3904873326663712912L;
   private static final long GUID_RESTORE = -7484766735474520310L;
   private static final long GUID_EXTRA = -4696636443275693781L;
   private static final long GUID_LOG = 4234230263395208602L;
   private static final long GUID_NULL = 0L;
   private static final int AMOUNT_TO_WRITE = 131072;
   private static final int ITEMS_FOR_GC = 1024;
   private static final int TAG_RADIO = 1;
   private static final int TAG_RADIO_IDS = 2;
   private static final int RADIO_ON = 1;
   private static final int RADIO_OFF = 2;
   private static final long RADIO_IDS_PRESENT = Long.MIN_VALUE;
   private static final long OTASYNC_OFF_DELAY_IN_MS = 5000L;
   private static final long SYSTEM_RECOVERY_TIME_IN_MS = 2500L;
   private static final long LONG_SYSTEM_RECOVERY_TIME_IN_MS = 25000L;
   private static final int RESET_RESTORE_UNDO_FLUSH_OUTPUT_TO_FLASH = 1;
   private static final int RESET_RESTORE_UNDO_FLUSH_OUTPUT_TO_FLASH_1 = 2;
   private static final int RESET_RESTORE_UNDO_FLUSH_OUTPUT_TO_FLASH_2 = 3;
   private static final int RESET_APPEND_INT_OUTPUT_TO_FLASH = 4;
   private static final int RESET_APPEND_INT_OUTPUT_TO_FLASH_1 = 5;
   private static final int RESET_APPEND_INT_OUTPUT_TO_FLASH_2 = 6;
   private static final int RESET_FINI_BACKUP_OUTPUT_TO_FLASH = 7;
   private static final int RESET_FINI_BACKUP_OUTPUT_TO_FLASH_1 = 8;
   private static final int RESET_FINI_BACKUP_OUTPUT_TO_FLASH_2 = 9;
   private static final int RESET_INIT_COLLECTION_OUTPUT_TO_FLASH = 10;
   private static final int RESET_INIT_COLLECTION_OUTPUT_TO_FLASH_1 = 11;
   private static final int RESET_INIT_COLLECTION_OUTPUT_TO_FLASH_2 = 12;
   private static final int RESET_REGISTER_COLLECTION_OUTPUT_TO_FLASH = 13;
   private static final int RESET_REGISTER_COLLECTION_OUTPUT_TO_FLASH_1 = 14;
   private static final int RESET_REGISTER_COLLECTION_OUTPUT_TO_FLASH_2 = 15;
   private static final int RESET_BACKUP_CHUNK_OUTPUT_TO_FLASH = 16;
   private static final int RESET_BACKUP_CHUNK_OUTPUT_TO_FLASH_1 = 17;
   private static final int RESET_BACKUP_CHUNK_OUTPUT_TO_FLASH_2 = 18;
   private static final int RESET_BACKUP_FLUSH_OUTPUT_TO_FLASH = 19;
   private static final int RESET_BACKUP_FLUSH_OUTPUT_TO_FLASH_1 = 20;
   private static final int RESET_BACKUP_FLUSH_OUTPUT_TO_FLASH_2 = 21;
   private static final int RESET_RESTORE_CHUNK_OUTPUT_TO_FLASH = 22;
   private static final int RESET_RESTORE_CHUNK_OUTPUT_TO_FLASH_1 = 23;
   private static final int RESET_RESTORE_CHUNK_OUTPUT_TO_FLASH_2 = 24;
   private static final int RESET_RESTORE_FLUSH_OUTPUT_TO_FLASH = 25;
   private static final int RESET_RESTORE_FLUSH_OUTPUT_TO_FLASH_1 = 26;
   private static final int RESET_RESTORE_FLUSH_OUTPUT_TO_FLASH_2 = 27;
   private static final int RESET_RESTORE_UNDO_CHUNK_OUTPUT_TO_FLASH = 28;
   private static final int RESET_RESTORE_UNDO_CHUNK_OUTPUT_TO_FLASH_1 = 29;
   private static final int RESET_RESTORE_UNDO_CHUNK_OUTPUT_TO_FLASH_2 = 30;
   private static final int RESET_BACKUP_COMMIT = 31;
   private static final int RESET_BACKUP_COMMIT_1 = 32;
   private static final int RESET_RESTORE_COMMIT = 33;
   private static final int RESET_RESTORE_COMMIT_1 = 34;
   private static final int RESET_RESTORE_PURGE = 35;
   private static final int RESET_RESTORE_PURGE_1 = 36;
   private static final int RESET_TRANSFER_ERASE = 37;
   private static final int RESET_TRANSFER_ERASE_1 = 38;
   private static final int RESET_BACKUP_COLLECTION_ERASE = 39;
   private static final int RESET_BACKUP_COLLECTION_ERASE_1 = 40;
   private static final int RESET_REGISTERED_COLLECTION_ERASE = 41;
   private static final int RESET_REGISTERED_COLLECTION_ERASE_1 = 42;
   private static final int RESET_UNDO_COLLECTION_ERASE = 43;
   private static final int RESET_UNDO_COLLECTION_ERASE_1 = 44;
   private static final int RESET_RESTORE_BACKUP_ERASE = 45;
   private static final int RESET_RESTORE_BACKUP_ERASE_1 = 46;
   private static final int RESET_EMPTY_UNDO_ERASE = 47;
   private static final int RESET_EMPTY_UNDO_ERASE_1 = 48;
   private static final int RESET_EXTRA_ERASE = 49;
   private static final int RESET_EXTRA_ERASE_1 = 50;
   private static final int RESET_APPEND_LONG_OUTPUT_TO_FLASH = 51;
   private static final int RESET_APPEND_LONG_OUTPUT_TO_FLASH_1 = 52;
   private static final int RESET_APPEND_LONG_OUTPUT_TO_FLASH_2 = 53;
   private static final int RESET_NUM = 54;
   private static final int RESET_UNKNOWN = 0;

   public final int getStatus() {
      return this._status;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void doBackup(boolean undoPhase) {
      this._control.updateProgress(0);
      this.clearStats();
      this._totalTime = -System.currentTimeMillis();
      SyncCollection[] syncCollections = this.getSyncCollections(false);
      boolean reservedFlashActive = true;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         this.registerForLMMFailures();
         reservedFlashActive = Memory.setReservedSpaceMode(false);
         this.activateCollections(syncCollections);
         this.startBackup();
         boolean finished = this.initBackup(syncCollections);
         label48:
         if (!finished) {
            int[] progressCheck = this.initBackupProgress(syncCollections);
            this.incrementTotalProgress();

            for (int i = syncCollections.length - 1; i >= 0; i--) {
               this.incrementTotalProgress();
               this.finishCollectionBackup(syncCollections[i], undoPhase);
               if (!undoPhase && this._lmmFailed) {
                  this._status = 1;
                  break;
               }

               if (this._control.stop()) {
                  break label48;
               }

               this.incrementTotalProgress();
               this.checkBackupProgress(syncCollections[i], progressCheck[i]);
            }

            this.finishStream(-3904873326663712912L);
         }

         this.endBackup();
         var9 = false;
      } finally {
         if (var9) {
            this.deactivateCollections(syncCollections);
            this._totalTime = this._totalTime + System.currentTimeMillis();
            this.printSummary("Backup");
            this._control.updateProgress(100);
            Memory.setReservedSpaceMode(reservedFlashActive);
            this.deregisterForLMMFailures();
         }
      }

      this.deactivateCollections(syncCollections);
      this._totalTime = this._totalTime + System.currentTimeMillis();
      this.printSummary("Backup");
      this._control.updateProgress(100);
      Memory.setReservedSpaceMode(reservedFlashActive);
      this.deregisterForLMMFailures();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final synchronized void doRestore(boolean undoPhase) {
      this._control.updateProgress(0);
      this.clearStats();
      this._totalTime = -System.currentTimeMillis();
      SyncCollection[] syncCollections = this.getSyncCollections(true);
      boolean reservedFlashActive = true;
      boolean var9 = false /* VF: Semaphore variable */;

      try {
         var9 = true;
         this.registerForLMMFailures();
         reservedFlashActive = Memory.setReservedSpaceMode(false);
         this.activateCollections(syncCollections);
         startRestore();
         boolean ready = this.initRestore();
         if (ready) {
            LongIntHashtable collectionCounts = this.initRestoreProgress(syncCollections);

            for (int i = syncCollections.length - 1; i >= 0; i--) {
               this.incrementTotalProgress();
               this.finishCollectionRestore(syncCollections[i], undoPhase, collectionCounts);
               this.incrementTotalProgress();
               if (!undoPhase && this._lmmFailed) {
                  this._status = 1;
                  break;
               }
            }

            this.finiRestore();
         }

         this.endRestore();
         var9 = false;
      } finally {
         if (var9) {
            this.deactivateCollections(syncCollections);
            this._totalTime = this._totalTime + System.currentTimeMillis();
            this.printSummary("Restore");
            this._control.updateProgress(100);
            Memory.setReservedSpaceMode(reservedFlashActive);
            this.deregisterForLMMFailures();
         }
      }

      this.deactivateCollections(syncCollections);
      this._totalTime = this._totalTime + System.currentTimeMillis();
      this.printSummary("Restore");
      this._control.updateProgress(100);
      Memory.setReservedSpaceMode(reservedFlashActive);
      this.deregisterForLMMFailures();
      if (undoPhase) {
         this.doPurge();
      }
   }

   public final synchronized void doPurge() {
      this.clearStats();
      this.eraseRegisteredCollections(-3904873326663712912L);
      FlashInputStream.erase(-3904873326663712912L);
      this.eraseRegisteredCollections(-7484766735474520310L);
      FlashInputStream.erase(-7484766735474520310L);
      FlashInputStream.erase(-4696636443275693781L);
      this.printSummary("Purge");
   }

   public final synchronized void toggleBWTCompression() {
      this.clearStats();
      this._compressBWT = !this._compressBWT;
      this.printSummary("toggleBWT");
   }

   public final synchronized int toggleResetTest() {
      this.clearStats();
      this._testResets = !this._testResets;
      this.printSummary("toggleResetTests");
      return 54;
   }

   public final synchronized void setCallback(OTAUpgradeControlCallback control) {
      this._control = control;
   }

   public final synchronized boolean canUndoUpgrade() {
      return this.isTagPresent(-7484766735474520310L, 2);
   }

   @Override
   public final void lowMemoryManagerFailed() {
      this._lmmFailed = true;
   }

   private final void initProgress(int amountOfWork) {
      this._progressTotal = amountOfWork;
      this._progressCurrent = 0;
      this._progress = 0;
      this.updateProgress(false);
   }

   private final int[] initBackupProgress(SyncCollection[] syncCollections) {
      int n = syncCollections.length;
      int[] progress = new int[n];
      int work = 1;

      for (int i = syncCollections.length - 1; i >= 0; i--) {
         work += 2;
         SyncCollection sc = syncCollections[i];
         if (sc != null) {
            work += OTAUpgradeControl$SCSafe.getSyncObjectCount(sc);
         }

         progress[i] = work;
      }

      this.initProgress(work);
      return progress;
   }

   private static final int checkAvailable(int prevAvailable, DataBuffer buff) {
      int currAvailable = buff.available();
      if (currAvailable <= 0) {
         return currAvailable;
      } else if (currAvailable >= prevAvailable) {
         throw new Object();
      } else {
         return currAvailable;
      }
   }

   private final LongIntHashtable initRestoreProgress(SyncCollection[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: invokespecial net/rim/device/api/util/LongIntHashtable.<init> ()V
      // 07: astore 2
      // 08: bipush 1
      // 09: istore 3
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: ldc2_w -3904873326663712912
      // 11: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 14: astore 4
      // 16: new java/lang/Object
      // 19: dup
      // 1a: bipush 0
      // 1b: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 1e: astore 5
      // 20: bipush 0
      // 21: i2l
      // 22: lstore 6
      // 24: bipush 0
      // 25: istore 8
      // 27: ldc_w 2147483647
      // 2a: istore 9
      // 2c: aload 0
      // 2d: aload 4
      // 2f: aload 5
      // 31: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 34: ifeq 3c
      // 37: ldc_w 2147483647
      // 3a: istore 9
      // 3c: iload 9
      // 3e: aload 5
      // 40: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 43: istore 9
      // 45: iload 9
      // 47: ifgt 4d
      // 4a: goto be
      // 4d: aload 5
      // 4f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 52: istore 10
      // 54: iload 10
      // 56: lookupswitch 95 3 3 34 5 54 9 44
      // 78: aload 5
      // 7a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 7d: lstore 6
      // 7f: goto 2c
      // 82: aload 5
      // 84: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 87: istore 8
      // 89: goto 2c
      // 8c: aload 5
      // 8e: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 91: pop
      // 92: aload 2
      // 93: lload 6
      // 95: iload 8
      // 97: invokevirtual net/rim/device/api/util/LongIntHashtable.put (JI)I
      // 9a: istore 11
      // 9c: iload 11
      // 9e: bipush -1
      // a0: if_icmpeq a8
      // a3: iload 3
      // a4: iload 8
      // a6: isub
      // a7: istore 3
      // a8: iload 8
      // aa: iflt 2c
      // ad: iload 3
      // ae: iload 8
      // b0: iadd
      // b1: istore 3
      // b2: goto 2c
      // b5: aload 5
      // b7: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // ba: pop
      // bb: goto 2c
      // be: aload 4
      // c0: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // c3: goto da
      // c6: astore 5
      // c8: aload 4
      // ca: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // cd: goto da
      // d0: astore 12
      // d2: aload 4
      // d4: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // d7: aload 12
      // d9: athrow
      // da: iload 3
      // db: bipush 2
      // dd: aload 1
      // de: arraylength
      // df: imul
      // e0: iadd
      // e1: istore 3
      // e2: aload 0
      // e3: iload 3
      // e4: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.initProgress (I)V
      // e7: aload 2
      // e8: areturn
      // try (11 -> 76): 79 null
      // try (11 -> 76): 83 null
      // try (79 -> 80): 83 null
      // try (83 -> 84): 83 null
   }

   private final void updateProgress(boolean doUpdate) {
      int curr = 1;
      if (this._progressTotal != 0) {
         curr = (int)(this._progressCurrent * 100 / this._progressTotal);
         if (curr == 0 && this._progressCurrent != 0) {
            curr = 1;
         } else if (curr > 99) {
            curr = 99;
         }
      }

      if (curr != this._progress) {
         this._progress = curr;
         if (doUpdate) {
            this._control.updateProgress(curr);
         }
      }
   }

   private final void incrementTotalProgress() {
      this._progressCurrent += 1;
      this.updateProgress(true);
   }

   private final void checkBackupProgress(SyncCollection sc, int curr) {
      if (curr != this._progressCurrent) {
         String syncName = OTAUpgradeControl$SCSafe.getSyncName(sc);
         logInfo(
            ((StringBuffer)(new Object("backup; '")))
               .append(syncName)
               .append("' progress mismatch ")
               .append(this._progressCurrent)
               .append(" should be ")
               .append(curr)
               .toString()
         );
      }

      while (this._progressCurrent < curr) {
         this.incrementTotalProgress();
      }
   }

   private final long readLongFromStream(long param1, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: i2l
      // 02: lstore 4
      // 04: new java/lang/Object
      // 07: dup
      // 08: lload 1
      // 09: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 0c: astore 6
      // 0e: new java/lang/Object
      // 11: dup
      // 12: bipush 0
      // 13: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 16: astore 7
      // 18: ldc_w 2147483647
      // 1b: istore 8
      // 1d: aload 0
      // 1e: aload 6
      // 20: aload 7
      // 22: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 25: ifeq 2d
      // 28: ldc_w 2147483647
      // 2b: istore 8
      // 2d: iload 8
      // 2f: aload 7
      // 31: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 34: istore 8
      // 36: iload 8
      // 38: ifgt 3e
      // 3b: goto 5e
      // 3e: aload 7
      // 40: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 43: istore 9
      // 45: iload 9
      // 47: iload 3
      // 48: if_icmpne 55
      // 4b: aload 7
      // 4d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 50: lstore 4
      // 52: goto 1d
      // 55: aload 7
      // 57: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 5a: pop
      // 5b: goto 1d
      // 5e: aload 6
      // 60: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 63: goto 7a
      // 66: astore 7
      // 68: aload 6
      // 6a: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 6d: goto 7a
      // 70: astore 10
      // 72: aload 6
      // 74: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 77: aload 10
      // 79: athrow
      // 7a: lload 4
      // 7c: lreturn
      // try (8 -> 43): 46 null
      // try (8 -> 43): 50 null
      // try (46 -> 47): 50 null
      // try (50 -> 51): 50 null
   }

   private final int readIntFromStream(long param1, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: istore 4
      // 03: new java/lang/Object
      // 06: dup
      // 07: lload 1
      // 08: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 0b: astore 5
      // 0d: new java/lang/Object
      // 10: dup
      // 11: bipush 0
      // 12: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 15: astore 6
      // 17: ldc_w 2147483647
      // 1a: istore 7
      // 1c: aload 0
      // 1d: aload 5
      // 1f: aload 6
      // 21: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 24: ifeq 2c
      // 27: ldc_w 2147483647
      // 2a: istore 7
      // 2c: iload 7
      // 2e: aload 6
      // 30: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 33: istore 7
      // 35: iload 7
      // 37: ifgt 3d
      // 3a: goto 5d
      // 3d: aload 6
      // 3f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 42: istore 8
      // 44: iload 8
      // 46: iload 3
      // 47: if_icmpne 54
      // 4a: aload 6
      // 4c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 4f: istore 4
      // 51: goto 1c
      // 54: aload 6
      // 56: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 59: pop
      // 5a: goto 1c
      // 5d: aload 5
      // 5f: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 62: goto 79
      // 65: astore 6
      // 67: aload 5
      // 69: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 6c: goto 79
      // 6f: astore 9
      // 71: aload 5
      // 73: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 76: aload 9
      // 78: athrow
      // 79: iload 4
      // 7b: ireturn
      // try (7 -> 42): 45 null
      // try (7 -> 42): 49 null
      // try (45 -> 46): 49 null
      // try (49 -> 50): 49 null
   }

   private final void appendToStream(long param1, int param3, int param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: bipush 1
      // 06: invokespecial net/rim/vm/FlashOutputStream.<init> (JZ)V
      // 09: astore 5
      // 0b: new java/lang/Object
      // 0e: dup
      // 0f: bipush 0
      // 10: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 13: astore 6
      // 15: aload 6
      // 17: iload 3
      // 18: iload 4
      // 1a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 1d: pop
      // 1e: aload 0
      // 1f: aload 5
      // 21: aload 6
      // 23: bipush 1
      // 24: bipush 4
      // 26: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 29: pop
      // 2a: aload 5
      // 2c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 2f: return
      // 30: astore 6
      // 32: aload 5
      // 34: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 37: return
      // 38: astore 7
      // 3a: aload 5
      // 3c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 3f: aload 7
      // 41: athrow
      // try (6 -> 23): 26 null
      // try (6 -> 23): 30 null
      // try (26 -> 27): 30 null
      // try (30 -> 31): 30 null
   }

   private final void appendToStream(long param1, int param3, long param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: bipush 1
      // 06: invokespecial net/rim/vm/FlashOutputStream.<init> (JZ)V
      // 09: astore 6
      // 0b: new java/lang/Object
      // 0e: dup
      // 0f: bipush 0
      // 10: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 13: astore 7
      // 15: aload 7
      // 17: iload 3
      // 18: lload 4
      // 1a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeLong (Lnet/rim/device/api/util/DataBuffer;IJ)Z
      // 1d: pop
      // 1e: aload 0
      // 1f: aload 6
      // 21: aload 7
      // 23: bipush 1
      // 24: bipush 51
      // 26: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 29: pop
      // 2a: aload 6
      // 2c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 2f: return
      // 30: astore 7
      // 32: aload 6
      // 34: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 37: return
      // 38: astore 8
      // 3a: aload 6
      // 3c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 3f: aload 8
      // 41: athrow
      // try (6 -> 23): 26 null
      // try (6 -> 23): 30 null
      // try (26 -> 27): 30 null
      // try (30 -> 31): 30 null
   }

   private static final void setOTASyncState(boolean on) {
      try {
         SyncManager sm = SyncManager.getInstance();
         if (sm != null) {
            sm.enableOTASync(on);
            if (!on) {
               Process.waitForIdle(5000);
               return;
            }
         }
      } finally {
         return;
      }
   }

   private final void startBackup() {
      long radioIDState = this.readLongFromStream(-4696636443275693781L, 2);
      if (radioIDState == 0) {
         this.appendToStream(-4696636443275693781L, 2, Long.MIN_VALUE | RadioInternal.getActiveRadios());
      }

      OTAUpgrade.requestRadioState(false);
      setOTASyncState(false);
   }

   private final void endBackup() {
      this.doGC(true);
      cleanseMemory();
   }

   private static final void startRestore() {
      setOTASyncState(false);
   }

   private final void endRestore() {
      int radioState = this.readIntFromStream(-4696636443275693781L, 1);
      if (radioState != 0) {
         OTAUpgrade.requestRadioState(radioState == 1);
      } else {
         long radioIDState = this.readLongFromStream(-4696636443275693781L, 2);
         if (radioIDState != 0) {
            OTAUpgrade.activateRadios((int)radioIDState);
         }
      }

      this.testReset(49);
      FlashInputStream.erase(-4696636443275693781L);
      this.testReset(50);
      this.doGC(true);
      cleanseMemory();
      setOTASyncState(true);
   }

   private static final byte[] tryZLIBCompress(byte[] input, int inputOff, int inputLen) {
      Deflater state = (Deflater)(new Object(1, 0, 15));
      return state.compress(input, inputOff, inputLen, 4);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int prepareBufferForWrite(int control, byte[] input, int inputOff, int inputLen, byte[] output, int outputOff) {
      this._compressionTime = this._compressionTime - System.currentTimeMillis();
      boolean var17 = false /* VF: Semaphore variable */;

      int compressedSize;
      try {
         var17 = true;
         if ((control & 1) != 0) {
            byte[] result = tryZLIBCompress(input, inputOff, inputLen);
            compressedSize = result.length;
            Array.resize(output, outputOff + compressedSize);
            System.arraycopy(result, 0, output, outputOff, compressedSize);
         } else {
            compressedSize = CompressUtilities.compress(input, inputOff, inputLen, output, outputOff);
         }

         this._compressedSize += compressedSize;
         this._uncompressedSize += inputLen;
         var17 = false;
      } finally {
         if (var17) {
            this._compressionTime = this._compressionTime + System.currentTimeMillis();
         }
      }

      this._compressionTime = this._compressionTime + System.currentTimeMillis();
      if ((control & 2) != 0) {
         this._encryptionTime = this._encryptionTime - System.currentTimeMillis();
         boolean var14 = false /* VF: Semaphore variable */;

         try {
            var14 = true;
            byte[] var21 = new byte[compressedSize];
            System.arraycopy(output, outputOff, var21, 0, compressedSize);
            Object encoding = PersistentContent.encode(var21, false, true);
            byte[] cipherText = PersistentContent.convertEncodingToByteArray(encoding);
            compressedSize = cipherText.length;
            Array.resize(output, outputOff + compressedSize);
            System.arraycopy(cipherText, 0, output, outputOff, compressedSize);
            var14 = false;
         } finally {
            if (var14) {
               this._encryptionTime = this._encryptionTime + System.currentTimeMillis();
            }
         }

         this._encryptionTime = this._encryptionTime + System.currentTimeMillis();
         return control;
      } else {
         return control;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int prepareBufferFromRead(int control, byte[] input, byte[] output) {
      if ((control & 2) != 0) {
         this._encryptionTime = this._encryptionTime - System.currentTimeMillis();
         boolean var13 = false /* VF: Semaphore variable */;

         try {
            var13 = true;
            Object decompressedSize = PersistentContent.convertByteArrayToEncoding(input);
            input = PersistentContent.decodeByteArray(decompressedSize);
            var13 = false;
         } finally {
            if (var13) {
               this._encryptionTime = this._encryptionTime + System.currentTimeMillis();
            }
         }

         this._encryptionTime = this._encryptionTime + System.currentTimeMillis();
      }

      this._compressionTime = this._compressionTime - System.currentTimeMillis();
      boolean var10 = false /* VF: Semaphore variable */;

      int decompressedSize;
      try {
         var10 = true;
         if ((control & 1) != 0) {
            Inflater state = (Inflater)(new Object(15));
            byte[] result = state.decompress(input, 0, input.length);
            decompressedSize = result.length;
            Array.resize(output, decompressedSize);
            System.arraycopy(result, 0, output, 0, decompressedSize);
         } else {
            decompressedSize = CompressUtilities.decompress(input, 0, input.length, output, 0, false, false);
         }

         this._compressedSize += input.length;
         this._uncompressedSize += decompressedSize;
         var10 = false;
      } finally {
         if (var10) {
            this._compressionTime = this._compressionTime + System.currentTimeMillis();
         }
      }

      this._compressionTime = this._compressionTime + System.currentTimeMillis();
      return decompressedSize;
   }

   private final void testReset(int code) {
      if (this._testResets) {
         this._control.testReset(code);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean outputToFlash(FlashOutputStream fos, DataBuffer buff, boolean flush, int resetCode) {
      int amountWritten = buff.getArrayPosition();
      if (flush) {
         if (amountWritten <= 0) {
            return false;
         }
      } else if (amountWritten < 131072) {
         return false;
      }

      byte[] raw = buff.getArray();
      int headerLen = 8;
      int flags = 0;
      if (!this._compressBWT) {
         flags |= 1;
      }

      if (this._encryptStream) {
         flags |= 2;
      }

      byte[] compressed = new byte[headerLen + 1];
      flags = this.prepareBufferForWrite(flags, raw, 0, amountWritten, compressed, headerLen);
      int compressedSize = compressed.length - headerLen;
      if (compressedSize <= 0) {
         throw new Object();
      }

      setUShort(compressed, 0, headerLen);
      setUShort(compressed, 2, flags);
      setInt(compressed, 4, compressedSize);
      this._streamTime = this._streamTime - System.currentTimeMillis();
      boolean var13 = false /* VF: Semaphore variable */;

      try {
         var13 = true;
         this.testReset(resetCode);
         fos.write(compressed, 0, headerLen + compressedSize);
         this.testReset(resetCode + 1);
         fos.flush();
         this.testReset(resetCode + 2);
         buff.reset();
         var13 = false;
      } finally {
         if (var13) {
            this._streamTime = this._streamTime + System.currentTimeMillis();
         }
      }

      this._streamTime = this._streamTime + System.currentTimeMillis();
      return true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean inputFromFlash(FlashInputStream fis, DataBuffer buff) {
      if (buff.available() != 0) {
         return false;
      }

      buff.reset();
      int c0 = fis.read();
      if (c0 < 0) {
         return false;
      }

      int c1 = fis.read();
      if (c1 < 0) {
         return false;
      }

      int headerLen = c0 & 0xFF | (c1 & 0xFF) << 8;
      if (headerLen < 8) {
         throw new Object();
      }

      byte[] header = new byte[headerLen - 2];
      int headerAmtRead = fis.read(header, 0, header.length);
      if (headerAmtRead != header.length) {
         throw new Object();
      }

      int flags = header[0] & 255 | (header[1] & 255) << 8;
      if ((flags & -4) != 0) {
         throw new Object();
      }

      int size = 0;
      size |= header[2] & 255;
      size |= (header[3] & 255) << 8;
      size |= (header[4] & 255) << 16;
      size |= (header[5] & 255) << 24;
      byte[] compressed = new byte[size];
      this._streamTime = this._streamTime - System.currentTimeMillis();
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         int decompressed = fis.read(compressed, 0, size);
         if (decompressed != size) {
            throw new Object();
         }

         var14 = false;
      } finally {
         if (var14) {
            this._streamTime = this._streamTime + System.currentTimeMillis();
         }
      }

      this._streamTime = this._streamTime + System.currentTimeMillis();
      byte[] var20 = new byte[0];
      int decompressedSize = this.prepareBufferFromRead(flags, compressed, var20);
      buff.setData(var20, 0, decompressedSize);
      return true;
   }

   private static final void safeClose(OutputStream os) {
      try {
         if (os != null) {
            os.close();
            return;
         }
      } finally {
         return;
      }
   }

   private static final void safeClose(InputStream os) {
      try {
         if (os != null) {
            os.close();
            return;
         }
      } finally {
         return;
      }
   }

   private static final long calcStringGUID(String str) {
      while (true) {
         long guid = StringUtilities.stringHashToLong(str);
         if (guid != 0) {
            return guid;
         }

         str = ((StringBuffer)(new Object())).append(str).append(Integer.toString(str.hashCode())).toString();
      }
   }

   private final boolean isTagPresent(long param1, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 08: astore 4
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: bipush 0
      // 0f: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 12: astore 5
      // 14: ldc_w 2147483647
      // 17: istore 6
      // 19: aload 0
      // 1a: aload 4
      // 1c: aload 5
      // 1e: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 21: ifeq 29
      // 24: ldc_w 2147483647
      // 27: istore 6
      // 29: iload 6
      // 2b: aload 5
      // 2d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 30: istore 6
      // 32: iload 6
      // 34: ifgt 3a
      // 37: goto 5b
      // 3a: aload 5
      // 3c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 3f: istore 7
      // 41: iload 7
      // 43: iload 3
      // 44: if_icmpne 52
      // 47: bipush 1
      // 48: istore 8
      // 4a: aload 4
      // 4c: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 4f: iload 8
      // 51: ireturn
      // 52: aload 5
      // 54: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 57: pop
      // 58: goto 19
      // 5b: aload 4
      // 5d: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 60: bipush 0
      // 61: ireturn
      // 62: astore 5
      // 64: aload 4
      // 66: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 69: bipush 0
      // 6a: ireturn
      // 6b: astore 9
      // 6d: aload 4
      // 6f: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 72: aload 9
      // 74: athrow
      // try (5 -> 34): 46 null
      // try (38 -> 42): 46 null
      // try (5 -> 34): 51 null
      // try (38 -> 42): 51 null
      // try (46 -> 47): 51 null
      // try (51 -> 52): 51 null
   }

   private final void transferControl(long fromGUID, long toGUID) {
      if (FlashInputStream.exists(fromGUID)) {
         this.transferRegisteredCollections(fromGUID, toGUID);
         this.testReset(37);
         FlashInputStream.erase(fromGUID);
         this.testReset(38);
      }
   }

   private final boolean initBackup(SyncCollection[] syncCollections) {
      boolean deleteBackups = true;
      if (FlashInputStream.exists(-7484766735474520310L)) {
         int restoreStatus = this.readIntFromStream(-7484766735474520310L, 13);
         switch (restoreStatus) {
            case 1:
               deleteBackups = false;
         }
      }

      if (!FlashInputStream.exists(-3904873326663712912L)) {
         if (deleteBackups) {
            label53:
            try {
               for (int i = syncCollections.length - 1; i >= 0; i--) {
                  SyncCollection sc = syncCollections[i];
                  String syncName = OTAUpgradeControl$SCSafe.getSyncName(sc);
                  long guid = calcStringGUID(syncName);
                  this.testReset(39);
                  FlashInputStream.erase(guid);
                  this.testReset(40);
               }
            } finally {
               break label53;
            }
         }

         this.appendToStream(-3904873326663712912L, 1, 65537);
      }

      if (FlashInputStream.exists(-7484766735474520310L)) {
         int streamVersion = this.readIntFromStream(-7484766735474520310L, 1);
         if (streamVersion > 65537) {
            logInfo(((StringBuffer)(new Object("backup; restore stream format is of unknown format (version:"))).append(streamVersion).append(')').toString());
            return true;
         }
      }

      this.transferControl(-7484766735474520310L, -3904873326663712912L);
      return this.isTagPresent(-3904873326663712912L, 2);
   }

   private final void finishStream(long param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: bipush 1
      // 06: invokespecial net/rim/vm/FlashOutputStream.<init> (JZ)V
      // 09: astore 3
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: bipush 0
      // 0f: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 12: astore 4
      // 14: aload 4
      // 16: bipush 13
      // 18: aload 0
      // 19: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._status I
      // 1c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 1f: pop
      // 20: aload 4
      // 22: bipush 1
      // 23: ldc_w 65537
      // 26: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 29: pop
      // 2a: aload 4
      // 2c: bipush 2
      // 2e: invokestatic net/rim/device/api/crypto/RandomSource.getInt ()I
      // 31: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 34: pop
      // 35: aload 0
      // 36: aload 3
      // 37: aload 4
      // 39: bipush 1
      // 3a: bipush 7
      // 3c: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 3f: pop
      // 40: aload 3
      // 41: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 44: return
      // 45: astore 4
      // 47: aload 3
      // 48: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 4b: return
      // 4c: astore 5
      // 4e: aload 3
      // 4f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 52: aload 5
      // 54: athrow
      // try (6 -> 34): 37 null
      // try (6 -> 34): 41 null
      // try (37 -> 38): 41 null
      // try (41 -> 42): 41 null
   }

   private final boolean isCollectionBackupRegistered(long param1, long param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 08: astore 5
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: bipush 0
      // 0f: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 12: astore 6
      // 14: ldc_w 2147483647
      // 17: istore 7
      // 19: aload 0
      // 1a: aload 5
      // 1c: aload 6
      // 1e: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 21: ifeq 29
      // 24: ldc_w 2147483647
      // 27: istore 7
      // 29: iload 7
      // 2b: aload 6
      // 2d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 30: istore 7
      // 32: iload 7
      // 34: ifgt 3a
      // 37: goto 6a
      // 3a: aload 6
      // 3c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 3f: istore 8
      // 41: iload 8
      // 43: bipush 3
      // 45: if_icmpne 61
      // 48: aload 6
      // 4a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 4d: lstore 9
      // 4f: lload 9
      // 51: lload 3
      // 52: lcmp
      // 53: ifne 19
      // 56: bipush 1
      // 57: istore 11
      // 59: aload 5
      // 5b: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 5e: iload 11
      // 60: ireturn
      // 61: aload 6
      // 63: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 66: pop
      // 67: goto 19
      // 6a: aload 5
      // 6c: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 6f: bipush 0
      // 70: ireturn
      // 71: astore 6
      // 73: aload 5
      // 75: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 78: bipush 0
      // 79: ireturn
      // 7a: astore 12
      // 7c: aload 5
      // 7e: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 81: aload 12
      // 83: athrow
      // try (5 -> 41): 53 null
      // try (45 -> 49): 53 null
      // try (5 -> 41): 58 null
      // try (45 -> 49): 58 null
      // try (53 -> 54): 58 null
      // try (58 -> 59): 58 null
   }

   private final void eraseRegisteredCollections(long param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 08: astore 3
      // 09: new java/lang/Object
      // 0c: dup
      // 0d: bipush 0
      // 0e: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 11: astore 4
      // 13: ldc_w 2147483647
      // 16: istore 5
      // 18: aload 0
      // 19: aload 3
      // 1a: aload 4
      // 1c: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 1f: ifeq 27
      // 22: ldc_w 2147483647
      // 25: istore 5
      // 27: iload 5
      // 29: aload 4
      // 2b: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 2e: istore 5
      // 30: iload 5
      // 32: ifgt 38
      // 35: goto 6a
      // 38: aload 4
      // 3a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 3d: istore 6
      // 3f: iload 6
      // 41: bipush 3
      // 43: if_icmpne 61
      // 46: aload 4
      // 48: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 4b: lstore 7
      // 4d: aload 0
      // 4e: bipush 41
      // 50: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 53: lload 7
      // 55: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 58: aload 0
      // 59: bipush 42
      // 5b: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 5e: goto 18
      // 61: aload 4
      // 63: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 66: pop
      // 67: goto 18
      // 6a: aload 3
      // 6b: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 6e: return
      // 6f: astore 4
      // 71: aload 3
      // 72: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 75: return
      // 76: astore 9
      // 78: aload 3
      // 79: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 7c: aload 9
      // 7e: athrow
      // try (5 -> 48): 51 null
      // try (5 -> 48): 55 null
      // try (51 -> 52): 55 null
      // try (55 -> 56): 55 null
   }

   private final void initCollectionStream(long param1, boolean param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: invokespecial net/rim/vm/FlashOutputStream.<init> (J)V
      // 08: astore 4
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: bipush 0
      // 0f: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 12: astore 5
      // 14: aload 5
      // 16: bipush 8
      // 18: iload 3
      // 19: ifeq 20
      // 1c: bipush 1
      // 1d: goto 21
      // 20: bipush 0
      // 21: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 24: pop
      // 25: aload 0
      // 26: aload 4
      // 28: aload 5
      // 2a: bipush 1
      // 2b: bipush 10
      // 2d: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 30: pop
      // 31: aload 4
      // 33: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 36: return
      // 37: astore 5
      // 39: aload 4
      // 3b: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 3e: return
      // 3f: astore 6
      // 41: aload 4
      // 43: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 46: aload 6
      // 48: athrow
      // try (5 -> 26): 29 null
      // try (5 -> 26): 33 null
      // try (29 -> 30): 33 null
      // try (33 -> 34): 33 null
   }

   private final void doRegisterCollectionInfo(long outputGUID, long collectionGUID, SyncCollection sc) {
      String syncName = OTAUpgradeControl$SCSafe.getSyncName(sc);
      int syncVersion = OTAUpgradeControl$SCSafe.getSyncVersion(sc);
      int syncCount = OTAUpgradeControl$SCSafe.getSyncObjectCount(sc);
      this.doRegisterCollectionInfo(outputGUID, collectionGUID, syncName, syncVersion, syncCount);
   }

   private final void doRegisterCollectionInfo(long param1, long param3, String param5, int param6, int param7) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: bipush 1
      // 06: invokespecial net/rim/vm/FlashOutputStream.<init> (JZ)V
      // 09: astore 8
      // 0b: new java/lang/Object
      // 0e: dup
      // 0f: bipush 0
      // 10: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 13: astore 9
      // 15: aload 9
      // 17: bipush 3
      // 19: lload 3
      // 1a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeLong (Lnet/rim/device/api/util/DataBuffer;IJ)Z
      // 1d: pop
      // 1e: aload 9
      // 20: bipush 4
      // 22: aload 5
      // 24: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeString (Lnet/rim/device/api/util/DataBuffer;ILjava/lang/String;)Z
      // 27: pop
      // 28: aload 9
      // 2a: bipush 9
      // 2c: iload 7
      // 2e: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 31: pop
      // 32: aload 9
      // 34: bipush 5
      // 36: iload 6
      // 38: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 3b: pop
      // 3c: aload 0
      // 3d: aload 8
      // 3f: aload 9
      // 41: bipush 1
      // 42: bipush 13
      // 44: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 47: pop
      // 48: aload 8
      // 4a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 4d: return
      // 4e: astore 9
      // 50: aload 8
      // 52: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 55: return
      // 56: astore 10
      // 58: aload 8
      // 5a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 5d: aload 10
      // 5f: athrow
      // try (6 -> 38): 41 null
      // try (6 -> 38): 45 null
      // try (41 -> 42): 45 null
      // try (45 -> 46): 45 null
   }

   private final void registerCollectionBackup(long guid, SyncCollection sc, boolean undoPhase) {
      if (!this.isCollectionBackupRegistered(-3904873326663712912L, guid)) {
         this.initCollectionStream(guid, undoPhase);
         this.doRegisterCollectionInfo(-3904873326663712912L, guid, sc);
      }
   }

   private final IntHashtable establishBackedUpUIDs(long param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: sipush 1031
      // 07: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 0a: astore 3
      // 0b: new java/lang/Object
      // 0e: dup
      // 0f: lload 1
      // 10: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 13: astore 4
      // 15: new java/lang/Object
      // 18: dup
      // 19: bipush 0
      // 1a: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 1d: astore 5
      // 1f: ldc_w 2147483647
      // 22: istore 6
      // 24: aload 0
      // 25: aload 4
      // 27: aload 5
      // 29: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 2c: ifeq 34
      // 2f: ldc_w 2147483647
      // 32: istore 6
      // 34: iload 6
      // 36: aload 5
      // 38: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 3b: istore 6
      // 3d: iload 6
      // 3f: ifgt 45
      // 42: goto 6e
      // 45: aload 5
      // 47: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 4a: istore 7
      // 4c: iload 7
      // 4e: bipush 6
      // 50: if_icmpne 65
      // 53: aload 5
      // 55: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 58: istore 8
      // 5a: aload 3
      // 5b: iload 8
      // 5d: aload 3
      // 5e: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 61: pop
      // 62: goto 24
      // 65: aload 5
      // 67: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 6a: pop
      // 6b: goto 24
      // 6e: aload 4
      // 70: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 73: goto 8a
      // 76: astore 5
      // 78: aload 4
      // 7a: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 7d: goto 8a
      // 80: astore 9
      // 82: aload 4
      // 84: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 87: aload 9
      // 89: athrow
      // 8a: aload 3
      // 8b: invokevirtual net/rim/device/api/util/IntHashtable.size ()I
      // 8e: ifne 93
      // 91: aconst_null
      // 92: astore 3
      // 93: aload 3
      // 94: areturn
      // try (10 -> 50): 53 null
      // try (10 -> 50): 57 null
      // try (53 -> 54): 57 null
      // try (57 -> 58): 57 null
   }

   private final void doEndTransaction(SyncCollection sc) {
      DirtyBits.commit();
      OTAUpgradeControl$SCSafe.endTransaction(sc);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void removeBackedUpObjects(SyncCollection sc, IntHashtable check, SyncObject[] syncObjs) {
      if (check != null) {
         synchronized (RIMPersistentStore.getSynchObject()) {
            synchronized (CollectionLock.getGlobalLock()) {
               if (OTAUpgradeControl$SCSafe.beginTransaction(sc)) {
                  boolean var16 = false /* VF: Semaphore variable */;

                  try {
                     var16 = true;

                     for (int i = syncObjs.length - 1; i >= 0; i--) {
                        SyncObject so = syncObjs[i];
                        if (so != null) {
                           int uid = OTAUpgradeControl$SCSafe.getUID(so);
                           if (check.get(uid) != null) {
                              if (!OTAUpgradeControl$SCSafe.removeSyncObject(sc, so)) {
                                 String syncName = OTAUpgradeControl$SCSafe.getSyncName(sc);
                                 logInfo(((StringBuffer)(new Object("backup; could not remove "))).append(uid).append(" from ").append(syncName).toString());
                              }

                              syncObjs[i] = null;
                           }
                        }
                     }

                     var16 = false;
                  } finally {
                     if (var16) {
                        this.doEndTransaction(sc);
                     }
                  }

                  this.doEndTransaction(sc);
               }
            }
         }

         letSystemRecover(25000);
      }
   }

   private final int getItemFlags(SyncCollection sc, SyncObject so) {
      int flags = 0;
      if (OTAUpgradeControl$SCSafe.isSyncObjectDirty(sc, so)) {
         flags |= 1;
      }

      return flags;
   }

   private final void setItemFlags(int flags, SyncCollection sc, SyncObject so) {
      if ((flags & 1) != 0) {
         OTAUpgradeControl$SCSafe.setSyncObjectDirty(sc, so);
      } else {
         OTAUpgradeControl$SCSafe.clearSyncObjectDirty(sc, so);
      }
   }

   private final void updateDatabaseProgress(int curr, int limit) {
      if (limit == 0) {
         limit = 1;
      }

      if (curr > limit) {
         curr = limit;
      } else if (curr < 1) {
         curr = 1;
      }

      int progress = 99 * curr / limit;
      if (progress < 1) {
         progress = 1;
      }

      this._control.updateDatabaseProgress(null, progress);
   }

   private static final void writeBytes(DataBuffer buff, byte[] bytes) {
      int len = bytes.length;
      if (len > 65280) {
         OTAUpgradeControl$CUSafe.writeByteStream(buff, 12, bytes);
      } else {
         OTAUpgradeControl$CUSafe.writeByteArray(buff, 7, bytes);
      }
   }

   private static final byte[] readBytes(DataBuffer buff, int tag) {
      if (tag == 12) {
         byte[] bytes = OTAUpgradeControl$CUSafe.readByteStream(buff);
         return bytes;
      } else {
         return OTAUpgradeControl$CUSafe.readByteArray(buff);
      }
   }

   private final int backupCollectionChunk(FlashOutputStream param1, SyncCollection param2, SyncObject[] param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 4
      // 003: invokestatic net/rim/device/api/system/RIMPersistentStore.getSynchObject ()Ljava/lang/Object;
      // 006: dup
      // 007: astore 5
      // 009: monitorenter
      // 00a: invokestatic net/rim/device/api/collection/CollectionLock.getGlobalLock ()Lnet/rim/device/api/collection/CollectionLock;
      // 00d: dup
      // 00e: astore 6
      // 010: monitorenter
      // 011: bipush 0
      // 012: istore 7
      // 014: bipush 0
      // 015: istore 8
      // 017: bipush 0
      // 018: istore 9
      // 01a: new java/lang/Object
      // 01d: dup
      // 01e: invokespecial java/util/Vector.<init> ()V
      // 021: astore 10
      // 023: aload 2
      // 024: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.beginTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 027: ifne 02d
      // 02a: goto 4b8
      // 02d: bipush 0
      // 02e: istore 11
      // 030: aload 2
      // 031: dup
      // 032: instanceof java/lang/Object
      // 035: ifne 03c
      // 038: pop
      // 039: goto 048
      // 03c: checkcast java/lang/Object
      // 03f: astore 12
      // 041: aload 12
      // 043: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSPSafe.getOTASLControlMask (Lnet/rim/device/api/synchronization/SyncCollectionStatusProvider;)I
      // 046: istore 11
      // 048: new java/lang/Object
      // 04b: dup
      // 04c: bipush 0
      // 04d: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 050: astore 12
      // 052: new java/lang/Object
      // 055: dup
      // 056: bipush 0
      // 057: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 05a: astore 13
      // 05c: aload 2
      // 05d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncConverter (Lnet/rim/device/api/synchronization/SyncCollection;)Lnet/rim/device/api/synchronization/SyncConverter;
      // 060: astore 14
      // 062: aload 2
      // 063: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncVersion (Lnet/rim/device/api/synchronization/SyncCollection;)I
      // 066: istore 15
      // 068: aload 3
      // 069: arraylength
      // 06a: istore 16
      // 06c: bipush 0
      // 06d: istore 17
      // 06f: iload 17
      // 071: iload 16
      // 073: if_icmplt 079
      // 076: goto 1f5
      // 079: aload 0
      // 07a: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 07d: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.stop ()Z 1
      // 082: ifne 088
      // 085: goto 16b
      // 088: iload 7
      // 08a: ifne 090
      // 08d: goto 163
      // 090: bipush 1
      // 091: istore 22
      // 093: iload 11
      // 095: bipush 1
      // 096: iand
      // 097: ifeq 09d
      // 09a: bipush 0
      // 09b: istore 22
      // 09d: iload 22
      // 09f: ifne 0a5
      // 0a2: goto 163
      // 0a5: aload 2
      // 0a6: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 0a9: astore 23
      // 0ab: aload 10
      // 0ad: invokevirtual java/util/Vector.size ()I
      // 0b0: istore 24
      // 0b2: aload 0
      // 0b3: aload 0
      // 0b4: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 0b7: iload 24
      // 0b9: iadd
      // 0ba: putfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 0bd: iload 9
      // 0bf: iload 24
      // 0c1: isub
      // 0c2: istore 25
      // 0c4: iload 8
      // 0c6: ifeq 0f0
      // 0c9: iload 25
      // 0cb: ifne 0f0
      // 0ce: aload 2
      // 0cf: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeAllSyncObjects (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 0d2: ifeq 0d8
      // 0d5: goto 163
      // 0d8: new java/lang/Object
      // 0db: dup
      // 0dc: ldc_w "backup; could not removeall from "
      // 0df: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0e2: aload 23
      // 0e4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0e7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ea: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 0ed: goto 163
      // 0f0: iload 24
      // 0f2: bipush 1
      // 0f3: isub
      // 0f4: istore 26
      // 0f6: iload 26
      // 0f8: iflt 139
      // 0fb: aload 10
      // 0fd: iload 26
      // 0ff: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 102: checkcast java/lang/Object
      // 105: astore 27
      // 107: aload 2
      // 108: aload 27
      // 10a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 10d: ifne 133
      // 110: new java/lang/Object
      // 113: dup
      // 114: ldc_w "backup; could not remove "
      // 117: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 11a: aload 27
      // 11c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getUID (Lnet/rim/device/api/synchronization/SyncObject;)I
      // 11f: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 122: ldc_w " from "
      // 125: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 128: aload 23
      // 12a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 12d: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 130: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 133: iinc 26 -1
      // 136: goto 0f6
      // 139: iload 8
      // 13b: ifeq 163
      // 13e: new java/lang/Object
      // 141: dup
      // 142: ldc_w "backup failed; "
      // 145: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 148: aload 23
      // 14a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 14d: ldc_w " items left: "
      // 150: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 153: iload 25
      // 155: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 158: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 15b: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 15e: goto 163
      // 161: astore 23
      // 163: aload 0
      // 164: aload 2
      // 165: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 168: goto 4b8
      // 16b: aload 3
      // 16c: iload 17
      // 16e: aaload
      // 16f: astore 18
      // 171: aload 18
      // 173: ifnonnull 179
      // 176: goto 1ef
      // 179: aload 0
      // 17a: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.incrementTotalProgress ()V
      // 17d: aload 0
      // 17e: iload 17
      // 180: iload 16
      // 182: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.updateDatabaseProgress (II)V
      // 185: aload 13
      // 187: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 18a: iinc 9 1
      // 18d: aload 14
      // 18f: aload 18
      // 191: aload 13
      // 193: iload 15
      // 195: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.convert (Lnet/rim/device/api/synchronization/SyncConverter;Lnet/rim/device/api/synchronization/SyncObject;Lnet/rim/device/api/util/DataBuffer;I)Z
      // 198: ifeq 1ef
      // 19b: aload 13
      // 19d: invokevirtual net/rim/device/api/util/DataBuffer.trim ()V
      // 1a0: aload 12
      // 1a2: bipush 6
      // 1a4: aload 18
      // 1a6: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getUID (Lnet/rim/device/api/synchronization/SyncObject;)I
      // 1a9: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 1ac: pop
      // 1ad: aload 12
      // 1af: bipush 11
      // 1b1: aload 0
      // 1b2: aload 2
      // 1b3: aload 18
      // 1b5: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.getItemFlags (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)I
      // 1b8: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 1bb: pop
      // 1bc: aload 12
      // 1be: aload 13
      // 1c0: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 1c3: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.writeBytes (Lnet/rim/device/api/util/DataBuffer;[B)V
      // 1c6: aload 0
      // 1c7: aload 1
      // 1c8: aload 12
      // 1ca: bipush 0
      // 1cb: bipush 16
      // 1cd: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 1d0: istore 19
      // 1d2: aload 10
      // 1d4: aload 18
      // 1d6: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1d9: aload 3
      // 1da: iload 17
      // 1dc: aconst_null
      // 1dd: aastore
      // 1de: iload 19
      // 1e0: ifeq 1ef
      // 1e3: bipush 1
      // 1e4: istore 7
      // 1e6: iload 17
      // 1e8: istore 4
      // 1ea: goto 1f5
      // 1ed: astore 19
      // 1ef: iinc 17 1
      // 1f2: goto 06f
      // 1f5: aload 0
      // 1f6: aload 1
      // 1f7: aload 12
      // 1f9: bipush 1
      // 1fa: bipush 19
      // 1fc: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 1ff: ifeq 208
      // 202: bipush 1
      // 203: istore 7
      // 205: bipush 1
      // 206: istore 8
      // 208: invokestatic net/rim/device/api/lowmemory/LowMemoryManager.poll ()V
      // 20b: iload 7
      // 20d: ifne 213
      // 210: goto 2e6
      // 213: bipush 1
      // 214: istore 22
      // 216: iload 11
      // 218: bipush 1
      // 219: iand
      // 21a: ifeq 220
      // 21d: bipush 0
      // 21e: istore 22
      // 220: iload 22
      // 222: ifne 228
      // 225: goto 2e6
      // 228: aload 2
      // 229: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 22c: astore 23
      // 22e: aload 10
      // 230: invokevirtual java/util/Vector.size ()I
      // 233: istore 24
      // 235: aload 0
      // 236: aload 0
      // 237: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 23a: iload 24
      // 23c: iadd
      // 23d: putfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 240: iload 9
      // 242: iload 24
      // 244: isub
      // 245: istore 25
      // 247: iload 8
      // 249: ifeq 273
      // 24c: iload 25
      // 24e: ifne 273
      // 251: aload 2
      // 252: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeAllSyncObjects (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 255: ifeq 25b
      // 258: goto 2e6
      // 25b: new java/lang/Object
      // 25e: dup
      // 25f: ldc_w "backup; could not removeall from "
      // 262: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 265: aload 23
      // 267: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 26a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 26d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 270: goto 2e6
      // 273: iload 24
      // 275: bipush 1
      // 276: isub
      // 277: istore 26
      // 279: iload 26
      // 27b: iflt 2bc
      // 27e: aload 10
      // 280: iload 26
      // 282: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 285: checkcast java/lang/Object
      // 288: astore 27
      // 28a: aload 2
      // 28b: aload 27
      // 28d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 290: ifne 2b6
      // 293: new java/lang/Object
      // 296: dup
      // 297: ldc_w "backup; could not remove "
      // 29a: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 29d: aload 27
      // 29f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getUID (Lnet/rim/device/api/synchronization/SyncObject;)I
      // 2a2: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 2a5: ldc_w " from "
      // 2a8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2ab: aload 23
      // 2ad: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2b0: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2b3: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 2b6: iinc 26 -1
      // 2b9: goto 279
      // 2bc: iload 8
      // 2be: ifeq 2e6
      // 2c1: new java/lang/Object
      // 2c4: dup
      // 2c5: ldc_w "backup failed; "
      // 2c8: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 2cb: aload 23
      // 2cd: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2d0: ldc_w " items left: "
      // 2d3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2d6: iload 25
      // 2d8: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 2db: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2de: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 2e1: goto 2e6
      // 2e4: astore 23
      // 2e6: aload 0
      // 2e7: aload 2
      // 2e8: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 2eb: goto 4b8
      // 2ee: astore 12
      // 2f0: iload 7
      // 2f2: ifne 2f8
      // 2f5: goto 3cb
      // 2f8: bipush 1
      // 2f9: istore 22
      // 2fb: iload 11
      // 2fd: bipush 1
      // 2fe: iand
      // 2ff: ifeq 305
      // 302: bipush 0
      // 303: istore 22
      // 305: iload 22
      // 307: ifne 30d
      // 30a: goto 3cb
      // 30d: aload 2
      // 30e: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 311: astore 23
      // 313: aload 10
      // 315: invokevirtual java/util/Vector.size ()I
      // 318: istore 24
      // 31a: aload 0
      // 31b: aload 0
      // 31c: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 31f: iload 24
      // 321: iadd
      // 322: putfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 325: iload 9
      // 327: iload 24
      // 329: isub
      // 32a: istore 25
      // 32c: iload 8
      // 32e: ifeq 358
      // 331: iload 25
      // 333: ifne 358
      // 336: aload 2
      // 337: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeAllSyncObjects (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 33a: ifeq 340
      // 33d: goto 3cb
      // 340: new java/lang/Object
      // 343: dup
      // 344: ldc_w "backup; could not removeall from "
      // 347: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 34a: aload 23
      // 34c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 34f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 352: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 355: goto 3cb
      // 358: iload 24
      // 35a: bipush 1
      // 35b: isub
      // 35c: istore 26
      // 35e: iload 26
      // 360: iflt 3a1
      // 363: aload 10
      // 365: iload 26
      // 367: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 36a: checkcast java/lang/Object
      // 36d: astore 27
      // 36f: aload 2
      // 370: aload 27
      // 372: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 375: ifne 39b
      // 378: new java/lang/Object
      // 37b: dup
      // 37c: ldc_w "backup; could not remove "
      // 37f: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 382: aload 27
      // 384: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getUID (Lnet/rim/device/api/synchronization/SyncObject;)I
      // 387: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 38a: ldc_w " from "
      // 38d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 390: aload 23
      // 392: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 395: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 398: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 39b: iinc 26 -1
      // 39e: goto 35e
      // 3a1: iload 8
      // 3a3: ifeq 3cb
      // 3a6: new java/lang/Object
      // 3a9: dup
      // 3aa: ldc_w "backup failed; "
      // 3ad: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 3b0: aload 23
      // 3b2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3b5: ldc_w " items left: "
      // 3b8: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 3bb: iload 25
      // 3bd: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 3c0: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 3c3: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 3c6: goto 3cb
      // 3c9: astore 23
      // 3cb: aload 0
      // 3cc: aload 2
      // 3cd: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 3d0: goto 4b8
      // 3d3: astore 20
      // 3d5: iload 7
      // 3d7: ifne 3dd
      // 3da: goto 4b0
      // 3dd: bipush 1
      // 3de: istore 22
      // 3e0: iload 11
      // 3e2: bipush 1
      // 3e3: iand
      // 3e4: ifeq 3ea
      // 3e7: bipush 0
      // 3e8: istore 22
      // 3ea: iload 22
      // 3ec: ifne 3f2
      // 3ef: goto 4b0
      // 3f2: aload 2
      // 3f3: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 3f6: astore 23
      // 3f8: aload 10
      // 3fa: invokevirtual java/util/Vector.size ()I
      // 3fd: istore 24
      // 3ff: aload 0
      // 400: aload 0
      // 401: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 404: iload 24
      // 406: iadd
      // 407: putfield net/rim/device/internal/synchronization/OTAUpgradeControl._itemsRemoved I
      // 40a: iload 9
      // 40c: iload 24
      // 40e: isub
      // 40f: istore 25
      // 411: iload 8
      // 413: ifeq 43d
      // 416: iload 25
      // 418: ifne 43d
      // 41b: aload 2
      // 41c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeAllSyncObjects (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 41f: ifeq 425
      // 422: goto 4b0
      // 425: new java/lang/Object
      // 428: dup
      // 429: ldc_w "backup; could not removeall from "
      // 42c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 42f: aload 23
      // 431: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 434: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 437: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 43a: goto 4b0
      // 43d: iload 24
      // 43f: bipush 1
      // 440: isub
      // 441: istore 26
      // 443: iload 26
      // 445: iflt 486
      // 448: aload 10
      // 44a: iload 26
      // 44c: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 44f: checkcast java/lang/Object
      // 452: astore 27
      // 454: aload 2
      // 455: aload 27
      // 457: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 45a: ifne 480
      // 45d: new java/lang/Object
      // 460: dup
      // 461: ldc_w "backup; could not remove "
      // 464: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 467: aload 27
      // 469: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getUID (Lnet/rim/device/api/synchronization/SyncObject;)I
      // 46c: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 46f: ldc_w " from "
      // 472: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 475: aload 23
      // 477: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 47a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 47d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 480: iinc 26 -1
      // 483: goto 443
      // 486: iload 8
      // 488: ifeq 4b0
      // 48b: new java/lang/Object
      // 48e: dup
      // 48f: ldc_w "backup failed; "
      // 492: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 495: aload 23
      // 497: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 49a: ldc_w " items left: "
      // 49d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 4a0: iload 25
      // 4a2: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 4a5: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 4a8: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 4ab: goto 4b0
      // 4ae: astore 23
      // 4b0: aload 0
      // 4b1: aload 2
      // 4b2: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 4b5: aload 20
      // 4b7: athrow
      // 4b8: aload 6
      // 4ba: monitorexit
      // 4bb: goto 4c6
      // 4be: astore 28
      // 4c0: aload 6
      // 4c2: monitorexit
      // 4c3: aload 28
      // 4c5: athrow
      // 4c6: aload 0
      // 4c7: bipush 31
      // 4c9: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 4cc: aload 5
      // 4ce: monitorexit
      // 4cf: goto 4da
      // 4d2: astore 29
      // 4d4: aload 5
      // 4d6: monitorexit
      // 4d7: aload 29
      // 4d9: athrow
      // 4da: sipush 25000
      // 4dd: i2l
      // 4de: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.letSystemRecover (J)V
      // 4e1: aload 0
      // 4e2: bipush 32
      // 4e4: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 4e7: iload 4
      // 4e9: ireturn
      // try (179 -> 226): 227 null
      // try (37 -> 67): 339 null
      // try (164 -> 242): 339 null
      // try (37 -> 67): 437 null
      // try (164 -> 242): 437 null
      // try (339 -> 340): 437 null
      // try (437 -> 438): 437 null
      // try (452 -> 529): 530 null
      // try (354 -> 431): 432 null
      // try (256 -> 333): 334 null
      // try (81 -> 158): 159 null
      // try (10 -> 538): 539 null
      // try (539 -> 542): 539 null
      // try (6 -> 549): 550 null
      // try (550 -> 553): 550 null
   }

   private final IntHashtable addUndoItems(SyncCollection param1, FlashOutputStream param2, IntHashtable param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 004: astore 4
      // 006: aload 4
      // 008: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.makeUndoName (Ljava/lang/String;)Ljava/lang/String;
      // 00b: astore 5
      // 00d: aload 5
      // 00f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.calcStringGUID (Ljava/lang/String;)J
      // 012: lstore 6
      // 014: aload 0
      // 015: lload 6
      // 017: aload 5
      // 019: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.findCollectionBackupVersion (JLjava/lang/String;)I
      // 01c: istore 8
      // 01e: aload 1
      // 01f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncVersion (Lnet/rim/device/api/synchronization/SyncCollection;)I
      // 022: istore 9
      // 024: bipush 0
      // 025: istore 10
      // 027: new java/lang/Object
      // 02a: dup
      // 02b: lload 6
      // 02d: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 030: astore 11
      // 032: new java/lang/Object
      // 035: dup
      // 036: invokespecial net/rim/device/api/util/IntVector.<init> ()V
      // 039: astore 12
      // 03b: new java/lang/Object
      // 03e: dup
      // 03f: bipush 0
      // 040: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 043: astore 13
      // 045: new java/lang/Object
      // 048: dup
      // 049: bipush 0
      // 04a: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 04d: astore 14
      // 04f: bipush 0
      // 050: istore 15
      // 052: bipush 0
      // 053: istore 16
      // 055: bipush 0
      // 056: istore 17
      // 058: aconst_null
      // 059: astore 18
      // 05b: ldc_w 2147483647
      // 05e: istore 19
      // 060: aload 0
      // 061: aload 11
      // 063: aload 14
      // 065: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 068: ifeq 070
      // 06b: ldc_w 2147483647
      // 06e: istore 19
      // 070: iload 19
      // 072: aload 14
      // 074: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 077: istore 19
      // 079: iload 19
      // 07b: ifgt 081
      // 07e: goto 139
      // 081: aload 14
      // 083: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 086: istore 20
      // 088: iload 20
      // 08a: lookupswitch 162 4 6 42 7 62 11 52 12 62
      // 0b4: aload 14
      // 0b6: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 0b9: istore 16
      // 0bb: goto 132
      // 0be: aload 14
      // 0c0: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 0c3: istore 17
      // 0c5: goto 132
      // 0c8: aload 14
      // 0ca: iload 20
      // 0cc: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.readBytes (Lnet/rim/device/api/util/DataBuffer;I)[B
      // 0cf: astore 18
      // 0d1: iload 15
      // 0d3: bipush 11
      // 0d5: if_icmpeq 0db
      // 0d8: goto 132
      // 0db: aload 3
      // 0dc: ifnull 0eb
      // 0df: aload 3
      // 0e0: iload 16
      // 0e2: invokevirtual net/rim/device/api/util/IntHashtable.get (I)Ljava/lang/Object;
      // 0e5: ifnull 0eb
      // 0e8: goto 132
      // 0eb: iload 8
      // 0ed: iload 9
      // 0ef: if_icmpeq 0fc
      // 0f2: aload 13
      // 0f4: bipush 10
      // 0f6: iload 8
      // 0f8: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 0fb: pop
      // 0fc: aload 13
      // 0fe: bipush 6
      // 100: iload 16
      // 102: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 105: pop
      // 106: aload 13
      // 108: bipush 11
      // 10a: iload 17
      // 10c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 10f: pop
      // 110: aload 13
      // 112: aload 18
      // 114: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.writeBytes (Lnet/rim/device/api/util/DataBuffer;[B)V
      // 117: aload 12
      // 119: iload 16
      // 11b: invokevirtual net/rim/device/api/util/IntVector.addElement (I)V
      // 11e: aload 0
      // 11f: aload 2
      // 120: aload 13
      // 122: bipush 0
      // 123: bipush 28
      // 125: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 128: pop
      // 129: goto 132
      // 12c: aload 14
      // 12e: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 131: pop
      // 132: iload 20
      // 134: istore 15
      // 136: goto 060
      // 139: aload 0
      // 13a: aload 2
      // 13b: aload 13
      // 13d: bipush 1
      // 13e: bipush 1
      // 13f: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 142: pop
      // 143: bipush 1
      // 144: istore 10
      // 146: aload 11
      // 148: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 14b: iload 10
      // 14d: ifne 153
      // 150: goto 252
      // 153: aload 12
      // 155: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 158: istore 23
      // 15a: iload 23
      // 15c: ifeq 18b
      // 15f: aload 3
      // 160: ifnonnull 16d
      // 163: new java/lang/Object
      // 166: dup
      // 167: iload 23
      // 169: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 16c: astore 3
      // 16d: iload 23
      // 16f: bipush 1
      // 170: isub
      // 171: istore 24
      // 173: iload 24
      // 175: iflt 18b
      // 178: aload 3
      // 179: aload 12
      // 17b: iload 24
      // 17d: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 180: aload 3
      // 181: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 184: pop
      // 185: iinc 24 -1
      // 188: goto 173
      // 18b: aload 0
      // 18c: bipush 43
      // 18e: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 191: lload 6
      // 193: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 196: aload 0
      // 197: bipush 44
      // 199: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 19c: goto 252
      // 19f: astore 13
      // 1a1: aload 11
      // 1a3: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 1a6: iload 10
      // 1a8: ifne 1ae
      // 1ab: goto 252
      // 1ae: aload 12
      // 1b0: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 1b3: istore 23
      // 1b5: iload 23
      // 1b7: ifeq 1e6
      // 1ba: aload 3
      // 1bb: ifnonnull 1c8
      // 1be: new java/lang/Object
      // 1c1: dup
      // 1c2: iload 23
      // 1c4: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 1c7: astore 3
      // 1c8: iload 23
      // 1ca: bipush 1
      // 1cb: isub
      // 1cc: istore 24
      // 1ce: iload 24
      // 1d0: iflt 1e6
      // 1d3: aload 3
      // 1d4: aload 12
      // 1d6: iload 24
      // 1d8: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 1db: aload 3
      // 1dc: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 1df: pop
      // 1e0: iinc 24 -1
      // 1e3: goto 1ce
      // 1e6: aload 0
      // 1e7: bipush 43
      // 1e9: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 1ec: lload 6
      // 1ee: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 1f1: aload 0
      // 1f2: bipush 44
      // 1f4: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 1f7: goto 252
      // 1fa: astore 21
      // 1fc: aload 11
      // 1fe: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 201: iload 10
      // 203: ifeq 24f
      // 206: aload 12
      // 208: invokevirtual net/rim/device/api/util/IntVector.size ()I
      // 20b: istore 23
      // 20d: iload 23
      // 20f: ifeq 23e
      // 212: aload 3
      // 213: ifnonnull 220
      // 216: new java/lang/Object
      // 219: dup
      // 21a: iload 23
      // 21c: invokespecial net/rim/device/api/util/IntHashtable.<init> (I)V
      // 21f: astore 3
      // 220: iload 23
      // 222: bipush 1
      // 223: isub
      // 224: istore 24
      // 226: iload 24
      // 228: iflt 23e
      // 22b: aload 3
      // 22c: aload 12
      // 22e: iload 24
      // 230: invokevirtual net/rim/device/api/util/IntVector.elementAt (I)I
      // 233: aload 3
      // 234: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 237: pop
      // 238: iinc 24 -1
      // 23b: goto 226
      // 23e: aload 0
      // 23f: bipush 43
      // 241: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 244: lload 6
      // 246: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 249: aload 0
      // 24a: bipush 44
      // 24c: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 24f: aload 21
      // 251: athrow
      // 252: aload 3
      // 253: areturn
      // try (28 -> 137): 178 null
      // try (28 -> 137): 220 null
      // try (178 -> 179): 220 null
      // try (220 -> 221): 220 null
   }

   private final void finishCollectionBackup(SyncCollection param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 1
      // 01: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getLocalizedSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 04: astore 3
      // 05: aload 1
      // 06: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 09: astore 4
      // 0b: new java/lang/Object
      // 0e: dup
      // 0f: ldc_w "backup+ "
      // 12: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 15: aload 4
      // 17: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1a: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 20: aload 0
      // 21: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 24: aload 3
      // 25: bipush 0
      // 26: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.updateDatabaseProgress (Ljava/lang/String;I)V 3
      // 2b: aload 4
      // 2d: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.calcStringGUID (Ljava/lang/String;)J
      // 30: lstore 5
      // 32: lload 5
      // 34: invokestatic net/rim/vm/FlashInputStream.exists (J)Z
      // 37: ifne 42
      // 3a: aload 0
      // 3b: lload 5
      // 3d: aload 1
      // 3e: iload 2
      // 3f: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.registerCollectionBackup (JLnet/rim/device/api/synchronization/SyncCollection;Z)V
      // 42: aload 1
      // 43: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncObjects (Lnet/rim/device/api/synchronization/SyncCollection;)[Lnet/rim/device/api/synchronization/SyncObject;
      // 46: astore 7
      // 48: aload 0
      // 49: lload 5
      // 4b: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.establishBackedUpUIDs (J)Lnet/rim/device/api/util/IntHashtable;
      // 4e: astore 8
      // 50: new java/lang/Object
      // 53: dup
      // 54: lload 5
      // 56: bipush 1
      // 57: invokespecial net/rim/vm/FlashOutputStream.<init> (JZ)V
      // 5a: astore 9
      // 5c: iload 2
      // 5d: ifeq 6b
      // 60: aload 0
      // 61: aload 1
      // 62: aload 9
      // 64: aload 8
      // 66: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.addUndoItems (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/IntHashtable;)Lnet/rim/device/api/util/IntHashtable;
      // 69: astore 8
      // 6b: aload 0
      // 6c: aload 1
      // 6d: aload 8
      // 6f: aload 7
      // 71: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.removeBackedUpObjects (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/util/IntHashtable;[Lnet/rim/device/api/synchronization/SyncObject;)V
      // 74: aload 0
      // 75: aload 9
      // 77: aload 1
      // 78: aload 7
      // 7a: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.backupCollectionChunk (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/synchronization/SyncCollection;[Lnet/rim/device/api/synchronization/SyncObject;)I
      // 7d: istore 10
      // 7f: iload 10
      // 81: ifne 87
      // 84: goto b1
      // 87: iload 2
      // 88: ifne 9a
      // 8b: aload 0
      // 8c: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._lmmFailed Z
      // 8f: ifeq 9a
      // 92: aload 0
      // 93: bipush 1
      // 94: putfield net/rim/device/internal/synchronization/OTAUpgradeControl._status I
      // 97: goto b1
      // 9a: aload 0
      // 9b: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 9e: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.stop ()Z 1
      // a3: ifeq a9
      // a6: goto b1
      // a9: aload 0
      // aa: bipush 0
      // ab: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doGC (Z)V
      // ae: goto 74
      // b1: aload 9
      // b3: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // b6: goto cd
      // b9: astore 10
      // bb: aload 9
      // bd: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // c0: goto cd
      // c3: astore 11
      // c5: aload 9
      // c7: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // ca: aload 11
      // cc: athrow
      // cd: aload 0
      // ce: bipush 0
      // cf: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doGC (Z)V
      // d2: aload 0
      // d3: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // d6: aload 3
      // d7: bipush 100
      // d9: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.updateDatabaseProgress (Ljava/lang/String;I)V 3
      // de: new java/lang/Object
      // e1: dup
      // e2: ldc_w "backup- "
      // e5: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // e8: aload 4
      // ea: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // ed: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // f0: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // f3: return
      // try (56 -> 83): 86 null
      // try (56 -> 83): 90 null
      // try (86 -> 87): 90 null
      // try (90 -> 91): 90 null
   }

   private final void registerForLMMFailures() {
      LowMemoryManager.addLowMemoryFailedListener(this);
   }

   private final void deregisterForLMMFailures() {
      LowMemoryManager.removeLowMemoryFailedListener(this);
   }

   private static final void cleanseMemory() {
      if (PersistentContent.isEncryptionEnabled()) {
         Memory.secureThoroughGC();
      }
   }

   private final void clearStats() {
      this._compressionTime = 0;
      this._encryptionTime = 0;
      this._gcTime = 0;
      this._streamTime = 0;
      this._totalTime = 0;
      this._compressedSize = 0;
      this._uncompressedSize = 0;
      this._flashFree = Memory.getFlashFree();
      this._itemsRemoved = 0;
      this._progress = 0;
      this._progressTotal = 0;
      this._progressCurrent = 0;
      this._serviceBookLegacyMode = false;
      this._lmmFailed = false;
      this._status = 0;
   }

   private final boolean initRestore() {
      if (FlashInputStream.exists(-3904873326663712912L)) {
         int streamVersion = this.readIntFromStream(-3904873326663712912L, 1);
         if (streamVersion > 65537) {
            logInfo(((StringBuffer)(new Object("restore; backup stream format is of unknown format (version: "))).append(streamVersion).append(')').toString());
            return false;
         } else {
            return this.isTagPresent(-3904873326663712912L, 2);
         }
      } else {
         return false;
      }
   }

   private final void transferRegisteredCollections(long param1, long param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: lload 1
      // 05: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 08: astore 5
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: bipush 0
      // 0f: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 12: astore 6
      // 14: bipush 0
      // 15: i2l
      // 16: lstore 7
      // 18: ldc_w ""
      // 1b: astore 9
      // 1d: bipush 0
      // 1e: istore 10
      // 20: bipush 0
      // 21: istore 11
      // 23: ldc_w 2147483647
      // 26: istore 12
      // 28: aload 0
      // 29: aload 5
      // 2b: aload 6
      // 2d: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 30: ifeq 38
      // 33: ldc_w 2147483647
      // 36: istore 12
      // 38: iload 12
      // 3a: aload 6
      // 3c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 3f: istore 12
      // 41: iload 12
      // 43: ifgt 49
      // 46: goto cc
      // 49: aload 6
      // 4b: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 4e: istore 13
      // 50: iload 13
      // 52: lookupswitch 113 4 3 42 4 52 5 72 9 62
      // 7c: aload 6
      // 7e: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 81: lstore 7
      // 83: goto 28
      // 86: aload 6
      // 88: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 8b: astore 9
      // 8d: goto 28
      // 90: aload 6
      // 92: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 95: istore 10
      // 97: goto 28
      // 9a: aload 6
      // 9c: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 9f: istore 11
      // a1: lload 7
      // a3: invokestatic net/rim/vm/FlashInputStream.exists (J)Z
      // a6: ifeq 28
      // a9: aload 0
      // aa: lload 3
      // ab: lload 7
      // ad: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.isCollectionBackupRegistered (JJ)Z
      // b0: ifne 28
      // b3: aload 0
      // b4: lload 3
      // b5: lload 7
      // b7: aload 9
      // b9: iload 11
      // bb: iload 10
      // bd: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doRegisterCollectionInfo (JJLjava/lang/String;II)V
      // c0: goto 28
      // c3: aload 6
      // c5: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // c8: pop
      // c9: goto 28
      // cc: aload 5
      // ce: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // d1: return
      // d2: astore 6
      // d4: aload 5
      // d6: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // d9: return
      // da: astore 14
      // dc: aload 5
      // de: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // e1: aload 14
      // e3: athrow
      // try (5 -> 75): 78 null
      // try (5 -> 75): 82 null
      // try (78 -> 79): 82 null
      // try (82 -> 83): 82 null
   }

   private final void finiRestore() {
      if (!this.isTagPresent(-7484766735474520310L, 2)) {
         this.transferRegisteredCollections(-3904873326663712912L, -7484766735474520310L);
         this.finishStream(-7484766735474520310L);
      }

      this.testReset(45);
      FlashInputStream.erase(-3904873326663712912L);
      this.testReset(46);
   }

   private final IntHashtable establishRestoredUIDs(SyncCollection sc, IntHashtable uidCheck) {
      IntHashtable originalCheck = uidCheck;
      SyncObject[] syncObjs = OTAUpgradeControl$SCSafe.getSyncObjects(sc);
      int len = syncObjs.length;
      if (uidCheck == null) {
         uidCheck = (IntHashtable)(new Object((len | 1) * 6 / 5));
      } else {
         uidCheck.clear();
      }

      for (int i = len - 1; i >= 0; i--) {
         SyncObject so = syncObjs[i];
         if (so != null) {
            int uid = OTAUpgradeControl$SCSafe.getUID(so);
            uidCheck.put(uid, so);
         }
      }

      if (uidCheck.size() == 0 && uidCheck != originalCheck) {
         uidCheck = null;
      }

      return uidCheck;
   }

   private final boolean restoreCollectionChunk(
      FlashInputStream param1, FlashOutputStream param2, SyncCollection param3, IntHashtable param4, IntHashtable param5, int param6, int[] param7
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 8
      // 003: bipush 0
      // 004: istore 9
      // 006: bipush 1
      // 007: istore 10
      // 009: aload 3
      // 00a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 00d: astore 11
      // 00f: aload 3
      // 010: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncVersion (Lnet/rim/device/api/synchronization/SyncCollection;)I
      // 013: istore 12
      // 015: iload 12
      // 017: iload 6
      // 019: if_icmple 04e
      // 01c: new java/lang/Object
      // 01f: dup
      // 020: ldc_w "restore undo stream saved for "
      // 023: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 026: aload 11
      // 028: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 02b: ldc_w "(version: "
      // 02e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 031: iload 12
      // 033: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 036: bipush 47
      // 038: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 03b: iload 6
      // 03d: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 040: bipush 41
      // 042: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 045: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 048: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 04b: bipush 1
      // 04c: istore 8
      // 04e: aload 2
      // 04f: ifnonnull 058
      // 052: bipush 0
      // 053: istore 10
      // 055: bipush 1
      // 056: istore 9
      // 058: bipush 0
      // 059: istore 13
      // 05b: bipush 0
      // 05c: istore 14
      // 05e: bipush 0
      // 05f: istore 15
      // 061: new java/lang/Object
      // 064: dup
      // 065: bipush 0
      // 066: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 069: astore 16
      // 06b: invokestatic net/rim/device/api/system/RIMPersistentStore.getSynchObject ()Ljava/lang/Object;
      // 06e: dup
      // 06f: astore 17
      // 071: monitorenter
      // 072: invokestatic net/rim/device/api/collection/CollectionLock.getGlobalLock ()Lnet/rim/device/api/collection/CollectionLock;
      // 075: dup
      // 076: astore 18
      // 078: monitorenter
      // 079: aload 3
      // 07a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.beginTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 07d: ifne 083
      // 080: goto 340
      // 083: new java/lang/Object
      // 086: dup
      // 087: bipush 0
      // 088: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 08b: astore 19
      // 08d: new java/lang/Object
      // 090: dup
      // 091: bipush 0
      // 092: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 095: astore 20
      // 097: aload 3
      // 098: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncConverter (Lnet/rim/device/api/synchronization/SyncCollection;)Lnet/rim/device/api/synchronization/SyncConverter;
      // 09b: astore 21
      // 09d: iload 6
      // 09f: istore 22
      // 0a1: bipush 0
      // 0a2: istore 23
      // 0a4: bipush 0
      // 0a5: istore 24
      // 0a7: aconst_null
      // 0a8: astore 25
      // 0aa: bipush 0
      // 0ab: istore 26
      // 0ad: ldc_w 2147483647
      // 0b0: istore 27
      // 0b2: iload 27
      // 0b4: aload 19
      // 0b6: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 0b9: istore 27
      // 0bb: iload 13
      // 0bd: ifeq 0cb
      // 0c0: iload 27
      // 0c2: ifgt 0cb
      // 0c5: bipush 1
      // 0c6: istore 15
      // 0c8: goto 324
      // 0cb: aload 0
      // 0cc: aload 1
      // 0cd: aload 19
      // 0cf: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 0d2: ifeq 0e0
      // 0d5: iinc 13 1
      // 0d8: invokestatic net/rim/device/api/lowmemory/LowMemoryManager.poll ()V
      // 0db: ldc_w 2147483647
      // 0de: istore 27
      // 0e0: aload 19
      // 0e2: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 0e5: ifgt 0f1
      // 0e8: bipush 1
      // 0e9: istore 14
      // 0eb: bipush 1
      // 0ec: istore 15
      // 0ee: goto 324
      // 0f1: aload 0
      // 0f2: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 0f5: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.stop ()Z 1
      // 0fa: ifeq 100
      // 0fd: goto 324
      // 100: aload 19
      // 102: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 105: istore 28
      // 107: iload 28
      // 109: tableswitch 47 5 12 521 133 153 47 521 123 143 153
      // 138: aload 19
      // 13a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 13d: ifeq 144
      // 140: bipush 1
      // 141: goto 145
      // 144: bipush 0
      // 145: istore 29
      // 147: iload 9
      // 149: iload 29
      // 14b: if_icmpeq 151
      // 14e: goto 318
      // 151: aload 3
      // 152: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeAllSyncObjects (Lnet/rim/device/api/synchronization/SyncCollection;)Z
      // 155: ifne 170
      // 158: new java/lang/Object
      // 15b: dup
      // 15c: ldc_w "restore; could not removeall from "
      // 15f: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 162: aload 11
      // 164: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 167: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 16a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 16d: goto 318
      // 170: aload 4
      // 172: ifnonnull 178
      // 175: goto 318
      // 178: aload 0
      // 179: aload 3
      // 17a: aload 4
      // 17c: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.establishRestoredUIDs (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/util/IntHashtable;)Lnet/rim/device/api/util/IntHashtable;
      // 17f: astore 4
      // 181: goto 318
      // 184: aload 19
      // 186: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 189: istore 22
      // 18b: goto 318
      // 18e: aload 19
      // 190: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 193: istore 23
      // 195: goto 318
      // 198: aload 19
      // 19a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // 19d: istore 24
      // 19f: goto 318
      // 1a2: aload 19
      // 1a4: iload 28
      // 1a6: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.readBytes (Lnet/rim/device/api/util/DataBuffer;I)[B
      // 1a9: astore 25
      // 1ab: iload 26
      // 1ad: bipush 11
      // 1af: if_icmpeq 1b5
      // 1b2: goto 318
      // 1b5: aload 0
      // 1b6: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.incrementTotalProgress ()V
      // 1b9: aload 0
      // 1ba: aload 7
      // 1bc: bipush 0
      // 1bd: dup2
      // 1be: iaload
      // 1bf: bipush 1
      // 1c0: iadd
      // 1c1: dup_x2
      // 1c2: iastore
      // 1c3: aload 7
      // 1c5: bipush 1
      // 1c6: iaload
      // 1c7: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.updateDatabaseProgress (II)V
      // 1ca: aconst_null
      // 1cb: astore 29
      // 1cd: aload 4
      // 1cf: ifnull 1ed
      // 1d2: aload 4
      // 1d4: iload 23
      // 1d6: invokevirtual net/rim/device/api/util/IntHashtable.get (I)Ljava/lang/Object;
      // 1d9: astore 30
      // 1db: aload 30
      // 1dd: dup
      // 1de: instanceof java/lang/Object
      // 1e1: ifne 1e8
      // 1e4: pop
      // 1e5: goto 1ed
      // 1e8: checkcast java/lang/Object
      // 1eb: astore 29
      // 1ed: aconst_null
      // 1ee: astore 30
      // 1f0: aload 20
      // 1f2: invokevirtual net/rim/device/api/util/DataBuffer.reset ()V
      // 1f5: aload 20
      // 1f7: aload 25
      // 1f9: bipush 0
      // 1fa: aload 25
      // 1fc: arraylength
      // 1fd: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BII)V
      // 200: aload 21
      // 202: aload 20
      // 204: iload 22
      // 206: iload 23
      // 208: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.convert (Lnet/rim/device/api/synchronization/SyncConverter;Lnet/rim/device/api/util/DataBuffer;II)Lnet/rim/device/api/synchronization/SyncObject;
      // 20b: astore 31
      // 20d: aload 31
      // 20f: ifnonnull 215
      // 212: goto 2a1
      // 215: aload 29
      // 217: ifnull 27c
      // 21a: new java/lang/Object
      // 21d: dup
      // 21e: ldc_w "updating item "
      // 221: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 224: iload 23
      // 226: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 229: ldc_w " in "
      // 22c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 22f: aload 11
      // 231: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 234: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 237: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 23a: aload 3
      // 23b: aload 29
      // 23d: aload 31
      // 23f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.updateSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 242: ifeq 24c
      // 245: aload 31
      // 247: astore 30
      // 249: goto 27c
      // 24c: aload 3
      // 24d: aload 29
      // 24f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.removeSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 252: ifne 27c
      // 255: new java/lang/Object
      // 258: dup
      // 259: ldc_w "restore; could not update/remove "
      // 25c: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 25f: aload 29
      // 261: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getUID (Lnet/rim/device/api/synchronization/SyncObject;)I
      // 264: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 267: ldc_w " from "
      // 26a: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 26d: aload 11
      // 26f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 272: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 275: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 278: aload 31
      // 27a: astore 30
      // 27c: aload 30
      // 27e: ifnonnull 28e
      // 281: aload 3
      // 282: aload 31
      // 284: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.addSyncObject (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)Z
      // 287: ifeq 28e
      // 28a: aload 31
      // 28c: astore 30
      // 28e: aload 30
      // 290: ifnull 2a1
      // 293: aload 0
      // 294: iload 24
      // 296: aload 3
      // 297: aload 31
      // 299: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.setItemFlags (ILnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/synchronization/SyncObject;)V
      // 29c: goto 2a1
      // 29f: astore 31
      // 2a1: iload 6
      // 2a3: istore 22
      // 2a5: aload 30
      // 2a7: ifnull 2af
      // 2aa: iload 8
      // 2ac: ifeq 318
      // 2af: new java/lang/Object
      // 2b2: dup
      // 2b3: ldc_w "saving undo information for item "
      // 2b6: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 2b9: iload 23
      // 2bb: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 2be: ldc_w " in "
      // 2c1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2c4: aload 11
      // 2c6: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 2c9: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 2cc: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 2cf: iload 10
      // 2d1: ifne 2d7
      // 2d4: goto 318
      // 2d7: aload 5
      // 2d9: ifnull 2e9
      // 2dc: aload 5
      // 2de: iload 23
      // 2e0: invokevirtual net/rim/device/api/util/IntHashtable.get (I)Ljava/lang/Object;
      // 2e3: ifnull 2e9
      // 2e6: goto 318
      // 2e9: aload 16
      // 2eb: bipush 6
      // 2ed: iload 23
      // 2ef: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 2f2: pop
      // 2f3: aload 16
      // 2f5: bipush 11
      // 2f7: iload 24
      // 2f9: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.writeInt (Lnet/rim/device/api/util/DataBuffer;II)Z
      // 2fc: pop
      // 2fd: aload 16
      // 2ff: aload 25
      // 301: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.writeBytes (Lnet/rim/device/api/util/DataBuffer;[B)V
      // 304: aload 0
      // 305: aload 2
      // 306: aload 16
      // 308: bipush 0
      // 309: bipush 22
      // 30b: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 30e: pop
      // 30f: goto 318
      // 312: aload 19
      // 314: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // 317: pop
      // 318: iload 28
      // 31a: istore 26
      // 31c: goto 0b2
      // 31f: astore 28
      // 321: goto 0b2
      // 324: aload 0
      // 325: aload 3
      // 326: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 329: goto 343
      // 32c: astore 19
      // 32e: aload 0
      // 32f: aload 3
      // 330: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 333: goto 343
      // 336: astore 32
      // 338: aload 0
      // 339: aload 3
      // 33a: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doEndTransaction (Lnet/rim/device/api/synchronization/SyncCollection;)V
      // 33d: aload 32
      // 33f: athrow
      // 340: bipush 1
      // 341: istore 14
      // 343: aload 18
      // 345: monitorexit
      // 346: goto 351
      // 349: astore 33
      // 34b: aload 18
      // 34d: monitorexit
      // 34e: aload 33
      // 350: athrow
      // 351: aload 0
      // 352: bipush 33
      // 354: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 357: aload 17
      // 359: monitorexit
      // 35a: goto 365
      // 35d: astore 34
      // 35f: aload 17
      // 361: monitorexit
      // 362: aload 34
      // 364: athrow
      // 365: sipush 25000
      // 368: i2l
      // 369: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.letSystemRecover (J)V
      // 36c: aload 0
      // 36d: bipush 34
      // 36f: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 372: iload 13
      // 374: ifeq 3a5
      // 377: iload 15
      // 379: ifeq 3a5
      // 37c: aload 2
      // 37d: ifnull 390
      // 380: aload 0
      // 381: aload 2
      // 382: aload 16
      // 384: bipush 1
      // 385: bipush 25
      // 387: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.outputToFlash (Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/util/DataBuffer;ZI)Z
      // 38a: pop
      // 38b: goto 390
      // 38e: astore 17
      // 390: aload 0
      // 391: bipush 35
      // 393: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 396: aload 1
      // 397: invokevirtual net/rim/vm/FlashInputStream.purge ()V
      // 39a: aload 0
      // 39b: bipush 36
      // 39d: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 3a0: goto 3a5
      // 3a3: astore 17
      // 3a5: iload 14
      // 3a7: ireturn
      // try (212 -> 285): 286 null
      // try (122 -> 341): 342 null
      // try (64 -> 344): 348 null
      // try (64 -> 344): 353 null
      // try (348 -> 349): 353 null
      // try (353 -> 354): 353 null
      // try (60 -> 363): 364 null
      // try (364 -> 367): 364 null
      // try (56 -> 374): 375 null
      // try (375 -> 378): 375 null
      // try (390 -> 399): 400 null
      // try (401 -> 409): 410 null
   }

   private final int findCollectionBackupVersion(long param1, String param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: ldc2_w -3904873326663712912
      // 07: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 0a: astore 4
      // 0c: new java/lang/Object
      // 0f: dup
      // 10: bipush 0
      // 11: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 14: astore 5
      // 16: bipush 0
      // 17: i2l
      // 18: lstore 6
      // 1a: ldc_w ""
      // 1d: astore 8
      // 1f: ldc_w 2147483647
      // 22: istore 9
      // 24: aload 0
      // 25: aload 4
      // 27: aload 5
      // 29: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.inputFromFlash (Lnet/rim/vm/FlashInputStream;Lnet/rim/device/api/util/DataBuffer;)Z
      // 2c: ifeq 34
      // 2f: ldc_w 2147483647
      // 32: istore 9
      // 34: iload 9
      // 36: aload 5
      // 38: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.checkAvailable (ILnet/rim/device/api/util/DataBuffer;)I
      // 3b: istore 9
      // 3d: iload 9
      // 3f: ifgt 45
      // 42: goto c2
      // 45: bipush 0
      // 46: istore 10
      // 48: aload 5
      // 4a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 4d: istore 11
      // 4f: iload 11
      // 51: tableswitch 31 2 5 99 31 44 64
      // 70: aload 5
      // 72: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readLong (Lnet/rim/device/api/util/DataBuffer;)J
      // 75: lstore 6
      // 77: bipush 1
      // 78: istore 10
      // 7a: goto b4
      // 7d: lload 6
      // 7f: lload 1
      // 80: lcmp
      // 81: ifne b4
      // 84: aload 5
      // 86: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readString (Lnet/rim/device/api/util/DataBuffer;)Ljava/lang/String;
      // 89: astore 8
      // 8b: bipush 1
      // 8c: istore 10
      // 8e: goto b4
      // 91: lload 6
      // 93: lload 1
      // 94: lcmp
      // 95: ifne b4
      // 98: aload 3
      // 99: aload 8
      // 9b: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 9e: ifeq b4
      // a1: aload 5
      // a3: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.readInt (Lnet/rim/device/api/util/DataBuffer;)I
      // a6: istore 12
      // a8: iload 12
      // aa: istore 13
      // ac: aload 4
      // ae: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // b1: iload 13
      // b3: ireturn
      // b4: iload 10
      // b6: ifne 24
      // b9: aload 5
      // bb: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$CUSafe.skipField (Lnet/rim/device/api/util/DataBuffer;)Z
      // be: pop
      // bf: goto 24
      // c2: aload 4
      // c4: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // c7: bipush 0
      // c8: ireturn
      // c9: astore 5
      // cb: aload 4
      // cd: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // d0: bipush 0
      // d1: ireturn
      // d2: astore 14
      // d4: aload 4
      // d6: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // d9: aload 14
      // db: athrow
      // try (5 -> 67): 81 null
      // try (71 -> 77): 81 null
      // try (5 -> 67): 86 null
      // try (71 -> 77): 86 null
      // try (81 -> 82): 86 null
      // try (86 -> 87): 86 null
   }

   private static final String makeUndoName(String name) {
      return ((StringBuffer)(new Object())).append(name).append("_undo").toString();
   }

   private final void finishCollectionRestore(SyncCollection param1, boolean param2, LongIntHashtable param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getLocalizedSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 004: astore 4
      // 006: aload 1
      // 007: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl$SCSafe.getSyncName (Lnet/rim/device/api/synchronization/SyncCollection;)Ljava/lang/String;
      // 00a: astore 5
      // 00c: new java/lang/Object
      // 00f: dup
      // 010: ldc_w "restore+ "
      // 013: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 016: aload 5
      // 018: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 01b: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 01e: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 021: aload 0
      // 022: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 025: aload 4
      // 027: bipush 0
      // 028: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.updateDatabaseProgress (Ljava/lang/String;I)V 3
      // 02d: aload 5
      // 02f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.calcStringGUID (Ljava/lang/String;)J
      // 032: lstore 6
      // 034: lload 6
      // 036: invokestatic net/rim/vm/FlashInputStream.exists (J)Z
      // 039: ifne 058
      // 03c: new java/lang/Object
      // 03f: dup
      // 040: ldc_w "restore- "
      // 043: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 046: aload 5
      // 048: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04b: ldc_w " backup collection missing"
      // 04e: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 051: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 054: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 057: return
      // 058: aload 3
      // 059: lload 6
      // 05b: invokevirtual net/rim/device/api/util/LongIntHashtable.get (J)I
      // 05e: istore 8
      // 060: iload 8
      // 062: ifgt 068
      // 065: bipush 1
      // 066: istore 8
      // 068: aload 5
      // 06a: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.makeUndoName (Ljava/lang/String;)Ljava/lang/String;
      // 06d: astore 9
      // 06f: aload 5
      // 071: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.makeUndoName (Ljava/lang/String;)Ljava/lang/String;
      // 074: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.calcStringGUID (Ljava/lang/String;)J
      // 077: lstore 10
      // 079: aload 0
      // 07a: lload 10
      // 07c: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.establishBackedUpUIDs (J)Lnet/rim/device/api/util/IntHashtable;
      // 07f: astore 12
      // 081: aload 0
      // 082: aload 1
      // 083: aconst_null
      // 084: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.establishRestoredUIDs (Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/util/IntHashtable;)Lnet/rim/device/api/util/IntHashtable;
      // 087: astore 13
      // 089: aload 0
      // 08a: lload 6
      // 08c: aload 5
      // 08e: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.findCollectionBackupVersion (JLjava/lang/String;)I
      // 091: istore 14
      // 093: bipush 2
      // 095: newarray 10
      // 097: dup
      // 098: bipush 0
      // 099: bipush 0
      // 09a: iastore
      // 09b: dup
      // 09c: bipush 1
      // 09d: iload 8
      // 09f: iastore
      // 0a0: astore 15
      // 0a2: new java/lang/Object
      // 0a5: dup
      // 0a6: lload 6
      // 0a8: invokespecial net/rim/vm/FlashInputStream.<init> (J)V
      // 0ab: astore 16
      // 0ad: aconst_null
      // 0ae: astore 17
      // 0b0: iload 2
      // 0b1: ifne 0db
      // 0b4: aload 0
      // 0b5: ldc2_w -7484766735474520310
      // 0b8: lload 10
      // 0ba: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.isCollectionBackupRegistered (JJ)Z
      // 0bd: ifne 0cf
      // 0c0: aload 0
      // 0c1: ldc2_w -7484766735474520310
      // 0c4: lload 10
      // 0c6: aload 9
      // 0c8: iload 14
      // 0ca: iload 8
      // 0cc: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doRegisterCollectionInfo (JJLjava/lang/String;II)V
      // 0cf: new java/lang/Object
      // 0d2: dup
      // 0d3: lload 10
      // 0d5: bipush 1
      // 0d6: invokespecial net/rim/vm/FlashOutputStream.<init> (JZ)V
      // 0d9: astore 17
      // 0db: aload 0
      // 0dc: aload 16
      // 0de: aload 17
      // 0e0: aload 1
      // 0e1: aload 13
      // 0e3: aload 12
      // 0e5: iload 14
      // 0e7: aload 15
      // 0e9: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.restoreCollectionChunk (Lnet/rim/vm/FlashInputStream;Lnet/rim/vm/FlashOutputStream;Lnet/rim/device/api/synchronization/SyncCollection;Lnet/rim/device/api/util/IntHashtable;Lnet/rim/device/api/util/IntHashtable;I[I)Z
      // 0ec: istore 18
      // 0ee: iload 18
      // 0f0: ifeq 0f6
      // 0f3: goto 120
      // 0f6: iload 2
      // 0f7: ifne 109
      // 0fa: aload 0
      // 0fb: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._lmmFailed Z
      // 0fe: ifeq 109
      // 101: aload 0
      // 102: bipush 1
      // 103: putfield net/rim/device/internal/synchronization/OTAUpgradeControl._status I
      // 106: goto 120
      // 109: aload 0
      // 10a: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 10d: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.stop ()Z 1
      // 112: ifeq 118
      // 115: goto 120
      // 118: aload 0
      // 119: bipush 0
      // 11a: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doGC (Z)V
      // 11d: goto 0db
      // 120: aload 16
      // 122: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 125: aload 17
      // 127: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 12a: lload 10
      // 12c: invokestatic net/rim/vm/FlashInputStream.exists (J)Z
      // 12f: ifne 196
      // 132: aload 0
      // 133: bipush 47
      // 135: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 138: lload 10
      // 13a: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 13d: aload 0
      // 13e: bipush 48
      // 140: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 143: goto 196
      // 146: astore 18
      // 148: aload 16
      // 14a: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 14d: aload 17
      // 14f: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 152: lload 10
      // 154: invokestatic net/rim/vm/FlashInputStream.exists (J)Z
      // 157: ifne 196
      // 15a: aload 0
      // 15b: bipush 47
      // 15d: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 160: lload 10
      // 162: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 165: aload 0
      // 166: bipush 48
      // 168: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 16b: goto 196
      // 16e: astore 19
      // 170: aload 16
      // 172: invokevirtual net/rim/vm/FlashInputStream.close ()V
      // 175: aload 17
      // 177: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.safeClose (Ljava/io/OutputStream;)V
      // 17a: lload 10
      // 17c: invokestatic net/rim/vm/FlashInputStream.exists (J)Z
      // 17f: ifne 193
      // 182: aload 0
      // 183: bipush 47
      // 185: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 188: lload 10
      // 18a: invokestatic net/rim/vm/FlashInputStream.erase (J)V
      // 18d: aload 0
      // 18e: bipush 48
      // 190: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.testReset (I)V
      // 193: aload 19
      // 195: athrow
      // 196: aload 0
      // 197: bipush 0
      // 198: invokespecial net/rim/device/internal/synchronization/OTAUpgradeControl.doGC (Z)V
      // 19b: aload 0
      // 19c: getfield net/rim/device/internal/synchronization/OTAUpgradeControl._control Lnet/rim/device/internal/synchronization/OTAUpgradeControlCallback;
      // 19f: aload 4
      // 1a1: bipush 100
      // 1a3: invokeinterface net/rim/device/internal/synchronization/OTAUpgradeControlCallback.updateDatabaseProgress (Ljava/lang/String;I)V 3
      // 1a8: new java/lang/Object
      // 1ab: dup
      // 1ac: ldc_w "restore- "
      // 1af: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 1b2: aload 5
      // 1b4: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1b7: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 1ba: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.logInfo (Ljava/lang/String;)V
      // 1bd: return
      // try (103 -> 134): 150 null
      // try (103 -> 134): 167 null
      // try (150 -> 151): 167 null
      // try (167 -> 168): 167 null
   }

   private static final void setUShort(byte[] a, int off, int v) {
      a[off] = (byte)(v & 0xFF);
      a[off + 1] = (byte)(v >>> 8 & 0xFF);
   }

   private static final SyncCollection[] getActivationServiceCollections() {
      try {
         return ActivationService.getInstance().getCollections();
      } finally {
         return new Object[0];
      }
   }

   private static final SyncCollection[] getRegisteredCollections() {
      try {
         SyncManagerImpl syncManager = (SyncManagerImpl)SyncManager.getInstance();
         return syncManager.getSyncCollections();
      } finally {
         return new Object[0];
      }
   }

   private final SyncCollection[] getSyncCollections(boolean restoreOrder) {
      SyncCollection[] main = getRegisteredCollections();
      int mainLen = main.length;
      SyncCollection[] extra1 = OTAUpgrade.getOTASLOnlyCollections();
      int extra1Len = extra1.length;
      SyncCollection[] extra2 = getActivationServiceCollections();
      int extra2Len = extra2.length;
      int collectionsLen = mainLen + extra1Len + extra2Len;
      SyncCollection[] syncCollections = new Object[collectionsLen];
      int offset = 0;
      System.arraycopy(extra1, 0, syncCollections, offset, extra1Len);
      offset += extra1Len;
      System.arraycopy(extra2, 0, syncCollections, offset, extra2Len);
      offset += extra2Len;
      System.arraycopy(main, 0, syncCollections, offset, mainLen);
      String deviceOptions = "Device Options";

      for (int i = syncCollections.length - 1; i >= 0; i--) {
         SyncCollection sc = syncCollections[i];
         boolean remove = false;
         String syncName = OTAUpgradeControl$SCSafe.getSyncName(sc);
         if (!(sc instanceof Object)) {
            if (syncName.equals(deviceOptions) && OTAUpgradeControl$SCSafe.getSyncObjectCount(sc) == 0) {
               remove = true;
            }
         } else {
            SyncCollectionStatusProvider scsp = (SyncCollectionStatusProvider)sc;
            if (!OTAUpgradeControl$SCSPSafe.isWritableForOTASL(scsp)) {
               remove = true;
            }
         }

         if (remove) {
            Arrays.removeAt(syncCollections, i);
         }
      }

      Arrays.sort(syncCollections, new OTAUpgradeControl$SCComp(restoreOrder, null));
      return syncCollections;
   }

   private final void activateCollections(SyncCollection[] syncCollections) {
      for (int i = syncCollections.length - 1; i >= 0; i--) {
         OTAUpgradeControl$SCSafe.activateCollection(syncCollections[i], this);
      }
   }

   private final void deactivateCollections(SyncCollection[] syncCollections) {
      for (int i = syncCollections.length - 1; i >= 0; i--) {
         OTAUpgradeControl$SCSafe.deactivateCollection(syncCollections[i], this);
      }
   }

   private final String percentage(long n, long d) {
      if (d == 0) {
         return "";
      }

      n *= 100;
      n /= d;
      return ((StringBuffer)(new Object(" ("))).append(n).append("%)").toString();
   }

   private final void printSummary(String what) {
      System.out.println(((StringBuffer)(new Object())).append(what).append(" finished").toString());
      System.out.println(((StringBuffer)(new Object("BWT: "))).append(this._compressBWT).toString());
      System.out.println(((StringBuffer)(new Object("test resets: "))).append(this._testResets).toString());
      System.out.println(((StringBuffer)(new Object("LMM failed: "))).append(this._lmmFailed).toString());
      if (this._totalTime != 0) {
         System.out.println(((StringBuffer)(new Object("flash free before: "))).append(this._flashFree).toString());
         System.out.println(((StringBuffer)(new Object("flash free after: "))).append(Memory.getFlashFree()).toString());
         System.out.println(((StringBuffer)(new Object("uncompressed size: "))).append(this._uncompressedSize).toString());
         System.out
            .println(
               ((StringBuffer)(new Object("compressed size: ")))
                  .append(this._compressedSize)
                  .append(this.percentage(this._compressedSize, this._uncompressedSize))
                  .toString()
            );
         System.out
            .println(
               ((StringBuffer)(new Object("compression time: ")))
                  .append(this._compressionTime)
                  .append(this.percentage(this._compressionTime, this._totalTime))
                  .toString()
            );
         System.out
            .println(
               ((StringBuffer)(new Object("encryption time: ")))
                  .append(this._encryptionTime)
                  .append(this.percentage(this._encryptionTime, this._totalTime))
                  .toString()
            );
         System.out
            .println(
               ((StringBuffer)(new Object("stream time: "))).append(this._streamTime).append(this.percentage(this._streamTime, this._totalTime)).toString()
            );
         System.out.println(((StringBuffer)(new Object("gc time: "))).append(this._gcTime).append(this.percentage(this._gcTime, this._totalTime)).toString());
         System.out.println(((StringBuffer)(new Object("total time: "))).append(this._totalTime).toString());
      }
   }

   private static final void setInt(byte[] a, int off, int v) {
      a[off] = (byte)(v & 0xFF);
      a[off + 1] = (byte)(v >>> 8 & 0xFF);
      a[off + 2] = (byte)(v >>> 16 & 0xFF);
      a[off + 3] = (byte)(v >>> 24 & 0xFF);
   }

   private static final void logInfo(String msg) {
      System.out.println(msg);
      EventLog.logEvent(4234230263395208602L, System.currentTimeMillis(), (byte)0, msg.getBytes());
   }

   private static final void letSystemRecover() {
      letSystemRecover(2500);
   }

   private static final void letSystemRecover(long millis) {
      try {
         Process.waitForIdle(millis);
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void doGC(boolean force) {
      if (Memory.getFlashNeeded(true) > 0 || Memory.getHandlesNeeded(true) > 0) {
         force = true;
      }

      if (this._itemsRemoved > 1024) {
         this._itemsRemoved = 0;
         force = true;
      }

      if (force) {
         this._gcTime = this._gcTime - System.currentTimeMillis();
         boolean var4 = false /* VF: Semaphore variable */;

         try {
            var4 = true;
            Memory.persistentGC();
            var4 = false;
         } finally {
            if (var4) {
               this._gcTime = this._gcTime + System.currentTimeMillis();
            }
         }

         this._gcTime = this._gcTime + System.currentTimeMillis();
      }
   }

   public OTAUpgradeControl() {
      EventLog.registerApp(4234230263395208602L, 2, "net.rim.otasl.control");
   }
}
