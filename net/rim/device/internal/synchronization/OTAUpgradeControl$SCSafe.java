package net.rim.device.internal.synchronization;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

class OTAUpgradeControl$SCSafe {
   private static SyncConverter _stubConverter = new OTAUpgradeControl$SCSafe$CnvStub(null);

   private OTAUpgradeControl$SCSafe() {
   }

   private static SyncObject[] getSyncObjects(SyncCollection sc) {
      try {
         return sc.getSyncObjects();
      } finally {
         return new Object[0];
      }
   }

   private static int getSyncObjectCount(SyncCollection sc) {
      try {
         return sc.getSyncObjectCount();
      } finally {
         ;
      }
   }

   private static int getSyncVersion(SyncCollection sc) {
      try {
         return sc.getSyncVersion();
      } finally {
         ;
      }
   }

   private static String getSyncName(SyncCollection sc) {
      try {
         return sc.getSyncName();
      } finally {
         return ((StringBuffer)(new Object())).append(sc.toString()).append(System.identityHashCode(sc)).toString();
      }
   }

   private static String getLocalizedSyncName(SyncCollection sc) {
      try {
         String name = sc.getSyncName(Locale.getDefault());
         if (name == null) {
            name = sc.getSyncName();
         }

         return name;
      } finally {
         return ((StringBuffer)(new Object())).append(sc.toString()).append(System.identityHashCode(sc)).toString();
      }
   }

