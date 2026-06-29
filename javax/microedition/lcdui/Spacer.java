package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

public class Spacer extends Item {
   private SpacerField _spacerField;

   public Spacer(int minWidth, int minHeight) {
      synchronized (Application.getEventLock()) {
         if (minWidth >= 0 && minHeight >= 0) {
            this._spacerField = new SpacerField(minWidth, minHeight);
            this._spacerField.setCookie(this);
            this.setPeer(this._spacerField);
            this.setPreferredSize(minWidth, minHeight);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      this._spacerField.setChangeListener(null);
      this._spacerField.setChangeListener(changeListener);
      return this._spacerField;
   }

   public void setMinimumSize(int minWidth, int minHeight) {
      synchronized (Application.getEventLock()) {
         if (minWidth >= 0 && minHeight >= 0) {
            this._spacerField.setSize(minWidth, minHeight);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   @Override
   public void addCommand(Command cmd) {
      throw new IllegalStateException();
   }

   @Override
   public void setDefaultCommand(Command cmd) {
      throw new IllegalStateException();
   }

   @Override
   public void setLabel(String label) {
      throw new IllegalStateException();
   }

   @Override
   public String getLabel() {
      return null;
   }
}
