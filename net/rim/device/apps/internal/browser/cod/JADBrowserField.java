package net.rim.device.apps.internal.browser.cod;

import java.io.InputStream;
import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ApplicationPermissionsManager;
import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.CodeModuleGroup;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.RunnableDialog;
import net.rim.device.apps.api.ui.SecurityDialog;
import net.rim.device.apps.api.utility.general.URI;
import net.rim.device.apps.internal.browser.download.DescriptorField;
import net.rim.device.apps.internal.browser.download.DownloadManager;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.Asserts;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.ui.Image;

final class JADBrowserField extends DescriptorField implements ApplicationDownloadListener, FieldChangeListener {
   private JADAttributeParser _jad;
   private Manager _buttonContainer;
   private ButtonField _downloadButton;
   private ButtonField _cancelButton;
   private boolean _isDownloading;
   private PopupScreen _downloadScreen;
   private ApplicationDownloadManager _downloadThread;
   private RichTextField _statusText;
   private GaugeField _statusGauge;
   private ButtonField _statusButton;
   private CheckboxField _permissionsButton;
   private String _downloadLabel;
   private String _installLabel;
   private int _currentProgressAction;
   private UiApplication _application = UiApplication.getUiApplication();
   private RenderingApplication _renderingApp;
   private InputStream _jarStream;
   private boolean _drmProtected;
   private static final int OK;
   private static final int DETAILS;
   private static final int LATER;
   private static final int REBOOT;
   private static final int COPY;
   private static final int RUN;

   public JADBrowserField(BrowserContentBaseImpl browserContent, JADAttributeParser jad, InputStream jarStream) {
      super(281474976710656L);
      this.setChangeListener(this);
      this._renderingApp = browserContent.getRenderingApplication();
      this._drmProtected = jarStream != null && (browserContent.getRenderingFlags() & 2048) != 0;

      label170:
      try {
         browserContent.setTitle(((URI)(new Object(browserContent.getURL()))).getFileName());
      } finally {
         break label170;
      }

      this._jarStream = jarStream;
      if (jad != null && jad.isValid()) {
         this._jad = jad;
         int numberOfCODfiles = jad.getCodUrlCount();
         int codeSize = 0;
         if (numberOfCODfiles > 0 || jad.getJarURL() != null) {
            codeSize = jad.totalSize();
         }

         boolean anyDownloadsAllowed = !ITPolicy.getBoolean(24, 11, false);
         boolean downloadAllowed = anyDownloadsAllowed && !this.isApplicationExcluded();
         String missingDependencies = this.getMissingDependencies((String)jad.get("RIM-COD-Module-Dependencies"));
         int memoryrequired = codeSize + 131072;
         boolean sufficientMemory = true;
         if (codeSize != 0 && downloadAllowed && missingDependencies == null && !DownloadManager.ensureAvailableFlash(memoryrequired)) {
            sufficientMemory = false;
         }

         this._downloadLabel = (String)jad.get("RIM-Download-Label");
         if (this._downloadLabel == null) {
            String midletName = (String)jad.get("MIDlet-Name");
            if (midletName == null) {
               midletName = "";
            }

            String[] formatArguments = new Object[]{midletName};
            this._downloadLabel = MessageFormat.format(BrowserResources.getString(682), formatArguments);
            this._installLabel = MessageFormat.format(BrowserResources.getString(707), formatArguments);
         } else {
            this._installLabel = this._downloadLabel;
         }

         this.addNameValueField(CommonResources.getResourceBundle(), 2002, (String)jad.get("MIDlet-Name"));
         this.addNameValueField(BrowserResources.getResourceBundle(), 407, (String)jad.get("MIDlet-Version"));
         this.addNameValueField(BrowserResources.getResourceBundle(), 512, (String)jad.get("MIDlet-Vendor"));
         if (numberOfCODfiles > 0 || jad.getJarURL() != null) {
            this.addNameValueField(BrowserResources.getResourceBundle(), 513, FileUtilities.sizeToString(codeSize));
         }

         if (jad.containsKey("MIDlet-Description")) {
            this.addDescriptionField((String)jad.get("MIDlet-Description"));
         }

         this.add((Field)(new Object()));
         if (!downloadAllowed) {
            EditField downloadDisallowedField = (EditField)(new Object(9007199254740992L));
            downloadDisallowedField.setText(BrowserResources.getString(anyDownloadsAllowed ? 816 : 726));
            this.add(downloadDisallowedField);
         } else if (missingDependencies != null) {
            EditField missingDependenciesField = (EditField)(new Object(9007199254740992L));
            missingDependenciesField.setText(
               ((StringBuffer)(new Object()))
                  .append(missingDependencies.indexOf(10) == -1 ? BrowserResources.getString(732) : BrowserResources.getString(731))
                  .append('\n')
                  .append(missingDependencies)
                  .toString()
            );
            this.add(missingDependenciesField);
         } else if (!sufficientMemory) {
            EditField insufficientMemoryField = (EditField)(new Object(9007199254740992L));
            insufficientMemoryField.setText(BrowserResources.getString(521));
            this.add(insufficientMemoryField);
         } else {
            this._permissionsButton = (CheckboxField)(new Object());
            this._permissionsButton.setLabel(BrowserResources.getString(880));
            this._permissionsButton.setChecked(false);
            this.add(this._permissionsButton);
            this._downloadButton = (ButtonField)(new Object(BrowserResources.getString(273), 65536));
            this._downloadButton.setChangeListener(this);
            this._cancelButton = (ButtonField)(new Object(CommonResources.getString(9042), 65536));
            this._cancelButton.setChangeListener(this);
            HorizontalFieldManager buttonContainer = (HorizontalFieldManager)(new Object(12884901888L));
            buttonContainer.add(this._downloadButton);
            buttonContainer.add(this._cancelButton);
            this.add(buttonContainer);
            this._buttonContainer = buttonContainer;
         }
      } else {
         logResult(906, jad != null ? jad.getErrorMessage() : null, false);
         String errorMessage = BrowserResources.getString(502);
         if (jad != null) {
            String errorDetail = jad.getErrorMessage();
            if (errorDetail != null) {
               errorMessage = ((StringBuffer)(new Object())).append(errorMessage).append("\n\n").append(errorDetail).toString();
            }
         }

         EditField invalidJADField = (EditField)(new Object(9007199254740992L));
         invalidJADField.setText(errorMessage);
         this.add(invalidJADField);
      }
   }

