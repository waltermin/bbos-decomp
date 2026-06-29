package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class ResourceElement extends AbstractElement {
   private String _mimeType;
   private String _url;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitResourceElement(this);
   }

   @Override
   public final String getElementName() {
      return "resource";
   }

   public final String getMimeType() {
      return this._mimeType;
   }

   public final String getUrl() {
      return this._url;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("mimeType")) {
               this._mimeType = attValue;
            } else if (attName.equals("url")) {
               this._url = attValue;
            }
         }
      }
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer(120);
      buf.append("ResourceElement[name=").append(super._name).append(",url=").append(this._url).append(",mimeType=").append(this._mimeType).append(']');
      return buf.toString();
   }
}
