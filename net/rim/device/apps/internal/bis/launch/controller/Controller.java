package net.rim.device.apps.internal.bis.launch.controller;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.bis.launch.resource.ApplicationResources;
import net.rim.device.apps.internal.bis.launch.ui.BusyDialog;

public final class Controller implements Runnable {
   private Object _runLock = new Object();
   private Runnable _runnable;
   private Runnable _navigation;
   private boolean _readyToBeRun;
   private BusyDialog _busyDialog = new BusyDialog();
   private static Controller _instance;

   public static final void initialize() {
      _instance = new Controller();
      Thread controllerThread = new Thread(_instance);
      controllerThread.start();
   }

   public static final void run(Runnable runnable, Runnable navigation, String busyMessage) {
      _instance.runRunnable(runnable, navigation, busyMessage);
   }

   protected final void runRunnable(Runnable runnable, Runnable navigation, String busyMessage) {
      synchronized (this._runLock) {
         if (busyMessage == null) {
            this._busyDialog.setMessage(ApplicationResources.getString(16));
         } else {
            this._busyDialog.setMessage(busyMessage);
         }

         this._runnable = runnable;
         this._navigation = navigation;
         this._readyToBeRun = true;
         this._runLock.notify();
      }
   }

   @Override
   public final void run() {
      try {
         UiApplication app = UiApplication.getUiApplication();

         while (true) {
            synchronized (this._runLock) {
               if (this._readyToBeRun) {
                  synchronized (Application.getEventLock()) {
                     app.pushScreen(this._busyDialog);
                  }

                  this._runnable.run();
                  synchronized (Application.getEventLock()) {
                     app.popScreen(this._busyDialog);
                  }

                  app.invokeLater(this._navigation);
                  this._runnable = null;
                  this._navigation = null;
                  this._readyToBeRun = false;
               } else {
                  try {
                     this._runLock.wait();
                  } finally {
                     continue;
                  }
               }
            }
         }
      } finally {
         return;
      }
   }
}
