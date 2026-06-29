package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;

public class RibbonLauncher {
   protected static final long GUID;
   public static final long GUID_HOMESCREEN_LAYOUT;
   public static final long GUID_RIBBON_INITIALIZED;
   public static final long RIBBON_BAR_MENU_VERB_FACTORY_REPOSITORY;
   public static final int BACKGROUND_IMAGE_SCALE_X_FP;
   public static final int BACKGROUND_IMAGE_ROTATION;
   public static final int BACKGROUND_IMAGE_TOP_X;
   public static final int BACKGROUND_IMAGE_TOP_Y;

   public static RibbonLauncher getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (RibbonLauncher)ar.waitForStartup(-8849441768144918583L);
   }

   public void addRibbonListener(RibbonListener _1) {
      throw null;
   }

   public void removeRibbonListener(RibbonListener _1) {
      throw null;
   }

   public void registerAction(String _1, EntryPointDescriptor _2) {
      throw null;
   }

   public void unregisterAction(String _1) {
      throw null;
   }

   public EntryPointDescriptor getRegisteredAction(String _1) {
      throw null;
   }

   public EntryPointDescriptor getEntryPointDescriptor(String _1) {
      throw null;
   }

   public void updateRegisteredAction(String _1) {
      throw null;
   }

   public void disableHotKeys(boolean _1) {
      throw null;
   }

   public void launch(String _1) {
      throw null;
   }

   public void setBackgroundImage(Object _1, String[] _2) {
      throw null;
   }

   public void setNetworkImmediateDisplayString(String _1) {
      throw null;
   }

   public void showRootFolder() {
      this.showRootFolder(false);
   }

   public void showRootFolder(boolean _1) {
      throw null;
   }

   public void setLongIdleModeText(String _1) {
      throw null;
   }

   public void setLongImmediateCellBroadcastText(String _1) {
      throw null;
   }

   public void setApplicationEntryPointProperty(String _1, long _2, Object _4) {
      throw null;
   }

   public boolean isFirstBoot() {
      throw null;
   }
}
