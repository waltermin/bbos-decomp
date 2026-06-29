package javax.microedition.content;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.api.system.ApplicationProcess;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleGroupManager;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.content.ContentHandlerRenderingManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.CodeStore;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.vm.Array;
import net.rim.vm.Message;
import net.rim.vm.Process;
import net.rim.vm.TraceBack;

class RegistryImpl extends Registry {
   private String _classname;
   private String _authority;
   private ResponseListener _listener;
   private InvocationQueue _queue;
   private ApplicationDescriptor _application;
   private int _numStaleInvocations;
   private static final long REGISTRY_KEY = 1885653817802432006L;
   private static final long TRANSACTIONS_KEY = -1460427697355923683L;
   private static final long REGISTRY_IMPLS_KEY = -984470147161509581L;
   private static final long PERSISTENT_KEY = 9146446116437442900L;
   private static final int REGISTRY_INDEX = 0;
   private static final int BY_TYPE_INDEX = 1;
   private static final int BY_ACTION_INDEX = 2;
   private static final int BY_SUFFIX_INDEX = 3;
   private static final int BY_ID_INDEX = 4;
   private static final int REGISTRY_ARRAY_SIZE = 5;
   private static Hashtable _registry;
   private static PersistentObject _persist;
   private static Hashtable _handlersByType;
   private static Hashtable _handlersByAction;
   private static Hashtable _handlersBySuffix;
   private static Hashtable _handlersByID;
   private static Transaction[] _transactions;
   private static Hashtable _registryImpls;
   private static Hashtable _persistedHandlers;

   private RegistryImpl(String classname) {
      this._classname = classname;
      this._queue = new InvocationQueue();
   }

   static RegistryImpl getRegistryImpl() {
      return new RegistryImpl(null);
   }

   public static Registry getRegistry(String classname) {
      assertPermission();

      int moduleHandle;
      try {
         moduleHandle = verifyClassname(classname);
      } catch (ClassNotFoundException cnfe) {
         throw new IllegalArgumentException("classname is not implemented");
      }

      verifyRegistered(moduleHandle, classname);
      RegistryImpl ri = (RegistryImpl)_registryImpls.get(classname);
      if (ri == null) {
         ri = new RegistryImpl(classname);
         _registryImpls.put(classname, ri);
      }

      int callingModule = TraceBack.getCallingModule(2);
      ApplicationDescriptor callingApplication = ApplicationDescriptor.currentApplicationDescriptor();
      ri.setAuthority(getAuthority(callingModule));
      ri.setApplication(callingApplication);
      return ri;
   }

   public static ContentHandlerServer getServer(String classname) {
      assertPermission();

      try {
         verifyClassname(classname);
      } catch (IllegalArgumentException iae) {
         throw new ContentHandlerException(iae.getMessage(), 1);
      } catch (ClassNotFoundException cnfe) {
         throw new ContentHandlerException(cnfe.getMessage(), 1);
      }

      ContentHandlerServer server = (ContentHandlerServer)_registry.get(classname);
      if (server == null) {
         throw new ContentHandlerException("No registered handler for " + classname, 1);
      } else {
         return server;
      }
   }

   @Override
   public ContentHandlerServer register(
      String classname, String[] types, String[] suffixes, String[] actions, ActionNameMap[] actionnames, String ID, String[] accessAllowed
   ) {
      assertPermission();
      int moduleHandle = verifyClassname(classname);
      MIDletSecurity.checkPermission(37);
      ContentHandlerServerImpl server = new ContentHandlerServerImpl();
      server.init(types, suffixes, actions, actionnames, ID == null ? getOrCreateID(moduleHandle, classname, true) : ID, accessAllowed);
      verifyID(server.getID(), classname, false);
      ApplicationDescriptor appDescriptor = ContentHandlerUtilities.findApplicationDescriptor(moduleHandle, classname);
      if (appDescriptor == null) {
         throw new IllegalArgumentException("classname not present in current application");
      }

      server.setApplicationDescriptor(appDescriptor);
      server.setAppName(getAppName(moduleHandle, classname, appDescriptor));
      server.setVersion(appDescriptor.getVersion());
      server.setAuthority(getAuthority(moduleHandle));
      server.setClassname(classname);
      server.setModuleHandle(moduleHandle);
      server.setDynamic(true);
      this.registerAndPersistHandler(classname, server);
      InvocationCleanupManager.getInstance().addContentHandlerModule(moduleHandle, classname, true);
      return server;
   }

