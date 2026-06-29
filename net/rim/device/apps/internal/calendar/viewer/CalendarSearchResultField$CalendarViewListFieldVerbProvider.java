package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.HotKeyProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchResultField;
import net.rim.device.apps.internal.calendar.eventprovider.NewEventVerb;
import net.rim.device.internal.ui.UiInternal;
import net.rim.tid.im.layout.SLKeyLayout;

final class CalendarSearchResultField$CalendarViewListFieldVerbProvider extends CalendarViewListField implements VerbProvider, SearchResultField {
   private final CalendarSearchResultField this$0;

   public CalendarSearchResultField$CalendarViewListFieldVerbProvider(
      CalendarSearchResultField _1, CalendarViewListField$CalendarViewListFieldCallback callback, boolean multiSelectAllowed
   ) {
      super(callback, multiSelectAllowed);
      this.this$0 = _1;
   }

   @Override
   public final Object getSelectedObject() {
      return this.this$0.getSelectedObjectInternal();
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      key = UiInternal.map(Keypad.getLayout().getOriginalKeyCode(key, SLKeyLayout.convertStatusToModifiers(status)), status);
      Object selectedObject = this.getSelectedObject();
      ResourceBundle rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
      char lowerCase = Character.toLowerCase(key);
      if (key == '\n') {
         if (selectedObject != null) {
            this.getScreen().invokeDefaultMenuItem(0);
         }

         return true;
      } else {
         if (lowerCase == rb.getString(349).charAt(0)) {
            CalendarSearchResultField$SearchResultsVerb todayVerb = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 321, 479504, 349, 1);
            todayVerb.invoke(null);
            return true;
         }

         if (lowerCase != rb.getString(341).charAt(0) && (key != ' ' || (status & 2) == 0)) {
            if (lowerCase != rb.getString(340).charAt(0) && (key != ' ' || (status & 2) != 0)) {
               return !(selectedObject instanceof HotKeyProvider) ? true : ((HotKeyProvider)selectedObject).invokeHotkey(null, key) != null;
            }

            CalendarSearchResultField$SearchResultsVerb nextVerb = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 321, 479504, 349, 2);
            nextVerb.invoke(null);
            return true;
         } else {
            CalendarSearchResultField$SearchResultsVerb previousVerb = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 321, 479504, 349, 0);
            previousVerb.invoke(null);
            return true;
         }
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Object selectedObject = this.getSelectedObject();
            if (selectedObject != null) {
               this.getScreen().invokeDefaultMenuItem(0);
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      Object selectedObject = this.getSelectedObject();
      if (selectedObject instanceof VerbProvider) {
         defaultVerb = ((VerbProvider)selectedObject).getVerbs(context, verbs);
      }

      if (verbs == null) {
         verbs = new Verb[0];
      }

      CalOptionCache.setTimeWithFocus(this.this$0.getSelectedStartTime());
      NewEventVerb newEventVerb = new NewEventVerb();
      Arrays.add(verbs, newEventVerb);
      if (defaultVerb == null) {
         defaultVerb = newEventVerb;
      }

      CalendarSearchResultField$SearchResultsVerb pickDate = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 320, 479509, 348, 3);
      Arrays.add(verbs, pickDate);
      if (!ContextObject.getFlag(context, 81)) {
         CalendarSearchResultField$SearchResultsVerb nextDate = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 329, 479525, 340, 2);
         Arrays.add(verbs, nextDate);
         CalendarSearchResultField$SearchResultsVerb prevDate = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 328, 479520, 341, 0);
         Arrays.add(verbs, prevDate);
         CalendarSearchResultField$SearchResultsVerb todayVerb = new CalendarSearchResultField$SearchResultsVerb(this.this$0, 321, 479504, 349, 1);
         Arrays.add(verbs, todayVerb);
      }

      return defaultVerb;
   }
}
