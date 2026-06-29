package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

final class InternalLBSNetworkScreen extends MainScreen {
   private int _counter;
   private long _pingAvg;
   private int _procID;
   private String _pingText;
   private BasicEditField _LBSServer;
   private BasicEditField _pingValues;

   InternalLBSNetworkScreen() {
      super(65536);
      this.add(this._LBSServer = (BasicEditField)(new Object("LBS Server: ", LBSOptions.getURL(-7064416726417485961L), 255, 117440512)));
      this.add((Field)(new Object()));
      this.add(this._pingValues = (BasicEditField)(new Object(9007199254740992L)));
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(MenuItem.getPrefab(14));
      if (this._procID == 0) {
         menu.add(new InternalLBSNetworkScreen$1(this, "Ping Server", 1, 0));
      } else {
         menu.add(new InternalLBSNetworkScreen$2(this, "Stop Ping", 1, 0));
      }
   }

   private final void pingServer() {
      this._procID = Application.getApplication().invokeLater(new InternalLBSNetworkScreen$3(this), 10, false);
   }

   private final void donePing(long ping) {
      this._counter++;
      Application.getApplication().invokeAndWait(new InternalLBSNetworkScreen$4(this, ping));
      if (this._counter > 2) {
         Application.getApplication().cancelInvokeLater(this._procID);
         this._procID = 0;
      } else {
         if (this._procID != 0) {
            this.pingServer();
         }
      }
   }

   @Override
   public final boolean onClose() {
      this.close();
      return true;
   }

   static final String access$384(InternalLBSNetworkScreen x0, Object x1) {
      return x0._pingText = ((StringBuffer)(new Object())).append(x0._pingText).append(x1).toString();
   }
}
