package net.rim.wica.compatibility.handlers;

import net.rim.wica.compatibility.VersionContext;

public interface CapabilityHandler {
   boolean isCapable(VersionContext var1);

   String getName();
}
