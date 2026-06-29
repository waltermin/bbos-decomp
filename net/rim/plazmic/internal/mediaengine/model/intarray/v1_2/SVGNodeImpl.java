package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.service.node.Node;
import net.rim.plazmic.internal.mediaengine.service.node.SVGNode;

public class SVGNodeImpl extends ViewportNodeImpl implements SVGNode {
   public SVGNodeImpl(int handle, ModelInteractorImpl model) {
      super._model = model;
      super._handle = handle;
   }

   public static void init(int handle, ModelInteractorImpl model) {
      ViewportNodeImpl.initViewport(handle, model);
      model._nodes[handle + 29] = -1;
      model._nodes[handle + 8] = model._nodes[handle + 8] | 131072;
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
   public boolean isZoomAndPannable() {
      return isZoomAndPannable(super._handle, super._model);
   }

   public static boolean isZoomAndPannable(int handle, ModelInteractorImpl model) {
      return (model._nodes[handle + 8] & 131072) != 0;
   }

   @Override
   public void setZoomAndPannable(boolean zoomAndPannable) {
      setZoomAndPannable(zoomAndPannable, super._handle, super._model);
   }

   public static void setZoomAndPannable(boolean zoomAndPannable, int handle, ModelInteractorImpl model) {
      if (zoomAndPannable) {
         model._nodes[handle + 8] = model._nodes[handle + 8] | 131072;
      } else {
         model._nodes[handle + 8] = model._nodes[handle + 8] & -131073;
      }

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

   public static void setAsRootHandle(int rootHandle, ModelInteractorImpl model) {
      model._visualRoot = rootHandle;
      model._isZoomAndPannable = isZoomAndPannable(rootHandle, model);
   }

   @Override
   public void setAsRoot() {
      setAsRootHandle(super._handle, super._model);
   }
}
