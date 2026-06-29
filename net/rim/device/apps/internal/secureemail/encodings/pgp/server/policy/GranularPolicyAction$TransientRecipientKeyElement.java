package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

final class GranularPolicyAction$TransientRecipientKeyElement extends GranularPolicyElement {
   public boolean _requireTrusted;
   public boolean _requireEndToEnd;

   public GranularPolicyAction$TransientRecipientKeyElement(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public final GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return !StringUtilities.strEqualIgnoreCase(localName, "require-trusted") && !StringUtilities.strEqualIgnoreCase(localName, "require-end-to-end")
         ? super.handleStartElement(localName, attributes)
         : this;
   }

   @Override
   public final void addProperty(String name, StringBuffer value) {
      if (StringUtilities.strEqualIgnoreCase(name, "require-trusted")) {
         this._requireTrusted = GranularPolicyElement.getBooleanPropertyValue(value);
      } else {
         if (StringUtilities.strEqualIgnoreCase(name, "require-end-to-end")) {
            this._requireEndToEnd = GranularPolicyElement.getBooleanPropertyValue(value);
         }
      }
   }
}
