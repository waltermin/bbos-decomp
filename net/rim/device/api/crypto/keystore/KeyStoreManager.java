package net.rim.device.api.crypto.keystore;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.EventLogger;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class KeyStoreManager {
   private WeakReference[] _keyStores;
   private static final long KEYSTOREMANAGER = -5169796521124280422L;
   static final long EVENT_LOGGER_GUID = 3915475930975345450L;
   private static final int EVENT_CHANGE_PASSWORD_START = 1129346899;
   private static final int EVENT_CHANGE_PASSWORD_FINISH = 1129346886;
   private static KeyStoreManager _manager;

   private KeyStoreManager() {
   }

   private final void initialize() {
      this.recreateAndRestore();
      MemoryCleanerDaemon.addListener(new KeyStoreManager$KeyStoreManagerCleaner(), false);
      SyncManager.getInstance().enableSynchronization(new KeyStoreManagerSyncCollection());
      EventLogger.register(3915475930975345450L, "Key Store Manager", 2);
   }

   public static final KeyStoreManager getInstance() {
      if (_manager == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _manager = (KeyStoreManager)ar.getOrWaitFor(-5169796521124280422L);
         if (_manager == null) {
            _manager = new KeyStoreManager();
            ar.put(-5169796521124280422L, _manager);
            _manager.initialize();
         }
      }

      return _manager;
   }

   public final synchronized void register(KeyStore keyStore) {
      WeakReference weakReference = new WeakReference(keyStore);
      if (this._keyStores != null) {
         int length = this._keyStores.length;

         for (int i = 0; i < length; i++) {
            if (this._keyStores[i] != null) {
               KeyStore tempKeyStore = (KeyStore)this._keyStores[i].get();
               if (tempKeyStore == null) {
                  this._keyStores[i] = null;
               } else if (tempKeyStore.equals(keyStore)) {
                  return;
               }
            }
         }

         for (int i = 0; i < length; i++) {
            if (this._keyStores[i] == null) {
               this._keyStores[i] = weakReference;
               return;
            }
         }

         Array.resize(this._keyStores, length + 1);
         this._keyStores[length] = weakReference;
      } else {
         this._keyStores = new WeakReference[]{weakReference};
      }
   }

   public final synchronized void register(String param1, long param2, CodeSigningKey param4, KeyStore param5) throws KeyStoreRegisterException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 0b
      // 04: aload 0
      // 05: aload 5
      // 07: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManager.register (Lnet/rim/device/api/crypto/keystore/KeyStore;)V
      // 0a: return
      // 0b: aload 1
      // 0c: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 0f: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 12: astore 6
      // 14: aload 6
      // 16: instanceof net/rim/device/api/crypto/keystore/PersistableRIMKeyStoreFactory
      // 19: ifne 48
      // 1c: new java/lang/IllegalArgumentException
      // 1f: dup
      // 20: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 23: athrow
      // 24: astore 6
      // 26: new net/rim/device/api/crypto/keystore/KeyStoreRegisterException
      // 29: dup
      // 2a: aload 6
      // 2c: invokespecial net/rim/device/api/crypto/keystore/KeyStoreRegisterException.<init> (Ljava/lang/Exception;)V
      // 2f: athrow
      // 30: astore 6
      // 32: new net/rim/device/api/crypto/keystore/KeyStoreRegisterException
      // 35: dup
      // 36: aload 6
      // 38: invokespecial net/rim/device/api/crypto/keystore/KeyStoreRegisterException.<init> (Ljava/lang/Exception;)V
      // 3b: athrow
      // 3c: astore 6
      // 3e: new net/rim/device/api/crypto/keystore/KeyStoreRegisterException
      // 41: dup
      // 42: aload 6
      // 44: invokespecial net/rim/device/api/crypto/keystore/KeyStoreRegisterException.<init> (Ljava/lang/Exception;)V
      // 47: athrow
      // 48: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // 4b: astore 6
      // 4d: aload 6
      // 4f: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getCodeSigningHashtable ()Lnet/rim/device/api/util/LongHashtable;
      // 52: astore 7
      // 54: aload 7
      // 56: lload 2
      // 57: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 5a: checkcast net/rim/device/api/system/CodeSigningKey
      // 5d: astore 8
      // 5f: aload 8
      // 61: ifnonnull 75
      // 64: aload 4
      // 66: ifnull 75
      // 69: aload 7
      // 6b: lload 2
      // 6c: aload 4
      // 6e: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 71: pop
      // 72: goto a1
      // 75: aload 8
      // 77: ifnull a1
      // 7a: aload 8
      // 7c: aload 4
      // 7e: invokevirtual net/rim/device/api/system/CodeSigningKey.equals (Ljava/lang/Object;)Z
      // 81: ifne 8c
      // 84: new net/rim/device/api/system/ControlledAccessException
      // 87: dup
      // 88: invokespecial net/rim/device/api/system/ControlledAccessException.<init> ()V
      // 8b: athrow
      // 8c: bipush 2
      // 8e: invokestatic net/rim/vm/TraceBack.getCallingModule (I)I
      // 91: aload 8
      // 93: invokestatic net/rim/device/api/system/ControlledAccess.verifyCodeModuleSignature (ILnet/rim/device/api/system/CodeSigningKey;)Z
      // 96: ifne a1
      // 99: new net/rim/device/api/system/ControlledAccessException
      // 9c: dup
      // 9d: invokespecial net/rim/device/api/system/ControlledAccessException.<init> ()V
      // a0: athrow
      // a1: aload 0
      // a2: aload 5
      // a4: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManager.register (Lnet/rim/device/api/crypto/keystore/KeyStore;)V
      // a7: aload 6
      // a9: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getKeyStoreHashtable ()Ljava/util/Hashtable;
      // ac: astore 9
      // ae: aload 9
      // b0: aload 1
      // b1: aload 1
      // b2: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // b5: pop
      // b6: aload 6
      // b8: aload 9
      // ba: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.setKeyStoreHashtable (Ljava/util/Hashtable;)V
      // bd: return
      // try (6 -> 17): 17 null
      // try (6 -> 17): 23 null
      // try (6 -> 17): 29 null
   }

   final Enumeration getRegisteredKeyStores() {
      return new KeyStoreEnumeration(this._keyStores);
   }

   final synchronized void changePassword() {
      EventLogger.logEvent(3915475930975345450L, 1129346899);
      Enumeration enumeration = this.getRegisteredKeyStores();

      while (enumeration.hasMoreElements()) {
         KeyStoreDecodeRuntimeException e;
         try {
            ((KeyStore)enumeration.nextElement()).changePassword();
            continue;
         } catch (KeyStoreDecodeRuntimeException var5) {
            e = var5;
         } finally {
            continue;
         }

         throw e;
      }

      EventLogger.logEvent(3915475930975345450L, 1129346886);
   }

   private final void recreateAndRestore() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getInstance ()Lnet/rim/device/api/crypto/keystore/KeyStoreManagerHelper;
      // 03: astore 1
      // 04: aload 1
      // 05: invokevirtual net/rim/device/api/crypto/keystore/KeyStoreManagerHelper.getKeyStoreHashtable ()Ljava/util/Hashtable;
      // 08: astore 2
      // 09: aload 2
      // 0a: invokevirtual java/util/Hashtable.elements ()Ljava/util/Enumeration;
      // 0d: astore 3
      // 0e: aload 3
      // 0f: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 14: ifeq 54
      // 17: aload 3
      // 18: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 1d: checkcast java/lang/String
      // 20: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 23: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 26: astore 4
      // 28: aload 4
      // 2a: dup
      // 2b: instanceof net/rim/device/api/crypto/keystore/PersistableRIMKeyStoreFactory
      // 2e: ifne 35
      // 31: pop
      // 32: goto 0e
      // 35: checkcast net/rim/device/api/crypto/keystore/PersistableRIMKeyStoreFactory
      // 38: astore 5
      // 3a: aload 5
      // 3c: invokeinterface net/rim/device/api/crypto/keystore/PersistableRIMKeyStoreFactory.createInstance ()Lnet/rim/device/api/crypto/keystore/KeyStore; 1
      // 41: pop
      // 42: goto 0e
      // 45: astore 4
      // 47: goto 0e
      // 4a: astore 4
      // 4c: goto 0e
      // 4f: astore 4
      // 51: goto 0e
      // 54: return
      // try (11 -> 28): 29 null
      // try (11 -> 28): 31 null
      // try (11 -> 28): 33 null
   }

   public final KeyStore getKeyStore(KeyStoreData data) {
      Enumeration enumeration = this.getRegisteredKeyStores();

      while (enumeration.hasMoreElements()) {
         KeyStore keyStore = (KeyStore)enumeration.nextElement();
         if (keyStore.isMember(data)) {
            return keyStore;
         }
      }

      return null;
   }

   public final boolean isSyncedWithBES(Certificate certificate) {
      Hashtable hashtable = KeyStoreManagerHelper.getInstance().getSyncedWithBESHashtable();
      return hashtable.get(certificate) == null;
   }

   public final void certificateAdded(Certificate certificate) {
      if (certificate != null) {
         KeyStoreManagerHelper helper = KeyStoreManagerHelper.getInstance();
         Hashtable hashtable = helper.getSyncedWithBESHashtable();
         hashtable.put(certificate, certificate);
         helper.setSyncedWithBESHashtable(hashtable);
      }
   }
}
