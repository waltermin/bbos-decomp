package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.util.StringUtilities;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptionElement;

final class HTMLOption extends HTMLGenericElement implements HTMLOptionElement {
   private String _text;
   private int _index;
   private HTMLSelect _select;

   final void setSelect(HTMLSelect select) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setIndex(int index) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setText(String value) {
      this._text = value;
      if (this._select != null) {
         HTMLSelectField field = (HTMLSelectField)this._select.getPeer();
         field.setOptionText(this._index, value, true);
      }
   }

   @Override
   public final String getText() {
      return this._text;
   }

   @Override
   public final void setDefaultSelected(boolean defaultSelected) {
      this.setAttributeValue(177, defaultSelected ? 1 : 0);
      if (this._select != null) {
         HTMLSelectField field = (HTMLSelectField)this._select.getPeer();
         field.setDefaultSelected(this._index, defaultSelected);
      }
   }

   @Override
   public final int getIndex() {
      return this._index;
   }

   @Override
   public final boolean getDefaultSelected() {
      return this.getAttributeValueAsBoolean(177, false);
   }

   @Override
   public final HTMLFormElement getForm() {
      return (HTMLFormElement)(this._select == null ? null : this._select.getForm());
   }

   @Override
   public final boolean getDisabled() {
      return this.getAttributeValueAsBoolean(117, false);
   }

   @Override
   public final void setDisabled(boolean disabled) {
      this.setAttributeValue(117, disabled ? 1 : 0);
   }

   @Override
   public final String getLabel() {
      return this.getAttributeValue(131);
   }

   @Override
   public final void setLabel(String label) {
      this.setAttributeValue(131, label);
   }

   @Override
   public final boolean getSelected() {
      if (this._select != null) {
         HTMLSelectField field = (HTMLSelectField)this._select.getPeer();
         return field.isOptionSet(this._index);
      } else {
         return false;
      }
   }

   @Override
   public final void setSelected(boolean selected) {
      if (this._select != null) {
         HTMLSelectField field = (HTMLSelectField)this._select.getPeer();
         field.setOption(this._index, selected);
      }
   }

   @Override
   public final String getValue() {
      return this.getAttributeValue(193);
   }

   @Override
   public final void setValue(String value) {
      this.setAttributeValue(193, value);
   }

   public HTMLOption(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final void setAttribute(String name, String value) {
      if (StringUtilities.strEqualIgnoreCase("text", name, 1701707776)) {
         this.setText(value);
      } else {
         super.setAttribute(name, value);
      }
   }

   @Override
   public final int getTagNameInt() {
      return 67;
   }
}