   void registerInternal(
      String classname,
      String[] types,
      String[] suffixes,
      String[] actions,
      ActionNameMap[] actionnames,
      String ID,
      String[] accessAllowed,
      ApplicationDescriptor application,
      int moduleHandle
   ) {
      ContentHandlerServerImpl server = new ContentHandlerServerImpl();
      server.init(types, suffixes, actions, actionnames, ID, accessAllowed);
      verifyID(server.getID(), classname, false);
      server.setApplicationDescriptor(application);
      server.setAppName(getAppName(moduleHandle, classname, application));
      server.setVersion(application.getVersion());
      server.setAuthority(getAuthority(moduleHandle));
      server.setClassname(classname);
      server.setModuleHandle(moduleHandle);
      server.setDynamic(false);
      this.registerAndPersistHandler(classname, server);
      InvocationCleanupManager.getInstance().addContentHandlerModule(moduleHandle, classname, false);
   }

   private void registerAndPersistHandler(String classname, ContentHandlerServerImpl server) {
      registerHandler(classname, server);
      DataBuffer persistableHandler = ContentHandlerConverter.convert(server);
      if (persistableHandler != null) {
         _persistedHandlers.put(classname, persistableHandler);
         _persist.commit();
      }
   }

   private static void registerHandler(String classname, ContentHandlerServer server) {
      _registry.put(classname, server);
      int numTypes = server.getTypeCount();

      for (int i = 0; i < numTypes; i++) {
         String type = StringUtilities.toLowerCase(server.getType(i), 1701707776);
         Vector handlers = null;
         if (_handlersByType.containsKey(type)) {
            handlers = (Vector)_handlersByType.get(type);
         } else {
            handlers = new Vector();
            _handlersByType.put(type, handlers);
         }

         if (handlers != null) {
            addOrReplaceHandler(handlers, server);
         }

         if (!server.getID().equals("net.rim.bb.mediacontenthandler")) {
            ContentHandlerRenderingManager.getInstance().register(type);
         }
      }

      int numActions = server.getActionCount();

      for (int i = 0; i < numActions; i++) {
         String action = server.getAction(i);
         Vector handlers = null;
         if (_handlersByAction.containsKey(action)) {
            handlers = (Vector)_handlersByAction.get(action);
         } else {
            handlers = new Vector();
            _handlersByAction.put(action, handlers);
         }

         if (handlers != null) {
            addOrReplaceHandler(handlers, server);
         }
      }

      int numSuffixes = server.getSuffixCount();

      for (int i = 0; i < numSuffixes; i++) {
         String suffix = StringUtilities.toLowerCase(server.getSuffix(i), 1701707776);
         Vector handlers = null;
         if (_handlersBySuffix.containsKey(suffix)) {
            handlers = (Vector)_handlersBySuffix.get(suffix);
         } else {
            handlers = new Vector();
            _handlersBySuffix.put(suffix, handlers);
         }

         if (handlers != null) {
            addOrReplaceHandler(handlers, server);
         }
      }

      String ID = server.getID();
      _handlersByID.put(ID, server);
   }

