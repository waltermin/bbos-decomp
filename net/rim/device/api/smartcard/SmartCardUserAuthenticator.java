package net.rim.device.api.smartcard;

import java.util.Enumeration;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateChoiceDialog;
import net.rim.device.api.crypto.certificate.CertificateChoiceField;
import net.rim.device.api.crypto.certificate.CertificateKeyStoreIndex;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.AssociatedDataKeyStoreIndex;
import net.rim.device.api.crypto.keystore.CombinedKeyStore;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.UserAuthenticationException;
import net.rim.device.api.system.UserAuthenticator;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.internal.system.LockEventLogger;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.vm.Array;

public class SmartCardUserAuthenticator extends UserAuthenticator implements ReaderStatusListener, FieldProvider {
   private String _label;
   private SmartCard _smartCard;
   private SmartCardID _id;
   private KeyStoreData _authenticationKeyStoreData;
   private KeyStore _keyStore;
   private byte[] _authenticationCertificateHash;
   private int _maxAuthenticationAttempts;
   private int _remainingAuthenticationAttempts;
   private static final int ATTEMPTS_DEFAULT_VALUE = 2147483646;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(7215549882295292649L, "net.rim.device.internal.resource.SmartCard");

   public boolean isChallengeResponseEnabled() {
      return this._authenticationKeyStoreData != null || this._authenticationCertificateHash != null;
   }

   protected void populateCertField(
      CertificateChoiceField certField, KeyStoreData initialSelection, KeyStore keyStore, boolean allowNoneOption, boolean disableNoneIfCertsFound
   ) {
      int initialCertIndex = -1;
      int defaultCertIndex = -1;
      int numData = 0;
      String[] dataLabels = new String[numData];
      Certificate[] certificates = new Certificate[numData];
      if (allowNoneOption) {
         Array.resize(dataLabels, numData + 1);
         Array.resize(certificates, numData + 1);
         dataLabels[0] = _rb.getString(34);
         certificates[0] = null;
         numData++;
      }

      Certificate initialCert = null;
      if (initialSelection == null) {
         initialCertIndex = 0;
      } else {
         initialCert = initialSelection.getCertificate();
      }

      AssociatedDataKeyStoreIndex index = new AssociatedDataKeyStoreIndex(-4699629744920546763L);
      keyStore.addIndex(index);
      Enumeration enumeration = keyStore.elements(index.getID());

      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         Certificate certificate = data.getCertificate();
         if (certificate != null
            && data.isPrivateKeySet()
            && certificate.queryKeyUsage(1) != 0
            && this.isCertificateAllowed(data)
            && !Arrays.contains(certificates, certificate)) {
            Array.resize(dataLabels, numData + 1);
            Array.resize(certificates, numData + 1);
            dataLabels[numData] = data.getLabel();
            certificates[numData] = data.getCertificate();
            if (initialCertIndex < 0 && defaultCertIndex < 0 && initialCert != null && initialCert.equals(certificates[numData])) {
               initialCertIndex = numData;
            }

            numData++;
         }
      }

      if (initialCert != null && initialCertIndex == -1) {
         Array.resize(dataLabels, numData + 1);
         Array.resize(certificates, numData + 1);
         dataLabels[numData] = initialSelection.getLabel();
         certificates[numData] = initialCert;
         initialCertIndex = numData++;
      }

      if (allowNoneOption && disableNoneIfCertsFound && numData > 1) {
         numData--;
         System.arraycopy(dataLabels, 1, dataLabels, 0, dataLabels.length - 1);
         System.arraycopy(certificates, 1, certificates, 0, certificates.length - 1);
         Array.resize(dataLabels, numData);
         Array.resize(certificates, numData);
      }

