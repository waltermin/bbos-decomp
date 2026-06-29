package net.rim.device.apps.internal.browser.core;

import java.util.Vector;
import net.rim.device.api.util.Persistable;

final class PendingRequestThread$PendingRequestStore implements Persistable {
   private Vector _pendingRequests = (Vector)(new Object(0, 1));
   private Vector _pendingRequestListeners = (Vector)(new Object(0, 1));
   private Vector _savedPendingRequests = (Vector)(new Object(0, 1));
}
