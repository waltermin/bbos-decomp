package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.EntityReference;

final class ESEntityReference extends ESNode {
   ESEntityReference(EntityReference er) {
      super(er, Names.EntityReference);
   }
}
