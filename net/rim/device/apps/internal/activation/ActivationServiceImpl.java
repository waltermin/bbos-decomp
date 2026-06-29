package net.rim.device.apps.internal.activation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.DatagramConnection;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceBook$ServiceStatus;
import net.rim.device.api.servicebook.ServiceBookSyncCollection;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.Radio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.internal.crypto.OTAKeyGenCrypto;
import net.rim.device.internal.deviceagent.OutgoingDeviceAgentCollection;
import net.rim.device.internal.provisioning.ActivationService;
import net.rim.device.internal.provisioning.ProvisioningService;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.NvStore;
import net.rim.vm.Array;

final class ActivationServiceImpl
   extends ActivationService
   implements ActivationServiceConstants,
   GlobalEventListener,
   Runnable,
   TransmissionStatusListener,
   ServiceRoutingListener2 {
   private int _numberOfActivationAttempts = 1;
   final byte NVSTORE_ENDOFFIELDS = 0;
   final byte NVSTORE_SERVICEBOOK = 1;
   final byte NVSTORE_EMAILADDRESS = 2;
   final byte NVSTORE_PASSWORD = 3;
   private Object _serviceLock = new Object();
   private OTAKeyGenEvent _lastEvent;
   private OTAKeyGenEvent _pendingEvent;
   private int _transmitTag;
   private String _transmitUID;
   private ActivationTransmissionService _transmissionService;
   private ActivationServiceImpl$ServerLongTermKeyStore _serverLongTermKeyStore;
   private ActivationServiceHandler _activationHandler;
   private String _needToAttempReKey;
   Object _ticket;
   private ApplicationEntryPoint _activationAppEntryPoint;
   private ActivationApp$OptionsProvider _activationOptionsProvider = null;
   private boolean _debug = false;
   ProvisioningService _pservice;
   public String _lastEmailAddress = "";
   private String _lastPassword = null;
   public String _lastActivationServerAddress = "";
   int _timeOutTransactionId = 0;
   int _timeOutThreadID = -1;
   int _initialTransactionId = 0;
   private String _oldKeyId;
   private String _newKeyId;
   private String _activatingUID;
   private DataBuffer _intDataBuffer = (DataBuffer)(new Object(new byte[4], 0, 4, true));
   public static final long ACTIVATION_EVENT;
   static final long EVENT_LOGGER_GUID;
   static final long EVENT_LOGGER_DATA_GUID;
   static final String EVENT_LOGGER_NAME;
   private static final long LAST_OTA_KEYGREN_STORE_ID;
   private static final int TIMEOUT_DELAY;
   private static final int MAX_RETRIES;
   private static final int USER_STATUS_ACTIVATED;
   private static final int USER_STATUS_CANCELLED;
   private static final String ACTIVATION_BBR_SUFFIX;
   private static final int ACTIVATION_PORT;

   final void initProvisioning() {
      try {
         this._pservice = ProvisioningService.getInstance();
         this._pservice.addHandler(this._activationHandler);
      } finally {
         logEvent(1347636801, 2);
         return;
      }
   }

   final void setDebugMode(boolean debugMode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int reActivation(String uid, int transactionId) {
      return this.encryptionKeyRefresh(uid, false, transactionId);
   }

   final void notifyActivationStatusListener(String collection, boolean errornous) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   final void abortTransaction(int transactionId, byte reason, boolean sendAbortPacket, String originalUid) {
      synchronized (this._serviceLock) {
         this._ticket = null;
         OTAKeyGenEvent event = this.getLastOTAKeyGenEvent();
         if (this._newKeyId != null) {
            byte flags = 2;
            if (event != null && event._keyGenerationType == 1) {
               flags = (byte)(flags | 1);
            }

            OTAKeyGenCrypto.removeSymmetricKeyByKeyID(this._newKeyId, flags);
         }

         this._newKeyId = null;
         if (this._oldKeyId != null) {
            boolean result = false;

            label172:
            try {
               result = OTAKeyGenCrypto.revertSymmetricKey(this._oldKeyId);
            } finally {
               break label172;
            }

            if (!result) {
               logEvent(1381387078, 2);
            }

            this._oldKeyId = null;
         }

         if (event != null && originalUid != null) {
            String tempUID = event._serviceUID != null ? event._serviceUID : event._emailAddress;
            if (StringUtilities.compareToIgnoreCase(tempUID, originalUid, 1701707776) == 0) {
               transactionId = event._transactionId;
            }
         }

         if (event != null && (transactionId == event._transactionId || transactionId == this._initialTransactionId)) {
            if (reason == 13) {
               this.displayError(1397638213);
            } else {
               if (event._keyGenerationType == 3 && reason != 7) {
                  this.reActivation(event._serviceUID, event._transactionId);
                  return;
               }

               if (reason == 8) {
                  if (this._numberOfActivationAttempts < 3) {
                     this.displayMessage(1381257817, null);
                     this._numberOfActivationAttempts++;
                     this.endTransaction(3844, !sendAbortPacket);
                     this.attemptActivation(this._lastEmailAddress, this._lastPassword, this._lastActivationServerAddress);
                     return;
                  }

                  this._numberOfActivationAttempts = 1;
                  this._initialTransactionId = 0;
                  this.displayError(1296128082);
               } else if (reason == 4) {
                  this.displayError(1296128082);
               } else if (reason == 10) {
                  this.displayError(1346981421);
               } else if (sendAbortPacket) {
                  OTAKeyGenEvent abortTransactionRequest = new OTAKeyGenEvent(transactionId);
                  abortTransactionRequest._abortReason = reason;
                  abortTransactionRequest._command = 4;
                  abortTransactionRequest._keyGenerationType = event._keyGenerationType;
                  String uid = event._serviceUID;
                  if (event._keyGenerationType == 1 && event._emailAddress != null && event._command == 1) {
                     uid = event._emailAddress;
                  }

                  if (!this.sendOTAKeyGenEvent(abortTransactionRequest, uid)) {
                     logEvent(1397638213, 2);
                  }
               }
            }

            this.endTransaction(3844, !sendAbortPacket);
            RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3844, reason, null, null);
         } else if (event == null) {
            this.endTransaction(3844, true);
            RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3844, reason, null, null);
         } else {
            logEvent(1415073107, 5);
         }
      }
   }

   final void serviceAccept(OTAKeyGenEvent param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serviceLock Ljava/lang/Object;
      // 004: dup
      // 005: astore 2
      // 006: monitorenter
      // 007: aconst_null
      // 008: astore 3
      // 009: aload 0
      // 00a: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.getLastOTAKeyGenEvent ()Lnet/rim/device/apps/internal/activation/OTAKeyGenEvent;
      // 00d: astore 4
      // 00f: bipush 0
      // 010: newarray 8
      // 012: astore 5
      // 014: bipush 0
      // 015: newarray 8
      // 017: astore 6
      // 019: aconst_null
      // 01a: astore 7
      // 01c: aconst_null
      // 01d: astore 8
      // 01f: aload 4
      // 021: ifnonnull 032
      // 024: aload 0
      // 025: aload 1
      // 026: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 029: bipush 1
      // 02a: bipush 1
      // 02b: aconst_null
      // 02c: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 02f: aload 2
      // 030: monitorexit
      // 031: return
      // 032: aload 4
      // 034: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 037: aload 1
      // 038: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 03b: if_icmpne 047
      // 03e: aload 4
      // 040: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._command B
      // 043: bipush 1
      // 044: if_icmpeq 056
      // 047: aload 0
      // 048: aload 4
      // 04a: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 04d: bipush 1
      // 04e: bipush 1
      // 04f: aconst_null
      // 050: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 053: aload 2
      // 054: monitorexit
      // 055: return
      // 056: aload 4
      // 058: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._emailAddress Ljava/lang/String;
      // 05b: ifnull 062
      // 05e: bipush 1
      // 05f: goto 063
      // 062: bipush 0
      // 063: istore 9
      // 065: aload 4
      // 067: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyGenerationType B
      // 06a: bipush 2
      // 06c: if_icmpne 073
      // 06f: bipush 1
      // 070: goto 074
      // 073: bipush 0
      // 074: istore 10
      // 076: aload 1
      // 077: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceUID Ljava/lang/String;
      // 07a: astore 8
      // 07c: aload 8
      // 07e: ifnonnull 088
      // 081: aload 4
      // 083: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceUID Ljava/lang/String;
      // 086: astore 8
      // 088: iload 9
      // 08a: ifne 092
      // 08d: iload 10
      // 08f: ifeq 0bc
      // 092: aload 1
      // 093: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceLongTermPublicKey [B
      // 096: astore 7
      // 098: aload 0
      // 099: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 09c: aload 8
      // 09e: ldc_w 1701707776
      // 0a1: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 0a4: aload 7
      // 0a6: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 0a9: pop
      // 0aa: aload 0
      // 0ab: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 0ae: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore.commit ()V
      // 0b1: aload 0
      // 0b2: ldc_w 1094931521
      // 0b5: aconst_null
      // 0b6: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.displayMessage (ILjava/lang/Object;)V
      // 0b9: goto 0ec
      // 0bc: aload 0
      // 0bd: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 0c0: aload 8
      // 0c2: ldc_w 1701707776
      // 0c5: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 0c8: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 0cb: checkcast [B
      // 0ce: astore 7
      // 0d0: aload 7
      // 0d2: ifnonnull 0e4
      // 0d5: aload 0
      // 0d6: aload 1
      // 0d7: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 0da: bipush 5
      // 0dc: bipush 1
      // 0dd: aconst_null
      // 0de: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 0e1: aload 2
      // 0e2: monitorexit
      // 0e3: return
      // 0e4: aload 0
      // 0e5: ldc_w 1380272961
      // 0e8: aconst_null
      // 0e9: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.displayMessage (ILjava/lang/Object;)V
      // 0ec: aload 0
      // 0ed: ldc_w 1397511243
      // 0f0: aload 7
      // 0f2: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 0f5: aload 0
      // 0f6: ldc_w 1397969995
      // 0f9: aload 1
      // 0fa: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceAuthenticationPublicKey [B
      // 0fd: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 100: aload 0
      // 101: ldc_w 1262836041
      // 104: aload 1
      // 105: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 108: invokevirtual java/lang/String.getBytes ()[B
      // 10b: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 10e: aload 4
      // 110: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._cryptoContext Lnet/rim/device/internal/crypto/OTAKeyGenCrypto;
      // 113: astore 11
      // 115: aload 11
      // 117: aload 8
      // 119: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (Ljava/lang/String;)I
      // 11c: pop
      // 11d: aload 11
      // 11f: aload 1
      // 120: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 123: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (B)I
      // 126: pop
      // 127: aload 11
      // 129: aload 1
      // 12a: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 12d: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (Ljava/lang/String;)I
      // 130: pop
      // 131: aload 11
      // 133: aload 7
      // 135: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 138: pop
      // 139: aload 11
      // 13b: aload 1
      // 13c: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceAuthenticationPublicKey [B
      // 13f: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 142: pop
      // 143: iload 9
      // 145: ifne 14d
      // 148: iload 10
      // 14a: ifeq 16a
      // 14d: aload 11
      // 14f: aload 7
      // 151: aload 1
      // 152: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceAuthenticationPublicKey [B
      // 155: bipush 19
      // 157: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 15a: aload 1
      // 15b: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyHash [B
      // 15e: aload 5
      // 160: aload 6
      // 162: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.continueActivation ([B[B[B[B[B[B)I
      // 165: istore 12
      // 167: goto 184
      // 16a: aload 11
      // 16c: aload 7
      // 16e: aload 1
      // 16f: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceAuthenticationPublicKey [B
      // 172: bipush 19
      // 174: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 177: aload 1
      // 178: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyHash [B
      // 17b: aload 5
      // 17d: aload 6
      // 17f: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.continueReKey ([B[B[B[B[B[B)I
      // 182: istore 12
      // 184: iload 12
      // 186: ifne 1c3
      // 189: new net/rim/device/apps/internal/activation/OTAKeyGenEvent
      // 18c: dup
      // 18d: aload 1
      // 18e: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 191: invokespecial net/rim/device/apps/internal/activation/OTAKeyGenEvent.<init> (I)V
      // 194: astore 3
      // 195: aload 3
      // 196: aload 4
      // 198: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyGenerationType B
      // 19b: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyGenerationType B
      // 19e: aload 3
      // 19f: bipush 3
      // 1a1: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._command B
      // 1a4: aload 3
      // 1a5: aload 6
      // 1a7: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyHash [B
      // 1aa: iload 9
      // 1ac: ifeq 1b8
      // 1af: aload 3
      // 1b0: aload 4
      // 1b2: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._emailAddress Ljava/lang/String;
      // 1b5: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._emailAddress Ljava/lang/String;
      // 1b8: aload 0
      // 1b9: ldc_w 1262836054
      // 1bc: aconst_null
      // 1bd: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.displayMessage (ILjava/lang/Object;)V
      // 1c0: goto 1d2
      // 1c3: aload 0
      // 1c4: aload 1
      // 1c5: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 1c8: bipush 5
      // 1ca: bipush 1
      // 1cb: aconst_null
      // 1cc: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 1cf: aload 2
      // 1d0: monitorexit
      // 1d1: return
      // 1d2: aload 3
      // 1d3: aload 8
      // 1d5: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceUID Ljava/lang/String;
      // 1d8: aload 3
      // 1d9: aload 1
      // 1da: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 1dd: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 1e0: aload 3
      // 1e1: aload 1
      // 1e2: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 1e5: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 1e8: aload 3
      // 1e9: aload 5
      // 1eb: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._masterKey [B
      // 1ee: aload 0
      // 1ef: aload 3
      // 1f0: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 1f3: aload 3
      // 1f4: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 1f7: invokevirtual java/lang/String.getBytes ()[B
      // 1fa: aload 5
      // 1fc: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.generateCryptoKey (B[B[B)Lnet/rim/device/api/util/DataBuffer;
      // 1ff: astore 13
      // 201: aload 0
      // 202: aload 8
      // 204: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.getKeyIDForUID (Ljava/lang/String;)Ljava/lang/String;
      // 207: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._oldKeyId Ljava/lang/String;
      // 20a: aload 0
      // 20b: aload 3
      // 20c: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 20f: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._newKeyId Ljava/lang/String;
      // 212: iload 9
      // 214: ifeq 229
      // 217: aload 8
      // 219: aload 13
      // 21b: invokestatic java/lang/System.currentTimeMillis ()J
      // 21e: ldc_w 604800000
      // 221: i2l
      // 222: ladd
      // 223: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.addSymmetricKey (Ljava/lang/String;Ljava/io/DataInput;J)V
      // 226: goto 238
      // 229: aload 8
      // 22b: aload 13
      // 22d: invokestatic java/lang/System.currentTimeMillis ()J
      // 230: ldc_w 604800000
      // 233: i2l
      // 234: ladd
      // 235: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.addSymmetricKeyAsSecondaryKey (Ljava/lang/String;Ljava/io/DataInput;J)V
      // 238: aload 0
      // 239: aload 8
      // 23b: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._activatingUID Ljava/lang/String;
      // 23e: iload 9
      // 240: ifeq 27c
      // 243: invokestatic net/rim/device/api/servicebook/ServiceBook.getSB ()Lnet/rim/device/api/servicebook/ServiceBook;
      // 246: astore 14
      // 248: aload 14
      // 24a: aload 8
      // 24c: invokevirtual net/rim/device/api/servicebook/ServiceBook.findRecordsByUid (Ljava/lang/String;)[Lnet/rim/device/api/servicebook/ServiceRecord;
      // 24f: astore 15
      // 251: aload 15
      // 253: ifnull 27c
      // 256: bipush 0
      // 257: istore 16
      // 259: iload 16
      // 25b: aload 15
      // 25d: arraylength
      // 25e: if_icmpge 27c
      // 261: aload 15
      // 263: iload 16
      // 265: aaload
      // 266: astore 17
      // 268: aload 17
      // 26a: bipush 1
      // 26b: invokevirtual net/rim/device/api/servicebook/ServiceRecord.setRestoredFromBackup (Z)V
      // 26e: aload 14
      // 270: aload 17
      // 272: invokevirtual net/rim/device/api/servicebook/ServiceBook.addRecord (Lnet/rim/device/api/servicebook/ServiceRecord;)Lnet/rim/device/api/servicebook/ServiceRecord;
      // 275: pop
      // 276: iinc 16 1
      // 279: goto 259
      // 27c: aload 0
      // 27d: aload 3
      // 27e: aload 8
      // 280: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.sendOTAKeyGenEvent (Lnet/rim/device/apps/internal/activation/OTAKeyGenEvent;Ljava/lang/String;)Z
      // 283: ifeq 2b2
      // 286: iload 9
      // 288: ifeq 293
      // 28b: aload 0
      // 28c: ldc_w 1464226646
      // 28f: aconst_null
      // 290: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.displayMessage (ILjava/lang/Object;)V
      // 293: aload 0
      // 294: aconst_null
      // 295: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._ticket Ljava/lang/Object;
      // 298: aload 2
      // 299: monitorexit
      // 29a: return
      // 29b: astore 13
      // 29d: ldc_w 1129470288
      // 2a0: bipush 2
      // 2a2: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 2a5: goto 2b2
      // 2a8: astore 13
      // 2aa: ldc_w 1431194437
      // 2ad: bipush 2
      // 2af: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 2b2: ldc_w 1397638213
      // 2b5: bipush 2
      // 2b7: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 2ba: aload 0
      // 2bb: aload 4
      // 2bd: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 2c0: bipush 1
      // 2c1: bipush 1
      // 2c2: aconst_null
      // 2c3: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 2c6: aload 2
      // 2c7: monitorexit
      // 2c8: return
      // 2c9: astore 18
      // 2cb: aload 2
      // 2cc: monitorexit
      // 2cd: aload 18
      // 2cf: athrow
      // try (230 -> 324): 327 null
      // try (230 -> 324): 332 null
      // try (5 -> 31): 349 null
      // try (32 -> 50): 349 null
      // try (51 -> 116): 349 null
      // try (117 -> 229): 349 null
      // try (230 -> 326): 349 null
      // try (327 -> 348): 349 null
      // try (349 -> 352): 349 null
   }

   final void keyPromoted(OTAKeyGenEvent param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.getLastOTAKeyGenEvent ()Lnet/rim/device/apps/internal/activation/OTAKeyGenEvent;
      // 04: astore 2
      // 05: aload 0
      // 06: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serviceLock Ljava/lang/Object;
      // 09: dup
      // 0a: astore 3
      // 0b: monitorenter
      // 0c: aload 2
      // 0d: ifnonnull 1e
      // 10: aload 0
      // 11: aload 1
      // 12: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 15: bipush 1
      // 16: bipush 1
      // 17: aconst_null
      // 18: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 1b: aload 3
      // 1c: monitorexit
      // 1d: return
      // 1e: aload 2
      // 1f: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 22: aload 1
      // 23: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 26: if_icmpeq 37
      // 29: aload 0
      // 2a: aload 2
      // 2b: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 2e: bipush 1
      // 2f: bipush 1
      // 30: aconst_null
      // 31: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IBZLjava/lang/String;)V
      // 34: aload 3
      // 35: monitorexit
      // 36: return
      // 37: aload 0
      // 38: aload 2
      // 39: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 3c: aload 2
      // 3d: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 40: invokevirtual java/lang/String.getBytes ()[B
      // 43: aload 2
      // 44: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._masterKey [B
      // 47: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.generateCryptoKey (B[B[B)Lnet/rim/device/api/util/DataBuffer;
      // 4a: astore 4
      // 4c: aload 2
      // 4d: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceUID Ljava/lang/String;
      // 50: aload 4
      // 52: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.addSymmetricKey (Ljava/lang/String;Ljava/io/DataInput;)V
      // 55: invokestatic net/rim/device/apps/internal/activation/ActivationApp.getActivationProcessId ()I
      // 58: ldc2_w -4731267519193158412
      // 5b: sipush 3841
      // 5e: bipush 0
      // 5f: aload 2
      // 60: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceUID Ljava/lang/String;
      // 63: aconst_null
      // 64: invokestatic net/rim/device/api/system/RIMGlobalMessagePoster.postGlobalEvent (IJIILjava/lang/Object;Ljava/lang/Object;)Z
      // 67: pop
      // 68: aload 0
      // 69: sipush 3841
      // 6c: bipush 1
      // 6d: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.endTransaction (IZ)V
      // 70: aload 0
      // 71: aconst_null
      // 72: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._ticket Ljava/lang/Object;
      // 75: goto 8f
      // 78: astore 4
      // 7a: ldc_w 1129470288
      // 7d: bipush 2
      // 7f: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 82: goto 8f
      // 85: astore 4
      // 87: ldc_w 1431194437
      // 8a: bipush 2
      // 8c: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 8f: aload 3
      // 90: monitorexit
      // 91: return
      // 92: astore 5
      // 94: aload 3
      // 95: monitorexit
      // 96: aload 5
      // 98: athrow
      // try (35 -> 65): 66 null
      // try (35 -> 65): 71 null
      // try (8 -> 19): 78 null
      // try (20 -> 34): 78 null
      // try (35 -> 77): 78 null
      // try (78 -> 81): 78 null
   }

   final String[] getActivatedSids() {
      long[] sidTable = ActivationService.getActivatedServices();
      String[] result = new Object[sidTable.length];

      for (int i = sidTable.length - 1; i >= 0; i--) {
         result[i] = String.valueOf(sidTable[i]);
      }

      return result;
   }

   final void clearActivationRecord(long sid) {
      ActivationService.activationComplete(false, sid);
   }

   final void markServiceActivated(long sid) {
      ActivationService.activationComplete(true, sid);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void sendPendingTransmission(String serviceUID) {
      synchronized (this._serviceLock) {
         if (this._pendingEvent != null
            && this._transmitUID != null
            && serviceUID != null
            && StringUtilities.compareToIgnoreCase(serviceUID, this._transmitUID) == 0
            && ServiceRouting.getInstance().isServiceCapable(8, this._transmitUID, -1)) {
            try {
               this._transmitTag = UIDGenerator.getUID();
               this._transmissionService.transmitObject("net.rim.OTAKeyGenProtocol", this._pendingEvent, this, this._transmitTag, null);
               this._pendingEvent = null;
            } catch (Throwable var8) {
               StringBuffer tempBuffer = (StringBuffer)(new Object());
               tempBuffer.append(StringUtilities.intToString(1397638213));
               tempBuffer.append(":");
               tempBuffer.append(E.toString());
               this.abortTransaction(this._pendingEvent._transactionId, (byte)13, false, this._transmitUID);
               return;
            }
         }
      }
   }

   public final OTAKeyGenEvent getLastOTAKeyGenEvent() {
      synchronized (this._serviceLock) {
         return this._lastEvent;
      }
   }

   final boolean hasServiceEverActivated(long sid) {
      return ActivationService.getLastSuccessfulActivationDate(sid) > 0;
   }

   final void displayMessage(int message, Object data) {
      RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3846, message, data, null);
   }

   final DataBuffer generateCryptoKey(byte keyAlgorithm, byte[] keyId, byte[] keyData) {
      DataBuffer cryptoKey = (DataBuffer)(new Object());
      cryptoKey.writeByte(keyAlgorithm);
      cryptoKey.write(keyId);
      cryptoKey.writeByte(0);
      cryptoKey.write(keyData);
      cryptoKey.setPosition(0);
      return cryptoKey;
   }

   public final void processEvent(OTAKeyGenEvent event) {
      switch (event._command) {
         case 1:
         case 3:
            EventLogger.logEvent(-5915434835955743234L, 1431194446, 2);
            return;
         case 2:
            this.serviceAccept(event);
            return;
         case 4:
         default:
            this.abortTransaction(event._transactionId, event._abortReason, false, null);
            return;
         case 5:
            this.keyPromoted(event);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void restoreDataAfterDeviceWipe() {
      byte[] data = NvStore.readData(31);
      boolean var15 = false /* VF: Semaphore variable */;

      label83:
      try {
         var15 = true;
         data = NvStore.readData(31);
         var15 = false;
      } finally {
         if (var15) {
            logEvent(1314276683, 2);
            NvStore.deleteData(31);
            break label83;
         }
      }

      if (data != null) {
         byte[] bytes = null;
         String emailAddress = null;
         String password = null;
         DataBuffer buffer = (DataBuffer)(new Object(data, 0, data.length, false));

         byte fieldID;
         label77:
         try {
            while ((fieldID = buffer.readByte()) != 0) {
               switch (fieldID) {
                  case 0:
                     break;
                  case 1:
                  default:
                     ServiceBook sb = ServiceBook.getSB();
                     bytes = buffer.readByteArray();
                     DataBuffer tempBuffer = (DataBuffer)(new Object(bytes, 0, bytes.length, false));
                     ServiceBookSyncCollection sbs = (ServiceBookSyncCollection)(new Object(sb));
                     ServiceRecord sr = (ServiceRecord)sbs.convert(tempBuffer, 1);
                     sr.setType(0);
                     sb.addRecord(sr);
                     break;
                  case 2:
                     bytes = buffer.readByteArray();
                     emailAddress = (String)(new Object(bytes));
                     break;
                  case 3:
                     bytes = buffer.readByteArray();
                     password = (String)(new Object(bytes));
               }
            }
         } finally {
            break label77;
         }

         Array.resize(data, 1);
         data[0] = 0;
         NvStore.writeData(31, data);
         if (emailAddress != null && password != null) {
            String[] args = new Object[]{emailAddress, password};
            Radio.requestPowerOn();
            Proxy.getInstance().invokeLater(new ActivationServiceImpl$AutoActivate(args), 120000, false);
         }
      }
   }

   final boolean displayIcon() {
      if (this._pservice == null) {
         this.initProvisioning();
      }

      boolean result = this._activationHandler.isOTAEnterpriseActivationProvisioned() && ITPolicy.getInteger(24, 39, 1) <= 2;
      if (result) {
         boolean alreadyRegistered = false;
         Vector optionProviders = OptionsProviderRegistration.getOptionsProviders();

         for (int i = 0; i < optionProviders.size(); i++) {
            Object o = optionProviders.elementAt(i);
            if (o instanceof ActivationApp$OptionsProvider) {
               alreadyRegistered = true;
            }
         }

         if (!alreadyRegistered) {
            OptionsProviderRegistration.registerOptionsProvider(this._activationOptionsProvider);
         } else {
            logEvent(1094799943, 5);
         }

         return ITPolicy.getBoolean(33, 10, false) ? false : this.isActivationInProgress();
      } else {
         if (!this._activationHandler.isOTAEnterpriseActivationProvisioned()) {
            logEvent(1146245206, 0);
         } else if (ITPolicy.getInteger(24, 39, 1) <= 2) {
            logEvent(1146243412, 0);
         }

         OptionsProviderRegistration.deRegisterOptionsProvider(this._activationOptionsProvider);
         return result;
      }
   }

   public final void iconRefresh() {
      RibbonLauncher rl = RibbonLauncher.getInstance();
      if (rl != null) {
         if (this.displayIcon()) {
            RibbonLauncher.getInstance().registerAction("net.rim.ActivationHomeScreenApp", this._activationAppEntryPoint);
            return;
         }

         RibbonLauncher.getInstance().unregisterAction("net.rim.ActivationHomeScreenApp");
      }
   }

   public final void storeDataBeforeDeviceWipe(String email, String password) {
      DataBuffer buffer = (DataBuffer)(new Object(false));
      DataBuffer tempBuffer = (DataBuffer)(new Object(false));
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid("PROVISIONING");
      if (records != null && records.length > 0) {
         ServiceRecord sr = records[0];
         ServiceBookSyncCollection sbs = (ServiceBookSyncCollection)(new Object(sb));
         sbs.convertServices(sr, tempBuffer, 1);
         tempBuffer.trim();
         byte[] sbBytes = tempBuffer.toArray();
         buffer.writeByte(1);
         buffer.writeByteArray(sbBytes);
      }

      if (email != null && password != null) {
         buffer.writeByte(2);
         buffer.writeByteArray(email.getBytes());
         buffer.writeByte(3);
         buffer.writeByteArray(password.getBytes());
      }

      buffer.writeByte(0);
      buffer.trim();
      NvStore.writeData(31, buffer.toArray());
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         if (this._serverLongTermKeyStore == null) {
            this._serverLongTermKeyStore = new ActivationServiceImpl$ServerLongTermKeyStore();
            if (!this._serverLongTermKeyStore.initalize()) {
               this._serverLongTermKeyStore = null;
               return;
            }

            this.restoreDataAfterDeviceWipe();
         }

         if (this._needToAttempReKey != null) {
            this.encryptionKeyRefresh(this._needToAttempReKey, true, UIDGenerator.getUID());
            return;
         }
      } else if (guid == 6213587377148297993L) {
         if (object0 instanceof Object) {
            String uid = (String)object0;
            OTAKeyGenEvent lastEvent = this.getLastOTAKeyGenEvent();
            if (lastEvent != null
               && lastEvent._command == 3
               && lastEvent._serviceUID != null
               && StringUtilities.compareToIgnoreCase(lastEvent._serviceUID, uid, 1701707776) == 0) {
               this.serviceRecordsReceived(uid);
               return;
            }
         }
      } else {
         if (guid == -1426098722237447363L) {
            EventLogger.logEvent(-5915434835955743234L, 1346981418, 0);
            return;
         }

         if (guid != -4220058463650496006L && guid != 8288627527798139133L) {
            if (guid == 2522898683889177438L) {
               this.iconRefresh();
               return;
            }

            if (guid == 8508406279413621091L) {
               this.iconRefresh();
            }
         } else {
            ServiceBook sb = ServiceBook.getSB();
            ServiceRecord targetSR = sb.getRecordById(data0);
            if (targetSR != null && targetSR.isSecureService()) {
               NvStore.deleteData(31);
               String targetUID = targetSR.getUid();
               ServiceBook$ServiceStatus serviceStatus = sb.getStatusForUid(targetUID);
               if (serviceStatus != null) {
                  int currentPIN = DeviceInfo.getDeviceId();
                  int lastPIN = serviceStatus.getLastPIN();
                  if (currentPIN != lastPIN) {
                     EventLogger.logEvent(-5915434835955743234L, 1346981419, 0);
                     ServiceRecord[] serviceRecords = sb.findRecordsByUid(targetUID);

                     for (int i = 0; serviceRecords != null && i < serviceRecords.length; i++) {
                        this.clearActivationRecord(ServiceIdentifier.createSid(serviceRecords[i]));
                     }
                  } else {
                     EventLogger.logEvent(-5915434835955743234L, 1313884494, 5);
                  }
               }

               if (StringUtilities.compareToIgnoreCase(targetSR.getCid(), "CMIME", 1701707776) == 0
                  || StringUtilities.compareToIgnoreCase(targetSR.getCid(), "CICAL", 1701707776) == 0
                  || StringUtilities.compareToIgnoreCase(targetSR.getCid(), "SYNC", 1701707776) == 0) {
                  this.iconRefresh();
                  return;
               }
            }
         }
      }
   }

   @Override
   public final void updateTransmissionStatus(TransmissionService ts, int tagInt, int codeInt, Object contextObject) {
      if (this._transmitTag == tagInt) {
         if (!this.isAnyTransactionInProgress()) {
            this.resetOtaKeyGenSR();
            return;
         }

         if (this._lastEvent != null) {
            if (codeInt == 0 && this._lastEvent._command == 3 && this._lastEvent._keyGenerationType == 1) {
               this.resetOtaKeyGenSR();

               label44:
               try {
                  this._serviceLock.wait(3000);
               } finally {
                  break label44;
               }

               this.createOtaKeyGenSR(this._lastEvent._serviceUID, this._lastEvent._emailAddress, this._lastActivationServerAddress);
               return;
            }

            if ((codeInt & 128) != 0) {
               StringBuffer tempBuffer = (StringBuffer)(new Object());
               tempBuffer.append(StringUtilities.intToString(1397638213));
               tempBuffer.append(":0x");
               tempBuffer.append(Integer.toHexString(codeInt));
               EventLogger.logEvent(-5915434835955743234L, tempBuffer.toString().getBytes(), 2);
               this.abortTransaction(this._lastEvent._transactionId, (byte)13, false, this._lastEvent._serviceUID);
            }
         }
      }
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      if (serviceState) {
         this.sendPendingTransmission(service);
      }
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
      this.sendPendingTransmission(service);
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      if (routeState && this._transmitUID != null) {
         this.sendPendingTransmission(this._transmitUID);
      }
   }

   @Override
   public final void run() {
      synchronized (this._serviceLock) {
         OTAKeyGenEvent lastEvent = this.getLastOTAKeyGenEvent();
         if (lastEvent != null && lastEvent._transactionId == this._timeOutTransactionId) {
            String uid = lastEvent._serviceUID;
            if (uid == null && lastEvent._emailAddress != null) {
               uid = lastEvent._emailAddress;
            }

            if (uid != null) {
               this.abortTransaction(lastEvent._transactionId, (byte)8, true, uid);
            }
         }
      }
   }

   private final void setPendingOTAKeyGenEvent(OTAKeyGenEvent event) {
      synchronized (this._serviceLock) {
         this._pendingEvent = event;
      }
   }

   private final byte[] flipNibbles(byte[] bytes) {
      byte[] result = new byte[bytes.length];

      for (int i = 0; i < bytes.length; i++) {
         result[i] = (byte)(bytes[i] << 4 & 0xFF | bytes[i] >>> 4 & 15);
      }

      return result;
   }

   @Override
   public final void createOtaKeyGenSR(String uid, String emailAddress, String activationServerAddress) {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] otakeygenSR = sb.findRecordsByCid(OTAKeyGenCrypto.OTAKEYGEN_CID);
      boolean addNewSR = false;
      ServiceRecord oldSR = null;
      if (otakeygenSR != null && otakeygenSR.length > 0) {
         String currOTAKeyGenUID = otakeygenSR[0].getUid();
         if (uid != null && (uid == null || StringUtilities.compareToIgnoreCase(currOTAKeyGenUID, uid, 1701707776) == 0)) {
            return;
         }

         oldSR = otakeygenSR[0];
         addNewSR = true;
         otakeygenSR = new Object[]{new Object()};
      } else if (otakeygenSR.length == 0) {
         addNewSR = true;
         otakeygenSR = new Object[]{new Object()};
      }

      otakeygenSR[0].setType(0);
      otakeygenSR[0].setUid(uid);
      otakeygenSR[0].setName(uid);
      otakeygenSR[0].setCid(OTAKeyGenCrypto.OTAKEYGEN_CID);
      otakeygenSR[0].setEncryptionMode(1);
      otakeygenSR[0].setCompressionMode(1);
      otakeygenSR[0].setInvisible(true);
      otakeygenSR[0].setRestoreDisabled(true);
      String[] bbrHosts = new Object[3];
      int[] bbrPorts = new int[3];
      int hostIndex = 0;
      int activationServerIndex = -1;
      int activationServerPort = 4101;
      if (activationServerAddress != null && activationServerAddress.length() > 0) {
         activationServerIndex = 0;
         int portStartIndex = activationServerAddress.indexOf(58) + 1;
         if (portStartIndex > 0) {
            int portEndIndex = activationServerAddress.indexOf(58, portStartIndex);

            try {
               if (portEndIndex > -1) {
                  activationServerPort = Integer.valueOf(activationServerAddress.substring(portStartIndex, portEndIndex));
               } else {
                  activationServerPort = Integer.valueOf(activationServerAddress.substring(portStartIndex));
               }
            } finally {
               ;
            }

            activationServerAddress = activationServerAddress.substring(0, portStartIndex - 1);
         }

         bbrHosts[hostIndex++] = activationServerAddress;
      }

      if (emailAddress != null) {
         String domainName = emailAddress.substring(emailAddress.indexOf(64) + 1);
         bbrHosts[hostIndex++] = ((StringBuffer)(new Object("activationBBR."))).append(domainName).toString();
      }

      bbrHosts[hostIndex++] = "activationBBR";
      Array.resize(bbrHosts, hostIndex);
      Array.resize(bbrPorts, hostIndex);

      for (int i = 0; i < hostIndex; i++) {
         if (i == activationServerIndex) {
            bbrPorts[i] = activationServerPort;
         } else {
            bbrPorts[i] = 4101;
         }
      }

      otakeygenSR[0].setBBRHosts(bbrHosts);
      otakeygenSR[0].setBBRPorts(bbrPorts);
      if (addNewSR) {
         sb.addRecord(otakeygenSR[0]);
         if (oldSR != null) {
            sb.removeRecord(oldSR);
            return;
         }
      } else {
         sb.commit();
      }
   }

   @Override
   public final void resetOtaKeyGenSR() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] records = sb.findRecordsByCid(OTAKeyGenCrypto.OTAKEYGEN_CID);
      if (records.length != 0) {
         for (int i = records.length - 1; i >= 0; i--) {
            sb.removeRecord(records[i]);
         }
      }
   }

   @Override
   public final boolean isActivationPending(String uid) {
      synchronized (this._serviceLock) {
         OTAKeyGenEvent lastEvent = this.getLastOTAKeyGenEvent();
         return uid != null
            && lastEvent != null
            && lastEvent._emailAddress != null
            && StringUtilities.compareToIgnoreCase(uid, lastEvent._serviceUID, 1701707776) == 0;
      }
   }

   @Override
   public final boolean isTransactionInProgress(String uid) {
      synchronized (this._serviceLock) {
         OTAKeyGenEvent lastEvent = this.getLastOTAKeyGenEvent();
         return uid != null && lastEvent != null && StringUtilities.compareToIgnoreCase(uid, lastEvent._serviceUID, 1701707776) == 0;
      }
   }

   @Override
   public final boolean isAnyTransactionInProgress() {
      return this.isTransactionPending() != -1;
   }

   @Override
   public final String[][][] getRegenerationUIDs() {
      ServiceBook sb = ServiceBook.getSB();
      ServiceRecord[] activeServiceRecords = sb.findRecordsByType(0);
      if (activeServiceRecords.length <= 0) {
         return (Object[][])null;
      }

      Hashtable ht = (Hashtable)(new Object());

      for (int i = 0; i < activeServiceRecords.length; i++) {
         ServiceRecord sr = activeServiceRecords[i];
         if (sr.isSecureService()) {
            String uid = StringUtilities.toLowerCase(sr.getUid(), 1701707776);
            if (!ht.contains(uid)) {
               ht.put(uid, sr);
            }
         }
      }

      String[][][] s = new Object[2][ht.size()][];
      Enumeration enumeration = ht.elements();

      for (int var8 = 0; enumeration.hasMoreElements(); var8++) {
         ServiceRecord sr = (ServiceRecord)enumeration.nextElement();
         s[0][var8] = sr.getUid();
         s[1][var8] = sr.getName();
      }

      return s;
   }

   private final int isTransactionPending() {
      synchronized (this._serviceLock) {
         OTAKeyGenEvent lastEvent = this.getLastOTAKeyGenEvent();
         return lastEvent != null ? lastEvent._keyGenerationType : -1;
      }
   }

   private final void displayError(int message) {
      RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3845, message, null, null);
      logEvent(message, 2);
   }

   private final void displayWarning(int message) {
      RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3849, message, null, null);
      logEvent(message, 3);
   }

   private final void displayDebug(int message, Object data) {
      if (this._debug) {
         RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3847, message, data, null);
      }
   }

   @Override
   public final String getUIDbyEmailAddress(String emailAddress) {
      String result = null;
      synchronized (this._serviceLock) {
         OTAKeyGenEvent event = this.getLastOTAKeyGenEvent();
         if (event != null
            && event._emailAddress != null
            && emailAddress != null
            && (
               StringUtilities.compareToIgnoreCase(event._emailAddress, emailAddress, 1701707776) == 0
                  || StringUtilities.compareToIgnoreCase(event._serviceUID, emailAddress, 1701707776) == 0
            )) {
            result = event._serviceUID;
         }

         return result;
      }
   }

   @Override
   public final void abortTransaction(int transactionId, byte reason) {
      this.abortTransaction(transactionId, reason, true, this._activatingUID);
   }

   private final void serviceRecordsReceived(String uid) {
      synchronized (this._serviceLock) {
         OTAKeyGenEvent lastEvent = this.getLastOTAKeyGenEvent();
         if (lastEvent != null) {
            try {
               ServiceBook sb = ServiceBook.getSB();
               ServiceRecord[] srs = sb.findRecordsByUid(uid);

               for (int i = 0; srs != null && i < srs.length; i++) {
                  ServiceRecord sr = srs[i];
                  int userID = sr.getUserId();
                  this.clearActivationRecord(ServiceIdentifier.createSid(srs[i]));
               }

               this.displayMessage(1398166104, null);
               DataBuffer newKey = this.generateCryptoKey(lastEvent._encryptionAlgorithm, lastEvent._fullKeyId.getBytes(), lastEvent._masterKey);
               OTAKeyGenCrypto.addSymmetricKey(lastEvent._serviceUID, newKey);
               this.endTransaction(3840, true);
               RIMGlobalMessagePoster.postGlobalEvent(ActivationApp.getActivationProcessId(), -4731267519193158412L, 3840, 0, uid, null);
               this._ticket = null;
            } finally {
               return;
            }
         }
      }
   }

   @Override
   public final void abortTransaction(String uid, byte reason) {
      this.abortTransaction(-1, reason, true, uid);
   }

   private final void setLastOTAKeyGenEvent(OTAKeyGenEvent event) {
      synchronized (this._serviceLock) {
         this._lastEvent = event;
      }
   }

   @Override
   public final int regenerateKey(String uid, boolean showUI) {
      return this.encryptionKeyRefresh(uid, showUI, -1);
   }

   @Override
   public final boolean isActivationInProgress() {
      ActivationApp eaApp = ActivationApp.getInstance();
      int appState = eaApp == null ? 0 : eaApp.getCurrentState();
      if (this.isTransactionPending() == -1 && appState != 1 && appState != 2) {
         OTASyncProgressHandler progressHandler = OTASyncProgressHandler.getInstance();
         long currentService = progressHandler.getCurrentService();
         return currentService != -1
            ? !this.hasServiceEverActivated(currentService) && progressHandler.getPercentComplete() >= 0 && progressHandler.getPercentComplete() < 100
            : false;
      } else {
         return true;
      }
   }

   ActivationServiceImpl() {
      this.setDebugMode(false);
      this._activationHandler = new ActivationServiceHandler(this);
      this.initProvisioning();
      this._activationOptionsProvider = new ActivationApp$OptionsProvider();
      this._transmissionService = new ActivationTransmissionService(this);
      TransmissionServiceManager.register(this._transmissionService);
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_activation");
      if (moduleHandle > 0) {
         ApplicationDescriptor[] activationDescriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
         if (activationDescriptors != null && activationDescriptors.length > 0) {
            this._activationAppEntryPoint = (ApplicationEntryPoint)(new Object(activationDescriptors[0]));
            this._activationAppEntryPoint.set(9, "net_rim_bb_activation.ActivationApp");
         }
      }

      this.resetOtaKeyGenSR();
      ServiceRouting.getInstance().addListener(this);
      this.updateDeviceCapabilities();
      this.restoreDataAfterDeviceWipe();
   }

   private final void endTransaction(int status, boolean removeSR) {
      synchronized (this._serviceLock) {
         this.setLastOTAKeyGenEvent(null);
         this._oldKeyId = null;
         this._newKeyId = null;
         this._activatingUID = null;
         this._transmitUID = null;
         if (removeSR) {
            this.setPendingOTAKeyGenEvent(null);
            this.resetOtaKeyGenSR();
         }

         if (this._timeOutThreadID != -1) {
            Proxy.getInstance().cancelInvokeLater(this._timeOutThreadID);
         }

         this._timeOutTransactionId = 0;
         this._timeOutThreadID = -1;
         this._ticket = null;
         this._serviceLock.notify();
      }
   }

   static final void logEvent(int eventid, int level) {
      EventLogger.logEvent(-5915434835955743234L, eventid, level);
   }

   private final boolean sendOTAKeyGenEvent(OTAKeyGenEvent keyGenEvent, String uid) {
      try {
         this._transmissionService
            .setNewSenderConnection((DatagramConnection)Connector.open(((StringBuffer)(new Object("gme:OTAKEYGEN/"))).append(uid).toString()));
         this._transmitUID = uid;
         this.createOtaKeyGenSR(uid, keyGenEvent._emailAddress, this._lastActivationServerAddress);
         if (this._timeOutThreadID != -1) {
            Proxy.getInstance().cancelInvokeLater(this._timeOutThreadID);
         }

         if (keyGenEvent._command != 4) {
            this._timeOutTransactionId = keyGenEvent.getTransactionId();
            this._timeOutThreadID = Proxy.getInstance().invokeLater(this, 1200000, false);
         }

         if (keyGenEvent._keyGenerationType == 1 && !ServiceRouting.getInstance().isServiceCapable(8, this._transmitUID, -1)) {
            this.setPendingOTAKeyGenEvent(keyGenEvent);
         } else {
            this._transmitTag = UIDGenerator.getUID();
            this._transmissionService.transmitObject("net.rim.OTAKeyGenProtocol", keyGenEvent, this, this._transmitTag, null);
            this.setPendingOTAKeyGenEvent(null);
         }

         this.setLastOTAKeyGenEvent(keyGenEvent);
         return true;
      } finally {
         ;
      }
   }

   @Override
   public final int attemptActivation(String emailAddress, String password, String activationServerAddress) {
      return this.attemptActivation(emailAddress, password, activationServerAddress, true);
   }

   private final int encryptionKeyRefresh(String param1, boolean param2, int param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serviceLock Ljava/lang/Object;
      // 004: dup
      // 005: astore 4
      // 007: monitorenter
      // 008: aload 0
      // 009: invokestatic net/rim/device/api/system/PersistentContent.getTicket ()Ljava/lang/Object;
      // 00c: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._ticket Ljava/lang/Object;
      // 00f: aload 0
      // 010: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._ticket Ljava/lang/Object;
      // 013: ifnonnull 027
      // 016: aload 0
      // 017: aload 1
      // 018: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._needToAttempReKey Ljava/lang/String;
      // 01b: aload 0
      // 01c: iload 3
      // 01d: bipush 1
      // 01e: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.abortTransaction (IB)V
      // 021: bipush -1
      // 023: aload 4
      // 025: monitorexit
      // 026: ireturn
      // 027: aload 0
      // 028: aconst_null
      // 029: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._needToAttempReKey Ljava/lang/String;
      // 02c: aload 0
      // 02d: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 030: ifnonnull 057
      // 033: aload 0
      // 034: new net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore
      // 037: dup
      // 038: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore.<init> ()V
      // 03b: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 03e: aload 0
      // 03f: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 042: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore.initalize ()Z
      // 045: ifne 053
      // 048: aload 0
      // 049: aconst_null
      // 04a: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 04d: bipush -1
      // 04f: aload 4
      // 051: monitorexit
      // 052: ireturn
      // 053: aload 0
      // 054: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.restoreDataAfterDeviceWipe ()V
      // 057: aload 4
      // 059: monitorexit
      // 05a: goto 065
      // 05d: astore 5
      // 05f: aload 4
      // 061: monitorexit
      // 062: aload 5
      // 064: athrow
      // 065: iload 3
      // 066: bipush -1
      // 068: if_icmpeq 06f
      // 06b: bipush 1
      // 06c: goto 070
      // 06f: bipush 0
      // 070: istore 4
      // 072: iload 2
      // 073: ifeq 0a1
      // 076: bipush 1
      // 077: invokestatic net/rim/device/apps/internal/activation/ActivationApp.isRunning (Z)Z
      // 07a: ifeq 09d
      // 07d: invokestatic net/rim/device/apps/internal/activation/ActivationApp.getInstance ()Lnet/rim/device/apps/internal/activation/ActivationApp;
      // 080: astore 5
      // 082: aload 5
      // 084: invokevirtual net/rim/device/apps/internal/activation/ActivationApp.getCurrentState ()I
      // 087: ifne 0a1
      // 08a: aload 5
      // 08c: getfield net/rim/device/apps/internal/activation/ActivationApp._screen Lnet/rim/device/apps/internal/activation/ActivationScreen;
      // 08f: aload 1
      // 090: putfield net/rim/device/apps/internal/activation/ActivationScreen._uid Ljava/lang/String;
      // 093: aload 5
      // 095: bipush 3
      // 097: invokevirtual net/rim/device/apps/internal/activation/ActivationApp.setCurrentState (I)V
      // 09a: goto 0a1
      // 09d: aconst_null
      // 09e: invokestatic net/rim/device/apps/internal/activation/ActivationApp.run ([Ljava/lang/String;)V
      // 0a1: invokestatic net/rim/device/apps/internal/activation/OTASyncProgressHandler.getInstance ()Lnet/rim/device/apps/internal/activation/OTASyncProgressHandler;
      // 0a4: invokevirtual net/rim/device/apps/internal/activation/OTASyncProgressHandler.isSyncInProgress ()Z
      // 0a7: ifeq 0b4
      // 0aa: aload 0
      // 0ab: ldc_w 1263686990
      // 0ae: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayWarning (I)V
      // 0b1: bipush -1
      // 0b3: ireturn
      // 0b4: aload 0
      // 0b5: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.isTransactionPending ()I
      // 0b8: istore 5
      // 0ba: iload 4
      // 0bc: ifne 0dc
      // 0bf: iload 5
      // 0c1: bipush -1
      // 0c3: if_icmpeq 0dc
      // 0c6: aload 0
      // 0c7: iload 5
      // 0c9: bipush 1
      // 0ca: if_icmpne 0d3
      // 0cd: ldc_w 1094931529
      // 0d0: goto 0d6
      // 0d3: ldc_w 1380669769
      // 0d6: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 0d9: bipush -1
      // 0db: ireturn
      // 0dc: aconst_null
      // 0dd: astore 6
      // 0df: aload 1
      // 0e0: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.getSymmetricKey (Ljava/lang/String;)[B
      // 0e3: astore 6
      // 0e5: goto 0ea
      // 0e8: astore 7
      // 0ea: aload 6
      // 0ec: ifnonnull 0f9
      // 0ef: aload 0
      // 0f0: ldc_w 1129202514
      // 0f3: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 0f6: bipush -1
      // 0f8: ireturn
      // 0f9: aconst_null
      // 0fa: astore 7
      // 0fc: aconst_null
      // 0fd: astore 8
      // 0ff: aconst_null
      // 100: astore 9
      // 102: bipush 18
      // 104: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 107: astore 8
      // 109: bipush 19
      // 10b: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 10e: astore 9
      // 110: goto 145
      // 113: astore 10
      // 115: ldc_w 1314276683
      // 118: bipush 2
      // 11a: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 11d: bipush 18
      // 11f: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 122: pop
      // 123: bipush 18
      // 125: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 128: pop
      // 129: goto 145
      // 12c: astore 10
      // 12e: ldc_w 1314276683
      // 131: bipush 2
      // 133: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 136: bipush 18
      // 138: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 13b: pop
      // 13c: bipush 18
      // 13e: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 141: pop
      // 142: bipush -1
      // 144: ireturn
      // 145: bipush 0
      // 146: newarray 8
      // 148: astore 10
      // 14a: bipush 0
      // 14b: istore 11
      // 14d: aconst_null
      // 14e: astore 12
      // 150: bipush 3
      // 152: istore 13
      // 154: iload 4
      // 156: ifne 16d
      // 159: aload 0
      // 15a: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 15d: aload 1
      // 15e: ldc_w 1701707776
      // 161: invokestatic net/rim/device/api/util/StringUtilities.toLowerCase (Ljava/lang/String;I)Ljava/lang/String;
      // 164: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 167: ifnonnull 16d
      // 16a: bipush 1
      // 16b: istore 4
      // 16d: new java/lang/Object
      // 170: dup
      // 171: invokespecial net/rim/device/internal/crypto/OTAKeyGenCrypto.<init> ()V
      // 174: astore 12
      // 176: aload 8
      // 178: ifnull 185
      // 17b: aload 9
      // 17d: ifnull 185
      // 180: iload 4
      // 182: ifeq 1d1
      // 185: aload 8
      // 187: ifnull 18f
      // 18a: aload 9
      // 18c: ifnonnull 1b7
      // 18f: bipush 0
      // 190: newarray 8
      // 192: astore 8
      // 194: bipush 0
      // 195: newarray 8
      // 197: astore 9
      // 199: aload 8
      // 19b: aload 9
      // 19d: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.generateKeyPair ([B[B)I
      // 1a0: istore 11
      // 1a2: iload 11
      // 1a4: ifne 1b7
      // 1a7: bipush 18
      // 1a9: aload 8
      // 1ab: invokestatic net/rim/device/internal/system/NvStore.writeData (I[B)Z
      // 1ae: pop
      // 1af: bipush 19
      // 1b1: aload 9
      // 1b3: invokestatic net/rim/device/internal/system/NvStore.writeData (I[B)Z
      // 1b6: pop
      // 1b7: iload 11
      // 1b9: ifne 1da
      // 1bc: aload 12
      // 1be: aload 6
      // 1c0: aload 10
      // 1c2: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.beginActivation ([B[B)I
      // 1c5: istore 11
      // 1c7: bipush 2
      // 1c9: istore 13
      // 1cb: bipush 1
      // 1cc: istore 4
      // 1ce: goto 1da
      // 1d1: aload 12
      // 1d3: aload 10
      // 1d5: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.beginReKey ([B)I
      // 1d8: istore 11
      // 1da: iload 3
      // 1db: bipush -1
      // 1dd: if_icmpne 1e4
      // 1e0: invokestatic net/rim/device/api/synchronization/UIDGenerator.getUID ()I
      // 1e3: istore 3
      // 1e4: iload 11
      // 1e6: ifne 26b
      // 1e9: new net/rim/device/apps/internal/activation/OTAKeyGenEvent
      // 1ec: dup
      // 1ed: invokespecial net/rim/device/apps/internal/activation/OTAKeyGenEvent.<init> ()V
      // 1f0: astore 7
      // 1f2: iload 4
      // 1f4: ifeq 21e
      // 1f7: aload 7
      // 1f9: iload 3
      // 1fa: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 1fd: aload 7
      // 1ff: aload 8
      // 201: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceLongTermPublicKey [B
      // 204: aload 7
      // 206: aload 1
      // 207: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.getKeyIDForUID (Ljava/lang/String;)Ljava/lang/String;
      // 20a: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 20d: aload 7
      // 20f: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 212: ifnonnull 21e
      // 215: aload 0
      // 216: ldc_w 1212240712
      // 219: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 21c: bipush 0
      // 21d: ireturn
      // 21e: aload 7
      // 220: bipush 1
      // 221: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._command B
      // 224: aload 7
      // 226: aload 1
      // 227: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._serviceUID Ljava/lang/String;
      // 22a: aload 7
      // 22c: aload 12
      // 22e: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._cryptoContext Lnet/rim/device/internal/crypto/OTAKeyGenCrypto;
      // 231: aload 7
      // 233: aload 10
      // 235: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceAuthenticationPublicKey [B
      // 238: aload 7
      // 23a: invokestatic net/rim/device/api/synchronization/UIDGenerator.getUID ()I
      // 23d: i2s
      // 23e: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keySequenceHint I
      // 241: aload 7
      // 243: iload 13
      // 245: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyGenerationType B
      // 248: invokestatic net/rim/device/internal/deviceagent/OutgoingDeviceAgentCollection.getInstance ()Lnet/rim/device/internal/deviceagent/DeviceAgentCollection;
      // 24b: checkcast java/lang/Object
      // 24e: astore 14
      // 250: aload 14
      // 252: bipush 6
      // 254: invokevirtual net/rim/device/internal/deviceagent/OutgoingDeviceAgentCollection.getDeviceCapabilities (B)[B
      // 257: astore 15
      // 259: aload 15
      // 25b: arraylength
      // 25c: ifle 274
      // 25f: aload 7
      // 261: aload 15
      // 263: bipush 0
      // 264: baload
      // 265: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 268: goto 274
      // 26b: aload 0
      // 26c: ldc_w 1129470288
      // 26f: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 272: bipush 0
      // 273: ireturn
      // 274: aload 0
      // 275: ldc_w 1380272979
      // 278: aconst_null
      // 279: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.displayMessage (ILjava/lang/Object;)V
      // 27c: aload 0
      // 27d: ldc_w 1146311755
      // 280: aload 10
      // 282: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 285: iload 4
      // 287: ifeq 293
      // 28a: aload 0
      // 28b: ldc_w 1145853003
      // 28e: aload 8
      // 290: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 293: aload 0
      // 294: aload 7
      // 296: aload 1
      // 297: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.sendOTAKeyGenEvent (Lnet/rim/device/apps/internal/activation/OTAKeyGenEvent;Ljava/lang/String;)Z
      // 29a: ifeq 2f2
      // 29d: iload 4
      // 29f: ifeq 2b8
      // 2a2: aload 12
      // 2a4: aload 7
      // 2a6: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._reKeyAlgorithm B
      // 2a9: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (B)I
      // 2ac: pop
      // 2ad: aload 12
      // 2af: aload 7
      // 2b1: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._fullKeyId Ljava/lang/String;
      // 2b4: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (Ljava/lang/String;)I
      // 2b7: pop
      // 2b8: aload 12
      // 2ba: aload 7
      // 2bc: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 2bf: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (I)I
      // 2c2: pop
      // 2c3: aload 12
      // 2c5: aload 7
      // 2c7: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 2ca: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (B)I
      // 2cd: pop
      // 2ce: aload 12
      // 2d0: aload 7
      // 2d2: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keySequenceHint I
      // 2d5: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (I)I
      // 2d8: pop
      // 2d9: aload 12
      // 2db: aload 8
      // 2dd: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 2e0: pop
      // 2e1: aload 12
      // 2e3: aload 7
      // 2e5: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceAuthenticationPublicKey [B
      // 2e8: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 2eb: pop
      // 2ec: aload 7
      // 2ee: invokevirtual net/rim/device/apps/internal/activation/OTAKeyGenEvent.getTransactionId ()I
      // 2f1: ireturn
      // 2f2: aload 0
      // 2f3: ldc_w 1397638213
      // 2f6: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 2f9: bipush 0
      // 2fa: ireturn
      // try (5 -> 21): 49 null
      // try (22 -> 43): 49 null
      // try (44 -> 48): 49 null
      // try (49 -> 52): 49 null
      // try (109 -> 112): 113 null
      // try (127 -> 133): 134 null
      // try (127 -> 133): 145 null
   }

   private final void updateDeviceCapabilities() {
      OutgoingDeviceAgentCollection deviceAgent = (OutgoingDeviceAgentCollection)OutgoingDeviceAgentCollection.getInstance();
      if (deviceAgent != null) {
         if (SIMCard.isSupported()) {
            label42:
            try {
               byte[] data = SIMCard.getICCID();
               data = this.flipNibbles(data);
               deviceAgent.addDeviceCapabilities((byte)27, data);
               data = SIMCard.getIMSI();
               deviceAgent.addDeviceCapabilities((byte)28, data);
            } finally {
               break label42;
            }
         }

         int networkType = RadioInfo.getNetworkType();
         switch (networkType) {
            case 2:
            case 6:
               break;
            case 3:
            case 7:
            default:
               deviceAgent.addDeviceCapabilities((byte)32, GPRSInfo.getIMEI());
               break;
            case 4:
               deviceAgent.addDeviceCapabilities((byte)33, this.convertInt(CDMAInfo.getESN()));
               deviceAgent.addDeviceCapabilities((byte)28, CDMAInfo.getIMSI());
               break;
            case 5:
               deviceAgent.addDeviceCapabilities((byte)32, IDENInfo.getIMEI());
         }

         byte[] operatorName = deviceAgent.getOperatorName().getBytes();
         if (operatorName != null && operatorName.length > 0) {
            deviceAgent.addDeviceCapabilities((byte)31, operatorName);
         }
      }
   }

   private final int attemptActivation(String param1, String param2, String param3, boolean param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serviceLock Ljava/lang/Object;
      // 004: dup
      // 005: astore 5
      // 007: monitorenter
      // 008: aload 0
      // 009: aload 1
      // 00a: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._lastEmailAddress Ljava/lang/String;
      // 00d: aload 0
      // 00e: aload 2
      // 00f: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._lastPassword Ljava/lang/String;
      // 012: aload 0
      // 013: aload 3
      // 014: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._lastActivationServerAddress Ljava/lang/String;
      // 017: aload 0
      // 018: invokestatic net/rim/device/api/system/PersistentContent.getTicket ()Ljava/lang/Object;
      // 01b: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._ticket Ljava/lang/Object;
      // 01e: aload 0
      // 01f: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._ticket Ljava/lang/Object;
      // 022: ifnonnull 02b
      // 025: bipush -1
      // 027: aload 5
      // 029: monitorexit
      // 02a: ireturn
      // 02b: aload 0
      // 02c: aconst_null
      // 02d: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._needToAttempReKey Ljava/lang/String;
      // 030: aload 0
      // 031: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 034: ifnonnull 05b
      // 037: aload 0
      // 038: new net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore
      // 03b: dup
      // 03c: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore.<init> ()V
      // 03f: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 042: aload 0
      // 043: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 046: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore.initalize ()Z
      // 049: ifne 057
      // 04c: aload 0
      // 04d: aconst_null
      // 04e: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._serverLongTermKeyStore Lnet/rim/device/apps/internal/activation/ActivationServiceImpl$ServerLongTermKeyStore;
      // 051: bipush -1
      // 053: aload 5
      // 055: monitorexit
      // 056: ireturn
      // 057: aload 0
      // 058: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.restoreDataAfterDeviceWipe ()V
      // 05b: aload 5
      // 05d: monitorexit
      // 05e: goto 069
      // 061: astore 6
      // 063: aload 5
      // 065: monitorexit
      // 066: aload 6
      // 068: athrow
      // 069: iload 4
      // 06b: ifeq 072
      // 06e: aconst_null
      // 06f: invokestatic net/rim/device/apps/internal/activation/ActivationApp.run ([Ljava/lang/String;)V
      // 072: aload 0
      // 073: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.isTransactionPending ()I
      // 076: istore 5
      // 078: iload 5
      // 07a: bipush -1
      // 07c: if_icmpeq 095
      // 07f: aload 0
      // 080: iload 5
      // 082: bipush 1
      // 083: if_icmpne 08c
      // 086: ldc_w 1094931529
      // 089: goto 08f
      // 08c: ldc_w 1380669769
      // 08f: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 092: bipush -1
      // 094: ireturn
      // 095: aconst_null
      // 096: astore 6
      // 098: bipush 0
      // 099: newarray 8
      // 09b: astore 7
      // 09d: new java/lang/Object
      // 0a0: dup
      // 0a1: invokespecial net/rim/device/internal/crypto/OTAKeyGenCrypto.<init> ()V
      // 0a4: astore 8
      // 0a6: aload 8
      // 0a8: aload 2
      // 0a9: invokevirtual java/lang/String.getBytes ()[B
      // 0ac: aload 7
      // 0ae: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.beginActivation ([B[B)I
      // 0b1: istore 9
      // 0b3: aconst_null
      // 0b4: astore 10
      // 0b6: aconst_null
      // 0b7: astore 11
      // 0b9: bipush 18
      // 0bb: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 0be: astore 10
      // 0c0: bipush 19
      // 0c2: invokestatic net/rim/device/internal/system/NvStore.readData (I)[B
      // 0c5: astore 11
      // 0c7: goto 0fc
      // 0ca: astore 12
      // 0cc: ldc_w 1314276683
      // 0cf: bipush 2
      // 0d1: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 0d4: bipush 18
      // 0d6: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 0d9: pop
      // 0da: bipush 18
      // 0dc: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 0df: pop
      // 0e0: goto 0fc
      // 0e3: astore 12
      // 0e5: ldc_w 1314276683
      // 0e8: bipush 2
      // 0ea: invokestatic net/rim/device/apps/internal/activation/ActivationServiceImpl.logEvent (II)V
      // 0ed: bipush 18
      // 0ef: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 0f2: pop
      // 0f3: bipush 18
      // 0f5: invokestatic net/rim/device/internal/system/NvStore.deleteData (I)Z
      // 0f8: pop
      // 0f9: bipush -1
      // 0fb: ireturn
      // 0fc: bipush 0
      // 0fd: istore 12
      // 0ff: aload 10
      // 101: ifnull 109
      // 104: aload 11
      // 106: ifnonnull 12c
      // 109: bipush 0
      // 10a: newarray 8
      // 10c: astore 10
      // 10e: bipush 0
      // 10f: newarray 8
      // 111: astore 11
      // 113: aload 10
      // 115: aload 11
      // 117: invokestatic net/rim/device/internal/crypto/OTAKeyGenCrypto.generateKeyPair ([B[B)I
      // 11a: istore 12
      // 11c: bipush 18
      // 11e: aload 10
      // 120: invokestatic net/rim/device/internal/system/NvStore.writeData (I[B)Z
      // 123: pop
      // 124: bipush 19
      // 126: aload 11
      // 128: invokestatic net/rim/device/internal/system/NvStore.writeData (I[B)Z
      // 12b: pop
      // 12c: iload 9
      // 12e: ifne 1ae
      // 131: iload 12
      // 133: ifne 1ae
      // 136: new net/rim/device/apps/internal/activation/OTAKeyGenEvent
      // 139: dup
      // 13a: invokespecial net/rim/device/apps/internal/activation/OTAKeyGenEvent.<init> ()V
      // 13d: astore 6
      // 13f: aload 6
      // 141: aload 1
      // 142: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._emailAddress Ljava/lang/String;
      // 145: aload 6
      // 147: bipush 1
      // 148: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._command B
      // 14b: aload 6
      // 14d: aload 7
      // 14f: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceAuthenticationPublicKey [B
      // 152: aload 6
      // 154: aload 10
      // 156: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceLongTermPublicKey [B
      // 159: aload 6
      // 15b: invokestatic net/rim/device/api/synchronization/UIDGenerator.getUID ()I
      // 15e: i2s
      // 15f: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keySequenceHint I
      // 162: aload 6
      // 164: aload 8
      // 166: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._cryptoContext Lnet/rim/device/internal/crypto/OTAKeyGenCrypto;
      // 169: aload 6
      // 16b: bipush 1
      // 16c: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keyGenerationType B
      // 16f: aload 0
      // 170: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.updateDeviceCapabilities ()V
      // 173: invokestatic net/rim/device/internal/deviceagent/OutgoingDeviceAgentCollection.getInstance ()Lnet/rim/device/internal/deviceagent/DeviceAgentCollection;
      // 176: checkcast java/lang/Object
      // 179: astore 13
      // 17b: aload 13
      // 17d: bipush 7
      // 17f: invokevirtual net/rim/device/internal/deviceagent/OutgoingDeviceAgentCollection.getDeviceAgentInfo (B)Lnet/rim/device/api/util/DataBuffer;
      // 182: astore 14
      // 184: aload 14
      // 186: ifnull 193
      // 189: aload 6
      // 18b: aload 14
      // 18d: invokevirtual net/rim/device/api/util/DataBuffer.toArray ()[B
      // 190: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceCapabilities [B
      // 193: aload 13
      // 195: bipush 6
      // 197: invokevirtual net/rim/device/internal/deviceagent/OutgoingDeviceAgentCollection.getDeviceCapabilities (B)[B
      // 19a: astore 15
      // 19c: aload 15
      // 19e: arraylength
      // 19f: ifle 1b7
      // 1a2: aload 6
      // 1a4: aload 15
      // 1a6: bipush 0
      // 1a7: baload
      // 1a8: putfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 1ab: goto 1b7
      // 1ae: aload 0
      // 1af: ldc_w 1129470288
      // 1b2: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 1b5: bipush 0
      // 1b6: ireturn
      // 1b7: aload 0
      // 1b8: ldc_w 1094931539
      // 1bb: aconst_null
      // 1bc: invokevirtual net/rim/device/apps/internal/activation/ActivationServiceImpl.displayMessage (ILjava/lang/Object;)V
      // 1bf: aload 0
      // 1c0: ldc_w 1145853003
      // 1c3: aload 10
      // 1c5: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 1c8: aload 0
      // 1c9: ldc_w 1146311755
      // 1cc: aload 7
      // 1ce: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayDebug (ILjava/lang/Object;)V
      // 1d1: aload 0
      // 1d2: aload 6
      // 1d4: aload 1
      // 1d5: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.sendOTAKeyGenEvent (Lnet/rim/device/apps/internal/activation/OTAKeyGenEvent;Ljava/lang/String;)Z
      // 1d8: ifeq 255
      // 1db: aload 8
      // 1dd: aload 6
      // 1df: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._pin I
      // 1e2: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (I)I
      // 1e5: pop
      // 1e6: aload 8
      // 1e8: aload 6
      // 1ea: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._networkType B
      // 1ed: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (B)I
      // 1f0: pop
      // 1f1: aload 8
      // 1f3: aload 6
      // 1f5: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceCapabilities [B
      // 1f8: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 1fb: pop
      // 1fc: aload 8
      // 1fe: aload 6
      // 200: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._reKeyAlgorithm B
      // 203: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (B)I
      // 206: pop
      // 207: aload 8
      // 209: aload 6
      // 20b: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._transactionId I
      // 20e: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (I)I
      // 211: pop
      // 212: aload 8
      // 214: aload 6
      // 216: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._encryptionAlgorithm B
      // 219: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (B)I
      // 21c: pop
      // 21d: aload 8
      // 21f: aload 6
      // 221: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._keySequenceHint I
      // 224: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash (I)I
      // 227: pop
      // 228: aload 8
      // 22a: aload 6
      // 22c: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceLongTermPublicKey [B
      // 22f: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 232: pop
      // 233: aload 8
      // 235: aload 6
      // 237: getfield net/rim/device/apps/internal/activation/OTAKeyGenEvent._deviceAuthenticationPublicKey [B
      // 23a: invokevirtual net/rim/device/internal/crypto/OTAKeyGenCrypto.addDataToHash ([B)I
      // 23d: pop
      // 23e: aload 6
      // 240: invokevirtual net/rim/device/apps/internal/activation/OTAKeyGenEvent.getTransactionId ()I
      // 243: istore 13
      // 245: aload 0
      // 246: getfield net/rim/device/apps/internal/activation/ActivationServiceImpl._initialTransactionId I
      // 249: ifne 252
      // 24c: aload 0
      // 24d: iload 13
      // 24f: putfield net/rim/device/apps/internal/activation/ActivationServiceImpl._initialTransactionId I
      // 252: iload 13
      // 254: ireturn
      // 255: aload 0
      // 256: ldc_w 1397638213
      // 259: invokespecial net/rim/device/apps/internal/activation/ActivationServiceImpl.displayError (I)V
      // 25c: bipush 0
      // 25d: ireturn
      // try (5 -> 23): 51 null
      // try (24 -> 45): 51 null
      // try (46 -> 50): 51 null
      // try (51 -> 54): 51 null
      // try (95 -> 101): 102 null
      // try (95 -> 101): 113 null
   }

   static final void serviceIdUpdated(long oldServiceId, long newServiceId) {
      ActivationService.serviceIdChanged(oldServiceId, newServiceId);
   }

   private final byte[] convertInt(int integer) {
      this._intDataBuffer.reset();
      this._intDataBuffer.writeInt(integer);
      this._intDataBuffer.trim();
      return this._intDataBuffer.toArray();
   }

   @Override
   public final SyncCollection[] getCollections() {
      SyncCollection[] collections = new Object[1];
      collections[0] = new ActivationServiceImpl$ASCollection();
      return collections;
   }

   static final void register() {
      ActivationServiceImpl service = new ActivationServiceImpl();
      ApplicationRegistry.getApplicationRegistry().put(-1320069024724775836L, service);
      Proxy.getInstance().addGlobalEventListener(service);
   }

   static final void access$000(long x0, long x1) {
      ActivationService.activationComplete(x0, x1);
   }
}
