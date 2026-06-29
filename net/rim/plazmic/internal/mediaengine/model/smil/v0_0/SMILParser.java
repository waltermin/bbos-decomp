package net.rim.plazmic.internal.mediaengine.model.smil.v0_0;

import java.io.InputStream;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Display;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;
import net.rim.plazmic.internal.mediaengine.dataformat.Colors;
import net.rim.plazmic.internal.mediaengine.dataformat.Units;
import net.rim.plazmic.internal.mediaengine.event.Event;
import net.rim.plazmic.internal.mediaengine.event.EventLogic;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.MediaObject;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.Par;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.Seq;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimeContainer;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.timing.TimingObject;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.util.DOMUtilities;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.util.IdMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SMILParser {
   private int _configuration;
   private Event _triggerEvent;
   private Event _resultingEvent;
   private IdMap _idMap = new IdMap();
   private SMILTimingParser _timingParser = new SMILTimingParser();
   private int _horizontalScaleFactor;
   private int _verticalScaleFactor;
   public static final int CONFIGURATION_SMIL;
   public static final int CONFIGURATION_MMS;
   private static final int LEFT;
   private static final int RIGHT;
   private static final int WIDTH;
   private static final int TOP;
   private static final int BOTTOM;
   private static final int HEIGHT;
   private static final String TEXT;

   public SMILParser() {
      this._triggerEvent = (Event)(new Object());
      this._resultingEvent = (Event)(new Object());
      this._horizontalScaleFactor = 65536;
      this._verticalScaleFactor = 65536;
   }

   public Document parseDocument(InputStream in) {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setAllowUndefinedNamespaces(true);
      DocumentBuilder parser = factory.newDocumentBuilder();
      return parser.parse(in);
   }

   public void setConfiguration(int configuration) {
      switch (configuration) {
         case -1:
            throw new Object("Unsupported Parser Configuration");
         case 0:
         case 1:
         default:
            this._configuration = configuration;
      }
   }

   public SMILModel parse(Document dom) {
      SMILModel model = new SMILModel();
      this.parse(dom, model);
      return model;
   }

   protected void parse(Document dom, SMILModel model) {
      Element smil = dom.getDocumentElement();
      this.parseHead(smil, model);
      this.parseBody(smil, model);
   }

   protected void parseHead(Element smil, SMILModel model) {
      Element head = this.findChildElement(smil, "head");
      if (head != null) {
         this.parseMetaData(head, model);
         this.parseLayout(head, model);
      } else {
         Element layout = smil;
         this.parseRootLayout(layout, model);
         this.parseRegions(layout, model);
         this.setupDefaultRegions(model);
      }
   }

   protected void parseMetaData(Element head, SMILModel model) {
      NodeList metaData = head.getElementsByTagName("meta");

      for (int i = 0; i < metaData.getLength(); i++) {
         Element meta = (Element)metaData.item(i);
         String name = DOMUtilities.getAttribute(meta, "name");
         String content = DOMUtilities.getAttribute(meta, "content");
         if (name.length() > 0) {
            model.addMetaData(name, content);
         }
      }
   }

   protected void parseLayout(Element head, SMILModel model) {
      Element layout = this.findChildElement(head, "layout");
      if (layout != null) {
         this.parseRootLayout(layout, model);
         this.parseRegions(layout, model);
      } else {
         throw new Object("No layout found");
      }
   }

   protected void parseRootLayout(Element layout, SMILModel model) {
      Element rootLayout = this.findChildElement(layout, "root-layout");
      Region rl = new Region(163624922399113216L);
      int rootLayoutWidth = Display.getWidth();
      int rootLayoutHeight = Display.getHeight();
      int backgroundColor = -1;
      if (rootLayout != null) {
         if (rootLayout.hasAttribute("width")) {
            if (this._configuration == 1) {
               int width = Units.getLength(rootLayout.getAttribute("width"));
               if (rootLayoutWidth < width) {
                  this._horizontalScaleFactor = Fixed32.div(Fixed32.toFP(rootLayoutWidth), Fixed32.toFP(width));
               } else {
                  rootLayoutWidth = width;
               }
            } else {
               rootLayoutWidth = Units.getLength(rootLayout.getAttribute("width"));
            }
         }

         if (rootLayout.hasAttribute("height")) {
            if (this._configuration == 1) {
               int height = Units.getLength(rootLayout.getAttribute("height"));
               if (rootLayoutHeight < height) {
                  this._verticalScaleFactor = Fixed32.div(Fixed32.toFP(rootLayoutHeight), Fixed32.toFP(height));
               } else {
                  rootLayoutHeight = height;
               }
            } else {
               rootLayoutHeight = Units.getLength(rootLayout.getAttribute("height"));
            }
         }

         backgroundColor = this.parseBackgroundColor(rootLayout);
      }

      rl.setWidth(rootLayoutWidth, false);
      rl.setHeight(rootLayoutHeight, false);
      rl.setBackgroundColor(backgroundColor);
      model.setRootLayout(rl);
   }

   protected void parseRegions(Element layout, SMILModel model) {
      NodeList regions = layout.getElementsByTagName("region");

      for (int i = 0; i < regions.getLength(); i++) {
         this.parseRegion((Element)regions.item(i), model);
      }
   }

   protected void parseRegion(Element region, SMILModel model) {
      String regionName = DOMUtilities.getAttribute(region, "regionName");
      int id = regionName.length() == 0 ? this.getIntId(region) : this._idMap.getId(regionName);
      long style = 18014398509481984L;
      int fitStyle = 0;
      String fit = DOMUtilities.getAttribute(region, "fit");
      if (fit.equals("scroll")) {
         style |= 145610523889631232L;
         fitStyle = 8;
      } else if (fit.equals("fill")) {
         fitStyle = 1;
      } else if (fit.equals("meet")) {
         fitStyle = 4;
      } else if (fit.equals("slice")) {
         fitStyle = 2;
      }

      if (this._configuration == 1
         && (this._horizontalScaleFactor != 65536 || this._verticalScaleFactor != 65536)
         && (regionName.equals("Text") || regionName.length() == 0 && region.getAttribute("id").equals("Text"))) {
         style |= 145610523889631232L;
         fitStyle = 8;
      }

      Region targetRegion = new Region(style);
      targetRegion.setFit(fitStyle);
      this.parseRegionDimension(region, "left", 0, targetRegion);
      this.parseRegionDimension(region, "right", 1, targetRegion);
      this.parseRegionDimension(region, "width", 2, targetRegion);
      this.parseRegionDimension(region, "top", 3, targetRegion);
      this.parseRegionDimension(region, "bottom", 4, targetRegion);
      this.parseRegionDimension(region, "height", 5, targetRegion);
      int backgroundColor = this.parseBackgroundColor(region);
      targetRegion.setBackgroundColor(backgroundColor);
      model.addRegion(id, targetRegion);
      model.getRootLayout().add(targetRegion);
   }

   protected void setupDefaultRegions(SMILModel model) {
      Region imageRegion = new Region(18014398509481984L);
      imageRegion.setFit(4);
      int imageID = this._idMap.getId("Text");
      model.addRegion(imageID, imageRegion);
      model.getRootLayout().add(imageRegion);
      Region textRegion = new Region(163624922399113216L);
      textRegion.setFit(8);
      int textID = this._idMap.getId("Image");
      model.addRegion(textID, textRegion);
      model.getRootLayout().add(textRegion);
   }

   protected int parseBackgroundColor(Element element) {
      int color = -1;
      if (element.hasAttribute("backgroundColor")) {
         return Colors.getColor(DOMUtilities.getAttribute(element, "backgroundColor"));
      }

      if (element.hasAttribute("background-color")) {
         color = Colors.getColor(DOMUtilities.getAttribute(element, "background-color"));
      }

      return color;
   }

   protected void parseRegionDimension(Element regionElement, String dimensionAttributeName, int dimensionType, Region region) {
      if (regionElement.hasAttribute(dimensionAttributeName)) {
         String attr = regionElement.getAttribute(dimensionAttributeName);
         boolean isPercent = Units.unitIsPercentage(attr);
         int dimension = isPercent ? Fixed32.div(Units.getPercentage(attr), Fixed32.toFP(100)) : Units.getLength(attr, Integer.MAX_VALUE);
         switch (dimensionType) {
            case -1:
               break;
            case 0:
            default:
               region.setLeft(dimension, isPercent);
               return;
            case 1:
               region.setRight(dimension, isPercent);
               return;
            case 2:
               dimension = this.scaleDimension(dimension, this._horizontalScaleFactor, isPercent);
               region.setWidth(dimension, isPercent);
               return;
            case 3:
               dimension = this.scaleDimension(dimension, this._verticalScaleFactor, isPercent);
               region.setTop(dimension, isPercent);
               return;
            case 4:
               dimension = this.scaleDimension(dimension, this._verticalScaleFactor, isPercent);
               region.setBottom(dimension, isPercent);
               return;
            case 5:
               dimension = this.scaleDimension(dimension, this._verticalScaleFactor, isPercent);
               region.setHeight(dimension, isPercent);
         }
      }
   }

   protected int scaleDimension(int dimension, int scaleFactor, boolean isPercent) {
      if (this._configuration == 1 && !isPercent && scaleFactor != 65536) {
         dimension = (int)((long)dimension * scaleFactor + 32768 >> 16);
      }

      return dimension;
   }

   protected void parseBody(Element smil, SMILModel model) {
      Element body = this.findChildElement(smil, "body");
      if (body != null) {
         EventLogic logic = (EventLogic)(new Object());
         int id = this.getIntId(body);
         Seq seq = new Seq(id, null);
         model.addTimingObject(id, seq);
         Event seqEvent = (Event)(new Object());
         seqEvent._event = 1;
         seqEvent._eventParam = id;
         model.setStartEvent(seqEvent);
         this.parseChildren(body, seq, logic, model);
         seq.setImplicitDuration(true);
         seq.setEndLogic(logic);
         model.setEventLogic(logic);
      } else {
         throw new Object("No body element");
      }
   }

   protected void parseTimeContainerMediaObject(Element element, TimeContainer parent, EventLogic logic, SMILModel model) {
      String tagName = element.getTagName();
      int id = this.getIntId(element);
      if ("par".equals(tagName)) {
         Par par = new Par(id, parent);
         model.addTimingObject(id, par);
         this.parseParAttributes(element, par, parent, logic);
         this.parseChildren(element, par, logic, model);
         par.setEndLogic(logic);
         parent.addChildElement(par);
      } else if ("seq".equals(tagName)) {
         Seq seq = new Seq(id, parent);
         model.addTimingObject(id, seq);
         this.parseSeqAttributes(element, seq, parent, logic);
         this.parseChildren(element, seq, logic, model);
         seq.setEndLogic(logic);
         parent.addChildElement(seq);
      } else {
         MediaObject mediaObject = new MediaObject(id, parent);
         model.addTimingObject(id, mediaObject);
         this.parseTimingObjectFields(element, mediaObject, parent, logic);
         this.parseMediaObjectFields(element, mediaObject, logic, model);
         parent.addChildElement(mediaObject);
      }
   }

   protected void parseParAttributes(Element element, Par par, TimeContainer parent, EventLogic logic) {
      this.parseTimingObjectFields(element, par, parent, logic);
   }

   protected void parseSeqAttributes(Element element, Seq seq, TimeContainer parent, EventLogic logic) {
      this.parseTimingObjectFields(element, seq, parent, logic);
   }

   protected void parseTimingObjectFields(Element element, TimingObject timingObject, TimeContainer parent, EventLogic logic) {
      this._timingParser.extractTimingData(element, parent, logic, this._idMap);
      if (element.hasAttribute("dur")) {
         long dur = Units.getTime(DOMUtilities.getAttribute(element, "dur"));
         timingObject.setDur(dur);
         int id = timingObject.getId();
         this._triggerEvent._event = 1;
         this._triggerEvent._eventParam = id;
         this._resultingEvent._event = 2;
         this._resultingEvent._eventParam = id;
         logic.addEventDependancy(this._triggerEvent, this._resultingEvent, dur);
      } else if (!element.hasAttribute("end")) {
         timingObject.setImplicitDuration(true);
      }

      String fillAttr = DOMUtilities.getAttribute(element, "fill");
      int fill;
      if (this._configuration == 1) {
         fill = 1;
      } else if (fillAttr.equals("remove")) {
         fill = 2;
      } else if (fillAttr.equals("freeze")) {
         fill = 1;
      } else {
         fill = this.getDefaultFillValue(element);
      }

      timingObject.setFill(fill);
   }

   protected int getDefaultFillValue(Element element) {
      return !element.hasAttribute("dur") && !element.hasAttribute("end") ? 1 : 2;
   }

   protected void parseMediaObjectFields(Element element, MediaObject mediaObject, EventLogic logic, SMILModel model) {
      String uri = DOMUtilities.getAttribute(element, "src");
      String mimeType = DOMUtilities.getAttribute(element, "type");
      String alt = DOMUtilities.getAttribute(element, "alt");
      String region = DOMUtilities.getAttribute(element, "region");
      mediaObject.setURI(uri);
      mediaObject.setMimeType(mimeType);
      mediaObject.setAlt(alt);
      if (region.length() != 0) {
         mediaObject.setRegion(model.getRegion(this._idMap.getId(region)));
      } else if (element.getTagName().equals("text")) {
         mediaObject.setRegion(model.getRegion(this._idMap.getId("Text")));
      } else if (element.getTagName().equals("img")) {
         mediaObject.setRegion(model.getRegion(this._idMap.getId("Image")));
      }

      mediaObject.realize();
      if (mediaObject.getMediaType() == 1) {
         model.setHasAudio(true);
      }

      if (mediaObject.getMediaType() == 3) {
         model.setHasVideo(true);
      }

      if (mediaObject.isImplicitDuration()) {
         Event result = (Event)(new Object());
         result._event = 2;
         result._eventParam = mediaObject.getId();
         Event trigger = (Event)(new Object());
         trigger._eventParam = mediaObject.getId();
         if (mediaObject.isDiscreteMedia()) {
            trigger._event = 1;
         } else {
            trigger._event = 23;
         }

         logic.addEventDependancy(trigger, result, 0);
      }
   }

   protected void parseChildren(Element element, TimeContainer parent, EventLogic logic, SMILModel model) {
      NodeList children = element.getChildNodes();

      for (int i = 0; i < children.getLength(); i++) {
         Node child = children.item(i);
         if (child instanceof Object) {
            this.parseTimeContainerMediaObject((Element)child, parent, logic, model);
         }
      }
   }

   protected Element findChildElement(Element parent, String tagName) {
      NodeList childNodes = parent.getChildNodes();

      for (int i = 0; i < childNodes.getLength(); i++) {
         Node node = childNodes.item(i);
         if (node instanceof Object && node.getNodeName().equals(tagName)) {
            return (Element)node;
         }
      }

      return null;
   }

   protected int getIntId(Element element) {
      String id = DOMUtilities.getAttribute(element, "id");
      if (id.length() == 0) {
         id = this._idMap.createNewId();
         element.setAttribute("id", id);
      }

      return this._idMap.getId(id);
   }
}
