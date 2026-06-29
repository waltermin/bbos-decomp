package net.rim.device.apps.internal.browser.pme.form;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.stack.FormData;
import net.rim.plazmic.mediaengine.MediaListener;

public final class PMEForm implements MediaListener {
   private BrowserContent _browserContent;
   private Hashtable _controls;
   private Hashtable _submissions;
   private String _resetTriggerId;

   PMEForm(BrowserContent browserContent) {
      this._browserContent = browserContent;
      this._controls = (Hashtable)(new Object());
      this._submissions = (Hashtable)(new Object());
   }

   final void addControl(String ref, FormField control) {
      this._controls.put(ref, control);
   }

   final void addControl(String ref, String initialValue) {
      this._controls.put(ref, initialValue);
   }

   final void addSubmission(String id, PMEForm$PMEFormSubmission submission) {
      this._submissions.put(id, submission);
      submission._submissionId = id;
   }

   final void addSubmitTrigger(String triggerId, String submissionId) {
      if (triggerId != null && submissionId != null) {
         PMEForm$PMEFormSubmission s = (PMEForm$PMEFormSubmission)this._submissions.get(submissionId);
         if (s != null) {
            s._triggerId = triggerId;
            this._submissions.put(triggerId, s);
         }
      }
   }

   final void setResetTrigger(String triggerId) {
      this._resetTriggerId = triggerId;
   }

   final Object getInitialValue(String name) {
      return this._controls == null ? null : this._controls.get(name);
   }

   @Override
   public final void mediaEvent(Object sender, int event, int eventParam, Object data) {
      if (event == 107 && data != null) {
         if (data.equals(this._resetTriggerId)) {
            this.reset();
            return;
         }

         PMEForm$PMEFormSubmission submit = (PMEForm$PMEFormSubmission)this._submissions.get(data);
         if (submit != null) {
            this.submit(submit);
         }
      }
   }

   private final void submit(PMEForm$PMEFormSubmission submission) {
      RenderingApplication renderingApplication = this._browserContent == null ? null : this._browserContent.getRenderingApplication();
      if (renderingApplication != null) {
         FormData formData = null;
         Object buffer = null;
         if (submission._multipart) {
            formData = (FormData)(new Object(submission._encoding, false));
            buffer = new Object();
         } else {
            formData = (FormData)(new Object(submission._encoding, false));
            StringBuffer stringBuffer = (StringBuffer)(new Object());
            if (!submission._post) {
               stringBuffer.append(submission._action);
               stringBuffer.append('?');
            }

            buffer = stringBuffer;
         }

         Enumeration e = this._controls.keys();

         while (e.hasMoreElements()) {
            Object key = e.nextElement();
            Object control = this._controls.get(key);
            if (!(control instanceof FormField)) {
               formData.append(buffer, (String)key, (String)control);
            } else {
               ((FormField)control).submit(formData, buffer);
            }
         }

         String url = null;
         byte[] postData = null;
         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         String var14;
         if (!submission._post) {
            var14 = buffer.toString();
            formData = null;
         } else {
            var14 = submission._action;
            formData.setData(buffer);
            postData = formData.getBytes();
            String contentType = formData.getContentType();
            if (contentType != null) {
               requestHeaders.setProperty("Content-Type", contentType);
            }
         }

         RenderingUtilities.setReferrer(requestHeaders, this._browserContent.getURL());
         UrlRequestedInternalEvent event = (UrlRequestedInternalEvent)(new Object(this._browserContent, var14, null, postData, requestHeaders, false, 3));
         event.setSubmitOffline(true);
         renderingApplication.eventOccurred(event);
      }
   }

   private final void reset() {
      Enumeration e = this._controls.elements();

      while (e.hasMoreElements()) {
         Object control = e.nextElement();
         if (control instanceof FormField) {
            ((FormField)control).reset();
         }
      }
   }
}