   private final boolean isApplicationExcluded() {
      if (isApplicationExcluded((String)this._jad.get("RIM-COD-SHA1"))) {
         return true;
      }

      String sha1AttributePrefix = "RIM-COD-SHA1-";
      int rimCodNumber = 1;

      while (true) {
         String sha1 = (String)this._jad.get(((StringBuffer)(new Object())).append(sha1AttributePrefix).append(rimCodNumber).toString());
         if (sha1 == null) {
            return false;
         }

         if (isApplicationExcluded(sha1)) {
            return true;
         }

         rimCodNumber++;
      }
   }

   private static final boolean isApplicationExcluded(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: ifnull 6a
      // 04: aload 0
      // 05: invokevirtual java/lang/String.trim ()Ljava/lang/String;
      // 08: astore 0
      // 09: bipush 20
      // 0b: newarray 8
      // 0d: astore 1
      // 0e: bipush 0
      // 0f: istore 2
      // 10: bipush 0
      // 11: istore 3
      // 12: iload 3
      // 13: aload 1
      // 14: arraylength
      // 15: if_icmpge 58
      // 18: iload 2
      // 19: ifle 2c
      // 1c: aload 0
      // 1d: iload 2
      // 1e: invokevirtual java/lang/String.charAt (I)C
      // 21: bipush 32
      // 23: if_icmpne 2c
      // 26: iinc 2 1
      // 29: goto 18
      // 2c: aload 0
      // 2d: iload 2
      // 2e: iinc 2 1
      // 31: invokevirtual java/lang/String.charAt (I)C
      // 34: istore 4
      // 36: aload 0
      // 37: iload 2
      // 38: iinc 2 1
      // 3b: invokevirtual java/lang/String.charAt (I)C
      // 3e: istore 5
      // 40: aload 1
      // 41: iload 3
      // 42: iload 4
      // 44: invokestatic net/rim/device/api/util/NumberUtilities.hexDigitToInt (C)I
      // 47: bipush 4
      // 49: ishl
      // 4a: iload 5
      // 4c: invokestatic net/rim/device/api/util/NumberUtilities.hexDigitToInt (C)I
      // 4f: ior
      // 50: i2b
      // 51: bastore
      // 52: iinc 3 1
      // 55: goto 12
      // 58: iload 2
      // 59: aload 0
      // 5a: invokevirtual java/lang/String.length ()I
      // 5d: if_icmpne 6a
      // 60: aload 1
      // 61: bipush 1
      // 62: invokestatic net/rim/device/internal/applicationcontrol/ApplicationControl.isFlagSetBoolean ([BI)Z
      // 65: ireturn
      // 66: astore 1
      // 67: bipush 0
      // 68: ireturn
      // 69: astore 1
      // 6a: bipush 0
      // 6b: ireturn
      // try (2 -> 55): 56 null
      // try (2 -> 55): 59 null
   }

