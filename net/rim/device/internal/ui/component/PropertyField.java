package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldLabelProvider;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.text.TextRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringProvider;

public class PropertyField extends Field implements FieldLabelProvider {
   private TextRect _label = new TextRect(this);
   private Object _value;
   private TextRect _text = new TextRect(this);
   private static Tag TAG = Tag.create("property");
   private static Tag TAG_LABEL = Tag.create("label");
   private static final int PADDING;

   public void setValue(Object value) {
      this._value = value;
      this._text.setText(value != null ? value.toString() : "(null)");
      this.fieldChangeNotify(Integer.MIN_VALUE);
      this.updateLayout();
   }

   public String getName() {
      return this.getLabel();
   }

   public Object getValue() {
      return this._value;
   }

   @Override
   public void setLabel(String label) {
      this._label.setText(label);
      this.updateLayout();
   }

   @Override
   public void setLabelStringProvider(StringProvider label) {
      throw new IllegalStateException("Unsupported API");
   }

   @Override
   public String getLabel() {
      return (String)this._label.getText();
   }

   public PropertyField(String label, String value, long style) {
      super(style);
      this.setTag(TAG);
      this._label.setTag(TAG_LABEL);
      this._label.setText(label);
      this.setValue(value);
   }

   @Override
   protected void layout(int width, int height) {
      this._label.layout(width, height);
      this._label.setPosition(0, 0);
      this._text.layout(width, height);
      if (!ThemeManager.getActiveTheme().isLabelOnOwnLine() && this._label.getWidth() + this._text.getWidth() <= width) {
         this._text.setPosition(width - this._text.getWidth(), 0);
      } else {
         this._text.setPosition(0, this._label.getHeight());
      }

      this.setExtent(width, this._text.getExtent().Y2());
   }

   @Override
   protected void paint(Graphics graphics) {
      this._label.paintSelf(graphics);
      this._text.paintSelf(graphics);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      this._label.applyTheme();
      this._text.applyTheme();
   }

   public PropertyField(String label, String value) {
      this(label, value, 18014398509481984L);
   }

   public PropertyField() {
      super(18014398509481984L);
      this.setTag(TAG);
      this._label.setTag(TAG_LABEL);
   }
}
