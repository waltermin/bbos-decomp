package net.rim.device.api.crypto.certificate.status;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.keystore.CertificateStatusManager;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.vm.WeakReference;

public final class CertificateStatusQuery {
   private CertificateStatusRequest _request;
   private Certificate[] _certChain;
   private boolean _checkEntireChain;
   private WeakReference _listenerWR;
   private QueryProgressListener _progressListener;
   private CertificateStatusQuery$StatusQueryThread _thread;
   private int _errorCode;
   private Vector _providerErrorMessages;
   private Object _lock;
   private Enumeration _providers;
   private Hashtable _providerContextData;
   private Vector _providerCertificates;
   private Certificate[] _responseCerts;
   private CertificateStatus[] _responseStatus;
   private boolean _returnedStatusObsolete;
   private CertificateStatus _overallStatus;
   private String _serviceUID;
   private final String[] ERROR_STRINGS;
   public static final int STATE_STARTED = 0;
   public static final int STATE_ENCODING = 1;
   public static final int STATE_PENDING = 2;
   public static final int STATE_SENDING = 3;
   public static final int STATE_SENT = 4;
   public static final int STATE_DECODING = 5;
   public static final int STATE_COMPLETE = 6;
   public static final int STATE_ERROR = 7;
   public static final int STATE_TIMEOUT = 8;
   public static final int STATE_ABORTED = 9;
   public static final int STATE_NO_SERVICE_BOOK = 10;
   private static final int PROXY_VERSION_ONE = 1;
   private static final int PROXY_CODE_SUCCESSFUL = 0;
   private static final int PROXY_CODE_INCORRECT_VERSION = 1;
   private static final int PROXY_CODE_MALFORMED_REQUEST = 2;
   private static final int PROXY_CODE_NO_PROVIDERS = 3;
   private static final int PROXY_CODE_PROVIDER_ERROR = 4;
   private static final int PROXY_CODE_INTERNAL_ERROR = 5;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-7644390350925054654L, "net.rim.device.internal.resource.crypto.StatusProviders");

   public CertificateStatusQuery(CertificateStatusRequest request, String serviceUID, CertificateStatusListener listener) {
      this.ERROR_STRINGS = new Object[]{
         _rb.getString(0),
         _rb.getString(1),
         _rb.getString(2),
         _rb.getString(3),
         _rb.getString(4),
         _rb.getString(5),
         _rb.getString(5),
         _rb.getString(6),
         _rb.getString(7),
         _rb.getString(8),
         _rb.getString(9),
         _rb.getString(10),
         _rb.getString(11),
         _rb.getString(48),
         _rb.getString(12),
         _rb.getString(65),
         _rb.getString(13)
      };
      this._request = request;
      this._certChain = request.getCertChain();
      this._checkEntireChain = request.checkEntireChain();
      this._listenerWR = (WeakReference)(listener != null ? new Object(listener) : null);
      this._errorCode = 0;
      this._providerErrorMessages = (Vector)(new Object());
      this._lock = new Object();
      this._providers = CertificateStatusProvider.getProviders();
      this._providerContextData = (Hashtable)(new Object());
      this._providerCertificates = (Vector)(new Object());
      this._serviceUID = serviceUID;
   }

   public final void setProgressListener(QueryProgressListener progressListener) {
      this._progressListener = progressListener;
   }

   public final int beginQuery() {
      CertificateStatusManager manager = CertificateStatusManager.getInstance();
      CertificateStatus status = manager.getStatus(this._certChain[0]);
      if (status != null && status.getStatus() == 1) {
         this._errorCode = 1;
         this._request.setResponse(null, null, null, this._errorCode, this.getErrorMessage(), this._providerErrorMessages);
         return 7;
      }

      CertificateStatusProvider[] compatibleProviders = new CertificateStatusProvider[0];

      while (this._providers.hasMoreElements()) {
         CertificateStatusProvider currentProvider = (CertificateStatusProvider)this._providers.nextElement();
         if (currentProvider.checkCompatibility(this._certChain, this._checkEntireChain)) {
            Arrays.add(compatibleProviders, currentProvider);
         }
      }

      if (compatibleProviders.length == 0) {
         this._errorCode = 2;
         this._request.setResponse(null, null, null, this._errorCode, this.getErrorMessage(), this._providerErrorMessages);
         return 7;
      } else if (ServiceRouting.getInstance().isServiceRoutable(this._serviceUID, -1)) {
         this._thread = new CertificateStatusQuery$StatusQueryThread(this, compatibleProviders);
         this._thread.start();
         return 0;
      } else {
         this._errorCode = RadioInfo.getActiveWAFs() == 0 && !ServiceRouting.getInstance().isSerialBypassActive() ? 4 : 17;
         this._request.setResponse(null, null, null, this._errorCode, this.getErrorMessage(), this._providerErrorMessages);
         return 7;
      }
   }

   public final void acceptAndNotifyListeners(boolean updateKeyStore) {
   }

   private final void applyResponseAndNotifyListener() {
      if (this._errorCode == 0) {
         CertificateStatusManagerTicket ticket = this._request.getCertificateStatusManagerTicket();
         this._returnedStatusObsolete = true;
         int size = this._responseCerts.length;
         if (this._checkEntireChain) {
            for (int i = 0; i < size; i++) {
               this._returnedStatusObsolete = this._returnedStatusObsolete
                  & !this.updateCertificateStatusManager(this._responseCerts[i], this._responseStatus[i], ticket);
            }
         } else {
            for (int i = 0; i < size; i++) {
               if (this._responseCerts[i].equals(this._certChain[0])) {
                  this._returnedStatusObsolete = this._returnedStatusObsolete
                     & !this.updateCertificateStatusManager(this._responseCerts[i], this._responseStatus[i], ticket);
                  break;
               }
            }
         }

         if (this._returnedStatusObsolete) {
            this._errorCode = 16;
         }
      }

      this._request
         .setResponse(this._responseCerts, this._responseStatus, this._overallStatus, this._errorCode, this.getErrorMessage(), this._providerErrorMessages);
      this.notifyCertificateStatusListener();
   }

   private final boolean updateCertificateStatusManager(Certificate param1, CertificateStatus param2, CertificateStatusManagerTicket param3) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/crypto/keystore/CertificateStatusManager.getInstance ()Lnet/rim/device/api/crypto/keystore/CertificateStatusManager;
      // 03: aload 1
      // 04: aload 2
      // 05: aload 3
      // 06: invokevirtual net/rim/device/api/crypto/keystore/CertificateStatusManager.setStatus (Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;)V
      // 09: bipush 1
      // 0a: ireturn
      // 0b: astore 4
      // 0d: bipush 1
      // 0e: ireturn
      // 0f: astore 4
      // 11: bipush 0
      // 12: ireturn
      // 13: astore 4
      // 15: bipush 0
      // 16: ireturn
      // try (0 -> 6): 7 null
      // try (0 -> 6): 10 null
      // try (0 -> 6): 13 null
   }

   private final void notifyCertificateStatusListener() {
      if (this._listenerWR != null) {
         CertificateStatusListener listener = (CertificateStatusListener)this._listenerWR.get();
         if (listener != null) {
            try {
               listener.receiveStatusResponse(this._request);
               return;
            } finally {
               return;
            }
         }
      }
   }

   final void copyResponseAndNotifyListener(CertificateStatusQuery queryToCopy) {
      Certificate[] responseCerts = queryToCopy._responseCerts;
      if (responseCerts == null) {
         this._responseCerts = null;
      } else {
         int numResponseCerts = responseCerts.length;
         this._responseCerts = new Object[numResponseCerts];

         for (int i = 0; i < numResponseCerts; i++) {
            this._responseCerts[i] = responseCerts[i];
         }
      }

      CertificateStatus[] responseStatus = queryToCopy._responseStatus;
      if (responseStatus == null) {
         this._responseStatus = null;
      } else {
         int numResponseStatus = responseStatus.length;
         this._responseStatus = new Object[numResponseStatus];

         for (int i = 0; i < numResponseStatus; i++) {
            this._responseStatus[i] = responseStatus[i];
         }
      }

      this._overallStatus = queryToCopy._overallStatus;
      this._errorCode = queryToCopy._errorCode;
      int numProviderErrorMessages = queryToCopy._providerErrorMessages.size();

      for (int i = 0; i < numProviderErrorMessages; i++) {
         this._providerErrorMessages.addElement(queryToCopy._providerErrorMessages.elementAt(i));
      }

      this._request
         .setResponse(this._responseCerts, this._responseStatus, this._overallStatus, this._errorCode, this.getErrorMessage(), this._providerErrorMessages);
      this.notifyCertificateStatusListener();
   }

   public final void terminateQuery() {
      synchronized (this._lock) {
         if (this._thread != null) {
            this._thread.terminate();
            this._thread = null;
         }
      }
   }

   public final boolean checkEntireChain() {
      return this._checkEntireChain;
   }

   public final Certificate[] getCertChain() {
      return this._certChain;
   }

   public final Certificate[] getResponseCerts() {
      return this._responseCerts;
   }

   public final CertificateStatus[] getResponseStatus() {
      return this._responseStatus;
   }

   public final CertificateStatus getOverallStatus() {
      return this._overallStatus;
   }

   public final String getErrorMessage() {
      return this.getErrorString(this._errorCode);
   }

   public final boolean returnedStatusObsolete() {
      return this._returnedStatusObsolete;
   }

   public final Enumeration getProviderErrorMessages() {
      return this._providerErrorMessages.elements();
   }

   public final CertificateStatusRequest getRequest() {
      return this._request;
   }

   private final String getErrorString(int errorCode) {
      if (errorCode == 0) {
         return null;
      } else {
         errorCode--;
         if (errorCode < this.ERROR_STRINGS.length && errorCode >= 0) {
            return this.ERROR_STRINGS[errorCode];
         } else {
            throw new Object();
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean encodeRequest(CertificateStatusProvider[] compatibleProviders, DataOutputStream out) {
      ProviderCompressionTable compressionTable = new ProviderCompressionTable();
      Vector requestData = (Vector)(new Object());
      CertificateStatusQuery$ProviderUiContextImpl uiContext = new CertificateStatusQuery$ProviderUiContextImpl(this, null);
      boolean success = false;

      for (CertificateStatusProvider provider : compatibleProviders) {
         ProviderDataSet dataSet = new ProviderDataSet(provider, null, this._providerCertificates);

         label126: {
            try {
               try {
                  uiContext.setErrorMessage(null);
                  provider.encodeRequest(this._certChain, this._checkEntireChain, dataSet, this._request.getKeyStore(), uiContext);
                  break label126;
               } catch (StatusProviderException var20) {
               }
            } catch (Throwable var21) {
               System.out.println(((StringBuffer)(new Object("Fatal provider error "))).append(t.toString()).toString());
               continue;
            }

            String errorMessage = uiContext.getErrorMessage();
            if (errorMessage != null && errorMessage.length() > 0) {
               this._providerErrorMessages.addElement(errorMessage);
            }
            continue;
         }

         ProviderDataSet compressedDataSet = new ProviderDataSet(null, compressionTable, this._providerCertificates);
         compressedDataSet.createFrom(dataSet);
         if (compressedDataSet.getContextObject() != null) {
            this._providerContextData.put(provider, compressedDataSet.getContextObject());
         }

         requestData.addElement(compressedDataSet);
         success = true;
      }

      if (success && compressionTable.size() != 0) {
         try {
            compressionTable.serialize(out);
            out.writeByte((byte)requestData.size());
            Enumeration enumeration = requestData.elements();

            while (enumeration.hasMoreElements()) {
               ProviderDataSet dataSet = (ProviderDataSet)enumeration.nextElement();
               dataSet.serialize(out);
            }
         } finally {
            return true;
         }

         return true;
      } else {
         return false;
      }
   }

   private final boolean decodeResponse(DataInputStream param1) throws ResponseParsingException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aconst_null
      // 002: putfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._overallStatus Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 005: new net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$ProviderUiContextImpl
      // 008: dup
      // 009: aload 0
      // 00a: aconst_null
      // 00b: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$ProviderUiContextImpl.<init> (Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery;Lnet/rim/device/api/crypto/certificate/status/CertificateStatusQuery$1;)V
      // 00e: astore 2
      // 00f: bipush 0
      // 010: istore 3
      // 011: new java/lang/Object
      // 014: dup
      // 015: invokespecial java/util/Hashtable.<init> ()V
      // 018: astore 4
      // 01a: new net/rim/device/api/crypto/certificate/status/ProviderCompressionTable
      // 01d: dup
      // 01e: invokespecial net/rim/device/api/crypto/certificate/status/ProviderCompressionTable.<init> ()V
      // 021: astore 5
      // 023: aload 5
      // 025: aload 1
      // 026: invokevirtual net/rim/device/api/crypto/certificate/status/ProviderCompressionTable.unSerialize (Ljava/io/DataInputStream;)V
      // 029: aload 1
      // 02a: invokevirtual java/io/DataInputStream.readByte ()B
      // 02d: sipush 255
      // 030: iand
      // 031: istore 6
      // 033: iload 6
      // 035: dup
      // 036: bipush 1
      // 037: isub
      // 038: istore 6
      // 03a: ifgt 040
      // 03d: goto 139
      // 040: new net/rim/device/api/crypto/certificate/status/ProviderDataSet
      // 043: dup
      // 044: aload 5
      // 046: aload 0
      // 047: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._providerCertificates Ljava/util/Vector;
      // 04a: invokespecial net/rim/device/api/crypto/certificate/status/ProviderDataSet.<init> (Lnet/rim/device/api/crypto/certificate/status/ProviderCompressionTable;Ljava/util/Vector;)V
      // 04d: astore 7
      // 04f: aload 7
      // 051: aload 1
      // 052: invokevirtual net/rim/device/api/crypto/certificate/status/ProviderDataSet.unSerialize (Ljava/io/DataInputStream;)V
      // 055: aload 7
      // 057: invokevirtual net/rim/device/api/crypto/certificate/status/ProviderDataSet.getProvider ()Lnet/rim/device/api/crypto/certificate/status/CertificateStatusProvider;
      // 05a: astore 8
      // 05c: aload 7
      // 05e: aload 0
      // 05f: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._providerContextData Ljava/util/Hashtable;
      // 062: aload 8
      // 064: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 067: invokevirtual net/rim/device/api/crypto/certificate/status/ProviderDataSet.setContextObject (Ljava/lang/Object;)V
      // 06a: aload 2
      // 06b: aconst_null
      // 06c: invokevirtual net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$ProviderUiContextImpl.setErrorMessage (Ljava/lang/String;)V
      // 06f: aload 8
      // 071: aload 0
      // 072: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._certChain [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 075: aload 0
      // 076: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._checkEntireChain Z
      // 079: aload 7
      // 07b: aload 0
      // 07c: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._request Lnet/rim/device/api/crypto/certificate/status/CertificateStatusRequest;
      // 07f: invokevirtual net/rim/device/api/crypto/certificate/status/CertificateStatusRequest.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 082: aload 2
      // 083: invokevirtual net/rim/device/api/crypto/certificate/status/CertificateStatusProvider.decodeResponse ([Lnet/rim/device/api/crypto/certificate/Certificate;ZLnet/rim/device/api/crypto/certificate/status/ProviderResponseData;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/certificate/status/ProviderUiContext;)V
      // 086: goto 0c7
      // 089: astore 9
      // 08b: aload 2
      // 08c: invokevirtual net/rim/device/api/crypto/certificate/status/CertificateStatusQuery$ProviderUiContextImpl.getErrorMessage ()Ljava/lang/String;
      // 08f: astore 10
      // 091: aload 10
      // 093: ifnull 033
      // 096: aload 10
      // 098: invokevirtual java/lang/String.length ()I
      // 09b: ifle 033
      // 09e: aload 0
      // 09f: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._providerErrorMessages Ljava/util/Vector;
      // 0a2: aload 10
      // 0a4: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0a7: goto 033
      // 0aa: astore 9
      // 0ac: getstatic net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0af: bipush 42
      // 0b1: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0b4: astore 10
      // 0b6: aload 0
      // 0b7: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._providerErrorMessages Ljava/util/Vector;
      // 0ba: aload 10
      // 0bc: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 0bf: goto 033
      // 0c2: astore 9
      // 0c4: goto 033
      // 0c7: aload 7
      // 0c9: invokevirtual net/rim/device/api/crypto/certificate/status/ProviderDataSet.getCertificates ()Ljava/util/Enumeration;
      // 0cc: astore 9
      // 0ce: aload 9
      // 0d0: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0d5: ifeq 12a
      // 0d8: aload 9
      // 0da: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0df: checkcast java/lang/Object
      // 0e2: astore 10
      // 0e4: aload 7
      // 0e6: aload 10
      // 0e8: invokevirtual net/rim/device/api/crypto/certificate/status/ProviderDataSet.getCertificateStatus (Lnet/rim/device/api/crypto/certificate/Certificate;)Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 0eb: astore 11
      // 0ed: aload 11
      // 0ef: ifnull 118
      // 0f2: aload 4
      // 0f4: aload 10
      // 0f6: aload 11
      // 0f8: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 0fb: pop
      // 0fc: aload 0
      // 0fd: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._overallStatus Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 100: ifnull 10f
      // 103: aload 11
      // 105: aload 0
      // 106: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._overallStatus Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 109: invokestatic net/rim/device/api/crypto/certificate/status/CertificateStatusUtilities.compareStatusCertificateChain (Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/certificate/CertificateStatus;)I
      // 10c: ifle 0ce
      // 10f: aload 0
      // 110: aload 11
      // 112: putfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._overallStatus Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 115: goto 0ce
      // 118: aload 4
      // 11a: aload 10
      // 11c: new java/lang/Object
      // 11f: dup
      // 120: invokespecial net/rim/device/api/crypto/certificate/CertificateStatus.<init> ()V
      // 123: invokevirtual java/util/Hashtable.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 126: pop
      // 127: goto 0ce
      // 12a: bipush 1
      // 12b: istore 3
      // 12c: goto 033
      // 12f: astore 5
      // 131: new net/rim/device/api/crypto/certificate/status/ResponseParsingException
      // 134: dup
      // 135: invokespecial net/rim/device/api/crypto/certificate/status/ResponseParsingException.<init> ()V
      // 138: athrow
      // 139: iload 3
      // 13a: ifne 13f
      // 13d: bipush 0
      // 13e: ireturn
      // 13f: aload 4
      // 141: invokevirtual java/util/Hashtable.size ()I
      // 144: istore 5
      // 146: aload 0
      // 147: iload 5
      // 149: anewarray 1940
      // 14c: putfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._responseCerts [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 14f: aload 0
      // 150: iload 5
      // 152: anewarray 1948
      // 155: putfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._responseStatus [Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 158: aload 4
      // 15a: invokevirtual java/util/Hashtable.keys ()Ljava/util/Enumeration;
      // 15d: astore 6
      // 15f: aload 4
      // 161: invokevirtual java/util/Hashtable.elements ()Ljava/util/Enumeration;
      // 164: astore 7
      // 166: bipush 0
      // 167: istore 8
      // 169: aload 6
      // 16b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 170: ifeq 19b
      // 173: aload 0
      // 174: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._responseCerts [Lnet/rim/device/api/crypto/certificate/Certificate;
      // 177: iload 8
      // 179: aload 6
      // 17b: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 180: checkcast java/lang/Object
      // 183: aastore
      // 184: aload 0
      // 185: getfield net/rim/device/api/crypto/certificate/status/CertificateStatusQuery._responseStatus [Lnet/rim/device/api/crypto/certificate/CertificateStatus;
      // 188: iload 8
      // 18a: aload 7
      // 18c: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 191: checkcast java/lang/Object
      // 194: aastore
      // 195: iinc 8 1
      // 198: goto 169
      // 19b: bipush 1
      // 19c: ireturn
      // try (53 -> 67): 68 net/rim/device/api/crypto/certificate/status/StatusProviderException
      // try (53 -> 67): 82 null
      // try (53 -> 67): 92 null
      // try (15 -> 138): 138 null
   }
}
