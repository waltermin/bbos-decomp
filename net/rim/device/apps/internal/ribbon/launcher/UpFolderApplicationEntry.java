package net.rim.device.apps.internal.ribbon.launcher;

import net.rim.device.apps.api.ribbon.ApplicationProperties;

final class UpFolderApplicationEntry extends ApplicationEntry {
   public static final String BUNDLE_NAME = "net.rim.device.apps.internal.ribbon.launcher.UpFolderApplicationEntry";
   private static FolderEntryPointDescriptor _descriptor;

   public static final ApplicationEntry createUpFolder(String parentURL) {
      if (_descriptor == null) {
         InternalApplicationFolder rootFolder = HierarchyManager.getInstance().getFolder(InternalApplicationFolder.ROOT_FOLDER_NAME);
         _descriptor = new FolderEntryPointDescriptor(rootFolder, "net.rim.device.apps.internal.ribbon.launcher.UpFolderApplicationEntry", 0);
      }

      _descriptor.setLaunchURL(parentURL);
      return new UpFolderApplicationEntry(_descriptor);
   }

   private UpFolderApplicationEntry(FolderEntryPointDescriptor descriptor) {
      super(descriptor, true);
      InternalApplicationHierarchy hierarchy = HierarchyManager.getInstance().getActiveHierarchy();
      String name = descriptor.get(1, "");
      ApplicationProperties properties = hierarchy.createDefaultProperties(name);
      this.setApplicationProperties(properties);
   }
}
