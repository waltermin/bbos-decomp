package net.rim.device.apps.internal.secureemail.sendmethods;

import net.rim.device.api.crypto.CryptoSystemProperties;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.keystore.TrustedKeyStore;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.device.apps.internal.ldap.LDAPBrowserContextFactory;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.apps.internal.secureemail.encodings.SecureEmailEncodingManager;
import net.rim.device.apps.internal.secureemail.server.SecureEmailCertificateServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyConflictException;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.RichTextFieldUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public class MessageEncoderLocator {
   private static final boolean DEBUG = false;
   private static final int PROBLEM_NO_CERTS = 0;
   private static final int PROBLEM_NO_ALLOWED_CERTS = 1;
   private static final int PROBLEM_UNVERIFIED = 2;
   private static final int PROBLEM_REVOKED = 3;
   private static final int PROBLEM_UNTRUSTED = 4;
   private static final int PROBLEM_WEAK = 5;
   private static final int PROBLEM_INVALID = 6;
   private static final int PROBLEM_STALE_STATUS = 7;
   private static final int NUM_PROBLEMS = 8;

   private MessageEncoderLocator() {
   }

   public static MessageEncoder locateMessageEncoder(
      EmailMessageModel emailMessageModel,
      SecureEmailPolicyServer[] secureEmailPolicyServers,
      RecipientData[] messageRecipientData,
      SendMethod selectedSendMethod,
      Object context
   ) {
      boolean noviceMode = true;
      long selectedEncodingUID = -1;
      int selectedEncodingAction = 0;
      if (selectedSendMethod instanceof MessageEncoderProvider) {
         MessageEncoderProvider messageEncoderProvider = (MessageEncoderProvider)selectedSendMethod;
         selectedEncodingUID = selectedSendMethod.getEncodingUID();
         selectedEncodingAction = selectedSendMethod.getEncodingAction();
         noviceMode = false;
      }

      int[] encodingActions = getEncodingActions(
         emailMessageModel, selectedSendMethod.getServiceRecord(), messageRecipientData, secureEmailPolicyServers, selectedEncodingAction
      );
      Certificate[] additionalCertificates = new Certificate[0];
      MessageEncoderProvider messageEncoderProvider = locateMessageEncoderProvider_UserSelectedEncoding(
         emailMessageModel, messageRecipientData, additionalCertificates, selectedEncodingUID, encodingActions, noviceMode, context
      );
      if (messageEncoderProvider == null) {
         messageEncoderProvider = locateMessageEncoderProvider_AutomaticEncoding(
            emailMessageModel, messageRecipientData, additionalCertificates, secureEmailPolicyServers, encodingActions, noviceMode, context
         );
      }

      if (messageEncoderProvider == null) {
         BackgroundDialog.showMessage(SecureEmailResources.getString(166));
         throw new AbortSendSecureEmailException();
      } else {
         return messageEncoderProvider.getMessageEncoder(emailMessageModel, messageRecipientData, additionalCertificates, context);
      }
   }

   private static int[] getEncodingActions(
      EmailMessageModel emailMessageModel,
      ServiceRecord serviceRecord,
      RecipientData[] messageRecipientData,
      SecureEmailPolicyServer[] secureEmailPolicyServers,
      int selectedEncodingAction
   ) {
      int[] encodingActions = new int[2];
      int numSecureEmailPolicyServers = secureEmailPolicyServers.length;
      if (numSecureEmailPolicyServers > 0) {
         for (int i = 0; i < numSecureEmailPolicyServers; i++) {
            int[] currentEncodingActions = secureEmailPolicyServers[i]
               .getEncodingActions(emailMessageModel, serviceRecord, messageRecipientData, selectedEncodingAction);
            encodingActions[0] |= currentEncodingActions[0];
            encodingActions[1] |= currentEncodingActions[1];
         }
      } else {
         encodingActions[0] = selectedEncodingAction;
         encodingActions[1] = selectedEncodingAction & -3;
      }

      return encodingActions;
   }

   private static String actionToString(int encodingAction) {
      switch (encodingAction) {
         case -1:
            return "Unrecognized";
         case 0:
         default:
            return "None";
         case 1:
            return "Sign";
         case 2:
            return "Encrypt";
         case 3:
            return "Sign & Encrypt";
      }
   }

   private static String encodingToString(long encodingUID) {
      if (encodingUID == 182808770805039415L) {
         return "Plain Text";
      } else if (encodingUID == 3681505275764314063L) {
         return "PGP";
      } else if (encodingUID == 5942148136637320404L) {
         return "S/MIME";
      } else {
         return encodingUID == -1 ? "Unspecified" : "Unrecognized";
      }
   }

   private static MessageEncoderProvider locateMessageEncoderProvider_UserSelectedEncoding(
      EmailMessageModel emailMessageModel,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      long selectedEncodingUID,
      int[] encodingActions,
      boolean noviceMode,
      Object context
   ) {
      int preferredAction = encodingActions[0];
      MessageEncoderProvider preferredMessageEncoderProvider = findMessageEncoderProvider_UserSelectedEncoding(
         emailMessageModel, selectedEncodingUID, preferredAction, context
      );
      if (preferredMessageEncoderProvider != null && preferredMessageEncoderProvider.requiresCertificates(emailMessageModel)) {
         int alternateAction = encodingActions[1];
         MessageEncoderProvider alternateMessageEncoderProvider = findMessageEncoderProvider_UserSelectedEncoding(
            emailMessageModel, selectedEncodingUID, alternateAction, context
         );
         if (alternateMessageEncoderProvider != null && alternateMessageEncoderProvider.requiresCertificates(emailMessageModel)) {
            alternateMessageEncoderProvider = null;
         }

         return processRecipientDataCertificates(
            emailMessageModel,
            messageRecipientData,
            additionalCertificates,
            preferredMessageEncoderProvider,
            alternateMessageEncoderProvider,
            noviceMode,
            context
         );
      } else {
         return preferredMessageEncoderProvider;
      }
   }

   private static MessageEncoderProvider findMessageEncoderProvider_UserSelectedEncoding(
      EmailMessageModel emailMessageModel, long encodingUID, int encodingAction, Object context
   ) {
      if (encodingAction == 0) {
         encodingUID = 182808770805039415L;
      }

      TransitoryMessagePropertiesModel messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
      ChoiceField messageEncodingField = messagePropertiesModel.getMessageEncodingField();
      int numSendMethods = messageEncodingField.getSize();

      for (int i = 0; i < numSendMethods; i++) {
         SendMethod currentSendMethod = (SendMethod)messageEncodingField.getChoice(i);
         if (currentSendMethod instanceof MessageEncoderProvider
            && currentSendMethod.getEncodingUID() == encodingUID
            && currentSendMethod.getEncodingAction() == encodingAction
            && currentSendMethod.isConfigured(emailMessageModel, context)) {
            return (MessageEncoderProvider)currentSendMethod;
         }
      }

      return null;
   }

   private static MessageEncoderProvider processRecipientDataCertificates(
      EmailMessageModel emailMessageModel,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      MessageEncoderProvider preferredMessageEncoderProvider,
      MessageEncoderProvider unencryptedMessageEncoderProvider,
      boolean noviceMode,
      Object context
   ) throws ReprocessRecipientsException {
      if (!noviceMode) {
         TransitoryMessagePropertiesModel messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
         ChoiceField messageEncodingField = messagePropertiesModel.getMessageEncodingField();
         synchronized (Application.getEventLock()) {
            messageEncodingField.setSelectedIndex(preferredMessageEncoderProvider);
         }
      }

      int unavailableUserOptions = 0;
      SecureEmailServerManager secureEmailServerManager = SecureEmailServerManager.getInstance();
      SecureEmailFactory preferredSecureEmailFactory = preferredMessageEncoderProvider.getSecureEmailFactory();
      ServiceRecord preferredServiceRecord = preferredMessageEncoderProvider.getServiceRecord();
      SecureEmailCertificateServer[] secureEmailCertificateServers = secureEmailServerManager.getCertificateServers(preferredServiceRecord);
      if (secureEmailCertificateServers.length > 0 || LDAPBrowserContextFactory.getContext(preferredSecureEmailFactory.getLDAPBrowserContextString()) == null) {
         unavailableUserOptions |= 256;
      }

      if (unencryptedMessageEncoderProvider == null) {
         unavailableUserOptions |= 16;
         unavailableUserOptions |= 32;
         unavailableUserOptions |= 64;
      } else {
         SecureEmailPolicyServer[] secureEmailPolicyServers = SecureEmailServerManager.getInstance()
            .getPolicyServers(preferredMessageEncoderProvider.getServiceRecord());
         int unencryptedAction = unencryptedMessageEncoderProvider.getEncodingAction();
         if (unencryptedAction == 1) {
            unavailableUserOptions |= 16;
            unavailableUserOptions |= 64;
         } else if (secureEmailPolicyServers.length > 0) {
            unavailableUserOptions |= 16;
            unavailableUserOptions |= 32;
         } else {
            unavailableUserOptions |= 32;
            unavailableUserOptions |= 64;
         }
      }

      Certificate senderEncryptionCertificate = preferredMessageEncoderProvider.obtainSenderEncryptionCertificate(emailMessageModel);
      if (senderEncryptionCertificate == null) {
         String[] containerStringLowerSingularArray = new String[]{preferredSecureEmailFactory.getPublicKeyContainerString(false, false)};
         String noEncryptionString = MessageFormat.format(SecureEmailResources.getString(93), containerStringLowerSingularArray);
         int noEncryptionCertOptions = 117;
         if (SubmemberUtilities.getFirstSubmember(emailMessageModel, preferredSecureEmailFactory.getOptionsRecognizer()) == null) {
            noEncryptionCertOptions |= 512;
         }

         ProblemRecipientData noEncryptionCertProblem = new ProblemRecipientData(noEncryptionString, noEncryptionCertOptions, preferredSecureEmailFactory);
         noEncryptionCertProblem.promptUser(0, unavailableUserOptions, context);
      } else {
         Arrays.add(additionalCertificates, senderEncryptionCertificate);
      }

      ProblemRecipientData[] problemRecipientData = processRecipientDataCertificates(preferredMessageEncoderProvider, messageRecipientData);
      int numProblemRecipientData = problemRecipientData.length;

      for (int i = 0; i < numProblemRecipientData; i++) {
         int problemRecipientPromptType = noviceMode ? 2 : 1;
         int promptAction = problemRecipientData[i].promptUser(problemRecipientPromptType, unavailableUserOptions, context);
         if (promptAction == 1) {
            return unencryptedMessageEncoderProvider;
         }

         if (promptAction == 2) {
            throw new ReprocessRecipientsException();
         }
      }

      return preferredMessageEncoderProvider;
   }

   private static ProblemRecipientData[] processRecipientDataCertificates(
      MessageEncoderProvider selectedMessageEncoderProvider, RecipientData[] messageRecipientData
   ) {
      ProblemRecipientData[] problemRecipientData = createProblemRecipientData(selectedMessageEncoderProvider);
      int numMessageRecipientData = messageRecipientData != null ? messageRecipientData.length : 0;

      for (int i = 0; i < numMessageRecipientData; i++) {
         processRecipientDataCertificates(selectedMessageEncoderProvider, messageRecipientData[i], problemRecipientData);
      }

      return problemRecipientData;
   }

   private static void processRecipientDataCertificates(
      MessageEncoderProvider selectedMessageEncoderProvider, RecipientData recipientData, ProblemRecipientData[] problemRecipientData
   ) {
      RecipientData$CertificateDetails[] recommendedCertificates = recipientData.getRecommendedCertificates();
      obtainSuitableCertificates(selectedMessageEncoderProvider, recommendedCertificates);
      if (recommendedCertificates != null && recommendedCertificates.length > 0) {
         recipientData.setSelectedCertificates(recommendedCertificates);
      } else {
         RecipientData$CertificateDetails[] allowedCertificates = recipientData.getAllowedCertificates();
         obtainSuitableCertificates(selectedMessageEncoderProvider, allowedCertificates);
         if (allowedCertificates != null && allowedCertificates.length > 0) {
            RecipientData$CertificateDetails[] selectedCertificates = promptUserToSelectCertificates(
               selectedMessageEncoderProvider, recipientData.getName(), allowedCertificates
            );
            int numSelectedCertificates = selectedCertificates.length;

            for (int i = 0; i < numSelectedCertificates; i++) {
               determineCertificateProblems(recipientData, selectedCertificates[i], problemRecipientData);
            }

            recipientData.setSelectedCertificates(selectedCertificates);
         } else {
            RecipientData$CertificateDetails[] allCertificates = recipientData.getAllCertificates();
            obtainSuitableCertificates(selectedMessageEncoderProvider, allCertificates);
            if (allCertificates != null && allCertificates.length > 0) {
               problemRecipientData[1].addRecipient(recipientData);
            } else {
               problemRecipientData[0].addRecipient(recipientData);
            }

            recipientData.setSelectedCertificates(null);
         }
      }
   }

   private static void obtainSuitableCertificates(MessageEncoderProvider messageEncoderProvider, RecipientData$CertificateDetails[] certificates) {
      int numCertificates = certificates != null ? certificates.length : 0;

      for (int i = numCertificates - 1; i >= 0; i--) {
         Certificate suitableCertificate = messageEncoderProvider.obtainSuitableRecipientCertificate(certificates[i].getCertificate());
         if (suitableCertificate != null) {
            certificates[i].setCertificate(suitableCertificate);
         } else {
            Arrays.removeAt(certificates, i);
         }
      }
   }

   private static RecipientData$CertificateDetails[] promptUserToSelectCertificates(
      MessageEncoderProvider selectedMessageEncoderProvider, String recipientNames, RecipientData$CertificateDetails[] allowedCertificates
   ) {
      int numAllowedCertificates = allowedCertificates.length;
      if (numAllowedCertificates == 1) {
         return allowedCertificates;
      }

      SecureEmailFactory secureEmailFactory = selectedMessageEncoderProvider.getSecureEmailFactory();
      String[] labels = new String[numAllowedCertificates];
      Certificate[] certificates = new Certificate[numAllowedCertificates];

      for (int i = 0; i < numAllowedCertificates; i++) {
         labels[i] = allowedCertificates[i].getCertificateLabel();
         certificates[i] = allowedCertificates[i].getCertificate();
      }

      while (true) {
         String selectCertificatesPrompt = MessageFormat.format(SecureEmailResources.getString(105), new String[]{recipientNames});
         int[] selections = CertificateUtilities.selectCertificates(
            RichTextFieldUtilities.getBoldFormattedRichTextField(selectCertificatesPrompt),
            labels,
            certificates,
            new int[0],
            secureEmailFactory.getPreferredKeyStore(),
            TrustedKeyStore.getInstance(),
            (CryptoSystemProperties)secureEmailFactory.getCryptoSystemProperties()
         );
         if (selections == null) {
            throw new AbortSendSecureEmailException();
         }

         int numSelections = selections.length;
         if (numSelections != 0) {
            RecipientData$CertificateDetails[] selectedCertificates = new RecipientData$CertificateDetails[numSelections];

            for (int j = 0; j < numSelections; j++) {
               selectedCertificates[j] = allowedCertificates[selections[j]];
            }

            return selectedCertificates;
         }

         SimpleChoiceDialog dialog = new SimpleChoiceDialog(SecureEmailResources.getString(104), CommonResource.getStringArray(10004), 0, null, 134217728);
         BackgroundDialog.show(dialog);
      }
   }

   private static void determineCertificateProblems(
      RecipientData recipientData, RecipientData$CertificateDetails recipientCertificateDetails, ProblemRecipientData[] problemRecipientDataArray
   ) {
      long properties = recipientCertificateDetails.getCertificateChainProperties();
      if ((properties & 22) != 0) {
         problemRecipientDataArray[2].addRecipient(recipientData);
      }

      if ((properties & 1024) != 0) {
         problemRecipientDataArray[3].addRecipient(recipientData);
      }

      if ((properties & 32) != 0) {
         problemRecipientDataArray[5].addRecipient(recipientData);
      }

      if ((properties & 256) != 0) {
         problemRecipientDataArray[6].addRecipient(recipientData);
      }

      if ((properties & 8) != 0) {
         problemRecipientDataArray[4].addRecipient(recipientData);
      }

      if ((properties & 2048) != 0) {
         problemRecipientDataArray[7].addRecipient(recipientData);
      }
   }

   private static ProblemRecipientData[] createProblemRecipientData(MessageEncoderProvider selectedMessageEncoderProvider) {
      SecureEmailFactory secureEmailFactory = selectedMessageEncoderProvider.getSecureEmailFactory();
      String[] containerStringLowerSingularArray = new String[]{secureEmailFactory.getPublicKeyContainerString(false, false)};
      String[] containerChainStringLowerSingularArray = new String[]{secureEmailFactory.getPublicKeyContainerChainString(false)};
      ProblemRecipientData[] problemRecipientDataArray = new ProblemRecipientData[8];
      String noCertsString = MessageFormat.format(SecureEmailResources.getString(96), containerStringLowerSingularArray);
      int noCertsUserOptions = 371;
      problemRecipientDataArray[0] = new ProblemRecipientData(noCertsString, noCertsUserOptions, secureEmailFactory);
      String noAllowedCertsString = MessageFormat.format(SecureEmailResources.getString(135), containerChainStringLowerSingularArray);
      int noAllowedCertsUserOptions = 371;
      problemRecipientDataArray[1] = new ProblemRecipientData(noAllowedCertsString, noAllowedCertsUserOptions, secureEmailFactory);
      String unverifiedString = MessageFormat.format(SecureEmailResources.getString(149), containerChainStringLowerSingularArray);
      int unverifiedUserOptions = 371;
      problemRecipientDataArray[2] = new ProblemRecipientData(unverifiedString, unverifiedUserOptions, secureEmailFactory);
      String revokedString = MessageFormat.format(SecureEmailResources.getString(98), containerChainStringLowerSingularArray);
      int revokedUserOptions = 323;
      if (!ITPolicy.getBoolean(24, 4, false)) {
         revokedUserOptions |= 8;
      }

      problemRecipientDataArray[3] = new ProblemRecipientData(revokedString, revokedUserOptions, secureEmailFactory);
      String untrustedString = MessageFormat.format(SecureEmailResources.getString(100), containerChainStringLowerSingularArray);
      int untrustedUserOptions = 323;
      if (!ITPolicy.getBoolean(24, 3, false)) {
         untrustedUserOptions |= 8;
      }

      problemRecipientDataArray[4] = new ProblemRecipientData(untrustedString, untrustedUserOptions, secureEmailFactory);
      String weakString = MessageFormat.format(SecureEmailResources.getString(102), containerChainStringLowerSingularArray);
      int weakUserOptions = 323;
      if (!ITPolicy.getBoolean(24, 23, false)) {
         weakUserOptions |= 8;
      }

      problemRecipientDataArray[5] = new ProblemRecipientData(weakString, weakUserOptions, secureEmailFactory);
      String invalidString = MessageFormat.format(SecureEmailResources.getString(94), containerChainStringLowerSingularArray);
      int invalidUserOptions = 323;
      if (!ITPolicy.getBoolean(24, 22, false)) {
         invalidUserOptions |= 8;
      }

      problemRecipientDataArray[6] = new ProblemRecipientData(invalidString, invalidUserOptions, secureEmailFactory);
      String staleString = MessageFormat.format(SecureEmailResources.getString(120), containerChainStringLowerSingularArray);
      int staleUserOptions = 451;
      if (!ITPolicy.getBoolean(24, 34, false)) {
         staleUserOptions |= 8;
      }

      problemRecipientDataArray[7] = new ProblemRecipientData(staleString, staleUserOptions, secureEmailFactory);
      return problemRecipientDataArray;
   }

   private static MessageEncoderProvider locateMessageEncoderProvider_AutomaticEncoding(
      EmailMessageModel emailMessageModel,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      SecureEmailPolicyServer[] secureEmailPolicyServers,
      int[] encodingActions,
      boolean noviceMode,
      Object context
   ) {
      int preferredAction = encodingActions[0];
      MessageEncoderProvider preferredMessageEncoderProvider = findMessageEncoderProvider_AutomaticEncoding(
         emailMessageModel, messageRecipientData, additionalCertificates, secureEmailPolicyServers, preferredAction, context
      );
      if (preferredMessageEncoderProvider != null && preferredMessageEncoderProvider.requiresCertificates(emailMessageModel)) {
         int alternateAction = encodingActions[1];
         MessageEncoderProvider alternateMessageEncoderProvider = findMessageEncoderProvider_AutomaticEncoding(
            emailMessageModel, messageRecipientData, additionalCertificates, secureEmailPolicyServers, alternateAction, context
         );
         if (alternateMessageEncoderProvider != null && alternateMessageEncoderProvider.requiresCertificates(emailMessageModel)) {
            alternateMessageEncoderProvider = null;
         }

         return processRecipientDataCertificates(
            emailMessageModel,
            messageRecipientData,
            additionalCertificates,
            preferredMessageEncoderProvider,
            alternateMessageEncoderProvider,
            noviceMode,
            context
         );
      } else {
         return preferredMessageEncoderProvider;
      }
   }

   private static MessageEncoderProvider findMessageEncoderProvider_AutomaticEncoding(
      EmailMessageModel emailMessageModel,
      RecipientData[] messageRecipientData,
      Certificate[] additionalCertificates,
      SecureEmailPolicyServer[] secureEmailPolicyServers,
      int encodingActions,
      Object context
   ) {
      SecureEmailEncodingManager secureEmailEncodingManager = SecureEmailEncodingManager.getInstance();
      TransitoryMessagePropertiesModel messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
      ChoiceField messageEncodingField = messagePropertiesModel.getMessageEncodingField();
      int bestAction = -1;
      long bestActionPreferredEncodingUID = -1;
      int preferredMethodIndex = -1;
      int highestPriorityMethodIndex = -1;
      int highestPriorityMethodPriority = -1;
      int numOtherSendMethods = messageEncodingField.getSize();

      for (int i = 0; i < numOtherSendMethods; i++) {
         SendMethod currentSendMethod = (SendMethod)messageEncodingField.getChoice(i);
         int currentEncodingAction = currentSendMethod.getEncodingAction();
         if (currentSendMethod instanceof MessageEncoderProvider
            && currentSendMethod.isConfigured(emailMessageModel, context)
            && (~currentEncodingAction & encodingActions) == 0) {
            int actionComparisonResult = compareEncodingActions(currentEncodingAction, bestAction);
            if (actionComparisonResult >= 0) {
               if (actionComparisonResult > 0) {
                  bestAction = currentEncodingAction;

                  try {
                     bestActionPreferredEncodingUID = determinePreferredEncodingUID(
                        emailMessageModel, currentSendMethod.getServiceRecord(), secureEmailPolicyServers, messageRecipientData, currentEncodingAction
                     );
                  } catch (SecureEmailPolicyConflictException e) {
                     bestActionPreferredEncodingUID = -1;
                  }

                  preferredMethodIndex = -1;
                  highestPriorityMethodIndex = -1;
                  highestPriorityMethodPriority = -1;
               }

               long currentEncodingUID = currentSendMethod.getEncodingUID();
               if (currentEncodingUID == bestActionPreferredEncodingUID) {
                  preferredMethodIndex = i;
               }

               int currentEncodingPriority = secureEmailEncodingManager.getEncodingPriority(currentSendMethod.getEncodingUID());
               if (currentEncodingPriority > highestPriorityMethodPriority) {
                  highestPriorityMethodIndex = i;
                  highestPriorityMethodPriority = currentEncodingPriority;
               }
            }
         }
      }

      int bestMethodIndex = preferredMethodIndex >= 0 ? preferredMethodIndex : highestPriorityMethodIndex;
      return bestMethodIndex < 0 ? null : (MessageEncoderProvider)messageEncodingField.getChoice(bestMethodIndex);
   }

   private static long determinePreferredEncodingUID(
      EmailMessageModel emailMessageModel,
      ServiceRecord serviceRecord,
      SecureEmailPolicyServer[] secureEmailPolicyServers,
      RecipientData[] messageRecipientData,
      int requiredAction
   ) throws SecureEmailPolicyConflictException {
      long preferredEncodingUID = -1;

      for (SecureEmailPolicyServer currentPolicyServer : secureEmailPolicyServers) {
         long currentPreferredEncodingUID = currentPolicyServer.getPreferredEncodingUID(emailMessageModel, serviceRecord, messageRecipientData, requiredAction);
         if (preferredEncodingUID != -1 && preferredEncodingUID != currentPreferredEncodingUID) {
            throw new SecureEmailPolicyConflictException();
         }

         preferredEncodingUID = currentPreferredEncodingUID;
      }

      return preferredEncodingUID;
   }

   private static int compareEncodingActions(int action1, int action2) {
      if (action1 == action2) {
         return 0;
      } else if (action1 < 0) {
         return -1;
      } else if (action2 < 0) {
         return 1;
      } else {
         return action1 < action2 ? 1 : -1;
      }
   }
}
