package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

public class GranularPolicyAction extends GranularPolicyElement {
   private int _operation;
   private boolean _encryptToRecipient;
   private boolean _requireTrustedRecipient;
   private boolean _requireEndToEndRecipient;
   private boolean _encryptToSender;
   private String[] _encryptToOtherKeyIDs;
   private boolean _sign;
   private String _keyNotFound;
   private String _encodingFormat;
   private String _chainName;
   private static final int UNKNOWN = -1;
   public static final int OPERATION_SEND = 0;
   public static final int OPERATION_BOUNCE = 1;
   public static final int OPERATION_VERIFY = 2;
   public static final int OPERATION_SEND_WEB_MESSENGER = 3;
   public static final int OPERATION_SEND_CLEAR = 4;
   public static final int OPERATION_GO_TO_CHAIN = 5;
   public static final int OPERATION_ADD_TO_DICTIONARY = 6;
   public static final int OPERATION_SEND_COPY = 7;
   public static final int OPERATION_SCAN_FOR_VIRUSES = 8;
   public static final int OPERATION_DROP = 9;
   public static final int OPERATION_EXPAND_TO_LIST = 10;
   public static final int OPERATION_DELIVER_MESSAGE = 11;

   GranularPolicyAction(String xmlTag, Attributes attributes, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
      int numAttributes = attributes.getLength();

      for (int i = 0; i < numAttributes; i++) {
         if (StringUtilities.strEqualIgnoreCase(attributes.getLocalName(i), "operation")) {
            String operation = attributes.getValue(i);
            if (StringUtilities.strEqualIgnoreCase(operation, "send")) {
               this._operation = 0;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "bounce")) {
               this._operation = 1;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "verify")) {
               this._operation = 2;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "send_web_messenger")) {
               this._operation = 3;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "send_clear")) {
               this._operation = 4;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "go_to_chain")) {
               this._operation = 5;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "add_to_dictionary")) {
               this._operation = 6;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "send_copy")) {
               this._operation = 7;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "scan_for_viruses")) {
               this._operation = 8;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "drop")) {
               this._operation = 9;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "expand_to_list")) {
               this._operation = 10;
            } else if (StringUtilities.strEqualIgnoreCase(operation, "deliver_message")) {
               this._operation = 11;
            } else {
               this._operation = -1;
            }
         }
      }
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      if (StringUtilities.strEqualIgnoreCase(localName, "encrypt-to")) {
         return new GranularPolicyAction$TransientEncryptToElement(localName, this);
      } else if (StringUtilities.strEqualIgnoreCase(localName, "sign")
         || StringUtilities.strEqualIgnoreCase(localName, "key-not-found")
         || StringUtilities.strEqualIgnoreCase(localName, "encoding-format")) {
         return this;
      } else {
         return StringUtilities.strEqualIgnoreCase(localName, "chain-name")
            ? new GranularPolicyAction$TransientChainNameElement(localName, this)
            : super.handleStartElement(localName, attributes);
      }
   }

   @Override
   public void addChildElement(GranularPolicyElement childElement) {
      if (!(childElement instanceof GranularPolicyAction$TransientEncryptToElement)) {
         if (!(childElement instanceof GranularPolicyAction$TransientChainNameElement)) {
            super.addChildElement(childElement);
         } else {
            GranularPolicyAction$TransientChainNameElement transientChainNameElement = (GranularPolicyAction$TransientChainNameElement)childElement;
            this._chainName = transientChainNameElement._data_value;
         }
      } else {
         GranularPolicyAction$TransientEncryptToElement transientEncryptToElement = (GranularPolicyAction$TransientEncryptToElement)childElement;
         this._encryptToRecipient = transientEncryptToElement._recipientKey;
         this._requireTrustedRecipient = transientEncryptToElement._recipientKey_requireTrusted;
         this._requireEndToEndRecipient = transientEncryptToElement._recipientKey_requireEndToEnd;
         this._encryptToSender = transientEncryptToElement._senderKey;
         this._encryptToOtherKeyIDs = transientEncryptToElement._otherKey_data_value;
      }
   }

   @Override
   public void addProperty(String name, StringBuffer value) {
      if (StringUtilities.strEqualIgnoreCase(name, "sign")) {
         this._sign = GranularPolicyElement.getBooleanPropertyValue(value);
      } else if (StringUtilities.strEqualIgnoreCase(name, "key-not-found")) {
         this._keyNotFound = value.toString();
      } else {
         if (StringUtilities.strEqualIgnoreCase(name, "encoding-format")) {
            this._encodingFormat = value.toString();
         }
      }
   }

   @Override
   public String getDebugPrintInfo() {
      StringBuffer info = (StringBuffer)(new Object("Action \""));
      switch (this._operation) {
         case -1:
            info.append("Unknown");
            break;
         case 0:
         default:
            info.append("Send");
            if (this._encryptToRecipient) {
               info.append(", encrypt-to-recipient");
            }

            if (this._requireTrustedRecipient) {
               info.append(", require-trusted");
            }

            if (this._requireEndToEndRecipient) {
               info.append(", require-end-to-end");
            }

            if (this._encryptToSender) {
               info.append(", encrypt-to-sender");
            }

            if (this._encryptToOtherKeyIDs != null) {
               info.append(", encrypt-to-").append(this._encryptToOtherKeyIDs.length).append("-others");
            }

            if (this._sign) {
               info.append(", sign");
            }

            if (this._keyNotFound != null) {
               info.append(", knf=").append(this._keyNotFound);
            }

            if (this._encodingFormat != null) {
               info.append(", format=").append(this._encodingFormat);
            }
            break;
         case 1:
            info.append("Bounce");
            break;
         case 2:
            info.append("Verify");
            break;
         case 3:
            info.append("Send Web Messenger");
            break;
         case 4:
            info.append("Send Clear");
            break;
         case 5:
            info.append("Go To Chain");
            if (this._chainName != null) {
               info.append(", ").append(this._chainName);
            }
            break;
         case 6:
            info.append("Add To Dictionary");
            break;
         case 7:
            info.append("Send Copy");
            break;
         case 8:
            info.append("Scan For Viruses");
            break;
         case 9:
            info.append("Drop");
            break;
         case 10:
            info.append("Expand To List");
            break;
         case 11:
            info.append("Deliver Message");
      }

      info.append('"');
      return info.toString();
   }

   public int getOperation() {
      return this._operation;
   }

   public String getChainName() {
      return this._chainName;
   }

   public int getRequiredActions() {
      int requiredActions = 0;
      if (this._sign) {
         requiredActions |= 1;
      }

      if (this._encryptToRecipient) {
         requiredActions |= 2;
      }

      return requiredActions;
   }

   public String getKeyNotFound() {
      return this._keyNotFound;
   }

   public String getEncodingFormat() {
      return this._encodingFormat;
   }
}
