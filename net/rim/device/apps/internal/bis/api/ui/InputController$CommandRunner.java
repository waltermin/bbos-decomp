package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.bis.ApplicationResources;
import net.rim.device.apps.internal.bis.BISEventLogger;
import net.rim.device.apps.internal.bis.commands.InitializationCommand;
import net.rim.device.apps.internal.bis.session.ClientSessionState;
import net.rim.device.apps.internal.bis.ui.CannotContinueScreen;

final class InputController$CommandRunner implements Runnable {
   private Object _commandLock = new Object();
   private int _commandID = -1;
   private Hashtable _params;
   private boolean _commandReadyToBeRun;
   private BusyDialog _busyDialog = new BusyDialog();

   public final void invokeCommand(int commandID, Hashtable params) {
      synchronized (this._commandLock) {
         this._commandID = commandID;
         this._params = params;
         this._commandReadyToBeRun = true;
         this._commandLock.notify();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         UiApplication app = UiApplication.getUiApplication();
         ApplicationController appController = ApplicationController.getInstance();

         while (true) {
            synchronized (this._commandLock) {
               if (!this._commandReadyToBeRun) {
                  try {
                     this._commandLock.wait();
                  } finally {
                     continue;
                  }
               } else {
                  DomainCommand domainCommand = appController.getDomainCommand(this._commandID);
                  synchronized (Application.getEventLock()) {
                     this._busyDialog.reloadMessage();
                     app.pushScreen(this._busyDialog);
                  }

                  DomainCommandResult commandResult = domainCommand.run(this._params);
                  synchronized (Application.getEventLock()) {
                     app.popScreen(this._busyDialog);
                  }

                  if (commandResult != null
                     && commandResult == DomainCommand.SESSION_TIMEOUT_RESULT
                     && ClientSessionState.getInstance().getUserInfo().isAutoAuth()) {
                     this._commandID = 0;
                     domainCommand = new InitializationCommand();
                     commandResult = domainCommand.run(new Hashtable());
                     commandResult.setStatus(ApplicationResources.getString(224));
                  }

                  if (commandResult != null) {
                     String commandResultName = commandResult.getResultName();
                     if (commandResultName != null) {
                        ApplicationController$Forward forward = appController.getViewForward(this._commandID, commandResultName);
                        Screen screen = forward.getView();
                        if (screen instanceof RefreshableScreen) {
                           RefreshableScreen refreshableScreen = (RefreshableScreen)screen;
                           if (commandResult.getParamsToForward() != null) {
                              this._params = commandResult.getParamsToForward();
                           }

                           if (app.getActiveScreen() != null) {
                              if (this._params == null) {
                                 this._params = new Hashtable();
                              }

                              this._params.put("previousScreen", app.getActiveScreen());
                           }

                           if (screen != app.getActiveScreen()) {
                              synchronized (Application.getEventLock()) {
                                 refreshableScreen.clearScreen();
                                 refreshableScreen.refresh(this._params);
                              }

                              if (!refreshableScreen.canGoBack()) {
                                 synchronized (Application.getEventLock()) {
                                    for (Screen activeScreen = app.getActiveScreen(); activeScreen != null; activeScreen = app.getActiveScreen()) {
                                       app.popScreen(activeScreen);
                                    }
                                 }
                              }
                           } else {
                              synchronized (Application.getEventLock()) {
                                 refreshableScreen.clearScreen();
                                 refreshableScreen.refresh(this._params);
                              }
                           }

                           refreshableScreen.setError(commandResult.getError());
                           refreshableScreen.setStatus(commandResult.getStatus());
                        }

                        if (screen != null && screen != app.getActiveScreen()) {
                           Application.getApplication().invokeLater(new InputController$ForwardScreenRunnable(screen));
                        }
                     }
                  }

                  this._commandID = -1;
                  this._params = null;
                  this._commandReadyToBeRun = false;
               }
            }
         }
      } catch (Throwable var38) {
         BISEventLogger.logEvent(e.toString(), 0);
         RefreshableScreen cannotContinueScreen = new CannotContinueScreen();
         cannotContinueScreen.refresh(null);
         Application.getApplication().invokeLater(new InputController$ForwardScreenRunnable(cannotContinueScreen));
         return;
      }
   }
}
