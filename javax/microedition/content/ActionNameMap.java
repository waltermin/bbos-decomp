package javax.microedition.content;

import net.rim.device.api.util.Arrays;

public final class ActionNameMap {
   private final String[] _actions;
   private final String[] _actionnames;
   private final String _locale;

   public ActionNameMap(String[] actions, String[] actionnames, String locale) {
      ContentHandlerUtilities.checkStringArrayValues(actions);
      ContentHandlerUtilities.checkStringArrayValues(actionnames);
      if (locale == null) {
         throw new NullPointerException("locale is null");
      }

      if (actions.length != actionnames.length) {
         throw new IllegalArgumentException("Mismatched array sizes");
      }

      if (actions.length == 0) {
         throw new IllegalArgumentException("Zero-length array");
      }

      if (ContentHandlerUtilities.containsDuplicates(actions)) {
         throw new IllegalArgumentException("actions array contains duplicate actions");
      }

      this._actions = actions;
      this._actionnames = actionnames;
      this._locale = locale;
   }

   public final String getActionName(String action) {
      if (action == null) {
         throw new NullPointerException("action is null");
      }

      int index = Arrays.getIndex(this._actions, action);
      return index < 0 ? null : this._actionnames[index];
   }

   public final String getAction(String actionname) {
      if (actionname == null) {
         throw new NullPointerException("actionname is null");
      }

      int index = Arrays.getIndex(this._actionnames, actionname);
      return index < 0 ? null : this._actions[index];
   }

   public final String getLocale() {
      return this._locale;
   }

   public final int size() {
      return this._actions.length;
   }

   public final String getAction(int index) {
      if (index >= 0 && index < this.size()) {
         return this._actions[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public final String getActionName(int index) {
      if (index >= 0 && index < this.size()) {
         return this._actionnames[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