   private static void addOrReplaceHandler(Vector handlers, ContentHandlerServer server) {
      int numHandlers = handlers.size();

      for (int i = 0; i < numHandlers; i++) {
         ContentHandlerServer chs = (ContentHandlerServer)handlers.elementAt(i);
         if (chs.equals(server)) {
            return;
         }

         if (chs.getID().equals(server.getID())) {
            handlers.setElementAt(server, i);
            return;
         }
      }

      handlers.addElement(server);
   }

   @Override
   public boolean unregister(String classname) {
      assertPermission();

      try {
         verifyClassname(classname);
      } catch (IllegalArgumentException iae) {
         return false;
      } catch (ClassNotFoundException cnfe) {
         return false;
      }

      return this.unregisterInternal(classname);
   }

   boolean unregisterInternal(String classname) {
      ContentHandlerServerImpl server = null;
      if (_registry.containsKey(classname)) {
         server = (ContentHandlerServerImpl)_registry.remove(classname);
         this.removeHandler(_handlersByType, server);
         this.removeHandler(_handlersByAction, server);
         this.removeHandler(_handlersBySuffix, server);
         _handlersByID.remove(server.getID());
         _persistedHandlers.remove(classname);
         _persist.commit();
         InvocationCleanupManager.getInstance().removeContentHandler(server.getModuleHandle(), classname);
         return true;
      } else {
         return false;
      }
   }

