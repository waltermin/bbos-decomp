package net.rim.wica.runtime.metadata.component.ui;

public interface ScreenManager {
   ScreenModel getCurrentScreenModel();

   void display(ScreenModel var1);

   void display(ScreenModel var1, long[] var2);

   void close(boolean var1);

   void back();

   boolean isBackAvailable();
}
