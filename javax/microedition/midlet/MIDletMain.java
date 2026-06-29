package javax.microedition.midlet;

import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.internal.content.ContentHandlerRegistrationHelper;
import net.rim.device.internal.io.PushRegistryHelper;
import net.rim.device.internal.lcdui.Lcdui;
import net.rim.device.internal.lcdui.MIDletInterface;
import net.rim.device.internal.resource.CommonResource;
import net.rim.device.internal.ui.MIDletApplication;
import net.rim.device.resources.Resource;
import net.rim.vm.Process;

class MIDletMain extends MIDletApplication implements MIDletInterface, CommonResource {
   private MIDlet _midlet;
   private String _moduleClassName;
   private boolean _active;
   private boolean _foregroundable = false;
   private Hashtable _workerThreadMap = new Hashtable();
   private Vector _alarmThreadCache = new Vector();
   private static final long MIDLETSUITEMAP_ID;
   private static ResourceBundle _resources = ResourceBundle.getBundle(-6812884907508133143L, "net.rim.device.internal.resource.Common");
   private static final long MIDLET_ID_MASK;
   private static final Object dummy = new Long(8205425079589422083L);

   private static MIDletMain getInstance(String midletName, boolean create) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         long id = 6179825861491949568L | midletName.hashCode() & 4294967295L;
         MIDletMain m = null;
         Object o = ar.get(id);
         if (o == null && create) {
            m = new MIDletMain(midletName);
            ar.put(id, m);
         } else if (create && o.equals(dummy)) {
            m = new MIDletMain(midletName);
            ar.replace(id, m);
         } else if (o != null && !o.equals(dummy)) {
            m = (MIDletMain)o;
         }

