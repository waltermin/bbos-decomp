package net.rim.device.api.synchronization;

public interface OTASyncPriorityAndDependencyProvider extends OTASyncPriorityProvider {
   int NO_DEPENDENCY;
   int MINIMUM_DEPENDENCY;
   int MAXIMUM_DEPENDENCY;

   int getDependencyLevel();
}
