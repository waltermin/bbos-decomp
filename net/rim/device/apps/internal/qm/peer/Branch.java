package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.internal.qm.peer.common.QmUtil;

class Branch extends VerticalFieldManager implements TreeItem {
   protected boolean _expanded = true;
   protected boolean _empty = true;
   private EmptyBranchPlaceholder _placeholder = new EmptyBranchPlaceholder();
   private static Tag TAG = Tag.create("bbmessenger-branch");
   protected static StringBuffer _sBuffer = new StringBuffer();

   void clear() {
      this.deleteRange(1, this.getFieldCount() - 1);
      super.add(this._placeholder);
      this._empty = true;
   }

   String name() {
      return null;
   }

   void drawText(Graphics graphics, int x, int y, int width) {
      graphics.drawText(this.name(), x, y, 70, width);
   }

   boolean isExpanded() {
      return this._expanded;
   }

   void toggleExpansion() {
      this._expanded = !this._expanded;
      this.updateLayout();
   }

   boolean hideWhenEmpty() {
      return false;
   }

   @Override
   public void doInvalidate(boolean andRelated) {
      this.invalidate();
   }

   @Override
   public void delete(Field field) {
      super.delete(field);
      if (!this._empty && this.getFieldCount() == 1) {
         super.add(this._placeholder);
         this._empty = true;
      }
   }

   @Override
   public void insert(Field field, int index) {
      if (this._empty) {
         super.delete(this._placeholder);
         this._empty = false;
         index--;
      }

      super.insert(field, index);
   }

   Branch() {
      super(576460752303423488L);
      super.add(new BranchTitleField());
      super.add(this._placeholder);
      this.setTag(TAG);
   }

   @Override
   public int getPreferredHeight() {
      return QmUtil.calculateTrueFontHeight(this.name());
   }

   @Override
   protected void sublayout(int width, int height) {
      if (this.hideWhenEmpty() && this._empty) {
         height = 0;
      } else if (!this._expanded) {
         height = this.getPreferredHeight();
      }

      super.sublayout(width, height);
   }

   @Override
   public boolean isFocusable() {
      return !this.hideWhenEmpty() || !this._empty;
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      return this._expanded ? super.moveFocus(amount, status, time) : amount;
   }

   @Override
   protected void onFocus(int direction) {
      if (!this._expanded) {
         this.setFieldWithFocus(this.getField(0));
      } else {
         super.onFocus(direction);
      }
   }

   @Override
   public void add(Field field) {
      if (this._empty) {
         super.delete(this._placeholder);
         this._empty = false;
      }

      super.add(field);
   }
}
