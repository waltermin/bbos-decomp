package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.system.Application;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLOptionsCollection;
import org.w3c.dom.html2.HTMLSelectElement;

final class HTMLSelect extends HTMLInput implements HTMLSelectElement {
   private HTMLCollectionImpl _elements = new HTMLCollectionImpl();

   final void addOption(HTMLOption option, byte appendFlags) {
      option.setIndex(this._elements.getLength());
      option.setSelect(this);
      this._elements.addItem(null, option);
      if (super._uiPeerType == 9) {
         String text = option.getText();
         if (text == null) {
            text = "";
         }

         if (option.getDefaultSelected()) {
            appendFlags = (byte)(appendFlags | 2);
         }

         if (option.getParentNode() instanceof HTMLOptGroup) {
            appendFlags = (byte)(appendFlags | 8);
         }

         synchronized (Application.getEventLock()) {
            ((HTMLSelectField)super._uiPeer).addOption(text, appendFlags);
         }
      }
   }

   public final Node appendChild(Node newChild, byte appendFlags) {
      if (newChild instanceof HTMLOption) {
         this.addOption((HTMLOption)newChild, appendFlags);
      } else if (newChild instanceof HTMLOptGroup) {
         this.addOptGroup((HTMLOptGroup)newChild);
      }

      return super.appendChild(newChild);
   }

   @Override
   public final void setLength(int length) {
      int oldLength = this._elements.getLength();
      if (oldLength > length) {
         this._elements.setLength(length);
         synchronized (Application.getEventLock()) {
            if (super._uiPeer != null) {
               ((HTMLSelectField)super._uiPeer).setLength(length);
            }
         }
      } else {
         Document doc = this.getOwnerDocument();

         for (int i = oldLength; i < length; i++) {
            Node node = doc.createElement("option");
            this.appendChild(node);
         }
      }
   }

   @Override
   public final HTMLOptionsCollection getOptions() {
      return this._elements;
   }

   @Override
   public final boolean getMultiple() {
      return this.getAttributeValueAsBoolean(141, false);
   }

   @Override
   public final void setMultiple(boolean multiple) {
      this.setAttributeValue(141, multiple ? 1 : 0);
   }

   @Override
   public final void setSelectedIndex(int selectedIndex) {
      if (super._uiPeer != null) {
         ((HTMLSelectField)super._uiPeer).toggleOption(selectedIndex, true);
      }
   }

   @Override
   public final int getSelectedIndex() {
      return super._uiPeer == null ? 0 : ((HTMLSelectField)super._uiPeer).getSelectedIndex();
   }

   @Override
   public final void add(HTMLElement element, HTMLElement before) {
      if (before == null) {
         this.appendChild(element);
      } else {
         if (element instanceof HTMLOption && before instanceof HTMLOption) {
            HTMLOption beforeOption = (HTMLOption)before;
            HTMLOption option = (HTMLOption)element;
            int index = beforeOption.getIndex();
            option.setIndex(index);
            option.setSelect(this);
            this._elements.addItem(index, null, element);
            if (super._uiPeerType == 9) {
               String text = option.getText();
               if (text == null) {
                  text = "";
               }

               byte appendFlags = 0;
               if (option.getDefaultSelected()) {
                  appendFlags = (byte)(appendFlags | 2);
               }

               if (option.getParentNode() instanceof HTMLOptGroup) {
                  appendFlags = (byte)(appendFlags | 8);
               }

               ((HTMLSelectField)super._uiPeer).addOption(text, appendFlags);
            }
         }
      }
   }

   @Override
   public final void remove(int index) {
      this._elements.remove(index);

      for (int i = this._elements.getLength() - 1; i >= index; i--) {
         ((HTMLOption)this._elements.item(i)).setIndex(i);
      }
   }

   @Override
   public final boolean getDisabled() {
      return super._uiPeer == null ? false : (((HTMLSelectField)super._uiPeer).getStyle() & 45035996273704960L) != 0;
   }

   @Override
   public final int getSize() {
      return this.getAttributeValueAsInt(179);
   }

   @Override
   public final void setSize(int size) {
      this.setAttributeValue(179, size);
      synchronized (Application.getEventLock()) {
         if (super._uiPeerType == 9) {
            ((HTMLSelectField)super._uiPeer).setSize(size);
         }
      }
   }

   @Override
   public final Node getLastChild() {
      int count = this._elements.getLength();
      return count <= 0 ? null : this._elements.item(count - 1);
   }

   @Override
   public final Node appendChild(Node newChild) {
      return this.appendChild(newChild, (byte)0);
   }

   @Override
   public final String getType() {
      return this.getMultiple() ? "select-multiple" : "select-one";
   }

   public HTMLSelect(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(76, dom, nodeId);
   }

   private final void addOptGroup(HTMLOptGroup optGroup) {
      if (super._uiPeerType == 9) {
         String text = optGroup.getLabel();
         if (text == null) {
            text = "";
         }

         synchronized (Application.getEventLock()) {
            ((HTMLSelectField)super._uiPeer).addOption(text, (byte)4);
         }
      }
   }

   @Override
   public final Node replaceChild(Node newChild, Node oldChild) {
      if (newChild instanceof HTMLOption && oldChild instanceof HTMLOption) {
         HTMLOption option = (HTMLOption)newChild;
         HTMLOption oldOption = (HTMLOption)oldChild;
         int index = oldOption.getIndex();
         option.setSelect(this);
         option.setIndex(index);
         this._elements.replace(index, null, option);
         if (super._uiPeerType == 9) {
            String text = option.getText();
            if (text == null) {
               text = "";
            }

            HTMLSelectField select = (HTMLSelectField)super._uiPeer;
            select.setDefaultSelected(index, option.getDefaultSelected());
            select.setOptionText(index, text, true);
         }
      }

      return newChild;
   }

   @Override
   public final void setDisabled(boolean disabled) {
   }

   @Override
   public final int getLength() {
      return super._uiPeer == null ? 0 : ((HTMLSelectField)super._uiPeer).getLength();
   }

   @Override
   final void onChange() {
      synchronized (this) {
         super._dropEvents = true;
      }

      HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();

      label43:
      try {
         parent.executeJavaScriptAction(this, this.getAttributeValue(149), null);
         parent.executeJavaScriptAction(this, this.getAttributeValue(148), null);
      } finally {
         break label43;
      }

      synchronized (this) {
         super._dropEvents = false;
      }
   }

   @Override
   public final void submit(HTMLContext context) {
      if (super._uiPeerType != 9) {
         super.submit(context);
      } else {
         HTMLSelectField select = (HTMLSelectField)super._uiPeer;
         int size = select.getLength();
         if (size > 0 && context != null) {
            StringBuffer buffer = (StringBuffer)(new Object(size));

            for (int index = 0; index < size; index++) {
               buffer.append(select.isOptionSet(index) ? 1 : 0);
            }

            context.put(super._uid, buffer.toString());
         }
      }
   }

   @Override
   public final boolean loadFrom(HTMLContext context) {
      if (super._uiPeerType != 9) {
         return super.loadFrom(context);
      }

      HTMLSelectField select = (HTMLSelectField)super._uiPeer;
      select.resetOptions();
      if (context != null) {
         String value = context.get(super._uid);
         if (value != null) {
            int size = value.length();

            try {
               for (int index = 0; index < size; index++) {
                  select.setOption(index, value.charAt(index) == '1');
               }
            } finally {
               return true;
            }

            return true;
         }
      }

      return false;
   }
}
