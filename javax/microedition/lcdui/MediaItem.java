package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.ui.component.MMAPIMediaField;

class MediaItem extends Item {
   private VerticalFieldManager _container;
   private LabelField _label;
   private MMAPIMediaField _media;

   MediaItem(String label, int width, int height) {
      synchronized (Application.getEventLock()) {
         this.setLayout(0);
         this._container = new VerticalFieldManager(1152921504606846976L);
         this._container.setCookie(this);
         this.setLabel(label);
         this._media = new MMAPIMediaField(width, height);
         this._container.add(this._media);
         this.setPeer(this._container);
         this._media.setComponent(this);
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      return this._container;
   }

   @Override
   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         if (label == null) {
            if (this._label != null) {
               this._container.delete(this._label);
               this._label = null;
            }
         } else if (this._label == null) {
            this._label = new LabelField(label, Item.getFieldLayoutStyle(this.getLayout(), 1) | 1152921504606846976L);
            this._container.insert(this._label, 0);
         } else {
            this._label.setText(label);
         }
      }
   }

   @Override
   public String getLabel() {
      synchronized (Application.getEventLock()) {
         return this._label == null ? null : this._label.getText();
      }
   }

   MMAPIMediaField getMediaField() {
      return this._media;
   }
}
