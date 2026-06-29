package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import net.rim.device.api.util.StringTokenizer;
import net.rim.plazmic.internal.mediaengine.dataformat.Units;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.Par;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimeContainer;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.util.DOMUtilities;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.util.IdMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SMILTimingParser {
   private Event _triggerEvent = (Event)(new Object());
   private Event _resultingEvent = (Event)(new Object());

   public void extractTimingData(Element element, TimeContainer parent, EventLogic logic, IdMap idMap) {
      this._resultingEvent._eventParam = this.getIntId(element, idMap);
      this._resultingEvent._event = SMILEvents.getIntId("begin");
      if (!element.hasAttribute("begin")) {
         element.setAttribute("begin", "0");
      }

      this.extractTimingAttribute("begin", element, parent, logic, idMap);
      this._resultingEvent._event = SMILEvents.getIntId("end");
      this.extractTimingAttribute("end", element, parent, logic, idMap);
      if (!parent.isImplicitDuration()) {
         this._triggerEvent._event = 2;
         this._triggerEvent._eventParam = parent.getId();
         logic.addEventDependancy(this._triggerEvent, this._resultingEvent, 0);
      }

      this._triggerEvent.clear();
      this._resultingEvent.clear();
   }

   private void extractTimingAttribute(String timingAttribute, Element element, TimeContainer parent, EventLogic logic, IdMap idMap) {
      if (element.hasAttribute(timingAttribute)) {
         String criteria = DOMUtilities.getAttribute(element, timingAttribute);
         StringTokenizer tokenizer = (StringTokenizer)(new Object(criteria, ";,"));

         while (tokenizer.hasMoreTokens()) {
            this.parseTimingCriteria(tokenizer.nextToken(), element, parent, logic, idMap, this._triggerEvent, this._resultingEvent);
            this._triggerEvent.clear();
         }
      }
   }

   private void parseTimingCriteria(
      String criteria, Element element, TimeContainer parent, EventLogic logic, IdMap idMap, Event triggerEvent, Event resultingEvent
   ) {
      long offset = 0;
      if (this.isOffset(criteria)) {
         offset = Units.getTime(criteria);
         if (parent instanceof Par) {
            triggerEvent._event = 1;
            triggerEvent._eventParam = parent.getId();
         } else {
            Element previousSibling = this.getPreviousElement(element);
            if (previousSibling == null) {
               triggerEvent._event = 1;
               triggerEvent._eventParam = parent.getId();
            } else {
               triggerEvent._event = 2;
               triggerEvent._eventParam = this.getIntId(previousSibling, idMap);
            }
         }
      } else if (criteria.startsWith("indefinite")) {
         triggerEvent._event = 13;
         triggerEvent._eventParam = this.getIntId(element, idMap);
      } else if (!criteria.startsWith("accesskey") && !criteria.startsWith("wallclock")) {
         int indexOfPeriod = this.findCriteriaDelimiter(criteria, '.');
         if (indexOfPeriod == -1) {
            throw new Object("FOR DEBUG");
         }

         int indexOfOffset = Math.max(criteria.indexOf(43), this.findCriteriaDelimiter(criteria, '-'));
         String id = criteria.substring(0, indexOfPeriod);
         triggerEvent._eventParam = idMap.getId(id);
         if (indexOfOffset != -1) {
            triggerEvent._event = this.getEventType(criteria.substring(indexOfPeriod + 1, indexOfOffset).trim());
            offset = Units.getTime(criteria.substring(indexOfOffset).trim());
         } else {
            triggerEvent._event = this.getEventType(criteria.substring(indexOfPeriod + 1));
         }
      }

      logic.addEventDependancy(this._triggerEvent, this._resultingEvent, offset);
   }

   private boolean isOffset(String criteria) {
      char char0 = criteria.charAt(0);
      return char0 >= '0' && char0 <= '9' || char0 == '-' || char0 == '+';
   }

   private int findCriteriaDelimiter(String criteria, char delimiter) {
      int index = criteria.indexOf(delimiter);

      while (index != -1 && criteria.charAt(index - 1) == '\\') {
         index = criteria.indexOf(delimiter, index + 1);
      }

      return index;
   }

   private Element getPreviousElement(Node node) {
      Node previous;
      do {
         previous = node.getPreviousSibling();
         node = previous;
      } while (node != null && !(node instanceof Object));

      return (Element)previous;
   }

   private int getEventType(String type) {
      int id = SMILEvents.getIntId(type);
      if (id == -1) {
         throw new Object();
      } else {
         return id;
      }
   }

   private int getIntId(Element element, IdMap idMap) {
      String id = DOMUtilities.getAttribute(element, "id");
      if (id.length() == 0) {
         id = idMap.createNewId();
         element.setAttribute("id", id);
      }

      return idMap.getId(id);
   }
}
