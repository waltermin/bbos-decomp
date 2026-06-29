package net.rim.device.apps.internal.browser.html;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.device.apps.internal.browser.stack.MultipartFormData;
import net.rim.device.apps.internal.browser.stack.URLEncodedFormData;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLFormElement;

final class HTMLForm extends HTMLGenericElement implements HTMLFormElement {
   private HttpHeaders _offlineParameters;
   private HTMLCollectionImpl _elements = new HTMLCollectionImpl();
   private String _charset;
   static final String DEFAULT_ENCODING = "application/x-www-form-urlencoded";

   public final void setOfflineParameters(HttpHeaders defaultValues) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setOfflineParameter(String key, String value) {
      if (key != null && value != null) {
         if (this._offlineParameters == null) {
            this._offlineParameters = new HttpHeaders();
         }

         this._offlineParameters.setProperty(key, value);
      }
   }

   public final HttpHeaders getOfflineParameters() {
      return this._offlineParameters;
   }

   public final void addElement(HTMLInput control) {
      control.setForm(this);
      this._elements.addItem(control.getName(), control);
   }

   final void resetForm() {
      HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
      if (parent != null) {
         RenderingApplication renderingApplication = parent.getRenderingApplication();
         if (renderingApplication != null) {
            renderingApplication.invokeRunnable(new HTMLForm$FormAction(this, false, null));
         }
      }
   }

   final String getURL() {
      boolean isPost = StringUtilities.strEqualIgnoreCase(this.getMethod(), "post", 1701707776);
      if (isPost) {
         return this.getAction();
      }

      FormData formData = new URLEncodedFormData(this._charset, false);
      String action = this.getAction();
      if (action == null) {
         action = "";
      }

      String fragmentToAppend = null;
      int fragment = action.indexOf(35);
      if (fragment != -1) {
         fragmentToAppend = action.substring(fragment);
         action = action.substring(0, fragment);
      }

      StringBuffer buffer = new StringBuffer(action);
      int query = action.indexOf(63);
      if (query != -1) {
         buffer.setLength(query);
      }

      buffer.append('?');
      int length = this._elements.getLength();

      for (int index = 0; index < length; index++) {
         ((HTMLInput)this._elements.item(index)).submit(formData, buffer);
      }

      if (fragmentToAppend != null) {
         buffer.append(fragmentToAppend);
      }

      return buffer.toString();
   }

   final FormData getPostData() {
      boolean isPost = StringUtilities.strEqualIgnoreCase(this.getMethod(), "post", 1701707776);
      if (!isPost) {
         return null;
      }

      FormData formData = null;
      Object buffer = null;
      int enctype = StringUtilities.strEqualIgnoreCase(this.getEnctype(), "multipart/form-data", 1701707776) ? 1 : 0;
      if (enctype == 1) {
         formData = new MultipartFormData(this._charset, false);
         buffer = new ByteArrayOutputStream();
      } else {
         formData = new URLEncodedFormData(this._charset, false);
         buffer = new StringBuffer();
      }

      int length = this._elements.getLength();

      for (int index = 0; index < length; index++) {
         ((HTMLInput)this._elements.item(index)).submit(formData, buffer);
      }

      formData.setData(buffer);
      return formData;
   }

   final void submitForm(SubmitButton submitButton) {
      HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
      if (parent != null) {
         RenderingApplication renderingApplication = parent.getRenderingApplication();
         if (renderingApplication != null) {
            renderingApplication.invokeRunnable(new HTMLForm$FormAction(this, true, submitButton));
         }
      }
   }

   final void setPostCharset(String charset) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final String getMethod() {
      return this.getAttributeValue(140);
   }

   @Override
   public final void setMethod(String method) {
      this.setAttributeValue(140, method);
   }

   @Override
   public final String getTarget() {
      return this.getAttributeValue(187);
   }

   @Override
   public final void setTarget(String target) {
      this.setAttributeValue(187, target);
   }

   @Override
   public final void setEnctype(String enctype) {
      this.setAttributeValue(118, enctype);
   }

   @Override
   public final String getEnctype() {
      return this.getAttributeValue(118);
   }

