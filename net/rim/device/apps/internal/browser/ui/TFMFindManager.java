package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.html.HTMLBrowserField;
import net.rim.vm.Array;

public final class TFMFindManager {
   private TextFlowManager _manager;
   private FindVerb _findVerb;
   private FindNextVerb _findNextVerb;
   StringMatch _stringMatch;
   private long _position = Long.MIN_VALUE;

   public TFMFindManager(TextFlowManager baseManager) {
      this._manager = baseManager;
   }

   public final Verb[] getVerbs() {
      Verb[] verbs = new Verb[2];
      int verbCount = 0;
      if (this._findVerb == null) {
         this._findVerb = new FindVerb(this);
      }

      verbs[verbCount++] = this._findVerb;
      if (this._position != Long.MIN_VALUE) {
         if (this._findNextVerb == null) {
            this._findNextVerb = new FindNextVerb(this);
         }

         verbs[verbCount++] = this._findNextVerb;
      }

      Array.resize(verbs, verbCount);
      return verbs;
   }

   public final Object invokeFind(boolean findNext, Object context) {
      Verb[] verbs = this.getVerbs();
      return findNext && verbs.length > 1 ? verbs[1].invoke(context) : verbs[0].invoke(context);
   }

   final boolean findMatch(boolean findNext, boolean initialTFM) {
      if (!findNext) {
         this._position = this._manager.getCurrentTextPosition();
      }

      this._position = this._manager.find(this._stringMatch, findNext, this._position);
      if (this._position == -9223372036854775807L) {
         this._position = Long.MIN_VALUE;
         if (this.findInFrames(this._manager, 0)) {
            return true;
         }

         if (initialTFM) {
            Manager parentManager = this._manager.getManager();
            if (parentManager instanceof FrameManager && this.findInFrames(parentManager.getManager(), parentManager.getIndex() + 1)) {
               return true;
            }

            Status.show(CommonResources.getString(9026), 750);
         }

         return false;
      } else {
         return true;
      }
   }

   private final boolean findInFrames(Manager manager, int startingIndex) {
      if (manager != null) {
         int fieldCount = manager.getFieldCount();

         for (int i = startingIndex; i < fieldCount; i++) {
            Field field = manager.getField(i);
            if (field instanceof FrameManager) {
               FrameManager frameManager = (FrameManager)field;
               if (frameManager.getFieldCount() > 0) {
                  field = frameManager.getField(0);
                  if (field instanceof HTMLBrowserField && ((HTMLBrowserField)field).getBrowserContent().invokeFind(false, this._stringMatch) == Boolean.TRUE) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }
}
