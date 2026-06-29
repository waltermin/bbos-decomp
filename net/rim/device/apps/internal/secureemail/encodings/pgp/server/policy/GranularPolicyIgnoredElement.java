package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

class GranularPolicyIgnoredElement extends GranularPolicyElement {
   GranularPolicyIgnoredElement(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public String getDebugPrintInfo() {
      return ((StringBuffer)(new Object())).append(this.getXMLTag()).append(" (ignored)").toString();
   }
}
