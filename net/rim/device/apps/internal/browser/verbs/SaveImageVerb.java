package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;

public class SaveImageVerb extends Verb {
   private String _url;
   private EncodedImage _image;
   private boolean _drmProtected;
   private static final int DESCRIPTION;

   public SaveImageVerb(String url, EncodedImage image, boolean drmProtected) {
      super(341584);
      this._url = url;
      this._image = image;
      this._drmProtected = drmProtected;
   }

   @Override
   public String toString() {
      return BrowserResources.getString(670);
   }

   @Override
   public Object invoke(Object context) {
      if (this._image != null) {
         this._image = this._image.getStandardsCompliantEncodedImage();
      }

      if (this._image != null && this._image.getData() != null) {
         SaveFileDialog.save(
            this._url, this._image.getMIMEType(), 1, this._drmProtected, this._image.getData(), this._image.getOffset(), this._image.getLength()
         );
         return null;
      } else {
         return null;
      }
   }
}
