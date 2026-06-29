package net.rim.device.apps.internal.browser.stack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.InputConnection;
import net.rim.device.api.io.http.AuthScheme;
import net.rim.device.api.io.http.HttpFilterRegistry;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.common.AbortListener;
import net.rim.device.apps.internal.browser.cookie.CookieCache;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.core.BrowserStateListener;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.device.apps.internal.browser.options.GeneralProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.cldc.io.utility.SessionStats;
import net.rim.device.internal.browser.util.ProxyAuthStore;
import net.rim.device.internal.browser.wap.WAPServiceRecord;
import net.rim.device.internal.browser.wap.WPTCPServiceRecord;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.DataServices;
import net.rim.device.internal.ui.component.UsernamePasswordDialog;

public final class StackManager implements BrowserStateListener, PhoneCallListener, AbortListener {
   private Hashtable _networkPageFetcherRegistry = (Hashtable)(new Object());
   private CookieCache _cookieCache;
   private AuthScheme _currentScheme;
   private AuthScheme _proxyCurrentScheme;
   private Object _syncObject = new Object();
   private static final long APP_REGISTRY_KEY;
   private static final int HTTP_STATUS_BAD_REQUEST;
   private static final int HTTP_STATUS_AUTHORIZATION_REQUIRED;
   private static final int HTTP_STATUS_PROXY_AUTHORIZATION_REQUIRED;
   private static final String WWW_AUTHENTICATE_HEADER;
   private static final String PROXY_AUTHENTICATE_HEADER;
   private static final String AUTHORIZATION_HEADER;
   private static final String PROXY_AUTHORIZATION_HEADER;
   private static final String DOMAIN_VALUE;

   @Override
   public final void browserStateChanged(int newState) {
      if (newState == 0) {
         this.clearAuth();
      }
   }

