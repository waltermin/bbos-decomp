package net.rim.device.api.ldap;

import java.util.Enumeration;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.ListenerUtilities;
import net.rim.device.cldc.io.utility.URIEncoder;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.UsernamePasswordDialog;
import net.rim.vm.Array;

public class LDAPQuery {
   private boolean _firstSent;
   private boolean _firstPending;
   private Object[] _listeners;
   private String _server;
   private int _port;
   private String _baseQuery;
   private String _queryType;
   private int _errorCode;
   private String _errorMsg;
   private String _finalURL;
   private String[] _attributeList;
   private String[] _hashedAttributeList;
   private String[] _filterList;
   private int _authType;
   private int _connectionType;
   private String _userDN;
   private String _password;
   private String _serviceUID;
   private LDAPQuery$LDAPQueryImpl _impl;
   public static final int LDAP_SUCCESS = -1;
   public static final int LDAP_ERROR_UNKNOWN = 0;
   public static final int LDAP_ERROR_INVALID_SCOPE_SYNTAX = 1;
   public static final int LDAP_ERROR_NO_SERVICE_BOOK_EXCEPTION = 2;
   public static final int LDAP_ERROR_TIMEOUT = 3;
   public static final int LDAP_ERROR_RADIO_IS_OFF = 4;
   public static final int LDAP_ERROR_ATTRIBUTE_IN_USE_EXCEPTION = 101;
   public static final int LDAP_ERROR_ATTRIBUTE_MODIFICATION_EXCEPTION = 102;
   public static final int LDAP_ERROR_CANNOT_PROCEED_EXCEPTION = 103;
   public static final int LDAP_ERROR_COMMUNICATION_EXCEPTION = 104;
   public static final int LDAP_ERROR_CONFIGURATION_EXCEPTION = 105;
   public static final int LDAP_ERROR_CONTEXT_NOT_EMPTY_EXCEPTION = 106;
   public static final int LDAP_ERROR_INSUFFICIENT_RESOURCES_EXCEPTION = 107;
   public static final int LDAP_ERROR_INTERRUPTED_NAMING_EXCEPTION = 108;
   public static final int LDAP_ERROR_INVALID_ATTRIBUTE_IDENTIFIER_EXCEPTION = 109;
   public static final int LDAP_ERROR_INVALID_ATTRIBUTES_EXCEPTION = 110;
   public static final int LDAP_ERROR_INVALID_ATTRIBUTE_VALUE_EXCEPTION = 111;
   public static final int LDAP_ERROR_INVALID_NAME_EXCEPTION = 112;
   public static final int LDAP_ERROR_INVALID_SEARCH_CONTROLS_EXCEPTION = 113;
   public static final int LDAP_ERROR_INVALID_SEARCH_FILTER_EXCEPTION = 114;
   public static final int LDAP_ERROR_LIMIT_EXCEEDED_EXCEPTION = 115;
   public static final int LDAP_ERROR_LINK_EXCEPTION = 116;
   public static final int LDAP_ERROR_NAME_ALREADY_BOUND_EXCEPTION = 117;
   public static final int LDAP_ERROR_NAME_NOT_FOUND_EXCEPTION = 118;
   public static final int LDAP_ERROR_NAMING_SECURITY_EXCEPTION = 119;
   public static final int LDAP_ERROR_NO_INITIAL_CONTEXT_EXCEPTION = 120;
   public static final int LDAP_ERROR_NO_SUCH_ATTRIBUTE_EXCEPTION = 121;
   public static final int LDAP_ERROR_NOT_CONTEXT_EXCEPTION = 122;
   public static final int LDAP_ERROR_OPERATION_NOT_SUPPORTED_EXCEPTION = 123;
   public static final int LDAP_ERROR_PARTIAL_RESULT_EXCEPTION = 124;
   public static final int LDAP_ERROR_REFERRAL_EXCEPTION = 125;
   public static final int LDAP_ERROR_SCHEMA_VIOLATION_EXCEPTION = 126;
   public static final int LDAP_ERROR_SERVICE_UNAVAILABLE_EXCEPTION = 127;
   public static final int LDAP_ERROR_INTERNAL_PROXY_PROVIDER_ERROR = 128;
   public static final int LDAP_ERROR_AUTHENTICATION_EXCEPTION = 129;
   public static final int LDAP_ERROR_AUTHENTICATION_NOT_SUPPORTED_EXCEPTION = 130;
   public static final int LDAP_ERROR_NO_PERMISSION_EXCEPTION = 131;
   public static final int LDAP_ERROR_NO_DEFAULT_SERVER = 132;
   public static final int LDAP_ERROR_BAD_DEFAULT_AUTHENTICATION_EXCEPTION = 133;
   public static final int LDAP_ERROR_SSL_HANDSHAKE_EXCEPTION = 134;
   public static final int LDAP_SCOPE_BASE = 0;
   public static final int LDAP_SCOPE_ONE = 1;
   public static final int LDAP_SCOPE_SUB = 2;
   public static final int LDAP_AUTH_ANONYMOUS = 0;
   public static final int LDAP_AUTH_SIMPLE = 1;
   public static final int LDAP_CONN_DEFAULT = 0;
   public static final int LDAP_CONN_SSL_OR_TLS = 1;

