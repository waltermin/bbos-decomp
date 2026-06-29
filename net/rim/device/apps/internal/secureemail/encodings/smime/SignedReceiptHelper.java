package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.crypto.cms.CMSEntityIdentifier;
import net.rim.device.api.crypto.cms.CMSReceiptData;
import net.rim.device.api.crypto.cms.CMSReceiptRequest;
import net.rim.device.api.crypto.cms.CMSSignedDataInputStream;
import net.rim.device.api.crypto.cms.CMSSignedDataOutputStream;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.secureemail.cache.CachedManager;
import net.rim.device.apps.internal.secureemail.encodings.smime.cache.CachedSignedReceiptField;
import net.rim.device.internal.ui.component.PleaseWaitDialog;
import net.rim.vm.Memory;

public final class SignedReceiptHelper {
   public static final void processSignedReceipt(CMSSignedDataInputStream signedStream, CachedManager cachedManager, SMIMEBodyModel smimeBodyModel) {
      if (signedStream.isSignedReceipt()) {
         CMSReceiptData[] receiptData = SignedReceiptCache.getReceipts();
         cachedManager.addField(new CachedSignedReceiptField(signedStream.setReceiptData(receiptData)));
         if (!Memory.isObjectInGroup(smimeBodyModel)) {
            smimeBodyModel.setIsSignedReceipt();
         }
      }
   }

   private static final String getDeviceAddress(boolean isPIN, ServiceRecord serviceRecord) {
      if (isPIN) {
         return EmailSendUtility.getDevicePINString();
      } else {
         return serviceRecord == null ? CMIMEUtilities.getEmailAddress() : CMIMEUtilities.getEmailAddress(serviceRecord);
      }
   }

   public static final boolean processSignedReceiptRequest(CMSSignedDataInputStream signedStream, EmailMessageModel receivedModel) {
      if (signedStream == null || receivedModel == null) {
         return false;
      }

      if (!receivedModel.flagsSet(16384) && receivedModel.inbound()) {
         ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(receivedModel);
         boolean isPIN = receivedModel.flagsSet(8192);
         String address = getDeviceAddress(isPIN, serviceRecord);
         if (address == null) {
            return true;
         }

         CMSEntityIdentifier[] signers = null;

         label185:
         try {
            signers = signedStream.getSigners();
         } finally {
            break label185;
         }

         if (signers == null) {
            return false;
         }

         int numSigners = signers.length;
         CMSEntityIdentifier[] signersRequestingReceipts = null;
         int numSignersRequestingReceipts = 0;
         boolean signatureVerified = false;

         for (int i = 0; i < numSigners; i++) {
            CMSEntityIdentifier currentSigner = signers[i];

            label178:
            try {
               if (!signatureVerified) {
                  signedStream.verify(currentSigner);
                  signatureVerified = true;
               }
            } finally {
               break label178;
            }

            try {
               if (signedStream.isSignedReceiptRequested(currentSigner, address)) {
                  if (signersRequestingReceipts == null) {
                     signersRequestingReceipts = new Object[numSigners];
                  }

                  signersRequestingReceipts[numSignersRequestingReceipts++] = currentSigner;
               }
            } finally {
               continue;
            }
         }

         if (numSignersRequestingReceipts == 0) {
            return true;
         }

         if (!signatureVerified) {
            return false;
         }

         SignedReceiptHelper$DoSigningWork signingWorker = new SignedReceiptHelper$DoSigningWork(
            signedStream, receivedModel, isPIN, numSignersRequestingReceipts, signersRequestingReceipts, serviceRecord, null
         );
         PleaseWaitDialog pleaseWait = (PleaseWaitDialog)(new Object(SMIMEResources.getString(2086), signingWorker));
         pleaseWait.display();
         return signingWorker.getResult();
      } else {
         return true;
      }
   }

   public static final void addReceiptRequest(CMSSignedDataOutputStream signedCMSStream, ServiceRecord serviceRecord, EmailMessageModel message) {
      boolean isPIN = message.flagsSet(8192);
      String address = getDeviceAddress(isPIN, serviceRecord);
      if (address != null) {
         signedCMSStream.addReceiptRequest((CMSReceiptRequest)(new Object(new Object[]{address}, null)));
      }
   }

   public static final void processSignedReceiptForSend(CMSSignedDataOutputStream signedOS, EmailMessageModel message) {
      CMSReceiptData[] receiptData = signedOS.getReceiptInformation();
      if (receiptData != null) {
         addReceiptDataToCache(receiptData, message);
      }
   }

   private static final void addReceiptDataToCache(CMSReceiptData[] receiptData, EmailMessageModel message) {
      StringBuffer subject = (StringBuffer)(new Object());
      StringBuffer to = (StringBuffer)(new Object());
      StringBuffer cc = (StringBuffer)(new Object());
      StringBuffer bcc = (StringBuffer)(new Object());
      long sent = getMessageData(message, subject, to, cc, bcc);

      for (int i = 0; i < receiptData.length; i++) {
         SignedReceiptUserData userData = new SignedReceiptUserData(
            to.length() > 0 ? to.toString() : null, cc.length() > 0 ? cc.toString() : null, bcc.length() > 0 ? bcc.toString() : null, subject.toString(), sent
         );
         receiptData[i].setUserData(userData);
      }

      SignedReceiptCache.addReceipts(receiptData);
   }

   private static final long getMessageData(EmailMessageModel message, StringBuffer subject, StringBuffer to, StringBuffer cc, StringBuffer bcc) {
      subject.append(message.getSubject());
      String[] stringArray = new Object[2];
      StringBuffer tempBuffer = null;
      int numModels = message.size();

      for (int j = 0; j < numModels; j++) {
         Object submember = message.getAt(j);
         if (submember instanceof Object) {
            EmailHeaderModel emailHeaderModel = (EmailHeaderModel)submember;
            if (!emailHeaderModel.isBlank()) {
               emailHeaderModel.convert(null, stringArray);
               int headerType = emailHeaderModel.getHeaderType();
               switch (headerType) {
                  case -1:
                     continue;
                  case 0:
                  default:
                     tempBuffer = to;
                     break;
                  case 1:
                     tempBuffer = cc;
                     break;
                  case 2:
                     tempBuffer = bcc;
               }

               if (tempBuffer.length() > 0) {
                  tempBuffer.append("; ");
               }

               if (stringArray[1] != null) {
                  tempBuffer.append(stringArray[1]);
               } else {
                  tempBuffer.append(stringArray[0]);
               }
            }
         }
      }

      return message.getTimestamp();
   }
}
