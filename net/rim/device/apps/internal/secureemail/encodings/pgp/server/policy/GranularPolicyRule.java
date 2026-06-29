package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import org.xml.sax.Attributes;

class GranularPolicyRule extends GranularPolicyElement implements GranularPolicyEvaluateProvider, GranularPolicyOrderProvider {
   private String _name;
   private int _order;
   private boolean _enabled;
   private boolean _clientSafe;

   public GranularPolicyActionGroup getActionGroup() {
      Vector childElements = this.getChildElements();
      int numChildElements = childElements.size();

      for (int i = 0; i < numChildElements; i++) {
         GranularPolicyElement currentChildElement = (GranularPolicyElement)childElements.elementAt(i);
         if (currentChildElement instanceof GranularPolicyActionGroup) {
            return (GranularPolicyActionGroup)currentChildElement;
         }
      }

      return null;
   }

   @Override
   public boolean evaluate(String recipientAddress, ServiceRecord serviceRecord, EmailMessageModel emailMessageModel) {
      if (this._enabled && this._clientSafe) {
         Vector childElements = this.getChildElements();
         int numChildElements = childElements.size();

         for (int i = 0; i < numChildElements; i++) {
            Object currentChildElement = childElements.elementAt(i);
            if (currentChildElement instanceof GranularPolicyEvaluateProvider) {
               GranularPolicyEvaluateProvider granularPolicyEvaluateProvider = (GranularPolicyEvaluateProvider)currentChildElement;
               return granularPolicyEvaluateProvider.evaluate(recipientAddress, serviceRecord, emailMessageModel);
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public int getOrder() {
      return this._order;
   }

   @Override
   public void addProperty(String name, StringBuffer value) {
      if (StringUtilities.strEqualIgnoreCase(name, "name")) {
         this._name = value.toString();
      } else if (StringUtilities.strEqualIgnoreCase(name, "order")) {
         this._order = GranularPolicyElement.getIntegerPropertyValue(value);
      } else if (StringUtilities.strEqualIgnoreCase(name, "enabled")) {
         this._enabled = GranularPolicyElement.getBooleanPropertyValue(value);
      } else {
         if (StringUtilities.strEqualIgnoreCase(name, "client-safe")) {
            this._clientSafe = GranularPolicyElement.getBooleanPropertyValue(value);
         }
      }
   }

   @Override
   public String getDebugPrintInfo() {
      return ((StringBuffer)(new Object("Rule \""))).append(this._name).append("\"").toString();
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      if (StringUtilities.strEqualIgnoreCase(localName, "group")) {
         return new GranularPolicyExpressionGroup(localName, attributes, this);
      } else if (StringUtilities.strEqualIgnoreCase(localName, "actions")) {
         return new GranularPolicyActionGroup(localName, this);
      } else if (StringUtilities.strEqualIgnoreCase(localName, "rule-keyservers")) {
         return new GranularPolicyKeyserverGroup(localName, this);
      } else {
         return !StringUtilities.strEqualIgnoreCase(localName, "name")
               && !StringUtilities.strEqualIgnoreCase(localName, "description")
               && !StringUtilities.strEqualIgnoreCase(localName, "order")
               && !StringUtilities.strEqualIgnoreCase(localName, "server-safe")
               && !StringUtilities.strEqualIgnoreCase(localName, "client-safe")
               && !StringUtilities.strEqualIgnoreCase(localName, "legacy-safe")
               && !StringUtilities.strEqualIgnoreCase(localName, "enabled")
               && !StringUtilities.strEqualIgnoreCase(localName, "key-lookup-enabled")
            ? super.handleStartElement(localName, attributes)
            : this;
      }
   }

   public GranularPolicyRule(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }
}
