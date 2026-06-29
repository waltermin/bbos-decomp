package net.rim.device.apps.internal.mms.ui;

import java.util.Vector;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;
import net.rim.device.apps.internal.mms.api.MMSPayloadModel;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class MMSViewField extends VerticalFieldManager {
   private Field _addressField;
   private Field _subjectField;
   private Field _presentationField;

   public MMSViewField(MMSMessageModel message, ContextObject context) {
      super(17592186044416L);
      this.addHeaderFields(message, context);
      this.add(new SeparatorField());
      this.addPresentationField(message, context);
   }

   private final void addHeaderFields(MMSMessageModel message, ContextObject context) {
      MMSPayloadModel payload = message.getPayload();
      int priority = MMSUtilities.parseInt(payload.getAttribute("x-mms-priority"), 129);
      if (priority != 129) {
         String priorityLabel = MMSResources.getString(59);
         String priorityText = MMSResources.getString(priority == 130 ? 60 : 61);
         this.add(new ActiveAutoTextEditField(priorityLabel, priorityText, 1000000, 9007199254740992L));
      }

      Vector toList = payload.getRecipients();
      if (toList != null) {
         String label = MMSResources.getString(16);
         this.add(new RecipientFieldManager(toList, label, false, context));
      }

      Vector ccList = payload.getCcRecipients();
      if (ccList != null) {
         String label = MMSResources.getString(17);
         this.add(new RecipientFieldManager(ccList, label, false, context));
      }

      Vector bccList = payload.getBccRecipients();
      if (bccList != null) {
         String label = MMSResources.getString(65);
         this.add(new RecipientFieldManager(bccList, label, false, context));
      }

      String classText = getDisplayableMessageClass(payload.getAttribute("x-mms-message-class"));
      if (classText != null) {
         String classLabel = MMSResources.getString(45);
         long classFieldFlags = 9007199254740992L;
         Field classField = new ActiveAutoTextEditField(classLabel, classText, 1000000, classFieldFlags);
         this.add(classField);
      }

      if (message.isForwardLocked()) {
         String drmLabel = MMSResources.getString(49);
         Field drmField = new LabelField(drmLabel, 9007199254740992L);
         this.add(drmField);
      }

      String dateLabel = CommonResources.getString(2001);
      long dateFlags = 9007199254741046L;
      long date = MMSUtilities.parseLong(payload.getAttribute("date"), 0);
      if (date > 0) {
         date *= 1000;
      } else {
         date = payload.getCreationDate();
      }

      DateField dateField = new DateField(dateLabel, date, dateFlags);
      dateField.setEditable(false);
      this.add(dateField);
      int readReportCount = message.getReadReportCount();
      int deliveryReportCount = message.getDeliveryReportCount();
      if (deliveryReportCount > 0) {
         this.add(new StatusReportField(message, 74, context));
      }

      if (readReportCount > 0) {
         this.add(new StatusReportField(message, 73, context));
      }

      if (!message.isInbound() && message.isSuccessfullySent()) {
         if (message.getDeliveryDate() > 0) {
            dateLabel = MMSResources.getString(53);
            dateField = new DateField(dateLabel, message.getDeliveryDate(), dateFlags);
            dateField.setEditable(false);
            this.add(dateField);
         }

         if (message.getReadDate() > 0) {
            dateLabel = MMSResources.getString(54);
            dateField = new DateField(dateLabel, message.getReadDate(), dateFlags);
            dateField.setEditable(false);
            this.add(dateField);
         }
      }

      this.add(new MessageStatusField(message));
      RIMModel sender = payload.getSender();
      if (sender != null) {
         boolean labelFlag = !ContextObject.getFlag(context, 1);
         ContextObject.setFlag(context, 1);
         HorizontalFieldManager addressField = new HorizontalFieldManager();
         addressField.add(new LabelField(MMSResources.getString(15)));
         addressField.add(((FieldProvider)sender).getField(context));
         if (labelFlag) {
            ContextObject.clearFlag(context, 1);
         }

         this._addressField = addressField;
         this.add(this._addressField);
      }

      String subjectLabel = MMSResources.getString(18);
      String subjectText = payload.getAttribute("subject");
      long subjectFieldFlags = 9007199254740992L;
      this._subjectField = new ActiveAutoTextEditField(subjectLabel, subjectText, 1000000, subjectFieldFlags);
      this.add(this._subjectField);
   }

   private final void addPresentationField(MMSMessageModel message, ContextObject context) {
      MMSPayloadModel payload = message.getPayload();
      context.put(-7651695713744129224L, message);
      MMSPresentationModel presentation = payload.getPresentationModel();
      if (presentation != null) {
         this._presentationField = ((FieldProvider)presentation).getField(context);
         if (this._presentationField != null) {
            this.add(this._presentationField);
            return;
         }
      } else {
         String emptyContentMessage = MMSResources.getString(26);
         String sizeAttr = payload.getAttribute("x-mms-message-size");
         int size = MMSUtilities.parseInt(sizeAttr, -1);
         String sizeString;
         if (size < 0) {
            sizeString = MMSResources.getString(44);
         } else {
            int sizeKilobytes = Math.max(1, size + 512 >> 10);
            sizeString = Integer.toString(sizeKilobytes) + 'K';
         }

         String estimatedSizeMessage = MessageFormat.format(MMSResources.getString(42), new String[]{sizeString});
         this.add(new RichTextField(emptyContentMessage, 36028797018963968L));
         this.add(new RichTextField(estimatedSizeMessage, 36028797018963968L));
         long date = MMSUtilities.parseLong(payload.getAttribute("x-mms-expiry"), 0) * 1000;
         if (date > 0) {
            if (payload.getAttribute("x-mms-expiry-absolute") == null) {
               long creationDate = MMSUtilities.parseLong(payload.getAttribute("date"), 0) * 1000;
               if (creationDate > 0) {
                  date += creationDate;
               } else {
                  date += payload.getCreationDate();
               }
            }

            String expiryLabel = MMSResources.getString(55);
            long dateFlags = 9007199254741046L;
            DateField dateField = new DateField(expiryLabel, date, dateFlags);
            dateField.setEditable(false);
            this.add(dateField);
         }
      }
   }

   public final Field getInitialFocusField() {
      if (this._presentationField != null && this._presentationField.isFocusable()) {
         return this._presentationField;
      } else {
         return this._addressField != null ? this._addressField : this._subjectField;
      }
   }

   public final Field getInitialTopField() {
      return this._addressField != null ? this._addressField : this._subjectField;
   }

   public final Field getAddressField() {
      return this._addressField;
   }

   private static final String getDisplayableMessageClass(String attr) {
      int value = MMSUtilities.parseInt(attr, 0);
      if (value <= 0) {
         return attr;
      }

      switch (value) {
         case 127:
            return null;
         case 128:
            return MMSResources.getString(36);
         case 129:
         default:
            return MMSResources.getString(43);
         case 130:
            return MMSResources.getString(47);
         case 131:
            return MMSResources.getString(46);
      }
   }
}
