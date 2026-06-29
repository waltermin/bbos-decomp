package net.rim.wica.runtime.provisioning.internal.elements;

import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class MappedFieldElement extends AbstractElement {
   private String _mapping;

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitMappedFieldElement(this);
   }

   @Override
   public final String getElementName() {
      return "mappedField";
   }

   public final String getMapping() {
      return this._mapping;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("name")) {
               super._name = attValue;
            } else if (attName.equals("mapping")) {
               this._mapping = attValue;
            }
         }
      }
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer(128);
      buf.append("MappedFieldElement[name=").append(super._name).append(",mapping=").append(this._mapping).append(']');
      return buf.toString();
   }
}
