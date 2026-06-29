package net.rim.device.api.io.file;

import java.io.InputStream;
import java.util.Enumeration;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.system.CodeSigningKey;

public interface ExtendedFileConnection extends FileConnection {
   boolean setControlledAccess(CodeSigningKey var1);

   CodeSigningKey getControlledAccess();

   void enableDRMForwardLock();

   boolean isContentDRMForwardLocked();

   boolean isFileEncrypted();

   InputStream openRawInputStream();

   long rawFileSize();

   boolean isContentBuiltIn();

   void setAutoEncryptionResolveMode(boolean var1);

   Enumeration listWithDetails(String var1, boolean var2);
}
