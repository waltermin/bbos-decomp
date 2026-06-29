package net.rim.device.apps.internal.secureemail;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.crypto.AsymmetricCryptoToken;
import net.rim.device.api.crypto.CryptoIOException;
import net.rim.device.api.crypto.CryptoSystem;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.LDAPCertificateFetch;
import net.rim.device.api.crypto.certificate.x509.X509DistinguishedName;
import net.rim.device.api.crypto.keystore.KeyStore;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.api.crypto.keystore.KeyStoreIndex;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.AddressReference;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.registration.RecognizerRepository;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.header.HeaderTypes;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.secureemail.cache.CachedMessage;
import net.rim.device.internal.system.ITPolicyInternal;
import net.rim.vm.Array;

public class SecureEmailUtilities {
   private static final int BLOCK_SIZE;

   public long getCertificateProperties(Certificate _1) {
      throw null;
   }

   public int getITPolicyContentCiphers() {
      throw null;
   }

   public int getConstantForContentCipher(int _1) {
      throw null;
   }

   public int getContentCipherForConstant(int _1) {
      throw null;
   }

   public int getRecipientContentCiphers(String _1) {
      throw null;
   }

   public int getCertificateContentCiphers(Certificate _1) {
      throw null;
   }

   public static boolean isCertificateSupported(KeyStoreData keyStoreData, int operationType) {
      Certificate certificate = keyStoreData.getCertificate();
      return certificate == null ? false : isCertificateSupported(certificate, operationType);
   }

   public static boolean isCertificateSupported(Certificate certificate, int operationType) {
      try {
         return isCryptoSystemSupported(certificate.getPublicKey().getCryptoSystem(), operationType);
      } finally {
         ;
      }
   }

   public static boolean isCryptoSystemSupported(CryptoSystem system, int operationType) {
      if (system == null) {
         return false;
      }

      AsymmetricCryptoToken token = system.getAsymmetricCryptoToken();
      return token == null ? false : token.isSupported(system, operationType);
   }

   public boolean isCertificateRecommended(KeyStoreData keyStoreData, int operationType) {
      if (keyStoreData == null) {
         return false;
      }

      Certificate certificate = keyStoreData.getCertificate();
      if (certificate == null) {
         return false;
      }

      long properties = this.getCertificateProperties(certificate);
      return this.isCertificateRecommended(certificate, properties, operationType);
   }

   public boolean isCertificateRecommended(Certificate certificate, long properties, int operationType) {
      return this.isCertificateRecommended(certificate, properties, operationType, false);
   }

   public boolean isCertificateRecommended(Certificate certificate, long properties, int operationType, boolean ignoreStale) {
      if (!isCertificateSupported(certificate, operationType)) {
         return false;
      }

      long undesirableProperties = 1342;
      if (!ignoreStale) {
         undesirableProperties |= 2048;
      }

      return (properties & undesirableProperties) == 0;
   }

   public boolean isCertificateStale(Certificate certificate) {
      long properties = this.getCertificateProperties(certificate);
      return this.isCertificateStale(properties);
   }

   public boolean isCertificateStale(long properties) {
      return (properties & 2048) != 0;
   }

   public boolean isCertificateAllowed(KeyStoreData keyStoreData, int operationType) {
      if (keyStoreData == null) {
         return false;
      }

      Certificate certificate = keyStoreData.getCertificate();
      if (certificate == null) {
         return false;
      }

      long certificateProperties = this.getCertificateProperties(certificate);
      return this.isCertificateAllowed(certificate, certificateProperties, operationType);
   }

