package net.rim.wica.runtime.metadata.internal.handler;

import java.util.Stack;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.metadata.Wiclet;
import net.rim.wica.runtime.metadata.WicletRuntime;
import net.rim.wica.runtime.metadata.component.ui.ScreenManager;
import net.rim.wica.runtime.metadata.component.ui.ScreenModel;
import net.rim.wica.runtime.metadata.internal.component.ui.ScreenModelImpl;
import net.rim.wica.runtime.metadata.internal.transaction.TransactionManager;
import net.rim.wica.runtime.script.ScriptEngine;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.ui.UiService;

public class UIHandler implements Serviceable, ScreenManager, EventListener {
   private Stack _screenStack;
   private ScreenModelImpl _currentScreenModel;
   private UiService _uiService;
   private TransactionManager _transactions;
   private ScriptEngine _scriptEngine;
   private Wiclet _wiclet;
   private WicletRuntime _runtime;
   private boolean _keepTail = false;
   private int _giantTransactionId = -1;
   static Class class$net$rim$wica$runtime$ui$UiService;
   static Class class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager;
   static Class class$net$rim$wica$runtime$script$ScriptEngine;
   static Class class$net$rim$wica$runtime$metadata$WicletRuntime;
   static Class class$net$rim$wica$runtime$event$EventService;

   public boolean execute(int id, long[] params) {
      switch (this._wiclet.getDefType(id)) {
         case 9:
            return true;
         case 10:
         default:
            this.display(this._wiclet.getScreenModel(id), params);
            return true;
         case 11:
            return this._scriptEngine.call(id, params);
      }
   }

   public void shutdown() {
      if (this._currentScreenModel != null) {
         this._currentScreenModel.clean();
      }

      for (int screenIndex = this._screenStack.size() - 1; screenIndex >= 0; screenIndex--) {
         ((ScreenModelImpl)this._screenStack.elementAt(screenIndex)).clean();
      }

      this._screenStack.removeAllElements();
      this.completeTransaction(false);
   }

   public void deactivate() {
      if (this._currentScreenModel != null) {
         this._currentScreenModel.updateData();
      }

      this.completeTransaction(false);
   }

   public void activate() {
      if (this._currentScreenModel != null) {
         this._currentScreenModel.updateUI();
      }
   }

   public void completeTransaction(boolean rollback) {
      if (this._transactions != null) {
         if (rollback) {
            this._transactions.undo(true, this._giantTransactionId, true);
         } else {
            this._transactions.complete(true, this._giantTransactionId, true, true);
         }

         this._giantTransactionId = this._transactions.startTransaction();
         int screenCount = this._screenStack.size();

         for (int i = 0; i < screenCount; i++) {
            ((ScreenModelImpl)this._screenStack.elementAt(i)).setTransactionId(this._transactions.startTransaction());
         }

         if (this._currentScreenModel != null) {
            this._currentScreenModel.setTransactionId(this._transactions.startTransaction());
         }
      }
   }

   @Override
   public void close(boolean saveChanges) {
      if (this._currentScreenModel == null) {
         this._runtime.stop();
      } else {
         if (this._transactions != null) {
            int id = this._currentScreenModel.getTransactionId();
            if (id != -1) {
               this._currentScreenModel.setTransactionId(-1);
               if (!saveChanges) {
                  this._transactions.undo(true, id, true);
               }
            }
         }

         this._currentScreenModel.clean();
         this._currentScreenModel.setDisplayed(false);
         this._currentScreenModel = this._screenStack.size() > 0 ? (ScreenModelImpl)this._screenStack.pop() : null;
         if (this._currentScreenModel != null) {
            if (saveChanges) {
               this._currentScreenModel.invalidateUI(true);
               this._currentScreenModel.updateUI();
            } else if (!this._keepTail) {
               int id = this._currentScreenModel.getLastTransactionId();
               if (id != -1 && this._transactions.contains(id)) {
                  this._transactions.undo(true, id, true);
               }
            }

            this._currentScreenModel.setDisplayed(true);
            this._uiService.displayScreen(this._currentScreenModel, false);
         } else {
            this._runtime.stop();
         }
      }
   }

   @Override
   public void back() {
      if (this.isBackAvailable()) {
         this.close(false);
      }
   }

   @Override
   public boolean isBackAvailable() {
      return this._screenStack.size() > 0;
   }

   @Override
   public ScreenModel getCurrentScreenModel() {
      return this._currentScreenModel;
   }