   private void removeHandler(Hashtable hash, ContentHandler handler) {
      Enumeration e = hash.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         Vector handlers = (Vector)hash.get(key);
         handlers.removeElement(handler);
         if (handlers.size() == 0) {
            hash.remove(key);
         }
      }
   }

   @Override
   public String[] getTypes() {
      return this.retrieveAccessibleKeys(_handlersByType);
   }

   @Override
   public String[] getIDs() {
      String[] ids = new String[0];
      Enumeration e = _handlersByID.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         if (((ContentHandlerServer)_handlersByID.get(key)).isAccessAllowed(this.getID())) {
            Array.resize(ids, ids.length + 1);
            ids[ids.length - 1] = key;
         }
      }

      return ids;
   }

   @Override
   public String[] getActions() {
      return this.retrieveAccessibleKeys(_handlersByAction);
   }

   @Override
   public String[] getSuffixes() {
      return this.retrieveAccessibleKeys(_handlersBySuffix);
   }

   private String[] retrieveAccessibleKeys(Hashtable hash) {
      String[] results = new String[0];
      Enumeration e = hash.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         Vector handlers = (Vector)hash.get(key);
         int numHandlers = handlers.size();

         for (int i = 0; i < numHandlers; i++) {
            ContentHandlerServer server = (ContentHandlerServer)handlers.elementAt(i);
            if (server.isAccessAllowed(this.getID())) {
               Array.resize(results, results.length + 1);
               results[results.length - 1] = key;
               break;
            }
         }
      }

      return results;
   }

   @Override
   public ContentHandler[] forType(String type) {
      return this.retrieveAccessibleHandlers(_handlersByType, StringUtilities.toLowerCase(type, 1701707776));
   }

   @Override
   public ContentHandler[] forAction(String action) {
      return this.retrieveAccessibleHandlers(_handlersByAction, action);
   }

   @Override
   public ContentHandler[] forSuffix(String suffix) {
      return this.retrieveAccessibleHandlers(_handlersBySuffix, StringUtilities.toLowerCase(suffix, 1701707776));
   }

   private ContentHandler[] retrieveAccessibleHandlers(Hashtable hash, String key) {
      ContentHandler[] results = new ContentHandler[0];
      Vector handlers = (Vector)hash.get(key);
      if (handlers != null) {
         int numHandlers = handlers.size();

         for (int i = 0; i < numHandlers; i++) {
            ContentHandlerServerImpl server = (ContentHandlerServerImpl)handlers.elementAt(i);
            if (server.isAccessAllowed(this.getID())) {
               Array.resize(results, results.length + 1);
               results[results.length - 1] = new ContentHandlerImpl(server);
            }
         }
      }

      return results;
   }

   @Override
   public ContentHandler forID(String ID, boolean exact) {
      if (ID == null) {
         throw new NullPointerException("null ID");
      }

      if (exact) {
         ContentHandlerServerImpl server = (ContentHandlerServerImpl)_handlersByID.get(ID);
         return server != null && server.isAccessAllowed(this.getID()) ? new ContentHandlerImpl(server) : null;
      }

      Enumeration e = _handlersByID.keys();

      while (e.hasMoreElements()) {
         String key = (String)e.nextElement();
         if (ID.startsWith(key)) {
            ContentHandlerServerImpl server = (ContentHandlerServerImpl)_handlersByID.get(key);
            if (server.isAccessAllowed(this.getID())) {
               return new ContentHandlerImpl(server);
            }
         }
      }

      return null;
   }

   @Override
   public ContentHandler[] findHandler(Invocation invocation) {
      String action = invocation.getAction();
      String ID = invocation.getID();
      if (ID != null) {
         ContentHandler handler = this.forID(ID, false);
         if (handler != null) {
            return action == null ? new ContentHandler[]{handler} : this.filterByAction(new ContentHandler[]{handler}, action);
         } else {
            throw new ContentHandlerException("No ContentHandler found for Invocation ID", 1);
         }
      } else {
         ContentHandler[] handlers = new ContentHandler[0];
         String type = invocation.getType();
         if (type == null) {
            try {
               type = invocation.findType();
            } catch (ContentHandlerException var10) {
            }
         }

         if (type != null) {
            handlers = this.forType(type);
            if (handlers.length > 0) {
               return action == null ? handlers : this.filterByAction(handlers, action);
            } else {
               throw new ContentHandlerException("No ContentHandlers found for type " + type, 1);
            }
         } else {
            String url = invocation.getURL();
            if (url != null) {
               int length = url.length();

               for (int i = length - 1; i >= 0; i--) {
                  ContentHandler[] newMatches = this.forSuffix(url.substring(i));
                  if (newMatches.length > 0) {
                     handlers = newMatches;
                  }
               }

               if (handlers.length <= 0) {
                  throw new ContentHandlerException("No ContentHandlers with matching suffix", 1);
               } else {
                  return action == null ? handlers : this.filterByAction(handlers, action);
               }
            } else if (action != null) {
               handlers = this.forAction(action);
               if (handlers.length > 0) {
                  return handlers;
               } else {
                  throw new ContentHandlerException("No ContentHandlers for action " + action, 1);
               }
            } else {
               throw new IllegalArgumentException("No ContentHandlers found");
            }
         }
      }
   }

   private ContentHandler[] filterByAction(ContentHandler[] candidateHandlers, String action) {
      ContentHandler[] handlers = new ContentHandler[0];

      for (int i = 0; i < candidateHandlers.length; i++) {
         if (candidateHandlers[i].hasAction(action)) {
            Arrays.add(handlers, candidateHandlers[i]);
         }
      }

      if (handlers.length == 0) {
         throw new ContentHandlerException("No ContentHandlers found", 1);
      } else {
         return handlers;
      }
   }

   @Override
   public boolean invoke(Invocation invocation) {
      return this.invoke(invocation, null);
   }

   @Override
   public boolean invoke(Invocation invocation, Invocation previous) {
      assertPermission();
      if (previous != null) {
         if (previous.getStatus() != 2) {
            throw new IllegalStateException("previous must have state ACTIVE");
         }

         if (!previous.getResponseRequired()) {
            throw new IllegalArgumentException("previous.getResponseRequired returned false");
         }
      }

      if (invocation.getStatus() != 1) {
         throw new IllegalStateException("invocation must have state INIT");
      }

      invocation.populateStackAndDescriptor();
      ContentHandlerImpl handler = this.findAppropriateHandler(this.findHandler(invocation));
      Invocation newInvocation = new Invocation(invocation);
      newInvocation.setStatus(2);
      newInvocation.setInvokerInfo(this._authority, this.getID(), this.getAppName());
      invocation.setStatus(3);
      if (previous != null) {
         previous.setStatus(4);
         invocation.setPrevious(previous);
      }

      if (previous == null) {
         Arrays.add(_transactions, new Transaction(newInvocation, this));
      } else {
         for (int i = 0; i < _transactions.length; i++) {
            if (_transactions[i].getActiveInvocation() == previous) {
               _transactions[i].append(newInvocation, this);
            }
         }

         InvocationCleanupManager.getInstance().removeActiveInvocation(Process.currentProcess().getProcessId(), previous);
      }

      ((ContentHandlerServerImpl)_handlersByID.get(handler.getID())).start(newInvocation);
      return false;
   }

   @Override
   public boolean reinvoke(Invocation invocation) {
      assertPermission();
      if (invocation.getStatus() != 2) {
         throw new IllegalStateException("invocation must be ACTIVE");
      }

      invocation.populateStackAndDescriptor();
      ContentHandlerImpl handler = this.findAppropriateHandler(this.findHandler(invocation));
      Invocation newInvocation = new Invocation(invocation);
      newInvocation.setStatus(2);
      newInvocation.setInvokerInfo(this._authority, this.getID(), this.getAppName());
      invocation.setStatus(5);
      InvocationCleanupManager.getInstance().removeActiveInvocation(Process.currentProcess().getProcessId(), invocation);

      for (int i = 0; i < _transactions.length; i++) {
         if (_transactions[i].getActiveInvocation() == invocation) {
            _transactions[i].replaceActive(newInvocation);
         }
      }

      ((ContentHandlerServerImpl)_handlersByID.get(handler.getID())).start(newInvocation);
      return false;
   }

   private ContentHandlerImpl findAppropriateHandler(ContentHandler[] handlers) {
      int lastDot = 0;
      int dotIndex = -1;

      while (handlers.length > 1) {
         ContentHandler[] newHandlers = new ContentHandler[0];
         dotIndex = this._classname.indexOf(46, lastDot);
         if (dotIndex == -1) {
            break;
         }

         lastDot = dotIndex;

         for (int i = 0; i < handlers.length; i++) {
            ContentHandlerImpl curr = (ContentHandlerImpl)handlers[i];
            if (curr.getClassname().startsWith(this._classname.substring(0, dotIndex))) {
               Arrays.add(newHandlers, curr);
            }
         }

         if (newHandlers.length == 0) {
            break;
         }

         handlers = newHandlers;
      }

      return (ContentHandlerImpl)handlers[0];
   }

   @Override
   public Invocation getResponse(boolean wait) {
      assertPermission();
      synchronized (this._queue) {
         Invocation next = this._queue.nextInvocation();
         if (next == null && wait) {
            try {
               this._queue.wait();
            } catch (InterruptedException var6) {
            }

            next = this._queue.nextInvocation();
         }

         if (next != null) {
            this.checkTransaction(next);
            InvocationCleanupManager.getInstance().responseRetreived(Process.currentProcess().getProcessId());
         }

         return next;
      }
   }

   @Override
   public void cancelGetResponse() {
      synchronized (this._queue) {
         this._queue.notifyAll();
      }
   }

   void addResponse(Invocation invocation) {
      this._queue.addInvocation(invocation);
      this.respond();
   }

   void respond() {
      int pid = this.wakeAppAndNotifyListener(true);
      if (pid == -1) {
         this._queue.removeAllElements();
      } else {
         boolean firstRun = InvocationCleanupManager.getInstance().responseHandlerStarted(pid, this);
         if (firstRun) {
            this._numStaleInvocations = this._queue.size();
         }

         synchronized (this._queue) {
            this._queue.notifyAll();
         }
      }
   }

   void removeOldInvocations() {
      for (int i = 0; i < this._numStaleInvocations; i++) {
         this._queue.removeElementAt(0);
      }
   }

   int getQueueSize() {
      return this._queue.size();
   }

   @Override
   public void setListener(ResponseListener listener) {
      assertPermission();
      this._listener = listener;
      synchronized (this._queue) {
         if (this._queue.size() > 0) {
            this.wakeAppAndNotifyListener(false);
         }
      }
   }

   private int wakeAppAndNotifyListener(boolean grabForeground) {
      int pid = -1;
      ApplicationManager am = ApplicationManager.getApplicationManager();

      try {
         pid = am.runApplication(this._application, grabForeground);
      } catch (ApplicationManagerException var8) {
      } finally {
         if (pid == -1) {
            return -1;
         }
      }

      if (this._listener != null) {
         Message invokeLaterMessage = new Message(0, 2, new RegistryImpl$1(this), null);
         ((ApplicationManagerInternal)am).postMessage(pid, invokeLaterMessage);
      }

      return pid;
   }

   @Override
   public String getID() {
      int moduleHandle = TraceBack.getCallingModule(2);
      return getOrCreateID(moduleHandle, this._classname, false);
   }

   static void invocationFinished(Invocation invocation, int status) {
      invocation.setStatus(status);

      for (int i = 0; i < _transactions.length; i++) {
         if (_transactions[i].getActiveInvocation() == invocation) {
            if (invocation.getResponseRequired()) {
               Invocation original = invocation.getOriginal();
               original.setURL(invocation.getURL());
               original.setType(invocation.getType());
               original.setAction(invocation.getAction());
               original.setArgs(invocation.getArgs());
               original.setData(invocation.getData());
               original.setStatus(status);
               RegistryImpl ri = (RegistryImpl)_transactions[i].get(invocation);
               ri.addResponse(original);
               return;
            }

            removeTransactionElement(_transactions[i], invocation);
            return;
         }
      }
   }

   static void removeTransactionElement(Transaction t, Invocation i) {
      t.remove(i);
      if (t.size() == 0) {
         Arrays.remove(_transactions, t);
      }
   }

   private void checkTransaction(Invocation invocation) {
      for (int i = 0; i < _transactions.length; i++) {
         Transaction t = _transactions[i];
         if (t.getActiveInvocation().getOriginal() == invocation) {
            removeTransactionElement(t, t.getActiveInvocation());
            Invocation current = t.getActiveInvocation();
            if (current != null) {
               current.setStatus(2);
               InvocationCleanupManager.getInstance().addActiveInvocation(Process.currentProcess().getProcessId(), current);
               return;
            }
            break;
         }
      }
   }

   private void setApplication(ApplicationDescriptor application) {
      this._application = application;
   }

   private void setAuthority(String authority) {
      this._authority = authority;
   }

   private String getAppName() {
      int moduleHandle = TraceBack.getCallingModule(2);
      return getAppName(moduleHandle, this._classname, ApplicationDescriptor.currentApplicationDescriptor());
   }

   private static String getOrCreateID(int moduleHandle, String classname, boolean isContentHandler) {
      if (!isContentHandler) {
         ContentHandlerServerImpl chs = (ContentHandlerServerImpl)_registry.get(classname);
         if (chs != null) {
            return chs.getID();
         }
      }

      String ID = null;
      boolean isMidlet = CodeModuleManager.isMidlet(moduleHandle);
      if (!isMidlet && !isContentHandler) {
         ID = CodeModuleManager.getModuleVendor(moduleHandle) + "-" + ApplicationDescriptor.currentApplicationDescriptor().getName() + "-" + classname;
         return ID.replace(' ', '_');
      }

      String prefix = isContentHandler ? "MicroEdition-Handler-" : "MIDlet-";
      int midletIndex = 1;

      for (String propertyString = ContentHandlerUtilities.getStringValue(prefix + midletIndex, moduleHandle);
         propertyString != null;
         propertyString = ContentHandlerUtilities.getStringValue(prefix + ++midletIndex, moduleHandle)
      ) {
         String midletClassname = propertyString.substring(propertyString.lastIndexOf(44) + 1).trim();
         if (classname.equals(midletClassname)) {
            ID = ContentHandlerUtilities.getStringValue(prefix + midletIndex + "-ID", moduleHandle);
            if (ID != null) {
               break;
            }
         }
      }

      if (ID == null) {
         try {
            ID = ContentHandlerUtilities.getStringValue("MIDlet-Vendor", moduleHandle)
               + "-"
               + ContentHandlerUtilities.getStringValue("MIDlet-Name", moduleHandle)
               + "-"
               + classname;
            return ID.replace(' ', '_');
         } catch (NullPointerException npe) {
            return null;
         }
      } else {
         verifyCharacters(ID);
         return ID;
      }
   }

   private static String getAppName(int moduleHandle, String classname, ApplicationDescriptor application) {
      String appName = null;
      if (CodeModuleManager.isMidlet(moduleHandle)) {
         int midletIndex = 1;

         for (String propertyString = ContentHandlerUtilities.getStringValue("MIDlet-" + midletIndex, moduleHandle);
            propertyString != null;
            propertyString = ContentHandlerUtilities.getStringValue("MIDlet-" + ++midletIndex, moduleHandle)
         ) {
            String midletClassname = propertyString.substring(propertyString.lastIndexOf(44) + 1).trim();
            if (classname.equals(midletClassname)) {
               appName = propertyString.substring(0, propertyString.indexOf(44)).trim();
               break;
            }
         }

         if (appName == null) {
            return ContentHandlerUtilities.getStringValue("MIDlet-Name", moduleHandle);
         }
      } else {
         appName = application.getName();
      }

      return appName;
   }

   private static String getAuthority(int moduleHandle) {
      if (!CodeModuleManager.isMidlet(moduleHandle)) {
         return null;
      }

      CodeModuleGroup[] cmgs = CodeModuleGroupManager.loadAll();
      CodeModuleGroup cmg = null;
      if (cmgs != null) {
         for (int i = 0; i < cmgs.length; i++) {
            if (cmgs[i].containsModule(CodeModuleManager.getModuleName(moduleHandle))) {
               cmg = cmgs[i];
               break;
            }
         }
      }

      return cmg == null ? null : cmg.getMIDletSigner();
   }

   private static int verifyClassname(String classname) {
      if (classname == null) {
         throw new NullPointerException("classname is null");
      }

      if (classname.length() == 0) {
         throw new IllegalArgumentException("Invalid classname");
      }

      Process p = Process.currentProcess();
      int handle = -1;
      if (!(p instanceof ApplicationProcess)) {
         throw new IllegalArgumentException("classname does not implement the application life cycle");
      }

      Class c = Class.forName(classname);
      handle = CodeModuleManager.getModuleHandleForClass(c);
      int currentProcessModuleHandle = p.getModuleHandle();
      if ((CodeModuleManager.isMidlet(currentProcessModuleHandle) || !ControlledAccess.verifyRRISignatures(false)) && currentProcessModuleHandle != handle) {
         int[] siblings = CodeStore.getSiblingHandles(handle);
         boolean isSibling = false;

         for (int i = 0; i < siblings.length; i++) {
            if (currentProcessModuleHandle == siblings[i]) {
               isSibling = true;
               handle = currentProcessModuleHandle;
               break;
            }
         }

         if (!isSibling) {
            throw new IllegalArgumentException("classname does not exist in the current application package");
         }
      }

      return handle;
   }

   static void verifyID(String ID, String classname, boolean upgrade) {
      Enumeration e = _registry.keys();

      while (e.hasMoreElements()) {
         String currClassname = (String)e.nextElement();
         ContentHandler currHandler = (ContentHandler)_registry.get(currClassname);
         if (!upgrade && !classname.equals(currClassname) && (ID.startsWith(currHandler.getID()) || currHandler.getID().startsWith(ID))) {
            throw new ContentHandlerException("The ID " + ID + " conflicts with existing ID " + currHandler.getID(), 3);
         }
      }
   }

   static void verifyCharacters(String ID) {
      int len = ID.length();

      for (int i = 0; i < len; i++) {
         char c = ID.charAt(i);
         if (c == ' ' || c <= 31) {
            throw new IllegalArgumentException("Illegal characters in ID");
         }
      }
   }

   private static void verifyRegistered(int moduleHandle, String classname) {
      if (!_registry.containsKey(classname) && CodeModuleManager.isMidlet(moduleHandle)) {
         boolean registered = false;
         int midletIndex = 1;

         for (String propertyString = ContentHandlerUtilities.getStringValue("MIDlet-" + midletIndex, moduleHandle);
            propertyString != null;
            propertyString = ContentHandlerUtilities.getStringValue("MIDlet-" + ++midletIndex, moduleHandle)
         ) {
            String midletClassname = propertyString.substring(propertyString.lastIndexOf(44) + 1).trim();
            if (classname.equals(midletClassname)) {
               registered = true;
               break;
            }
         }

         if (!registered) {
            throw new IllegalArgumentException("classname not registered as application");
         }
      }
   }

   private static void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      if (_registry == null) {
         Hashtable[] registryArray = (Hashtable[])ar.getOrWaitFor(1885653817802432006L);
         if (registryArray != null) {
            _registry = registryArray[0];
            _handlersByType = registryArray[1];
            _handlersByAction = registryArray[2];
            _handlersBySuffix = registryArray[3];
            _handlersByID = registryArray[4];
            _persist = PersistentStore.getPersistentObject(9146446116437442900L);
            _persistedHandlers = (Hashtable)_persist.getContents();
         } else {
            registryArray = new Hashtable[5];
            _registry = new Hashtable();
            _handlersByType = new Hashtable();
            _handlersByAction = new Hashtable();
            _handlersBySuffix = new Hashtable();
            _handlersByID = new Hashtable();
            InvocationCleanupManager icm = InvocationCleanupManager.getInstance();
            Proxy.getInstance().addGlobalEventListener(icm);
            _persist = PersistentStore.getPersistentObject(9146446116437442900L);
            _persistedHandlers = (Hashtable)_persist.getContents();
            if (_persistedHandlers == null) {
               _persistedHandlers = new Hashtable();
               _persist.setContents(_persistedHandlers, 51);
               _persist.commit();
            } else {
               Enumeration e = _persistedHandlers.keys();

               while (e.hasMoreElements()) {
                  String classname = (String)e.nextElement();
                  DataBuffer persistedServer = (DataBuffer)_persistedHandlers.get(classname);
                  if (persistedServer != null) {
                     ContentHandlerServerImpl server = ContentHandlerConverter.convert(persistedServer);
                     if (server != null) {
                        registerHandler(classname, server);
                        icm.addContentHandlerModule(server.getModuleHandle(), classname, server.isDynamic());
                     }
                  }
               }
            }

            registryArray[0] = _registry;
            registryArray[1] = _handlersByType;
            registryArray[2] = _handlersByAction;
            registryArray[3] = _handlersBySuffix;
            registryArray[4] = _handlersByID;
            ar.put(1885653817802432006L, registryArray);
         }

         _transactions = (Transaction[])ar.getOrWaitFor(-1460427697355923683L);
         if (_transactions == null) {
            _transactions = new Transaction[0];
            ar.put(-1460427697355923683L, _transactions);
         }

         _registryImpls = (Hashtable)ar.getOrWaitFor(-984470147161509581L);
         if (_registryImpls == null) {
            _registryImpls = new Hashtable();
            ar.put(-984470147161509581L, _registryImpls);
         }
      }
   }
}
