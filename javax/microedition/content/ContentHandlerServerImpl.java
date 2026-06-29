package javax.microedition.content;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationManagerException;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Message;
import net.rim.vm.Process;

class ContentHandlerServerImpl extends ContentHandlerImpl implements ContentHandlerServer {
   private String[] _accessAllowed;
   private RequestListener _listener;
   private InvocationQueue _queue = new InvocationQueue();
   private ApplicationDescriptor _application;
   private int _numStaleInvocations;

   void init(String[] types, String[] suffixes, String[] actions, ActionNameMap[] actionnames, String ID, String[] accessAllowed) {
      super._types = types == null ? new String[0] : types;
      super._suffixes = suffixes == null ? new String[0] : suffixes;
      super._actions = actions == null ? new String[0] : actions;
      super._actionnames = actionnames == null ? new ActionNameMap[0] : actionnames;
      super._ID = ID;
      this.checkID(super._ID);
      this._accessAllowed = accessAllowed;
      ContentHandlerUtilities.checkStringArrayValues(super._types);
      ContentHandlerUtilities.checkStringArrayValues(super._suffixes);
      ContentHandlerUtilities.checkStringArrayValues(super._actions);
      ContentHandlerUtilities.checkStringArrayValues(this._accessAllowed, false);
      this.checkActionNameMapArrayValues(super._actionnames);
   }

   void setApplicationDescriptor(ApplicationDescriptor application) {
      this._application = application;
   }

   int getQueueSize() {
      return this._queue.size();
   }

   void cancelOldInvocations() {
      for (int i = 0; i < this._numStaleInvocations; i++) {
         Invocation next = this._queue.nextInvocation();
         if (next != null) {
            RegistryImpl.invocationFinished(next, 7);
         }
      }
   }

   String[] getAccessAllowed() {
      return this._accessAllowed;
   }

   void start() {
      synchronized (this._queue) {
         int pid = this.wakeAppAndNotifyListener(true);
         if (pid == -1) {
            int numInvocations = this._queue.size();

            for (int i = 0; i < numInvocations; i++) {
               RegistryImpl.invocationFinished(this._queue.nextInvocation(), 7);
            }
         } else {
            boolean firstRun = InvocationCleanupManager.getInstance().requestHandlerStarted(pid, this);
            if (firstRun) {
               this._numStaleInvocations = this._queue.size();
            }

            this._queue.notifyAll();
         }
      }
   }

   void start(Invocation invocation) {
      synchronized (this._queue) {
         this._queue.addInvocation(invocation);
         this.start();
      }
   }

   @Override
   public Invocation getRequest(boolean wait) {
      this.assertPermission();
      synchronized (this._queue) {
         Invocation next = this._queue.nextInvocation();
         if (next == null && wait) {
            try {
               this._queue.wait();
            } catch (InterruptedException var7) {
            }

            next = this._queue.nextInvocation();
         }

         if (next != null) {
            InvocationCleanupManager icm = InvocationCleanupManager.getInstance();
            int pid = Process.currentProcess().getProcessId();
            icm.addActiveInvocation(pid, next);
            icm.requestRetreived(pid);
         }

         return next;
      }
   }

   @Override
   public void cancelGetRequest() {
      this.assertPermission();
      synchronized (this._queue) {
         this._queue.notifyAll();
      }
   }

   @Override
   public void setListener(RequestListener listener) {
      this.assertPermission();
      this._listener = listener;
      synchronized (this._queue) {
         if (this._queue.size() > 0) {
            this.wakeAppAndNotifyListener(false);
         }
      }
   }

   @Override
   public boolean isAccessAllowed(String ID) {
      if (ID == null) {
         throw new NullPointerException("ID is null");
      }

      if (this._accessAllowed != null && this._accessAllowed.length != 0) {
         for (int i = 0; i < this._accessAllowed.length; i++) {
            if (ID.startsWith(this._accessAllowed[i])) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public boolean finish(Invocation invocation, int status) {
      this.assertPermission();
      if (status != 5 && status != 6 && status != 8) {
         throw new IllegalArgumentException("Invalid status");
      }

      if (invocation.getStatus() != 2 && invocation.getStatus() != 4) {
         throw new IllegalStateException("Invocation has invalid status");
      }

      InvocationCleanupManager.getInstance().removeActiveInvocation(Process.currentProcess().getProcessId(), invocation);
      if (invocation.getResponseRequired()) {
         RegistryImpl.invocationFinished(invocation, status);
      }

      return false;
   }

   @Override
   public String getAccessAllowed(int index) {
      if (index >= 0 && index < this.accessAllowedCount()) {
         return this._accessAllowed[index];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public int accessAllowedCount() {
      return this._accessAllowed == null ? 0 : this._accessAllowed.length;
   }

   private int wakeAppAndNotifyListener(boolean grabForeground) {
      int pid = -1;
      ApplicationManager am = ApplicationManager.getApplicationManager();

      try {
         pid = am.runApplication(this._application, grabForeground);
      } catch (ApplicationManagerException var8) {
      } finally {
         if (pid == -1) {
            return pid;
         }
      }

      if (this._listener != null) {
         Message invokeLaterMessage = new Message(0, 2, new ContentHandlerServerImpl$1(this), null);
         ((ApplicationManagerInternal)am).postMessage(pid, invokeLaterMessage);
      }

      return pid;
   }

   private void checkActionNameMapArrayValues(ActionNameMap[] actionnames) {
      String[] locales = new String[actionnames.length];

      for (int i = 0; i < actionnames.length; i++) {
         if (actionnames[i].size() != super._actions.length) {
            throw new IllegalArgumentException("Sequence of actions does not match the sequence in all ActionNameMaps");
         }

         for (int j = 0; j < super._actions.length; j++) {
            if (!super._actions[j].equals(actionnames[i].getAction(j))) {
               throw new IllegalArgumentException("Sequence of actions does not match the sequence in all ActionNameMaps");
            }
         }

         locales[i] = actionnames[i].getLocale();
      }

      if (ContentHandlerUtilities.containsDuplicates(locales)) {
         throw new IllegalArgumentException("ActionNameMap locales are not unique");
      }
   }

   private void checkID(String ID) {
      int length = ID.length();
      if (length == 0) {
         throw new IllegalArgumentException("ID is an empty String");
      }

      for (int i = 0; i < length; i++) {
         if (ID.charAt(i) <= ' ') {
            throw new IllegalArgumentException("ID contains invalid characters");
         }
      }
   }

   private void assertPermission() {
      ApplicationControl.assertIPCAllowed(true);
   }
}
