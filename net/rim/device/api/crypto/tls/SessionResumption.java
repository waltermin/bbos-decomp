package net.rim.device.api.crypto.tls;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.SIMCardSecurityListener;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.crypto.WTLSSessionKey;
import net.rim.device.internal.system.NvStore;
import net.rim.vm.Array;

public final class SessionResumption implements SIMCardSecurityListener {
   private Hashtable _connections;
   private Hashtable _nvStore;
   private Object _wtlsSyncObject = new Object();
   private int _simResult;
   private PersistentObject _persist = RIMPersistentStore.getPersistentObject(-7807228486018716511L);
   public static final int PERSIST_TYPE_NONE = 0;
   public static final int PERSIST_TYPE_FLASH = 1;
   public static final int PERSIST_TYPE_NVRAM = 2;
   public static final int PERSIST_TYPE_NO_DELETE = 4;
   public static final int PERSIST_TYPE_SIM = 8;
   private static final int VERSION_2 = 2;
   private static final long ID = -7807228486018716511L;

   public SessionResumption() {
      synchronized (this._persist) {
         if (this._persist.getContents() == null) {
            this._persist.setContents(new Hashtable(), 4801362);
            this._persist.commit();
         }
      }

      this._connections = (Hashtable)this._persist.getContents();
      this.processNvStore();
   }