   private final String getMissingDependencies(String moduleDependencies) {
      if (moduleDependencies != null) {
         int[] moduleHandles = null;
         StringBuffer missingModules = null;
         String module = null;
         int numCods = this._jad.getCodUrlCount();
         StringTokenizer tokenizer = (StringTokenizer)(new Object(moduleDependencies, ','));

         label83:
         while (tokenizer.hasMoreTokens()) {
            module = tokenizer.nextToken().trim();

            for (int i = numCods - 1; i >= 0; i--) {
               String cod = this._jad.getURLbyNumber(i);
               if (cod != null) {
                  int queryString = cod.indexOf(63);
                  if (queryString != -1) {
                     cod = cod.substring(0, queryString);
                  }

                  int startOfModuleName = cod.lastIndexOf(47) + 1;
                  if (startOfModuleName < cod.length()) {
                     int endOfModuleName = cod.indexOf(46, startOfModuleName);
                     if (endOfModuleName == -1) {
                        endOfModuleName = cod.length();
                     }

                     if (endOfModuleName - startOfModuleName == module.length()
                        && StringUtilities.regionMatches(cod, true, startOfModuleName, module, 0, endOfModuleName - startOfModuleName, 1701707776)) {
                        continue label83;
                     }
                  }
               }
            }

            if (CodeModuleManager.getModuleHandle(module) == 0) {
               if (moduleHandles == null) {
                  moduleHandles = CodeModuleManager.getModuleHandles();
               }

               String alias = null;
               int aliasIndex = 0;

               for (int i = moduleHandles.length - 1; i >= 0; i--) {
                  aliasIndex = 0;

                  while (true) {
                     alias = CodeModuleManager.getModuleAliasName(moduleHandles[i], aliasIndex);
                     if (alias == null) {
                        break;
                     }

                     if (alias.equals(module)) {
                        continue label83;
                     }

                     aliasIndex++;
                  }
               }

               if (missingModules == null) {
                  missingModules = (StringBuffer)(new Object(module));
               } else {
                  missingModules.append('\n');
                  missingModules.append(module);
               }
            }
         }

         if (missingModules != null) {
            return missingModules.toString();
         }
      }

      return null;
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      this._isDownloading = false;
   }

   @Override
   protected final void onUndisplay() {
      super.onUndisplay();
      if (!this._isDownloading && this._jad != null) {
         new ApplicationDownloadManager(null, null, this._jad, null, null).sendStatusReport(902);
      }
   }

