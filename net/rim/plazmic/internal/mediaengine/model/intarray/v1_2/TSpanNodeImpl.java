package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.TSpanNode;

public class TSpanNodeImpl extends VisualNodeImpl implements TSpanNode {
   public TSpanNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
      model._nodes[handle + 23] = -1;
   }

   int createTextAttr() {
      return TextAttrNodeImpl.createTextAttr(super._handle, super._model);
   }

   @Override
   public int getFontSize() {
      return TextAttrNodeImpl.getFontSize(super._handle, super._model);
   }

   @Override
   public void setFontSize(int fontSize) {
      TextAttrNodeImpl.setFontSize(fontSize, super._handle, super._model);
   }

   @Override
   public int getFontWeight() {
      return TextAttrNodeImpl.getFontWeight(super._handle, super._model);
   }

   @Override
   public void setFontWeight(int fontWeight) {
      TextAttrNodeImpl.setFontWeight(fontWeight, super._handle, super._model);
   }

   @Override
   public int getFontStyle() {
      return TextAttrNodeImpl.getFontStyle(super._handle, super._model);
   }

   @Override
   public void setFontStyle(int fontStyle) {
      TextAttrNodeImpl.setFontStyle(fontStyle, super._handle, super._model);
   }

   @Override
   public int getTextDecoration() {
      return TextAttrNodeImpl.getTextDecoration(super._handle, super._model);
   }

   @Override
   public void setTextDecoration(int decoration) {
      TextAttrNodeImpl.setTextDecoration(decoration, super._handle, super._model);
   }

   @Override
   public String getFontFamily() {
      return TextAttrNodeImpl.getFontFamily(super._handle, super._model);
   }

   @Override
   public void setFontFamily(String fontFamily) {
      TextAttrNodeImpl.setFontFamily(fontFamily, super._handle, super._model);
   }

   @Override
   public int getStringStart() {
      return getStringStart(super._handle, super._model);
   }

   public static int getStringStart(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 25];
   }

   @Override
   public void setStringStart(int stringStart) {
      setStringStart(stringStart, super._handle, super._model);
   }

   public static void setStringStart(int stringStart, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 25] = stringStart;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   @Override
   public void setString(char[] s) {
      setString(s, super._handle, super._model);
   }

   public static void setString(char[] s, int handle, ModelInteractorImpl model) {
      int parent = NodeImpl.getParent(handle, model);
      int sibling = NodeImpl.getNextSibling(handle, model);
      char[] curStr = TextNodeImpl.getString(parent, model);
      int curStart = getStringStart(handle, model);
      int curEnd;
      if (sibling == -1) {
         curEnd = curStr.length;
      } else {
         curEnd = getStringStart(sibling, model);
      }

      int oldTSpanLength = curEnd - curStart;
      int newTSpanLength = s.length;
      int oldStringLength = curStr.length;
      char[] newString = new char[oldStringLength + newTSpanLength - oldTSpanLength];
      System.arraycopy(curStr, 0, newString, 0, curStart);
      System.arraycopy(s, 0, newString, curStart, newTSpanLength);
      System.arraycopy(curStr, curEnd, newString, curStart + newTSpanLength, oldStringLength - curEnd);
      TextNodeImpl.setString(newString, parent, model);
      if (sibling != -1) {
         setStringStart(curStart + newTSpanLength, sibling, model);
      }
   }

   @Override
   public int getDY() {
      return getDY(super._handle, super._model);
   }

   public static int getDY(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 27];
   }

   @Override
   public void setDY(int dy) {
      setDY(dy, super._handle, super._model);
   }

   public static void setDY(int dy, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 27] = dy;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   @Override
   public int getDX() {
      return getDX(super._handle, super._model);
   }

   public static int getDX(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 26];
   }

   @Override
   public void setDX(int dx) {
      setDX(dx, super._handle, super._model);
   }

   public static void setDX(int dx, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 26] = dx;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }
}
