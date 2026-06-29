package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

class GranularPolicyActionGroup extends GranularPolicyElement {
   GranularPolicyActionGroup(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return StringUtilities.strEqualIgnoreCase(localName, "action")
         ? new GranularPolicyAction(localName, attributes, this)
         : super.handleStartElement(localName, attributes);
   }

   @Override
   public String getDebugPrintInfo() {
      return "Action Group";
   }
}
