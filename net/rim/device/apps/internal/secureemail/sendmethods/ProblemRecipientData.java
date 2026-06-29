package net.rim.device.apps.internal.secureemail.sendmethods;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailEditorScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.device.apps.internal.ldap.LDAPBrowser;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.RecipientData$CertificateDetails;
import net.rim.device.apps.internal.secureemail.SecureEmailFactory;
import net.rim.device.apps.internal.secureemail.SecureEmailOptionsItem;
import net.rim.device.apps.internal.secureemail.SecureEmailResources;
import net.rim.device.internal.ui.RichTextFieldUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.vm.Array;

public class ProblemRecipientData {
   private Vector _problemRecipients;
   private String _warningMessage;
   private int _userOptions;
   private SecureEmailFactory _secureEmailFactory;
   private String[] _userOptionStringArray;
   public static final int PROMPT_ACTION_SEND_ENCRYPTED;
   public static final int PROMPT_ACTION_SEND_UNENCRYPTED;
   public static final int PROMPT_ACTION_REPROCESS_RECIPIENTS;
   public static final int PROMPT_TYPE_ALWAYS;
   public static final int PROMPT_TYPE_ONLY_ON_PROBLEM;
   public static final int PROMPT_TYPE_ALLOW_SILENT_DOWNGRADE;
   public static final int NUM_USER_OPTIONS;
   public static final int USER_OPTION_DO_NOT_SEND;
   public static final int USER_OPTION_REMOVE_RECIPIENTS;
   public static final int USER_OPTION_SEND_ENCRYPTED;
   public static final int USER_OPTION_SEND_ANYWAY;
   public static final int USER_OPTION_SEND_UNENCRYPTED;
   public static final int USER_OPTION_SEND_SIGNED_ONLY;
   public static final int USER_OPTION_SEND_TO_SERVER;
   public static final int USER_OPTION_FETCH_STATUS;
   public static final int USER_OPTION_FETCH_CERTIFICATES;
   public static final int USER_OPTION_CONFIGURE_SECURE_EMAIL;

   public ProblemRecipientData(String warningMessage, int userOptions, SecureEmailFactory secureEmailFactory) {
      this._warningMessage = warningMessage;
      this._userOptions = userOptions;
      this._secureEmailFactory = secureEmailFactory;
   }

   public void addRecipient(Object recipient) {
      if (this._problemRecipients == null) {
         this._problemRecipients = (Vector)(new Object());
      }

      this._problemRecipients.addElement(recipient);
   }

   public int promptUser(int promptType, int unavailableUserOptions, Object context) {
      this._userOptions &= ~unavailableUserOptions;
      switch (promptType) {
         case 0:
            break;
         case 1:
         default:
            if (this._problemRecipients == null) {
               return 0;
            }
            break;
         case 2:
            if (this._problemRecipients == null) {
               return 0;
            }

            if ((this._userOptions & 112) != 0) {
               return 1;
            }
      }

      TransitoryMessagePropertiesModel messagePropertiesModel = (TransitoryMessagePropertiesModel)ContextObject.get(context, 32241034113959076L);
      EmailMessageModel message = messagePropertiesModel.getEmailMessageModel();
      if (!this.removeRecipientsAvailable(this._problemRecipients, message)) {
         this._userOptions &= -3;
      }

      if (this._problemRecipients != null) {
         StringBuffer buffer = (StringBuffer)(new Object(this._warningMessage));
         this.formatRecipientList(this._problemRecipients, buffer);
         this._warningMessage = buffer.toString();
      }

      RichTextField warningMessageField = RichTextFieldUtilities.getBoldFormattedRichTextField(this._warningMessage, 36028797018963968L);
      String[] userOptionLabels = this.getUserOptionStringArray();
      String[] choices = new Object[10];
      int choiceIndex = 0;

      for (int i = 0; i < 10; i++) {
         int currentBit = 1 << i;
         if ((this._userOptions & currentBit) != 0) {
            choices[choiceIndex++] = userOptionLabels[i];
         }
      }

      Array.resize(choices, choiceIndex);
      SimpleChoiceDialog scd = (SimpleChoiceDialog)(new Object(warningMessageField, choices, 0, null, 134217728));
      scd.setCancelAllowed(true);
      BackgroundDialog.show(scd);
      if (scd.getCloseReason() == -1) {
         throw new AbortSendSecureEmailException();
      }

      int selectedIndex = scd.getSelectedIndex();
      int numSetBits = 0;
      int currentBit = 0;

      for (int i = 0; i < 10; i++) {
         currentBit = 1 << i;
         if ((this._userOptions & currentBit) != 0 && numSetBits++ == selectedIndex) {
            break;
         }
      }

      switch (currentBit) {
         case 2:
            EmailEditorScreen screen = (EmailEditorScreen)ContextObject.get(context, -6581931217101110672L);
            this.removeRecipients(this._problemRecipients, message, screen);
            throw new AbortSendSecureEmailException();
         case 4:
         case 8:
            return 0;
         case 16:
         case 32:
         case 64:
            return 1;
         case 128:
            this.manualFetchStatus(this._problemRecipients);
            return 2;
         case 256:
            this.manualFetchCertificates(this._problemRecipients);
            return 2;
         case 512:
            ((SecureEmailOptionsItem)this._secureEmailFactory.createOptionsItem()).doBlocking(context);
            throw new AbortSendSecureEmailException();
         default:
            throw new AbortSendSecureEmailException();
      }
   }