   public final void addSession(
      String domainName, String protocol, byte[] sessionID, byte[] masterSecret, int cipherSuite, Certificate cert, Certificate[] certificatePool
   ) {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         if (sessionID != null && sessionID.length > 0) {
            SessionIdentifier identifier = new SessionIdentifier(domainName, protocol);
            SessionInformation info = new SessionInformation(sessionID, masterSecret, cipherSuite, cert, certificatePool);
            this._connections.put(identifier, info);
            this._persist.commit();
         }
      }
   }

   public final void addWTLSSession(
      String param1,
      String param2,
      int param3,
      int param4,
      int param5,
      byte[] param6,
      byte[] param7,
      byte[] param8,
      int param9,
      Certificate param10,
      int param11
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
      // 000: invokestatic net/rim/device/api/system/PersistentContent.getTicket ()Ljava/lang/Object;
      // 003: astore 12
      // 005: aload 12
      // 007: ifnonnull 00b
      // 00a: return
      // 00b: iload 11
      // 00d: bipush 1
      // 00e: iand
      // 00f: ifeq 042
      // 012: new net/rim/device/api/crypto/tls/SessionIdentifier
      // 015: dup
      // 016: aload 1
      // 017: aload 2
      // 018: invokespecial net/rim/device/api/crypto/tls/SessionIdentifier.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 01b: astore 13
      // 01d: new net/rim/device/api/crypto/tls/SessionInformation
      // 020: dup
      // 021: aload 6
      // 023: aload 7
      // 025: iload 9
      // 027: aload 10
      // 029: aconst_null
      // 02a: invokespecial net/rim/device/api/crypto/tls/SessionInformation.<init> ([B[BILnet/rim/device/api/crypto/certificate/Certificate;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 02d: astore 14
      // 02f: aload 0
      // 030: getfield net/rim/device/api/crypto/tls/SessionResumption._connections Ljava/util/Hashtable;
      // 033: aload 13
      // 035: aload 14
      // 037: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 03a: pop
      // 03b: aload 0
      // 03c: getfield net/rim/device/api/crypto/tls/SessionResumption._persist Lnet/rim/device/api/system/PersistentObject;
      // 03f: invokevirtual net/rim/device/api/system/PersistentObject.commit ()V
      // 042: iload 11
      // 044: bipush 2
      // 046: iand
      // 047: ifeq 072
      // 04a: aload 0
      // 04b: getfield net/rim/device/api/crypto/tls/SessionResumption._nvStore Ljava/util/Hashtable;
      // 04e: aload 1
      // 04f: new net/rim/device/api/crypto/tls/NvStoreInfo
      // 052: dup
      // 053: aload 1
      // 054: aload 6
      // 056: aload 7
      // 058: iload 9
      // 05a: iload 11
      // 05c: bipush 4
      // 05e: iand
      // 05f: ifne 066
      // 062: bipush 1
      // 063: goto 067
      // 066: bipush 0
      // 067: invokespecial net/rim/device/api/crypto/tls/NvStoreInfo.<init> (Ljava/lang/String;[B[BIZ)V
      // 06a: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 06d: pop
      // 06e: aload 0
      // 06f: invokespecial net/rim/device/api/crypto/tls/SessionResumption.writeNvStore ()V
      // 072: iload 11
      // 074: bipush 8
      // 076: iand
      // 077: ifne 07d
      // 07a: goto 151
      // 07d: aload 6
      // 07f: arraylength
      // 080: bipush 8
      // 082: if_icmpeq 088
      // 085: goto 151
      // 088: aload 7
      // 08a: arraylength
      // 08b: bipush 20
      // 08d: if_icmpeq 093
      // 090: goto 151
      // 093: aload 8
      // 095: arraylength
      // 096: bipush 12
      // 098: if_icmpeq 09e
      // 09b: goto 151
      // 09e: bipush 42
      // 0a0: newarray 8
      // 0a2: astore 13
      // 0a4: aload 13
      // 0a6: bipush 0
      // 0a7: iload 9
      // 0a9: bipush 8
      // 0ab: ishr
      // 0ac: i2b
      // 0ad: bastore
      // 0ae: aload 13
      // 0b0: bipush 1
      // 0b1: iload 9
      // 0b3: i2b
      // 0b4: bastore
      // 0b5: aload 7
      // 0b7: bipush 0
      // 0b8: aload 13
      // 0ba: bipush 2
      // 0bc: bipush 20
      // 0be: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0c1: aload 6
      // 0c3: bipush 0
      // 0c4: aload 13
      // 0c6: bipush 22
      // 0c8: bipush 8
      // 0ca: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0cd: aload 8
      // 0cf: bipush 0
      // 0d0: aload 13
      // 0d2: bipush 30
      // 0d4: bipush 12
      // 0d6: invokestatic java/lang/System.arraycopy (Ljava/lang/Object;ILjava/lang/Object;II)V
      // 0d9: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 0dc: astore 14
      // 0de: aload 0
      // 0df: getfield net/rim/device/api/crypto/tls/SessionResumption._wtlsSyncObject Ljava/lang/Object;
      // 0e2: dup
      // 0e3: astore 15
      // 0e5: monitorenter
      // 0e6: aload 14
      // 0e8: aload 0
      // 0e9: invokestatic net/rim/device/api/system/SIMCard.addListener (Lnet/rim/device/api/system/Application;Lnet/rim/device/api/system/SIMCardListener;)V
      // 0ec: aload 0
      // 0ed: bipush -1
      // 0ef: putfield net/rim/device/api/crypto/tls/SessionResumption._simResult I
      // 0f2: bipush 0
      // 0f3: istore 16
      // 0f5: iload 16
      // 0f7: bipush 2
      // 0f9: if_icmpge 128
      // 0fc: iload 3
      // 0fd: iload 4
      // 0ff: aload 13
      // 101: iload 5
      // 103: invokestatic net/rim/device/internal/crypto/WTLSSessionKey.writeKey (II[BI)Z
      // 106: pop
      // 107: aload 0
      // 108: getfield net/rim/device/api/crypto/tls/SessionResumption._wtlsSyncObject Ljava/lang/Object;
      // 10b: sipush 1000
      // 10e: i2l
      // 10f: invokevirtual java/lang/Object.wait (J)V
      // 112: goto 117
      // 115: astore 17
      // 117: aload 0
      // 118: getfield net/rim/device/api/crypto/tls/SessionResumption._simResult I
      // 11b: bipush 1
      // 11c: if_icmpne 122
      // 11f: goto 128
      // 122: iinc 16 1
      // 125: goto 0f5
      // 128: aload 15
      // 12a: monitorexit
      // 12b: goto 136
      // 12e: astore 18
      // 130: aload 15
      // 132: monitorexit
      // 133: aload 18
      // 135: athrow
      // 136: aload 14
      // 138: aload 0
      // 139: invokestatic net/rim/device/api/system/SIMCard.removeListener (Lnet/rim/device/api/system/Application;Lnet/rim/device/api/system/SIMCardListener;)V
      // 13c: return
      // 13d: astore 15
      // 13f: aload 14
      // 141: aload 0
      // 142: invokestatic net/rim/device/api/system/SIMCard.removeListener (Lnet/rim/device/api/system/Application;Lnet/rim/device/api/system/SIMCardListener;)V
      // 145: return
      // 146: astore 19
      // 148: aload 14
      // 14a: aload 0
      // 14b: invokestatic net/rim/device/api/system/SIMCard.removeListener (Lnet/rim/device/api/system/Application;Lnet/rim/device/api/system/SIMCardListener;)V
      // 14e: aload 19
      // 150: athrow
      // 151: return
      // try (135 -> 140): 141 null
      // try (118 -> 151): 152 null
      // try (152 -> 155): 152 null
      // try (113 -> 157): 161 null
      // try (113 -> 157): 166 null
      // try (161 -> 162): 166 null
      // try (166 -> 167): 166 null
   }

   public final int getSession(String domainName, String protocol, byte[] sessionID, byte[] masterSecret) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return -1;
      }

      SessionIdentifier identifier = new SessionIdentifier(domainName, protocol, false);
      SessionInformation info = (SessionInformation)this._connections.get(identifier);
      if (info == null) {
         return -1;
      }

      byte[] tempSessionID = info.getSessionID();
      if (sessionID.length != tempSessionID.length) {
         Array.resize(sessionID, tempSessionID.length);
      }

      System.arraycopy(tempSessionID, 0, sessionID, 0, tempSessionID.length);
      byte[] tempMasterSecret = info.getMasterSecret();
      if (masterSecret.length != tempMasterSecret.length) {
         throw new IllegalArgumentException();
      }

      System.arraycopy(tempMasterSecret, 0, masterSecret, 0, tempMasterSecret.length);
      return info.getCipherSuite();
   }

   public final int getSessionFromPermanentStore(int persistType, String domainName, int ipAddress, int port, byte[] sessionID, byte[] masterSecret) {
      if ((persistType & 2) != 0) {
         NvStoreInfo info = (NvStoreInfo)this._nvStore.get(domainName);
         if (info != null) {
            byte[] tempSessionID = info.getSessionID();
            if (sessionID.length != tempSessionID.length) {
               Array.resize(sessionID, tempSessionID.length);
            }

            System.arraycopy(tempSessionID, 0, sessionID, 0, tempSessionID.length);
            byte[] tempMasterSecret = info.getMasterSecret();
            if (masterSecret.length != tempMasterSecret.length) {
               throw new IllegalArgumentException();
            }

            System.arraycopy(tempMasterSecret, 0, masterSecret, 0, tempMasterSecret.length);
            return info.getCipherSuite();
         }
      }

      if ((persistType & 8) != 0) {
         try {
            byte[] simInfo = WTLSSessionKey.readKey(ipAddress, port);
            if (simInfo != null && simInfo.length == 42 && sessionID.length == 8 && masterSecret.length == 20) {
               int cipherSuite = (simInfo[0] & 255) << 8 | simInfo[1] & 255;
               System.arraycopy(simInfo, 2, masterSecret, 0, 20);
               System.arraycopy(simInfo, 22, sessionID, 0, 8);
               return cipherSuite;
            }
         } finally {
            return -1;
         }
      }

      return -1;
   }

   public final Certificate getSessionCertificate(String domainName, String protocol) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return null;
      }

      SessionIdentifier identifier = new SessionIdentifier(domainName, protocol, false);
      SessionInformation info = (SessionInformation)this._connections.get(identifier);
      return (Certificate)(info == null ? null : info.getCertificate());
   }

   public final Certificate[] getSessionCertificatePool(String domainName, String protocol) {
      Object ticket = PersistentContent.getTicket();
      if (ticket == null) {
         return null;
      }

      SessionIdentifier identifier = new SessionIdentifier(domainName, protocol, false);
      SessionInformation info = (SessionInformation)this._connections.get(identifier);
      return info == null ? null : info.getCertificatePool();
   }

   public final boolean removeSession(String domainName, String protocol) {
      SessionIdentifier identifier = new SessionIdentifier(domainName, protocol, false);
      NvStoreInfo info = (NvStoreInfo)this._nvStore.get(domainName);
      if (info != null && info.getAllowDeleteSession() && this._nvStore.remove(domainName) != null) {
         this.writeNvStore();
      }

      boolean result = this._connections.remove(identifier) != null;
      this._persist.commit();
      return result;
   }

   public final void removeAllSessions() {
      this._connections.clear();
      this._persist.commit();
   }

   public final void purgeNvStore() {
      this._nvStore.clear();
      NvStore.deleteData(3);
   }

   private final void processNvStore() {
      byte[] nvStore = NvStore.readData(3);
      if (nvStore == null) {
         nvStore = new byte[0];
      }

      this._nvStore = new Hashtable();
      if (nvStore.length > 0) {
         DataBuffer buffer = new DataBuffer(nvStore, 0, nvStore.length, true);

         try {
            byte version = buffer.readByte();
            if (version != 2) {
               buffer.rewind();
            }

            while (buffer.available() > 0) {
               String id = buffer.readUTF();
               int cipherSuite = buffer.readInt();
               byte[] sessionId = buffer.readByteArray();
               byte[] masterSecret = buffer.readByteArray();
               boolean deleteSession = true;
               if (version == 2) {
                  deleteSession = buffer.readBoolean();
               }

               this._nvStore.put(id, new NvStoreInfo(id, sessionId, masterSecret, cipherSuite, deleteSession));
            }
         } finally {
            return;
         }
      }
   }

   private final void writeNvStore() {
      DataBuffer buffer = new DataBuffer();
      if (this._nvStore != null) {
         try {
            buffer.writeByte(2);
            Enumeration elements = this._nvStore.elements();

            while (elements.hasMoreElements()) {
               Object obj = elements.nextElement();
               if (obj instanceof NvStoreInfo) {
                  NvStoreInfo info = (NvStoreInfo)obj;
                  buffer.writeUTF(info.getIdentifier());
                  buffer.writeInt(info.getCipherSuite());
                  buffer.writeByteArray(info.getSessionID());
                  buffer.writeByteArray(info.getMasterSecret());
                  buffer.writeBoolean(info.getAllowDeleteSession());
               }
            }
         } finally {
            ;
         }
      }

      NvStore.writeData(3, buffer.getArray(), buffer.getArrayStart(), buffer.getLength());
   }

   @Override
   public final void requestSendPIN(int retriesRemaining) {
   }

   @Override
   public final void requestSendPUK(int retriesRemaining) {
   }

   @Override
   public final void pinValid() {
   }

   @Override
   public final void responseEnablePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseDisablePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseChangePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseValidatePIN(int code, int id, int remainingPINRetries) {
   }

   @Override
   public final void responseDeactivateMEP(boolean success) {
   }

   @Override
   public final void wtlsKeyWriteComplete(int status) {
      synchronized (this._wtlsSyncObject) {
         this._simResult = status;
         this._wtlsSyncObject.notify();
      }
   }
}
