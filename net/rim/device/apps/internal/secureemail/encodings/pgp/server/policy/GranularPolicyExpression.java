package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.framework.SubmemberUtilities;
import net.rim.device.apps.internal.api.crypto.certificate.CertificateAttachmentModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;
import net.rim.device.apps.internal.secureemail.CertificateServersAttachmentModel;
import net.rim.device.apps.internal.secureemail.RecipientData;
import net.rim.device.apps.internal.secureemail.SecureEmailUtilities;
import net.rim.device.apps.internal.secureemail.SendCertificateServerVerb;
import net.rim.ecmascript.regexp.RegExp;
import org.xml.sax.Attributes;

class GranularPolicyExpression extends GranularPolicyElement implements GranularPolicyEvaluateProvider {
   private int _condition = -1;
   private int _operator = -1;
   private String _lhs;
   private String _rhs;
   private static final int UNKNOWN = -1;
   private static final int CONDITION_RECIPIENT_ADDRESS = 0;
   private static final int CONDITION_RECIPIENT_DOMAIN = 1;
   private static final int CONDITION_RECIPIENT_USER_GROUP = 2;
   private static final int CONDITION_SENDER_ADDRESS = 3;
   private static final int CONDITION_SENDER_DOMAIN = 4;
   private static final int CONDITION_SENDER_USER_GROUP = 5;
   private static final int CONDITION_MESSAGE_HEADER = 6;
   private static final int CONDITION_MESSAGE_SUBJECT = 7;
   private static final int CONDITION_MESSAGE_BODY = 8;
   private static final int CONDITION_MESSAGE_IS_SIGNED = 9;
   private static final int CONDITION_MESSAGE_IS_ENCRYPTED = 10;
   private static final int CONDITION_MESSAGE_SIZE = 11;
   private static final int CONDITION_MESSAGE_CONTAINED_VIRUS = 12;
   private static final int CONDITION_MESSAGE_HAS_ATTACHMENT_FILENAME = 13;
   private static final int CONDITION_MESSAGE_HAS_ATTACHMENT_TYPE = 14;
   private static final int CONDITION_SERVICE_TYPE = 15;
   private static final int CONDITION_USER_HAS_AUTHENTICATED = 16;
   private static final int CONDITION_APPLICATION_IS = 17;
   private static final int CONDITION_MESSAGE_FROM_MAILING_LIST = 18;
   private static final int CONDITION_RECIPIENT_IS_EXCHANGE_LIST = 19;
   private static final int CONDITION_MAILING_LIST_CONTAINS_NUM_USERS = 20;
   private static final int CONDITION_IP_ADDRESS_LOCAL_CONNECTOR = 21;
   private static final int CONDITION_PORT_LOCAL_CONNECTOR = 22;
   private static final int CONDITION_RECIPIENT_KEY_MODE = 23;
   private static final int CONDITION_SENDER_KEY_MODE = 24;
   private static final int CONDITION_MESSAGE_IS_CONFIDENTIAL = 25;
   private static final int CONDITION_EXTERNAL_RECIPIENT_DELIVERY_PREF = 26;
   private static final int CONDITION_PART_OF_MESSAGE_IS_ENCRYPTED = 27;
   private static final int OPERATOR_IS = 100;
   private static final int OPERATOR_CONTAINS = 101;
   private static final int OPERATOR_BEGINS_WITH = 102;
   private static final int OPERATOR_ENDS_WITH = 103;
   private static final int OPERATOR_MATCHES_PATTERN = 104;
   private static final int OPERATOR_IS_IN_DICTIONARY = 105;
   private static final int OPERATOR_IS_IN_SUBDOMAIN_OF = 106;
   private static final int OPERATOR_CONTAINS_ENTRY_IN_DICTIONARY = 107;
   private static final int OPERATOR_TO_ANY_KEY = 108;
   private static final int OPERATOR_TO_KEY_ID = 109;
   private static final int OPERATOR_TO_ADK = 110;
   private static final int OPERATOR_TO_KEY_IN_DICTIONARY = 111;
   private static final int OPERATOR_IS_GREATER_THAN = 112;
   private static final int OPERATOR_IS_LESS_THAN = 113;
   private static final int OPERATOR_IS_SMTP_INBOUND = 114;
   private static final int OPERATOR_IS_SMTP_OUTBOUND = 115;
   private static final int OPERATOR_IS_POP = 116;
   private static final int OPERATOR_IS_IMAP = 117;
   private static final int OPERATOR_IS_MAPI = 118;
   private static final int OPERATOR_IS_NOTES = 119;
   private static final int OPERATOR_IS_WEB_MESSENGER = 120;
   private static final int OPERATOR_IS_INTERNAL_DESKTOP_OR_OC = 121;
   private static final int OPERATOR_IS_EXTERNAL_DESKTOP_OR_OC = 122;
   private static final int OPERATOR_IS_SERVER = 123;
   private static final int OPERATOR_IS_RIM_BLACKBERRY = 124;
   private static final int OPERATOR_FEWER_THAN = 125;
   private static final int OPERATOR_GREATER_THAN = 126;
   private static final int OPERATOR_IS_EQUALS = 127;
   private static final int OPERATOR_IS_SKM = 128;
   private static final int OPERATOR_IS_CKM = 129;
   private static final int OPERATOR_IS_GKM = 130;
   private static final int OPERATOR_IS_SCKM = 131;
   private static final int OPERATOR_IS_CONFIDENTIAL = 132;
   private static final int OPERATOR_IS_CONFIDENTIAL_OR_SUBJECT = 133;
   private static final int OPERATOR_IS_DELIVERY_WEB_MESSENGER = 134;
   private static final int OPERATOR_IS_SMART_TRAILER = 135;
   private static final int OPERATOR_IS_DESKTOP_OR_OC = 136;

