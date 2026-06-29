package net.rim.wica.runtime.lifecycle;

import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.WicletContext;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;

public interface Wiclet extends WicletContext {
   int INSTALLED_STATE = 0;
   int INSTALLING_STATE = 1;
   int UNINSTALLED_STATE = 2;
   int QUARANTINED_STATE = 4;
   int STOPPED_STATE = 0;
   int STARTING_STATE = 1;
   int ACTIVE_STATE = 2;
   int STOPPING_STATE = 4;
   int UPDATING_STATE = 8;

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