      if (numData > 0) {
         boolean dirtyFlag = certField.isDirty();
         certField.setCertificates(certificates, dataLabels, initialCertIndex);
         certField.setDirty(dirtyFlag);
      }
   }

   protected SmartCard getSmartCard() {
      return this._smartCard;
   }

   public CertificateChoiceField getCertificateChoiceField(boolean allowNoneOption, boolean disableNoneIfCertsFound) {
      CertificateChoiceField certificateChoiceField = new CertificateChoiceField(_rb.getString(33), this.getKeyStore(), TrustedKeyStore.getInstance());
      this.populateCertField(certificateChoiceField, this._authenticationKeyStoreData, this.getKeyStore(), allowNoneOption, disableNoneIfCertsFound);
      return certificateChoiceField;
   }

   @Override
   public Field getField(Object context) {
      boolean allowNoneOption = !ITPolicy.getBoolean(24, 63, false);
      return this.getCertificateChoiceField(allowNoneOption, false);
   }

   @Override
   public void readerStatus(SmartCardReader reader, int status) {
      switch (status) {
         case 1:
            if (Security.getInstance().isPasswordEnabled() && this.lockWhenSmartcardRemoved() && Security.getInstance().getUserAuthenticator() != null) {
               LockEventLogger.logLockEvent(1282630514);
               ApplicationManager.getApplicationManager().lockSystem(true);
            }

            reader.removeListener(this);
      }
   }

   @Override
   public int getOrder(Object context) {
      return 0;
   }

   @Override
   public boolean grabDataFromField(Field param1, Object param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: dup
      // 02: instanceof net/rim/device/api/crypto/certificate/CertificateChoiceField
      // 05: ifne 0c
      // 08: pop
      // 09: goto cb
      // 0c: checkcast net/rim/device/api/crypto/certificate/CertificateChoiceField
      // 0f: astore 3
      // 10: aload 3
      // 11: invokevirtual net/rim/device/api/crypto/certificate/CertificateChoiceField.getSelectedCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 14: astore 4
      // 16: aload 4
      // 18: ifnonnull 1e
      // 1b: goto b7
      // 1e: aload 0
      // 1f: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 22: new net/rim/device/api/crypto/certificate/CertificateKeyStoreIndex
      // 25: dup
      // 26: invokespecial net/rim/device/api/crypto/certificate/CertificateKeyStoreIndex.<init> ()V
      // 29: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 2e: pop
      // 2f: aload 0
      // 30: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 33: ldc2_w -2038609988711824737
      // 36: aload 4
      // 38: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 3d: astore 5
      // 3f: aload 5
      // 41: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 46: ifeq b5
      // 49: aload 5
      // 4b: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 50: checkcast net/rim/device/api/crypto/keystore/KeyStoreData
      // 53: astore 6
      // 55: aload 0
      // 56: invokevirtual net/rim/device/api/smartcard/SmartCardUserAuthenticator.isInitialized ()Z
      // 59: ifeq a0
      // 5c: aload 0
      // 5d: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 60: astore 7
      // 62: aload 0
      // 63: aload 6
      // 65: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 68: bipush 0
      // 69: istore 8
      // 6b: aload 0
      // 6c: aconst_null
      // 6d: bipush 1
      // 6e: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 71: bipush 35
      // 73: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 76: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.authenticate (Ljava/lang/String;ZLjava/lang/String;)Z
      // 79: istore 8
      // 7b: goto 85
      // 7e: astore 9
      // 80: goto 85
      // 83: astore 9
      // 85: iload 8
      // 87: ifne a6
      // 8a: aload 0
      // 8b: aload 7
      // 8d: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 90: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 93: bipush 36
      // 95: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 98: ldc_w -2147483644
      // 9b: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 9e: bipush 0
      // 9f: ireturn
      // a0: aload 0
      // a1: aload 6
      // a3: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // a6: aload 0
      // a7: invokevirtual net/rim/device/api/smartcard/SmartCardUserAuthenticator.isInitialized ()Z
      // aa: ifeq b3
      // ad: invokestatic net/rim/device/internal/system/Security.getInstance ()Lnet/rim/device/internal/system/Security;
      // b0: invokevirtual net/rim/device/internal/system/Security.reinitializeUserAuthenticatorStateData ()V
      // b3: bipush 1
      // b4: ireturn
      // b5: bipush 0
      // b6: ireturn
      // b7: aload 0
      // b8: aconst_null
      // b9: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // bc: aload 0
      // bd: invokevirtual net/rim/device/api/smartcard/SmartCardUserAuthenticator.isInitialized ()Z
      // c0: ifeq c9
      // c3: invokestatic net/rim/device/internal/system/Security.getInstance ()Lnet/rim/device/internal/system/Security;
      // c6: invokevirtual net/rim/device/internal/system/Security.reinitializeUserAuthenticatorStateData ()V
      // c9: bipush 1
      // ca: ireturn
      // cb: bipush 0
      // cc: ireturn
      // try (45 -> 53): 54 null
      // try (45 -> 53): 56 null
   }

   @Override
   public boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public boolean initialize(String param1) throws UserAuthenticationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ifnonnull 00c
      // 004: new java/lang/IllegalArgumentException
      // 007: dup
      // 008: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 00b: athrow
      // 00c: aconst_null
      // 00d: astore 2
      // 00e: aconst_null
      // 00f: astore 3
      // 010: aload 0
      // 011: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._smartCard Lnet/rim/device/api/smartcard/SmartCard;
      // 014: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCardSessionForced (Lnet/rim/device/api/smartcard/SmartCard;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 017: astore 2
      // 018: aload 0
      // 019: aload 2
      // 01a: aload 1
      // 01b: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.login (Lnet/rim/device/api/smartcard/SmartCardSession;Ljava/lang/String;)Z
      // 01e: ifne 042
      // 021: bipush 0
      // 022: istore 4
      // 024: aload 2
      // 025: ifnull 02c
      // 028: aload 2
      // 029: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 02c: aload 3
      // 02d: ifnull 03f
      // 030: aload 3
      // 031: ldc_w -2147483644
      // 034: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 037: new net/rim/device/api/system/UserAuthenticationException
      // 03a: dup
      // 03b: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 03e: athrow
      // 03f: iload 4
      // 041: ireturn
      // 042: aload 0
      // 043: aload 2
      // 044: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardID ()Lnet/rim/device/api/smartcard/SmartCardID;
      // 047: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._id Lnet/rim/device/api/smartcard/SmartCardID;
      // 04a: aload 2
      // 04b: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 04e: aload 0
      // 04f: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._id Lnet/rim/device/api/smartcard/SmartCardID;
      // 052: ifnonnull 06b
      // 055: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 058: bipush 21
      // 05a: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 05d: ldc_w -2147483644
      // 060: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 063: new net/rim/device/api/system/UserAuthenticationException
      // 066: dup
      // 067: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 06a: athrow
      // 06b: bipush 1
      // 06c: istore 4
      // 06e: aload 2
      // 06f: ifnull 076
      // 072: aload 2
      // 073: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 076: aload 3
      // 077: ifnull 089
      // 07a: aload 3
      // 07b: ldc_w -2147483644
      // 07e: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 081: new net/rim/device/api/system/UserAuthenticationException
      // 084: dup
      // 085: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 088: athrow
      // 089: iload 4
      // 08b: ireturn
      // 08c: astore 4
      // 08e: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 091: bipush 14
      // 093: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 096: astore 3
      // 097: aload 2
      // 098: ifnull 09f
      // 09b: aload 2
      // 09c: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 09f: aload 3
      // 0a0: ifnonnull 0a6
      // 0a3: goto 132
      // 0a6: aload 3
      // 0a7: ldc_w -2147483644
      // 0aa: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0ad: new net/rim/device/api/system/UserAuthenticationException
      // 0b0: dup
      // 0b1: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0b4: athrow
      // 0b5: astore 4
      // 0b7: bipush 0
      // 0b8: istore 5
      // 0ba: aload 2
      // 0bb: ifnull 0c2
      // 0be: aload 2
      // 0bf: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0c2: aload 3
      // 0c3: ifnull 0d5
      // 0c6: aload 3
      // 0c7: ldc_w -2147483644
      // 0ca: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0cd: new net/rim/device/api/system/UserAuthenticationException
      // 0d0: dup
      // 0d1: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0d4: athrow
      // 0d5: iload 5
      // 0d7: ireturn
      // 0d8: astore 4
      // 0da: new net/rim/device/api/system/UserAuthenticationException
      // 0dd: dup
      // 0de: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0e1: athrow
      // 0e2: astore 4
      // 0e4: new net/rim/device/api/system/UserAuthenticationException
      // 0e7: dup
      // 0e8: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0eb: athrow
      // 0ec: astore 4
      // 0ee: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0f1: bipush 21
      // 0f3: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0f6: astore 3
      // 0f7: aload 2
      // 0f8: ifnull 0ff
      // 0fb: aload 2
      // 0fc: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0ff: aload 3
      // 100: ifnull 132
      // 103: aload 3
      // 104: ldc_w -2147483644
      // 107: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 10a: new net/rim/device/api/system/UserAuthenticationException
      // 10d: dup
      // 10e: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 111: athrow
      // 112: astore 6
      // 114: aload 2
      // 115: ifnull 11c
      // 118: aload 2
      // 119: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 11c: aload 3
      // 11d: ifnull 12f
      // 120: aload 3
      // 121: ldc_w -2147483644
      // 124: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 127: new net/rim/device/api/system/UserAuthenticationException
      // 12a: dup
      // 12b: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 12e: athrow
      // 12f: aload 6
      // 131: athrow
      // 132: bipush 0
      // 133: ireturn
      // try (10 -> 21): 71 null
      // try (36 -> 56): 71 null
      // try (10 -> 21): 90 null
      // try (36 -> 56): 90 null
      // try (10 -> 21): 108 null
      // try (36 -> 56): 108 null
      // try (10 -> 21): 113 null
      // try (36 -> 56): 113 null
      // try (10 -> 21): 118 null
      // try (36 -> 56): 118 null
      // try (10 -> 21): 136 null
      // try (36 -> 56): 136 null
      // try (71 -> 76): 136 null
      // try (90 -> 93): 136 null
      // try (108 -> 123): 136 null
      // try (136 -> 137): 136 null
   }

   @Override
   public void uninitialize() {
      this._id = null;
   }

   @Override
   public boolean isInitialized() {
      return this._id != null;
   }

   @Override
   public boolean authenticate(String password) {
      return this.authenticate(password, false, null);
   }

   private boolean authenticate(String param1, boolean param2, String param3) throws UserAuthenticationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokevirtual net/rim/device/api/smartcard/SmartCardUserAuthenticator.isInitialized ()Z
      // 004: ifne 012
      // 007: new java/lang/RuntimeException
      // 00a: dup
      // 00b: ldc_w "SmartCardUserAuthenticator must be initialized before calling authenticate()"
      // 00e: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 011: athrow
      // 012: aconst_null
      // 013: astore 4
      // 015: aconst_null
      // 016: astore 5
      // 018: aload 0
      // 019: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._id Lnet/rim/device/api/smartcard/SmartCardID;
      // 01c: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCardSessionForced (Lnet/rim/device/api/smartcard/SmartCardID;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 01f: astore 4
      // 021: aload 1
      // 022: ifnonnull 032
      // 025: iload 2
      // 026: ifeq 032
      // 029: aload 4
      // 02b: aload 3
      // 02c: bipush 3
      // 02e: bipush 1
      // 02f: invokevirtual net/rim/device/api/smartcard/SmartCardSession.loginPrompt (Ljava/lang/String;IZ)V
      // 032: aload 0
      // 033: aload 4
      // 035: aload 1
      // 036: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.login (Lnet/rim/device/api/smartcard/SmartCardSession;Ljava/lang/String;)Z
      // 039: istore 6
      // 03b: aload 4
      // 03d: ifnull 045
      // 040: aload 4
      // 042: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 045: aload 5
      // 047: ifnull 05a
      // 04a: aload 5
      // 04c: ldc_w -2147483644
      // 04f: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 052: new net/rim/device/api/system/UserAuthenticationException
      // 055: dup
      // 056: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 059: athrow
      // 05a: iload 6
      // 05c: ireturn
      // 05d: astore 6
      // 05f: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 062: bipush 14
      // 064: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 067: astore 5
      // 069: aload 4
      // 06b: ifnull 073
      // 06e: aload 4
      // 070: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 073: aload 5
      // 075: ifnonnull 07b
      // 078: goto 140
      // 07b: aload 5
      // 07d: ldc_w -2147483644
      // 080: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 083: new net/rim/device/api/system/UserAuthenticationException
      // 086: dup
      // 087: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 08a: athrow
      // 08b: astore 6
      // 08d: bipush 0
      // 08e: istore 7
      // 090: aload 4
      // 092: ifnull 09a
      // 095: aload 4
      // 097: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 09a: aload 5
      // 09c: ifnull 0af
      // 09f: aload 5
      // 0a1: ldc_w -2147483644
      // 0a4: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0a7: new net/rim/device/api/system/UserAuthenticationException
      // 0aa: dup
      // 0ab: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0ae: athrow
      // 0af: iload 7
      // 0b1: ireturn
      // 0b2: astore 6
      // 0b4: new net/rim/device/api/system/UserAuthenticationException
      // 0b7: dup
      // 0b8: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0bb: athrow
      // 0bc: astore 6
      // 0be: new net/rim/device/api/system/UserAuthenticationException
      // 0c1: dup
      // 0c2: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0c5: athrow
      // 0c6: astore 6
      // 0c8: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0cb: bipush 22
      // 0cd: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0d0: astore 5
      // 0d2: aload 4
      // 0d4: ifnull 0dc
      // 0d7: aload 4
      // 0d9: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 0dc: aload 5
      // 0de: ifnull 140
      // 0e1: aload 5
      // 0e3: ldc_w -2147483644
      // 0e6: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 0e9: new net/rim/device/api/system/UserAuthenticationException
      // 0ec: dup
      // 0ed: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 0f0: athrow
      // 0f1: astore 6
      // 0f3: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 0f6: bipush 21
      // 0f8: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0fb: astore 5
      // 0fd: aload 4
      // 0ff: ifnull 107
      // 102: aload 4
      // 104: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 107: aload 5
      // 109: ifnull 140
      // 10c: aload 5
      // 10e: ldc_w -2147483644
      // 111: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 114: new net/rim/device/api/system/UserAuthenticationException
      // 117: dup
      // 118: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 11b: athrow
      // 11c: astore 8
      // 11e: aload 4
      // 120: ifnull 128
      // 123: aload 4
      // 125: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 128: aload 5
      // 12a: ifnull 13d
      // 12d: aload 5
      // 12f: ldc_w -2147483644
      // 132: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 135: new net/rim/device/api/system/UserAuthenticationException
      // 138: dup
      // 139: invokespecial net/rim/device/api/system/UserAuthenticationException.<init> ()V
      // 13c: athrow
      // 13d: aload 8
      // 13f: athrow
      // 140: bipush 0
      // 141: ireturn
      // try (12 -> 30): 45 null
      // try (12 -> 30): 64 null
      // try (12 -> 30): 82 null
      // try (12 -> 30): 87 null
      // try (12 -> 30): 92 null
      // try (12 -> 30): 110 null
      // try (12 -> 30): 128 null
      // try (45 -> 50): 128 null
      // try (64 -> 67): 128 null
      // try (82 -> 97): 128 null
      // try (110 -> 115): 128 null
      // try (128 -> 129): 128 null
   }

   private boolean lockWhenSmartcardRemoved() {
      return SmartCardOptions.getInstance().getAllowLockOnCardRemoval();
   }

   @Override
   public byte[] getStateData() {
      if (!this.isInitialized()) {
         throw new RuntimeException("SmartCardUserAuthenticator must be initialized before calling getStateData()");
      }

      try {
         DataBuffer db = new DataBuffer();
         db.writeByte(this._authenticationKeyStoreData == null ? 0 : 1);
         db.writeUTF(this._label);
         db.writeUTF(this._smartCard.getClass().getName());
         db.writeLong(this._id.getID());
         db.writeUTF(this._id.getLabel());
         if (this._authenticationKeyStoreData != null) {
            Certificate cert = this._authenticationKeyStoreData.getCertificate();
            SHA1Digest digest = new SHA1Digest();
            digest.update(cert.getEncoding());
            byte[] hash = digest.getDigest();
            db.writeInt(hash.length);
            db.write(hash);
         }

         return db.toArray();
      } finally {
         ;
      }
   }

   @Override
   public int getMaxAuthenticationAttempts() {
      return this._maxAuthenticationAttempts;
   }

   @Override
   public int getRemainingAuthenticationAttempts() {
      return this._remainingAuthenticationAttempts;
   }

   @Override
   public boolean isInitializationPossible() {
      return SmartCardReaderFactory.getNumSmartCardReaders() > 0;
   }

   @Override
   public boolean isReadyForInitialization() {
      if (this.isInitializationPossible() && this._smartCard != null) {
         SmartCard smartCardInserted = SmartCardFactory.getSmartCard();
         return smartCardInserted != null && smartCardInserted.equals(this._smartCard);
      } else {
         return false;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof SmartCardUserAuthenticator)) {
         return false;
      }

      SmartCardUserAuthenticator other = (SmartCardUserAuthenticator)obj;
      return ObjectUtilities.classesEqual(this._smartCard, other._smartCard);
   }

   public SmartCardUserAuthenticator() {
      this(null, null);
   }

   @Override
   public String getName() {
      return this._label;
   }

   @Override
   public boolean isConfigured() {
      return !ITPolicy.getBoolean(24, 63, false) || this._authenticationKeyStoreData != null || this._authenticationCertificateHash != null;
   }

   @Override
   public void configure() {
      if (!this.isConfigured()) {
         SmartCardUserAuthenticator$ConfigureWorkerThread workerThread;
         do {
            CertificateChoiceField field = this.getCertificateChoiceField(true, true);
            CertificateChoiceDialog dialog = new CertificateChoiceDialog(_rb.getString(37), field, false, true, this.getKeyStore(), 0);
            dialog.setCancelAllowed(false);
            dialog.show();
            workerThread = new SmartCardUserAuthenticator$ConfigureWorkerThread(this, field, dialog.getCloseReason());
            PleaseWaitDialog pleaseWait = new PleaseWaitDialog(workerThread);
            pleaseWait.display();
         } while (!workerThread.getConfigureComplete());
      }
   }

   private boolean importCertificatesFromSmartCard(SmartCardSession param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aconst_null
      // 001: astore 2
      // 002: aload 1
      // 003: ifnonnull 00a
      // 006: bipush 1
      // 007: goto 00b
      // 00a: bipush 0
      // 00b: istore 3
      // 00c: aload 1
      // 00d: ifnonnull 018
      // 010: aload 0
      // 011: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._id Lnet/rim/device/api/smartcard/SmartCardID;
      // 014: invokestatic net/rim/device/api/smartcard/SmartCardFactory.getSmartCardSessionForced (Lnet/rim/device/api/smartcard/SmartCardID;)Lnet/rim/device/api/smartcard/SmartCardSession;
      // 017: astore 1
      // 018: aload 1
      // 019: dup
      // 01a: instanceof net/rim/device/api/crypto/CryptoSmartCardSession
      // 01d: ifne 024
      // 020: pop
      // 021: goto 116
      // 024: checkcast net/rim/device/api/crypto/CryptoSmartCardSession
      // 027: astore 4
      // 029: aload 4
      // 02b: invokevirtual net/rim/device/api/crypto/CryptoSmartCardSession.getKeyStoreDataArray ()[Lnet/rim/device/api/crypto/CryptoSmartCardKeyStoreData;
      // 02e: astore 5
      // 030: aload 5
      // 032: ifnonnull 052
      // 035: bipush 1
      // 036: istore 6
      // 038: aload 1
      // 039: ifnull 044
      // 03c: iload 3
      // 03d: ifeq 044
      // 040: aload 1
      // 041: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 044: aload 2
      // 045: ifnull 04f
      // 048: aload 2
      // 049: ldc_w -2147483644
      // 04c: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 04f: iload 6
      // 051: ireturn
      // 052: aload 5
      // 054: arraylength
      // 055: istore 6
      // 057: aload 0
      // 058: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 05b: astore 7
      // 05d: bipush 0
      // 05e: istore 8
      // 060: iload 8
      // 062: iload 6
      // 064: if_icmplt 06a
      // 067: goto 0f7
      // 06a: aload 5
      // 06c: iload 8
      // 06e: aaload
      // 06f: astore 9
      // 071: aload 9
      // 073: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 078: ifne 07e
      // 07b: goto 0f1
      // 07e: aload 9
      // 080: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate; 1
      // 085: astore 10
      // 087: aload 10
      // 089: ifnull 0f1
      // 08c: bipush 0
      // 08d: istore 11
      // 08f: aload 7
      // 091: ldc2_w -2038609988711824737
      // 094: aload 10
      // 096: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 09b: astore 12
      // 09d: aload 12
      // 09f: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0a4: ifeq 0c5
      // 0a7: aload 12
      // 0a9: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0ae: checkcast net/rim/device/api/crypto/keystore/KeyStoreData
      // 0b1: astore 13
      // 0b3: aload 13
      // 0b5: ifnull 09d
      // 0b8: aload 13
      // 0ba: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 0bf: ifeq 09d
      // 0c2: bipush 1
      // 0c3: istore 11
      // 0c5: iload 11
      // 0c7: ifne 0f1
      // 0ca: aload 7
      // 0cc: aload 10
      // 0ce: invokestatic net/rim/device/api/crypto/CryptoSmartCardUtilities.getSmartCardAssociatedData (Lnet/rim/device/api/crypto/certificate/Certificate;)[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 0d1: aload 9
      // 0d3: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 0d8: aload 9
      // 0da: aconst_null
      // 0db: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 0e0: aconst_null
      // 0e1: bipush 1
      // 0e2: aload 10
      // 0e4: aconst_null
      // 0e5: aconst_null
      // 0e6: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;ILnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 9
      // 0eb: pop
      // 0ec: goto 0f1
      // 0ef: astore 13
      // 0f1: iinc 8 1
      // 0f4: goto 060
      // 0f7: bipush 1
      // 0f8: istore 8
      // 0fa: aload 1
      // 0fb: ifnull 106
      // 0fe: iload 3
      // 0ff: ifeq 106
      // 102: aload 1
      // 103: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 106: aload 2
      // 107: ifnull 111
      // 10a: aload 2
      // 10b: ldc_w -2147483644
      // 10e: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 111: iload 8
      // 113: ireturn
      // 114: astore 5
      // 116: aload 1
      // 117: ifnull 122
      // 11a: iload 3
      // 11b: ifeq 122
      // 11e: aload 1
      // 11f: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 122: aload 2
      // 123: ifnonnull 129
      // 126: goto 21d
      // 129: aload 2
      // 12a: ldc_w -2147483644
      // 12d: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 130: bipush 0
      // 131: ireturn
      // 132: astore 4
      // 134: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 137: bipush 14
      // 139: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 13c: astore 2
      // 13d: aload 1
      // 13e: ifnull 149
      // 141: iload 3
      // 142: ifeq 149
      // 145: aload 1
      // 146: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 149: aload 2
      // 14a: ifnonnull 150
      // 14d: goto 21d
      // 150: aload 2
      // 151: ldc_w -2147483644
      // 154: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 157: bipush 0
      // 158: ireturn
      // 159: astore 4
      // 15b: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 15e: bipush 13
      // 160: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 163: astore 2
      // 164: aload 1
      // 165: ifnull 170
      // 168: iload 3
      // 169: ifeq 170
      // 16c: aload 1
      // 16d: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 170: aload 2
      // 171: ifnonnull 177
      // 174: goto 21d
      // 177: aload 2
      // 178: ldc_w -2147483644
      // 17b: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 17e: bipush 0
      // 17f: ireturn
      // 180: astore 4
      // 182: aload 1
      // 183: ifnull 18e
      // 186: iload 3
      // 187: ifeq 18e
      // 18a: aload 1
      // 18b: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 18e: aload 2
      // 18f: ifnonnull 195
      // 192: goto 21d
      // 195: aload 2
      // 196: ldc_w -2147483644
      // 199: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 19c: bipush 0
      // 19d: ireturn
      // 19e: astore 4
      // 1a0: aload 1
      // 1a1: ifnull 1ac
      // 1a4: iload 3
      // 1a5: ifeq 1ac
      // 1a8: aload 1
      // 1a9: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 1ac: aload 2
      // 1ad: ifnull 21d
      // 1b0: aload 2
      // 1b1: ldc_w -2147483644
      // 1b4: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 1b7: bipush 0
      // 1b8: ireturn
      // 1b9: astore 4
      // 1bb: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1be: bipush 22
      // 1c0: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1c3: astore 2
      // 1c4: aload 1
      // 1c5: ifnull 1d0
      // 1c8: iload 3
      // 1c9: ifeq 1d0
      // 1cc: aload 1
      // 1cd: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 1d0: aload 2
      // 1d1: ifnull 21d
      // 1d4: aload 2
      // 1d5: ldc_w -2147483644
      // 1d8: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 1db: bipush 0
      // 1dc: ireturn
      // 1dd: astore 4
      // 1df: getstatic net/rim/device/api/smartcard/SmartCardUserAuthenticator._rb Lnet/rim/device/api/i18n/ResourceBundle;
      // 1e2: bipush 21
      // 1e4: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 1e7: astore 2
      // 1e8: aload 1
      // 1e9: ifnull 1f4
      // 1ec: iload 3
      // 1ed: ifeq 1f4
      // 1f0: aload 1
      // 1f1: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 1f4: aload 2
      // 1f5: ifnull 21d
      // 1f8: aload 2
      // 1f9: ldc_w -2147483644
      // 1fc: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 1ff: bipush 0
      // 200: ireturn
      // 201: astore 14
      // 203: aload 1
      // 204: ifnull 20f
      // 207: iload 3
      // 208: ifeq 20f
      // 20b: aload 1
      // 20c: invokevirtual net/rim/device/api/smartcard/SmartCardSession.close ()V
      // 20f: aload 2
      // 210: ifnull 21a
      // 213: aload 2
      // 214: ldc_w -2147483644
      // 217: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;I)V
      // 21a: aload 14
      // 21c: athrow
      // 21d: bipush 0
      // 21e: ireturn
      // try (90 -> 105): 106 null
      // try (22 -> 29): 124 null
      // try (42 -> 111): 124 null
      // try (8 -> 29): 139 null
      // try (42 -> 111): 139 null
      // try (124 -> 125): 139 null
      // try (8 -> 29): 158 null
      // try (42 -> 111): 158 null
      // try (124 -> 125): 158 null
      // try (8 -> 29): 177 null
      // try (42 -> 111): 177 null
      // try (124 -> 125): 177 null
      // try (8 -> 29): 192 null
      // try (42 -> 111): 192 null
      // try (124 -> 125): 192 null
      // try (8 -> 29): 206 null
      // try (42 -> 111): 206 null
      // try (124 -> 125): 206 null
      // try (8 -> 29): 224 null
      // try (42 -> 111): 224 null
      // try (124 -> 125): 224 null
      // try (8 -> 29): 242 null
      // try (42 -> 111): 242 null
      // try (124 -> 125): 242 null
      // try (139 -> 144): 242 null
      // try (158 -> 163): 242 null
      // try (177 -> 178): 242 null
      // try (192 -> 193): 242 null
      // try (206 -> 211): 242 null
      // try (224 -> 229): 242 null
      // try (242 -> 243): 242 null
   }

   @Override
   public boolean setStateData(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 06
      // 04: bipush 0
      // 05: ireturn
      // 06: new net/rim/device/api/util/DataBuffer
      // 09: dup
      // 0a: aload 1
      // 0b: bipush 0
      // 0c: aload 1
      // 0d: arraylength
      // 0e: bipush 1
      // 0f: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 12: astore 2
      // 13: aload 2
      // 14: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedByte ()I
      // 17: istore 3
      // 18: iload 3
      // 19: bipush 1
      // 1a: if_icmple 1f
      // 1d: bipush 0
      // 1e: ireturn
      // 1f: aload 0
      // 20: aload 2
      // 21: invokevirtual net/rim/device/api/util/DataBuffer.readUTF ()Ljava/lang/String;
      // 24: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._label Ljava/lang/String;
      // 27: aload 0
      // 28: aload 2
      // 29: invokevirtual net/rim/device/api/util/DataBuffer.readUTF ()Ljava/lang/String;
      // 2c: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 2f: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 32: checkcast net/rim/device/api/smartcard/SmartCard
      // 35: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._smartCard Lnet/rim/device/api/smartcard/SmartCard;
      // 38: aload 2
      // 39: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 3c: ifeq 45
      // 3f: aload 0
      // 40: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.reset ()V
      // 43: bipush 0
      // 44: ireturn
      // 45: aload 2
      // 46: invokevirtual net/rim/device/api/util/DataBuffer.readLong ()J
      // 49: lstore 4
      // 4b: aload 2
      // 4c: invokevirtual net/rim/device/api/util/DataBuffer.readUTF ()Ljava/lang/String;
      // 4f: astore 6
      // 51: aload 0
      // 52: new net/rim/device/api/smartcard/SmartCardID
      // 55: dup
      // 56: lload 4
      // 58: aload 6
      // 5a: aload 0
      // 5b: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._smartCard Lnet/rim/device/api/smartcard/SmartCard;
      // 5e: invokespecial net/rim/device/api/smartcard/SmartCardID.<init> (JLjava/lang/String;Lnet/rim/device/api/smartcard/SmartCard;)V
      // 61: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._id Lnet/rim/device/api/smartcard/SmartCardID;
      // 64: aload 2
      // 65: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 68: ifne a4
      // 6b: iload 3
      // 6c: ifne 75
      // 6f: aload 0
      // 70: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.reset ()V
      // 73: bipush 0
      // 74: ireturn
      // 75: iload 3
      // 76: bipush 1
      // 77: if_icmpne 9e
      // 7a: aload 2
      // 7b: invokevirtual net/rim/device/api/util/DataBuffer.readInt ()I
      // 7e: istore 7
      // 80: aload 0
      // 81: iload 7
      // 83: newarray 8
      // 85: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationCertificateHash [B
      // 88: aload 2
      // 89: aload 0
      // 8a: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationCertificateHash [B
      // 8d: invokevirtual net/rim/device/api/util/DataBuffer.read ([B)I
      // 90: pop
      // 91: aload 0
      // 92: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationCertificateHash [B
      // 95: ifnull 9c
      // 98: bipush 1
      // 99: goto 9d
      // 9c: bipush 0
      // 9d: ireturn
      // 9e: aload 0
      // 9f: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.reset ()V
      // a2: bipush 0
      // a3: ireturn
      // a4: bipush 1
      // a5: ireturn
      // a6: astore 2
      // a7: goto b7
      // aa: astore 2
      // ab: goto b7
      // ae: astore 2
      // af: goto b7
      // b2: astore 2
      // b3: goto b7
      // b6: astore 2
      // b7: aload 0
      // b8: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.reset ()V
      // bb: bipush 0
      // bc: ireturn
      // try (0 -> 3): 91 null
      // try (4 -> 20): 91 null
      // try (21 -> 38): 91 null
      // try (39 -> 62): 91 null
      // try (63 -> 84): 91 null
      // try (85 -> 88): 91 null
      // try (89 -> 90): 91 null
      // try (0 -> 3): 93 null
      // try (4 -> 20): 93 null
      // try (21 -> 38): 93 null
      // try (39 -> 62): 93 null
      // try (63 -> 84): 93 null
      // try (85 -> 88): 93 null
      // try (89 -> 90): 93 null
      // try (0 -> 3): 95 null
      // try (4 -> 20): 95 null
      // try (21 -> 38): 95 null
      // try (39 -> 62): 95 null
      // try (63 -> 84): 95 null
      // try (85 -> 88): 95 null
      // try (89 -> 90): 95 null
      // try (0 -> 3): 97 null
      // try (4 -> 20): 97 null
      // try (21 -> 38): 97 null
      // try (39 -> 62): 97 null
      // try (63 -> 84): 97 null
      // try (85 -> 88): 97 null
      // try (89 -> 90): 97 null
      // try (0 -> 3): 99 null
      // try (4 -> 20): 99 null
      // try (21 -> 38): 99 null
      // try (39 -> 62): 99 null
      // try (63 -> 84): 99 null
      // try (85 -> 88): 99 null
      // try (89 -> 90): 99 null
   }

   private void reset() {
      this._label = null;
      this._smartCard = null;
      this._id = null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private KeyStore getKeyStore() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         if (this._keyStore == null) {
            KeyStore[] keyStoresToCombine = new KeyStore[]{
               new SmartCardUserAuthenticator$SmartCardUserAuthenticatorKeyCache(null), DeviceKeyStore.getInstance()
            };
            this._keyStore = new CombinedKeyStore(keyStoresToCombine, 0);
            this._keyStore.addIndex(new CertificateKeyStoreIndex());
            var3 = false;
         } else {
            var3 = false;
         }
      } finally {
         if (var3) {
            throw new RuntimeException("Unable to register SmartCardUserAuthenticatorKeyCache");
         }
      }

      return this._keyStore;
   }

   private boolean isCertificateAllowed(KeyStoreData keyStoreData) {
      if (keyStoreData == null) {
         return false;
      }

      Certificate certificate = keyStoreData.getCertificate();
      if (certificate == null) {
         return false;
      }

      long certificateProperties = this.getCertificateProperties(certificate);
      return this.isCertificateAllowed(certificate, certificateProperties);
   }

   private long getCertificateProperties(Certificate certificate) {
      Certificate[][] certificateChains = CertificateUtilities.buildCertificateChains(certificate, this.getKeyStore());
      long[] certificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
         certificateChains, TrustedKeyStore.getInstance(), System.currentTimeMillis()
      );
      return CertificateChainProperties.selectBestCertificateChainProperties(certificateChainProperties);
   }

   private boolean isCertificateAllowed(Certificate certificate, long certificateProperties) {
      long forbiddenProperties = 0;
      if (ITPolicy.getBoolean(24, 23, false)) {
         forbiddenProperties |= 32;
      }

      if (ITPolicy.getBoolean(24, 22, false)) {
         forbiddenProperties |= 256;
      }

      if (ITPolicy.getBoolean(24, 4, false)) {
         forbiddenProperties |= 1024;
      }

      if (ITPolicy.getBoolean(24, 3, false)) {
         forbiddenProperties |= 8;
      }

      if (ITPolicy.getBoolean(24, 48, false)) {
         forbiddenProperties |= 22;
      }

      return (certificateProperties & forbiddenProperties) == 0;
   }

   @Override
   public Object getInformation(long id, Object parameter, Object defaultValue) {
      return defaultValue;
   }

   public SmartCardUserAuthenticator(SmartCard param1, String param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/device/api/system/UserAuthenticator.<init> ()V
      // 04: aload 0
      // 05: ldc_w 2147483646
      // 08: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 0b: aload 0
      // 0c: ldc_w 2147483646
      // 0f: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 12: aload 0
      // 13: aload 2
      // 14: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._label Ljava/lang/String;
      // 17: aload 1
      // 18: ifnull 63
      // 1b: aload 0
      // 1c: aload 1
      // 1d: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._smartCard Lnet/rim/device/api/smartcard/SmartCard;
      // 20: aload 0
      // 21: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._label Ljava/lang/String;
      // 24: ifnonnull 32
      // 27: aload 0
      // 28: aload 0
      // 29: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._smartCard Lnet/rim/device/api/smartcard/SmartCard;
      // 2c: invokevirtual net/rim/device/api/smartcard/SmartCard.getLabel ()Ljava/lang/String;
      // 2f: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._label Ljava/lang/String;
      // 32: aload 1
      // 33: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 36: astore 3
      // 37: aload 3
      // 38: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 3b: pop
      // 3c: return
      // 3d: astore 4
      // 3f: goto 44
      // 42: astore 4
      // 44: new java/lang/IllegalArgumentException
      // 47: dup
      // 48: new java/lang/StringBuffer
      // 4b: dup
      // 4c: invokespecial java/lang/StringBuffer.<init> ()V
      // 4f: aload 3
      // 50: invokevirtual java/lang/Class.getName ()Ljava/lang/String;
      // 53: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 56: ldc_w " must have public default constructor"
      // 59: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 5c: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 5f: invokespecial java/lang/IllegalArgumentException.<init> (Ljava/lang/String;)V
      // 62: athrow
      // 63: return
      // try (27 -> 30): 31 null
      // try (27 -> 30): 33 null
   }

   private boolean login(SmartCardSession param1, String param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 004: ifnull 00a
      // 007: goto 0a8
      // 00a: aload 0
      // 00b: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationCertificateHash [B
      // 00e: ifnonnull 014
      // 011: goto 0a8
      // 014: aload 0
      // 015: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 018: new net/rim/device/api/crypto/certificate/CertificateHashKeyStoreIndex
      // 01b: dup
      // 01c: invokespecial net/rim/device/api/crypto/certificate/CertificateHashKeyStoreIndex.<init> ()V
      // 01f: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.addIndex (Lnet/rim/device/api/crypto/keystore/KeyStoreIndex;)Z 2
      // 024: pop
      // 025: bipush 0
      // 026: istore 3
      // 027: iload 3
      // 028: bipush 2
      // 02a: if_icmpge 09f
      // 02d: iload 3
      // 02e: tableswitch 26 -1 1 97 36 26
      // 048: aload 0
      // 049: aload 1
      // 04a: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.importCertificatesFromSmartCard (Lnet/rim/device/api/smartcard/SmartCardSession;)Z
      // 04d: ifne 052
      // 050: bipush 0
      // 051: ireturn
      // 052: aload 0
      // 053: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.getKeyStore ()Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 056: ldc2_w 4966172969402917741
      // 059: aload 0
      // 05a: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationCertificateHash [B
      // 05d: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.elements (JLjava/lang/Object;)Ljava/util/Enumeration; 4
      // 062: astore 4
      // 064: aload 4
      // 066: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 06b: ifeq 08f
      // 06e: aload 4
      // 070: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 075: checkcast net/rim/device/api/crypto/keystore/KeyStoreData
      // 078: astore 5
      // 07a: aload 5
      // 07c: ifnull 064
      // 07f: aload 5
      // 081: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.isPrivateKeySet ()Z 1
      // 086: ifeq 064
      // 089: aload 0
      // 08a: aload 5
      // 08c: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 08f: aload 0
      // 090: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 093: ifnull 099
      // 096: goto 09f
      // 099: iinc 3 1
      // 09c: goto 027
      // 09f: aload 0
      // 0a0: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 0a3: ifnonnull 0a8
      // 0a6: bipush 0
      // 0a7: ireturn
      // 0a8: aload 1
      // 0a9: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 0ac: ifne 0bb
      // 0af: aload 2
      // 0b0: ifnull 0bb
      // 0b3: aload 1
      // 0b4: aload 2
      // 0b5: bipush 3
      // 0b7: invokevirtual net/rim/device/api/smartcard/SmartCardSession.login (Ljava/lang/String;I)Z
      // 0ba: pop
      // 0bb: aload 0
      // 0bc: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 0bf: ifnonnull 0c5
      // 0c2: goto 2f8
      // 0c5: bipush 64
      // 0c7: newarray 8
      // 0c9: astore 3
      // 0ca: aload 3
      // 0cb: invokestatic net/rim/device/api/crypto/RandomSource.getBytes ([B)V
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 0d2: aconst_null
      // 0d3: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPrivateKey (Lnet/rim/device/api/crypto/keystore/KeyStoreDataTicket;)Lnet/rim/device/api/crypto/PrivateKey; 2
      // 0d8: astore 4
      // 0da: aconst_null
      // 0db: astore 5
      // 0dd: aload 4
      // 0df: dup
      // 0e0: instanceof net/rim/device/api/crypto/RSAPrivateKey
      // 0e3: ifne 0ea
      // 0e6: pop
      // 0e7: goto 12c
      // 0ea: checkcast net/rim/device/api/crypto/RSAPrivateKey
      // 0ed: astore 6
      // 0ef: new net/rim/device/api/smartcard/SmartCardUserAuthenticator$AuthenticatorRSACryptoToken
      // 0f2: dup
      // 0f3: aload 6
      // 0f5: invokevirtual net/rim/device/api/crypto/RSAPrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 0f8: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAsymmetricCryptoToken ()Lnet/rim/device/api/crypto/AsymmetricCryptoToken; 1
      // 0fd: checkcast net/rim/device/api/crypto/RSACryptoToken
      // 100: aload 1
      // 101: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator$AuthenticatorRSACryptoToken.<init> (Lnet/rim/device/api/crypto/RSACryptoToken;Lnet/rim/device/api/smartcard/SmartCardSession;)V
      // 104: astore 7
      // 106: new net/rim/device/api/crypto/RSACryptoSystem
      // 109: dup
      // 10a: aload 7
      // 10c: aload 6
      // 10e: invokevirtual net/rim/device/api/crypto/RSAPrivateKey.getRSACryptoSystem ()Lnet/rim/device/api/crypto/RSACryptoSystem;
      // 111: invokevirtual net/rim/device/api/crypto/RSACryptoSystem.getBitLength ()I
      // 114: invokespecial net/rim/device/api/crypto/RSACryptoSystem.<init> (Lnet/rim/device/api/crypto/RSACryptoToken;I)V
      // 117: astore 8
      // 119: new net/rim/device/api/crypto/RSAPrivateKey
      // 11c: dup
      // 11d: aload 8
      // 11f: aload 6
      // 121: invokevirtual net/rim/device/api/crypto/RSAPrivateKey.getCryptoTokenData ()Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;
      // 124: invokespecial net/rim/device/api/crypto/RSAPrivateKey.<init> (Lnet/rim/device/api/crypto/RSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 127: astore 5
      // 129: goto 1db
      // 12c: aload 4
      // 12e: dup
      // 12f: instanceof net/rim/device/api/crypto/DSAPrivateKey
      // 132: ifne 139
      // 135: pop
      // 136: goto 17e
      // 139: checkcast net/rim/device/api/crypto/DSAPrivateKey
      // 13c: astore 6
      // 13e: new net/rim/device/api/smartcard/SmartCardUserAuthenticator$AuthenticatorDSACryptoToken
      // 141: dup
      // 142: aload 6
      // 144: invokevirtual net/rim/device/api/crypto/DSAPrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 147: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAsymmetricCryptoToken ()Lnet/rim/device/api/crypto/AsymmetricCryptoToken; 1
      // 14c: checkcast net/rim/device/api/crypto/DSACryptoToken
      // 14f: aload 1
      // 150: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator$AuthenticatorDSACryptoToken.<init> (Lnet/rim/device/api/crypto/DSACryptoToken;Lnet/rim/device/api/smartcard/SmartCardSession;)V
      // 153: astore 7
      // 155: new net/rim/device/api/crypto/DSACryptoSystem
      // 158: dup
      // 159: aload 7
      // 15b: aload 6
      // 15d: invokevirtual net/rim/device/api/crypto/DSAPrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 160: checkcast net/rim/device/api/crypto/DSACryptoSystem
      // 163: invokevirtual net/rim/device/api/crypto/DSACryptoSystem.getCryptoTokenData ()Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;
      // 166: invokespecial net/rim/device/api/crypto/DSACryptoSystem.<init> (Lnet/rim/device/api/crypto/DSACryptoToken;Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;)V
      // 169: astore 8
      // 16b: new net/rim/device/api/crypto/DSAPrivateKey
      // 16e: dup
      // 16f: aload 8
      // 171: aload 6
      // 173: invokevirtual net/rim/device/api/crypto/DSAPrivateKey.getCryptoTokenData ()Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;
      // 176: invokespecial net/rim/device/api/crypto/DSAPrivateKey.<init> (Lnet/rim/device/api/crypto/DSACryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 179: astore 5
      // 17b: goto 1db
      // 17e: aload 4
      // 180: dup
      // 181: instanceof net/rim/device/api/crypto/ECPrivateKey
      // 184: ifne 18b
      // 187: pop
      // 188: goto 1d0
      // 18b: checkcast net/rim/device/api/crypto/ECPrivateKey
      // 18e: astore 6
      // 190: new net/rim/device/api/smartcard/SmartCardUserAuthenticator$AuthenticatorECCryptoToken
      // 193: dup
      // 194: aload 6
      // 196: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 199: invokeinterface net/rim/device/api/crypto/CryptoSystem.getAsymmetricCryptoToken ()Lnet/rim/device/api/crypto/AsymmetricCryptoToken; 1
      // 19e: checkcast net/rim/device/api/crypto/ECCryptoToken
      // 1a1: aload 1
      // 1a2: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator$AuthenticatorECCryptoToken.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Lnet/rim/device/api/smartcard/SmartCardSession;)V
      // 1a5: astore 7
      // 1a7: new net/rim/device/api/crypto/ECCryptoSystem
      // 1aa: dup
      // 1ab: aload 7
      // 1ad: aload 6
      // 1af: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getCryptoSystem ()Lnet/rim/device/api/crypto/CryptoSystem;
      // 1b2: checkcast net/rim/device/api/crypto/ECCryptoSystem
      // 1b5: invokevirtual net/rim/device/api/crypto/ECCryptoSystem.getCryptoTokenData ()Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;
      // 1b8: invokespecial net/rim/device/api/crypto/ECCryptoSystem.<init> (Lnet/rim/device/api/crypto/ECCryptoToken;Lnet/rim/device/api/crypto/CryptoTokenCryptoSystemData;)V
      // 1bb: astore 8
      // 1bd: new net/rim/device/api/crypto/ECPrivateKey
      // 1c0: dup
      // 1c1: aload 8
      // 1c3: aload 6
      // 1c5: invokevirtual net/rim/device/api/crypto/ECPrivateKey.getCryptoTokenData ()Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;
      // 1c8: invokespecial net/rim/device/api/crypto/ECPrivateKey.<init> (Lnet/rim/device/api/crypto/ECCryptoSystem;Lnet/rim/device/api/crypto/CryptoTokenPrivateKeyData;)V
      // 1cb: astore 5
      // 1cd: goto 1db
      // 1d0: new net/rim/device/api/smartcard/SmartCardException
      // 1d3: dup
      // 1d4: ldc_w "Unknown private key type"
      // 1d7: invokespecial net/rim/device/api/smartcard/SmartCardException.<init> (Ljava/lang/String;)V
      // 1da: athrow
      // 1db: aload 3
      // 1dc: bipush 0
      // 1dd: aload 3
      // 1de: arraylength
      // 1df: aload 5
      // 1e1: aconst_null
      // 1e2: ldc_w "X509"
      // 1e5: invokestatic net/rim/device/api/crypto/Crypto.sign ([BIILnet/rim/device/api/crypto/PrivateKey;Ljava/lang/String;Ljava/lang/String;)[B
      // 1e8: astore 6
      // 1ea: aload 3
      // 1eb: bipush 0
      // 1ec: aload 3
      // 1ed: arraylength
      // 1ee: aload 0
      // 1ef: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._authenticationKeyStoreData Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 1f2: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getPublicKey ()Lnet/rim/device/api/crypto/PublicKey; 1
      // 1f7: ldc_w "X509"
      // 1fa: aload 6
      // 1fc: bipush 0
      // 1fd: invokestatic net/rim/device/api/crypto/Crypto.verify ([BIILnet/rim/device/api/crypto/PublicKey;Ljava/lang/String;[BI)Z
      // 200: ifeq 206
      // 203: goto 2f8
      // 206: bipush 0
      // 207: istore 7
      // 209: aload 0
      // 20a: aload 1
      // 20b: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getMaxLoginAttempts ()I
      // 20e: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 211: aload 1
      // 212: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 215: ifeq 223
      // 218: aload 0
      // 219: aload 0
      // 21a: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 21d: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 220: goto 23e
      // 223: aload 0
      // 224: aload 1
      // 225: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getRemainingLoginAttempts ()I
      // 228: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 22b: goto 23e
      // 22e: astore 8
      // 230: aload 0
      // 231: ldc_w 2147483646
      // 234: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 237: aload 0
      // 238: ldc_w 2147483646
      // 23b: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 23e: iload 7
      // 240: ireturn
      // 241: astore 4
      // 243: bipush 0
      // 244: istore 5
      // 246: aload 0
      // 247: aload 1
      // 248: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getMaxLoginAttempts ()I
      // 24b: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 24e: aload 1
      // 24f: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 252: ifeq 260
      // 255: aload 0
      // 256: aload 0
      // 257: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 25a: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 25d: goto 27b
      // 260: aload 0
      // 261: aload 1
      // 262: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getRemainingLoginAttempts ()I
      // 265: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 268: goto 27b
      // 26b: astore 6
      // 26d: aload 0
      // 26e: ldc_w 2147483646
      // 271: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 274: aload 0
      // 275: ldc_w 2147483646
      // 278: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 27b: iload 5
      // 27d: ireturn
      // 27e: astore 4
      // 280: bipush 0
      // 281: istore 5
      // 283: aload 0
      // 284: aload 1
      // 285: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getMaxLoginAttempts ()I
      // 288: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 28b: aload 1
      // 28c: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 28f: ifeq 29d
      // 292: aload 0
      // 293: aload 0
      // 294: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 297: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 29a: goto 2b8
      // 29d: aload 0
      // 29e: aload 1
      // 29f: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getRemainingLoginAttempts ()I
      // 2a2: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 2a5: goto 2b8
      // 2a8: astore 6
      // 2aa: aload 0
      // 2ab: ldc_w 2147483646
      // 2ae: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 2b1: aload 0
      // 2b2: ldc_w 2147483646
      // 2b5: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 2b8: iload 5
      // 2ba: ireturn
      // 2bb: astore 4
      // 2bd: bipush 0
      // 2be: istore 5
      // 2c0: aload 0
      // 2c1: aload 1
      // 2c2: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getMaxLoginAttempts ()I
      // 2c5: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 2c8: aload 1
      // 2c9: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 2cc: ifeq 2da
      // 2cf: aload 0
      // 2d0: aload 0
      // 2d1: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 2d4: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 2d7: goto 2f5
      // 2da: aload 0
      // 2db: aload 1
      // 2dc: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getRemainingLoginAttempts ()I
      // 2df: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 2e2: goto 2f5
      // 2e5: astore 6
      // 2e7: aload 0
      // 2e8: ldc_w 2147483646
      // 2eb: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 2ee: aload 0
      // 2ef: ldc_w 2147483646
      // 2f2: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 2f5: iload 5
      // 2f7: ireturn
      // 2f8: aload 0
      // 2f9: aload 1
      // 2fa: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getMaxLoginAttempts ()I
      // 2fd: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 300: aload 1
      // 301: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 304: ifeq 312
      // 307: aload 0
      // 308: aload 0
      // 309: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 30c: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 30f: goto 369
      // 312: aload 0
      // 313: aload 1
      // 314: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getRemainingLoginAttempts ()I
      // 317: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 31a: goto 369
      // 31d: astore 3
      // 31e: aload 0
      // 31f: ldc_w 2147483646
      // 322: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 325: aload 0
      // 326: ldc_w 2147483646
      // 329: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 32c: goto 369
      // 32f: astore 9
      // 331: aload 0
      // 332: aload 1
      // 333: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getMaxLoginAttempts ()I
      // 336: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 339: aload 1
      // 33a: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 33d: ifeq 34b
      // 340: aload 0
      // 341: aload 0
      // 342: getfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 345: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 348: goto 366
      // 34b: aload 0
      // 34c: aload 1
      // 34d: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getRemainingLoginAttempts ()I
      // 350: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 353: goto 366
      // 356: astore 10
      // 358: aload 0
      // 359: ldc_w 2147483646
      // 35c: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._maxAuthenticationAttempts I
      // 35f: aload 0
      // 360: ldc_w 2147483646
      // 363: putfield net/rim/device/api/smartcard/SmartCardUserAuthenticator._remainingAuthenticationAttempts I
      // 366: aload 9
      // 368: athrow
      // 369: aload 1
      // 36a: invokevirtual net/rim/device/api/smartcard/SmartCardSession.isLoggedIn ()Z
      // 36d: ifne 372
      // 370: bipush 0
      // 371: ireturn
      // 372: aload 0
      // 373: invokespecial net/rim/device/api/smartcard/SmartCardUserAuthenticator.lockWhenSmartcardRemoved ()Z
      // 376: ifeq 385
      // 379: aload 1
      // 37a: invokevirtual net/rim/device/api/smartcard/SmartCardSession.getSmartCardReaderSession ()Lnet/rim/device/api/smartcard/SmartCardReaderSession;
      // 37d: invokevirtual net/rim/device/api/smartcard/SmartCardReaderSession.getSmartCardReader ()Lnet/rim/device/api/smartcard/SmartCardReader;
      // 380: aload 0
      // 381: invokevirtual net/rim/device/api/smartcard/SmartCardReader.addListener (Lnet/rim/device/api/smartcard/ReaderStatusListener;)Z
      // 384: pop
      // 385: bipush 1
      // 386: ireturn
      // try (217 -> 233): 234 null
      // try (80 -> 217): 243 null
      // try (246 -> 262): 263 null
      // try (80 -> 217): 272 null
      // try (275 -> 291): 292 null
      // try (80 -> 217): 301 null
      // try (304 -> 320): 321 null
      // try (330 -> 346): 347 null
      // try (61 -> 217): 355 null
      // try (243 -> 246): 355 null
      // try (272 -> 275): 355 null
      // try (301 -> 304): 355 null
      // try (356 -> 372): 373 null
      // try (355 -> 356): 355 null
   }

   @Override
   public Class getClassInAuthenticatorModule() {
      return this._smartCard.getClass();
   }
}
