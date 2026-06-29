package net.rim.device.api.synchronization;

public interface OTASyncPriorityAndDependencyProvider extends OTASyncPriorityProvider {
   int NO_DEPENDENCY = 255;
   int MINIMUM_DEPENDENCY = 254;
   int MAXIMUM_DEPENDENCY = 1;

   int getDependencyLevel();
}
