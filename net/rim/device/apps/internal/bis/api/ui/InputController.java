package net.rim.device.apps.internal.bis.api.ui;

import java.util.Hashtable;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.bis.event.BackEvent;
import net.rim.device.apps.internal.bis.event.CloseEvent;
import net.rim.device.apps.internal.bis.event.LinkEvent;

public final class InputController {
   private Thread _commandExecutionThread;
   private InputController$CommandRunner _commandRunner;

   private InputController() {
   }

   public static final InputController getInstance() {
      return InputController$InputControllerHolder.instance;
   }

   public final void submitInput(int commandID, Hashtable params) {
      if (ApplicationController.getInstance().getDomainCommand(commandID) == null) {
         throw new IllegalArgumentException("Unknown command id");
      }

      if (this._commandRunner == null) {
         this._commandRunner = new InputController$CommandRunner();
         this._commandExecutionThread = new Thread(this._commandRunner, "CommandRunner");
         this._commandExecutionThread.start();
      }

      this._commandRunner.invokeCommand(commandID, params);
   }

   public final void submitEvent(Event event) {
      if (!(event instanceof LinkEvent)) {
         if (event instanceof BackEvent) {
            UiApplication app = UiApplication.getUiApplication();
            app.popScreen(app.getActiveScreen());
            return;
         }

         if (event instanceof CloseEvent) {
            System.exit(0);
         }
      } else {
         LinkEvent linkEvent = (LinkEvent)event;
         Screen screen = ApplicationController.getInstance().getLinkView(linkEvent.getLink());
         if (screen != null) {
            UiApplication app = UiApplication.getUiApplication();
            if (screen instanceof RefreshableScreen) {
               Hashtable params = null;
               if (app.getActiveScreen() != null) {
                  if (params == null) {
                     params = new Hashtable();
                  }

                  params.put("previousScreen", app.getActiveScreen());
               }

               RefreshableScreen refreshableScreen = (RefreshableScreen)screen;
               refreshableScreen.clearScreen();
               refreshableScreen.refresh(params);
               if (!refreshableScreen.canGoBack()) {
                  for (Screen activeScreen = app.getActiveScreen(); activeScreen != null; activeScreen = app.getActiveScreen()) {
                     app.popScreen(activeScreen);
                  }
               }
            }

            app.pushScreen(screen);
            return;
         }
      }
   }

   InputController(InputController$1 x0) {
      this();
   }
}
