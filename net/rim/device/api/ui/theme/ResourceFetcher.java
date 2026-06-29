package net.rim.device.api.ui.theme;

import java.util.Enumeration;
import net.rim.device.resources.Resource;

public interface ResourceFetcher {
   void setResourcesFromModule(String var1);

   Resource getResources();

   Enumeration listResources();

   byte[] fetchResource(String var1);

   String getBaseURL();
}
