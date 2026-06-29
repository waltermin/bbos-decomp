package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

class GranularPolicyChain extends GranularPolicyElement implements GranularPolicyOrderProvider {
   private String _name;
   private int _order;
   private boolean _clientSafe;

   public GranularPolicyChain(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      if (StringUtilities.strEqualIgnoreCase(localName, "rule")) {
         return new GranularPolicyRule(localName, this);
      } else {
         return StringUtilities.strEqualIgnoreCase(localName, "dict")
               || !StringUtilities.strEqualIgnoreCase(localName, "name")
                  && !StringUtilities.strEqualIgnoreCase(localName, "revision")
                  && !StringUtilities.strEqualIgnoreCase(localName, "uuid")
                  && !StringUtilities.strEqualIgnoreCase(localName, "order")
                  && !StringUtilities.strEqualIgnoreCase(localName, "server-safe")
                  && !StringUtilities.strEqualIgnoreCase(localName, "client-safe")
                  && !StringUtilities.strEqualIgnoreCase(localName, "legacy-safe")
            ? super.handleStartElement(localName, attributes)
            : this;
      }
   }

   @Override
   public void addProperty(String name, StringBuffer value) {
      if (StringUtilities.strEqualIgnoreCase(name, "name")) {
         this._name = value.toString();
      } else if (StringUtilities.strEqualIgnoreCase(name, "order")) {
         this._order = GranularPolicyElement.getIntegerPropertyValue(value);
      } else {
         if (StringUtilities.strEqualIgnoreCase(name, "client-safe")) {
            this._clientSafe = GranularPolicyElement.getBooleanPropertyValue(value);
         }
      }
   }

   public String getName() {
      return this._name;
   }

   @Override
   public int getOrder() {
      return this._order;
   }

   @Override
   public String getDebugPrintInfo() {
      return "Chain \"" + this._name + "\"";
   }
}