         return m;
      }
   }

   private static void removeInstance(String midletName, MIDletMain instance) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      long id = 6179825861491949568L | midletName.hashCode() & 4294967295L;
      synchronized (ar) {
         Object o = ar.get(id);
         if (o == instance) {
            ar.replace(id, dummy);
         }
      }
   }

   public static void main(String[] args) {
      List.SELECT_COMMAND.getPriority();
      if (args != null && args.length > 0) {
         if (args[0].equals(PushRegistryHelper.APPLICATION_DESCRIPTOR_ARG_PUSH_REGISTRY_WORK)) {
            initializePushRegistry(args[1]);
            return;
         }

         if (args[0].equals(PushRegistryHelper.APPLICATION_DESCRIPTOR_ARG_DYNAMIC_PUSH_REGISTRY_WORK)) {
            getInstance(args[1], true).addPushRegistry(args[1], args[2]);
            return;
         }

         if (args[0].equals(PushRegistryHelper.APPLICATION_DESCRIPTOR_ARG_SHUTDOWN_PUSH_REGISTRY_WORK)) {
            MIDletMain mm = getInstance(args[1], false);
            if (mm != null) {
               mm.removePushRegistry(args[1], args[2]);
               return;
            }

            throw new RuntimeException("MIDlet could not be started");
         }

         if (args[0].equals("dostaticcontenthandlerregistration")) {
            int moduleHandle = Integer.parseInt(args[1]);
            ContentHandlerRegistrationHelper.getInstance().registerContentHandlers(moduleHandle);
            return;
         }

         MIDletMain app = getInstance(args[0], false);
         if (app == null) {
            app = new MIDletMain(args[0]);
         }

         app._foregroundable = true;
         ApplicationManager.getApplicationManager().requestForeground(app.getProcessId());
         if (!app.isHandlingEvents()) {
            app.enterEventDispatcher();
         }
      }
   }

   private MIDletMain(String moduleClassName) {
      this.enableKeyUpEvents(true);
      this._moduleClassName = moduleClassName;
   }

   @Override
   public void exit() {
      if (!this.keepAliveProcess()) {
         removeInstance(this._moduleClassName, this);
         Process.currentProcess().destroy();
      } else {
         this.pokeWorkerThreads();
         this.requestBackground();
         this._foregroundable = false;
         this._active = false;
         this._midlet = null;
      }
   }

   @Override
   public void registerAlarm(Runnable r) {
      this._alarmThreadCache.addElement(new WeakReference(r));
   }

   private boolean keepAliveProcess() {
      if (!this._workerThreadMap.isEmpty()) {
         return true;
      }

      for (int i = this._alarmThreadCache.size() - 1; i >= 0; i--) {
         WeakReference wr = (WeakReference)this._alarmThreadCache.elementAt(i);
         if (wr != null) {
            if (wr.get() != null) {
               return true;
            }

            this._alarmThreadCache.removeElement(wr);
         }
      }

      return false;
   }

   @Override
   public boolean acceptsForeground() {
      return this._foregroundable;
   }

   @Override
   public void setForegroundable(boolean foregroundable) {
      this._foregroundable = foregroundable;
   }

   @Override
   public void bringToForeground() {
      this._foregroundable = true;
      ApplicationManager.getApplicationManager().requestForeground(this.getProcessId());
   }

   private static void initializePushRegistry(String midletname) {
      StringBuffer sb = new StringBuffer(PushRegistryHelper.MIDLET_PUSH_PROPERTY_NAME_PREFIX);
      sb.append('n');
      int charindex = sb.length() - 1;
      int i = 1;

      while (true) {
         sb.setCharAt(charindex, (char)(i + 48));
         String s = PushRegistryHelper.getMidletProperty(sb.toString());
         if (s == null) {
            return;
         }

         String[] values = PushRegistryHelper.getPushPropertyValues(s);
         String connectionUrl = values[0];
         String midletClassName = values[1];
         String filter = values[2];
         if (midletname.equals(midletClassName)) {
            MIDletMain m = getInstance(midletname, true);

            try {
               PushRegistry.registerConnection(connectionUrl, midletClassName, filter);
            } catch (Exception e) {
               removeInstance(midletname, m);
               throw new RuntimeException(e.toString());
            }
         }

         i++;
      }
   }

   @Override
   public void addPushRegistry(String midletname, String connection) {
      PushRegistryHelper prh = PushRegistryHelper.getInstance();
      prh._weakreferencemap.put(connection, new WeakReference(this));
      Thread t = new MIDletMain$MIDletPushRegistryWorkerThread(this, connection);
      this._workerThreadMap.put(connection, t);
      if (this.isHandlingEvents()) {
         ((MIDletMain$MIDletPushRegistryWorkerThread)t).pause();
      }

      t.start();
      if (!this.isHandlingEvents()) {
         this._foregroundable = false;
         this.enterEventDispatcher();
      }
   }

   @Override
   public void removePushRegistry(String midletname, String connection) {
      MIDletMain$MIDletPushRegistryWorkerThread t = (MIDletMain$MIDletPushRegistryWorkerThread)this._workerThreadMap.remove(connection);
      t.shutdown();
      PushRegistryHelper prh = PushRegistryHelper.getInstance();
      prh._weakreferencemap.remove(connection);
   }

   @Override
   public void shutdownWorkerThread(String connectionString) {
      PushRegistryHelper prh = PushRegistryHelper.getInstance();
      prh._connectionMap.remove(connectionString);
      prh._filterMap.remove(connectionString);
      MIDletMain$MIDletPushRegistryWorkerThread workerThread = (MIDletMain$MIDletPushRegistryWorkerThread)this._workerThreadMap.get(connectionString);
      if (workerThread != null) {
         workerThread.shutdown();
      }
   }

   @Override
   public void activate() {
      try {
         if (this._midlet == null) {
            MIDlet._instantiationAllowed = true;
            this._midlet = (MIDlet)Resource.getResourceClass().instantiateMIDlet(this._moduleClassName);
            MIDlet._instantiationAllowed = false;
            this._midlet.setMain(this);
         }

         boolean displayNeedsPainting = true;
         this._midlet.startApp();
         if (!this._active) {
            Display display = Display.getDisplay(this._midlet);
            Displayable currentDisplayable = display.getCurrent();
            if (currentDisplayable != null && currentDisplayable instanceof Canvas) {
               display.setCurrent(currentDisplayable);
               displayNeedsPainting = false;
            }
         }

         this._active = true;
         if (displayNeedsPainting) {
            this.repaint();
            return;
         }
      } catch (MIDletStateChangeException ex) {
         this.requestBackground();
      }
   }

   @Override
   public void deactivate() {
      this.pokeWorkerThreads();
      if (this._active) {
         this._midlet.pauseApp();
         this._active = false;
      }
   }

   @Override
   public void updateScreen() {
      Lcdui.runCallback();
      synchronized (this.getAppEventLock()) {
         this.doPainting();
      }

      Lcdui.runPaintCallback();
      this.updateDisplay();
   }

   @Override
   protected void dispatchInvokeLater(Runnable runnable, Object notifier, int data0) {
      if (data0 == 1) {
         Lcdui.setInvokeLaterCallback(runnable, notifier);
      } else {
         super.dispatchInvokeLater(runnable, notifier, data0);
      }
   }

   @Override
   public void destroyApp(boolean unconditional) {
      this._midlet.destroyApp(unconditional);
      this.pokeWorkerThreads();
   }

   private void pokeWorkerThreads() {
      synchronized (this) {
         Enumeration e = this._workerThreadMap.elements();

         while (e.hasMoreElements()) {
            MIDletMain$MIDletPushRegistryWorkerThread mprwt = (MIDletMain$MIDletPushRegistryWorkerThread)e.nextElement();
            mprwt.resume();
         }
      }
   }
}
