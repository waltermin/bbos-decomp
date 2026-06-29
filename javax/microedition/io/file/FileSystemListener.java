package javax.microedition.io.file;

public interface FileSystemListener {
   int ROOT_ADDED;
   int ROOT_REMOVED;

   void rootChanged(int var1, String var2);
}
