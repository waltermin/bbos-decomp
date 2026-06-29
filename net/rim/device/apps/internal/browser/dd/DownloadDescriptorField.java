package net.rim.device.apps.internal.browser.dd;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.download.DescriptorField;
import net.rim.device.apps.internal.browser.download.DownloadManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;
import net.rim.device.apps.internal.browser.util.QuincyUtil;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.vm.Process;

final class DownloadDescriptorField extends DescriptorField implements FieldChangeListener {
   private int _statusCode = 900;
   private String _errorDetail;
   private Manager _buttonContainer;
   private ButtonField _downloadButton;
   private ButtonField _cancelButton;
   private ButtonField _statusButton;
   private ButtonField _nextButton;
   private GaugeField _statusGauge;
   private OmaDownloadManager _downloadThread;
   private PopupScreen _downloadScreen;
   private DownloadDescriptorStatusReport _statusReportThread;
   private String _name;
   private int _size;
   private String _descriptorURL;
   private String _downloadURL;
   private String _installNotifyURL;
   private String _nextURL;
   private String[] _mediaTypes;
   private String _savedFilename;
   private String _existingFilename;
   private SaveFileDialog _saveDialog;
   private String _desiredContentStoreName;
   private DownloadDescriptorBrowserContent _browserContent;
   private int _mainType;
   private String _mainMediaType;
   private boolean _downloadStarted;
   private boolean _statusReportStarted;
   private boolean _statusReportResponseHandled;
   private static final int STATUS_REPORT_PROGRESS_SIZE;

   public DownloadDescriptorField(String descriptorURL, long style) {
      super(style);
      this._descriptorURL = descriptorURL;
   }

