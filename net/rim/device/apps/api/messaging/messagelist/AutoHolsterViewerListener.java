package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.apps.api.messaging.MessageEntryPoint;

public class AutoHolsterViewerListener implements HolsterListener, GlobalEventListener {
   private boolean _holstered;
   private AutoHolsterViewerListener$HolsterTimer _holsterTimer;
   private Application _messagingApp;
   private final MessageEntryPoint _mep = MessageEntryPoint.findEntry("mainview");
   private boolean _inOperation;
   private static final long ID = -4978371853589284601L;
   private static AutoHolsterViewerListener _instance;

   private AutoHolsterViewerListener() {
      this._holsterTimer = new AutoHolsterViewerListener$HolsterTimer(this, null);
      this._holstered = false;
      this._inOperation = false;
      _instance = this;
   }

   public static AutoHolsterViewerListener getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (AutoHolsterViewerListener)reg.getOrWaitFor(-4978371853589284601L);
            if (_instance == null) {
               _instance = new AutoHolsterViewerListener();
               reg.put(-4978371853589284601L, _instance);
            }
         }
      }

      return _instance;
   }

   public synchronized void commenceOperation(long timelimit) {
      if (this._holstered && !ApplicationManager.getApplicationManager().isSystemLocked()) {
         if (this._inOperation) {
            this._holsterTimer.resetTimer(timelimit);
         } else {
            this.commenceOperationInternal(timelimit);
         }
      }
   }

   public void setMessagingAppInstance(Application app) {
      this._messagingApp = app;
   }

   private synchronized void commenceOperationInternal(long timelimit) {
      if (!this._inOperation && !ShowMessageApp.isMessagingAppForeground()) {
         ShowMessageApp.showMessageApp();
         this._holsterTimer.startTimer(timelimit);
         this._inOperation = true;
      }
   }

   private synchronized void terminateOperationInternal() {
      if (this._inOperation) {
         this.closeMessageApp(this._messagingApp, this._mep);
         this._holsterTimer.cancelTimer();
         this._inOperation = false;
      }
   }

   private void closeMessageApp(Application messagingApp, MessageEntryPoint mep) {
      messagingApp.requestBackground();
   }

   @Override
   public synchronized void inHolster() {
      this._holstered = true;
      this._inOperation = false;
   }

   @Override
   public synchronized void outOfHolster() {
      this._holstered = false;
      if (this._inOperation) {
         this._inOperation = false;
         this._holsterTimer.cancelTimer();
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L && this._inOperation) {
         this.terminateOperationInternal();
      }
   }
}
