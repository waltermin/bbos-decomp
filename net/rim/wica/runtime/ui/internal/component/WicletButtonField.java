package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ImageBitmap;
import net.rim.device.internal.ui.ImageEncoded;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.ButtonControl;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.Focusable;
import net.rim.wica.runtime.ui.internal.FontAccentsUtil;
import net.rim.wica.runtime.ui.internal.ResourceListener;

final class WicletButtonField extends ButtonField implements FieldChangeListener, View, ResourceListener, Focusable {
   private ScreenContext _context;
   private ButtonControl _model;
   private int _row;
   private byte _visibility;
   private Resource _resource;
   private static String DEFAULT_IMAGE = "default.png";

   WicletButtonField(ScreenContext context, ButtonControl model, int row, long style) {
      super(null, 4295032836L | style);
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this._row = row;
      this.setChangeListener(this);
      this.setValue();
   }

   private final void setValue() {
      this.setImage();
      this.setLabel();
   }

   private final void setImage() {
      String imageURL = this.getImageURL(this._row);
      if (imageURL != null) {
         this.setResource(this._context.getResourceProvider().getResource(imageURL, this));
      } else {
         int resourceId = this._model.getResourceId();
         if (resourceId != -1) {
            this.setResource(this._context.getResourceProvider().getApplicationResource(resourceId));
         }
      }
   }

   private final String getImageURL(int row) {
      Object imageValue = this._model.getImageValue();
      return (String)(!(imageValue instanceof Object) ? imageValue : ((Vector)imageValue).elementAt(row));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void setResource(Resource resource) {
      Image image = null;
      if (resource == null) {
         image = ImageBitmap.create(RuntimeResources.getBitmapResource(DEFAULT_IMAGE));
      } else if (resource != this._resource) {
         this._resource = resource;
         boolean var5 = false /* VF: Semaphore variable */;

         label31:
         try {
            var5 = true;
            image = ImageEncoded.create(EncodedImage.createEncodedImage(resource.getData(), 0, resource.size()));
            var5 = false;
         } finally {
            if (var5) {
               image = ImageBitmap.create(RuntimeResources.getBitmapResource(DEFAULT_IMAGE));
               break label31;
            }
         }
      }

      if (image != null) {
         this.setImage(image);
         this.setImageSize(image.getWidth(Integer.MAX_VALUE, Integer.MAX_VALUE), image.getHeight(Integer.MAX_VALUE, Integer.MAX_VALUE));
      }
   }

   private final void setLabel() {
      Object value = this._model.getValue();
      String label = null;
      if (!(value instanceof Object)) {
         label = (String)value;
      } else {
         label = (String)((Vector)value).elementAt(this._row);
      }

      this.setLabel(label);
   }

   @Override
   protected final void applyTheme() {
      if (this._visibility == 0) {
         super.applyTheme();
         int styleId = this._model.getStyle();
         if (styleId != -1) {
            ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
            Font font = attributes.getFont();
            if (font != null) {
               this.setFont(font);
            }
         }

         super.applyFont();
      }
   }

   @Override
   public final int getPreferredHeight() {
      int fontHeight = this.getFont().getHeight();
      int extraHeightToFitAccents = FontAccentsUtil.getExtraHeightToFitAccents(this.getFont(), this.getLabel(), Graphics.getScreenWidth());
      return fontHeight + Math.max(extraHeightToFitAccents * 2, super.getPreferredHeight() - fontHeight);
   }

   @Override
   public final boolean isFocusable() {
      return this._visibility == 0 ? super.isFocusable() : false;
   }

   @Override
   protected final boolean keyRepeat(int keycode, int time) {
      return Keypad.map(keycode) == '\n';
   }

   @Override
   protected final void layout(int width, int height) {
      if (this._visibility != 1) {
         super.layout(width, height);
      } else {
         this.setExtent(0, 0);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._visibility == 0) {
         super.paint(graphics);
      }
   }

   @Override
   protected final void paintBackground(Graphics graphics) {
      if (this._visibility == 0) {
         super.paintBackground(graphics);
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      return (status & 1) == 0 ? super.trackwheelClick(status, time) : false;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (context != Integer.MIN_VALUE) {
         this._model.onClick();
      }
   }

   @Override
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (ButtonControl)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
         if (this._visibility != 0) {
            if (this.isFocus()) {
               this.onUnfocus();
            }

            this.setMargin(0, 0, 0, 0);
            this.setBorder(0, 0, 0, 0);
            this.setBorder(0, null);
         } else {
            this.applyTheme();
         }

         if (this._visibility != 2) {
            this.updateLayout();
            return;
         }

         this.invalidate();
      }
   }

   @Override
   public final void update(int row) {
      this._row = row;
      this.setVisibility((byte)(this._model.isVisible() ? 0 : 1));
      this.setValue();
      this.invalidate();
   }

   @Override
   public final void resourceRetrieved(Resource resource) {
      this.setResource(resource);
      this.updateLayout();
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
