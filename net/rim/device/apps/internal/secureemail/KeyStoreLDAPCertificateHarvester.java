package net.rim.device.apps.internal.secureemail;

import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateChainProperties;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusQuery;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.keystore.CertificateStatusManagerTicket;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreOptions;
import net.rim.device.api.crypto.keystore.KeyStoreTicket;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ObjectUtilities;

public class KeyStoreLDAPCertificateHarvester extends CertificateHarvester {
   protected SecureEmailFactory _secureEmailFactory;
   protected SecureEmailUtilities _secureEmailUtilities;
   protected KeyStore _preferredKeyStore;
   private long[] _encodingUIDs;
   private CertificateStatusManagerTicket _autoStatusFetchTicket;
   private boolean _userCancelledAutoFetch;
   private static final boolean DEBUG;

   public KeyStoreLDAPCertificateHarvester(SecureEmailFactory secureEmailFactory, boolean isPINMessage) {
      super(isPINMessage);
      this._secureEmailFactory = secureEmailFactory;
      this._secureEmailUtilities = secureEmailFactory.getUtilities();
      this._preferredKeyStore = this._secureEmailFactory.getPreferredKeyStore();
      this._encodingUIDs = new long[]{this._secureEmailFactory.getEncodingUID()};
   }

   @Override
   public long[] getEncodingUIDs() {
      return this._encodingUIDs;
   }

   @Override
   public int getPriority() {
      return 0;
   }

   @Override
   public String getDisplayName() {
      return this._secureEmailFactory.getEncodingString();
   }

   @Override
   protected void harvestCertificates(RecipientData recipientData) {
      this.harvestCertificates(recipientData, true);
   }

   protected void harvestCertificates(RecipientData recipientData, boolean updateGauge) {
      SecureEmailKeyStoreData[] allKeyStoreData = new SecureEmailKeyStoreData[0];
      this._secureEmailUtilities.findLocalKeyStoreData(recipientData, allKeyStoreData, this._preferredKeyStore);
      int numAllKeyStoreData = allKeyStoreData.length;
      RecipientData$CertificateDetails[] allCertificateDetails = new RecipientData$CertificateDetails[numAllKeyStoreData];

      for (int i = 0; i < numAllKeyStoreData; i++) {
         SecureEmailKeyStoreData currentKeyStoreData = allKeyStoreData[i];
         Certificate currentCertificate = currentKeyStoreData.getCertificate();
         allCertificateDetails[i] = this.buildCertificateDetails(currentKeyStoreData.getEmailAddress(), currentCertificate, currentKeyStoreData.getLabel());
      }

      if (!this.populateRecommendedCertificates(recipientData, allCertificateDetails)) {
         boolean atLeastOneStatusRefreshed = this.refreshCertificateStatus(recipientData, allCertificateDetails, updateGauge);
         if (!atLeastOneStatusRefreshed || !this.populateRecommendedCertificates(recipientData, allCertificateDetails)) {
            boolean atLeastOneCertificateFetched = this.fetchCertificatesFromLDAP(recipientData, allCertificateDetails, updateGauge);
            if (!atLeastOneCertificateFetched || !this.populateRecommendedCertificates(recipientData, allCertificateDetails)) {
               atLeastOneStatusRefreshed = this.refreshCertificateStatus(recipientData, allCertificateDetails, updateGauge);
               if (!atLeastOneStatusRefreshed || !this.populateRecommendedCertificates(recipientData, allCertificateDetails)) {
                  this.populateRecipientData(recipientData, allCertificateDetails);
               }
            }
         }
      }
   }

   private boolean populateRecommendedCertificates(RecipientData recipientData, RecipientData$CertificateDetails[] allCertificateDetails) {
      int[] allowedIndices = this.determineAllowedIndices(allCertificateDetails);
      int[] recommendedIndices = this.determineRecommendedIndices(allCertificateDetails);
      if (recommendedIndices.length == 0) {
         return false;
      }

      this.populateRecipientData(recipientData, allCertificateDetails, allowedIndices, recommendedIndices);
      return true;
   }

