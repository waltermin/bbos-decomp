package net.rim.device.apps.api.pim;

import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.framework.model.DescriptionProvider;

public interface TimeBasedObject extends Duration, DescriptionProvider {
   long CONTEXT_FLAGS = -3866311304884942232L;
   int REFRESH_VIEW = 1;
}
