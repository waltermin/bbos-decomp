package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.GroupNode;
import net.rim.plazmic.internal.mediaengine.service.node.Node;

public class GroupNodeImpl extends VisualNodeImpl implements GroupNode {
   public GroupNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      VisualNodeImpl.initVisual(handle, model);
      model._nodes[handle + 23] = -1;
      model._nodes[handle + 24] = -2;
      model._nodes[handle + 6] = -1;
      model._nodes[handle + 7] = -1;
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
   public int getCurrentChild() {
      return getCurrentChild(super._handle, super._model);
   }

   public static int getCurrentChild(int handle, ModelInteractorImpl model) {
      return model._nodes[handle + 24];
   }

   @Override
   public void setCurrentChild(int currentChild) {
      setCurrentChild(currentChild, super._handle, super._model);
   }

   public static void setCurrentChild(int currentChild, int handle, ModelInteractorImpl model) {
      model._nodes[handle + 24] = currentChild;
      NodeImpl.setDirtyBits(handle, model, -16777216);
   }

   @Override
   public Node getFirstChild() {
      return super._model.getNodeObject(ContainerNodeImpl.getFirstChild(super._handle, super._model));
   }

   @Override
   public Node getLastChild() {
      return super._model.getNodeObject(ContainerNodeImpl.getLastChild(super._handle, super._model));
   }

   @Override
   public void insertFirstChild(Node nodeToInsert) {
      ContainerNodeImpl.insertFirstChild(((NodeImpl)nodeToInsert).getHandle(), super._handle, super._model);
   }

   @Override
   public void insertLastChild(Node nodeToInsert) {
      ContainerNodeImpl.insertLastChild(((NodeImpl)nodeToInsert).getHandle(), super._handle, super._model);
   }

   @Override
   public void deleteAllChildren() {
      Node currentChild = this.getFirstChild();

      while (currentChild != null) {
         currentChild = currentChild.deleteNode();
      }
   }
}
