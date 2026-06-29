package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.util.MathUtilities;

public class AutoTextInputFieldWithId extends Manager implements FocusChangeListener {
   protected String _name;
   protected String _title;
   protected String _resetValue;
   protected AutoTextEditField _edit;
   protected long _editFlags;
   protected int _id;
   private static final int SUB_BORDER_PADDING = 1;
   private static final int DOUBLE_SUB_BORDER_PADDING = 2;
   private static final int BORDER_WIDTH = 2;
   private static final int DOUBLE_BORDER_WIDTH = 4;
   public static final byte FLAG_READ_ONLY = 1;
   public static final byte FLAG_ALLOW_CR = 2;
   private static final int DEFAULT_INPUT_SIZE = 20;
   private static String INPUT_TEXT_LABEL = ((StringBuffer)(new Object())).append(String.valueOf('▶')).append(' ').toString();

   public int getFieldId() {
      return this._id;
   }

   public String getText() {
      return this._edit.getText();
   }

   public void reset() {
      this._edit.setText(this._resetValue);
   }

   protected AutoTextEditField createEditField(String inputTextLabel, String resetValue, int maxSize, long editFlags) {
      return (AutoTextEditField)(new Object(inputTextLabel, resetValue, maxSize, editFlags));
   }

   @Override
   public void focusChanged(Field field, int eventType) {
      if (field == this.getField(0)) {
         this.invalidate();
      }
   }

   @Override
   public void add(Field field) {
      this.deleteAll();
      super.add(field);
      field.setFocusListener(null);
      field.setFocusListener(this);
   }

   @Override
   public void insert(Field field, int index) {
      this.deleteAll();
      super.add(field);
      field.setFocusListener(null);
      field.setFocusListener(this);
   }

   @Override
   public int getPreferredHeight() {
      return this.getField(0).getPreferredHeight() + 4 + 2;
   }

   @Override
   public int getPreferredWidth() {
      return this.getField(0).getPreferredWidth() + 4 + 2;
   }

   @Override
   protected void subpaint(Graphics graphics) {
      int virtualWidth = this.getVirtualWidth();
      int virtualHeight = this.getVirtualHeight();
      int verticalScroll = this.getVerticalScroll();
      int horizontalScroll = this.getHorizontalScroll();
      graphics.pushContext(-horizontalScroll, -verticalScroll, virtualWidth, virtualHeight, -horizontalScroll, -verticalScroll);
      graphics.drawRect(1, 1, virtualWidth - 2, virtualHeight - 2);
      if (this.getFieldWithFocus() != null) {
         graphics.drawRect(0, 0, virtualWidth, virtualHeight);
      }

      graphics.popContext();
      super.subpaint(graphics);
   }

   @Override
   protected void sublayout(int width, int height) {
      Field field = this.getField(0);
      this.setPositionChild(field, 3, 3);
      this.layoutChild(field, width - 4 - 2, height - 4 - 2);
      int virtualHeight = field.getHeight() + 4 + 2;
      int virtualWidth = field.getWidth() + 4 + 2;
      this.setExtent(width, virtualHeight);
      this.setVirtualExtent(virtualWidth, virtualHeight);
   }

   @Override
   public int getFieldClosestToLocation(int x, int y, int status) {
      x = MathUtilities.clamp(3, x, this.getWidth() - 3);
      y = MathUtilities.clamp(3, y, this.getHeight() - 3);
      return super.getFieldClosestToLocation(x, y, status);
   }

   @Override
   public int getFieldAtLocation(int x, int y) {
      x = MathUtilities.clamp(3, x, this.getWidth() - 3);
      y = MathUtilities.clamp(3, y, this.getHeight() - 3);
      return super.getFieldAtLocation(x, y);
   }

   public AutoTextInputFieldWithId(int id, String title, String name, String initValue, int size, int maxLength, byte flags, long style) {
      super(1407374883553280L | style);
      this._id = id;
      this._name = name;
      this._title = title;
      this._resetValue = initValue;
      this._editFlags = (flags & 1) != 0 ? 0 : 4503599627370496L;
      this._editFlags |= (flags & 2) != 0 ? 0 : 2147483648L;
      if (maxLength > 0) {
         if (this._resetValue != null) {
            int resetLength = this._resetValue.length();
            if (maxLength < resetLength) {
               maxLength = resetLength;
            }
         }

         this._edit = this.createEditField(INPUT_TEXT_LABEL, this._resetValue, maxLength, this._editFlags);
      } else {
         this._edit = this.createEditField(INPUT_TEXT_LABEL, this._resetValue, 1000000, this._editFlags);
      }

      this.add(this._edit);
   }
}
