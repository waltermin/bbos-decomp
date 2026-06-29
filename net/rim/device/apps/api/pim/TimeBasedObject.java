package net.rim.device.apps.api.pim;

import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.framework.model.DescriptionProvider;

public interface TimeBasedObject extends Duration, DescriptionProvider {
   long CONTEXT_FLAGS;
   int REFRESH_VIEW;
}