   private void populateRecipientData(RecipientData recipientData, RecipientData$CertificateDetails[] allCertificateDetails) {
      int[] allowedIndices = this.determineAllowedIndices(allCertificateDetails);
      int[] recommendedIndices = this.determineRecommendedIndices(allCertificateDetails);
      this.populateRecipientData(recipientData, allCertificateDetails, allowedIndices, recommendedIndices);
   }

   private void populateRecipientData(
      RecipientData recipientData, RecipientData$CertificateDetails[] allCertificateDetails, int[] allowedIndices, int[] recommendedIndices
   ) {
      int numAllowedCertificates = allowedIndices.length;
      RecipientData$CertificateDetails[] allowedCertificateDetails = new RecipientData$CertificateDetails[numAllowedCertificates];

      for (int i = 0; i < numAllowedCertificates; i++) {
         allowedCertificateDetails[i] = allCertificateDetails[allowedIndices[i]].clone();
      }

      int numRecommendedCertificates = recommendedIndices.length;
      RecipientData$CertificateDetails[] recommendedCertificateDetails = new RecipientData$CertificateDetails[numRecommendedCertificates];

      for (int i = 0; i < numRecommendedCertificates; i++) {
         recommendedCertificateDetails[i] = allCertificateDetails[recommendedIndices[i]].clone();
      }

      recipientData.setAllCertificates(allCertificateDetails);
      recipientData.setAllowedCertificates(allowedCertificateDetails);
      recipientData.setRecommendedCertificates(recommendedCertificateDetails);
   }

   private int[] determineAllowedIndices(RecipientData$CertificateDetails[] allCertificateDetails) {
      int[] allowedIndices = new int[0];
      int numAllCertificateDetails = allCertificateDetails.length;

      for (int i = 0; i < numAllCertificateDetails; i++) {
         RecipientData$CertificateDetails currentCertificateDetails = allCertificateDetails[i];
         if (this._secureEmailUtilities
            .isCertificateAllowed(currentCertificateDetails.getCertificate(), currentCertificateDetails.getCertificateChainProperties(), 2)) {
            Arrays.add(allowedIndices, i);
         }
      }

      return allowedIndices;
   }

