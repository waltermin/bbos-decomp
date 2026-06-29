package net.rim.device.apps.internal.browser.core;

import java.util.Vector;
import net.rim.device.api.util.Persistable;

final class PendingRequestThread$PendingRequestStore implements Persistable {
   private Vector _pendingRequests = new Vector(0, 1);
   private Vector _pendingRequestListeners = new Vector(0, 1);
   private Vector _savedPendingRequests = new Vector(0, 1);
}
