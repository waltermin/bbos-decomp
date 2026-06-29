package net.rim.plazmic.internal.mediaengine.module;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.registry.Registry;

class BundleReader {
   public static void libMain(String[] args) {
      Registry registry = MediaFactory.getRegistry();
      registry.setValue(new String[]{"CONTENT", "application/x-vnd.rim.pme.b"}, "1");
      registry.setValue(
         new String[]{"CONTENT", "application/x-vnd.rim.pme.b", "VERSION_READER"}, "net.rim.plazmic.internal.mediaengine.format.BundleVersionReader"
      );
      registry.setValue(
         new String[]{"CONTENT", "application/x-vnd.rim.pme.b", "RESOURCE_PROVIDER"}, "net.rim.plazmic.internal.mediaengine.io.BundledMediaReader"
      );
   }
}