   public boolean isCertificateAllowed(Certificate certificate, long certificateProperties, int operationType) {
      if (!isCertificateSupported(certificate, operationType)) {
         return false;
      }

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

   public static Object getDisplayContext(CachedMessage cachedMessage, RIMModel childModel, ServiceRecord serviceRecord, Object context) {
      EmailMessageModel parentModel = getParentModel(childModel, context);
      if (parentModel != null && !cachedMessage.isEmailMessageModelSet()) {
         cachedMessage.setEmailMessageModel(parentModel, serviceRecord);
      }

      if (parentModel != null && !isForwardReplyMessage(context)) {
         return context;
      }

      Object displayContext = ContextObject.clone(context);
      ContextObject.setFlag(displayContext, 101);
      return displayContext;
   }

   static EmailMessageModel getParentModel(RIMModel childModel, Object context) {
      if (context instanceof Object) {
         Object parentModel = ContextObject.get(context, 246);
         if (parentModel instanceof Object) {
            EmailMessageModel emailMessageModel = (EmailMessageModel)parentModel;
            int numModels = emailMessageModel.size();

            for (int i = 0; i < numModels; i++) {
               Object submember = emailMessageModel.getAt(i);
               if (ObjectUtilities.objEqual(submember, childModel)) {
                  return emailMessageModel;
               }
            }
         }
      }

      return null;
   }

   public static String[] getEmailAddresses(Certificate cert) {
      return cert != null ? (Object[])cert.getInformation(-7850001002262082664L, null, null) : null;
   }

   public static boolean isForwardReplyMessage(Object context) {
      if (!(context instanceof Object)) {
         return false;
      }

      ContextObject contextObject = (ContextObject)context;
      return contextObject.getFlag(13) || contextObject.getFlag(12) || contextObject.getFlag(53) || contextObject.getFlag(29) || contextObject.getFlag(30);
   }

   public static void getDataToEncodeFromMessage(
      EmailMessageModel message, Vector modelsToEncode, StringBuffer bufferToEncode, Object context, String autoSignature
   ) {
      StringBuffer currentMessage = null;
      StringBuffer originalMessage = null;
      int numModels = message.size();

      for (int i = 0; i < numModels; i++) {
         RIMModel currentModel = (RIMModel)message.getAt(i);
         if (!(currentModel instanceof Object)) {
            if (currentModel instanceof Object) {
               ConversionProvider cp = (ConversionProvider)currentModel;
               originalMessage = (StringBuffer)(new Object());
               cp.convert(context, originalMessage);
               modelsToEncode.addElement(currentModel);
            }
         } else {
            BodyModel bmi = (BodyModel)currentModel;
            currentMessage = (StringBuffer)(new Object());
            currentMessage.append(bmi.getText());
            modelsToEncode.addElement(currentModel);
         }
      }

      if (currentMessage != null) {
         bufferToEncode.append(currentMessage);
      }

      if (autoSignature != null) {
         bufferToEncode.append('\n');
         bufferToEncode.append(autoSignature);
      }

      if (originalMessage != null) {
         bufferToEncode.append(originalMessage);
      }
   }

   public static String[] getEmailAddressesFromPIN(EmailHeaderModel emailHeaderModel) {
      String[] emailAddresses = new Object[0];
      PersistableRIMModel addressBookEntry = emailHeaderModel.getAddressBookEntry();
      if (addressBookEntry instanceof Object) {
         addEmailAddressesFromAddressCard((AddressCardModel)addressBookEntry, emailAddresses);
      }

      return emailAddresses;
   }

   private static void addEmailAddressesFromAddressCard(AddressCardModel addressCardModel, String[] emailAddresses) {
      Recognizer recognizer = RecognizerRepository.getRecognizers(-2985347935260258684L);
      Object[] emailAddressModels = SubmemberUtilities.getSubmembers(addressCardModel, recognizer);
      int numEmailAddressModels = emailAddressModels != null ? emailAddressModels.length : 0;

      for (int i = 0; i < numEmailAddressModels; i++) {
         EmailAddressModel currentEmailAddressModel = (EmailAddressModel)emailAddressModels[i];
         String currentEmailAddress = currentEmailAddressModel.getAddress();
         if (!containsStringIgnoreCase(emailAddresses, currentEmailAddress)) {
            Arrays.add(emailAddresses, currentEmailAddress);
         }
      }
   }

   private static boolean containsStringIgnoreCase(String[] stringArray, String string) {
      int numStrings = stringArray.length;

      for (int i = 0; i < numStrings; i++) {
         if (StringUtilities.strEqualIgnoreCase(stringArray[i], string)) {
            return true;
         }
      }

      return false;
   }

   public static boolean verifySenderAddress(Certificate senderCertificate, String senderAddress, boolean isPINMessage) {
      if (ITPolicy.getBoolean(45, 1, false)) {
         return true;
      }

      if (isPINMessage) {
         return verifySenderEmailAddress(senderCertificate, convertPINToEmailAddress(senderAddress));
      }

      X509DistinguishedName dominoAddressDN = DominoAddressUtilities.createDominoAddressDN(senderAddress);
      return dominoAddressDN != null
         ? verifySenderDominoAddress(senderCertificate, dominoAddressDN)
         : verifySenderEmailAddress(senderCertificate, new Object[]{senderAddress});
   }

   public static boolean verifySenderEmailAddress(Certificate senderCertificate, String[] senderEmailAddresses) {
      String[] certificateEmailAddresses = getEmailAddresses(senderCertificate);
      if (certificateEmailAddresses == null) {
         return true;
      }

      CertificateUtilities.canonicalizeEmailAddresses(senderEmailAddresses);
      int numSenderEmailAddresses = senderEmailAddresses != null ? senderEmailAddresses.length : 0;
      int numCertificateEmailAddresses = certificateEmailAddresses != null ? certificateEmailAddresses.length : 0;

      for (int i = 0; i < numSenderEmailAddresses; i++) {
         for (int j = 0; j < numCertificateEmailAddresses; j++) {
            if (StringUtilities.strEqualIgnoreCase(senderEmailAddresses[i], certificateEmailAddresses[j])) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean verifySenderDominoAddress(Certificate senderCertificate, X509DistinguishedName senderDominoAddress) {
      return !(senderCertificate instanceof Object)
         ? false
         : DominoAddressUtilities.dominoAddressDNMatchesX509DN(senderDominoAddress, (X509DistinguishedName)senderCertificate.getSubject());
   }

   public static String[] convertPINToEmailAddress(String sendersPIN) {
      Factory pinAddressFactory = (Factory)ApplicationRegistry.getApplicationRegistry().waitFor(4246852237058296601L);
      String[] senderEmailAddresses = new Object[0];
      ContextObject pinCreationContext = (ContextObject)(new Object());
      pinCreationContext.put(253, sendersPIN);
      Object pinAddressModel = pinAddressFactory.createInstance(pinCreationContext);
      Object[] addressCardModels = AddressBookServices.reverseLookup(pinAddressModel, null);
      int numAddressCardModels = addressCardModels == null ? 0 : addressCardModels.length;

      for (int i = 0; i < numAddressCardModels; i++) {
         AddressCardModel currentAddressCardModel = (AddressCardModel)addressCardModels[i];
         addEmailAddressesFromAddressCard(currentAddressCardModel, senderEmailAddresses);
      }

      return senderEmailAddresses;
   }

   public static RecipientData[] getRecipientData(EmailHeaderModel emailHeaderModel, boolean isPINMessage) {
      Object insideModel = null;
      String groupName = null;
      if (emailHeaderModel instanceof Object) {
         AddressReference ar = emailHeaderModel;
         insideModel = ar.getInsideModel();
         if (insideModel instanceof Object) {
            GroupAddressCardModel groupModel = (GroupAddressCardModel)insideModel;
            groupName = groupModel.getName();
         }
      }

      if (!emailHeaderModel.isBlank() && !emailHeaderModel.isUnresolved() && !(insideModel instanceof Object)) {
         String[] addressAndName = new Object[2];
         if (!emailHeaderModel.convert(null, addressAndName)) {
            return null;
         }

         RecipientData[] result = new RecipientData[0];

         for (int i = 0; i + 1 < addressAndName.length; i += 2) {
            StringBuffer friendlyName = (StringBuffer)(new Object(addressAndName[i + 1] == null ? addressAndName[i] : addressAndName[i + 1]));
            if (groupName != null) {
               friendlyName.append(MessageFormat.format(SecureEmailResources.getString(147), new Object[]{groupName}));
            }

            String[] emailAddresses = isPINMessage ? getEmailAddressesFromPIN(emailHeaderModel) : new Object[]{addressAndName[i]};
            Array.resize(result, result.length + 1);
            result[result.length - 1] = new RecipientData(friendlyName.toString(), 0, emailAddresses, emailHeaderModel);
         }

         return result;
      } else {
         return null;
      }
   }

   public String getAlwaysBCCEmailAddress(String _1) {
      throw null;
   }

   public static boolean checkITAdminBoundUID(String serviceUID) {
      return ITPolicyInternal.verifyITAdminService(serviceUID, true);
   }

   public void addAlwaysBCCEmailAddress(EmailMessageModel message, String serviceUID) {
      String alwaysBCCEmailAddress = this.getAlwaysBCCEmailAddress(serviceUID);
      if (alwaysBCCEmailAddress != null && alwaysBCCEmailAddress.length() > 0) {
         String[] alwaysBCCArray = new Object[]{alwaysBCCEmailAddress, alwaysBCCEmailAddress};
         ContextObject co = (ContextObject)(new Object());
         co.put(-4054673099568009991L, HeaderTypes._typesAsInteger[2]);
         co.put(251, alwaysBCCArray);
         RIMModel newHeader = (RIMModel)FactoryUtil.createInstance(-8034039608019345282L, co);
         int numModels = message.size();

         for (int j = 0; j < numModels; j++) {
            Object submember = message.getAt(j);
            if (submember instanceof Object) {
               EmailHeaderModel ehm = (EmailHeaderModel)submember;
               if (ehm.getHeaderType() == 2 && ehm.equals(newHeader)) {
                  return;
               }
            }
         }

         message.add(newHeader);
      }
   }

   public static boolean messageContainsBody(ReadableList message, BodyModel body) {
      int numSubmembers = message.size();

      for (int i = 0; i < numSubmembers; i++) {
         Object currentSubmember = message.getAt(i);
         if (body.equals(currentSubmember)) {
            return true;
         }

         if (currentSubmember instanceof Object && messageContainsBody((ReadableList)currentSubmember, body)) {
            return true;
         }
      }

      return false;
   }

   public static byte[] readStreamCompletely(InputStream in) {
      byte[] textBytes = new byte[1024];
      int numTextBytes = 0;

      while (true) {
         int bytesRead = in.read(textBytes, numTextBytes, 1024);
         if (bytesRead == -1) {
            Array.resize(textBytes, numTextBytes);
            return textBytes;
         }

         numTextBytes += bytesRead;
         Array.resize(textBytes, numTextBytes + 1024);
      }
   }

   public void findLocalKeyStoreData(RecipientData recipientData, SecureEmailKeyStoreData[] keysFound, KeyStore keyStore) {
      Array.resize(keysFound, 0);
      String[] addresses = recipientData.getAddresses();
      if (addresses != null) {
         this.findLocalKeyStoreDataByAddresses(addresses, keysFound, keyStore);
      } else {
         Object certificateID = recipientData.getCertificateID();
         if (certificateID != null) {
            this.findLocalKeyStoreDataByCertificateID(certificateID, keysFound, keyStore);
         }
      }
   }

   private void findLocalKeyStoreDataByAddresses(String[] addresses, SecureEmailKeyStoreData[] keysFound, KeyStore keyStore) {
      keyStore.addIndex(new KeyUsageEmailAddressKeyStoreIndex());
      keyStore.addIndex(new KeyUsageDominoAddressKeyStoreIndex());
      CertificateUtilities.canonicalizeEmailAddresses(addresses);

      for (String currentAddress : addresses) {
         if (!this.findLocalKeyStoreDataByEmailAddress(currentAddress, keysFound, keyStore)
            && !this.findLocalKeyStoreDataByDominoAddress(currentAddress, keysFound, keyStore)) {
            this.findLocalKeyStoreDataByAddressFragments(currentAddress, keysFound, keyStore);
         }
      }
   }

   private boolean findLocalKeyStoreDataByEmailAddress(String address, SecureEmailKeyStoreData[] localKeyStoreData, KeyStore keyStore) {
      boolean keysFound = false;
      Object alias = KeyUsageEmailAddressKeyStoreIndex.getAlias(1, address);

      for (Enumeration enumeration = keyStore.elements(3687411874034296952L, alias); enumeration.hasMoreElements(); keysFound = true) {
         this.addLocalKeyStoreData(address, localKeyStoreData, (KeyStoreData)enumeration.nextElement());
      }

      return keysFound;
   }

   private boolean findLocalKeyStoreDataByDominoAddress(String address, SecureEmailKeyStoreData[] localKeyStoreData, KeyStore keyStore) {
      boolean keysFound = false;
      Object alias = KeyUsageDominoAddressKeyStoreIndex.getAlias(1, address);

      for (Enumeration enumeration = keyStore.elements(7492162715265800726L, alias); enumeration.hasMoreElements(); keysFound = true) {
         this.addLocalKeyStoreData(address, localKeyStoreData, (KeyStoreData)enumeration.nextElement());
      }

      return keysFound;
   }

   private void findLocalKeyStoreDataByAddressFragments(String address, SecureEmailKeyStoreData[] localKeyStoreData, KeyStore keyStore) {
      String[] addressFragments = StringUtilities.stringToWords(address);
      Enumeration enumeration = keyStore.elements(3687411874034296952L);

      label48:
      while (enumeration.hasMoreElements()) {
         KeyStoreData data = (KeyStoreData)enumeration.nextElement();
         Certificate certificate = data.getCertificate();
         if (certificate != null && certificate.queryKeyUsage(4) != 0) {
            byte[][][] emailAddresses = (byte[][][])data.getAssociatedData(-1124699153917633064L);
            if (emailAddresses != null) {
               int numEmailAddresses = emailAddresses.length;

               for (int i = 0; i < numEmailAddresses; i++) {
                  String currentEmailAddress = (String)(new Object((byte[])emailAddresses[i]));
                  if (this.checkForMatch(addressFragments, new Object[]{currentEmailAddress})) {
                     this.addLocalKeyStoreData(currentEmailAddress, localKeyStoreData, data);
                     continue label48;
                  }
               }

               String commonName = certificate.getSubjectFriendlyName();
               if (commonName != null && this.checkForMatch(addressFragments, StringUtilities.stringToWords(commonName))) {
                  this.addLocalKeyStoreData(null, localKeyStoreData, data);
               }
            }
         }
      }
   }

   private boolean checkForMatch(String[] searchStrings, String[] compareStrings) {
      int searchStringsLength = searchStrings.length;
      int compareStringsLength = compareStrings.length;

      for (int i = 0; i < searchStringsLength; i++) {
         boolean matchFound = false;

         for (int j = 0; j < compareStringsLength; j++) {
            if (StringUtilities.startsWithIgnoreCaseAndAccents(compareStrings[j], searchStrings[i])) {
               matchFound = true;
               break;
            }
         }

         if (!matchFound) {
            return false;
         }
      }

      return true;
   }

   private void addLocalKeyStoreData(String address, SecureEmailKeyStoreData[] existingKeyStoreData, KeyStoreData newKeyStoreData) {
      SecureEmailKeyStoreData newData = new SecureEmailKeyStoreData(newKeyStoreData, address);
      if (!Arrays.contains(existingKeyStoreData, newData)) {
         Arrays.add(existingKeyStoreData, newData);
      }
   }

   private void findLocalKeyStoreDataByCertificateID(Object certificateID, SecureEmailKeyStoreData[] keysFound, KeyStore keyStore) {
      KeyStoreIndex certificateIDKeyStoreIndex = this.getCertificateIDKeyStoreIndex();
      if (certificateIDKeyStoreIndex != null) {
         keyStore.addIndex(certificateIDKeyStoreIndex);
         Enumeration enumeration = keyStore.elements(certificateIDKeyStoreIndex.getID(), certificateID);

         while (enumeration.hasMoreElements()) {
            SecureEmailKeyStoreData newData = new SecureEmailKeyStoreData((KeyStoreData)enumeration.nextElement());
            Arrays.add(keysFound, newData);
         }
      }
   }

   protected KeyStoreIndex getCertificateIDKeyStoreIndex() {
      throw null;
   }

   public String getSender(EmailMessageModel message) {
      int numModels = message.size();
      String[] senderAddressAndName = new Object[2];

      for (int j = 0; j < numModels; j++) {
         Object submember = message.getAt(j);
         if (submember instanceof Object) {
            EmailHeaderModel emailHeaderModel = (EmailHeaderModel)submember;
            if (emailHeaderModel.getHeaderType() == 3 || emailHeaderModel.getHeaderType() == 4) {
               ConversionProvider converter = emailHeaderModel;
               converter.convert(null, senderAddressAndName);
               return senderAddressAndName[0];
            }
         }
      }

      return null;
   }

   public static String getLabelOrFriendlyName(Certificate certificate, KeyStore keyStore) {
      keyStore.addIndex((KeyStoreIndex)(new Object()));
      Enumeration enumeration = keyStore.elements(-2038609988711824737L, certificate);
      return enumeration.hasMoreElements() ? ((KeyStoreData)enumeration.nextElement()).getLabel() : certificate.getSubjectFriendlyName();
   }

   public static boolean isCancelException(Exception e) {
      return e instanceof Object || e instanceof Object && ((CryptoIOException)e).getCryptoException() instanceof Object;
   }

   public static Object[] extractSecureEmailAttachments(EmailMessageModel emailMessageModel) {
      return SubmemberUtilities.getSubmembers(emailMessageModel, new SecureEmailUtilities$SecureEmailAttachmentRecognizer());
   }

   public LDAPCertificateFetch getLDAPCertificateFetch() {
      throw null;
   }

   public boolean isSignatureRequired() {
      throw null;
   }

   public boolean isEncryptionRequired() {
      throw null;
   }
}
