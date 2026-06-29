package net.rim.device.apps.internal.bis.launch.protocol;

import java.util.Hashtable;

final class UpdateStatusHandler$DownloadHandler extends XMLToObjectHandler {
   private static final String[] REQUIRED_ELEMENTS = new String[]{"downloadUrl", "digest", "size"};

   public UpdateStatusHandler$DownloadHandler() {
      super("download", REQUIRED_ELEMENTS, true);
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      UpdateInfo$ModuleInfo module = new UpdateInfo$ModuleInfo();
      String moduleURL = XMLToObjectHandler.getStringValue(elementToValueMap, "downloadUrl");
      String digest = XMLToObjectHandler.getStringValue(elementToValueMap, "digest");
      byte[] digestBytes = UpdateStatusHandler.parseDigest(digest);
      int size = Integer.parseInt(XMLToObjectHandler.getStringValue(elementToValueMap, "size"));
      module.setDownloadURL(moduleURL);
      module.setDigest(digestBytes);
      module.setSize(size);
      return module;
   }
}
