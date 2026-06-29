package net.rim.device.apps.internal.bis.protocol;

import java.util.Hashtable;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.internal.bis.data.UpdateInfo$ModuleInfo;
import net.rim.device.apps.internal.bis.utils.ArgUtils;
import net.rim.device.apps.internal.bis.utils.xml.XMLToObjectHandler;

final class DownloadHandler extends XMLToObjectHandler implements BISServiceConstants {
   private static final String[] REQUIRED_ELEMENTS = new String[]{"downloadUrl", "digest", "size"};

   public DownloadHandler() {
      super("download", REQUIRED_ELEMENTS, true);
   }

   @Override
   public final Object getResult() {
      Hashtable elementToValueMap = (Hashtable)super.getResult();
      UpdateInfo$ModuleInfo module = new UpdateInfo$ModuleInfo();
      String moduleURL = ArgUtils.getStringValue(elementToValueMap, "downloadUrl");
      String digest = ArgUtils.getStringValue(elementToValueMap, "digest");
      byte[] digestBytes = parseDigest(digest);
      int size = Integer.parseInt(ArgUtils.getStringValue(elementToValueMap, "size"));
      module.setDownloadURL(moduleURL);
      module.setDigest(digestBytes);
      module.setSize(size);
      return module;
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
