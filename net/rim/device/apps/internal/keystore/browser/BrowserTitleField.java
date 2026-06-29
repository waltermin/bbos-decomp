package net.rim.device.apps.internal.keystore.browser;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

final class BrowserTitleField extends Field {
   private String _label;
   private StringBuffer _value;
   private String[] _possibleValues;
   private int _numCerts;
   private int _currentProgress;
   private int _updateDelta;
   private static final int PADDING = 5;

   public BrowserTitleField(String label) {
      this._label = label;
      this._value = new StringBuffer();
      this._possibleValues = KeyStoreBrowserResources.getStringArray(6048);
   }

   @Override
   protected final void layout(int width, int height) {
      this.setExtent(width, this.getFont().getHeight());
   }

   @Override
   protected final void paint(Graphics g) {
      int valueWidth = this.getFont().getBounds(this._value.toString());
      g.drawText(this._label, 0, 0, 70, this.getWidth() - valueWidth - 5);
      g.drawText(this._value.toString(), 0, 0, 5, this.getWidth());
   }

   public final void setValue(String value) {
      this._value.setLength(0);
      this._value.append(value);
      this.invalidate();
   }

   public final void setNumberOfCerts(int number) {
      this._numCerts = number;
      this._currentProgress = 0;
      this._updateDelta = this._numCerts / 20;
      this._updateDelta++;
      this.setValue(0);
      this.invalidate();
   }

   private final void setValue(int value) {
      this._value.setLength(0);
      this._value.append(value);
      this._value.append('%');
   }

   public final void incrementProgress() {
      this._currentProgress++;
      if (this._currentProgress % this._updateDelta == 0) {
         int currentPercentage = Math.min(100, this._currentProgress * 100 / this._numCerts);
         this.setValue(currentPercentage);
         this.invalidate();
      }
   }

   public final void setValueType(int type) {
      if (type >= 0 && type < this._possibleValues.length) {
         this.setValue(this._possibleValues[type]);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