   GranularPolicyExpression(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      if (StringUtilities.strEqualIgnoreCase(localName, "cond")) {
         this._condition = extractCondition(attributes);
         return this;
      } else if (StringUtilities.strEqualIgnoreCase(localName, "operator")) {
         this._operator = extractOperator(attributes);
         return this;
      } else {
         return !StringUtilities.strEqualIgnoreCase(localName, "rhs") && !StringUtilities.strEqualIgnoreCase(localName, "lhs")
            ? super.handleStartElement(localName, attributes)
            : new GranularPolicyExpression$TransientHSElement(localName, this);
      }
   }

   @Override
   public void addChildElement(GranularPolicyElement childElement) {
      if (!(childElement instanceof GranularPolicyExpression$TransientHSElement)) {
         super.addChildElement(childElement);
      } else {
         GranularPolicyExpression$TransientHSElement transientHSElement = (GranularPolicyExpression$TransientHSElement)childElement;
         if (StringUtilities.strEqualIgnoreCase(transientHSElement.getXMLTag(), "rhs")) {
            this._rhs = transientHSElement._data_value;
         } else {
            this._lhs = transientHSElement._data_value;
         }
      }
   }

   @Override
   public boolean evaluate(String recipientAddress, ServiceRecord serviceRecord, EmailMessageModel emailMessageModel) {
      switch (this._condition) {
         case -1:
         case 12:
         case 16:
         case 18:
         case 21:
         case 22:
         case 26:
            this.logWarning("condition=" + this._condition + " not applicable");
            return false;
         case 0:
         default:
            return this.evaluateStringMatchOperator(recipientAddress);
         case 1:
            return this.evaluateStringMatchOperator(this.extractDomain(recipientAddress));
         case 2:
         case 5:
         case 19:
         case 20:
         case 23:
         case 24:
            this.logWarning("condition=" + this._condition + " not implemented");
            return false;
         case 3: {
            String senderAddress = CMIMEUtilities.getEmailAddress(serviceRecord);
            return this.evaluateStringMatchOperator(senderAddress);
         }
         case 4: {
            String senderAddress = CMIMEUtilities.getEmailAddress(serviceRecord);
            return this.evaluateStringMatchOperator(this.extractDomain(senderAddress));
         }
         case 6:
            if (this._lhs != null && this._lhs.length() != 0) {
               if (StringUtilities.strEqualIgnoreCase(this._lhs, "subject")) {
                  String messageSubjectx = emailMessageModel.getSubject();
                  return this.evaluateStringMatchOperator(messageSubjectx);
               }

               if (StringUtilities.strEqualIgnoreCase(this._lhs, "importance")) {
                  String messageImportance;
                  switch (emailMessageModel.getPriority()) {
                     case 1:
                        messageImportance = "normal";
                        break;
                     case 2:
                     default:
                        messageImportance = "high";
                        break;
                     case 3:
                        messageImportance = "low";
                  }

                  return this.evaluateStringMatchOperator(messageImportance);
               }

               if (StringUtilities.strEqualIgnoreCase(this._lhs, "sensitivity")) {
                  String messageSensitivity;
                  switch (emailMessageModel.getSensitivity()) {
                     case 1:
                        messageSensitivity = "normal";
                        break;
                     case 2:
                        messageSensitivity = "personal";
                        break;
                     case 3:
                        messageSensitivity = "private";
                        break;
                     case 4:
                     default:
                        messageSensitivity = "confidential";
                  }

                  return this.evaluateStringMatchOperator(messageSensitivity);
               }

               if (StringUtilities.strEqualIgnoreCase(this._lhs, "to")) {
                  return this.evaluateEmailHeaderModelExpression(emailMessageModel, new GranularPolicyExpression$EmailHeaderModelRecognizer(0));
               }

               if (StringUtilities.strEqualIgnoreCase(this._lhs, "cc")) {
                  return this.evaluateEmailHeaderModelExpression(emailMessageModel, new GranularPolicyExpression$EmailHeaderModelRecognizer(1));
               }

               if (StringUtilities.strEqualIgnoreCase(this._lhs, "bcc")) {
                  return this.evaluateEmailHeaderModelExpression(emailMessageModel, new GranularPolicyExpression$EmailHeaderModelRecognizer(2));
               }

               this.logWarning("condition=" + this._condition + " (" + this._lhs + ") not implemented");
               return false;
            }

            throw new IllegalArgumentException();
         case 7:
            String messageSubject = emailMessageModel.getSubject();
            return this.evaluateStringMatchOperator(messageSubject);
         case 8:
            String messageBody = emailMessageModel.getBody();
            return this.evaluateStringMatchOperator(messageBody);
         case 9:
         case 10:
         case 27:
            return false;
         case 11:
            int messageSize = emailMessageModel.getBody().length();
            return this.evaluateNumericRangeOperator(messageSize);
         case 13:
         case 14:
            Object[] attachments = SecureEmailUtilities.extractSecureEmailAttachments(emailMessageModel);
            int numAttachments = attachments != null ? attachments.length : 0;

            for (int i = 0; i < numAttachments; i++) {
               Object currentAttachment = attachments[i];
               String currentFilename = null;
               String currentFileType = null;
               if (currentAttachment instanceof AddressCardModel) {
                  String xRIMDeviceString = "Application/X-rimdeviceAddress Book:" + currentAttachment.toString();
                  currentFilename = xRIMDeviceString;
                  currentFileType = xRIMDeviceString;
               } else if (!(currentAttachment instanceof CertificateAttachmentModel)) {
                  if (currentAttachment instanceof CertificateServersAttachmentModel) {
                     CertificateServersAttachmentModel certificateServersAttachmentModel = (CertificateServersAttachmentModel)currentAttachment;
                     currentFilename = certificateServersAttachmentModel.getFilename();
                     currentFileType = SendCertificateServerVerb.TYPE_APPLICATION_CERTIFICATE_SERVERS;
                  }
               } else {
                  CertificateAttachmentModel certificateAttachmentModel = (CertificateAttachmentModel)currentAttachment;
                  currentFilename = new String(certificateAttachmentModel.getNameBytes());
                  currentFileType = certificateAttachmentModel.getOutgoingMIMEContentType();
               }

               String stringToMatch = this._condition == 13 ? currentFilename : currentFileType;
               if (this.evaluateStringMatchOperator(stringToMatch)) {
                  return true;
               }
            }

            return false;
         case 15:
            if (this._operator == 124) {
               return true;
            }

            return false;
         case 17:
            if (this._operator != 121 && this._operator != 124) {
               return false;
            }

            return true;
         case 25:
            return emailMessageModel.getSensitivity() == 4;
      }
   }

