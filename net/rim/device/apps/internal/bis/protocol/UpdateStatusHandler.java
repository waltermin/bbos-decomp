package net.rim.device.apps.internal.bis.protocol;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.apps.internal.bis.data.UpdateInfo;
import net.rim.device.apps.internal.bis.data.UpdateInfo$ModuleInfo;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

final class UpdateStatusHandler extends XMLToObjectHandler implements BISServiceConstants {
   private static final String[] REQUIRED_ELEMENTS = new String[]{"upToDate"};

   public UpdateStatusHandler() {
      super("status", REQUIRED_ELEMENTS, true);
      this.setElementHandler("downloads", new DownloadsHandler());
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      UpdateInfo updateInfo = new UpdateInfo();
      boolean upToDate = "true".equals(ArgUtils.getStringValue(elementToValueMap, "upToDate"));
      updateInfo.setUpToDateStatus(upToDate);
      if (!upToDate) {
         String version = ArgUtils.getStringValue(elementToValueMap, "digest");
         boolean mandatory = "true".equals(ArgUtils.getStringValue(elementToValueMap, "mandatory"));
         updateInfo.setVersion(version);
         updateInfo.setMandatory(mandatory);
         Vector downloads = (Vector)elementToValueMap.get("downloads");
         if (downloads != null) {
            int numDownloads = downloads.size();

            for (int i = 0; i < numDownloads; i++) {
               updateInfo.addModule((UpdateInfo$ModuleInfo)downloads.elementAt(i));
            }
         }
      }

      return updateInfo;
   }
}
