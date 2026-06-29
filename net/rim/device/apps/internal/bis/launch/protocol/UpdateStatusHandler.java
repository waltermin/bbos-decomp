package net.rim.device.apps.internal.bis.launch.protocol;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.xml.parsers.SAXParser;
import net.rim.device.api.xml.parsers.SAXParserFactory;

final class UpdateStatusHandler extends XMLToObjectHandler {
   private SAXParser _parser;
   public static final String RESOURCE_STATUS = "status";
   public static final String RESOURCE_STATUS_UPTODATE = "upToDate";
   public static final String RESOURCE_STATUS_CURRENTVERSION = "currentVersion";
   public static final String RESOURCE_STATUS_DOWNLOADS = "downloads";
   public static final String RESOURCE_STATUS_DOWNLOAD = "download";
   public static final String RESOURCE_STATUS_DOWNLOADURL = "downloadUrl";
   public static final String RESOURCE_STATUS_MANDATORY = "mandatory";
   public static final String RESOURCE_STATUS_DIGEST = "digest";
   public static final String RESOURCE_STATUS_SIZE = "size";
   private static final String[] REQUIRED_ELEMENTS = new String[]{"upToDate"};

   public UpdateStatusHandler() {
      super("status", REQUIRED_ELEMENTS, true);
      this.setElementHandler("downloads", new UpdateStatusHandler$DownloadsHandler());
   }

   public final UpdateInfo loadFromXML(InputStream istream) {
      if (this._parser == null) {
         SAXParserFactory parserFactory = SAXParserFactory.newInstance();
         this._parser = parserFactory.newSAXParser();
         this._parser.setAllowUndefinedNamespaces(true);
      }

      this._parser.parse(istream, this);
      return (UpdateInfo)this.getResult();
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      UpdateInfo updateInfo = new UpdateInfo();
      boolean upToDate = "true".equals(XMLToObjectHandler.getStringValue(elementToValueMap, "upToDate"));
      updateInfo.setUpToDateStatus(upToDate);
      if (!upToDate) {
         String version = XMLToObjectHandler.getStringValue(elementToValueMap, "digest");
         boolean mandatory = "true".equals(XMLToObjectHandler.getStringValue(elementToValueMap, "mandatory"));
         updateInfo.setVersion(version);
         updateInfo.setMandatory(mandatory);
         Vector downloads = (Vector)elementToValueMap.get("downloads");
         if (downloads != null) {
            int numDownloads = downloads.size();
            if (numDownloads <= 0) {
               updateInfo.setUpToDateStatus(true);
               return updateInfo;
            }

            for (int i = 0; i < numDownloads; i++) {
               updateInfo.addModule((UpdateInfo$ModuleInfo)downloads.elementAt(i));
            }
         } else {
            updateInfo.setUpToDateStatus(true);
         }
      }

      return updateInfo;
   }

   private static final byte[] parseDigest(String digest) {
      int expectedDigestBytes = 32;
      digest = digest.trim();
      int digestStringLength = digest.length();
      if (digestStringLength != expectedDigestBytes << 1) {
         throw new IllegalArgumentException();
      }

      byte[] sha256DigestBytes = new byte[expectedDigestBytes];
      int strIndex = 0;

      try {
         for (int i = 0; i < expectedDigestBytes; i++) {
            char digit1 = digest.charAt(strIndex++);
            char digit2 = digest.charAt(strIndex++);
            sha256DigestBytes[i] = (byte)(NumberUtilities.hexDigitToInt(digit1) << 4 | NumberUtilities.hexDigitToInt(digit2));
         }

         return sha256DigestBytes;
      } finally {
         throw new IllegalArgumentException();
      }
   }
}