   @Override
   public final void setFocus() {
      if (this._downloadButton != null) {
         this._downloadButton.setFocus();
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      boolean altPressed = (status & 1) != 0;
      if (!this._isDownloading) {
         Field activeField = this.getLeafFieldWithFocus();
         if (activeField != this._downloadButton && activeField != this._cancelButton) {
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
               this.fieldChanged(this._cancelButton, 0);
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      }
   }

   public final void doDownload() {
      if (!this._isDownloading) {
         boolean downloadAuthorized = true;
         if (Security.getInstance().getPasswordRequiredForAppInstall()) {
            String passwordPrompt = MessageFormat.format(BrowserResources.getString(879), new Object[]{this._jad.get("MIDlet-Name")});
            downloadAuthorized = SecurityDialog.challengeUser(passwordPrompt, false, true, '\u0000', true);
         }

         if (downloadAuthorized) {
            this._isDownloading = true;
            int totalSize = this._jad.totalSize();
            if (totalSize == 0) {
               totalSize = 64000;
            }

            if (this._jad.getCodUrlCount() <= 0) {
               totalSize *= 4;
            }

            ApplicationPermissions permissions = null;
            if (this._permissionsButton.getChecked()) {
               permissions = ApplicationPermissionsManager.getInstance().invokePermissionsRequest((String)this._jad.get("MIDlet-Name"));
            }

            this.delete(this._buttonContainer);
            this.delete(this._permissionsButton);
            this._statusGauge = (GaugeField)(new Object(null, 0, totalSize, 0, 2));
            VerticalFieldManager vfm = (VerticalFieldManager)(new Object());
            vfm.add(this._statusText = (RichTextField)(new Object(this._downloadLabel, 36028797018963968L)));
            vfm.add(this._statusGauge);
            this._statusButton = (ButtonField)(new Object(CommonResources.getString(9042), 12884967424L));
            this._statusButton.setChangeListener(this);
            vfm.add(this._statusButton);
            this._downloadThread = new ApplicationDownloadManager(this._renderingApp, this, this._jad, this._jarStream, null);
            this._downloadThread.setDrmProtected(this._drmProtected);
            this._downloadThread.setApplicationDownloadPermissions(permissions);
            this._downloadScreen = new JADBrowserField$1(this, vfm);
            UiApplication.getUiApplication().pushScreen(this._downloadScreen);
         }
      }
   }

   @Override
   public final void progressUpdate(int totalSizeDownloaded, int progressAction) {
      synchronized (this._application.getAppEventLock()) {
         int maxSize = this._statusGauge.getValueMax();
         if (totalSizeDownloaded > maxSize) {
            totalSizeDownloaded = maxSize;
         }

         this._statusGauge.setValue(totalSizeDownloaded);
         if (progressAction != this._currentProgressAction) {
            this._currentProgressAction = progressAction;
            this._statusText.setText(progressAction == 0 ? this._downloadLabel : this._installLabel);
         }
      }
   }

   private static final void logResult(int statusCode, String errorDetail, boolean rebootRequired) {
      StringBuffer logMessage = (StringBuffer)(new Object("JAD "));
      logMessage.append(statusCode);
      if (errorDetail != null) {
         logMessage.append(' ');
         logMessage.append(errorDetail);
      }

      if (statusCode == 909) {
         logMessage.append(", vendor ");
         logMessage.append(Branding.getVendorId());
         logMessage.append(", hwid 0x");
         logMessage.append(Integer.toHexString(InternalServices.getHardwareID()));
      }

      if (rebootRequired) {
         logMessage.append(", reboot");
      }

      EventLogger.logEvent(1907089860548946979L, logMessage.toString().getBytes());
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void finishedDownload(int otaStatus, String error, boolean rebootRequired) {
      logResult(otaStatus, error, rebootRequired);
      Asserts.productionStateAssert(!Application.isEventDispatchThread());
      this._statusGauge.setValue(this._statusGauge.getValueMax());
      synchronized (this._application.getAppEventLock()) {
         this._application.popScreen(this._downloadScreen);
      }

      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor application = this.informUser(otaStatus, error, rebootRequired);
      boolean goBackInBackground = false;
      if (application != null) {
         label39:
         try {
            applicationManager.runApplication(application);
            goBackInBackground = true;
         } catch (Throwable var12) {
            RunnableDialog rd = (RunnableDialog)(new Object(ame.getMessage(), 0));
            this._application.invokeAndWait(rd);
            break label39;
         }
      }

      this.goBack(goBackInBackground);
   }

   @Override
   public final boolean performUpgrade() {
      return this.performUpgrade(null, null);
   }

   @Override
   public final boolean performUpgrade(String existingVersion, String newVersion) {
      String label = BrowserResources.getString(648);
      if (existingVersion != null && newVersion != null) {
         String name = (String)this._jad.get("MIDlet-Name");
         label = MessageFormat.format(BrowserResources.getString(688), new Object[]{name, existingVersion, newVersion});
      }

      RunnableDialog rd = (RunnableDialog)(new Object(3, label, null, null, -1));
      this._application.invokeAndWait(rd);
      return rd.getResult() == 4;
   }

   @Override
   public final boolean installUnsigned() {
      String label = BrowserResources.getString(788);
      RunnableDialog rd = (RunnableDialog)(new Object(3, label, null, null, 4));
      this._application.invokeAndWait(rd);
      return rd.getResult() == 4;
   }

   @Override
   public final boolean overwriteModule(String newGroupName, String oldModuleName, CodeModuleGroup oldGroup) {
      String label = BrowserResources.getString(814);
      if (newGroupName != null && oldModuleName != null) {
         if (oldGroup == null) {
            label = MessageFormat.format(BrowserResources.getString(813), new Object[]{newGroupName, oldModuleName});
         } else {
            label = MessageFormat.format(BrowserResources.getString(812), new Object[]{newGroupName, oldModuleName, oldGroup.getFriendlyName()});
         }
      }

      RunnableDialog rd = (RunnableDialog)(new Object(3, label, null, null, -1));
      this._application.invokeAndWait(rd);
      return rd.getResult() == 4;
   }

   @Override
   public final boolean newerModule(String newGroupName, String oldModuleName, String oldGroupName) {
      String label = MessageFormat.format(BrowserResources.getString(877), new Object[]{newGroupName, oldModuleName, oldGroupName});
      RunnableDialog rd = (RunnableDialog)(new Object(3, label, null, null, -1));
      this._application.invokeAndWait(rd);
      return rd.getResult() == 4;
   }

   @Override
   public final boolean removeRecordStores() {
      String label = BrowserResources.getString(910);
      RunnableDialog rd = (RunnableDialog)(new Object(3, label, null, null, 4));
      this._application.invokeAndWait(rd);
      return rd.getResult() == -1;
   }

   private final ApplicationDescriptor informUser(int otaStatus, String error, boolean rebootRequired) {
      String label = null;
      Image icon = null;
      String diagnostic = null;
      ApplicationDescriptor application = null;
      Object[] choices;
      int defaultChoice;
      if (otaStatus == 902) {
         choices = new Object[]{CommonResources.getString(117)};
         defaultChoice = 0;
         label = BrowserResources.getString(684);
         icon = ThemeManager.getThemeAwareImage("dialog_exclamation");
      } else if (otaStatus == 900) {
         if (rebootRequired) {
            choices = new Object[]{BrowserResources.getString(559), BrowserResources.getString(553)};
            defaultChoice = 0;
            label = BrowserResources.getString(560);
            icon = ThemeManager.getThemeAwareImage("dialog_information");
         } else {
            IntVector loadedModules = this._downloadThread.getLoadedModules();
            if (loadedModules != null) {
               for (int i = 0; i < loadedModules.size() && application == null; i++) {
                  ApplicationDescriptor[] ads = CodeModuleManager.getApplicationDescriptors(loadedModules.elementAt(i));
                  if (ads != null) {
                     for (int j = 0; j < ads.length; j++) {
                        if ((ads[j].getFlags() & 3) == 0) {
                           application = ads[j];
                           break;
                        }
                     }
                  }
               }
            }

            if (application != null) {
               choices = new Object[]{CommonResources.getString(117), BrowserResources.getString(683)};
            } else {
               choices = new Object[]{CommonResources.getString(117)};
            }

            label = BrowserResources.getString(532);
            icon = ThemeManager.getThemeAwareImage("dialog_information");
            defaultChoice = 0;
         }
      } else {
         choices = new Object[]{CommonResources.getString(117), CommonResources.getString(9046)};
         defaultChoice = 0;
         label = BrowserResources.getString(685);
         icon = ThemeManager.getThemeAwareImage("dialog_exclamation");
         StringBuffer buffer = (StringBuffer)(new Object());
         buffer.append(otaStatus);
         buffer.append(' ');
         buffer.append(OTAStatusReportSender.getStatusMessage(otaStatus, this._jad != null && this._jad.getCodUrlCount() > 0));
         if (error != null) {
            buffer.append('\n');
            buffer.append(error);
         }

         diagnostic = buffer.toString();
      }

      Dialog dialog = (Dialog)(new Object(label, choices, null, defaultChoice, null, 0));
      dialog.setIcon(icon);
      RunnableDialog rd = (RunnableDialog)(new Object(dialog));
      this._application.invokeAndWait(rd);
      int result = rd.getResult();
      if (otaStatus != 900) {
         if (result == 1) {
            choices = new Object[]{CommonResources.getString(117), CommonResources.getString(1800)};
            int var15 = 0;
            rd = (RunnableDialog)(new Object(0, diagnostic, choices, null, var15));
            this._application.invokeAndWait(rd);
            result = rd.getResult();
            if (result == 1) {
               Clipboard.getClipboard().put(diagnostic);
               return null;
            }
         }
      } else if (rebootRequired) {
         if (result == 1) {
            InternalServices.initiateReset("OTA");
            return null;
         }
      } else if (application != null && result == 1) {
         return application;
      }

      return null;
   }

   private final void goBack(boolean inBackground) {
      this._renderingApp.eventOccurred((Event)(new Object(this)));
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._downloadButton) {
         this.doDownload();
      } else {
         if (field == this._cancelButton) {
            if (!this._isDownloading) {
               this.goBack(false);
               return;
            }

            if (this._downloadThread != null) {
               this._downloadThread.abort();
               return;
            }
         } else if (field == this._statusButton && this._isDownloading && this._downloadThread != null) {
            this._downloadThread.abort();
         }
      }
   }
}
