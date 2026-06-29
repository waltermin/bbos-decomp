package net.rim.device.apps.internal.secureemail.encodings.pgp.server.policy;

import net.rim.device.api.util.StringUtilities;
import org.xml.sax.Attributes;

final class GranularPolicyExpression$TransientHSElement extends GranularPolicyElement {
   public String _data_value;

   public GranularPolicyExpression$TransientHSElement(String xmlTag, GranularPolicyElement parentElement) {
      super(xmlTag, parentElement);
   }

   @Override
   public final GranularPolicyElement handleStartElement(String localName, Attributes attributes) {
      return StringUtilities.strEqualIgnoreCase(localName, "data")
         ? new GranularPolicyExpression$TransientDataElement(localName, this)
         : super.handleStartElement(localName, attributes);
   }

   @Override
   public final void addChildElement(GranularPolicyElement childElement) {
      if (!(childElement instanceof GranularPolicyExpression$TransientDataElement)) {
         super.addChildElement(childElement);
      } else {
         GranularPolicyExpression$TransientDataElement transientDataElement = (GranularPolicyExpression$TransientDataElement)childElement;
         this._data_value = transientDataElement._value;
      }
   }
}
