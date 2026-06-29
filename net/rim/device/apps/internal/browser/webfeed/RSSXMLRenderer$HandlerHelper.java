package net.rim.device.apps.internal.browser.webfeed;

import net.rim.device.api.browser.field.BrowserContentChangedEvent;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

final class RSSXMLRenderer$HandlerHelper extends DefaultHandler {
   private final RSSXMLRenderer this$0;

   private RSSXMLRenderer$HandlerHelper(RSSXMLRenderer _1) {
      this.this$0 = _1;
   }

   @Override
   public final void startElement(String uri, String localName, String qName, Attributes attributes) {
      switch (this.this$0.getTag(localName)) {
         case 0:
            break;
         case 1:
         default:
            this.this$0._currentItem = new WebFeedItem();
            if (this.this$0._contentRead != null) {
               this.this$0._contentRead.setItemsRead(this.this$0._pipePosition._numRead);
               RSSXMLRenderer.access$500(this.this$0).eventOccurred(this.this$0._contentRead);
            }

            this.this$0._currentItem.setBaseUrl(this.this$0._browserContent.getURL());
            break;
         case 2:
            int top = this.this$0.top();
            if (top == 1) {
               this.this$0._append = true;
            } else if (top == -1 || top == 0) {
               this.this$0._append = true;
            }
            break;
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
            if (this.this$0.top() == 1) {
               this.this$0._append = true;
            }
            break;
         case 8:
            if (this.this$0._currentItem != null && attributes != null) {
               String url = attributes.getValue("url");
               if (url != null) {
                  String type = attributes.getValue("type");
                  if (type != null) {
                     String sizeStr = attributes.getValue("length");
                     if (sizeStr != null) {
                        label54:
                        try {
                           int size = Integer.parseInt(sizeStr);
                           if (size > 0) {
                              this.this$0._currentItem.setEnclosure(url, size, type);
                           }
                        } finally {
                           break label54;
                        }
                     }
                  }
               }
            }
      }

      this.this$0._itemStack.push(localName);
   }

   @Override
   public final void characters(char[] ch, int start, int length) {
      if (this.this$0._append) {
         this.this$0._buffer.append(ch, start, length);
      }
   }

   @Override
   public final void endElement(String uri, String localName, String qName) {
      this.this$0._itemStack.pop();
      switch (this.this$0.getTag(localName)) {
         case 0:
            break;
         case 1:
         default:
            if (this.this$0._currentItem != null) {
               this.this$0._webFeedField.addItem(this.this$0._currentItem);
               this.this$0._currentItem = null;
               return;
            }
            break;
         case 2:
            int top = this.this$0.top();
            if (top == 1) {
               if (this.this$0._currentItem != null) {
                  String title = this.this$0.getCurrentString();
                  this.this$0._charEntities.parse(title.toCharArray(), 0, title.length());
                  this.this$0._currentItem.setTitle(this.this$0._charEntities.getResult());
               }

               this.this$0._append = false;
               return;
            }

            if (top != -1 && top != 0) {
               break;
            }

            String title = this.this$0.getCurrentString();
            this.this$0._browserContent.setTitle(title);
            if (RSSXMLRenderer.access$1400(this.this$0) != null) {
               BrowserContentChangedEvent event = (BrowserContentChangedEvent)(new Object(this.this$0._browserContent));
               RSSXMLRenderer.access$1500(this.this$0).eventOccurred(event);
            }

            this.this$0._append = false;
            return;
         case 3:
            if (this.this$0.top() == 1) {
               this.this$0._append = false;
               if (this.this$0._currentItem != null) {
                  this.this$0._currentItem.setLink(this.this$0.getCurrentString());
                  return;
               }
            }
            break;
         case 4:
            if (this.this$0.top() == 1) {
               this.this$0._append = false;
               if (this.this$0._currentItem != null) {
                  this.this$0._currentItem.setDescription(this.this$0.getCurrentString());
                  return;
               }
            }
            break;
         case 5:
         case 6:
            if (this.this$0.top() == 1) {
               this.this$0._append = false;
               if (this.this$0._currentItem != null) {
                  this.this$0._currentItem.setPubDate(this.this$0.getCurrentString());
                  return;
               }
            }
            break;
         case 7:
            if (this.this$0.top() == 1) {
               this.this$0._append = false;
               if (this.this$0._currentItem != null) {
                  this.this$0._currentItem.setGuid(this.this$0.getCurrentString());
               }
            }
      }
   }

   RSSXMLRenderer$HandlerHelper(RSSXMLRenderer x0, RSSXMLRenderer$1 x1) {
      this(x0);
   }
}
