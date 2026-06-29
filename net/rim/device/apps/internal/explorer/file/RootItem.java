package net.rim.device.apps.internal.explorer.file;

public interface RootItem {
   FileConnectionHolder find(String var1);

   FileConnectionHolder getParent(FileItemField var1);
}
