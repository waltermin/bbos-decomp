package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

public class LeftRightFieldManager extends Manager {
   private Field _leftSide;
   private Field _rightSide;
   private int _maxHeight = Integer.MAX_VALUE;

   public LeftRightFieldManager(Field leftSide, Field rightSide) {
      super(1152921504606846976L);
      this._leftSide = leftSide;
      this._rightSide = rightSide;
      super.add(leftSide);
      super.add(rightSide);
   }

   public int getLeftPreferredWidth() {
      return this._leftSide.getPreferredWidth();
   }

   public void setLeftMaximumWidth(int leftwidth, long leftAlign, long rightAlign) {
      int width = this.getWidth();
      int height = this._maxHeight;
      this.setPositionChild(this._leftSide, 0, 0);
      this.layoutChild(this._leftSide, leftwidth, height);
      if (leftAlign == 8589934592L) {
         this.setPositionChild(this._leftSide, leftwidth - this._leftSide.getWidth(), 0);
      }

      this.setPositionChild(this._rightSide, 0, 0);
      this.layoutChild(this._rightSide, width - leftwidth, height);
      if (rightAlign == 8589934592L) {
         int preferredWidth = this._rightSide.getPreferredWidth();
         if (preferredWidth < width - leftwidth) {
            leftwidth += width - leftwidth - preferredWidth;
         }
      }

      this.setPositionChild(this._rightSide, leftwidth, 0);
      int leftHeight = this._leftSide.getHeight();
      int rightHeight = this._rightSide.getHeight();
      height = leftHeight > rightHeight ? leftHeight : rightHeight;
      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
   }

   public int getLeftWidth() {
      return this._leftSide.getWidth();
   }

   @Override
   public int getPreferredHeight() {
      int leftHeight = this._leftSide.getPreferredHeight();
      int rightHeight = this._rightSide.getPreferredHeight();
      return leftHeight > rightHeight ? leftHeight : rightHeight;
   }

   @Override
   public int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   protected void sublayout(int width, int height) {
      this._maxHeight = height;
      this.setPositionChild(this._leftSide, 0, 0);
      this.layoutChild(this._leftSide, width, height);
      int leftPreferredWidth = this._leftSide.getPreferredWidth();
      this.setPositionChild(this._rightSide, 0, 0);
      this.layoutChild(this._rightSide, width - leftPreferredWidth, height);
      this.layoutChild(this._rightSide, width - leftPreferredWidth, height);
      this.setPositionChild(this._rightSide, Math.max(leftPreferredWidth, width - this._rightSide.getWidth()), 0);
      int leftHeight = this._leftSide.getHeight();
      int rightHeight = this._rightSide.getHeight();
      height = leftHeight > rightHeight ? leftHeight : rightHeight;
      this.setExtent(width, height);
      this.setVirtualExtent(width, height);
   }

   @Override
   public void add(Field field) {
      throw new Object();
   }

   @Override
   public void insert(Field field, int index) {
      throw new Object();
   }

   @Override
   public void delete(Field field) {
      throw new Object();
   }

   @Override
   public void deleteRange(int start, int count) {
      throw new Object();
   }

   @Override
   public void deleteAll() {
      throw new Object();
   }
}
