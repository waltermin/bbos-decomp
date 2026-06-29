package net.rim.device.apps.internal.secureemail.sendmethods;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.transmission.rim.sendmethods.SendMethod;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.secureemail.AbortSendSecureEmailException;
import net.rim.device.apps.internal.secureemail.CertificateHarvester;
import net.rim.device.apps.internal.secureemail.CertificateHarvesterManager;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailCache;
import net.rim.device.apps.internal.secureemail.server.AbortablePleaseWaitDialog;
import net.rim.device.apps.internal.secureemail.server.SecureEmailPolicyServer;
import net.rim.device.apps.internal.secureemail.server.SecureEmailServerManager;

public class SecureEmailAwareSendMethod implements SendMethod {
   private ServiceRecord _serviceRecord;
   private long _encodingUID;
   private int _encodingAction;
   protected int _flags;
   private boolean _sendSuccessful;
   private MessageEncoder _messageEncoder;
   protected CertificateHarvester _harvester;
   private static final boolean DEBUG;

   protected MessageEncoder getMessageEncoderUsed() {
      return this._sendSuccessful ? this._messageEncoder : null;
   }

   @Override
   public long getEncodingUID() {
      MessageEncoder messageEncoderUsed = this.getMessageEncoderUsed();
      return messageEncoderUsed != null ? messageEncoderUsed.getEncodingUID() : this._encodingUID;
   }

   @Override
   public int getEncodingAction() {
      MessageEncoder messageEncoderUsed = this.getMessageEncoderUsed();
      return messageEncoderUsed != null ? messageEncoderUsed.getEncodingAction() : this._encodingAction;
   }

   @Override
   public boolean send(RIMModel model, Object context) {
      EmailMessageModel emailMessageModel = (EmailMessageModel)model;
      CertificateHarvesterManager certificateHarvesterManager = SecureEmailCache.getInstance().getCertificateHarvesterManager(emailMessageModel);
      CertificateHarvester certificateHarvester = certificateHarvesterManager.getCertificateHarvester(this.getEncodingUID());

      while (true) {
         SecureEmailPolicyServer[] secureEmailPolicyServers = SecureEmailServerManager.getInstance().getPolicyServers(this.getServiceRecord());

         try {
            for (SecureEmailPolicyServer currentPolicyServer : secureEmailPolicyServers) {
               if (!currentPolicyServer.isInitialized() || currentPolicyServer.isPolicyUpdateRequired()) {
                  updateSecureEmailPolicy(secureEmailPolicyServers);
                  break;
               }
            }
         } catch (AbortSendSecureEmailException e) {
            return false;
         } finally {
            ;
         }

         try {
            RecipientData[] messageRecipientData;
            if (certificateHarvester != null) {
               Vector harvestedRecipientData = certificateHarvester.getProcessedRecipients();
               messageRecipientData = new RecipientData[harvestedRecipientData.size()];
               harvestedRecipientData.copyInto(messageRecipientData);
            } else {
               messageRecipientData = new RecipientData[0];
            }

            this._messageEncoder = MessageEncoderLocator.locateMessageEncoder(emailMessageModel, secureEmailPolicyServers, messageRecipientData, this, context);
            break;
         } catch (AbortSendSecureEmailException e) {
            if (certificateHarvester != null) {
               certificateHarvester.reactivate();
            }

            return false;
         } catch (ReprocessRecipientsException e) {
            if (certificateHarvester != null) {
               certificateHarvester.reactivate();
            }
         }
      }

      if (this._messageEncoder != null && this._messageEncoder.encodeMessage()) {
         emailMessageModel = EmailSendUtility.sendMessage(emailMessageModel, this.getServiceRecord(), this._messageEncoder.getSendContext());
         emailMessageModel.setFlags(32);
         this._sendSuccessful = true;
         return true;
      }

      this._messageEncoder = null;
      if (certificateHarvester != null) {
         certificateHarvester.reactivate();
      }

      return false;
   }

   @Override
   public void setFlags(int flag) {
      this._flags |= flag;
   }

   @Override
   public void clearFlags(int flag) {
      this._flags &= ~flag;
   }

   @Override
   public int getFlags() {
      return this._flags;
   }

   @Override
   public ServiceRecord getServiceRecord() {
      return this._serviceRecord;
   }

   @Override
   public boolean isConfigurable(RIMModel _1, Object _2) {
      throw null;
   }

   @Override
   public boolean isConfigured(RIMModel _1, Object _2) {
      throw null;
   }

   public SecureEmailAwareSendMethod(ServiceRecord serviceRecord, long encodingUID, int encodingAction, Object context) {
      this._serviceRecord = serviceRecord;
      this._encodingUID = encodingUID;
      this._encodingAction = encodingAction;
   }

   private static void updateSecureEmailPolicy(SecureEmailPolicyServer[] secureEmailPolicyServers) {
      SecureEmailAwareSendMethod$UpdateSecureEmailPolicyWorkerThread updateSecureEmailPolicyWorkerThread = new SecureEmailAwareSendMethod$UpdateSecureEmailPolicyWorkerThread(
         secureEmailPolicyServers
      );
      AbortablePleaseWaitDialog updateSecureEmailPolicyDialog = new AbortablePleaseWaitDialog(updateSecureEmailPolicyWorkerThread);
      updateSecureEmailPolicyDialog.display();
      Throwable t = updateSecureEmailPolicyDialog.getThrowable();
      if (!(t instanceof AbortSendSecureEmailException)) {
         if (!(t instanceof Object)) {
            if (t != null) {
               throw new AbortSendSecureEmailException();
            }
         } else {
            throw (Object)t;
         }
      } else {
         throw (AbortSendSecureEmailException)t;
      }
   }
}
