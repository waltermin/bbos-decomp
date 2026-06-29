package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

final class GranularPolicyAction$TransientDataElement extends GranularPolicyElement {
   public String _value;

   public GranularPolicyAction$TransientDataElement(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public final GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return StringUtilities.strEqualIgnoreCase(localName, "value") ? this : super.handleStartElement(localName, attributes);
   }

   @Override
   public final void addProperty(String name, StringBuffer value) {
      if (StringUtilities.strEqualIgnoreCase(name, "value")) {
         this._value = value.toString();
      }
   }
}
