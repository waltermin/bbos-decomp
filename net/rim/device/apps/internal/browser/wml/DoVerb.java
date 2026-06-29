package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.internal.i18n.CommonResource;

final class DoVerb extends TaskContainer {
   private String _cardId;
   private String _type;
   private WMLVariable _label;
   private String _name;
   private boolean _optional;
   private WMLBrowserContent _browserContent;
   private boolean _templateLevel;

   DoVerb(String cardId, WMLBrowserContent browserContent, boolean templateLevel) {
      this(cardId, browserContent, 340005, templateLevel);
   }

   DoVerb(String cardId, WMLBrowserContent browserContent, int menuOrdering, boolean templateLevel) {
      super(menuOrdering);
      this._cardId = cardId;
      this._browserContent = browserContent;
      this._type = WMLConstants.STRING_ACCEPT;
      this._templateLevel = templateLevel;
   }

   DoVerb(DoVerb doVerb) {
      super(doVerb);
      this._cardId = doVerb._cardId;
      this._type = doVerb._type;
      this._label = doVerb._label;
      this._name = doVerb._name;
      this._optional = doVerb._optional;
      this._browserContent = doVerb._browserContent;
   }

   final void setOrdering(int ordering) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final TaskContainer clone() {
      return new DoVerb(this);
   }

   @Override
   public final String toString() {
      if (this._label != null) {
         return this._label.getName();
      } else if (this._type.equals(WMLConstants.STRING_ACCEPT)) {
         return BrowserResources.getString(150);
      } else if (this._type.equals(WMLConstants.STRING_PREV)) {
         return CommonResources.getString(9033);
      } else if (this._type.equals(WMLConstants.STRING_HELP)) {
         return CommonResources.getString(9034);
      } else if (this._type.equals(WMLConstants.STRING_RESET)) {
         return BrowserResources.getString(546);
      } else if (this._type.equals(WMLConstants.STRING_OPTIONS)) {
         return CommonResource.getString(20);
      } else {
         return this._type.equals(WMLConstants.STRING_DELETE) ? CommonResources.getString(1000) : this._type;
      }
   }

   final void setName(String name) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setLabel(WMLVariable label) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setType(String type) {
      this._type = type;
      if (this._type != null && this._type.equals(WMLConstants.STRING_ACCEPT)) {
         super._ordering = 339968;
      } else if (this._optional) {
         super._ordering = 340048;
      } else {
         super._ordering = 340005;
      }
   }

   final void setOptional(boolean optional) {
      this._optional = optional;
      if (super._ordering != 339968) {
         super._ordering = 340048;
      }
   }

   @Override
   final void setBrowserContent(WMLBrowserContent browserContent) {
      super.setBrowserContent(browserContent);
      this._browserContent = browserContent;
   }

   final void setCardId(String cardId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final String getType() {
      return this._type;
   }

   final String getLabel() {
      return this._label != null ? this._label.getName() : this.toString();
   }

   final String getName() {
      return this._name != null ? this._name : this._type;
   }

   @Override
   final void setTask(Task task) {
      task.setVerbLabel(this._label);
      super.setTask(task);
      if (task instanceof Prev) {
         super._ordering = 1180416;
      }
   }

   final BrowserContentImpl getBrowserContent() {
      return this._browserContent;
   }

   public final boolean isTemplateLevel() {
      return this._templateLevel;
   }
}
