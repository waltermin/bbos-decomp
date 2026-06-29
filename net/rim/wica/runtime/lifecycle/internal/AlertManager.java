package net.rim.wica.runtime.lifecycle.internal;

import net.rim.device.api.notification.NotificationsEngineListener;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.wica.runtime.lifecycle.Alert;
import net.rim.wica.runtime.resources.RuntimeResources;

final class AlertManager implements NotificationsEngineListener, DialogClosedListener {
   private Application _application = Application.getApplication();
   private WicletImpl _model;
   private Dialog _dialog;

   AlertManager(WicletImpl model) {
      this._model = model;
      this.registerSource(this._model.getId(), this._model.getName());
   }

   private final void registerSource(long sourceId, String sourceName) {
      NotificationsManager.registerSource(sourceId, sourceName, 3);
      NotificationsManager.registerNotificationsEngineListener(sourceId, this);
   }

   final void deregister() {
      long sourceId = this._model.getId();
      NotificationsManager.cancelImmediateEvent(sourceId, 0, null, null);
      NotificationsManager.cancelAllDeferredEvents(sourceId, 1, null);
      NotificationsManager.deregisterNotificationsEngineListener(sourceId, this);
      NotificationsManager.deregisterSource(sourceId);
   }

   final void cancelAlerts() {
      NotificationsManager.cancelImmediateEvent(this._model.getId(), 0, null, null);
      this._model.setAlerts(false, false);
   }

   final void handleAlert(Alert alert) {
      if (DeviceInfo.isInHolster() || !this._model.isRunning() || !this._model.getApplication().isForeground()) {
         NotificationsManager.triggerImmediateEvent(this._model.getId(), 0, null, null);
      }

      if (!this._model.isRunning() || !this._model.getApplication().isForeground()) {
         this._model.setAlerts(true, alert.hasRibbonIndicator());
         if (alert.hasDialog()) {
            NotificationsManager.negotiateDeferredEvent(this._model.getId(), 0, alert, 500, 1, null);
         }
      }
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      Alert alert = (Alert)dialog.getCookie();
      long sourceId = this._model.getId();
      NotificationsManager.cancelDeferredEvent(sourceId, 0, alert, 1, null);
      NotificationsManager.cancelImmediateEvent(sourceId, 0, null, null);
      if (choice != -1) {
         Action action = ((RuntimeDialog)dialog).getAction(choice);
         action.invoke(this._model);
      }

      this._dialog = null;
   }

   @Override
   public final void deferredEventWasSuperseded(long sourceID, long eventID, Object eventReferenceObject, Object context) {
      this._application.invokeLater(new AlertManager$1(this));
   }

   @Override
   public final void notificationsEngineStateChanged(int stateInt, long sourceID, long eventID, Object eventReferenceObject, Object context) {
   }

   @Override
   public final void proceedWithDeferredEvent(long sourceId, long eventId, Object eventReferenceObject, Object context) {
      Alert alert = (Alert)eventReferenceObject;
      Action[] actions = new Action[]{new StartApplicationAction(), new DefaultAction(RuntimeResources.getString(2))};
      String message = RuntimeResources.getString(22, new Object[]{this._model.getName(), alert.getText()});
      this._dialog = new RuntimeDialog(message, actions, null, 0, this._model.getDefaultIcon(), 33554432);
      this._dialog.setDialogClosedListener(this);
      this._dialog.setCookie(alert);
      this._application.invokeLater(new AlertManager$2(this));
   }
}
