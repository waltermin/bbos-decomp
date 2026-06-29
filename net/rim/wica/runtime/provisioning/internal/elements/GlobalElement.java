package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class GlobalElement extends FieldElement {
   private boolean _persist = false;
   private Vector _values = (Vector)(new Object());

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      v.visitGlobalElement(this);
   }

   @Override
   public final void addElementBody(String elementName, String elementBodyText) {
      this._values.addElement(elementBodyText);
   }

   @Override
   public final boolean equals(Object other) {
      if (!(other instanceof GlobalElement)) {
         return false;
      }

      GlobalElement ge = (GlobalElement)other;
      return this.getName().equals(ge.getName());
   }

   @Override
   public final int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public final String getElementName() {
      return "global";
   }

   public final Vector getValues() {
      return this._values;
   }

   public final boolean hasValues() {
      return this._values != null && !this._values.isEmpty();
   }

   public final boolean isPersist() {
      return this._persist;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);
      String attValue = attributes.getValue("persist");
      if (attValue != null) {
         this._persist = ProvisioningHelper.parseBoolean(attValue);
      }
   }

   @Override
   public final String toString() {
      StringBuffer buf = (StringBuffer)(new Object(120));
      buf.append("GlobalElement[name=")
         .append(super._name)
         .append(",type=")
         .append(super._type)
         .append(",component=")
         .append(super._component)
         .append(",array=")
         .append(super._array)
         .append(",persists=")
         .append(this._persist)
         .append(",value=")
         .append(this._values)
         .append(']');
      return buf.toString();
   }
}
