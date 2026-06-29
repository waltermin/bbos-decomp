package net.rim.plazmic.internal.mediaengine.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.registry.Registry;
import net.rim.plazmic.internal.mediaengine.registry.RegistryProvider;

public class RIMRegistryProvider implements RegistryProvider {
   public static final long ME_REGISTRY_KEY;
   private static Registry _registry;

   public RIMRegistryProvider() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _registry = (Registry)ar.getOrWaitFor(-3828669597622888330L);
      if (_registry == null) {
         _registry = (Registry)MediaFactory.createObject("net.rim.plazmic.internal.mediaengine.registry.RegistryImpl");
         this.initDefaultEntries();
         ar.put(-3828669597622888330L, _registry);
      }
   }

   protected void initDefaultEntries() {
      _registry.setValue(new String[]{"CONTENT", "application/x-vnd.rim.pme"}, "1");
      _registry.setValue(new String[]{"CONTENT", "application/x-vnd.rim.pme", "VERSION_READER"}, "net.rim.plazmic.internal.mediaengine.io.PMEVersionReader");
      _registry.setValue(new String[]{"FRAMEWORK", "PLATFORM"}, "net.rim.plazmic.internal.mediaengine.util.RIMPlatformImpl");
      _registry.setValue(new String[]{"FRAMEWORK", "CONNECTOR"}, "net.rim.plazmic.internal.mediaengine.io.BBMediaConnector");
      _registry.setValue(new String[]{"FRAMEWORK", "UI"}, "net.rim.device.api.ui.MediaField");
   }

   @Override
   public Registry getRegistry() {
      return _registry;
   }
}
