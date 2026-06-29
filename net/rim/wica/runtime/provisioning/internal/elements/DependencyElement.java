package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class DependencyElement extends AbstractElement {
   private String _type = "application";
   private String _value;
   private String _version;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitDependencyElement(this);
   }

   @Override
   public final String getElementName() {
      return "dependency";
   }

   public final String getType() {
      return this._type;
   }

   public final String getValue() {
      return this._value;
   }

   public final String getVersion() {
      return this._version;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attName.equals("type")) {
            this._type = attValue;
         } else if (attName.equals("value")) {
            this._value = attValue;
         } else if (attName.equals("version")) {
            this._version = attValue;
         }
      }
   }

   @Override
   public final String toString() {
      StringBuffer buf = (StringBuffer)(new Object(120));
      buf.append("DependencyElement[type=").append(this._type).append(",value=").append(this._value).append(",version=").append(this._version).append(']');
      return buf.toString();
   }
}
