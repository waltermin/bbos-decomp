package net.rim.device.apps.internal.browser.wml;

import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.FindVerbManager;
import net.rim.device.apps.internal.browser.core.IBrowserContext;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.page.Renderer;

final class WMLBrowserContent extends BrowserContentImpl {
   private WMLBrowserField _wmlBrowserField;
   private FindVerbManager _findVerbManager;
   private WMLScriptBrowserContent _wmlScriptBrowserContent;

   public WMLBrowserContent(Renderer renderer, String url, RenderingApplication renderingApplication, RenderingOptions renderingOptions, int flags) {
      super(renderer, url, null, renderingApplication, renderingOptions, flags);
   }

   @Override
   public final void setContent(Field content) {
      if (!(content instanceof WMLBrowserField)) {
         throw new RuntimeException("field must be WMLBrowserField");
      }

      super.setContent(content);
      this._wmlBrowserField = (WMLBrowserField)content;
      this._findVerbManager = new FindVerbManager(this._wmlBrowserField);
   }

   @Override
   public final String getTitle() {
      return this._wmlBrowserField != null ? this._wmlBrowserField.getTitle() : null;
   }

   @Override
   public final boolean isModified() {
      return this._wmlBrowserField.isModified();
   }

   @Override
   public final boolean savesContext() {
      return false;
   }

   @Override
   public final boolean includeInPageCache() {
      return false;
   }

   public final WMLBrowserField getWMLBrowserField() {
      return this._wmlBrowserField;
   }

   @Override
   public final IBrowserContext getContext() {
      return this._wmlBrowserField.getContext();
   }

   @Override
   public final boolean submit(boolean validate, IBrowserContext context) {
      return this._wmlBrowserField.submit(validate, context);
   }

   @Override
   protected final String getDecompilerName() {
      return "WMLD";
   }

   @Override
   public final boolean showImages() {
      return super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 6, true);
   }

   @Override
   public final Verb[] getFindVerb() {
      return !super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 39, false) ? this._findVerbManager.getVerbs() : null;
   }

   @Override
   public final Object invokeFind(boolean findNext, Object context) {
      return !super._renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 39, false) ? this._findVerbManager.invokeFind(findNext, context) : null;
   }

   @Override
   public final boolean jumpToPosition(long value) {
      synchronized (Application.getEventLock()) {
         this._wmlBrowserField.setFocus(0, (int)value, 0);
         return false;
      }
   }

   final void setWMLScriptBrowserContent(WMLScriptBrowserContent wmlScriptBrowserContent) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final synchronized void haltScripts() {
      if (this._wmlScriptBrowserContent != null) {
         this._wmlScriptBrowserContent.halt();
         this._wmlScriptBrowserContent = null;
      }
   }
}