   public final void setContent(String name, String vendor, String size, String[] mediaTypes, String description, String[] errors) {
      this.deleteAll();
      this._name = name;
      if (name != null) {
         this.addNameValueField(CommonResources.getResourceBundle(), 2002, name);
      }

      this._browserContent.setTitle(MessageFormat.format(BrowserResources.getString(775), new Object[]{this.getDefaultFilename()}));
      if (vendor != null) {
         this.addNameValueField(BrowserResources.getResourceBundle(), 512, vendor);
      }

      if (size != null) {
         if (size.length() > 0 && size.charAt(0) == '+') {
            size = size.substring(1);
         }

         label82:
         try {
            size = FileUtilities.sizeToString(this._size = Integer.parseInt(size));
         } finally {
            break label82;
         }

         this.addNameValueField(BrowserResources.getResourceBundle(), 513, size);
      }

      if (mediaTypes != null && mediaTypes.length > 0) {
         this.addNameValueField(BrowserResources.getResourceBundle(), 450, getFriendlyMediaTypes(mediaTypes));
      }

      if (description != null) {
         this.addDescriptionField(description);
      }

      this.add((Field)(new Object()));
      this._buttonContainer = (Manager)(new Object(12884901888L));
      if (this._statusCode != 900) {
         LabelField errorLabel = (LabelField)(new Object(getErrorHeading(this._statusCode)));
         errorLabel.setFont(errorLabel.getFont().derive(1));
         this.add(errorLabel);
         if (errors != null) {
            for (int i = 0; i < errors.length; i++) {
               this.add((Field)(new Object(errors[i], 18014398509481984L)));
            }
         }

         this._nextButton = (ButtonField)(new Object(BrowserResources.getString(776), 65536));
         this._nextButton.setChangeListener(this);
         this._buttonContainer.add(this._nextButton);
      } else {
         this._mediaTypes = mediaTypes;
         this._downloadButton = (ButtonField)(new Object(BrowserResources.getString(273), 65536));
         this._downloadButton.setChangeListener(this);
         this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042), 65536));
         this._cancelButton.setChangeListener(this);
         this._buttonContainer.add(this._downloadButton);
         this._buttonContainer.add(this._cancelButton);
      }

      this.add(this._buttonContainer);
   }

   final void setBrowserContent(DownloadDescriptorBrowserContent browserContent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private static final String getFriendlyMediaTypes(String[] mediaTypes) {
      if (mediaTypes.length > 0) {
         StringBuffer typesBuffer = (StringBuffer)(new Object());

         for (int i = 0; i < mediaTypes.length; i++) {
            if (i > 0) {
               typesBuffer.append(", ");
            }

            typesBuffer.append(getFriendlyType(mediaTypes[i]));
         }

         return typesBuffer.toString();
      } else {
         return "";
      }
   }

   private static final String getFriendlyType(String mediaType) {
      if (mediaType != null && mediaType.length() > 2) {
         switch (mediaType.charAt(1)) {
            case 'P':
            case 'p':
               if (mediaType.equalsIgnoreCase("application/vnd.oma.drm.message")) {
                  return BrowserResources.getString(777);
               }
         }
      }

      return mediaType;
   }

   public final void setStatusCode(int statusCode) {
      this._statusCode = statusCode;
      if (statusCode != 900) {
         this.sendStatusReport();
      }
   }

   private static final String getErrorHeading(int statusCode) {
      switch (statusCode) {
         case 906:
            return BrowserResources.getString(778);
         default:
            return BrowserResources.getString(779);
      }
   }

   private final int getProgressMaxAmount() {
      return this._installNotifyURL != null ? (this._size > 0 ? this._size : 64000) + 1000 : (this._size > 0 ? this._size : 64000) + 0;
   }

   final void setDownloadURL(String downloadURL) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setInstallNotifyURL(String installNotifyURL) {
      this._installNotifyURL = installNotifyURL;
      if (installNotifyURL != null && this._statusCode != 900) {
         this.sendStatusReport();
      }
   }

   final void setNextURL(String nextURL) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private final boolean sendStatusReport() {
      String installNotifyURL = this._installNotifyURL;
      if (installNotifyURL == null) {
         return false;
      }

      synchronized (this) {
         if (this._statusReportStarted) {
            return false;
         }

         this._statusReportStarted = true;
      }

      this._statusReportThread = new DownloadDescriptorStatusReport(
         this._browserContent.resolveUrl(installNotifyURL), this._statusCode, this, this._descriptorURL
      );
      Process process = null;
      Screen screen = this.getScreen();
      if (screen != null) {
         Application app = screen.getApplication();
         if (app != null) {
            process = Process.getProcess(app.getProcessId());
            if (process != null) {
               process.addThread(this._statusReportThread);
            }
         }
      }

      if (process == null) {
         this._browserContent.getRenderingApplication().invokeRunnable(new DownloadDescriptorField$1(this));
      }

      if (this._statusCode != 900) {
         this.finishedStatusReport(200);
      }

      return true;
   }

   private final String getDefaultFilename() {
      if (this._name != null && this._name.length() > 0) {
         return this._name;
      }

      String url = this._downloadURL;
      if (url != null) {
         int endIndex = url.indexOf(63);
         if (endIndex < 0) {
            endIndex = url.length();
         }

         int lastSlash = url.lastIndexOf(47, endIndex);
         return url.substring(lastSlash + 1, endIndex);
      } else {
         return null;
      }
   }

   private final void setMainType() {
      if (this._mediaTypes != null) {
         for (int i = 0; i < this._mediaTypes.length; i++) {
            if (StringUtilities.startsWithIgnoreCase(this._mediaTypes[i], "audio/")) {
               this._mainType = 2;
               this._mainMediaType = this._mediaTypes[i];
               return;
            }

            if (StringUtilities.startsWithIgnoreCase(this._mediaTypes[i], "image/")) {
               this._mainType = 1;
               this._mainMediaType = this._mediaTypes[i];
               return;
            }

            if (StringUtilities.startsWithIgnoreCase(this._mediaTypes[i], "video/")) {
               this._mainType = 3;
               this._mainMediaType = this._mediaTypes[i];
               return;
            }
         }
      }
   }

   private final void doDownload() {
      if (!this._downloadStarted) {
         this.setMainType();
         SaveFileDialog saveDialog = (SaveFileDialog)(new Object(this.getDefaultFilename(), this._mainMediaType, this._mainType, this._size));
         int saveResult = saveDialog.doModal();
         if (saveResult == 1 || saveResult == 0) {
            this.delete(this._buttonContainer);
            this._downloadStarted = true;
            this._desiredContentStoreName = saveDialog.getURL();
            this._statusGauge = (GaugeField)(new Object(null, 0, this.getProgressMaxAmount(), 0, 65536));
            VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
            vfm.add(
               (Field)(new Object(MessageFormat.format(BrowserResources.getString(682), new Object[]{FileUtilities.getName(this._desiredContentStoreName)})))
            );
            vfm.add(this._statusGauge);
            this._statusButton = (ButtonField)(new Object(CommonResources.getString(9042), 12884967424L));
            this._statusButton.setChangeListener(this);
            vfm.add(this._statusButton);
            this._downloadThread = new OmaDownloadManager(
               this._browserContent.resolveUrl(this._downloadURL),
               this._mainMediaType,
               this._size,
               this._desiredContentStoreName,
               this,
               this._descriptorURL,
               this._installNotifyURL == null,
               this._browserContent
            );
            this._downloadScreen = new DownloadDescriptorField$2(this, vfm);
            this._saveDialog = saveDialog;
            BrowserImpl browser = BrowserDaemonRegistry.getInstance();
            browser.pushScreen(this._downloadScreen);
         }
      }
   }

   final void progressUpdate(int totalSizeDownloaded) {
      synchronized (Application.getEventLock()) {
         int maxSize = this._statusGauge.getValueMax();
         this._statusGauge.setValue(totalSizeDownloaded <= maxSize ? totalSizeDownloaded : maxSize);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void finishedDownload(int statusCode, String errorDetail, String savedFilename, String existingFilename) {
      if (this._statusCode == 900) {
         this._statusCode = statusCode;
         this._errorDetail = errorDetail;
      } else if (savedFilename != null) {
         try {
            label51:
            try {
               FileUtilities.delete(savedFilename);
            } catch (Throwable var11) {
               QuincyUtil.sendQuincy(t, false);
               break label51;
            }
         } finally {
            String var13 = null;
            String var14 = null;
         }
      }

      this._savedFilename = savedFilename;
      this._existingFilename = existingFilename;
      if (!this.sendStatusReport()) {
         this.finishedStatusReport(200);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void finishedStatusReport(int statusReportHttpResponseCode) {
      synchronized (this) {
         if (this._statusReportResponseHandled) {
            return;
         }

         this._statusReportResponseHandled = true;
      }

      EventLogger.logEvent(1907089860548946979L, ((StringBuffer)(new Object("DD sc "))).append(this._statusCode).toString().getBytes());
      boolean successful = statusReportHttpResponseCode >= 200 && statusReportHttpResponseCode < 300;
      if (this._savedFilename != null) {
         if (successful && this._statusCode == 900) {
            label200:
            try {
               if (this._existingFilename != null) {
                  FileUtilities.delete(this._existingFilename);
                  FileUtilities.moveFile(this._savedFilename, this._desiredContentStoreName);
                  this._savedFilename = this._desiredContentStoreName;
               }

               this._saveDialog.runPostSaveActions();
            } catch (Throwable var15) {
               QuincyUtil.sendQuincy(t, false);
               break label200;
            }
         } else {
            label202:
            try {
               FileUtilities.delete(this._savedFilename);
            } catch (Throwable var16) {
               QuincyUtil.sendQuincy(t, false);
               break label202;
            }

            this._savedFilename = null;
         }
      }

      if (this._downloadScreen != null && this._downloadScreen.isDisplayed()) {
         if (this._savedFilename != null) {
            this._statusGauge.setValue(this._statusGauge.getValueMax());
         }

         synchronized (Application.getEventLock()) {
            BrowserDaemonRegistry.getInstance().popScreen(this._downloadScreen);
         }

         String message = null;
         Object[] choices = null;
         if (this._savedFilename != null) {
            message = BrowserResources.getString(821);
            choices = new Object[]{getActionString(this._mainType), null};
         } else {
            if (this._statusCode != 902 && statusReportHttpResponseCode != 902) {
               message = BrowserResources.getString(685);
               if (this._errorDetail != null) {
                  message = ((StringBuffer)(new Object())).append(message).append(' ').append(this._errorDetail).toString();
               } else if (!successful && this._statusCode == 900) {
                  message = ((StringBuffer)(new Object()))
                     .append(message)
                     .append(' ')
                     .append(BrowserResources.getString(782))
                     .append(' ')
                     .append(DownloadManager.formatHttpStatusMessage(statusReportHttpResponseCode))
                     .toString();
               }
            } else {
               message = BrowserResources.getString(684);
            }

            choices = new Object[1];
         }

         choices[choices.length - 1] = BrowserResources.getString(776);
         Dialog nextStepDialog = (Dialog)(new Object(message, choices, null, 0, null));
         nextStepDialog.setIcon(ThemeManager.getThemeAwareImage(this._savedFilename != null ? "dialog_information" : "dialog_exclamation"));
         Application.getApplication().invokeLater(new DownloadDescriptorField$3(this, nextStepDialog));
      }
   }

   private static final String getActionString(int mediaType) {
      int resourceID;
      switch (mediaType) {
         case 1:
            resourceID = 781;
            break;
         case 2:
         case 7:
            resourceID = 780;
            break;
         case 3:
            resourceID = 822;
            break;
         default:
            return CommonResources.getString(9126);
      }

      return BrowserResources.getString(resourceID);
   }

   private final void goToNextURL(String nextURL, HttpHeaders requestHeaders) {
      if (nextURL == null) {
         nextURL = this._nextURL;
         if (nextURL != null) {
            if (requestHeaders == null) {
               requestHeaders = (HttpHeaders)(new Object());
            }

            RenderingUtilities.setReferrer(requestHeaders, this._descriptorURL);
         }
      }

      if (nextURL != null) {
         RenderingApplication renderingApplication = this._browserContent.getRenderingApplication();
         renderingApplication.eventOccurred((Event)(new Object(this._browserContent, this._descriptorURL, 2)));
         renderingApplication.eventOccurred(
            (Event)(new Object(this._browserContent, nextURL, null, requestHeaders, false, this._browserContent.getSharedFlags() | 1))
         );
      } else {
         this.goBack(false);
      }
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._downloadButton) {
         this.doDownload();
      } else {
         if (field == this._cancelButton) {
            if (!this._downloadStarted) {
               this.goToNextURL(null, null);
               return;
            }

            if (this._downloadThread != null) {
               this._downloadThread.abort();
               return;
            }
         } else if (field == this._statusButton) {
            if (this._downloadStarted) {
               if (this._statusReportThread != null) {
                  EventLogger.logEvent(1907089860548946979L, 1145332590);
                  this._statusReportThread.abort();
               } else {
                  EventLogger.logEvent(1907089860548946979L, 1145332580);
                  this.setStatusCode(902);
               }

               if (this._downloadThread != null) {
                  this._downloadThread.abort();
               }

               if (this._statusCode != 900) {
                  this.finishedStatusReport(200);
                  return;
               }
            }
         } else if (field == this._nextButton) {
            this.goToNextURL(null, null);
         }
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      boolean altPressed = (status & 1) != 0;
      if (!this._downloadStarted) {
         Field activeField = this.getLeafFieldWithFocus();
         if (activeField != this._downloadButton && activeField != this._cancelButton && activeField != this._nextButton) {
            return super.trackwheelClick(status, time);
         } else if (!altPressed) {
            this.fieldChanged(activeField, 0);
            return true;
         } else {
            return false;
         }
      } else {
         Field focusField = this.getLeafFieldWithFocus();
         if (focusField == this._cancelButton) {
            if (!altPressed) {
               this.fieldChanged(focusField, 0);
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   @Override
   public final void setFocus() {
      if (this._downloadButton != null) {
         this._downloadButton.setFocus();
      }
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      if (!this._downloadStarted && this._statusCode == 900) {
         this._statusCode = 902;
         this.sendStatusReport();
      }
   }

   private final void goBack(boolean inBackground) {
      BrowserImpl browser = BrowserDaemonRegistry.getInstance();
      if (!browser.goBack(false, inBackground)) {
         browser.performEscape(inBackground);
      }
   }
}