   @Override
   public final void submit() {
      HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
      RenderingApplication renderingApplication = parent.getRenderingApplication();
      if (renderingApplication != null) {
         String action = this.getAction();
         if (action == null || action.length() == 0) {
            action = parent.getURL();
         }

         int length = this._elements.getLength();

         for (int index = 0; index < length; index++) {
            Field field = ((HTMLInput)this._elements.item(index)).getUIField();
            if (field instanceof Validation && !((Validation)field).validate()) {
               IncompleteInputWarningRunnable warning = new IncompleteInputWarningRunnable();
               Application.getApplication().invokeAndWait(warning);
               if (warning.editPage()) {
                  Application.getApplication().invokeLater(new HTMLForm$1(this, field));
                  return;
               }
               break;
            }
         }

         String fragmentToAppend = null;
         FormData formData = null;
         Object buffer = null;
         boolean isPost = StringUtilities.strEqualIgnoreCase(this.getMethod(), "post", 1701707776);
         int enctype = isPost && StringUtilities.strEqualIgnoreCase(this.getEnctype(), "multipart/form-data", 1701707776) ? 1 : 0;
         if (enctype == 1) {
            formData = new MultipartFormData(this._charset, false);
            buffer = new ByteArrayOutputStream();
         } else {
            formData = new URLEncodedFormData(this._charset, false);
            StringBuffer stringBuffer = new StringBuffer();
            if (!isPost) {
               int fragment = action.indexOf(35);
               if (fragment != -1) {
                  fragmentToAppend = action.substring(fragment);
                  action = action.substring(0, fragment);
               }

               stringBuffer.append(action);
               int query = action.indexOf(63);
               if (query != -1) {
                  stringBuffer.setLength(query);
               }

               stringBuffer.append('?');
            }

            buffer = stringBuffer;
         }

         for (int index = 0; index < length; index++) {
            ((HTMLInput)this._elements.item(index)).submit(formData, buffer);
         }

         String url = null;
         byte[] postData = null;
         HttpHeaders requestHeaders = new HttpHeaders();
         if (!isPost) {
            if (fragmentToAppend != null) {
               ((StringBuffer)buffer).append(fragmentToAppend);
            }

            url = buffer.toString();
            formData = null;
         } else {
            url = action;
            formData.setData(buffer);
            postData = formData.getBytes();
            String contentType = formData.getContentType();
            if (contentType != null) {
               requestHeaders.setProperty("Content-Type", contentType);
            }
         }

         RenderingUtilities.setReferrer(requestHeaders, parent.getURL());
         int flags = parent.getSharedFlags() | 5;
         String target = null;
         if (this.hasAttribute("target")) {
            target = this.getTarget();
         }

         if (target == null) {
            target = parent.getBaseTarget();
         }

         UrlRequestedInternalEvent event = UrlRequestedInternalEvent.processUrlRequest(parent, url, target, postData, requestHeaders, false, flags);
         if (event != null) {
            event.setSubmitOffline(true);
            event.setOfflineParameters(this._offlineParameters);
            renderingApplication.eventOccurred(event);
         }
      }
   }

   @Override
   public final void setAction(String action) {
      this.setAttributeValue(84, action);
   }

   @Override
   public final String getAction() {
      return this.getAttributeValue(84);
   }

   @Override
   public final void setAcceptCharset(String acceptCharset) {
      this.setAttributeValue(82, acceptCharset);
   }

   @Override
   public final void reset() {
      label19:
      try {
         HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
         if (!parent.executeJavaScriptAction(this, this.getAttributeValue(162), null)) {
            return;
         }
      } finally {
         break label19;
      }

      Application.getApplication().invokeAndWait(new HTMLForm$2(this));
   }

   @Override
   public final String getAcceptCharset() {
      return this.getAttributeValue(82);
   }

   @Override
   public final void setName(String name) {
      this.setAttributeValue(142, name);
   }

   @Override
   public final String getName() {
      return this.getAttributeValue(142);
   }

   @Override
   public final HTMLCollection getElements() {
      return this._elements;
   }

   @Override
   public final int getLength() {
      return this._elements.getLength();
   }

   @Override
   public final int getTagNameInt() {
      return 36;
   }

   public HTMLForm(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }
}
