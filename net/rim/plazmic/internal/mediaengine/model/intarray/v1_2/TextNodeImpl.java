package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.TSpanNode;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

public class TextNodeImpl extends VisualNodeImpl implements TextNode {
   public TextNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
      model._nodes[handle + 23] = -1;
      model._nodes[handle + 24] = -1;
      model._nodes[handle + 6] = -1;
      model._nodes[handle + 7] = -1;
      model._nodes[handle + 29] = -1;
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
   public char[] getString() {
      return getString(super._handle, super._model);
   }

   public static char[] getString(int handle, ModelInteractorImpl model) {
      int stringIndex = model._nodes[handle + 24];
      return stringIndex >= 0 ? model.getConvertedTextString(stringIndex) : null;
   }

   @Override
   public void setString(char[] string) {
      setString(string, super._handle, super._model);
   }

   public static void setString(char[] string, int handle, ModelInteractorImpl model) {
      int currentTSpan = getFirstTSpan(handle, model);

      while (currentTSpan >= 0) {
         currentTSpan = NodeImpl.deleteNodeHandle(currentTSpan, model);
      }

      int stringIndex = model._nodes[handle + 24];
      if (stringIndex >= 0) {
         model.setConvertedTextString(stringIndex, string);
      } else {
         stringIndex = model.addConvertedTextString(string);
         model._nodes[handle + 24] = stringIndex;
      }

      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }

   @Override
   public TSpanNode getFirstTSpan() {
      return (TSpanNode)super._model.getNodeObject(getFirstTSpan(super._handle, super._model));
   }

   public static int getFirstTSpan(int textHandle, ModelInteractorImpl model) {
      return model._nodes[textHandle + 6];
   }

   static void setFirstTSpan(int firstHandle, int textHandle, ModelInteractorImpl model) {
      if (textHandle >= 0) {
         model._nodes[textHandle + 6] = firstHandle;
         NodeImpl.setDirtyBits(textHandle, model, -16777216);
      }
   }

   @Override
   public TSpanNode getLastTSpan() {
      return (TSpanNode)super._model.getNodeObject(getLastTSpan(super._handle, super._model));
   }

   public static int getLastTSpan(int textHandle, ModelInteractorImpl model) {
      return model._nodes[textHandle + 7];
   }

   static void setLastTSpan(int lastHandle, int textHandle, ModelInteractorImpl model) {
      if (textHandle >= 0) {
         model._nodes[textHandle + 7] = lastHandle;
         NodeImpl.setDirtyBits(textHandle, model, -16777216);
      }
   }

   private static void setOnlyTSpan(int onlyTSpan, int textHandle, ModelInteractorImpl model) {
      setFirstTSpan(onlyTSpan, textHandle, model);
      setLastTSpan(onlyTSpan, textHandle, model);
      NodeImpl.setParent(textHandle, onlyTSpan, model);
   }

   @Override
   public void insertFirstTSpan(TSpanNode firstTSpan) {
      insertFirstTSpan(((NodeImpl)firstTSpan).getHandle(), super._handle, super._model);
   }

   public static void insertFirstTSpan(int firstTSpan, int textHandle, ModelInteractorImpl model) {
      if (NodeImpl.isInSceneGraph(firstTSpan, model) || NodeImpl.getType(textHandle, model) != 50 || NodeImpl.getType(firstTSpan, model) != 32) {
         throw new Object("The node to be inserted already exists in the scene graph, isn't a TSpan node, or the parent is not a text node.");
      }

      if (getLastTSpan(textHandle, model) < 0) {
         setOnlyTSpan(firstTSpan, textHandle, model);
      } else {
         NodeImpl.insertPreviousSibling(firstTSpan, getFirstTSpan(textHandle, model), model);
      }
   }

   @Override
   public void insertLastTSpan(TSpanNode lastTSpan) {
      insertLastTSpan(((NodeImpl)lastTSpan).getHandle(), super._handle, super._model);
   }

   public static void insertLastTSpan(int lastTSpan, int textHandle, ModelInteractorImpl model) {
      if (NodeImpl.isInSceneGraph(lastTSpan, model) || NodeImpl.getType(textHandle, model) != 50 || NodeImpl.getType(lastTSpan, model) != 32) {
         throw new Object("The node to be inserted already exists in the scene graph, isn't a TSpan node, or the parent is not a text node.");
      }

      if (getFirstTSpan(textHandle, model) < 0) {
         setOnlyTSpan(lastTSpan, textHandle, model);
      } else {
         NodeImpl.insertNextSibling(lastTSpan, getLastTSpan(textHandle, model), model);
      }
   }

   static boolean checkTSpan(int node1, int node2, ModelInteractorImpl model) {
      return NodeImpl.getType(node1, model) == 32 == (NodeImpl.getType(node2, model) == 32);
   }

   static void delete(int handle, ModelInteractorImpl model) {
      int stringIndex = model._nodes[handle + 24];
      if (stringIndex >= 0) {
         model.removeConvertedTextString(stringIndex);
      }
   }

   @Override
   public int getWidth() {
      return getWidth(super._handle, super._model);
   }

   public static int getWidth(int handle, ModelInteractorImpl model) {
      int bits = model._nodes[handle + 8];
      return (bits & 262144) != 0 ? model._nodes[handle + 28] : Integer.MIN_VALUE;
   }

   @Override
   public void setWidth(int width) {
      setWidth(width, super._handle, super._model);
   }

   public static void setWidth(int width, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 8] = model._nodes[handle + 8] | 262144;
      model._nodes[handle + 28] = width;
      NodeImpl.setDirtyBits(handle, model, Integer.MIN_VALUE);
   }
}
