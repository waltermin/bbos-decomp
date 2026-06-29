package net.rim.device.apps.internal.ldap.pgp;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateImporterFactory;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.pgp.PGPCertificate;
import net.rim.device.api.crypto.certificate.pgp.PGPKeyIDKeyStoreIndex;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.PGPKeyStore;
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
import net.rim.device.apps.internal.ldap.LDAPBrowser;
import net.rim.device.apps.internal.ldap.LDAPBrowserContext;
import net.rim.device.apps.internal.ldap.LDAPBrowserException;
import net.rim.device.apps.internal.ldap.LDAPBrowserOptionStore;
import net.rim.device.internal.crypto.pgp.PGPUtilities;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public final class PGPLDAPBrowserContext implements LDAPBrowserContext, MemoryCleanerListener {
   private KeyStore _keyStore = PGPKeyStore.getInstance();
   private KeyStoreTicket _keyStoreTicket;
   private LDAPBrowserOptionStore _optionStore;
   private ApplicationDescriptor _ribbonApplicationDescriptor;
   private static final long LAST_SERVER = -684815099476831942L;
   private static final String CONTEXT = "PGP";

   public PGPLDAPBrowserContext() {
      this._keyStore.addIndex(new PGPKeyIDKeyStoreIndex());
      this._optionStore = LDAPBrowserOptionStore.getInstance();
      this._ribbonApplicationDescriptor = new ApplicationDescriptor(
         ApplicationDescriptor.currentApplicationDescriptor(), LDAPBrowser.getString(89), new String[0]
      );
      MemoryCleanerDaemon.addWeakListener(this, false);
   }

   @Override
   public final KeyStore getKeyStore() {
      return this._keyStore;
   }

   @Override
   public final boolean isCertificateInKeyStore(Certificate certificate) {
      return this._keyStore.isMember(certificate);
   }

   @Override
   public final Certificate getCertificateFromKeyStore(byte[] certificateID) {
      Enumeration matchingElements = this._keyStore.elements(-2737350786039236692L, certificateID);

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
         return (String)entry.getAttribute(PGPCertificate.PGP_USER_ID).getValue(0);
      } catch (Throwable var5) {
         throw new LDAPBrowserException(e);
      }
   }

   @Override
   public final String getEmail(LDAPEntry entry) {
      return "";
   }

   @Override
   public final boolean isFetchRootApplicable() {
      return false;
   }

   @Override
   public final void addToKeyStore(LDAPEntry[] entries, Certificate[] certificates) throws LDAPBrowserException {
      if (certificates != null && certificates.length != 0) {
         try {
            boolean fetchStatus = this.shouldFetchStatus();
            boolean promptForLabel = this._optionStore.getPromptForCertLabel("PGP");
            int numCertificates = certificates.length;

            for (int i = 0; i < numCertificates; i++) {
               String currLabel = null;
               if (!promptForLabel) {
                  currLabel = certificates[i].getSubjectFriendlyName();
               }

               this.addSingleCertificateToKeyStore((PGPCertificate)certificates[i], currLabel, fetchStatus);
            }
         } finally {
            return;
         }
      } else {
         throw new LDAPBrowserException();
      }
   }

   @Override
   public final void addCrossCertificatesToKeyStore(LDAPEntry[] entries) throws LDAPBrowserException {
      throw new LDAPBrowserException();
   }

   @Override
   public final boolean isCrossCertificateAvailable(LDAPEntry entry) {
      return false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addSecondStageFilter(LDAPEntry entry, LDAPQuery query) throws LDAPBrowserException {
      try {
         StringBuffer buffer = new StringBuffer();
         buffer.append(PGPCertificate.PGP_USER_ID).append('=');
         buffer.append(entry.getAttribute(PGPCertificate.PGP_USER_ID).getValue(0));
         query.addFilter(buffer.toString());
      } catch (Throwable var5) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addSecondStageFilter(LDAPEntry[] entries, LDAPQuery query) throws LDAPBrowserException {
      try {
         StringBuffer filterBuffer = new StringBuffer();
         filterBuffer.append('(').append('|');
         StringBuffer entryBuffer = new StringBuffer();
         int entriesLength = entries.length;

         for (int i = 0; i < entriesLength; i++) {
            try {
               entryBuffer.setLength(0);
               entryBuffer.append(PGPCertificate.PGP_USER_ID).append('=');
               entryBuffer.append(entries[i].getAttribute(PGPCertificate.PGP_USER_ID).getValue(0));
               filterBuffer.append('(').append(entryBuffer).append(')');
            } finally {
               continue;
            }
         }

         filterBuffer.append(')');
         query.addFilter(filterBuffer.toString());
      } catch (Throwable var13) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addSecondStageAttributes(LDAPQuery query) throws LDAPBrowserException {
      try {
         query.addAttribute(PGPCertificate.PGP_KEY);
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   @Override
   public final void getCertificates(LDAPEntry param1, Certificate[] param2, byte[][] param3) throws LDAPBrowserException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: getstatic net/rim/device/api/crypto/certificate/pgp/PGPCertificate.PGP_KEY Ljava/lang/String;
      // 04: invokeinterface net/rim/device/api/ldap/LDAPEntry.getAttribute (Ljava/lang/String;)Lnet/rim/device/api/ldap/LDAPAttribute; 2
      // 09: astore 4
      // 0b: aload 4
      // 0d: invokeinterface net/rim/device/api/ldap/LDAPAttribute.getSize ()I 1
      // 12: istore 5
      // 14: bipush 0
      // 15: istore 6
      // 17: iload 6
      // 19: iload 5
      // 1b: if_icmplt 21
      // 1e: goto b1
      // 21: aload 4
      // 23: iload 6
      // 25: invokeinterface net/rim/device/api/ldap/LDAPAttribute.getValue (I)Ljava/lang/Object; 2
      // 2a: checkcast java/lang/String
      // 2d: invokevirtual java/lang/String.getBytes ()[B
      // 30: astore 7
      // 32: new java/io/ByteArrayInputStream
      // 35: dup
      // 36: aload 7
      // 38: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 3b: astore 8
      // 3d: new net/rim/device/api/crypto/pgp/PGPArmorDecoder
      // 40: dup
      // 41: aload 8
      // 43: aload 0
      // 44: getfield net/rim/device/apps/internal/ldap/pgp/PGPLDAPBrowserContext._keyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 47: invokespecial net/rim/device/api/crypto/pgp/PGPArmorDecoder.<init> (Ljava/io/InputStream;Lnet/rim/device/api/crypto/keystore/KeyStore;)V
      // 4a: astore 9
      // 4c: aload 9
      // 4e: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.numCertificates ()I
      // 51: istore 10
      // 53: bipush 0
      // 54: istore 11
      // 56: iload 11
      // 58: iload 10
      // 5a: if_icmpge 7b
      // 5d: aload 9
      // 5f: iload 11
      // 61: invokevirtual net/rim/device/api/crypto/pgp/PGPArmorDecoder.getCertificate (I)Lnet/rim/device/api/crypto/certificate/pgp/PGPCertificate;
      // 64: astore 12
      // 66: aload 2
      // 67: aload 12
      // 69: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 6c: aload 3
      // 6d: aload 12
      // 6f: invokevirtual net/rim/device/api/crypto/certificate/pgp/PGPCertificate.getKeyID ()[B
      // 72: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 75: iinc 11 1
      // 78: goto 56
      // 7b: iinc 6 1
      // 7e: goto 17
      // 81: astore 4
      // 83: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 86: dup
      // 87: aload 4
      // 89: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // 8c: athrow
      // 8d: astore 4
      // 8f: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 92: dup
      // 93: aload 4
      // 95: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // 98: athrow
      // 99: astore 4
      // 9b: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // 9e: dup
      // 9f: aload 4
      // a1: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // a4: athrow
      // a5: astore 4
      // a7: new net/rim/device/apps/internal/ldap/LDAPBrowserException
      // aa: dup
      // ab: aload 4
      // ad: invokespecial net/rim/device/apps/internal/ldap/LDAPBrowserException.<init> (Ljava/lang/Exception;)V
      // b0: athrow
      // b1: return
      // try (0 -> 54): 54 null
      // try (0 -> 54): 60 null
      // try (0 -> 54): 66 null
      // try (0 -> 54): 72 null
   }

   @Override
   public final void getCertificateIDs(LDAPEntry entry, byte[][] certificateIDs) {
      try {
         LDAPAttribute attribute = entry.getAttribute(PGPCertificate.PGP_CERT_ID);
         int numAttributeValues = attribute.getSize();

         for (int i = 0; i < numAttributeValues; i++) {
            byte[] currentValue = PGPUtilities.hexASCIIStringToBinary((String)attribute.getValue(i));
            Arrays.add(certificateIDs, currentValue);
         }
      } finally {
         return;
      }
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
               buffer.append('(').append(PGPCertificate.PGP_USER_ID).append('=').append(firstNames[i]).append('*').append(')');
            }

            buffer.append(')');
            query.addFilter(buffer.toString());
         }

         int numLastNames = lastNames != null ? lastNames.length : 0;
         if (numLastNames > 0) {
            buffer.append('(').append('|');

            for (int i = 0; i < numLastNames; i++) {
               buffer.append('(').append(PGPCertificate.PGP_USER_ID).append("=* ").append(lastNames[i]).append("*<*").append(')');
            }

            buffer.append(')');
            query.addFilter(buffer.toString());
         }

         int numEmailAddresses = emailAddresses != null ? emailAddresses.length : 0;
         if (numEmailAddresses > 0) {
            buffer.append('(').append('|');

            for (int i = 0; i < numEmailAddresses; i++) {
               buffer.append('(').append(PGPCertificate.PGP_USER_ID).append("=*<").append(emailAddresses[i]).append("*>*").append(')');
            }

            buffer.append(')');
            query.addFilter(buffer.toString());
         }
      } catch (Throwable var11) {
         throw new LDAPBrowserException(e);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void addFirstStageAttributes(LDAPQuery query) throws LDAPBrowserException {
      try {
         query.addAttribute(PGPCertificate.PGP_CERT_ID);
         query.addAttribute(PGPCertificate.PGP_USER_ID);
      } catch (Throwable var4) {
         throw new LDAPBrowserException(e);
      }
   }

   @Override
   public final void addRootCertFilter(LDAPQuery query) throws LDAPBrowserException {
      throw new LDAPBrowserException();
   }

   @Override
   public final void addRootCertAttributes(LDAPQuery query) throws LDAPBrowserException {
      throw new LDAPBrowserException();
   }

   @Override
   public final long getObjectTypesConstant() {
      return -3216013493425785397L;
   }

   @Override
   public final ApplicationDescriptor getRibbonApplicationDescriptor() {
      return this._ribbonApplicationDescriptor;
   }

   @Override
   public final String getFetchingString() {
      return LDAPBrowser.getString(90);
   }

   @Override
   public final String getScreenTitle() {
      return LDAPBrowser.getString(89);
   }

   @Override
   public final String getEmptyString() {
      return LDAPBrowser.getString(88);
   }

   @Override
   public final String getFetchStringPlural() {
      return LDAPBrowser.getString(60);
   }

   @Override
   public final String getMenuFetchStatusString() {
      return LDAPBrowser.getString(112);
   }

   @Override
   public final String getMenuFetchRootString() {
      throw new RuntimeException();
   }

   @Override
   public final String getMenuAddCertString() {
      return LDAPBrowser.getString(86);
   }

   @Override
   public final String getMenuAddCertStringPlural() {
      return LDAPBrowser.getString(69);
   }

   @Override
   public final String getMenuViewCertString() {
      return LDAPBrowser.getString(85);
   }

   @Override
   public final CertificateServerInfo getLastServer() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      return (CertificateServerInfo)registry.get(-684815099476831942L);
   }

   @Override
   public final void setLastServer(CertificateServerInfo lastServer) {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      if (registry.get(-684815099476831942L) == null) {
         registry.put(-684815099476831942L, lastServer);
      } else {
         registry.replace(-684815099476831942L, lastServer);
      }
   }

   @Override
   public final boolean cleanNow(int event) {
      boolean cleaned = this._keyStoreTicket != null;
      this._keyStoreTicket = null;
      return cleaned;
   }

   @Override
   public final String getDescription() {
      return this.getScreenTitle();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void addSingleCertificateToKeyStore(PGPCertificate certificate, String label, boolean fetchStatus) throws LDAPBrowserException {
      if (certificate == null) {
         throw new LDAPBrowserException();
      }

      try {
         if (fetchStatus) {
            CertificateStatusRequest request = new CertificateStatusRequest(new Certificate[]{certificate}, false, this._keyStore, null, certificate);
            CertificateStatusProvider.requestCertificateStatus(request, null, false, false);
         }

         CertificateImporterFactory.importCertificate(certificate, null, label, this._keyStore, this.getKeyStoreTicket());
      } catch (Throwable var6) {
         throw new LDAPBrowserException(e);
      }
   }

   private final KeyStoreTicket getKeyStoreTicket() {
      if (!this._keyStore.checkTicket(this._keyStoreTicket)) {
         this._keyStoreTicket = this._keyStore.getTicket(LDAPBrowser.getString(104));
      }

      return this._keyStoreTicket;
   }

   @Override
   public final boolean isRootCertificatesLoadNeeded(Certificate[] addedCerts) {
      return false;
   }

   @Override
   public final String getKeyStoreBrowserContextString() {
      return "PGP";
   }

   private final boolean shouldFetchStatus() {
      int fetchCertStatusOption = this._optionStore.getFetchCertStatus("PGP");
      return fetchCertStatusOption == 2 ? this.askYesNoQuestion(LDAPBrowser.getString(106)) : fetchCertStatusOption == 0;
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
}
