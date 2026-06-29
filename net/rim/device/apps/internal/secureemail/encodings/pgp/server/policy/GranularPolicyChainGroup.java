package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

class GranularPolicyChainGroup extends GranularPolicyElement {
   GranularPolicyChainGroup(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return StringUtilities.strEqualIgnoreCase(localName, "chain")
         ? new GranularPolicyChain(localName, this)
         : super.handleStartElement(localName, attributes);
   }

   @Override
   public String getDebugPrintInfo() {
      return "Chain Group";
   }

   public GranularPolicyChain locateChain(String chainName) {
      Vector childElements = this.getChildElements();
      int numChildElements = childElements.size();

      for (int i = 0; i < numChildElements; i++) {
         Object currentChildElement = childElements.elementAt(i);
         if (currentChildElement instanceof GranularPolicyChain) {
            GranularPolicyChain currentChain = (GranularPolicyChain)currentChildElement;
            if (chainName == null || StringUtilities.strEqual(chainName, currentChain.getName())) {
               return currentChain;
            }
         }
      }

      return null;
   }
}
