package net.rim.device.apps.internal.docview.gui;

final class DocViewImagePackageManager {
   public static final void libMain(String[] args) {
      registerOnceOnSystemStart();
   }

   private DocViewImagePackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      new ImageViewConverterDescriptor().register();
   }
}