   public LDAPQuery() {
      this(null, null, null);
   }

   public LDAPQuery(String url) {
      this(url, null, null);
   }

   public LDAPQuery(LDAPListener listener) {
      this(null, null, listener);
   }

   public LDAPQuery(String url, LDAPListener listener) {
      this(url, null, listener);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public LDAPQuery(String url, String serviceUID, LDAPListener listener) {
      if (listener != null) {
         this._listeners = ListenerUtilities.addListener(this._listeners, listener);
      }

      this._errorCode = -1;
      this._attributeList = new Object[0];
      this._hashedAttributeList = new Object[0];
      this._filterList = new Object[0];
      if (url != null) {
         try {
            new Object(url);
         } catch (Throwable var6) {
            throw new Object(e.toString());
         }

         this._finalURL = url;
      }

      this._serviceUID = serviceUID;
   }

   public void setHost(String server, int port, String baseQuery) throws LDAPBadSyntaxException, LDAPInvalidOperationException {
      if (!this.isRunning() && this._finalURL == null) {
         if (server == null) {
            server = "";
         }

         if (baseQuery == null) {
            baseQuery = "";
         }

         if (server.indexOf(63) == -1 && server.indexOf(47) == -1 && baseQuery.indexOf(63) == -1 && baseQuery.indexOf(47) == -1) {
            this._server = server;
            this._port = port;
            this._baseQuery = URIEncoder.encodeBlanks(baseQuery);
         } else {
            throw new LDAPBadSyntaxException();
         }
      } else {
         throw new LDAPInvalidOperationException();
      }
   }

   public void setScope(int scope) throws LDAPInvalidOperationException {
      if (!this.isRunning() && this._finalURL == null) {
         switch (scope) {
            case -1:
               throw new Object();
            case 0:
               this._queryType = "base";
               return;
            case 1:
               this._queryType = "one";
               return;
            case 2:
            default:
               this._queryType = "sub";
         }
      } else {
         throw new LDAPInvalidOperationException();
      }
   }

   public void setAuthType(int authType) throws LDAPInvalidOperationException {
      if (authType != 1 && authType != 0) {
         throw new LDAPInvalidOperationException();
      }

      this._authType = authType;
   }

   public void setConnectionType(int connectionType) throws LDAPInvalidOperationException {
      if (connectionType != 0 && connectionType != 1) {
         throw new LDAPInvalidOperationException();
      }

      this._connectionType = connectionType;
   }

   @Override
   public String toString() {
      if (this._finalURL != null) {
         return this._finalURL;
      }

      StringBuffer buff = (StringBuffer)(new Object());
      if (this._connectionType == 1) {
         buff.append("ldaps://");
      } else {
         buff.append("ldap://");
      }

      if (this._server != null) {
         buff.append(this._server);
      }

      if (this._port > 0) {
         buff.append(':').append(this._port);
      }

      buff.append('/');
      if (this._baseQuery != null) {
         buff.append(this._baseQuery);
      }

      buff.append('?');

      for (int i = 0; i < this._attributeList.length; i++) {
         buff.append(this._attributeList[i]);
         if (i + 1 < this._attributeList.length) {
            buff.append(',');
         }
      }

      buff.append('?');
      if (this._queryType != null) {
         buff.append(this._queryType);
      }

      buff.append('?');
      if (this._filterList.length > 0) {
         if (this._filterList.length > 1) {
            buff.append('(').append('&');

            for (int i = 0; i < this._filterList.length; i++) {
               buff.append(this._filterList[i]);
            }

            buff.append(')');
         } else {
            buff.append(this._filterList[0]);
         }
      }

      return buff.toString();
   }

   public void addListener(LDAPListener listener) {
      this._listeners = ListenerUtilities.addListener(this._listeners, listener);
   }

   public void removeListener(LDAPListener listener) {
      this._listeners = ListenerUtilities.removeListener(this._listeners, listener);
   }

   public void addFilter(String oid, String value) throws LDAPBadSyntaxException, LDAPInvalidOperationException {
      if (this.isRunning() || this._finalURL != null) {
         throw new LDAPInvalidOperationException();
      }

      if (oid != null && value != null && oid.length() != 0) {
         if (oid.indexOf(63) != -1) {
            throw new LDAPBadSyntaxException();
         }

         if (value.indexOf(63) != -1) {
            throw new LDAPBadSyntaxException();
         }

         Array.resize(this._filterList, this._filterList.length + 1);
         StringBuffer filterString = (StringBuffer)(new Object());
         int split = value.indexOf(59);
         if (split == -1) {
            filterString.append('(').append(oid).append('=');
            filterString.append(value).append(')');
         } else {
            int last = 0;
            filterString.append('(').append('|');

            while (split != -1) {
               filterString.append('(').append(oid).append('=');
               filterString.append(value.substring(last, split));
               filterString.append(')');
               last = split + 1;
               split = value.indexOf(59, last);
            }

            filterString.append('(').append(oid).append('=');
            filterString.append(value.substring(last, value.length()));
            filterString.append(')').append(')');
         }

         this._filterList[this._filterList.length - 1] = filterString.toString();
      } else {
         throw new Object();
      }
   }

   public void addFilter(String filter) throws LDAPBadSyntaxException, LDAPInvalidOperationException {
      if (this.isRunning() || this._finalURL != null) {
         throw new LDAPInvalidOperationException();
      }

      if (filter != null && filter.length() != 0) {
         if (filter.indexOf(63) != -1) {
            throw new LDAPBadSyntaxException();
         }

         Array.resize(this._filterList, this._filterList.length + 1);
         if (filter.charAt(0) == '(' && filter.charAt(filter.length() - 1) == ')') {
            this._filterList[this._filterList.length - 1] = filter;
         } else {
            this._filterList[this._filterList.length - 1] = ((StringBuffer)(new Object())).append('(').append(filter).append(')').toString();
         }
      } else {
         throw new Object();
      }
   }

   public void addAttribute(String oid) throws LDAPBadSyntaxException, LDAPInvalidOperationException {
      if (this.isRunning() || this._finalURL != null) {
         throw new LDAPInvalidOperationException();
      }

      if (oid == null) {
         throw new Object();
      }

      if (oid.indexOf(63) != -1) {
         throw new LDAPBadSyntaxException();
      }

      Array.resize(this._attributeList, this._attributeList.length + 1);
      this._attributeList[this._attributeList.length - 1] = oid;
   }

   public void addHashedAttribute(String oid) {
      this.addAttribute(oid);
      Arrays.add(this._hashedAttributeList, oid);
   }

   public LDAPEntry getEntry(int index) {
      if (this._impl == null) {
         return null;
      }

      try {
         while (index + 1 > this._impl._size && this.isRunning()) {
            synchronized (this) {
               this.wait();
            }
         }

         return index >= 0 && index < this._impl._size ? this._impl._entries[index] : null;
      } finally {
         ;
      }
   }

   public boolean isRunning() {
      return this._impl != null && !this._impl._isDone;
   }

   public void start() {
      this.abort();
      if (this._authType != 0) {
         if (this._authType != 1) {
            this.statusUpdate(9);
            return;
         }

         LDAPPasswordCache cache = LDAPPasswordCache.getInstance();
         this._userDN = cache.getUserDN(this._server, this._baseQuery);
         this._password = cache.getPassword(this._server, this._baseQuery);
         if (this._userDN == null || this._password == null) {
            String prompt;
            if (this._server != null) {
               prompt = MessageFormat.format(CommonResource.getString(10028), new Object[]{this._server});
            } else {
               prompt = CommonResource.getString(10067);
            }

            do {
               UsernamePasswordDialog dialog;
               if (this._userDN != null) {
                  dialog = (UsernamePasswordDialog)(new Object(prompt, this._userDN, null, null, 1, 134217728));
               } else {
                  dialog = (UsernamePasswordDialog)(new Object(prompt, null, null, null, 1, 134217728));
               }

               BackgroundDialog.show(dialog);
               if (dialog.getCloseReason() == -1) {
                  this.statusUpdate(9);
                  return;
               }

               this._userDN = dialog.getUsername();
               this._password = dialog.getPassword();
            } while (this._password == null || this._password.length() <= 0);

            cache.setPassword(this._server, this._baseQuery, this._userDN, this._password);
         }
      }

      this._impl = new LDAPQuery$LDAPQueryImpl(this, this);
      this._impl._isDone = false;
      this._impl.start();
   }

   public void abort() {
      if (this._impl != null) {
         this._impl.abort();
      }

      this._impl = null;
   }

   public int getResultSize() {
      return this._impl == null ? -1 : this._impl._size;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   public String getErrorMsg() {
      return this._errorMsg;
   }

   public Enumeration getResults() {
      try {
         while (this.isRunning()) {
            synchronized (this) {
               this.wait();
            }
         }
      } finally {
         return (Enumeration)(this._impl == null ? new Object(new RIMLDAPEntry[0]) : new Object(this._impl._entries));
      }

      return (Enumeration)(this._impl == null ? new Object(new RIMLDAPEntry[0]) : new Object(this._impl._entries));
   }

   private void statusUpdate(int status) {
      if (this._listeners != null) {
         Object[] listeners = this._listeners;
         int length = listeners.length;

         for (int i = 0; i < length; i++) {
            try {
               LDAPListener listener = (LDAPListener)listeners[i];
               listener.statusUpdate(this, status);
            } finally {
               continue;
            }
         }
      }
   }

   private void entryReady(int entry) {
      if (this._listeners != null) {
         Object[] listeners = this._listeners;
         int length = listeners.length;

         for (int i = 0; i < length; i++) {
            try {
               LDAPListener listener = (LDAPListener)listeners[i];
               listener.entryReady(this, entry);
            } finally {
               continue;
            }
         }
      }
   }
}
