package net.rim.device.apps.internal.ldap.x509;

import java.util.Enumeration;
import net.rim.device.api.crypto.SHA1Digest;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateHashKeyStoreIndex;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateStatus;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.AssociatedData;
import net.rim.device.api.crypto.keystore.DeviceKeyStore;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.ldap.LDAPAttribute;
import net.rim.device.api.ldap.LDAPEntry;
import net.rim.device.api.ldap.LDAPQuery;
import net.rim.device.api.memorycleaner.MemoryCleanerDaemon;
import net.rim.device.api.memorycleaner.MemoryCleanerListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.ldap.LDAPBrowser;
import net.rim.device.apps.internal.ldap.LDAPBrowserContext;
import net.rim.device.apps.internal.ldap.LDAPBrowserException;
import net.rim.device.apps.internal.ldap.LDAPBrowserOptionStore;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class X509LDAPBrowserContext implements LDAPBrowserContext, MemoryCleanerListener {
   private KeyStore _ks = DeviceKeyStore.getInstance();
   private TrustedKeyStore _rks = (TrustedKeyStore)TrustedKeyStore.getInstance();
   private KeyStoreTicket _ksTicket;
   private KeyStoreTicket _rksTicket;
   private LDAPBrowserOptionStore _optionStore;
   private ApplicationDescriptor _ribbonApplicationDescriptor;
   private static final long LAST_SERVER = -2428937988060367461L;
   private static final String CONTEXT = "X509";
   private static final String USER_CERT_BINARY = "usercertificate;binary";
   private static final String USER_CERT = "usercertificate";
   private static final String CA_CERT = "cacertificate";
   private static final String CA_CERT_BINARY = "cacertificate;binary";
   private static final String CN = "cn";
   private static final String MAIL = "mail";
   private static final String GIVENNAME = "givenname";
   private static final String SN = "sn";
   private static final String OBJECTCLASS = "objectclass";
   private static final String CA_CERT_PAIR = "crosscertificatepair";
   private static final String CA_CERT_PAIR_BINARY = "crosscertificatepair;binary";
   private static final String HASHED_PREFIX = "SHA1-";
   private static final String HASHED_USER_CERT = "SHA1-usercertificate";
   private static final String HASHED_USER_CERT_BINARY = "SHA1-usercertificate;binary";
   private static final String HASHED_CA_CERT = "SHA1-cacertificate";
   private static final String HASHED_CA_CERT_BINARY = "SHA1-cacertificate;binary";

   public X509LDAPBrowserContext() {
      this._ks.addIndex(new CertificateHashKeyStoreIndex());
      this._optionStore = LDAPBrowserOptionStore.getInstance();
      this._ribbonApplicationDescriptor = new ApplicationDescriptor(
         ApplicationDescriptor.currentApplicationDescriptor(), LDAPBrowser.getString(40), new String[0]
      );
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public final KeyStore getKeyStore() {
      return this._ks;
   }

   @Override
   public final boolean isCertificateInKeyStore(Certificate certificate) {
      return this._ks.isMember(certificate);
   }

   @Override
   public final Certificate getCertificateFromKeyStore(byte[] certificateID) {
      Enumeration matchingElements = this._ks.elements(4966172969402917741L, certificateID);

      while (matchingElements.hasMoreElements()) {
         KeyStoreData matchingData = (KeyStoreData)matchingElements.nextElement();
         Certificate matchingCertificate = matchingData.getCertificate();
         if (matchingCertificate != null) {
            return matchingCertificate;
         }
      }

      return null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final String getFriendlyName(LDAPEntry entry, Certificate certificate) throws LDAPBrowserException {
      try {
         return certificate != null ? certificate.getSubjectFriendlyName() : (String)entry.getAttribute("cn").getValue(0);
      } catch (Throwable var5) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final String getEmail(LDAPEntry entry) throws LDAPBrowserException {
      try {
         return (String)entry.getAttribute("mail").getValue(0);
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   @Override
   public final boolean isFetchRootApplicable() {
      return true;
   }

   @Override
   public final void addToKeyStore(LDAPEntry[] entries, Certificate[] certificates) {
      if (entries != null && entries.length != 0 && certificates != null && certificates.length != 0) {
         int certLength = certificates.length;
         boolean addCrossCerts = false;

         for (int i = 0; i < certLength; i++) {
            if (this.isCrossCertificateAvailable(entries[i])) {
               addCrossCerts = this.shouldAddCrossCertificates();
               break;
            }
         }

         boolean fetchStatus = this.shouldFetchStatus();

         for (int i = 0; i < certLength; i++) {
            this.addSingleCertificateToKeyStore(entries[i], certificates[i], fetchStatus, addCrossCerts);
         }
      }
   }

   @Override
   public final void addCrossCertificatesToKeyStore(LDAPEntry[] param1) throws LDAPBrowserException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnonnull 0c
      // 04: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 07: dup
      // 08: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> ()V
      // 0b: athrow
      // 0c: aload 1
      // 0d: arraylength
      // 0e: istore 2
      // 0f: bipush 0
      // 10: istore 3
      // 11: iload 3
      // 12: iload 2
      // 13: if_icmpge 87
      // 16: bipush 0
      // 17: anewarray 446
      // 1a: astore 4
      // 1c: aload 0
      // 1d: aload 1
      // 1e: iload 3
      // 1f: aaload
      // 20: ldc_w "crosscertificatepair"
      // 23: aload 4
      // 25: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.getCrossCertificateValues (Lnet/rim/device/api/ldap/LDAPEntry;Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 28: aload 0
      // 29: aload 1
      // 2a: iload 3
      // 2b: aaload
      // 2c: ldc_w "crosscertificatepair;binary"
      // 2f: aload 4
      // 31: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.getCrossCertificateValues (Lnet/rim/device/api/ldap/LDAPEntry;Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 34: aload 4
      // 36: arraylength
      // 37: istore 5
      // 39: bipush 0
      // 3a: istore 6
      // 3c: iload 6
      // 3e: iload 5
      // 40: if_icmpge 75
      // 43: aload 0
      // 44: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ks Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 47: aload 4
      // 49: iload 6
      // 4b: aaload
      // 4c: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 51: ifne 6f
      // 54: aload 0
      // 55: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.checkKSTicket ()V
      // 58: aload 0
      // 59: aload 1
      // 5a: iload 3
      // 5b: aaload
      // 5c: aload 0
      // 5d: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ks Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 60: aload 4
      // 62: iload 6
      // 64: aaload
      // 65: aconst_null
      // 66: aconst_null
      // 67: aload 0
      // 68: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ksTicket Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 6b: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.addCertificateToKeyStore (Lnet/rim/device/api/ldap/LDAPEntry;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/certificate/Certificate;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 6e: pop
      // 6f: iinc 6 1
      // 72: goto 3c
      // 75: iinc 3 1
      // 78: goto 11
      // 7b: astore 2
      // 7c: return
      // 7d: astore 2
      // 7e: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 81: dup
      // 82: aload 2
      // 83: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // 86: athrow
      // 87: return
      // try (6 -> 67): 67 null
      // try (6 -> 67): 69 null
   }

   @Override
   public final boolean isCrossCertificateAvailable(LDAPEntry entry) {
      Certificate[] crossCertificates = new Certificate[0];
      this.getCrossCertificateValues(entry, "crosscertificatepair", crossCertificates);
      this.getCrossCertificateValues(entry, "crosscertificatepair;binary", crossCertificates);
      int numCrossCertificates = crossCertificates.length;

      for (int i = 0; i < numCrossCertificates; i++) {
         if (!this._ks.isMember(crossCertificates[i])) {
            return true;
         }
      }

      return false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addSecondStageFilter(LDAPEntry entry, LDAPQuery query) throws LDAPBrowserException {
      try {
         StringBuffer buffer = new StringBuffer();

         label55:
         try {
            query.addFilter(buffer.append("cn").append('=').append(entry.getAttribute("cn").getValue(0)).toString());
         } finally {
            break label55;
         }

         buffer.setLength(0);

         try {
            query.addFilter(buffer.append("mail").append('=').append(entry.getAttribute("mail").getValue(0)).toString());
         } finally {
            return;
         }
      } catch (Throwable var16) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addSecondStageFilter(LDAPEntry[] entries, LDAPQuery query) throws LDAPBrowserException {
      try {
         StringBuffer buffer = new StringBuffer();
         buffer.append('(').append('|');
         int entriesLength = entries.length;

         for (int i = 0; i < entriesLength; i++) {
            String cn = null;
            String mail = null;

            label116:
            try {
               cn = "cn=" + entries[i].getAttribute("cn").getValue(0).toString();
            } finally {
               break label116;
            }

            label113:
            try {
               mail = "mail=" + entries[i].getAttribute("mail").getValue(0).toString();
            } finally {
               break label113;
            }

            if (mail != null && cn != null) {
               buffer.append('(').append('&').append('(').append(cn).append(')');
               buffer.append('(').append(mail).append(')').append(')');
            } else if (mail != null) {
               buffer.append('(').append(mail).append(')');
            } else {
               buffer.append('(').append(cn).append(')');
            }
         }

         buffer.append(')');
         query.addFilter(buffer.toString());
      } catch (Throwable var20) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addSecondStageAttributes(LDAPQuery query) throws LDAPBrowserException {
      try {
         query.addAttribute("givenname");
         query.addAttribute("cn");
         query.addAttribute("sn");
         query.addAttribute("mail");
         query.addAttribute("usercertificate");
         query.addAttribute("usercertificate;binary");
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   @Override
   public final void getCertificates(LDAPEntry entry, Certificate[] certificates, byte[][] certificateIDs) {
      this.getCertificateValues(entry, "usercertificate", certificates, certificateIDs);
      this.getCertificateValues(entry, "usercertificate;binary", certificates, certificateIDs);
      this.getCertificateValues(entry, "cacertificate", certificates, certificateIDs);
      this.getCertificateValues(entry, "cacertificate;binary", certificates, certificateIDs);
   }

   @Override
   public final void getCertificateIDs(LDAPEntry entry, byte[][] certificateIDs) {
      this.getByteArrayValues(entry, "SHA1-usercertificate", certificateIDs);
      this.getByteArrayValues(entry, "SHA1-usercertificate;binary", certificateIDs);
      this.getByteArrayValues(entry, "SHA1-cacertificate", certificateIDs);
      this.getByteArrayValues(entry, "SHA1-cacertificate;binary", certificateIDs);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addFirstStageFilter(String[] firstNames, String[] lastNames, String[] emailAddresses, LDAPQuery query) throws LDAPBrowserException {
      try {
         StringBuffer buffer = new StringBuffer();
         int numFirstNames = firstNames != null ? firstNames.length : 0;
         if (numFirstNames > 0) {
            buffer.append('(').append('|');

            for (int i = 0; i < numFirstNames; i++) {
               buffer.append('(').append("givenname").append('=').append(firstNames[i]).append('*').append(')');
               buffer.append('(').append("cn").append('=').append(firstNames[i]).append('*').append(')');
            }

            buffer.append(')');
            query.addFilter(buffer.toString());
         }

         buffer.setLength(0);
         int numLastNames = lastNames != null ? lastNames.length : 0;
         if (numLastNames > 0) {
            buffer.append('(').append('|');

            for (int i = 0; i < numLastNames; i++) {
               buffer.append('(').append("sn").append('=').append(lastNames[i]).append('*').append(')');
            }

            buffer.append(')');
            query.addFilter(buffer.toString());
         }

         buffer.setLength(0);
         int numEmailAddresses = emailAddresses != null ? emailAddresses.length : 0;
         if (numEmailAddresses > 0) {
            buffer.append('(').append('|');

            for (int i = 0; i < numEmailAddresses; i++) {
               buffer.append('(').append("mail").append('=').append(emailAddresses[i]).append('*').append(')');
            }

            buffer.append(')');
            query.addFilter(buffer.toString());
         }

         buffer.setLength(0);
         buffer.append('(').append('|');
         buffer.append('(').append("usercertificate").append('=').append('*').append(')');
         buffer.append('(').append("usercertificate;binary").append('=').append('*').append(')');
         buffer.append(')');
         query.addFilter(buffer.toString());
      } catch (Throwable var11) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addFirstStageAttributes(LDAPQuery query) throws LDAPBrowserException {
      try {
         query.addAttribute("givenname");
         query.addAttribute("cn");
         query.addAttribute("sn");
         query.addAttribute("mail");
         query.addHashedAttribute("usercertificate");
         query.addHashedAttribute("usercertificate;binary");
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addRootCertFilter(LDAPQuery query) throws LDAPBrowserException {
      try {
         StringBuffer buffer = new StringBuffer();
         buffer.append('(').append('|').append('(').append("objectclass").append("=certificationAuthority)(");
         buffer.append("objectclass").append("=pkiCa)(").append("objectclass").append("=entrustCA))");
         query.addFilter(buffer.toString());
         buffer.setLength(0);
         buffer.append('(').append('|').append('(').append("cacertificate").append('=').append('*').append(')').append('(');
         buffer.append("cacertificate;binary").append('=').append('*').append(')').append(')');
         query.addFilter(buffer.toString());
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addRootCertAttributes(LDAPQuery query) throws LDAPBrowserException {
      try {
         query.addAttribute("cacertificate");
         query.addAttribute("cacertificate;binary");
         query.addAttribute("cn");
         query.addAttribute("crosscertificatepair");
         query.addAttribute("crosscertificatepair;binary");
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   @Override
   public final long getObjectTypesConstant() {
      return 12420023019045170L;
   }

   @Override
   public final ApplicationDescriptor getRibbonApplicationDescriptor() {
      return this._ribbonApplicationDescriptor;
   }

   @Override
   public final String getFetchingString() {
      return LDAPBrowser.getString(67);
   }

   @Override
   public final String getScreenTitle() {
      return LDAPBrowser.getString(40);
   }

   @Override
   public final String getEmptyString() {
      return LDAPBrowser.getString(68);
   }

   @Override
   public final String getFetchStringPlural() {
      return LDAPBrowser.getString(91);
   }

   @Override
   public final String getMenuFetchStatusString() {
      return LDAPBrowser.getString(8);
   }

   @Override
   public final String getMenuFetchRootString() {
      return LDAPBrowser.getString(6);
   }

   @Override
   public final String getMenuAddCertString() {
      return LDAPBrowser.getString(5);
   }

   @Override
   public final String getMenuAddCertStringPlural() {
      return LDAPBrowser.getString(92);
   }

   @Override
   public final String getMenuViewCertString() {
      return LDAPBrowser.getString(3);
   }

   @Override
   public final CertificateServerInfo getLastServer() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      return (CertificateServerInfo)registry.get(-2428937988060367461L);
   }

   @Override
   public final void setLastServer(CertificateServerInfo lastServer) {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      if (registry.get(-2428937988060367461L) == null) {
         registry.put(-2428937988060367461L, lastServer);
      } else {
         registry.replace(-2428937988060367461L, lastServer);
      }
   }

   @Override
   public final boolean cleanNow(int event) {
      boolean cleaned = this._ksTicket != null || this._rksTicket != null;
      this._ksTicket = null;
      this._rksTicket = null;
      return cleaned;
   }

   @Override
   public final String getDescription() {
      return this.getScreenTitle();
   }

   private final boolean shouldFetchStatus() {
      int fetchCertStatusOption = this._optionStore.getFetchCertStatus("X509");
      return fetchCertStatusOption == 2 ? this.askYesNoQuestion(LDAPBrowser.getString(70)) : fetchCertStatusOption == 0;
   }

   private final boolean shouldAddCrossCertificates() {
      return this.askYesNoQuestion(LDAPBrowser.getString(101));
   }

   private final boolean askYesNoQuestion(String message) {
      String[] yesNo = CommonResource.getStringArray(10012);
      Bitmap bitmap = Bitmap.getPredefinedBitmap(1);
      SimpleChoiceDialog dialog = new SimpleChoiceDialog(message, yesNo, 0, bitmap, 0);
      synchronized (Application.getEventLock()) {
         dialog.show();
      }

      return dialog.getSelectedIndex() == 0;
   }

   private final void addSingleCertificateToKeyStore(LDAPEntry param1, Certificate param2, boolean param3, boolean param4) throws LDAPBrowserException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 2
      // 001: ifnonnull 00c
      // 004: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 007: dup
      // 008: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> ()V
      // 00b: athrow
      // 00c: iload 3
      // 00d: ifeq 031
      // 010: new net/rim/device/api/crypto/certificate/status/CertificateStatusRequest
      // 013: dup
      // 014: bipush 1
      // 015: anewarray 2229
      // 018: dup
      // 019: bipush 0
      // 01a: aload 2
      // 01b: aastore
      // 01c: bipush 0
      // 01d: aload 0
      // 01e: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ks Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 021: aconst_null
      // 022: aload 2
      // 023: invokespecial net/rim/device/api/crypto/certificate/status/CertificateStatusRequest.<init> ([Lnet/rim/device/api/crypto/certificate/Certificate;ZLnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/keystore/CertificateStatusManagerTicket;Ljava/lang/Object;)V
      // 026: astore 5
      // 028: aload 5
      // 02a: aconst_null
      // 02b: bipush 0
      // 02c: bipush 0
      // 02d: invokestatic net/rim/device/api/crypto/certificate/status/CertificateStatusProvider.requestCertificateStatus (Lnet/rim/device/api/crypto/certificate/status/CertificateStatusRequest;Lnet/rim/device/api/crypto/certificate/status/CertificateStatusListener;ZZ)I
      // 030: pop
      // 031: aload 0
      // 032: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.checkKSTicket ()V
      // 035: aload 0
      // 036: aload 1
      // 037: aload 0
      // 038: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ks Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 03b: aload 2
      // 03c: aconst_null
      // 03d: aconst_null
      // 03e: aload 0
      // 03f: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ksTicket Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 042: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.addCertificateToKeyStore (Lnet/rim/device/api/ldap/LDAPEntry;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/certificate/Certificate;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 045: astore 5
      // 047: aload 5
      // 049: invokeinterface net/rim/device/api/crypto/keystore/KeyStoreData.getLabel ()Ljava/lang/String; 1
      // 04e: astore 6
      // 050: bipush 8
      // 052: invokestatic net/rim/device/apps/internal/api/crypto/CryptoCommonResources.getString (I)Ljava/lang/String;
      // 055: bipush 1
      // 056: anewarray 2285
      // 059: dup
      // 05a: bipush 0
      // 05b: aload 2
      // 05c: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSubjectFriendlyName ()Ljava/lang/String; 1
      // 061: aastore
      // 062: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 065: astore 7
      // 067: aload 2
      // 068: invokeinterface net/rim/device/api/crypto/certificate/Certificate.isRoot ()Z 1
      // 06d: ifeq 0b7
      // 070: aload 0
      // 071: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rks Lnet/rim/device/api/crypto/keystore/TrustedKeyStore;
      // 074: aload 2
      // 075: invokevirtual net/rim/device/api/crypto/keystore/TrustedKeyStore.isAllowed (Lnet/rim/device/api/crypto/certificate/Certificate;)Z
      // 078: ifeq 0b7
      // 07b: bipush 3
      // 07d: aload 7
      // 07f: invokestatic net/rim/device/api/ui/component/Dialog.ask (ILjava/lang/String;)I
      // 082: bipush 4
      // 084: if_icmpne 0b7
      // 087: aload 0
      // 088: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rks Lnet/rim/device/api/crypto/keystore/TrustedKeyStore;
      // 08b: aload 0
      // 08c: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rksTicket Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 08f: invokevirtual net/rim/device/api/crypto/keystore/TrustedKeyStore.checkTicket (Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Z
      // 092: ifne 0a5
      // 095: aload 0
      // 096: aload 0
      // 097: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rks Lnet/rim/device/api/crypto/keystore/TrustedKeyStore;
      // 09a: bipush 104
      // 09c: invokestatic net/rim/device/apps/internal/ldap/LDAPBrowser.getString (I)Ljava/lang/String;
      // 09f: invokevirtual net/rim/device/api/crypto/keystore/SyncableRIMKeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 0a2: putfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rksTicket Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 0a5: aload 0
      // 0a6: aload 1
      // 0a7: aload 0
      // 0a8: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rks Lnet/rim/device/api/crypto/keystore/TrustedKeyStore;
      // 0ab: aload 2
      // 0ac: aload 6
      // 0ae: aconst_null
      // 0af: aload 0
      // 0b0: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._rksTicket Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 0b3: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.addCertificateToKeyStore (Lnet/rim/device/api/ldap/LDAPEntry;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/certificate/Certificate;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 0b6: pop
      // 0b7: iload 4
      // 0b9: ifeq 124
      // 0bc: bipush 0
      // 0bd: anewarray 2383
      // 0c0: astore 8
      // 0c2: aload 0
      // 0c3: aload 1
      // 0c4: ldc_w "crosscertificatepair"
      // 0c7: aload 8
      // 0c9: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.getCrossCertificateValues (Lnet/rim/device/api/ldap/LDAPEntry;Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 0cc: aload 0
      // 0cd: aload 1
      // 0ce: ldc_w "crosscertificatepair;binary"
      // 0d1: aload 8
      // 0d3: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.getCrossCertificateValues (Lnet/rim/device/api/ldap/LDAPEntry;Ljava/lang/String;[Lnet/rim/device/api/crypto/certificate/Certificate;)V
      // 0d6: aload 8
      // 0d8: arraylength
      // 0d9: istore 9
      // 0db: bipush 0
      // 0dc: istore 10
      // 0de: iload 10
      // 0e0: iload 9
      // 0e2: if_icmpge 124
      // 0e5: aload 0
      // 0e6: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ks Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 0e9: aload 8
      // 0eb: iload 10
      // 0ed: aaload
      // 0ee: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 0f3: ifne 10f
      // 0f6: aload 0
      // 0f7: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.checkKSTicket ()V
      // 0fa: aload 0
      // 0fb: aload 1
      // 0fc: aload 0
      // 0fd: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ks Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 100: aload 8
      // 102: iload 10
      // 104: aaload
      // 105: aconst_null
      // 106: aconst_null
      // 107: aload 0
      // 108: getfield net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext._ksTicket Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;
      // 10b: invokespecial net/rim/device/apps/internal/ldap/x509/X509LDAPBrowserContext.addCertificateToKeyStore (Lnet/rim/device/api/ldap/LDAPEntry;Lnet/rim/device/api/crypto/keystore/KeyStore;Lnet/rim/device/api/crypto/certificate/Certificate;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData;
      // 10e: pop
      // 10f: iinc 10 1
      // 112: goto 0de
      // 115: astore 5
      // 117: return
      // 118: astore 5
      // 11a: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 11d: dup
      // 11e: aload 5
      // 120: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // 123: athrow
      // 124: return
      // try (6 -> 140): 140 null
      // try (6 -> 140): 142 null
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final KeyStoreData addCertificateToKeyStore(
      LDAPEntry entry, KeyStore keyStore, Certificate certificate, String label, CertificateStatus status, KeyStoreTicket ticket
   ) {
      if (keyStore.isMember(certificate)) {
         return null;
      }

      AssociatedData[] associatedData = null;
      boolean var12 = false /* VF: Semaphore variable */;

      label62:
      try {
         var12 = true;
         associatedData = CertificateUtilities.getEmailAssociatedDataArray(certificate);
         if (associatedData == null) {
            if (entry != null) {
               int numEmailAddresses = entry.getAttribute("mail").getSize();
               associatedData = new AssociatedData[numEmailAddresses];

               for (int i = 0; i < numEmailAddresses; i++) {
                  String email = (String)entry.getAttribute("mail").getValue(i);
                  if (email != null && email.length() > 0) {
                     associatedData[i] = new AssociatedData(-1124699153917633064L, StringUtilities.toLowerCase(email, 1701707776).getBytes());
                  }
               }

               var12 = false;
            } else {
               var12 = false;
            }
         } else {
            var12 = false;
         }
      } finally {
         if (var12) {
            associatedData = CertificateUtilities.getEmailAssociatedDataArray(certificate);
            break label62;
         }
      }

      if (label == null && !this._optionStore.getPromptForCertLabel("X509")) {
         label = certificate.getSubjectFriendlyName();
      }

      return keyStore.set(associatedData, label, certificate, status, ticket);
   }

   private final void getCertificateValues(LDAPEntry entry, String attributeName, Certificate[] certificates, byte[][] certificateIDs) {
      try {
         LDAPAttribute attribute = entry.getAttribute(attributeName);
         SHA1Digest sha1Digest = new SHA1Digest();
         int numAttributeValues = attribute.getSize();

         for (int i = 0; i < numAttributeValues; i++) {
            byte[] currentCertificateEncoding = (byte[])attribute.getValue(i);
            sha1Digest.update(currentCertificateEncoding);
            byte[] currentHash = sha1Digest.getDigest();
            Certificate currentCertificate = CertificateUtilities.readCertificateFile(null, currentCertificateEncoding);
            if (currentCertificate != null) {
               Arrays.add(certificates, currentCertificate);
               Arrays.add(certificateIDs, currentHash);
            }
         }
      } finally {
         return;
      }
   }

   private final void getCrossCertificateValues(LDAPEntry param1, String param2, Certificate[] param3) throws LDAPBrowserException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: aload 2
      // 02: invokeinterface net/rim/device/api/ldap/LDAPEntry.getAttribute (Ljava/lang/String;)Lnet/rim/device/api/ldap/LDAPAttribute; 2
      // 07: astore 4
      // 09: aload 4
      // 0b: invokeinterface net/rim/device/api/ldap/LDAPAttribute.getSize ()I 1
      // 10: istore 5
      // 12: bipush 0
      // 13: istore 6
      // 15: iload 6
      // 17: iload 5
      // 19: if_icmpge 87
      // 1c: aload 4
      // 1e: iload 6
      // 20: invokeinterface net/rim/device/api/ldap/LDAPAttribute.getValue (I)Ljava/lang/Object; 2
      // 25: checkcast [B
      // 28: astore 7
      // 2a: new net/rim/device/api/crypto/asn1/ASN1InputByteArray
      // 2d: dup
      // 2e: aload 7
      // 30: invokespecial net/rim/device/api/crypto/asn1/ASN1InputByteArray.<init> ([B)V
      // 33: astore 8
      // 35: aload 8
      // 37: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence ()V
      // 3a: bipush 0
      // 3b: istore 9
      // 3d: iload 9
      // 3f: bipush 2
      // 41: if_icmpge 73
      // 44: aload 8
      // 46: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.peekNextTag ()I
      // 49: iload 9
      // 4b: if_icmpne 6d
      // 4e: aload 8
      // 50: bipush 2
      // 52: iload 9
      // 54: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readSequence (II)V
      // 57: aconst_null
      // 58: aload 8
      // 5a: invokevirtual net/rim/device/api/crypto/asn1/ASN1InputByteArray.readFieldAsByteArray ()[B
      // 5d: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.readCertificateFile (Ljava/lang/String;[B)Lnet/rim/device/api/crypto/certificate/Certificate;
      // 60: astore 10
      // 62: aload 10
      // 64: ifnull 6d
      // 67: aload 3
      // 68: aload 10
      // 6a: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 6d: iinc 9 1
      // 70: goto 3d
      // 73: iinc 6 1
      // 76: goto 15
      // 79: astore 4
      // 7b: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 7e: dup
      // 7f: aload 4
      // 81: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // 84: athrow
      // 85: astore 4
      // 87: return
      // try (0 -> 51): 51 null
      // try (0 -> 51): 57 null
   }

   private final void getByteArrayValues(LDAPEntry entry, String attributeName, byte[][] values) {
      try {
         LDAPAttribute attribute = entry.getAttribute(attributeName);
         int numAttributeValues = attribute.getSize();

         for (int i = 0; i < numAttributeValues; i++) {
            byte[] currentValue = (byte[])attribute.getValue(i);
            Arrays.add(values, currentValue);
         }
      } finally {
         return;
      }
   }

   private final void checkKSTicket() {
      if (!this._ks.checkTicket(this._ksTicket)) {
         this._ksTicket = this._ks.getTicket(LDAPBrowser.getString(104));
      }
   }

   @Override
   public final boolean isRootCertificatesLoadNeeded(Certificate[] addedCerts) {
      int numCerts = addedCerts.length;

      for (int i = 0; i < numCerts; i++) {
         if (addedCerts[i] instanceof Certificate) {
            Certificate[] chain = CertificateUtilities.buildCertificateChain(addedCerts[i], this._ks);
            long properties = CertificateChainProperties.getCertificateChainProperties(chain, this._rks, System.currentTimeMillis());
            if ((properties & 8) != 0 && (properties & 1) != 0) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final String getKeyStoreBrowserContextString() {
      return "Certificate";
   }
}
