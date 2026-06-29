package net.rim.device.apps.api.addressbook;

import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface DisplayPictureModel extends PersistableRIMModel {
   int MAX_HEIGHT = 96;
   int MAX_WIDTH = 72;
   int MAX_FILE_SIZE = 3072;
   int MAX_SYNC_HEIGHT = 100;
   int MAX_SYNC_WIDTH = 100;
   int MAX_SYNC_SIZE = 32768;
   int DEFAULT_QUALITY_FACTOR = 75;
   int MIN_QUALITY_FACTOR = 50;

   byte[] getDisplayPicture();

   Bitmap getDisplayBitmap();

   Bitmap getDisplayIcon();

   void setDisplayPicture(byte[] var1);
}
