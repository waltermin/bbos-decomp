package net.rim.device.apps.internal.bis.launch.protocol;

import java.util.Hashtable;
import java.util.Vector;

final class UpdateStatusHandler$DownloadsHandler extends XMLToObjectHandler {
   private static final String[] REQUIRED_ELEMENTS = new String[]{"download"};

   public UpdateStatusHandler$DownloadsHandler() {
      super("downloads", REQUIRED_ELEMENTS, true);
      this.setElementHandler("download", new UpdateStatusHandler$DownloadHandler());
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      Object download = elementToValueMap.get("download");
      if (download != null) {
         if (!(download instanceof Object)) {
            Vector downloadsVector = (Vector)(new Object());
            downloadsVector.addElement(download);
            return downloadsVector;
         } else {
            return download;
         }
      } else {
         return null;
      }
   }
}
