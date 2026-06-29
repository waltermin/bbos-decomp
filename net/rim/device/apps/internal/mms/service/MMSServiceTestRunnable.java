package net.rim.device.apps.internal.mms.service;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSTask;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.options.MMSTransportServiceBook;
import net.rim.device.apps.internal.mms.verbs.MMSSendVerb;

final class MMSServiceTestRunnable implements MMSTask {
   @Override
   public final long getTaskThreadGuid() {
      return -1627158063255138358L;
   }

   @Override
   public final boolean requiresRadioCoverage() {
      return true;
   }

   @Override
   public final void run() {
      String number = null;

      label63:
      try {
         number = Phone.getInstance().getNumber(0);
      } finally {
         break label63;
      }

      if (number != null && number.length() != 0) {
         String url = MMSTransportServiceBook.getMMSCUrl();
         if (url == null) {
            alert("MMSC url not set.");
         } else {
            url = url + MMSTransportServiceBook.getMMSCConnectionParameters();
            if (!MMSUtilities.canSend()) {
               alert("Insufficient radio coverage.");
            } else {
               String EOL = "\n\n";
               String subject = "Test MMS message.";
               StringBuffer body = new StringBuffer();
               body.append("Phone Number: " + number + EOL);
               body.append("MMSC url: " + url + EOL);
               Hashtable requestHeaders = MMSHttpUtilities.getStandardRequestHeaders().toHashtable();
               Enumeration keys = requestHeaders.keys();

               while (keys.hasMoreElements()) {
                  String key = (String)keys.nextElement();
                  body.append(key + ": " + (String)requestHeaders.get(key) + EOL);
               }

               MMSMessageModelBuilder builder = new MMSMessageModelBuilder();
               builder.addRecipient(number + "/TYPE=PLMN");
               builder.setSubject(subject);
               builder.addAttachment("body.txt", 3, body.toString());
               MMSMessageModel message = builder.getResult();
               boolean isSent = MMSSendVerb.send(message, message.getPayload(), message.getAttachmentDataProvider(), new ContextObject());
               if (isSent) {
                  inform("Test message queued.");
               } else {
                  alert("Send failed.");
               }
            }
         }
      } else {
         alert("Device has unknown phone number.");
      }
   }

   private static final void inform(String message) {
      Application.getApplication().invokeLater(new ShowMessageRunnable(message, 0));
   }

   private static final void alert(String message) {
      Application.getApplication().invokeLater(new ShowMessageRunnable(message, 2));
   }
}
