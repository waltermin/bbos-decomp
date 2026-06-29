package net.rim.wica.runtime.lifecycle;

import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;

public interface Wiclet extends WicletContext {
   int INSTALLED_STATE;
   int INSTALLING_STATE;
   int UNINSTALLED_STATE;
   int QUARANTINED_STATE;
   int STOPPED_STATE;
   int STARTING_STATE;
   int ACTIVE_STATE;
   int STOPPING_STATE;
   int UPDATING_STATE;

   int getState();

   int getExecutionState();

   String getVersion();

   String getLanguage();

   String getVendor();

   String getDescription();

   int getMetadataSize();

   int getDataSize();

   int getCacheSize();

   String getTargetFolder();

   boolean isSystemApplication();

   int getMessageDelivery();

   long getAgId();

   int getInboundQueueSizeLimit();

   int getOutboundQueueSizeLimit();

   String getIconUri();

   long getInstallDate();

   EventService getEventService();

   boolean isRunning();

   boolean isQuarantined();

   boolean isUpgrading();

   boolean hasUpgrade();

   long getUpgradeExpiryDate();

   DeploymentDescriptor getUpgradeDescriptor();
}
