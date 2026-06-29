package net.rim.device.internal.EScreens;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public final class EScreenSessionManager extends MainScreen implements ListFieldCallback {
   private Vector _sessions = (Vector)(new Object());
   private Hashtable _registeredTypes;
   private ListField _listField;
   private StringBuffer _strBuf;
   private static final String MY_TITLE = "Session Manager";
   private static final int MENU_REFRESH = 1;
   private static final int MENU_KILL_SESSION = 2;
   private static final int MENU_DELETE_SESSION = 3;
   private static final int MENU_NEW_SESSION = 4;
   private static final int MENU_DETAILS = 5;

   public final void registerSession(String name, String className) {
      try {
         if (Class.forName(className) != null) {
            this._registeredTypes.put(name, className);
            return;
         }
      } finally {
         return;
      }
   }

   public final int getNumSessions() {
      return this._sessions.size();
   }

   public final void removeSession(int index) {
      EScreenSession session = (EScreenSession)this._sessions.elementAt(index);
      if (session.getState() == 1) {
         session.stop();
      }

      this._sessions.removeElementAt(index);
   }

   public final void deleteAllSessions() {
      for (int i = this._sessions.size() - 1; i >= 0; i--) {
         EScreenSession s = (EScreenSession)this._sessions.elementAt(i);
         if (s.getState() == 1) {
            s.stop();
         }

         this._sessions.removeElementAt(i);
      }
   }

   public final void summaryChanged(int sessionIndex) {
      if (sessionIndex < this._listField.getSize()) {
         this._listField.invalidate(sessionIndex);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return this.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      EScreenSession s = (EScreenSession)this._sessions.elementAt(index);
      this._strBuf.setLength(0);
      s.appendSummary(this._strBuf);
      graphics.drawText(this._strBuf, 0, this._strBuf.length(), 0, y, 0, width);
   }

   public EScreenSessionManager(Font font) {
      this.setFont(font);
      this._strBuf = (StringBuffer)(new Object(32));
      this._sessions = (Vector)(new Object());
      this._registeredTypes = (Hashtable)(new Object(8));
      this.registerSession("Ping", "net.rim.device.internal.EScreens.sessions.PingSession");
      this.registerSession("UDP Blaster", "net.rim.device.internal.EScreens.sessions.UDPBlasterSession");
      this.registerSession("TraceRoute", "net.rim.device.internal.EScreens.sessions.TraceRouteSession");
      this._listField = (ListField)(new Object());
      this._listField.setCallback(this);
      this.add(this._listField);
      this.setTitle("Session Manager");
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new EScreenSessionManager$MyMenuItem(this, 1, "Refresh", null));
      if (this._registeredTypes.size() != 0) {
         Enumeration names = this._registeredTypes.keys();

         while (names.hasMoreElements()) {
            String name = (String)names.nextElement();
            menu.add(
               new EScreenSessionManager$MyMenuItem(
                  this, 4, ((StringBuffer)(new Object("New "))).append(name).append(" Session").toString(), this._registeredTypes.get(name)
               )
            );
         }
      }

      int index = this._listField.getSelectedIndex();
      if (index >= 0) {
         EScreenSession session = (EScreenSession)this._sessions.elementAt(index);
         if (session.getState() == 1) {
            menu.add(new EScreenSessionManager$MyMenuItem(this, 2, "Stop Session", session));
         }

         menu.add(new EScreenSessionManager$MyMenuItem(this, 3, "Delete Session", new Object(index)));
         menu.add(new EScreenSessionManager$MyMenuItem(this, 5, "Details", new Object(index)));
      }

      this.populateMenuFromRepository(menu);
   }

   private final void populateMenuFromRepository(Menu menu) {
      EScreenExtendedMenuRepository rep = EScreenExtendedMenuRepository.getInstance();
      if (rep != null) {
         MenuItem[] items = rep.getExtendedMenu();
         if (items != null) {
            for (int i = items.length - 1; i >= 0; i--) {
               if (items[i] != null) {
                  menu.add(items[i]);
               }
            }
         }
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      super.onUiEngineAttached(attached);
      if (attached) {
         this._listField.setSize(this._sessions.size());
      }
   }

   private final int getMenuOrdinal(int id) {
      switch (id) {
         case 0:
            return 0;
         case 1:
         default:
            return 65536;
         case 2:
            return 196608;
         case 3:
            return 262144;
         case 4:
            return 131072;
         case 5:
            return 327680;
      }
   }

   private final int getMenuPriority(int id) {
      return 0;
   }
}