   public final String getRoutableService(String requestedConfig, BrowserConfigRecord config) {
      if (!WLAN.isSupported()) {
         return requestedConfig;
      }

      if (WLAN.isAssociated() == null) {
         if ("S TCP-WBC".equalsIgnoreCase(requestedConfig)) {
            String defaultConfig = GeneralProperty.getDefaultBrowserConfigServiceUID();
            if ("S TCP-WBC".equalsIgnoreCase(defaultConfig)) {
               defaultConfig = GeneralProperty.getDefaultMdsBrowserConfigServiceUID();
            }

            if (ServiceBook.getSB().getRecordByUidAndCid(defaultConfig, BrowserConfigRecord.SERVICE_CID) != null) {
               return defaultConfig;
            }
         }

         return requestedConfig;
      } else {
         if (config == null) {
            config = BrowserConfigRecord.getDecodedConfig(requestedConfig, -1, null);
         }

         if (config == null) {
            return requestedConfig;
         }

         String transportCID = config.getPropertyAsString(3);
         String transportUID = config.getPropertyAsString(4);
         if (transportCID.equals(BrowserConfigRecord.IPPP_SERVICE_CID)) {
            if (ServiceRouting.getInstance().isServiceRoutable(transportUID, -1)) {
               return requestedConfig;
            }
         } else if (transportCID.equals(WAPServiceRecord.SERVICE_CID)) {
            if (RadioInfo.isDataServiceOperational() && this.isValidSIM() && BrowserDaemonRegistry.getInstance().isCoverageSufficient(transportCID)) {
               return requestedConfig;
            }
         } else if (transportCID.equals(WPTCPServiceRecord.SERVICE_CID)) {
            return requestedConfig;
         }

         return ServiceBook.getSB().getRecordByUidAndCid("S TCP-WBC", BrowserConfigRecord.SERVICE_CID) != null ? "S TCP-WBC" : requestedConfig;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final InputConnection fetchPage(FetchRequest fetchRequest) {
      Asserts.productionStateAssert(!Application.isEventDispatchThread());
      CacheResult cacheResult = fetchRequest.getModelResult().getCacheResult();
      String url = fetchRequest.getModelResult().getURL();
      int colonIndex = url.indexOf(58);
      if (colonIndex == -1) {
         cacheResult.setExceptionString(BrowserResources.getString(761));
         cacheResult.setStatus(400);
         return null;
      }

      String protocol = StringUtilities.toLowerCase(url.substring(0, colonIndex), 1701707776).intern();
      BrowserConfigRecord browserConfig = fetchRequest.getBrowserConfigRecord();
      if (browserConfig == null) {
         BrowserSession session = BrowserSession.getCurrentSession();
         if (session != null) {
            browserConfig = session.getConfig();
         }
      }

      label3086:
      try {
         URI uri = (URI)(new Object(fetchRequest.getModelResult().getURL()));
         String authority = uri.getAuthority();
         if (authority != null && HttpFilterRegistry.isLocalFilter(authority)) {
            protocol = "local";
         }
      } finally {
         break label3086;
      }

      NetworkPageFetcher networkPageFetcher = (NetworkPageFetcher)this._networkPageFetcherRegistry.get(protocol);
      if (networkPageFetcher == null) {
         cacheResult.setExceptionString(BrowserResources.getString(761));
         cacheResult.setStatus(400);
         return null;
      }

      Proxy proxy;
      Object var191;
      label3123: {
         BrowserImpl browserImpl = BrowserDaemonRegistry.getInstance();
         if (!(networkPageFetcher instanceof LocalStackAdapter)) {
            String transportCID = browserConfig != null ? browserConfig.getPropertyAsString(3) : null;
            String transportUID = browserConfig != null ? browserConfig.getPropertyAsString(4) : null;
            if (transportCID == null || transportCID.length() == 0 || browserConfig == null) {
               cacheResult.setExceptionString(BrowserResources.getString(233));
               cacheResult.setStatus(400);
               return null;
            }

            if (!browserConfig.isITEnabled()) {
               cacheResult.setExceptionString(BrowserResources.getString(643));
               cacheResult.setStatus(400);
               return null;
            }

            boolean isIPPP = false;
            boolean needsWiFi = false;
            boolean dataRoutable = false;
            if (transportCID.equals(BrowserConfigRecord.IPPP_SERVICE_CID)) {
               dataRoutable = ServiceRouting.getInstance().isServiceRoutable(transportUID, -1);
               isIPPP = true;
            } else if (WLAN.isSupported() && StringUtilities.strEqualIgnoreCase(transportCID, WPTCPServiceRecord.SERVICE_CID, 1701707776)) {
               ServiceRecord sr = ServiceBook.getSB().getRecordByUidAndCid(transportUID, transportCID);
               if (sr != null) {
                  WPTCPServiceRecord record = WPTCPServiceRecord.getRecord(sr);
                  if (record != null && StringUtilities.strEqualIgnoreCase(record.getPropertyAsString(19), "wifi", 1701707776)) {
                     dataRoutable = WLAN.isAssociated() != null;
                     needsWiFi = true;
                  }
               }
            }

            if (!dataRoutable) {
               if (needsWiFi) {
                  cacheResult.setExceptionString(BrowserResources.getString(875));
                  cacheResult.setStatus(400);
                  return null;
               }

               while (RadioInfo.isDataServiceSuspended()) {
                  boolean programmatic = (fetchRequest.getFlags() & 32) != 0;
                  if (!programmatic) {
                     if ((RadioInfo.getNetworkService() & 2048) != 0) {
                        cacheResult.setExceptionString(BrowserResources.getString(886));
                        cacheResult.setStatus(400);
                        fetchRequest.setSaveAllowed(true);
                        return null;
                     }

                     cacheResult.setExceptionString(BrowserResources.getString(426));
                     cacheResult.setStatus(400);
                     return null;
                  }

                  proxy = Proxy.getInstance();
                  boolean var122 = false /* VF: Semaphore variable */;

                  try {
                     var122 = true;
                     proxy.addRadioListener(this);
                     boolean var135 = false /* VF: Semaphore variable */;

                     label3064: {
                        try {
                           var135 = true;
                           fetchRequest.registerAbortListener(this, this);
                           synchronized (this._syncObject) {
                              label3053:
                              try {
                                 this._syncObject.wait();
                              } finally {
                                 break label3053;
                              }
                           }

                           if (!fetchRequest.isAborted()) {
                              var135 = false;
                              break label3064;
                           }

                           var191 = null;
                           var135 = false;
                        } finally {
                           if (var135) {
                              fetchRequest.deRegisterAbortListener(this, this);
                           }
                        }

                        fetchRequest.deRegisterAbortListener(this, this);
                        var122 = false;
                        break label3123;
                     }

                     fetchRequest.deRegisterAbortListener(this, this);
                     var122 = false;
                  } finally {
                     if (var122) {
                        proxy.removeRadioListener(this);
                     }
                  }

                  proxy.removeRadioListener(this);
               }

               if (!this.isRadioOn()) {
                  cacheResult.setExceptionString(BrowserResources.getString(386));
                  cacheResult.setStatus(400);
                  fetchRequest.setSaveAllowed(true);
                  return null;
               }

               if (!this.isValidSIM()) {
                  cacheResult.setExceptionString(BrowserResources.getString(680));
                  cacheResult.setStatus(400);
                  return null;
               }

               if (!this.inDataCoverage()) {
                  cacheResult.setExceptionString(BrowserResources.getString(383));
                  cacheResult.setStatus(400);
                  fetchRequest.setSaveAllowed(true);
                  return null;
               }

               if (!DataServices.getInstance().isDataServicesEnabled()) {
                  cacheResult.setExceptionString(BrowserResources.getString(858));
                  cacheResult.setStatus(400);
                  fetchRequest.setSaveAllowed(true);
                  return null;
               }

               if (!browserImpl.isCoverageSufficient(transportCID) || !DeviceInfo.isSimulator() && isIPPP && RadioInfo.areWAFsSupported(4)) {
                  cacheResult.setExceptionString(BrowserResources.getString(698));
                  cacheResult.setStatus(400);
                  fetchRequest.setSaveAllowed(true);
                  return null;
               }
            }

            this.addAuthIfApplicable(401, fetchRequest);
            this.addAuthIfApplicable(407, fetchRequest);
         }

         EventLogger.logEvent(1907089860548946979L, 1397581424, 5);
         InputConnection conn = this.fetchPage(networkPageFetcher, fetchRequest, browserConfig);
         InputConnection result = null;
         boolean var73 = false /* VF: Semaphore variable */;

         InputConnection var185;
         label3100: {
            try {
               label3148: {
                  var73 = true;
                  boolean authRequired = cacheResult.getStatus() == 401 || cacheResult.getStatus() == 407;
                  if (authRequired) {
                     AuthScheme newScheme = this.getAuthScheme(fetchRequest);
                     int type = cacheResult.getStatus();
                     if (newScheme != null && this.usePreviousAuth(newScheme, fetchRequest, browserConfig)) {
                        if (conn != null) {
                           label2999:
                           try {
                              conn.close();
                           } finally {
                              break label2999;
                           }
                        }

                        conn = this.fetchPage(networkPageFetcher, fetchRequest, browserConfig);
                        int var181 = fetchRequest.getModelResult().getCacheResult().getStatus();
                        authRequired = var181 == 401 || var181 == 407;
                     }

                     for (int j = 0; j < 3 && authRequired; j++) {
                        if ((fetchRequest.getFlags() & 4) != 0 || browserImpl == null) {
                           cacheResult.setExceptionString(BrowserResources.getString(280));
                           cacheResult.setStatus(400);
                           var190 = null;
                           var73 = false;
                           break label3148;
                        }

                        String realm = newScheme != null ? newScheme.getParameter("realm") : null;
                        if (newScheme == null || realm == null) {
                           break;
                        }

                        String rimDomain = newScheme.getParameter("rimdomain");
                        int var182 = fetchRequest.getModelResult().getCacheResult().getStatus();
                        String title = null;
                        if (var182 == 401) {
                           title = ((StringBuffer)(new Object())).append(BrowserResources.getString(220)).append(realm).toString();
                        } else if (var182 == 407) {
                           title = ((StringBuffer)(new Object())).append(BrowserResources.getString(501)).append(realm).toString();
                        }

                        String username = null;
                        String[] credentials = GeneralProperty.getAuthenticationCredentialsValue(realm);
                        if (credentials != null) {
                           username = credentials[0];
                           if (credentials.length > 2) {
                              rimDomain = credentials[2];
                           } else {
                              rimDomain = null;
                           }
                        }

                        UsernamePasswordDialog upd = (UsernamePasswordDialog)(new Object(title, username, rimDomain, null, 2, 0));
                        upd.setAllowUnicodePassword(true);
                        Application.getApplication().invokeAndWait(new UPDRunnable(upd));
                        if (upd.getCloseReason() == -1) {
                           if (var182 == 401) {
                              this._currentScheme = null;
                           } else if (var182 == 407) {
                              this._proxyCurrentScheme = null;
                           }
                        } else {
                           username = upd.getUsername();
                           String password = upd.getPassword();
                           if (username == null || password == null) {
                              break;
                           }

                           rimDomain = upd.getDomain();
                           String domainUsername = username;
                           if (rimDomain != null) {
                              domainUsername = ((StringBuffer)(new Object())).append(rimDomain).append("\\").append(username).toString();
                           }

                           newScheme.setParameter("username", domainUsername);
                           newScheme.setParameter("password", password);
                           if (var182 == 401) {
                              this._currentScheme = newScheme;
                           } else if (var182 == 407) {
                              this._proxyCurrentScheme = newScheme;
                           }

                           if (upd.rememberCredentials()) {
                              credentials = new Object[rimDomain != null ? 3 : 2];
                              credentials[0] = username;
                              credentials[1] = "";
                              if (rimDomain != null) {
                                 credentials[2] = rimDomain;
                              }

                              GeneralProperty.setAuthenticationCredentialsValue(newScheme.getParameter("realm"), credentials);
                           }
                        }

                        if (!this.addAuth(var182, fetchRequest)) {
                           break;
                        }

                        if (var182 != 407) {
                           this.addAuthIfApplicable(407, fetchRequest);
                        }

                        label2978:
                        try {
                           if (conn != null) {
                              label2975:
                              try {
                                 conn.close();
                              } finally {
                                 break label2975;
                              }

                              conn = null;
                           }
                        } finally {
                           break label2978;
                        }

                        conn = this.fetchPage(networkPageFetcher, fetchRequest, browserConfig);
                        authRequired = cacheResult.getStatus() == 401 || cacheResult.getStatus() == 407;
                        if (authRequired) {
                           newScheme = this.getAuthScheme(fetchRequest);
                        }
                     }
                  }

                  HttpHeaders headers = cacheResult.getResponseHeaders();
                  if (headers != null) {
                     String authInfo = headers.getPropertyValue("authentication-info");
                     if (authInfo != null && this._currentScheme != null) {
                        this._currentScheme.updateAuth(authInfo);
                     }

                     String var184 = headers.getPropertyValue("proxy-authentication-info");
                     if (var184 != null && this._proxyCurrentScheme != null) {
                        this._proxyCurrentScheme.updateAuth(var184);
                     }
                  }

                  result = conn;
                  var185 = conn;
                  var73 = false;
                  break label3100;
               }
            } finally {
               if (var73) {
                  if (result == null && conn != null) {
                     label2922:
                     try {
                        conn.close();
                     } finally {
                        break label2922;
                     }
                  }
               }
            }

            if (result == null && conn != null) {
               try {
                  conn.close();
               } finally {
                  return (InputConnection)var190;
               }
            }

            return (InputConnection)var190;
         }

         if (result == null && conn != null) {
            try {
               conn.close();
            } finally {
               return var185;
            }
         }

         return var185;
      }

      proxy.removeRadioListener(this);
      return (InputConnection)var191;
   }

   public final void registerNetworkPageFetcher(NetworkPageFetcher pageFetcher, String[] protocols) {
      for (int i = protocols.length - 1; i >= 0; i--) {
         this._networkPageFetcherRegistry.put(protocols[i], pageFetcher);
      }
   }

   public final void reinitialize() {
      Enumeration fetchers = this._networkPageFetcherRegistry.elements();

      while (fetchers.hasMoreElements()) {
         ((NetworkPageFetcher)fetchers.nextElement()).reinitialize();
      }
   }

   public final SessionStats getSessionStats() {
      NetworkPageFetcher networkFetcher = (NetworkPageFetcher)this._networkPageFetcherRegistry.get("http");
      return !(networkFetcher instanceof HTTPStackAdapter) ? null : ((HTTPStackAdapter)networkFetcher).getSessionStats();
   }

   public final boolean ableToMakeRequest() {
      return RadioInfo.isDataServiceOperational();
   }

   public final boolean authExists() {
      return this._currentScheme != null || this._proxyCurrentScheme != null;
   }

   public final void clearAuth() {
      this._currentScheme = null;
      this._proxyCurrentScheme = null;
   }

   @Override
   public final void callAdded(int arg0) {
   }

   @Override
   public final void callInitiated(int arg0) {
   }

   @Override
   public final void callConnected(int arg0) {
   }

   @Override
   public final void callDelivered(int arg0) {
   }

   @Override
   public final void callDisconnected(int arg0) {
      synchronized (this._syncObject) {
         this._syncObject.notifyAll();
      }
   }

   @Override
   public final void callDisplayUpdated(int arg0) {
   }

   @Override
   public final void callFailed(int arg0, int arg1) {
      synchronized (this._syncObject) {
         this._syncObject.notifyAll();
      }
   }

   @Override
   public final void callHeld(int arg0) {
   }

   @Override
   public final void callIncoming(int arg0) {
   }

   @Override
   public final void callManipulateFailed(int arg0, int arg1) {
   }

   @Override
   public final void callOTAStatusUpdated(int arg0, int arg1) {
   }

   @Override
   public final void callRemoved(int arg0) {
   }

   @Override
   public final void callResumed(int arg0) {
   }

   @Override
   public final void callTransferred(int arg0, int arg1) {
   }

   @Override
   public final void callTransferStateUpdated(int arg0, int arg1) {
   }

   @Override
   public final void callVoicePrivacyUpdated(int arg0, boolean arg1) {
   }

   @Override
   public final void callWaiting(int arg0) {
   }

   @Override
   public final void abort(Object abortContext) {
      synchronized (this._syncObject) {
         this._syncObject.notifyAll();
      }
   }

   @Override
   public final void dtmfData(int dtmf) {
   }

   private final InputConnection fetchPage(NetworkPageFetcher networkPageFetcher, FetchRequest fetchRequest, BrowserConfigRecord browserConfig) {
      CacheResult cacheResult = fetchRequest.getModelResult().getCacheResult();
      String cookieStr = this._cookieCache.getCookies(URI.getAbsoluteURL(fetchRequest.getModelResult().getURL(), null));
      if (cookieStr != null && cookieStr.length() != 0) {
         fetchRequest.getModelResult().getRequestHeaders().setProperty("Cookie", cookieStr);
      }

      InputConnection conn = networkPageFetcher.fetchPage(fetchRequest, browserConfig);
      HttpHeaders headers = cacheResult.getResponseHeaders();
      if (headers != null) {
         Vector cookies = headers.getPropertyValues("set-cookie");
         if (cookies != null) {
            String absUrl = fetchRequest.getModelResult().getURL();

            for (int i = cookies.size() - 1; i >= 0; i--) {
               this._cookieCache.addCookies(absUrl, (String)cookies.elementAt(i));
            }
         }
      }

      return conn;
   }

   private final boolean testScheme(AuthScheme scheme, AuthScheme newScheme) {
      return newScheme != null
         && scheme != null
         && StringUtilities.strEqual(scheme.getParameter("realm"), newScheme.getParameter("realm"))
         && StringUtilities.strEqual(scheme.getParameter("rimdomain"), newScheme.getParameter("rimdomain"));
   }

   private final boolean usePreviousAuth(AuthScheme newScheme, FetchRequest fetchRequest, BrowserConfigRecord browserConfig) {
      int type = fetchRequest.getModelResult().getCacheResult().getStatus();
      if (type == 407
         && StringUtilities.strEqualIgnoreCase(browserConfig.getPropertyAsString(3), "WPTCP", 1701707776)
         && browserConfig.getPropertyAsInt(12) == 8) {
         String[] parms = ProxyAuthStore.getConnection(browserConfig.getPropertyAsString(4));
         if (parms != null && StringUtilities.strEqualIgnoreCase(parms[0], newScheme.getParameter("realm"), 1701707776)) {
            newScheme.setParameter("username", parms[1]);
            newScheme.setParameter("password", parms[2]);
            this._proxyCurrentScheme = newScheme;
            return this.addAuth(type, fetchRequest);
         }
      }

      if (this.testScheme(type == 401 ? this._currentScheme : this._proxyCurrentScheme, newScheme)) {
         if (StringUtilities.strEqualIgnoreCase(newScheme.getParameter("stale"), "true", 1701707776)) {
            newScheme.setParameter("username", this._currentScheme.getParameter("username"));
            newScheme.setParameter("password", this._currentScheme.getParameter("password"));
            if (type == 401) {
               this._currentScheme = newScheme;
            } else if (type == 407) {
               this._proxyCurrentScheme = newScheme;
            }
         }

         return this.addAuth(type, fetchRequest);
      } else {
         return false;
      }
   }

   private final void addAuthIfApplicable(int type, FetchRequest fetchRequest) {
      AuthScheme scheme = null;
      if (type == 401) {
         scheme = this._currentScheme;
      } else if (type == 407) {
         this.addAuth(type, fetchRequest);
      }

      if (scheme != null) {
         String domain = scheme.getParameter("domain");
         String rimHost = scheme.getParameter("rimhost");
         if (domain != null) {
            domain = StringUtilities.toLowerCase(domain, 1701707776);
            String uri = StringUtilities.toLowerCase(fetchRequest.getModelResult().getURL(), 1701707776);
            StringTokenizer tokens = (StringTokenizer)(new Object(domain, ' '));

            while (tokens.hasMoreElements()) {
               if (uri.startsWith(tokens.nextToken())) {
                  this.addAuth(type, fetchRequest);
                  return;
               }
            }
         } else if (rimHost != null) {
            try {
               URI uri = (URI)(new Object(fetchRequest.getModelResult().getURL()));
               if (StringUtilities.strEqualIgnoreCase(rimHost, uri.getAuthority(), 1701707776)) {
                  this.addAuth(type, fetchRequest);
                  return;
               }
            } finally {
               return;
            }
         }
      }
   }

   private final boolean addAuth(int type, FetchRequest fetchRequest) {
      AuthScheme scheme = null;
      String typeStr = null;
      if (type == 401) {
         scheme = this._currentScheme;
         typeStr = "authorization";
      } else if (type == 407) {
         scheme = this._proxyCurrentScheme;
         typeStr = "proxy-authorization";
      }

      if (scheme == null) {
         return false;
      }

      label72:
      try {
         String url = fetchRequest.getModelResult().getURL();
         URI uri = (URI)(new Object(url));
         String query = uri.getQuery();
         if (query != null && query.length() == 0) {
            query = null;
         }

         if (url.startsWith("https") && type == 407) {
            scheme.setParameter("method", "CONNECT");
            scheme.setParameter("uri", uri.getAuthority());
         } else {
            scheme.setParameter("method", fetchRequest.getModelResult().getPostData() != null ? "POST" : "GET");
            scheme.setParameter(
               "uri",
               ((StringBuffer)(new Object()))
                  .append(uri.getPath())
                  .append(query == null ? "" : ((StringBuffer)(new Object())).append('?').append(query).toString())
                  .toString()
            );
         }
      } finally {
         break label72;
      }

      String response = scheme.getAuthResponse();
      if (response != null) {
         HttpHeaders requestHeaders = fetchRequest.getModelResult().getRequestHeaders();
         requestHeaders.setProperty(typeStr, response);
         return true;
      } else {
         return false;
      }
   }

   private final AuthScheme getAuthScheme(FetchRequest fetchRequest) {
      int type = fetchRequest.getModelResult().getCacheResult().getStatus();
      HttpHeaders responseHeaders = fetchRequest.getModelResult().getCacheResult().getResponseHeaders();
      Vector challenges = null;
      if (type == 401) {
         challenges = responseHeaders.getPropertyValues("www-authenticate");
      } else if (type == 407) {
         challenges = responseHeaders.getPropertyValues("proxy-authenticate");
      }

      if (challenges != null) {
         int size = challenges.size();

         for (int i = 0; i < size; i++) {
            AuthScheme scheme = AuthScheme.getAuthScheme((String)challenges.elementAt(i));
            if (scheme != null) {
               String rimDomain = this.readRIMDomain(fetchRequest);
               if (rimDomain != null) {
                  scheme.setParameter("rimdomain", rimDomain);
               }

               if (scheme.getType() == 1) {
                  try {
                     URI uri = (URI)(new Object(fetchRequest.getModelResult().getURL()));
                     scheme.setParameter("rimhost", uri.getAuthority());
                  } finally {
                     return scheme;
                  }
               }

               return scheme;
            }
         }
      }

      return null;
   }

   private final String readRIMDomain(FetchRequest fetchRequest) {
      HttpHeaders responseHeaders = fetchRequest.getModelResult().getCacheResult().getResponseHeaders();
      String authenticate = responseHeaders.getPropertyValue("x-rim-authenticate");
      if (authenticate != null) {
         int index = authenticate.indexOf("domain=");
         if (index != -1) {
            int endIndex = authenticate.indexOf(index, 44);
            if (endIndex == -1) {
               endIndex = authenticate.length();
            }

            return authenticate.substring(index + 7, endIndex);
         }
      }

      return null;
   }

   public static final boolean isVoiceActive() {
      return Phone.isSupported() ? Phone.getInstance().isActive() : false;
   }

   private final boolean isRadioOn() {
      return RadioInfo.getState() == 1;
   }

   private final boolean isValidSIM() {
      if (SIMCard.isSupported() && (RadioInfo.getActiveWAFs() & 2) == 0) {
         try {
            return SIMCard.isValid();
         } finally {
            ;
         }
      } else {
         return true;
      }
   }

   private final boolean inDataCoverage() {
      return (RadioInfo.getNetworkService() & 4) != 0;
   }

   public static final StackManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      StackManager instance = (StackManager)ar.get(-7621055828466010426L);
      if (instance == null) {
         instance = new StackManager();
         StackManager old = (StackManager)ar.getOrWaitFor(-7621055828466010426L);
         if (old == null) {
            ar.put(-7621055828466010426L, instance);
         } else {
            instance = old;
         }

         if (old == null) {
            BrowserDaemonRegistry.addBrowserStateListener(instance);
         }
      }

      return instance;
   }

   private StackManager() {
      this._cookieCache = CookieCache.getInstance();
   }
}
