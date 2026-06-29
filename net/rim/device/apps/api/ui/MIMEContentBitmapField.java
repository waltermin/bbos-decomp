package net.rim.device.apps.api.ui;

import java.io.InputStream;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.MIMEContentVerbRepository;

public class MIMEContentBitmapField extends BitmapField {
   private ContextObject _context;
   private boolean _isProtected;
   private String _name;

   public MIMEContentBitmapField() {
   }

   public MIMEContentBitmapField(Bitmap bitmap) {
   }

   public MIMEContentBitmapField(Bitmap bitmap, long style) {
      super(bitmap, style);
   }

   public void setContext(ContextObject context) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setProtected(boolean isProtected) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setName(String name) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      if (!this._isProtected) {
         EncodedImage image = this.getImage();
         if (image != null) {
            makeContextMenu(contextMenu, image, this._name, this._context);
         }
      }
   }

   static void makeContextMenu(ContextMenu contextMenu, EncodedImage image, String name, ContextObject context) {
      String mimeType;
      switch (image.getImageType()) {
         case 0:
            return;
         case 1:
         default:
            mimeType = "image/gif";
            break;
         case 2:
            mimeType = "image/png";
            break;
         case 3:
            mimeType = "image/jpeg";
            break;
         case 4:
            mimeType = "image/vnd.wap.wbmp";
      }

      InputStream istream = (InputStream)(new Object(image.getData(), image.getOffset(), image.getLength()));
      MenuItem[] menuItems = MIMEContentVerbRepository.getMenuItems(mimeType, istream, name, context);
      if (menuItems != null) {
         for (int idx = 0; idx < menuItems.length; idx++) {
            contextMenu.addItem(menuItems[idx]);
         }
      }
   }
}
