package net.rim.plazmic.internal.mediaengine.model.intarray.v1_2;

import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.util.Platform;

class BehaviorManager$LogicBuilder {
   private Event _cause;
   private Event _effect;
   private EventLogic _logic;
   private AnimationModel _data;

   private EventLogic buildLogic(AnimationModel data) {
      EventLogic result = null;
      this._data = data;
      if (this._data._behaviorsRoot != -1) {
         if (this._data._logic == null) {
            this._data._logic = (EventLogic)(new Object());
         }

         result = this._logic = this._data._logic;
         this._cause = (Event)(new Object());
         this._effect = (Event)(new Object());
         this.buildEventLogic(this._data._behaviorsRoot);
         this._cause = null;
         this._effect = null;
         this._logic = null;
         this._data = null;
      }

      return result;
   }

   private void buildEventLogic(int nodeIdx) {
      int beginTriggerIndex = this._data._nodes[nodeIdx + 9];
      int beginEventType = 200;
      int endTriggerIndex = -1;
      int endEventType = 201;
      this._effect._eventParam = nodeIdx;
      int nodeType = this._data._nodes[nodeIdx + 1];
      switch (nodeType) {
         case 1:
            beginEventType = 203;
            endEventType = 204;
            endTriggerIndex = this._data._nodes[nodeIdx + 10];
            break;
         case 3:
         case 11:
            beginEventType = 207;
            endEventType = 208;
            endTriggerIndex = this._data._nodes[nodeIdx + 10];
            break;
         case 5:
            beginEventType = 206;
            break;
         case 9:
            beginEventType = 209;
      }

      int parentIdx = this._data._nodes[nodeIdx + 3];
      if (beginTriggerIndex != -1) {
         this._effect._event = beginEventType;
         this.processTriggers(parentIdx, this._data._triggers, beginTriggerIndex);
      } else if (parentIdx != -1 && nodeType != 9 && nodeType != 5) {
         this._effect._event = beginEventType;
         this._cause._event = 100;
         this._cause._eventParam = parentIdx;
         int triggerIdx = this._data._nodes[parentIdx + 9];
         if (triggerIdx != -1) {
            this._cause._eventParamLong = this._data._triggers[triggerIdx + 3];
         } else {
            this._cause._eventParamLong = 0;
         }

         this._logic.addEventDependancy(this._cause, this._effect, 0);
      }

      if (endTriggerIndex != -1) {
         this._effect._event = endEventType;
         this.processTriggers(parentIdx, this._data._triggers, endTriggerIndex);
      }

      for (int next = this._data._nodes[nodeIdx + 6]; next != -1; next = this._data._nodes[next + 4]) {
         this.buildEventLogic(next);
      }
   }

   private void processTriggers(int parentIdx, int[] triggers, int triggerIndex) {
      Platform platform = MediaFactory.getPlatform();
      int numTriggers = triggers[triggerIndex++];

      for (int i = 0; i < numTriggers; i++) {
         this._cause._event = triggers[triggerIndex + 0];
         if (this._cause._event == 106) {
            this._cause._eventParam = platform.getKeyCode(triggers[triggerIndex + 3]);
            this._cause._eventParamLong = triggers[triggerIndex + 1] == -1 ? 0 : triggers[triggerIndex + 1];
         } else {
            this._cause._eventParam = triggers[triggerIndex + 1];
            this._cause._eventParamLong = triggers[triggerIndex + 3];
         }

         if (this._cause._event == -1 && this._cause._eventParam == -1 && parentIdx != -1) {
            this._cause._event = 100;
            this._cause._eventParam = parentIdx;
         }

         this._logic.addEventDependancy(this._cause, this._effect, triggers[triggerIndex + 2]);
         triggerIndex += 4;
      }
   }
}