   private int[] determineRecommendedIndices(RecipientData$CertificateDetails[] allCertificateDetails) {
      int[] recommendedIndices = new int[0];
      int numAllCertificateDetails = allCertificateDetails.length;

      for (int i = 0; i < numAllCertificateDetails; i++) {
         RecipientData$CertificateDetails currentCertificateDetails = allCertificateDetails[i];
         if (this._secureEmailUtilities
            .isCertificateRecommended(currentCertificateDetails.getCertificate(), currentCertificateDetails.getCertificateChainProperties(), 2)) {
            Arrays.add(recommendedIndices, i);
         }
      }

      return recommendedIndices;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean refreshCertificateStatus(RecipientData recipientData, RecipientData$CertificateDetails[] allCertificateDetails, boolean updateGauge) {
      if (this._userCancelledAutoFetch) {
         return false;
      }

      int numAllCertificateDetails = allCertificateDetails.length;
      boolean performedAutoFetch = false;
      if (updateGauge) {
         super._completionDialog.stepGauge();
      }

      boolean var11 = false /* VF: Semaphore variable */;

      label55:
      try {
         var11 = true;

         for (int e = 0; e < numAllCertificateDetails; e++) {
            RecipientData$CertificateDetails currentCertificateDetails = allCertificateDetails[e];
            long currentCertificateChainProperties = currentCertificateDetails.getCertificateChainProperties();
            if ((currentCertificateChainProperties & 2560) != 0
               && SecureEmailCache.getInstance().shouldAttemptAutoFetchStatus(currentCertificateDetails.getCertificate())) {
               this._autoStatusFetchTicket = this.autoFetchStatus(currentCertificateDetails, this._autoStatusFetchTicket, updateGauge);
               performedAutoFetch = true;
            }
         }

         var11 = false;
      } finally {
         if (var11) {
            this._userCancelledAutoFetch = true;
            break label55;
         }
      }

      if (performedAutoFetch) {
         this.updateCertificateChainProperties(allCertificateDetails);
      }

      return performedAutoFetch;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private CertificateStatusManagerTicket autoFetchStatus(
      RecipientData$CertificateDetails certificateDetails, CertificateStatusManagerTicket certificateStatusManagerTicket, boolean updateGauge
   ) {
      String[] containerStringLowerSingularArray = new Object[]{this._secureEmailFactory.getPublicKeyContainerString(false, false)};
      String fetchingCertificateStatusMessage = MessageFormat.format(SecureEmailResources.getString(138), containerStringLowerSingularArray);
      boolean var19 = false /* VF: Semaphore variable */;

      CertificateStatusManagerTicket var13;
      try {
         var19 = true;
         Certificate[] certificateChain = certificateDetails.getCertificateChain();
         boolean extendedCheckingAvailable = CertificateStatusProvider.queryStatusAvailability(certificateChain, true);
         CertificateStatusRequest request = new Object(certificateChain, extendedCheckingAvailable, this._preferredKeyStore, null, null);
         KeyStoreLDAPCertificateHarvester$FetchStatusBlocker fetchStatusBlocker = new KeyStoreLDAPCertificateHarvester$FetchStatusBlocker(null);
         CertificateStatusQuery query = new Object((CertificateStatusRequest)request, KeyStoreOptions.getCertificateServiceUID(), null);
         ((CertificateStatusQuery)query).setProgressListener(fetchStatusBlocker);
         if (updateGauge) {
            super._completionDialog.setRecipientAction(fetchingCertificateStatusMessage);
            super._completionDialog.setCertificateStatusQuery((CertificateStatusQuery)query);
         }

         synchronized (fetchStatusBlocker) {
            int errorCode = ((CertificateStatusQuery)query).beginQuery();
            if (errorCode == 0) {
               label99:
               try {
                  fetchStatusBlocker.wait();
               } finally {
                  break label99;
               }

               if (fetchStatusBlocker._queryAborted) {
                  throw new Object();
               }
            }

            SecureEmailCache.getInstance().recordAutoFetchStatusResult((CertificateStatusRequest)request);
            var13 = certificateStatusManagerTicket;
            var19 = false;
         }
      } finally {
         if (var19) {
            if (updateGauge) {
               super._completionDialog.clearCertificateStatusQuery();
            }
         }
      }

      if (updateGauge) {
         super._completionDialog.clearCertificateStatusQuery();
      }

      return var13;
   }

   private void updateCertificateChainProperties(RecipientData$CertificateDetails[] allCertificateDetails) {
      int numAllCertificateDetails = allCertificateDetails.length;
      Certificate currentCertificate = null;

      for (int i = 0; i < numAllCertificateDetails; i++) {
         RecipientData$CertificateDetails currentDetails = allCertificateDetails[i];
         currentCertificate = currentDetails.getCertificate();
         if (currentCertificate != null) {
            allCertificateDetails[i] = this.buildCertificateDetails(
               currentDetails.getEmailAddress(), currentCertificate, currentCertificate.getSubjectFriendlyName()
            );
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean fetchCertificatesFromLDAP(RecipientData recipientData, RecipientData$CertificateDetails[] allCertificateDetails, boolean updateGauge) {
      if (this._userCancelledAutoFetch) {
         return false;
      }

      boolean performedAutoFetch = false;
      if (updateGauge) {
         super._completionDialog.stepGauge();
      }

      boolean var13 = false /* VF: Semaphore variable */;

      label102:
      try {
         var13 = true;
         String[] e = recipientData.getAddresses();
         int numEmailAddresses = e != null ? e.length : 0;

         for (int i = 0; i < numEmailAddresses; i++) {
            String emailAddress = e[i];
            Certificate[] fetchedCertificatesByEmail = this.autoFetchCertificates(emailAddress, updateGauge);
            int numFetchedCertificates = fetchedCertificatesByEmail != null ? fetchedCertificatesByEmail.length : 0;

            for (int j = 0; j < numFetchedCertificates; j++) {
               this.addCertificate(emailAddress, fetchedCertificatesByEmail[j], allCertificateDetails);
               performedAutoFetch = true;
            }
         }

         Object certificateID = recipientData.getCertificateID();
         if (certificateID != null) {
            Certificate[] fetchedCertificatesByID = this.autoFetchCertificatesByID(certificateID, updateGauge);
            int numFetchedCertificates = fetchedCertificatesByID != null ? fetchedCertificatesByID.length : 0;

            for (int j = 0; j < numFetchedCertificates; j++) {
               this.addCertificate(null, fetchedCertificatesByID[j], allCertificateDetails);
               performedAutoFetch = true;
            }

            var13 = false;
         } else {
            var13 = false;
         }
      } finally {
         if (var13) {
            this._userCancelledAutoFetch = true;
            break label102;
         }
      }

      if (performedAutoFetch) {
         this.updateCertificateChainProperties(allCertificateDetails);
      }

      return performedAutoFetch;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Certificate[] autoFetchCertificates(String emailAddress, boolean updateGauge) {
      Certificate[] fetchedCertificates = null;
      LDAPCertificateFetch ldapCertificateFetch = this._secureEmailUtilities.getLDAPCertificateFetch();
      if (updateGauge) {
         String[] containerStringLowerSingularArray = new Object[]{this._secureEmailFactory.getPublicKeyContainerString(false, false)};
         String fetchingCertificateMessage = MessageFormat.format(SecureEmailResources.getString(137), containerStringLowerSingularArray);
         super._completionDialog.setRecipientAction(fetchingCertificateMessage);
         super._completionDialog.setLDAPCertificateFetch(ldapCertificateFetch);
      }

      boolean var11 = false /* VF: Semaphore variable */;

      try {
         var11 = true;
         SecureEmailCache var13 = SecureEmailCache.getInstance();
         String[] var14 = new Object[]{emailAddress};
         CertificateUtilities.canonicalizeEmailAddresses(var14);
         CertificateServerInfo[] certificateServerInfo = CertificateServers.selectBestServers(var14);
         if (certificateServerInfo == null) {
            certificateServerInfo = new Object[]{null};
         }

         int i = 0;

         while (true) {
            if (i >= certificateServerInfo.length) {
               var11 = false;
               break;
            }

            if (var13.shouldAttemptAutoFetchCertificate((CertificateServerInfo)certificateServerInfo[i], emailAddress)) {
               fetchedCertificates = ldapCertificateFetch.fetchCertificates(emailAddress, certificateServerInfo);
               if (ldapCertificateFetch.fetchAborted()) {
                  throw new Object();
               }

               var13.recordAutoFetchCertificateResult(emailAddress, ldapCertificateFetch.getLDAPErrorCode(), fetchedCertificates.length, certificateServerInfo);
               var11 = false;
               break;
            }

            i++;
         }
      } finally {
         if (var11) {
            if (updateGauge) {
               super._completionDialog.clearLDAPCertificateFetch();
            }
         }
      }

      if (updateGauge) {
         super._completionDialog.clearLDAPCertificateFetch();
         return fetchedCertificates;
      } else {
         return fetchedCertificates;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Certificate[] autoFetchCertificatesByID(Object certificateID, boolean updateGauge) {
      Certificate[] fetchedCertificates = null;
      LDAPCertificateFetch ldapCertificateFetch = this._secureEmailUtilities.getLDAPCertificateFetch();
      if (updateGauge) {
         String[] containerStringLowerSingularArray = new Object[]{this._secureEmailFactory.getPublicKeyContainerString(false, false)};
         String fetchingCertificateMessage = MessageFormat.format(SecureEmailResources.getString(137), containerStringLowerSingularArray);
         super._completionDialog.setRecipientAction(fetchingCertificateMessage);
         super._completionDialog.setLDAPCertificateFetch(ldapCertificateFetch);
      }

      boolean var10 = false /* VF: Semaphore variable */;

      try {
         var10 = true;
         SecureEmailCache var12 = SecureEmailCache.getInstance();
         CertificateServerInfo[] var13 = CertificateServers.getInstance().getServerInfo(1);
         int i = 0;

         while (true) {
            if (i >= var13.length) {
               var10 = false;
               break;
            }

            if (var12.shouldAttemptAutoFetchCertificate(var13[i], certificateID)) {
               fetchedCertificates = ldapCertificateFetch.fetchCertificates(certificateID, var13);
               if (ldapCertificateFetch.fetchAborted()) {
                  throw new Object();
               }

               var12.recordAutoFetchCertificateResult(certificateID, ldapCertificateFetch.getLDAPErrorCode(), fetchedCertificates.length, var13);
               var10 = false;
               break;
            }

            i++;
         }
      } finally {
         if (var10) {
            if (updateGauge) {
               super._completionDialog.clearLDAPCertificateFetch();
            }
         }
      }

      if (updateGauge) {
         super._completionDialog.clearLDAPCertificateFetch();
         return fetchedCertificates;
      } else {
         return fetchedCertificates;
      }
   }

   private void addCertificate(String emailAddress, Certificate newCertificate, RecipientData$CertificateDetails[] allCertificateDetails) {
      int numCertificateDetails = allCertificateDetails.length;

      for (int i = 0; i < numCertificateDetails; i++) {
         Certificate currentCertificate = allCertificateDetails[i].getCertificate();
         if (ObjectUtilities.objEqual(newCertificate, currentCertificate)) {
            return;
         }
      }

      RecipientData$CertificateDetails newCertificateDetails = new RecipientData$CertificateDetails(
         newCertificate, newCertificate.getSubjectFriendlyName(), emailAddress
      );
      Arrays.add(allCertificateDetails, newCertificateDetails);
   }

   @Override
   public Vector getProcessedRecipients() {
      KeyStoreTicket keyStoreTicket = null;
      Vector processedRecipients = super.getProcessedRecipients();

      try {
         int numProcessedRecipients = processedRecipients.size();

         for (int i = 0; i < numProcessedRecipients; i++) {
            RecipientData currentData = (RecipientData)processedRecipients.elementAt(i);
            RecipientData$CertificateDetails[] recommendedCertificates = currentData.getRecommendedCertificates();
            if (recommendedCertificates != null && recommendedCertificates.length > 0) {
               keyStoreTicket = this.importCertificates(recommendedCertificates, keyStoreTicket);
            } else {
               RecipientData$CertificateDetails[] allowedCertificates = currentData.getAllowedCertificates();
               if (allowedCertificates != null && allowedCertificates.length > 0) {
                  keyStoreTicket = this.importCertificates(allowedCertificates, keyStoreTicket);
               }
            }
         }
      } finally {
         return processedRecipients;
      }

      return processedRecipients;
   }

   private KeyStoreTicket importCertificates(RecipientData$CertificateDetails[] param1, KeyStoreTicket param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 09
      // 04: aload 1
      // 05: arraylength
      // 06: goto 0a
      // 09: bipush 0
      // 0a: istore 3
      // 0b: bipush 0
      // 0c: istore 4
      // 0e: iload 4
      // 10: iload 3
      // 11: if_icmpge 7b
      // 14: aload 1
      // 15: iload 4
      // 17: aaload
      // 18: invokevirtual net/rim/device/apps/internal/secureemail/RecipientData$CertificateDetails.getCertificate ()Lnet/rim/device/api/crypto/certificate/Certificate;
      // 1b: astore 5
      // 1d: aload 0
      // 1e: getfield net/rim/device/apps/internal/secureemail/KeyStoreLDAPCertificateHarvester._preferredKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 21: aload 5
      // 23: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.isMember (Lnet/rim/device/api/crypto/certificate/Certificate;)Z 2
      // 28: ifeq 2e
      // 2b: goto 75
      // 2e: aload 2
      // 2f: ifnonnull 42
      // 32: aload 0
      // 33: getfield net/rim/device/apps/internal/secureemail/KeyStoreLDAPCertificateHarvester._preferredKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 36: sipush 139
      // 39: invokestatic net/rim/device/apps/internal/secureemail/SecureEmailResources.getString (I)Ljava/lang/String;
      // 3c: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.getTicket (Ljava/lang/String;)Lnet/rim/device/api/crypto/keystore/KeyStoreTicket; 2
      // 41: astore 2
      // 42: aload 0
      // 43: getfield net/rim/device/apps/internal/secureemail/KeyStoreLDAPCertificateHarvester._preferredKeyStore Lnet/rim/device/api/crypto/keystore/KeyStore;
      // 46: aload 5
      // 48: invokestatic net/rim/device/api/crypto/certificate/CertificateUtilities.getEmailAssociatedDataArray (Lnet/rim/device/api/crypto/certificate/Certificate;)[Lnet/rim/device/api/crypto/keystore/AssociatedData;
      // 4b: aload 5
      // 4d: invokeinterface net/rim/device/api/crypto/certificate/Certificate.getSubjectFriendlyName ()Ljava/lang/String; 1
      // 52: aload 5
      // 54: aconst_null
      // 55: aload 2
      // 56: invokeinterface net/rim/device/api/crypto/keystore/KeyStore.set ([Lnet/rim/device/api/crypto/keystore/AssociatedData;Ljava/lang/String;Lnet/rim/device/api/crypto/certificate/Certificate;Lnet/rim/device/api/crypto/certificate/CertificateStatus;Lnet/rim/device/api/crypto/keystore/KeyStoreTicket;)Lnet/rim/device/api/crypto/keystore/KeyStoreData; 6
      // 5b: pop
      // 5c: goto 75
      // 5f: astore 6
      // 61: goto 75
      // 64: astore 6
      // 66: goto 75
      // 69: astore 6
      // 6b: goto 75
      // 6e: astore 6
      // 70: goto 75
      // 73: astore 6
      // 75: iinc 4 1
      // 78: goto 0e
      // 7b: aload 2
      // 7c: areturn
      // try (31 -> 42): 43 null
      // try (31 -> 42): 45 null
      // try (31 -> 42): 47 null
      // try (31 -> 42): 49 null
      // try (31 -> 42): 51 null
   }

   public RecipientData$CertificateDetails buildCertificateDetails(String emailAddress, Certificate certificate, String label) {
      Certificate[] certificateChain = null;
      long certificateChainProperties = -1;
      if (certificate != null) {
         Certificate[][][] allCertificateChains = CertificateUtilities.buildCertificateChains(certificate, this._preferredKeyStore, emailAddress);
         long[] allCertificateChainProperties = CertificateChainProperties.getCertificateChainProperties(
            allCertificateChains,
            this._preferredKeyStore,
            TrustedKeyStore.getInstance(),
            System.currentTimeMillis(),
            this._secureEmailFactory.getCryptoSystemProperties()
         );
         int bestCertificateChainIndex = CertificateChainProperties.selectBestCertificateChain(allCertificateChainProperties);
         certificateChain = allCertificateChains[bestCertificateChainIndex];
         certificateChainProperties = allCertificateChainProperties[bestCertificateChainIndex];
      }

      return new RecipientData$CertificateDetails(certificate, label, certificateChainProperties, certificateChain, null, emailAddress);
   }
}
