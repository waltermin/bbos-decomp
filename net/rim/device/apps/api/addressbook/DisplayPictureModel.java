package net.rim.device.apps.api.addressbook;

import net.rim.device.api.system.Bitmap;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface DisplayPictureModel extends PersistableRIMModel {
   int MAX_HEIGHT;
   int MAX_WIDTH;
   int MAX_FILE_SIZE;
   int MAX_SYNC_HEIGHT;
   int MAX_SYNC_WIDTH;
   int MAX_SYNC_SIZE;
   int DEFAULT_QUALITY_FACTOR;
   int MIN_QUALITY_FACTOR;

   byte[] getDisplayPicture();

   Bitmap getDisplayBitmap();

   Bitmap getDisplayIcon();

   void setDisplayPicture(byte[] var1);
}
