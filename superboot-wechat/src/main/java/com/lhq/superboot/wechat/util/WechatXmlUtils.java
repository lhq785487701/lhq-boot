package com.lhq.superboot.wechat.util;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @Description: 微信支付时的xml转化
 * @author: lihaoqi
 * @date: 2019年6月17日 下午2:48:04
 */
public class WechatXmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(WechatXmlUtils.class);

    /**
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     * @Description: 解析xml, 返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map doXMLParse(String strxml) {

        Map m = new HashMap();

        try {
            strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");

            if ("".equals(strxml)) {
                return null;
            }

            InputStream in = new ByteArrayInputStream(strxml.getBytes(StandardCharsets.UTF_8));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(in);
            Element root = doc.getRootElement();
            List list = root.getChildren();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String k = e.getName();
                String v;
                List children = e.getChildren();
                if (children.isEmpty()) {
                    v = e.getTextNormalize();
                } else {
                    v = WechatXmlUtils.getChildrenText(children);
                }

                m.put(k, v);
            }

            // 关闭流
            in.close();
        } catch (JDOMException | IOException e) {
            logger.error("msg", e);
        }

        return m;
    }

    /**
     * @param children
     * @return String
     * @Description: 获取子结点的xml
     */
    @SuppressWarnings("rawtypes")
    private static String getChildrenText(List children) {
        StringBuilder sb = new StringBuilder();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<").append(name).append(">");
                if (!list.isEmpty()) {
                    sb.append(WechatXmlUtils.getChildrenText(list));
                }
                sb.append(value);
                sb.append("</").append(name).append(">");
            }
        }

        return sb.toString();
    }

    /**
     * @param paramMap
     * @return
     * @Description: 微信支付将请求参数转换为xml格式的String
     */
    @SuppressWarnings("rawtypes")
    public static String getRequestXmlQuery(SortedMap<String, String> paramMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set set = paramMap.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            sb.append("<").append(key).append(">").append("<![CDATA[").append(value).append("]]></").append(key).append(">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * @param paramMap
     * @param isCDATA
     * @return
     * @Description: 微信支付将请求参数转换为xml格式的String
     */
    @SuppressWarnings("rawtypes")
    public static String getRequestXml(SortedMap<String, String> paramMap, boolean isCDATA) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        Set set = paramMap.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (isCDATA) {
                sb.append("<").append(key).append("><![CDATA[").append(value).append("]]></").append(key).append(">");
            } else {
                sb.append("<").append(key).append(">").append(value).append("</").append(key).append(">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

}
