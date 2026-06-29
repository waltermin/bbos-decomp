package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

final class MessageStatusField extends VerticalFieldManager {
   private MMSMessageModel _message;
   private String _details;

   public MessageStatusField(MMSMessageModel message) {
      this._message = message;
      this.updateFields();
   }

   private final void updateFields() {
      String status = null;
      this._details = null;
      switch (this._message.getStatus()) {
         case 1:
            status = MMSResources.getString(51);
            this._details = this._message.getPayload().getAttribute("x-mms-retrieve-text");
            break;
         case 511:
            status = MMSResources.getString(51);
            this._details = MMSResources.getString(116);
            break;
         case 1023:
            status = MMSResources.getString(100);
            this._details = this._message.getPayload().getAttribute("x-mms-retrieve-text");
            break;
         case 8191:
         case 16383:
            status = MMSResources.getString(52);
            this._details = this._message.getPayload().getAttribute("x-mms-response-text");
            break;
         case 131071:
            status = MMSResources.getString(52);
            this._details = MMSResources.getString(116);
            break;
         case 1048575:
            status = MMSResources.getString(56);
            break;
         case 8388607:
            status = MMSResources.getString(57);
      }

      this.deleteAll();
      if (status != null) {
         this.add(new AutoTextEditField("", status, 1000000, 9007199254740992L));
         if (this._details == null) {
            int errorCode = this._message.getHttpErrorCode();
            if (errorCode != 0) {
               String errorLabel = MessageFormat.format(MMSResources.getString(50), new Object[]{Integer.toString(errorCode)});
               String errorMsg = RendererControl.getStatusMessage(errorCode);
               this._details = errorLabel + errorMsg;
            }
         }

         if (this._details == null) {
            int exceptionError = this._message.getWAPIOExceptionError();
            int exceptionAdditionalData = this._message.getWAPIOExceptionAdditionalData();
            this._details = getWAPIOExceptionString(exceptionError, exceptionAdditionalData);
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (this._details != null && menu instanceof SystemEnabledMenu) {
         SystemEnabledMenu systemMenu = (SystemEnabledMenu)menu;
         Verb showDetailsVerb = new MessageStatusField$1(this, 16978432, MMSResources.getResourceBundle(), 70);
         systemMenu.add(showDetailsVerb);
         systemMenu.setDefault(showDetailsVerb);
      }
   }

   private static final String getWAPIOExceptionString(int error, int additionalData) {
      switch (error) {
         case 1000:
            if (error != 0) {
               String str = "E=" + error;
               if (additionalData != 0) {
                  str = str + ":" + additionalData;
               }

               return str;
            }

            return null;
         case 1001:
         default:
            return BrowserResources.getString(214);
         case 1002:
            return BrowserResources.getString(215);
         case 1003:
            return BrowserResources.getString(211);
         case 1004:
            return BrowserResources.getString(441);
         case 1005:
            switch (additionalData) {
               case 0:
                  return MMSResources.getString(115);
               case 1:
               default:
                  return BrowserResources.getString(439);
               case 2:
                  return BrowserResources.getString(2);
               case 3:
                  return BrowserResources.getString(3);
               case 4:
                  return BrowserResources.getString(4);
               case 5:
                  return BrowserResources.getString(5);
               case 6:
                  return BrowserResources.getString(6);
               case 7:
                  return BrowserResources.getString(7);
               case 8:
                  return BrowserResources.getString(8);
               case 9:
                  return BrowserResources.getString(9);
            }
         case 1006:
            return BrowserResources.getString(212);
         case 1007:
            switch (additionalData) {
               case -3:
                  return BrowserResources.getString(574);
               case -1:
                  return BrowserResources.getString(479);
               case 5:
                  return BrowserResources.getString(503);
               case 40:
                  return BrowserResources.getString(478);
               case 44:
               case 45:
               case 46:
                  return BrowserResources.getString(477);
               case 52:
                  return BrowserResources.getString(476);
               case 54:
                  return BrowserResources.getString(481);
               default:
                  Object[] items = new Object[]{"", new Integer(additionalData & 0xFF)};
                  return MessageFormat.format(BrowserResources.getString(480), items);
            }
         case 1008:
            return BrowserResources.getString(479);
         case 1009:
            return MMSResources.getString(114);
      }
   }
}
