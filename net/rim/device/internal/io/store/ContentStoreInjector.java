package net.rim.device.internal.io.store;

import java.io.InputStream;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.CRC32;
import net.rim.device.internal.io.file.FileUtilities;

public final class ContentStoreInjector {
   private static final boolean DEBUG = false;

   private ContentStoreInjector() {
   }

   public static final void injectLinkToCodFile(String srcLink, String destLink, boolean drmProtected) {
      if (destLink.startsWith("/store") && srcLink.startsWith("cod://")) {
         try {
            ContentStoreImpl cs = ContentStoreImpl.getInstance();
            String contentStorePathName = destLink.substring(6);
            SymbolicLinkImpl file = cs.addSymbolicLink(contentStorePathName, 3);
            file.setTimeCreate(1141171200000L);
            file.setTimeModify(1141171200000L);
            file.setAttributes(2049, 2);
            if (drmProtected) {
               file.setCodeSigningKey(CodeSigningKey.getBuiltInKey(51));
               file.setDrmAttributes(1, 0);
            }

            file.setLink(srcLink);
         } finally {
            return;
         }
      } else {
         String logData = "ContentStoreInjector - not injecting due to destLink: " + destLink + " or srcLink: " + srcLink;
         EventLogger.logEvent(-7509200465648525729L, logData.getBytes(), 0);
      }
   }

   public static final void removeLegacyLink(String removeLink) {
      try {
         ContentStoreImpl cs = ContentStoreImpl.getInstance();
         FSDescriptor oldFSD = cs.get(removeLink);
         if (oldFSD instanceof FileImpl) {
            FileImpl oldFile = (FileImpl)oldFSD;
            if ((oldFile.getDrmAttributes() & 1) != 0) {
               oldFile.remove();
            } else {
               int len = (int)oldFile.getLength();
               InputStream oldStream = oldFile.openInputStream();
               byte[] oldByte = new byte[len];
               oldStream.read(oldByte, 0, len);
               int crc = CRC32.update(-1, oldByte);
               String name = FileUtilities.getName(removeLink);
               if (name.equals(cs.getFilenameFromCRC(crc))) {
                  oldFile.remove();
                  return;
               }
            }
         }
      } finally {
         return;
      }
   }
}
