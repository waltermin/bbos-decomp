package net.rim.device.api.xml.jaxp;

import org.w3c.dom.CharacterData;

class DOMCharacterDataImpl extends DOMNodeImpl implements CharacterData {
   DOMCharacterDataImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeValue() {
      return this.getData();
   }

   @Override
   public void setNodeValue(String nodeValue) {
      this.setData(nodeValue);
   }

   @Override
   public String getData() {
      return super._ir.getTextString(super._node);
   }

   @Override
   public void setData(String data) {
      super._ir.notReadOnly(super._node);
      super._ir.setTextData(super._node, data);
   }

   @Override
   public int getLength() {
      return super._ir.getTextLength(super._node);
   }

   @Override
   public String substringData(int offset, int count) {
      try {
         return super._ir.getTextSubstring(super._node, offset, count);
      } finally {
         throw new Object((short)1, "");
      }
   }

   @Override
   public void appendData(String arg) {
      super._ir.notReadOnly(super._node);
      super._ir.appendTextData(super._node, arg);
   }

   @Override
   public void insertData(int offset, String arg) {
      super._ir.notReadOnly(super._node);
      if (!super._ir.insertTextData(super._node, offset, arg)) {
         throw new Object((short)1, "");
      }
   }

   @Override
   public void deleteData(int offset, int count) {
      super._ir.notReadOnly(super._node);
      if (!super._ir.deleteTextData(super._node, offset, count)) {
         throw new Object((short)1, "");
      }
   }

   @Override
   public void replaceData(int offset, int count, String arg) {
      super._ir.notReadOnly(super._node);
      if (!super._ir.replaceTextData(super._node, offset, count, arg)) {
         throw new Object((short)1, "");
      }
   }
}
