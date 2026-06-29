package net.rim.wica.runtime.provisioning.internal.elements;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.DefinitionVisitor;
import net.rim.wica.runtime.provisioning.internal.ProvisioningHelper;
import org.xml.sax.Attributes;

public final class TableElement extends CommonControlElement implements TableStyling {
   private Vector _nestedElements;
   private OnChangeElement _onChange;
   private OnMoreDataElement _onMoreData;
   private String _mapping;
   private int _visibleRows = Integer.MAX_VALUE;
   private boolean _showGridline = true;
   private boolean _showRowSelector = true;
   private boolean _showHeader = true;
   private ElementReference _headerStyle;
   private ElementReference _evenRowStyle;
   private ElementReference _oddRowStyle;
   private String _gridlineColor;

   public TableElement() {
      this._nestedElements = new Vector();
   }

   @Override
   public final String getElementName() {
      return "table";
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
            } else if (attName.equals("headerStyle")) {
               this._headerStyle = new StyleElementReference(this, attValue);
            } else if (attName.equals("oddRowStyle")) {
               this._oddRowStyle = new StyleElementReference(this, attValue);
            } else if (attName.equals("evenRowStyle")) {
               this._evenRowStyle = new StyleElementReference(this, attValue);
            } else if (attName.equals("gridlineColor")) {
               this._gridlineColor = attValue;
            } else if (attName.equals("showGridline")) {
               this._showGridline = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("showRowSelector")) {
               this._showRowSelector = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("showHeader")) {
               this._showHeader = ProvisioningHelper.parseBoolean(attValue);
            } else if (attName.equals("visibleRows")) {
               this._visibleRows = Integer.parseInt(attValue);
               if (this._visibleRows < 1) {
                  this._visibleRows = Integer.MAX_VALUE;
               }
            }
         }
      }
   }

   @Override
   public final void accept(DefinitionVisitor v) {
      v.setCurrentElementVisited(this);
      if (v.visitTableElement(this)) {
         ProvisioningHelper.visitDefinitionElementsHelper(this._nestedElements, v);
      }
   }

   @Override
   public final void addChild(AbstractElement parameter) {
      if (parameter instanceof ColumnElement) {
         this._nestedElements.addElement(parameter);
      } else if (parameter instanceof OnChangeElement) {
         this._onChange = (OnChangeElement)parameter;
      } else if (parameter instanceof OnMoreDataElement) {
         this._onMoreData = (OnMoreDataElement)parameter;
      } else {
         super.addChild(parameter);
      }
   }

   public final Vector getNestedElements() {
      return this._nestedElements;
   }

   public final OnChangeElement getOnChange() {
      return this._onChange;
   }

   public final OnMoreDataElement getOnMoreData() {
      return this._onMoreData;
   }

   public final String getGridlineColor() {
      return this._gridlineColor;
   }

   public final String getMapping() {
      return this._mapping;
   }

   public final boolean isShowGridline() {
      return this._showGridline;
   }

   public final boolean isShowHeader() {
      return this._showHeader;
   }

   public final boolean isShowRowSelector() {
      return this._showRowSelector;
   }

   public final int getVisibleRows() {
      return this._visibleRows;
   }

   @Override
   public final StyleElement getEvenRowStyle() {
      return this._evenRowStyle != null ? (StyleElement)this._evenRowStyle.resolve() : null;
   }

   @Override
   public final StyleElement getHeaderStyle() {
      return this._headerStyle != null ? (StyleElement)this._headerStyle.resolve() : null;
   }

   @Override
   public final StyleElement getOddRowStyle() {
      return this._oddRowStyle != null ? (StyleElement)this._oddRowStyle.resolve() : null;
   }
}
