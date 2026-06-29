package net.rim.wica.runtime.ui;

import net.rim.device.apps.api.ribbon.RibbonLauncher;

public final class HomeScreenUtilities {
   public static final String createEntryIdentifier(String initialString) {
      return "net.rim.mds.runtime." + initialString;
   }

   public static final void registerEntry(HomeScreenEntry entry) {
      RibbonLauncher.getInstance().registerAction(entry.getEntryId(), new HomeScreenUtilities$HomeScreenEntryWrapper(entry));
   }

   public static final void unregisterEntry(HomeScreenEntry entry) {
      RibbonLauncher.getInstance().unregisterAction(entry.getEntryId());
   }

   public static final void updateEntry(HomeScreenEntry entry) {
      RibbonLauncher.getInstance().updateRegisteredAction(entry.getEntryId());
   }
}
