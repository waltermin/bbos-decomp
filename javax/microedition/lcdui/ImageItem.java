package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;

public class ImageItem extends Item {
   private ImageItem$ImageItemManager _container;
   private LabelField _label;
   private BitmapField _image;
   private String _altText;
   private Image _midpImage;
   int _appearanceMode;
   public static final int LAYOUT_DEFAULT = 0;
   public static final int LAYOUT_LEFT = 1;
   public static final int LAYOUT_RIGHT = 2;
   public static final int LAYOUT_CENTER = 3;
   public static final int LAYOUT_NEWLINE_BEFORE = 256;
   public static final int LAYOUT_NEWLINE_AFTER = 512;

   public ImageItem(String label, Image img, int layout, String altText) {
      synchronized (Application.getEventLock()) {
         this.setLayout(layout);
         this._container = new ImageItem$ImageItemManager(this);
         this._container.setCookie(this);
         this.setLabel(label);
         this.setImage(img);
         this._altText = altText;
         this.setPeer(this._container);
      }
   }

   public ImageItem(String label, Image image, int layout, String altText, int appearanceMode) {
      this(label, image, layout, altText);
      synchronized (Application.getEventLock()) {
         if (appearanceMode != 0 && appearanceMode != 1 && appearanceMode != 2) {
            throw new IllegalArgumentException();
         }

         this._appearanceMode = appearanceMode;
      }
   }

   @Override
   Field addToForm(FieldChangeListener changeListener) {
      return this._container;
   }

   public Image getImage() {
      synchronized (Application.getEventLock()) {
         return this._midpImage;
      }
   }

   public void setImage(Image img) {
      synchronized (Application.getEventLock()) {
         if (img == null) {
            if (this._image != null) {
               this._container.delete(this._image);
               this._image = null;
            }
         } else if (this._image == null) {
            this._image = new BitmapField(img.getBitmap(), Item.getFieldLayoutStyle(this.getLayout(), 1) | 18014398509481984L);
            this._container.add(this._image);
         } else {
            this._image.setBitmap(img.getBitmap());
         }

         this._midpImage = img;
      }
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

   public String getAltText() {
      return this._altText;
   }

   public void setAltText(String text) {
      this._altText = text;
   }

   @Override
   public void setLayout(int layout) {
      synchronized (Application.getEventLock()) {
         Image img = this._midpImage;
         super.setLayout(layout);
         this.setImage(null);
         this.setImage(img);
      }
   }

   public int getAppearanceMode() {
      synchronized (Application.getEventLock()) {
         return this._appearanceMode;
      }
   }
}
