package net.rim.wica.runtime.ui;

import net.rim.device.api.system.Bitmap;

public interface HomeScreenEntry extends Runnable {
   Bitmap getEntryBitmap();

   Bitmap getEntryBitmapFocus();

   Integer getEntryDefaultPosition();

   String getEntryDescription();

   String getEntryId();
}
