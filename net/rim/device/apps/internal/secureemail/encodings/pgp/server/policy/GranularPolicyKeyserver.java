package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

class GranularPolicyKeyserver extends GranularPolicyElement {
   private String _name;
   private String _keyserver;

   public GranularPolicyKeyserver(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return !StringUtilities.strEqualIgnoreCase(localName, "type")
            && !StringUtilities.strEqualIgnoreCase(localName, "name")
            && !StringUtilities.strEqualIgnoreCase(localName, "keyserver")
            && !StringUtilities.strEqualIgnoreCase(localName, "port")
            && !StringUtilities.strEqualIgnoreCase(localName, "keyserver-type")
            && !StringUtilities.strEqualIgnoreCase(localName, "trusted")
            && !StringUtilities.strEqualIgnoreCase(localName, "default-set")
         ? super.handleStartElement(localName, attributes)
         : this;
   }

   @Override
   public void addProperty(String name, StringBuffer value) {
      if (StringUtilities.strEqualIgnoreCase(name, "name")) {
         this._name = value.toString();
      } else {
         if (StringUtilities.strEqualIgnoreCase(name, "keyserver")) {
            this._keyserver = value.toString();
         }
      }
   }

   @Override
   public String getDebugPrintInfo() {
      return ((StringBuffer)(new Object("Keyserver \""))).append(this._name).append("\" (").append(this._keyserver).append(")").toString();
   }
}
