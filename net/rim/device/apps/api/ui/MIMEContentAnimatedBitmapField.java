package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.ui.component.AnimatedBitmapField;

public class MIMEContentAnimatedBitmapField extends AnimatedBitmapField {
   private ContextObject _context;
   private boolean _isProtected;
   private boolean _addMIMEVerbs = true;
   private String _name;

   public MIMEContentAnimatedBitmapField() {
   }

   public MIMEContentAnimatedBitmapField(Bitmap bitmap) {
      super(bitmap);
   }

   public MIMEContentAnimatedBitmapField(Bitmap bitmap, long style) {
      super(bitmap, style);
   }

   public MIMEContentAnimatedBitmapField(EncodedImage image, int maxLoopIterations, long style) {
      super(image, maxLoopIterations, style);
   }

   public void setContext(ContextObject context) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setAddMIMEVerbs(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setProtected(boolean isProtected) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public boolean isProtected() {
      return this._isProtected;
   }

   public void setName(String name) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (!this._isProtected && this._addMIMEVerbs) {
         EncodedImage image = this.getImage();
         if (image != null) {
            MIMEContentBitmapField.makeContextMenu(contextMenu, image, this._name, this._context);
         }
      }
   }
}
