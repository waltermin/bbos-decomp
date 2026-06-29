package net.rim.tools.jar;

public class JarFile {
   public static final String MANIFEST_NAME;
   static final int LOCAL_FILE_HEADER_SIGNATURE;
   static final int LOCAL_DATA_DESCRIPTOR_SIGNATURE;
   static final int CENTRAL_FILE_HEADER_SIGNATURE;
   static final int END_CENTRAL_DIRECTORY_SIGNATURE;

   static int extractShort(byte[] bytes, int offset) {
      return (bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8) & 65535;
   }

   static int extractInt(byte[] bytes, int offset) {
      return bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | bytes[offset + 3] << 24;
   }
}
