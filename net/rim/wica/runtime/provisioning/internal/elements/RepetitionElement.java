package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class RepetitionElement extends CommonControlElement {
   private Vector _nestedElements;
   private String _mapping;
   private OnChangeElement _onChange;
   private String _layout;
   private boolean _collapsible;
   private int _visibleRows = Integer.MAX_VALUE;

   public RepetitionElement() {
      this._nestedElements = (Vector)(new Object());
   }

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      if (v.visitRepetitionElement(this)) {
         ProvisioningHelper.visitDefinitionElementsHelper(this._nestedElements, v);
      }
   }

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof CommonControlElement) {
         this._nestedElements.addElement(parameter);
      } else if (parameter instanceof OnChangeElement) {
         this._onChange = (OnChangeElement)parameter;
      } else {
         super.addChild(parameter);
      }
   }

   @Override
   public final String getElementName() {
      return "repetition";
   }

   public final String getMapping() {
      return this._mapping;
   }

   public final OnChangeElement getOnChange() {
      return this._onChange;
   }

   public final String getLayout() {
      return this._layout;
   }

   public final boolean isCollapsible() {
      return this._collapsible;
   }

   public final int getVisibleRows() {
      return this._visibleRows;
   }

   @Override
   public final void setAttributes(Attributes attributes) {
      super.setAttributes(attributes);

      for (int j = attributes.getLength() - 1; j >= 0; j--) {
         String attName = attributes.getLocalName(j);
         String attValue = attributes.getValue(j);
         if (attValue.length() != 0) {
            if (attName.equals("mapping")) {
               this._mapping = attValue;
            } else if (attName.equals("collapsible")) {
               this._collapsible = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("layout")) {
               this._layout = attValue;
            } else if (attName.equals("visibleRows")) {
               this._visibleRows = Integer.parseInt(attValue);
               if (this._visibleRows < 1) {
                  this._visibleRows = Integer.MAX_VALUE;
               }
            }
         }
      }
   }

   public final Vector getNestedElements() {
      return this._nestedElements;
   }
}
