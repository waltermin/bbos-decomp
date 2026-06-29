package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import org.xml.sax.Attributes;

public final class EnumerationElement extends AbstractElement {
   private Vector _values = new Vector();

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitEnumerationElement(this);
   }

   @Override
   public final void addElementBody(String elementName, String elementBodyText) {
      this._values.addElement(elementBodyText.trim());
   }

   @Override
   public final String getElementName() {
      return "enumeration";
   }

   public final Vector getValues() {
      return this._values;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attName.equals("name")) {
            super._name = attValue;
         }
      }
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer(120);
      buf.append("EnumerationElement[name=").append(super._name).append(']');
      return buf.toString();
   }
}
