package net.rim.wica.runtime.metadata.internal.component.ui;

import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.UIContainer;

public class UIContainerImpl extends UIComponentImpl implements UIContainer {
   protected UIComponent[] _children;
   private int _layout;

   protected UIContainerImpl(int id, int type, UIContainer parent, int style, int bits, int x, int y, int initId, int layout, int childCount) {
      super(id, type, parent, style, bits, x, y, initId);
      this._layout = layout;
      if (childCount > 0) {
         this._children = new UIComponent[childCount];
      }
   }

   @Override
   public void clean() {
      super.clean();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).clean();
      }
   }

   @Override
   public UIComponent[] getChildren() {
      return this._children;
   }

   @Override
   public int getLayout() {
      return this._layout;
   }

   @Override
   public void init() {
      super.init();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).init();
      }
   }

   @Override
   public void updateUI() {
      super.updateUI();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).updateUI();
      }
   }

   @Override
   public void updateData() {
      super.updateData();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).updateData();
      }
   }

   @Override
   protected void reset() {
      super.reset();
      int size = this._children == null ? 0 : this._children.length;

      for (int i = 0; i < size; i++) {
         ((UIComponentImpl)this._children[i]).reset();
      }
   }
}
