package javax.microedition.io.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;

public interface FileConnection extends StreamConnection {
   boolean isOpen();

   @Override
   InputStream openInputStream();

   @Override
   DataInputStream openDataInputStream();

   @Override
   OutputStream openOutputStream();

   @Override
   DataOutputStream openDataOutputStream();

   OutputStream openOutputStream(long var1);

   long totalSize();

   long availableSize();

   long usedSize();

   long directorySize(boolean var1);

   long fileSize();

   boolean canRead();

   boolean canWrite();

   boolean isHidden();

   void setReadable(boolean var1);

   void setWritable(boolean var1);

   void setHidden(boolean var1);

   Enumeration list();

   Enumeration list(String var1, boolean var2);

   void create();

   void mkdir();

   boolean exists();

   boolean isDirectory();

   void delete();

   void rename(String var1);

   void truncate(long var1);

   void setFileConnection(String var1);

   String getName();

   String getPath();

   String getURL();

   long lastModified();
}