   private static SyncConverter getSyncConverter(SyncCollection sc) {
      try {
         return sc.getSyncConverter();
      } finally {
         ;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean beginTransaction(SyncCollection sc) {
      boolean var6 = false /* VF: Semaphore variable */;
      boolean var9 = false /* VF: Semaphore variable */;

      boolean e;
      label38: {
         boolean var2;
         try {
            label36:
            try {
               var9 = true;
               var6 = true;
               sc.beginTransaction();
               e = true;
               var6 = false;
               var9 = false;
               break label38;
            } finally {
               if (var9) {
                  var2 = false;
                  var6 = false;
                  break label36;
               }
            }
         } finally {
            if (var6) {
               OTAUpgradeControl.letSystemRecover();
            }
         }

         OTAUpgradeControl.letSystemRecover();
         return var2;
      }

      OTAUpgradeControl.letSystemRecover();
      return e;
   }

   private static void endTransaction(SyncCollection param0) {
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
      // 00: aload 0
      // 01: invokeinterface net/rim/device/api/synchronization/SyncCollection.endTransaction ()V 1
      // 06: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.letSystemRecover ()V
      // 09: return
      // 0a: astore 1
      // 0b: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.letSystemRecover ()V
      // 0e: return
      // 0f: astore 2
      // 10: invokestatic net/rim/device/internal/synchronization/OTAUpgradeControl.letSystemRecover ()V
      // 13: aload 2
      // 14: athrow
      // try (0 -> 2): 4 null
      // try (0 -> 2): 7 null
      // try (4 -> 5): 7 null
      // try (7 -> 8): 7 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean removeAllSyncObjects(SyncCollection sc) {
      boolean var6 = false /* VF: Semaphore variable */;
      boolean var9 = false /* VF: Semaphore variable */;

      boolean e;
      label38: {
         boolean var2;
         try {
            label36:
            try {
               var9 = true;
               var6 = true;
               e = sc.removeAllSyncObjects();
               var6 = false;
               var9 = false;
               break label38;
            } finally {
               if (var9) {
                  var2 = false;
                  var6 = false;
                  break label36;
               }
            }
         } finally {
            if (var6) {
               OTAUpgradeControl.letSystemRecover();
            }
         }

         OTAUpgradeControl.letSystemRecover();
         return var2;
      }

      OTAUpgradeControl.letSystemRecover();
      return e;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static SyncObject getSyncObject(SyncCollection sc, int uid) {
      boolean var7 = false /* VF: Semaphore variable */;
      boolean var10 = false /* VF: Semaphore variable */;

      SyncObject e;
      label38: {
         Object var3;
         try {
            label36:
            try {
               var10 = true;
               var7 = true;
               e = sc.getSyncObject(uid);
               var7 = false;
               var10 = false;
               break label38;
            } finally {
               if (var10) {
                  var3 = null;
                  var7 = false;
                  break label36;
               }
            }
         } finally {
            if (var7) {
               OTAUpgradeControl.letSystemRecover();
            }
         }

         OTAUpgradeControl.letSystemRecover();
         return (SyncObject)var3;
      }

      OTAUpgradeControl.letSystemRecover();
      return e;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean removeSyncObject(SyncCollection sc, SyncObject so) {
      boolean var7 = false /* VF: Semaphore variable */;
      boolean var10 = false /* VF: Semaphore variable */;

      boolean e;
      label38: {
         boolean var3;
         try {
            label36:
            try {
               var10 = true;
               var7 = true;
               e = sc.removeSyncObject(so);
               var7 = false;
               var10 = false;
               break label38;
            } finally {
               if (var10) {
                  var3 = false;
                  var7 = false;
                  break label36;
               }
            }
         } finally {
            if (var7) {
               OTAUpgradeControl.letSystemRecover();
            }
         }

         OTAUpgradeControl.letSystemRecover();
         return var3;
      }

      OTAUpgradeControl.letSystemRecover();
      return e;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean addSyncObject(SyncCollection sc, SyncObject so) {
      boolean var7 = false /* VF: Semaphore variable */;
      boolean var10 = false /* VF: Semaphore variable */;

      boolean e;
      label38: {
         boolean var3;
         try {
            label36:
            try {
               var10 = true;
               var7 = true;
               e = sc.addSyncObject(so);
               var7 = false;
               var10 = false;
               break label38;
            } finally {
               if (var10) {
                  var3 = false;
                  var7 = false;
                  break label36;
               }
            }
         } finally {
            if (var7) {
               OTAUpgradeControl.letSystemRecover();
            }
         }

         OTAUpgradeControl.letSystemRecover();
         return var3;
      }

      OTAUpgradeControl.letSystemRecover();
      return e;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean updateSyncObject(SyncCollection sc, SyncObject oldSO, SyncObject newSO) {
      boolean var8 = false /* VF: Semaphore variable */;
      boolean var11 = false /* VF: Semaphore variable */;

      boolean e;
      label38: {
         boolean var4;
         try {
            label36:
            try {
               var11 = true;
               var8 = true;
               e = sc.updateSyncObject(oldSO, newSO);
               var8 = false;
               var11 = false;
               break label38;
            } finally {
               if (var11) {
                  var4 = false;
                  var8 = false;
                  break label36;
               }
            }
         } finally {
            if (var8) {
               OTAUpgradeControl.letSystemRecover();
            }
         }

         OTAUpgradeControl.letSystemRecover();
         return var4;
      }

      OTAUpgradeControl.letSystemRecover();
      return e;
   }

   private static boolean isSyncObjectDirty(SyncCollection sc, SyncObject so) {
      try {
         return sc.isSyncObjectDirty(so);
      } finally {
         ;
      }
   }

   private static void setSyncObjectDirty(SyncCollection sc, SyncObject so) {
      try {
         sc.setSyncObjectDirty(so);
      } finally {
         return;
      }
   }

   private static void clearSyncObjectDirty(SyncCollection sc, SyncObject so) {
      try {
         sc.clearSyncObjectDirty(so);
      } finally {
         return;
      }
   }

   static void activateCollection(SyncCollection sc, OTAUpgradeControl control) {
      if (sc instanceof ServiceBookSyncCollection) {
         ServiceBookSyncCollection sbsc = (ServiceBookSyncCollection)sc;
         control._serviceBookLegacyMode = sbsc.enableLegacyBackupRestore(true, false);
      }

      OTAUpgradeControl.letSystemRecover();
   }

   static void deactivateCollection(SyncCollection sc, OTAUpgradeControl control) {
      if (sc instanceof ServiceBookSyncCollection) {
         ServiceBookSyncCollection sbsc = (ServiceBookSyncCollection)sc;
         sbsc.enableLegacyBackupRestore(control._serviceBookLegacyMode, false);
         control._serviceBookLegacyMode = false;
      }

      OTAUpgradeControl.letSystemRecover();
   }

   static boolean convert(SyncConverter sc, SyncObject so, DataBuffer buff, int version) {
      try {
         return sc.convert(so, buff, version);
      } finally {
         ;
      }
   }

   static SyncObject convert(SyncConverter sc, DataBuffer buff, int version, int uid) {
      try {
         return sc.convert(buff, version, uid);
      } finally {
         ;
      }
   }

   static int getUID(SyncObject so) {
      try {
         return so.getUID();
      } finally {
         ;
      }
   }
}
