package net.rim.device.apps.internal.browser.wml;

import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.model.HTTPAddressModel;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.stack.HeaderParser;
import net.rim.device.apps.internal.browser.util.LinkType;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.cldc.io.utility.URIDecoder;

final class Go extends Task {
   private WMLVariable _href;
   private String _method = "get";
   private int _enctype = 0;
   private boolean _sendReferrer;
   private String _cacheControl;
   private String _accept_charset;
   private PostField _postField;
   private FormData _postFormData;
   private HttpHeaders _offlineParameters;

   Go(WMLBrowserContent browserContent, WMLContextManager contextManager) {
      super(browserContent, contextManager);
   }

   Go(WMLBrowserContent browserContent, RenderingApplication renderingApplication, WMLContextManager contextManager, InputConnection connection) {
      this(browserContent, contextManager);
      this._offlineParameters = RendererControl.getOfflineQueueHeaders(connection);
   }

   final void setHref(WMLVariable href) {
      this._href = href;
   }

   final WMLVariable getHref() {
      return this._href;
   }

   final void setSendReferrer(boolean sendReferrer) {
      this._sendReferrer = sendReferrer;
   }

   final void setMethod(String method) {
      this._method = method;
   }

   final void setEnctype(int enctype) {
      this._enctype = enctype;
   }

   final void setEnctype(String enctype) {
      if (StringUtilities.strEqualIgnoreCase(enctype, "multipart/form-data", 1701707776)) {
         this._enctype = 1;
      } else {
         this._enctype = 0;
      }
   }

   final void setAcceptCharset(String accept_charset) {
      this._accept_charset = accept_charset;
   }

   final String getAcceptCharset() {
      return this._accept_charset;
   }

   final void setPostField(WMLVariable name, WMLVariable value) {
      if (this._postField == null) {
         this._postField = new PostField();
      }

      this._postField.add(name, value);
   }

   final void setCacheControl(String cacheControl) {
      this._cacheControl = cacheControl;
   }

   @Override
   protected final String getURL() {
      String url = null;
      if (this._href != null) {
         url = this._href.getName();
         if (this._postField != null) {
            if (this._offlineParameters == null) {
               this._offlineParameters = (HttpHeaders)(new Object());
            }

            FormData formData = this._postField.getFormData(0, this._accept_charset, this._offlineParameters, super._contextManager);
            if (this._method.equals(WMLConstants.STRING_POST)) {
               this._postFormData = formData;
               return url;
            }

            this._postFormData = null;
            String formDataStr = formData.toString();
            String str;
            if (url.indexOf(63) == -1) {
               str = ((StringBuffer)(new Object())).append('?').append(formDataStr).toString();
            } else {
               str = ((StringBuffer)(new Object())).append('&').append(formDataStr).toString();
            }

            int start = url.indexOf(35);
            if (start != -1) {
               String base = url.substring(0, start);
               String card = url.substring(start);
               return ((StringBuffer)(new Object())).append(base).append(str).append(card).toString();
            }

            url = ((StringBuffer)(new Object())).append(url).append(str).toString();
         }
      }

      return url;
   }

   @Override
   public final void loadPage(String url, Object context) {
      ContextObject contextObject = null;
      boolean programmatic = false;
      if (context instanceof Object) {
         contextObject = (ContextObject)context;
      }

      if (contextObject != null) {
         Verb[] verbs = (Object[])contextObject.get(666175809445784644L);
         if (verbs != null && verbs.length != 0) {
            verbs[0].invoke(context);
            return;
         }

         programmatic = contextObject.getFlag(64);
      }

      Verb verb = this.getVerbs(null, new Object[0]);
      if (verb != null) {
         verb.invoke(context);
      } else if (super._browserContent != null) {
         RenderingApplication renderingApplication = super._browserContent.getRenderingApplication();
         if (renderingApplication != null) {
            HttpHeaders requestHeaders = null;
            byte[] postData = null;
            if (this._method.equals(WMLConstants.STRING_POST)) {
               if (this._postField == null) {
                  this._postField = new PostField();
               }

               FormData formData = this._postFormData;
               if (formData == null) {
                  if (this._offlineParameters == null) {
                     this._offlineParameters = (HttpHeaders)(new Object());
                  }

                  formData = this._postField.getFormData(this._enctype, this._accept_charset, this._offlineParameters, super._contextManager);
               }

               if (formData != null) {
                  postData = formData.getBytes();
                  String contentType = formData.getContentType();
                  if (contentType != null) {
                     requestHeaders = (HttpHeaders)(new Object());
                     requestHeaders.setProperty("Content-Type", contentType);
                  }
               }
            }

            int flags = super._browserContent.getSharedFlags() | 1;
            if (this._cacheControl != null) {
               if (requestHeaders == null) {
                  requestHeaders = (HttpHeaders)(new Object());
               }

               requestHeaders.setProperty(HeaderParser.CACHE_CONTROL, this._cacheControl);
               flags &= -8;
               flags |= 3;
            }

            if (this._sendReferrer) {
               if (requestHeaders == null) {
                  requestHeaders = (HttpHeaders)(new Object());
               }

               RenderingUtilities.setReferrer(requestHeaders, super._browserContent.getURL());
            }

            UrlRequestedInternalEvent event = new UrlRequestedInternalEvent(
               super._browserContent, url, postData, requestHeaders, programmatic, flags, super._label != null ? super._label.getName() : null
            );
            event.setSubmitOffline(true);
            if (this._offlineParameters != null && this._offlineParameters.size() > 0) {
               event.setOfflineParameters(this._offlineParameters);
            }

            renderingApplication.eventOccurred(event);
         }
      }
   }

   @Override
   final void setBrowserContent(WMLBrowserContent browserContent) {
      super.setBrowserContent(browserContent);
   }

   public final Verb getVerbs(Object context, Verb[] verbs) {
      WMLBrowserField browserField = super._browserContent.getWMLBrowserField();
      WMLContext copyContext = (WMLContext)browserField.getContext();
      if (copyContext != null) {
         copyContext = (WMLContext)copyContext.clone();
      }

      browserField.submit(false, copyContext);
      WMLContextManager contextManager = new WMLContextManager();
      contextManager.setContext(copyContext);
      WMLVariable variable = this.getHref();
      String url = variable.getName(contextManager);
      long addressType = 5019899335844518230L;
      if (url != null) {
         if (url.indexOf(37) != -1) {
            url = URIDecoder.decode(url, this._accept_charset);
         }

         addressType = LinkType.getLinkType(url);
      }

      if (addressType != 5019899335844518230L) {
         ContextObject contextObject = (ContextObject)(new Object());
         contextObject.put(253, url);
         VerbProvider newModel = (VerbProvider)FactoryUtil.createInstance(addressType, contextObject);
         if (!(newModel instanceof HTTPAddressModel)) {
            if (context == null) {
               context = new Object(2, 73, 96);
               ((ContextObject)context).setFlag(83);
               ((ContextObject)context).setFlag(61);
               ((LongHashtable)context).put(8128293842573788963L, super._browserContent.getBrowserPhoneConfirmation());
            }

            return newModel.getVerbs(context, verbs);
         }
      }

      return null;
   }
}
