package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.api.ui.component.ActiveFieldContext;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.vm.Array;

final class MailToMessageFactory implements Factory {
   private static final int SUBJECTLENGTH = 8;
   private static final int TOLENGTH = 3;
   private static final int CCLENGTH = 3;
   private static final int BODYLENGTH = 5;

   @Override
   public final Object createInstance(Object initialData) {
      String data = null;
      if (initialData == null) {
         return null;
      }

      if (!(initialData instanceof ActiveFieldContext)) {
         if (!(initialData instanceof ContextObject)) {
            data = initialData.toString();
         } else {
            ContextObject context = (ContextObject)initialData;
            data = (String)context.get(253);
         }
      } else {
         ActiveFieldContext afc = (ActiveFieldContext)initialData;
         data = afc.getData();
      }

      RIMModel[] toArray = new RIMModel[0];
      RIMModel[] ccArray = new RIMModel[0];
      String subject = null;
      String body = null;
      int startIndex = 0;
      int endIndex = -1;
      endIndex = data.indexOf(63);
      if (endIndex == -1) {
         endIndex = data.length();
      }

      if (data.toLowerCase().startsWith("mailto:")) {
         startIndex = 7;
      }

      if (endIndex > startIndex) {
         this.addAddresses(toArray, data.substring(startIndex, endIndex));
      }

      if (endIndex != data.length()) {
         StringTokenizer parameters = new StringTokenizer(data.substring(endIndex + 1, data.length()), '&');
         String parameter = null;
         String lowerCase = null;

         while (parameters.hasMoreTokens()) {
            parameter = parameters.nextToken();
            if (parameter.indexOf(61) != -1) {
               parameter = parameter.trim();
               lowerCase = parameter.substring(0, parameter.indexOf(61)).toLowerCase();
               if (lowerCase.equals("subject")) {
                  subject = parameter.substring(8);
               } else if (lowerCase.equals("to")) {
                  this.addAddresses(toArray, parameter.substring(3));
               } else if (lowerCase.equals("cc")) {
                  this.addAddresses(ccArray, parameter.substring(3));
               } else if (lowerCase.equals("body")) {
                  if (body == null) {
                     body = parameter.substring(5);
                  } else {
                     body = body.concat("\n" + parameter.substring(5));
                  }
               }
            }
         }
      }

      return new MailToEmailMessage(toArray, ccArray, subject, body);
   }

   private final void addAddresses(RIMModel[] addressArray, String newAddress) {
      StringTokenizer tokenizer = new StringTokenizer(newAddress, ',');
      int len = addressArray.length;
      ContextObject context = new ContextObject();

      while (tokenizer.hasMoreTokens()) {
         Array.resize(addressArray, len + 1);
         context.put(253, URIDecoder.decode(tokenizer.nextToken().trim(), "utf-8", false));
         addressArray[len] = (RIMModel)FactoryUtil.createInstance(-2985347935260258684L, context);
         len++;
      }
   }
}
