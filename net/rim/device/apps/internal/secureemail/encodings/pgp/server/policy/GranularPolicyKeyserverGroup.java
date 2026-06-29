package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

class GranularPolicyKeyserverGroup extends GranularPolicyElement {
   GranularPolicyKeyserverGroup(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return StringUtilities.strEqualIgnoreCase(localName, "rule-keyserver")
         ? new GranularPolicyKeyserver(localName, this)
         : super.handleStartElement(localName, attributes);
   }

   @Override
   public String getDebugPrintInfo() {
      return "Keyserver Group";
   }
}
