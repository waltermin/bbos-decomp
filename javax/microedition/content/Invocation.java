package javax.microedition.content;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.cldc.io.dns.DNSException;
import net.rim.device.internal.io.RIMConnector;
import net.rim.vm.TraceBack;

public final class Invocation {
   private int _status;
   private String _url;
   private String _type;
   private String _ID;
   private String _action;
   private boolean _responseRequired;
   private String[] _args;
   private byte[] _data;
   private String _invokingAuthority;
   private String _invokingID;
   private String _invokingAppName;
   private String _username;
   private char[] _password;
   private Invocation _previous;
   private Invocation _original;
   private Connection _connection;
   private ApplicationDescriptor _invokerDescriptor;
   private int[] _invokerStack;
   public static final int INIT = 1;
   public static final int ACTIVE = 2;
   public static final int WAITING = 3;
   public static final int HOLD = 4;
   public static final int OK = 5;
   public static final int CANCELLED = 6;
   public static final int ERROR = 7;
   public static final int INITIATED = 8;

   public Invocation() {
      this(null, null, null, true, null);
   }

   public Invocation(String url, String type, String ID) {
      this(url, type, ID, true, null);
   }

   public Invocation(String url, String type) {
      this(url, type, null, true, null);
   }

   public Invocation(String url) {
      this(url, null, null, true, null);
   }

   public Invocation(String url, String type, String ID, boolean responseRequired, String action) {
      this._url = url;
      this._type = type;
      this._ID = ID;
      this._responseRequired = responseRequired;
      this._action = action;
      this._status = 1;
      this._args = new String[0];
      this._data = new byte[0];
      this._previous = null;
   }

   Invocation(Invocation invocation) {
      this._url = invocation.getURL();
      this._type = invocation.getType();
      this._ID = invocation.getID();
      this._responseRequired = invocation.getResponseRequired();
      this._action = invocation.getAction();
      this._args = invocation.getArgs();
      this._data = invocation.getData();
      this._previous = invocation.getPrevious();
      this._username = invocation.getUsername();
      this._password = invocation.getPassword();
      this._invokerDescriptor = invocation.getInvokerDescriptor();
      this._invokerStack = invocation.getInvokerStack();
      this._original = invocation;

      for (int i = 0; i < this._args.length; i++) {
         if (this._args[i] == null) {
            throw new IllegalArgumentException("null parameter in invocation args");
         }
      }
   }

   public final void setArgs(String[] args) {
      this._args = args == null ? new String[0] : args;
   }

   public final String[] getArgs() {
      return this._args;
   }

   public final void setData(byte[] data) {
      this._data = data == null ? new byte[0] : data;
   }

   public final byte[] getData() {
      return this._data;
   }

   public final String getURL() {
      return this._url;
   }

   public final void setURL(String url) {
      this._url = url;
   }

   public final String getType() {
      return this._type;
   }

   public final void setType(String type) {
      this._type = type;
   }

   public final String getAction() {
      return this._action;
   }

   public final void setAction(String action) {
      this._action = action;
   }

   public final boolean getResponseRequired() {
      return this._responseRequired;
   }

   public final void setResponseRequired(boolean responseRequired) {
      if (this._status == 1) {
         this._responseRequired = responseRequired;
      } else {
         throw new IllegalStateException("Invocation is not in INIT state");
      }
   }

   public final int getStatus() {
      return this._status;
   }

   final void setStatus(int status) {
      this._status = status;
   }

   public final String getID() {
      return this._ID;
   }

   public final void setID(String ID) {
      this._ID = ID;
   }

   public final Invocation getPrevious() {
      return this._previous;
   }

   final Invocation getOriginal() {
      return this._original;
   }

   final void setInvokerInfo(String invokingAuthority, String invokingID, String invokingAppName) {
      this._invokingAuthority = invokingAuthority;
      this._invokingID = invokingID;
      this._invokingAppName = invokingAppName;
   }

   public final String getInvokingAuthority() {
      return this._status != 2 && this._status != 4 ? null : this._invokingAuthority;
   }

   public final String getInvokingID() {
      return this._status != 2 && this._status != 4 ? null : this._invokingID;
   }

   public final String getInvokingAppName() {
      return this._status != 2 && this._status != 4 ? null : this._invokingAppName;
   }

   public final String findType() {
      String type = this.getType();
      if (type != null) {
         return type;
      }

      if (this._url != null) {
         if (this._connection == null) {
            ContentHandlerUtilities.checkURL(this._url);
            int callingModule = TraceBack.getCallingModule(3);
            if (callingModule == 0) {
               callingModule = TraceBack.getCallingModule(0);
            }

            this.populateStackAndDescriptor();

            try {
               this._connection = RIMConnector.open(callingModule, this._url, 1, false, this._invokerStack, this._invokerDescriptor);
            } catch (ControlledAccessException cae) {
               throw new SecurityException(cae.getMessage());
            } catch (ConnectionNotFoundException cnfe) {
               throw new IOException(cnfe.getMessage());
            }
         }

         if (!(this._connection instanceof HttpConnection)) {
            if (this._connection instanceof ContentConnection) {
               type = ((ContentConnection)this._connection).getType();
            }
         } else {
            HttpConnection httpConnection = (HttpConnection)this._connection;
            int response = httpConnection.getResponseCode();
            if (response != 200) {
               throw new IOException("Error code: " + response);
            }

            type = httpConnection.getType();
         }
      }

      if (type != null) {
         this.setType(type);
         return type;
      } else {
         throw new ContentHandlerException("Type could not be found from the URL content", 2);
      }
   }

   public final Connection open(boolean timeouts) {
      if (this._url == null) {
         throw new NullPointerException("URL is null");
      }

      String urlToOpen = ContentHandlerUtilities.checkURL(this._url);
      int callingModule = TraceBack.getCallingModule(0);
      this.populateStackAndDescriptor();

      try {
         this._connection = RIMConnector.open(callingModule, urlToOpen, 1, timeouts, this._invokerStack, this._invokerDescriptor);
      } catch (DNSException dnse) {
         throw new ConnectionNotFoundException(dnse.getMessage());
      } catch (IOException ioe) {
         throw new ConnectionNotFoundException(ioe.getMessage());
      }

      return this._connection;
   }

   public final void setCredentials(String username, char[] password) {
      this._username = username;
      this._password = password;
   }

   final String getUsername() {
      return this._username;
   }

   final char[] getPassword() {
      return this._password;
   }

   final void setPrevious(Invocation invocation) {
      this._previous = invocation;
   }

   final void setInvokerStack(int[] stack) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._invokerStack = stack;
   }

   final void setInvokerDescriptor(ApplicationDescriptor descriptor) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._invokerDescriptor = descriptor;
   }

   final void populateStackAndDescriptor() {
      if (this._invokerDescriptor == null) {
         this.setInvokerDescriptor(ApplicationDescriptor.currentApplicationDescriptor());
      }

      if (this._invokerStack == null) {
         this.setInvokerStack(TraceBack.getCallingModules());
      }
   }

   final ApplicationDescriptor getInvokerDescriptor() {
      return this._invokerDescriptor;
   }

   final int[] getInvokerStack() {
      return this._invokerStack;
   }
}
