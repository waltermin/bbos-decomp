package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.internal.ui.component.AnimatedBitmapField;
import net.rim.device.resources.Resource;

public class Gauge extends Item {
   private VerticalFieldManager _container = new VerticalFieldManager(1152921504606846976L);
   private int _maxValue;
   private int _value;
   private boolean _interactive;
   private String _label;
   private RichTextField _labelField;
   private Field _peer;
   private FieldChangeListener _changeListener;
   private int _currentIncrementalUpdatingAnimationIndex;
   private EncodedImage _animationImage;
   public static final int INDEFINITE;
   public static final int CONTINUOUS_IDLE;
   public static final int INCREMENTAL_IDLE;
   public static final int CONTINUOUS_RUNNING;
   public static final int INCREMENTAL_UPDATING;

   private EncodedImage getAnimationImage() {
      if (this._animationImage == null) {
         byte[] imageData = Resource.getResourceClass().getResource("Hourglass.gif");
         this._animationImage = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
      }

      return this._animationImage;
   }

   private void updatePeer() {
      Field newPeer;
      if (this._maxValue != -1) {
         if (this._peer != null && this._peer instanceof GaugeField) {
            ((GaugeField)this._peer).reset(this._label, 0, this._maxValue, this._value);
            return;
         }

         newPeer = new GaugeField(this._label, 0, this._maxValue, this._value, 18014398509481984L);
         newPeer.setEditable(this._interactive);
      } else {
         if (this._label != null) {
            if (this._labelField == null) {
               this._labelField = new RichTextField();
               this._container.insert(this._labelField, 0);
            }

            this._labelField.setText(this._label);
         } else if (this._labelField != null) {
            this._container.delete(this._labelField);
            this._labelField = null;
         }

         switch (this._value) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            case 1:
            case 2:
            default:
               newPeer = new AnimatedBitmapField(this.getAnimationImage(), 1000, 0);
               break;
            case 3:
               if (this._currentIncrementalUpdatingAnimationIndex == this.getAnimationImage().getFrameCount()) {
                  this._currentIncrementalUpdatingAnimationIndex = 0;
               }

               if (this._peer != null && this._peer instanceof BitmapField) {
                  ((BitmapField)this._peer).setBitmap(this.getAnimationImage().getBitmap(this._currentIncrementalUpdatingAnimationIndex++));
                  return;
               }

               newPeer = new BitmapField(this.getAnimationImage().getBitmap(this._currentIncrementalUpdatingAnimationIndex++));
         }
      }

      if (this._peer != null) {
         this._container.delete(this._peer);
      }

      this._peer = newPeer;
      this._container.add(this._peer);
      this._peer.setChangeListener(this._changeListener);
      this._peer.setCookie(this);
   }

   private boolean validateMaxValue(int maxValue) {
      return maxValue > 0 || !this._interactive && maxValue == -1;
   }

   private boolean validateValue(int value) {
      return this._interactive || this._maxValue != -1 || value >= 0 && value <= 3;
   }

   public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
      synchronized (Application.getEventLock()) {
         this._label = label;
         this._interactive = interactive;
         if (!this.validateMaxValue(maxValue)) {
            throw new IllegalArgumentException();
         }

         this._maxValue = maxValue;
         if (maxValue != -1) {
            initialValue = MathUtilities.clamp(0, initialValue, maxValue);
         }

         if (!this.validateValue(initialValue)) {
            throw new IllegalArgumentException();
         }

         this._value = initialValue;
         this.updatePeer();
         this._container.setCookie(this);
         this.setPeer(this._container);
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      this._changeListener = changeListener;
      this._peer.setChangeListener(null);
      this._peer.setChangeListener(changeListener);
      return this._container;
   }

   public void setValue(int value) {
      synchronized (Application.getEventLock()) {
         if (!this.validateValue(value)) {
            throw new IllegalArgumentException();
         }

         if (this._maxValue != -1) {
            this._value = MathUtilities.clamp(0, value, this._maxValue);
         } else {
            this._value = value;
         }

         this.updatePeer();
      }
   }

   public int getValue() {
      synchronized (Application.getEventLock()) {
         return this._maxValue == -1 ? this._value : ((GaugeField)this._peer).getValue();
      }
   }

   public void setMaxValue(int maxValue) {
      synchronized (Application.getEventLock()) {
         if (!this.validateMaxValue(maxValue)) {
            throw new IllegalArgumentException();
         }

         int oldMaxValue = this.getMaxValue();
         this._maxValue = maxValue;
         if (oldMaxValue == -1) {
            if (maxValue > -1) {
               this._value = 0;
            }
         } else if (maxValue == -1) {
            this._value = 0;
         }

         if (this._maxValue != -1) {
            this._value = MathUtilities.clamp(0, this._value, this._maxValue);
         }

         this.updatePeer();
      }
   }

   public int getMaxValue() {
      synchronized (Application.getEventLock()) {
         return this._maxValue;
      }
   }

   public boolean isInteractive() {
      synchronized (Application.getEventLock()) {
         return this._interactive;
      }
   }

   @Override
   public void setLabel(String label) {
      synchronized (Application.getEventLock()) {
         this._label = label;
         this.updatePeer();
      }
   }

   @Override
   public String getLabel() {
      synchronized (Application.getEventLock()) {
         return this._label;
      }
   }
}
