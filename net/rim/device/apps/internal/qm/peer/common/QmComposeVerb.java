package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.qm.resource.QmResources;

public class QmComposeVerb extends Verb {
   public static final int COMPOSE_IM = 1267040;

   public QmComposeVerb(int ordering) {
   }

   @Override
   public int getVerbGroupId() {
      return -1937319827;
   }

   @Override
   public String toString(Object context) {
      return this.toString();
   }

   @Override
   public String toString() {
      return Entry.stripOffHotKey(QmResources.getString(75));
   }
}