   @Override
   public void display(ScreenModel newScreenModel, long[] params) {
      ScreenModelImpl prevScreenModel = this._currentScreenModel;
      if (prevScreenModel != null) {
         prevScreenModel.updateUI();
         prevScreenModel.updateData();
      }

      if (prevScreenModel != newScreenModel) {
         if (this._keepTail) {
            ScreenModelImpl topOfStack = this._screenStack.size() > 0 ? (ScreenModelImpl)this._screenStack.peek() : null;
            if (!newScreenModel.isDialog() && newScreenModel != topOfStack) {
               for (int screenIndex = this._screenStack.indexOf(newScreenModel); screenIndex >= 0; screenIndex--) {
                  ((ScreenModelImpl)this._screenStack.elementAt(screenIndex)).clean();
                  this._screenStack.removeElementAt(screenIndex);
               }
            } else if (topOfStack == newScreenModel) {
               if (prevScreenModel != null && prevScreenModel.isDialog()) {
                  this._screenStack.pop();
               } else {
                  this._screenStack.removeAllElements();
               }
            }

            if (prevScreenModel != null) {
               if (!prevScreenModel.isDialog()) {
                  this._screenStack.push(prevScreenModel);
               } else {
                  prevScreenModel.clean();
               }
            }
         } else {
            int screenIndex = this._screenStack.indexOf(newScreenModel);
            if (screenIndex != -1) {
               for (int i = this._screenStack.size() - 1; i >= screenIndex; i--) {
                  ((ScreenModelImpl)this._screenStack.elementAt(i)).clean();
                  this._screenStack.removeElementAt(i);
               }
            } else if (prevScreenModel != null && !prevScreenModel.isDialog()) {
               this._screenStack.push(prevScreenModel);
            }

            if (prevScreenModel != null && prevScreenModel.isDialog()) {
               prevScreenModel.clean();
            }
         }

         this._currentScreenModel = (ScreenModelImpl)newScreenModel;
         if (this._transactions != null) {
            this._currentScreenModel.setTransactionId(this._transactions.startTransaction());
            if (prevScreenModel != null) {
               prevScreenModel.setLastTransactionId(this._currentScreenModel.getTransactionId());
            }
         }
      }

      if (prevScreenModel != null) {
         prevScreenModel.setDisplayed(false);
      }

      this._currentScreenModel.display(params);
      this._uiService.displayScreen(this._currentScreenModel, true);
   }

   @Override
   public void display(ScreenModel screenModel) {
      this.display(screenModel, null);
   }

   @Override
   public void handleEvent(Object sender, int event, int eventParam, Object data) {
      switch (event) {
         case 207:
            this.verifyHistoryRule();
            break;
         case 600:
            if (this._currentScreenModel != null) {
               this._currentScreenModel.updateData();
               return;
            }
            break;
         case 601:
            if (this._currentScreenModel != null) {
               if (this._currentScreenModel.isRefreshMsg(eventParam)) {
                  this._currentScreenModel.invalidateUI(true);
                  if (this._runtime.isActive()) {
                     this._currentScreenModel.updateUI();
                  }
               }

               if (!this._screenStack.empty()) {
                  for (int i = this._screenStack.size() - 1; i >= 0; i--) {
                     ScreenModelImpl screenModel = (ScreenModelImpl)this._screenStack.elementAt(i);
                     if (screenModel != null && screenModel.isRefreshMsg(eventParam)) {
                        screenModel.invalidateUI(true);
                        if (this._runtime.isActive()) {
                           screenModel.updateUI();
                        }
                     }
                  }
               }
            }
            break;
         case 603:
         case 604:
            if (this._currentScreenModel != null && this._currentScreenModel.isDisplayed()) {
               this._currentScreenModel.updateUI();
               return;
            }
      }
   }

   @Override
   public void setServices(ServiceProvider provider) {
      this._uiService = (UiService)provider.getService(
         class$net$rim$wica$runtime$ui$UiService == null
            ? (class$net$rim$wica$runtime$ui$UiService = class$("net.rim.wica.runtime.ui.UiService"))
            : class$net$rim$wica$runtime$ui$UiService
      );
      this._transactions = (TransactionManager)provider.getService(
         class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager == null
            ? (
               class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager = class$(
                  "net.rim.wica.runtime.metadata.internal.transaction.TransactionManager"
               )
            )
            : class$net$rim$wica$runtime$metadata$internal$transaction$TransactionManager
      );
      this._giantTransactionId = this._transactions == null ? -1 : this._transactions.startTransaction();
      this._scriptEngine = (ScriptEngine)provider.getService(
         class$net$rim$wica$runtime$script$ScriptEngine == null
            ? (class$net$rim$wica$runtime$script$ScriptEngine = class$("net.rim.wica.runtime.script.ScriptEngine"))
            : class$net$rim$wica$runtime$script$ScriptEngine
      );
      this._runtime = (WicletRuntime)provider.getService(
         class$net$rim$wica$runtime$metadata$WicletRuntime == null
            ? (class$net$rim$wica$runtime$metadata$WicletRuntime = class$("net.rim.wica.runtime.metadata.WicletRuntime"))
            : class$net$rim$wica$runtime$metadata$WicletRuntime
      );
      this._wiclet = this._runtime.getWiclet();
      EventService eventService = (EventService)provider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      eventService.addListener(604, this);
      eventService.addListener(603, this);
      eventService.addListener(600, this);
      eventService.addListener(601, this);
      eventService.addListener(207, this);
      this.verifyHistoryRule();
   }

   public UIHandler() {
      this._screenStack = new Stack();
   }

   private void verifyHistoryRule() {
      String rule = this._wiclet.getContext().getProperty("historyRule");
      this._keepTail = "keepTail".equals(rule);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
