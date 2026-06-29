package net.rim.device.api.system;

import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.PopupDialog;

final class PersistentContent$RandomKeyPressDialog extends PopupDialog {
   GaugeField _gaugeField;
   int _numBitsNeeded;
   int _numBitsObtained;

   public PersistentContent$RandomKeyPressDialog(String dialogMessage, int numBitsNeeded, long style) {
      super(new VerticalFieldManager(1153220571769602048L), style);
      VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
      this._numBitsNeeded = numBitsNeeded;
      vfm.add(new RichTextField(dialogMessage, 45035996273704960L));
      this._gaugeField = new GaugeField(CommonResource.getString(10017), 0, numBitsNeeded, 0, 0);
      vfm.add(this._gaugeField);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return this.increaseBits(4);
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      return this.increaseBits(1);
   }

   @Override
   public final boolean navigationMovement(int dx, int dy, int status, int time) {
      return dx == 0 && dy == 0 ? false : this.increaseBits(1);
   }

   @Override
   public final boolean navigationUnclick(int status, int time) {
      return false;
   }

   private final boolean increaseBits(int increase) {
      this._numBitsObtained += increase;

      try {
         this._gaugeField.setValue(this._numBitsObtained);
      } catch (IllegalArgumentException var3) {
      }

      if (this._numBitsObtained >= this._numBitsNeeded) {
         this.close(0);
      }

      return true;
   }
}
