package net.rim.device.apps.internal.api.crypto.certificate;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.internal.ui.SystemIcon;

public class CertificateListField extends ListField implements ListFieldCallback {
   private int _checkboxWidth;
   private int _checkboxHeight;
   public static final int CHECK_STATE_NONE = -1;
   public static final int CHECK_STATE_UNCHECKED = 0;
   public static final int CHECK_STATE_CHECKED = 1;
   public static final int CHECK_STATE_SPACE = 2;
   private static final int SPACER_WIDTH = 3;

   protected String getText(int _1) {
      throw null;
   }

   protected int getCheckState(int _1) {
      throw null;
   }

   protected Font getFont(int index) {
      return this.getFont();
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public Object get(ListField listField, int index) {
      return this.getText(index);
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int systemIconIndex = -1;
      int textX = 0;
      switch (this.getCheckState(index)) {
         case -1:
            break;
         case 0:
         default:
            systemIconIndex = 0;
            textX = this._checkboxWidth + 3;
            break;
         case 1:
            systemIconIndex = 1;
            textX = this._checkboxWidth + 3;
            break;
         case 2:
            textX = this._checkboxWidth + 3;
      }

      if (systemIconIndex != -1) {
         SystemIcon.COLLECTION.paint(graphics, 0, y, this._checkboxWidth, this._checkboxHeight, systemIconIndex);
      }

      graphics.setFont(this.getFont(index));
      graphics.drawText(this.getText(index), textX, y, 64, width - textX);
   }

   @Override
   protected void layout(int width, int height) {
      Font font = this.getFont();
      this._checkboxWidth = SystemIcon.COLLECTION.getWidth(font);
      this._checkboxHeight = SystemIcon.COLLECTION.getHeight(font);
      super.layout(width, height);
   }

   public CertificateListField(int numRows, long style) {
      super(numRows, style);
      this.setCallback(this);
   }

   public CertificateListField(int numRows) {
      this(numRows, 0);
   }

   public CertificateListField() {
      this(0);
   }
}
