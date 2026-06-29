package net.rim.device.apps.internal.diagnostic;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.api.transmission.rim.RIMMessagingIncomingMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMessage;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.otasync.OTAMessageSync;

public final class TestEmailService extends Thread implements TransmissionServiceListener {
   private Timer timer;
   boolean timeoutFlag;
   boolean flashFlag;
   boolean emailPingFinished;
   private TimerTask emailTimeout;
   private TransmissionService transmissionService;
   private RunningScreen screen;
   private Diag manager;
   boolean echoFlag;
   String dest;
   String sub;

   TestEmailService(RunningScreen _scr) {
      this.screen = _scr;
      this.manager = this.screen._manager;
      this.flashFlag = true;
      long tmpLong = ((Random)(new Object())).nextLong();
      this.sub = "<$RemoveOnDelivery,SuppressSaveInSentItems>Self Ping Email - ";
      this.sub = ((StringBuffer)(new Object())).append(this.sub).append(new Object(tmpLong).toString()).toString();
   }

   @Override
   public final void run() {
      this.screen.state = 1;
      this.transmissionService = TransmissionServiceManager.get(8399767144006445082L);
      this.transmissionService.addTransmissionServiceListener("net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", 70, this);
      if (this.screen.hasEmail1) {
         this.dest = this.screen.emailAddress1;
         this.emailPingEmail1();
         this.screen.email1Finished = true;
         int var10000 = this.screen.state;
         this.screen.getClass();
         if (var10000 == 0) {
            return;
         }
      }

      if (this.screen.hasEmail2) {
         this.dest = this.screen.emailAddress2;
         this.emailPingEmail2();
         int var1 = this.screen.state;
         this.screen.getClass();
         if (var1 == 0) {
            return;
         }
      }

      this.screen.emailServiceFinished = true;
      this.transmissionService.removeTransmissionServiceListener("net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", this);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void emailPingEmail1() {
      try {
         if (!this.sendEmailPing()) {
            this.screen.report.emailService1 = 0;
         } else {
            this.echoFlag = false;
            this.timeoutFlag = false;
            this.timer = (Timer)(new Object());
            this.emailTimeout = new TestEmailService$emailExpired(this);
            this.timer.schedule(this.emailTimeout, 45000);

            while (!this.echoFlag) {
               Thread.sleep(500);
               if (this.timeoutFlag && this.screen.state == 1) {
                  this.screen.report.emailService1 = 0;
                  return;
               }

               int var10000 = this.screen.state;
               this.screen.getClass();
               if (var10000 == 0) {
                  return;
               }
            }

            this.timer.cancel();
            this.screen.report.emailService1 = 1;
         }
      } catch (Throwable var3) {
         System.out.println(((StringBuffer)(new Object("Excpetion:"))).append(e.toString()).toString());
         this.transmissionService
            .removeTransmissionServiceListener("net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", this);
         return;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void emailPingEmail2() {
      try {
         if (!this.sendEmailPing()) {
            this.screen.report.emailService2 = 0;
         } else {
            this.echoFlag = false;
            this.timeoutFlag = false;
            this.timer = (Timer)(new Object());
            this.emailTimeout = new TestEmailService$emailExpired(this);
            this.timer.schedule(this.emailTimeout, 45000);

            while (!this.echoFlag) {
               Thread.sleep(500);
               if (this.timeoutFlag && this.screen.state == 1) {
                  this.screen.report.emailService2 = 0;
                  return;
               }

               int var10000 = this.screen.state;
               this.screen.getClass();
               if (var10000 == 0) {
                  return;
               }
            }

            this.timer.cancel();
            this.screen.report.emailService2 = 1;
         }
      } catch (Throwable var3) {
         System.out.println(((StringBuffer)(new Object("Excpetion:"))).append(e.toString()).toString());
         this.transmissionService
            .removeTransmissionServiceListener("net.rim.device.apps.api.transmission.rim.RIMMessagingConstants.RIM_MESSAGING_MESSAGE", this);
         return;
      }
   }

   public final boolean sendEmailPing() {
      EmailMessageModel msg = EmailSendUtility.send(this.dest, this.sub, "");
      if (msg != null) {
         OTAMessageSync.getInstance().messageReadStatusChangeOnDevice(msg, true, new Object());
         OTAMessageSync.getInstance().messageDeletedOnDevice(msg, EmailHierarchy.getEmailFolder(msg.getFolderId()), new Object());
         ServiceRecord serviceRecord = msg.getServiceRecordForMessage();
         int refId = msg.getCMIMEReferenceIdentifier();
         OTAMessageSync.getInstance().messageReadStatusChangeOnDevice(serviceRecord, refId, true);
         OTAMessageSync.getInstance().messageDeletedOnDevice(serviceRecord, refId);
         EmailHierarchy.removeMessage(msg, msg.getFolderId());
         return true;
      } else {
         return false;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean receiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object context) {
      try {
         ContextObject contextObject = ContextObject.castOrCreate(context);
         RIMMessagingIncomingMessage incMsg = null;
         String _sub = null;
         if (!(transmissionObject instanceof Object)) {
            return false;
         }

         RIMMessagingIncomingMessage var12 = transmissionObject;
         ((RIMMessagingMessage)var12).doNotSaveMessageInSentItems();
         Object emailbody = ((RIMMessagingMessage)var12).getText();
         Object emailsub = ((RIMMessagingMessage)var12).getSubject();
         if (emailbody instanceof Object) {
            String var13 = emailsub;
            if (((String)var13).equals(this.sub)) {
               emailbody = ((RIMMessagingService)aTransmissionService).getOutgoingServiceRecord();
               emailsub = ((RIMMessagingMessage)var12).getReferenceIdentifier();
               OTAMessageSync.getInstance().messageReadStatusChangeOnDevice((ServiceRecord)emailbody, (int)emailsub, true);
               OTAMessageSync.getInstance().messageDeletedOnDevice((ServiceRecord)emailbody, (int)emailsub);
               this.echoFlag = true;
               RIMMessagingFolderManagement folderManager = (RIMMessagingFolderManagement)(new Object());
               folderManager.addDeleteMessage(((RIMMessagingMessage)var12).getReferenceIdentifier(), ((RIMMessagingMessage)var12).getFolderIdentifier());
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } catch (Throwable var11) {
         System.out.println(e.toString());
         return false;
      }
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
   }
}
