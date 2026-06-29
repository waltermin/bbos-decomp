package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class WMLAnchorVerb extends TaskContainer {
   private String _defaultTitle = BrowserResources.getString(100);
   private WMLVariable _title;
   private int _startOffset;
   private int _endOffset;
   private char _accessKey;
   private Verb _verb;
   private boolean _showTitleOnly;

   WMLAnchorVerb(int start) {
      super(341248);
      this._startOffset = start;
   }

   WMLAnchorVerb(WMLAnchorVerb wmlAnchorVerb) {
      super(wmlAnchorVerb);
      this._title = wmlAnchorVerb._title;
      this._startOffset = wmlAnchorVerb._startOffset;
      this._endOffset = wmlAnchorVerb._endOffset;
      this._accessKey = wmlAnchorVerb._accessKey;
   }

   final void setVerb(Verb verb) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final Verb getVerb() {
      return this._verb;
   }

   final void setTitle(WMLVariable title, boolean showTitleOnly) {
      this._title = title;
      this._showTitleOnly = showTitleOnly;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._verb != null) {
         if (context == null) {
            context = new ContextObject();
         }

         Verb[] verbArray = new Verb[]{this._verb};
         ContextObject.put(context, 666175809445784644L, verbArray);
      }

      Object result = super.invoke(context);
      if (this._verb != null) {
         ContextObject.remove(context, 666175809445784644L);
      }

      return result;
   }

   final void setAccessKey(char key) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int getStartOffset() {
      return this._startOffset;
   }

   final int getEndOffset() {
      return this._endOffset;
   }

   final void setStartOffset(int startOffset) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setEndOffset(int endOffset) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final int getVerbGroupId() {
      Verb verb = this.getVerb();
      return verb != null ? verb.getVerbGroupId() : super.getVerbGroupId();
   }

   @Override
   public final int getOrdering() {
      Verb verb = this.getVerb();
      return verb != null ? verb.getOrdering() : super.getOrdering();
   }

   @Override
   public final String toString() {
      Verb verb = this.getVerb();
      if (verb != null) {
         return verb.toString();
      }

      String title = null;
      if (this._title != null) {
         title = this._title.getName();
      }

      if (title == null || title.length() == 0) {
         return this._defaultTitle;
      }

      if (this._showTitleOnly) {
         return title;
      }

      StringBuffer temp = new StringBuffer(title);
      temp.append(" (");
      temp.append(BrowserResources.getString(100));
      temp.append(')');
      return temp.toString();
   }

   final TaskContainer clone() {
      return new WMLAnchorVerb(this);
   }
}