   private boolean removeRecipientsAvailable(Vector recipientData, EmailMessageModel message) {
      int numRecipientData = recipientData != null ? recipientData.size() : 0;

      for (int i = 0; i < numRecipientData; i++) {
         RecipientData currentRecipientData = (RecipientData)recipientData.elementAt(i);
         EmailHeaderModel currentEmailHeaderModel = currentRecipientData.getEmailHeaderModel();
         if (message.contains(currentEmailHeaderModel)) {
            return true;
         }
      }

      return false;
   }

   private void removeRecipients(Vector recipientData, EmailMessageModel message, EmailEditorScreen screen) {
      int numRecipientData = recipientData != null ? recipientData.size() : 0;

      for (int i = 0; i < numRecipientData; i++) {
         RecipientData currentRecipientData = (RecipientData)recipientData.elementAt(i);
         EmailHeaderModel currentEmailHeaderModel = currentRecipientData.getEmailHeaderModel();
         if (message.contains(currentEmailHeaderModel)) {
            message.remove(currentEmailHeaderModel);
            synchronized (screen.getApplication().getAppEventLock()) {
               screen.deleteModel(currentEmailHeaderModel);
               screen.handleRecipientRemoved(currentEmailHeaderModel);
            }
         }
      }
   }

   private void formatRecipientList(Vector recipientData, StringBuffer buffer) {
      Enumeration enumeration = recipientData.elements();

      while (enumeration.hasMoreElements()) {
         buffer.append(SecureEmailResources.getString(168));
         Object o = enumeration.nextElement();
         if (o instanceof Object) {
            buffer.append((String)o);
         } else if (o instanceof RecipientData) {
            RecipientData currentRecipientData = (RecipientData)o;
            String currentRecipientDataString;
            switch (currentRecipientData.getType()) {
               case 0:
                  currentRecipientDataString = currentRecipientData.getName();
                  break;
               case 1:
               default:
                  currentRecipientDataString = MessageFormat.format(SecureEmailResources.getString(169), new Object[]{currentRecipientData.getName()});
                  break;
               case 2:
                  currentRecipientDataString = MessageFormat.format(SecureEmailResources.getString(170), new Object[]{currentRecipientData.getName()});
            }

            buffer.append(currentRecipientDataString);
         }
      }
   }

   protected void manualFetchCertificates(Vector recipientsToFetch) {
      StringBuffer emailsToFetch = (StringBuffer)(new Object());
      Enumeration enumeration = recipientsToFetch.elements();

      while (enumeration.hasMoreElements()) {
         Object o = enumeration.nextElement();
         if (o instanceof Object) {
            String string = (String)o;
            if (emailsToFetch.length() > 0) {
               emailsToFetch.append(';');
            }

            emailsToFetch.append(string);
         } else if (o instanceof RecipientData) {
            RecipientData data = (RecipientData)o;
            String[] emails = data.getAddresses();
            int numEmails = emails.length;

            for (int i = 0; i < numEmails; i++) {
               if (emailsToFetch.length() > 0) {
                  emailsToFetch.append(';');
               }

               emailsToFetch.append(emails[i]);
            }
         }
      }

      LDAPBrowser browser = (LDAPBrowser)(new Object(this._secureEmailFactory.getLDAPBrowserContextString(), null, null, null, emailsToFetch.toString()));
      browser.open(true);
   }

   private void manualFetchStatus(Vector recipientsToFetch) {
      Enumeration enumeration = recipientsToFetch.elements();

      while (enumeration.hasMoreElements()) {
         RecipientData data = (RecipientData)enumeration.nextElement();
         RecipientData$CertificateDetails[] selectedCertificates = data.getSelectedCertificates();

         for (int i = 0; i < selectedCertificates.length; i++) {
            Certificate[] certificateChain = selectedCertificates[i].getCertificateChain();
            boolean extendedCheckingAvailable = CertificateStatusProvider.queryStatusAvailability(certificateChain, true);
            CertificateStatusRequest request = (CertificateStatusRequest)(new Object(
               certificateChain, extendedCheckingAvailable, this._secureEmailFactory.getPreferredKeyStore(), null, null
            ));
            CertificateStatusProvider.requestCertificateStatus(request, null, true, true);
         }
      }
   }

   private String[] getUserOptionStringArray() {
      if (this._userOptionStringArray == null) {
         this._userOptionStringArray = new Object[10];
         String[] temp = SecureEmailResources.getStringArray(153);

         for (int i = 0; i < 10; i++) {
            this._userOptionStringArray[i] = temp[i];
         }

         String[] containerStringLowerPluralArray = new Object[]{this._secureEmailFactory.getPublicKeyContainerString(false, true)};
         this._userOptionStringArray[8] = MessageFormat.format(this._userOptionStringArray[8], containerStringLowerPluralArray);
         this._userOptionStringArray[9] = MessageFormat.format(this._userOptionStringArray[9], new Object[]{this._secureEmailFactory.getEncodingString()});
      }

      return this._userOptionStringArray;
   }
}
