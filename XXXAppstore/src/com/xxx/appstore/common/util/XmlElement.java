package com.xxx.appstore.common.util;

import com.xxx.appstore.common.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlElement {

   private static final String EQUEL = "=";
   private static final String QUOT = "\"";
   private static final String[] REPLACE = new String[]{"&", "&amp;", "\"", "&quot;", "\'", "&apos;", "<", "&lt;", ">", "&gt;"};
   private static final int REPLACE_LENGTH = (REPLACE.length % 2 == 0)?REPLACE.length:(REPLACE.length - 1);
   private static final String SPACE = " ";
   private static final String TAG_END = "/";
   private static final String TAG_LEFT = "<";
   private static final String TAG_RIGHT = ">";
   static final transient Map<String, String> keys = new HashMap();
   private List<Object> all;
   private Map<String, Object> attributes;
   private Map<String, List<XmlElement>> children;
   private List<XmlElement> childrenList;
   private String name;
   private List<String> text;

   public XmlElement(String var1) {
      this.name = var1;
      this.attributes = new LinkedHashMap();
      this.text = new ArrayList(10);
      this.children = new LinkedHashMap();
      this.childrenList = new ArrayList(30);
      this.all = new ArrayList(100);
   }

   private void addAttributes(Writer var1) throws IOException {
      if(this.attributes != null) {
         Iterator var2 = this.attributes.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.append(" ").append((CharSequence)var3.getKey()).append("=").append("\"");
            this.addText(var1, (String)var3.getValue());
            var1.append("\"");
         }
      }
   }

   private void addTag(Writer var1) throws IOException {
      var1.append("<").append(this.name);
      this.addAttributes(var1);
      var1.append(" ").append("/").append(">");
   }

   private void addTagEnd(Writer var1) throws IOException {
      var1.append("<").append("/").append(this.name).append(">");
   }

   private void addTagStart(Writer var1) throws IOException {
      var1.append("<").append(this.name);
      this.addAttributes(var1);
      var1.append(">");
   }

   private void addText(Writer var1, String var2) throws IOException {
      if(var2 != null) {
         int var3 = 0;

         String var4;
         for(var4 = var2; var3 < REPLACE_LENGTH; var3 += 2) {
            var4 = var4.replace(REPLACE[var3], REPLACE[var3 + 1]);
         }

         var1.append(var4);
      }

   }

   private void addXml(Writer var1, XmlElement var2) throws IOException {
      if(var2.all.size() == 0) {
         var2.addTag(var1);
      } else {
         var2.addTagStart(var1);
         Iterator var3 = var2.all.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if(var4 != null) {
               if(var4 instanceof String) {
                  var2.addText(var1, (String)var4);
               } else {
                  var2.addXml(var1, (XmlElement)var4);
               }
            }
         }

         var2.addTagEnd(var1);
      }

   }

   private static String getKey(String var0) {
      String var1 = (String)keys.get(var0);
      if(var1 == null) {
         keys.put(var0, var0);
         var1 = var0;
      }

      return var1;
   }

   public static XmlElement parseXml(InputStream var0) throws XmlPullParserException, IOException {
      XmlElement var1 = null;
      XmlElement var5;
      if(var0 == null) {
         var5 = null;
      } else {
         XmlPullParserFactory var2 = XmlPullParserFactory.newInstance();
         var2.setNamespaceAware(true);
         XmlPullParser var3 = var2.newPullParser();
         var3.setInput(var0, "UTF-8");

         int var7;
         for(int var4 = var3.getEventType(); var4 != 1; var4 = var7) {
            XmlElement var6;
            if(var4 == 2) {
               var6 = new XmlElement(var3.getName());
               var6.parseXml(var3);
            } else {
               label24: {
                  if(var4 != 0) {
                     try {
                        Utils.W(XmlPullParser.TYPES[var4]);
                     } catch (Throwable var9) {
                        Utils.E("Oh! My God!", var9);
                        var6 = var1;
                        break label24;
                     }
                  }

                  var6 = var1;
               }
            }

            var7 = var3.next();
            var1 = var6;
         }

         var5 = var1;
      }

      return var5;
   }

   private void parseXml(XmlPullParser var1) throws XmlPullParserException, IOException {
      for(int var2 = 0; var2 < var1.getAttributeCount(); ++var2) {
         this.setAttribute(var1.getAttributeName(var2), var1.getAttributeValue(var2));
      }

      for(int var3 = var1.next(); var3 != 1; var3 = var1.next()) {
         if(var3 == 2) {
            XmlElement var4 = new XmlElement(var1.getName());
            this.addChild(var4);
            var4.parseXml(var1);
         }

         if(var3 == 3) {
            break;
         }

         if(var3 == 4) {
            this.addText(var1.getText());
         }
      }

   }

   public XmlElement addAttributes(Map<String, String> var1) {
      if(var1 != null) {
         Iterator var2 = var1.entrySet().iterator();

         while(var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            this.attributes.put(getKey((String)var3.getKey()), var3.getValue());
         }
      }

      return this;
   }

   public XmlElement addChild(XmlElement var1) {
      if(var1 != null && var1.name != null) {
         this.childrenList.add(var1);
         this.all.add(var1);
         if(!this.children.containsKey(var1.name)) {
            this.children.put(getKey(var1.name), new ArrayList(10));
         }

         ((List)this.children.get(var1.name)).add(var1);
      }

      return this;
   }

   public XmlElement addChildren(List<XmlElement> var1) {
      if(var1 != null) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            this.addChild((XmlElement)var2.next());
         }
      }

      return this;
   }

   public XmlElement addText(String var1) {
      this.text.add(var1);
      this.all.add(var1);
      return this;
   }

   public XmlElement clear() {
      this.attributes.clear();
      this.text.clear();
      this.children.clear();
      this.childrenList.clear();
      this.all.clear();
      return this;
   }

   public List<XmlElement> getAllChildren() {
      return this.childrenList;
   }

   public List<String> getAllText() {
      return this.text;
   }

   public List<Object> getAllTextAndChildren() {
      return this.all;
   }

   public String getAttribute(String var1) {
      return (String)this.attributes.get(var1);
   }

   public Map<String, Object> getAttributes() {
      return this.attributes;
   }

   public XmlElement getChild(int var1) {
      XmlElement var2;
      if(this.childrenList.size() > var1) {
         var2 = (XmlElement)this.childrenList.get(var1);
      } else {
         var2 = null;
      }

      return var2;
   }

   public XmlElement getChild(String var1, int var2) {
      List var3 = this.getChildren(var1);
      XmlElement var4;
      if(var3 != null && var3.size() > var2) {
         var4 = (XmlElement)var3.get(var2);
      } else {
         var4 = null;
      }

      return var4;
   }

   public List<XmlElement> getChildren(String var1) {
      return (List)this.children.get(var1);
   }

   public String getName() {
      return this.name;
   }

   public String getText() {
      return this.getText(0);
   }

   public String getText(int var1) {
      String var2;
      if(this.text.size() > var1) {
         var2 = (String)this.text.get(var1);
      } else {
         var2 = "";
      }

      return var2;
   }

   public XmlElement setAttribute(String var1, String var2) {
      this.attributes.put(getKey(var1), var2);
      return this;
   }

   public String toString() {
      StringWriter var1 = new StringWriter();

      try {
         this.writeAsXml(var1);
      } catch (IOException var3) {
         Utils.E("Oh! My God!", var3);
      }

      return var1.toString();
   }

   public void writeAsXml(Writer var1) throws IOException {
      this.addXml(var1, this);
   }
}
