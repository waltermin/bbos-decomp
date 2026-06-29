package net.rim.wica.runtime.lifecycle.internal;

import java.util.Date;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.wica.runtime.lifecycle.ApplicationVersion;
import net.rim.wica.runtime.lifecycle.UpgradeTaskInfo;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.resources.RuntimeResources;

final class LifecycleDialogManager implements DialogClosedListener {
   private LifecycleServiceImpl _lifecycleService;
   private Application _application = Application.getApplication();
   private static final String DISABLED_ICON = "disabled_icon.png";
   private static final String UPGRADE_ICON = "upgrade_icon.png";
   private static final String EXPIRED_ICON = "expired_icon.png";

   LifecycleDialogManager(LifecycleServiceImpl lifecycleService) {
      this._lifecycleService = lifecycleService;
   }

   final void displayDisabledDialog(WicletImpl application) {
      String message = RuntimeResources.getString(6, application.getName());
      Action[] actions = new Action[]{new DefaultAction(RuntimeResources.getString(3))};
      this.displayDialog(application, message, RuntimeResources.getBitmapResource("disabled_icon.png"), actions, 0, null);
   }

   final void displayUninstalledDialog(WicletImpl application) {
      String message = RuntimeResources.getString(152, application.getName());
      Action[] actions = new Action[]{new DefaultAction(RuntimeResources.getString(3))};
      this.displayDialog(application, message, ThemeManager.getPredefinedBitmap(2), actions, 0, null);
   }

   final void displayUpgradeAvailableDialog(WicletImpl application, UpgradeTaskInfo task) {
      String message = RuntimeResources.getString(10, application.getName());
      Action[] actions = new Action[]{new LifecycleDialogManager$UpgradeAppAction(), new StartApplicationAction()};
      this.displayDialog(
         application,
         message,
         RuntimeResources.getBitmapResource("upgrade_icon.png"),
         actions,
         1,
         this.getUpgradeMessage(application, task.getDeploymentDescriptor())
      );
   }

   final void displayUpgradeRequiredDialog(WicletImpl application, UpgradeTaskInfo task) {
      String message = null;
      String name = application.getName();
      Action[] actions = new Action[]{new LifecycleDialogManager$UpgradeAppAction(), null};
      boolean expired = task.isExpired();
      if (expired) {
         message = RuntimeResources.getString(19, name);
         actions[1] = new DefaultAction(RuntimeResources.getString(2));
      } else {
         message = RuntimeResources.getString(11, new Object[]{name, new Date(task.getExpiryDate()).toString()});
         actions[1] = new StartApplicationAction();
      }

      this.displayDialog(
         application,
         message,
         RuntimeResources.getBitmapResource(expired ? "expired_icon.png" : "upgrade_icon.png"),
         actions,
         0,
         this.getUpgradeMessage(application, task.getDeploymentDescriptor())
      );
   }

   private final String getUpgradeMessage(WicletImpl application, DeploymentDescriptor descriptor) {
      String message = null;
      if (new ApplicationVersion(application.getVersion()).isIncompatibleWith(new ApplicationVersion(descriptor.getVersion()))) {
         message = RuntimeResources.getString(130);
      }

      return message;
   }

   final void displayRecommendedInstallDialog(UpgradeTaskInfo task) {
      DeploymentDescriptor desc = task.getDeploymentDescriptor();
      String message = RuntimeResources.getString(63, desc.getName());
      Action[] actions = new Action[]{new LifecycleDialogManager$InstallAppAction(this), new DefaultAction(RuntimeResources.getString(2))};
      this.displayDialog(task, message, RuntimeResources.getBitmapResource("upgrade_icon.png"), actions, 0, null);
   }

   final void displayUpgradingDialog(WicletImpl application) {
      String message = RuntimeResources.getString(8);
      Action[] actions = new Action[]{new DefaultAction(RuntimeResources.getString(3))};
      this.displayDialog(application, message, application.getDefaultIcon(), actions, 0, null);
   }

   private final void displayDialog(Object cookie, String message, Bitmap icon, Action[] actions, int initialChoice, String detailMessage) {
      String displayedMessage = message;
      if (detailMessage != null) {
         StringBuffer b = new StringBuffer(message);
         b.append(' ');
         b.append(detailMessage);
         displayedMessage = b.toString();
      }

      RuntimeDialog dialog = new RuntimeDialog(displayedMessage, actions, null, initialChoice, icon, 33554432);
      dialog.setCookie(cookie);
      dialog.setDialogClosedListener(this);
      this._application.invokeLater(new LifecycleDialogManager$DialogRunnable(dialog));
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (choice != -1) {
         Action action = ((RuntimeDialog)dialog).getAction(choice);
         action.invoke(dialog.getCookie());
      }
   }
}