   private String extractDomain(String emailAddress) {
      int atIndex = emailAddress.indexOf(64);
      if (atIndex < 0) {
         this.logWarning(emailAddress + ": no @");
         return emailAddress;
      } else {
         return emailAddress.substring(atIndex + 1);
      }
   }

   private boolean evaluateEmailHeaderModelExpression(EmailMessageModel emailMessageModel, GranularPolicyExpression$EmailHeaderModelRecognizer recognizer) {
      Object[] matchingHeaderModels = SubmemberUtilities.getSubmembers(emailMessageModel, recognizer);
      int numMatchingHeaderModels = matchingHeaderModels != null ? matchingHeaderModels.length : 0;

      for (int i = 0; i < numMatchingHeaderModels; i++) {
         RecipientData[] recipientData = SecureEmailUtilities.getRecipientData((EmailHeaderModel)matchingHeaderModels[i], false);
         int numRecipientData = recipientData != null ? recipientData.length : 0;

         for (int j = 0; j < numRecipientData; j++) {
            String[] emailAddresses = recipientData[j].getAddresses();
            int numEmailAddresses = emailAddresses != null ? emailAddresses.length : 0;

            for (int k = 0; k < numEmailAddresses; k++) {
               if (this.evaluateStringMatchOperator(emailAddresses[k])) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private boolean evaluateStringMatchOperator(String stringToMatch) {
      if (this._rhs != null && this._rhs.length() != 0) {
         switch (this._operator) {
            case 99:
               this.logWarning("operator=" + this._operator + " not a string match operator");
               return false;
            case 100:
            default:
               return StringUtilities.strEqualIgnoreCase(stringToMatch, this._rhs);
            case 101:
               if (new StringMatch(this._rhs, false).indexOf(stringToMatch) >= 0) {
                  return true;
               }

               return false;
            case 102:
               return StringUtilities.startsWithIgnoreCase(stringToMatch, this._rhs, 1701707776);
            case 103:
            case 106:
               return StringUtilities.endsWithIgnoreCase(stringToMatch, this._rhs, 1701707776);
            case 104:
               if (stringToMatch != null) {
                  try {
                     return new RegExp(this._rhs, true).match(stringToMatch, 0) != null;
                  } finally {
                     return false;
                  }
               }

               return false;
            case 105:
               this.logWarning("operator=" + this._operator + " not implemented");
               return false;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private boolean evaluateNumericRangeOperator(int numericValue) {
      if (this._rhs != null && this._rhs.length() != 0) {
         int rhsNumericValue = Integer.parseInt(this._rhs);
         switch (this._operator) {
            case 112:
            case 126:
               if (numericValue > rhsNumericValue) {
                  return true;
               }

               return false;
            case 113:
            case 125:
               if (numericValue < rhsNumericValue) {
                  return true;
               }

               return false;
            case 127:
               if (numericValue == rhsNumericValue) {
                  return true;
               }

               return false;
            default:
               this.logWarning("operator=" + this._operator + " not a numeric range operator");
               return false;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public String getDebugPrintInfo() {
      StringBuffer info = new StringBuffer();
      info.append("Expression");
      if (this._lhs != null) {
         info.append(", lhs=\"").append(this._lhs).append("\"");
      }

      info.append(", condition=").append(this._condition);
      info.append(", operator=").append(this._operator);
      if (this._rhs != null) {
         info.append(", rhs=\"").append(this._rhs).append("\"");
      }

      return info.toString();
   }

   private static int extractCondition(Attributes attributes) {
      int numAttributes = attributes.getLength();

      for (int i = 0; i < numAttributes; i++) {
         if (StringUtilities.strEqualIgnoreCase(attributes.getLocalName(i), "name")) {
            String conditionName = attributes.getValue(i);
            if (StringUtilities.strEqualIgnoreCase(conditionName, "recipient.address")) {
               return 0;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "recipient.domain")) {
               return 1;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "recipient.user.group")) {
               return 2;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "sender.address")) {
               return 3;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "sender.domain")) {
               return 4;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "sender.user.group")) {
               return 5;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.header")) {
               return 6;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.subject")) {
               return 7;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.body")) {
               return 8;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.is.signed")) {
               return 9;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.is.encrypted")) {
               return 10;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.size")) {
               return 11;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.contained.virus")) {
               return 12;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.has.attachment.filename")) {
               return 13;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.has.attachment.type")) {
               return 14;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "service.type")) {
               return 15;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "user.has.authenticated")) {
               return 16;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "application.is")) {
               return 17;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.from.mailing.list")) {
               return 18;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "recipient.is.exchange.list")) {
               return 19;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "mailing.list.contains.num.users")) {
               return 20;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "ip.address.local.connector")) {
               return 21;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "port.local.connector")) {
               return 22;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "recipient.key.mode")) {
               return 23;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "sender.key.mode")) {
               return 24;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "message.is.confidential")) {
               return 25;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "external.recipient.delivery.pref")) {
               return 26;
            }

            if (StringUtilities.strEqualIgnoreCase(conditionName, "part.of.message.is.encrypted")) {
               return 27;
            }
         }
      }

      return -1;
   }

   private static int extractOperator(Attributes attributes) {
      int numAttributes = attributes.getLength();

      for (int i = 0; i < numAttributes; i++) {
         if (StringUtilities.strEqualIgnoreCase(attributes.getLocalName(i), "name")) {
            String operatorName = attributes.getValue(i);
            if (StringUtilities.strEqualIgnoreCase(operatorName, "is")) {
               return 100;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "contains")) {
               return 101;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "begins.with")) {
               return 102;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "ends.with")) {
               return 103;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "matches.pattern")) {
               return 104;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.in.dictionary")) {
               return 105;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.in.subdomain.of")) {
               return 106;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "contains.entry.in.dict")) {
               return 107;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "to.any.key")) {
               return 108;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "to.key.id")) {
               return 109;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "to.adk")) {
               return 110;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "to.key.in.dictionary")) {
               return 111;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.greater.than")) {
               return 112;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.less.than")) {
               return 113;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.smtp.inbound")) {
               return 114;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.smtp.outbound")) {
               return 115;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.pop")) {
               return 116;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.imap")) {
               return 117;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.mapi")) {
               return 118;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.notes")) {
               return 119;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.web.messenger")) {
               return 120;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.internal.desktop.or.oc")) {
               return 121;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.external.desktop.or.oc")) {
               return 122;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.server")) {
               return 123;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.rim.blackberry")) {
               return 124;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "fewer.than")) {
               return 125;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "greater.than")) {
               return 126;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.equals")) {
               return 127;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.skm")) {
               return 128;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.ckm")) {
               return 129;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.gkm")) {
               return 130;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.sckm")) {
               return 131;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.confidential")) {
               return 132;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.confidential.or.subject")) {
               return 133;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.delivery.web.messenger")) {
               return 134;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.smart.trailer")) {
               return 135;
            }

            if (StringUtilities.strEqualIgnoreCase(operatorName, "is.desktop.or.oc")) {
               return 136;
            }
         }
      }

      return -1;
   }
}
