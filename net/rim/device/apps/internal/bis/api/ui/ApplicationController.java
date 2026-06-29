package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.bis.utils.ArgValidationUtils;

public final class ApplicationController {
   private IntHashtable _commandIDToDomainCommandMap = new IntHashtable();
   private IntHashtable _commandIDToForwardsMap = new IntHashtable();
   private IntHashtable _linkIDToViewMap = new IntHashtable();
   private Hashtable _globalCommandResultToForwardMap = new Hashtable();

   private ApplicationController() {
   }

   public static final ApplicationController getInstance() {
      return ApplicationController$ApplicationControllerHolder.instance;
   }

   public final void registerCommand(int commandID, DomainCommand command) {
      ArgValidationUtils.notNull(command);
      this._commandIDToDomainCommandMap.put(commandID, command);
   }

   public final void registerViewForward(int commandID, String commandResult, int viewID) {
      if (this._globalCommandResultToForwardMap.get(commandResult) != null) {
         throw new IllegalArgumentException("There is already a global view associated with '" + commandResult);
      }

      if (this._commandIDToForwardsMap.get(commandID) == null) {
         this._commandIDToForwardsMap.put(commandID, new Hashtable());
      }

      Hashtable forwardsMap = (Hashtable)this._commandIDToForwardsMap.get(commandID);
      forwardsMap.put(commandResult, new ApplicationController$Forward(viewID));
   }

   public final void registerLinkView(int linkID, Screen view) {
      ArgValidationUtils.notNull(view);
      this._linkIDToViewMap.put(linkID, view);
   }

   public final void registerGlobalView(String commandResult, int viewID) {
      ArgValidationUtils.notNull(commandResult);
      this._globalCommandResultToForwardMap.put(commandResult, new ApplicationController$Forward(viewID));
   }

   public final DomainCommand getDomainCommand(int commandID) {
      return (DomainCommand)this._commandIDToDomainCommandMap.get(commandID);
   }

   public final ApplicationController$Forward getViewForward(int commandID, String commandResult) {
      ArgValidationUtils.notNull(commandResult);
      ApplicationController$Forward forward = (ApplicationController$Forward)this._globalCommandResultToForwardMap.get(commandResult);
      Hashtable forwardsMap = (Hashtable)this._commandIDToForwardsMap.get(commandID);
      if (forwardsMap == null) {
         return forward;
      }

      if (forwardsMap.containsKey(commandResult)) {
         forward = (ApplicationController$Forward)forwardsMap.get(commandResult);
      }

      return forward;
   }

   public final Screen getLinkView(int linkID) {
      return (Screen)this._linkIDToViewMap.get(linkID);
   }

   ApplicationController(ApplicationController$1 x0) {
      this();
   }
}
