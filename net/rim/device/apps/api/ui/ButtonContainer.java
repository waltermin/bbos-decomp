package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Theme;

public class ButtonContainer extends FlowFieldManager {
   private Manager _buttonManager;
   private Font _buttonFont;
   public static final int HORIZONTAL_LAYOUT = 0;
   public static final int VERTICAL_LAYOUT = 1;
   public static final int FLOW_LAYOUT = 2;
   private static final long DEFAULT_STYLE = 12884901888L;

   public ButtonContainer() {
      this(1, 12884901888L, null);
   }

   public ButtonContainer(Font buttonFont) {
      this(1, 12884901888L, buttonFont);
   }

   public ButtonContainer(int layout, Font buttonFont) {
      this(layout, 12884901888L, buttonFont);
   }

   public ButtonContainer(int layout, long style, Font buttonFont) {
      super(style | 1152921504606846976L);
      this._buttonFont = buttonFont;
      switch (layout) {
         case -1:
            break;
         case 0:
         default:
            this._buttonManager = new HorizontalFieldManager(299080047656960L);
            break;
         case 1:
            this._buttonManager = new VerticalFieldManager(299080047656960L);
            break;
         case 2:
            this._buttonManager = new FlowFieldManager(299080047656960L);
      }

      this.add(this._buttonManager);
   }

   public void deleteAllButtons() {
      if (this._buttonManager != null) {
         this._buttonManager.deleteAll();
      }
   }

   public void deleteButton(Field button) {
      this._buttonManager.delete(button);
   }

   public int getButtonCount() {
      return this._buttonManager != null ? this._buttonManager.getFieldCount() : 0;
   }

   public Field getButtonAt(int index) {
      if (index > this.getButtonCount()) {
         throw new IllegalArgumentException();
      } else {
         return this._buttonManager.getField(index);
      }
   }

   public void insertButtonAt(ButtonField button, int index) {
      if (button == null) {
         throw new IllegalArgumentException();
      }

      if (index > this.getButtonCount()) {
         throw new IllegalArgumentException();
      }

      if (this._buttonFont != null) {
         button.setFont(this._buttonFont);
      }

      this._buttonManager.insert(button, index);
   }

   public void addButton(ButtonField button) {
      if (button == null) {
         throw new IllegalArgumentException();
      }

      if (this._buttonFont != null) {
         button.setFont(this._buttonFont);
      }

      this._buttonManager.add(button);
   }

   @Override
   public void sublayout(int width, int height) {
      this._buttonManager.setPadding(0, 0, 0, 0);
      super.sublayout(width, height);
      int widthDelta = this.getWidth() - this._buttonManager.getWidth() >> 1;
      if (widthDelta > 0) {
         Bitmap up = Theme.getThemeBitmap(0);
         Bitmap down = Theme.getThemeBitmap(1);
         if (null != up && null != down) {
            int padding = Math.min(Math.max(up.getWidth(), down.getWidth()), widthDelta);
            this._buttonManager.setPadding(0, padding, 0, padding);
            this.setPositionChild(this._buttonManager, this._buttonManager.getLeft() - padding, this._buttonManager.getTop());
         }
      }
   }
}
