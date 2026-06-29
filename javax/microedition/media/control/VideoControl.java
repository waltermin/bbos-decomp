package javax.microedition.media.control;

public interface VideoControl extends GUIControl {
   int USE_DIRECT_VIDEO;

   @Override
   Object initDisplayMode(int var1, Object var2);

   void setDisplayLocation(int var1, int var2);

   int getDisplayX();

   int getDisplayY();

   void setVisible(boolean var1);

   void setDisplaySize(int var1, int var2);

   void setDisplayFullScreen(boolean var1);

   int getSourceWidth();

   int getSourceHeight();

   int getDisplayWidth();

   int getDisplayHeight();

   byte[] getSnapshot(String var1);
}
