package net.rim.device.apps.internal.browser.push;

import java.io.InputStream;
import net.rim.device.internal.io.file.FileUtilities;

public final class StoreModel extends BrowserPushModel {
   private InputStream _content;
   private String _path;

   public StoreModel(InputStream content, String path) {
      this._content = content;
      this._path = path;
   }

   @Override
   public final void run() {
      this.run(31, this._path);
   }

   @Override
   protected final void doAction() {
      try {
         FileUtilities.saveInputStream(this._content, this._path, true);
      } finally {
         return;
      }
   }
}
