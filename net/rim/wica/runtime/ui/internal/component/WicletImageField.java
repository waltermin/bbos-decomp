package net.rim.wica.runtime.ui.internal.component;

import java.util.Vector;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.wica.runtime.metadata.component.ui.UIComponent;
import net.rim.wica.runtime.metadata.component.ui.control.ImageControl;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.ui.View;
import net.rim.wica.runtime.ui.internal.ResourceListener;
import net.rim.wica.runtime.ui.internal.ResourceProvider;

final class WicletImageField extends BitmapField implements View, ResourceListener {
   private ScreenContext _context;
   private ImageControl _model;
   private Resource _resource;
   private byte _visibility;
   private int _row;

   WicletImageField(ScreenContext context, ImageControl model, int row) {
      this._context = context;
      this._model = model;
      this._visibility = (byte)(model.isVisible() ? 0 : 1);
      this._row = row;
      this.setResource();
   }

   private final void setResource() {
      Resource resource = null;
      if (this._model.getResourceId() != -1) {
         resource = this._context.getResourceProvider().getApplicationResource(this._model.getResourceId());
      }

      Object value = this._model.getValue();
      String uri = null;
      if (!(value instanceof Object)) {
         uri = (String)value;
      } else {
         uri = (String)((Vector)value).elementAt(this._row);
      }

      if (uri != null) {
         Resource dynamicResource = this._context.getResourceProvider().getResource(uri, this);
         if (dynamicResource != null) {
            resource = dynamicResource;
         }
      }

      if (resource != null) {
         this.setResource(resource);
      } else {
         this.setBitmap(ResourceProvider.getDefaultBitmapImage());
      }
   }

   private final void setResource(Resource resource) {
      if (resource != null && resource != this._resource) {
         this._resource = resource;

         try {
            this.setBitmap(EncodedImage.createEncodedImage(resource.getData(), 0, resource.size()).getBitmap());
         } finally {
            this.setBitmap(ResourceProvider.getDefaultBitmapImage());
            return;
         }
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      int styleId = this._model.getStyle();
      if (styleId != -1) {
         ThemeAttributeSet attributes = this._context.getStyleFactory().getStyle(styleId);
         this.setThemeAttributesSpecial(attributes, null);
      }

      super.applyFont();
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
   public final UIComponent getModel() {
      return this._model;
   }

   @Override
   public final void setModel(UIComponent model) {
      this._model = (ImageControl)model;
   }

   @Override
   public final void setVisibility(byte visibility) {
      if (visibility != this._visibility) {
         this._visibility = visibility;
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
      this.setResource();
   }

   @Override
   public final void resourceRetrieved(Resource resource) {
      this.setResource(resource);
   }
}
