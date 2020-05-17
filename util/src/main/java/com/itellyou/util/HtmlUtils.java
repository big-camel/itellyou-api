package com.itellyou.util;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.function.Function;

public class HtmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    private static void handleSection(Element element,Attributes attributes,JSONObject value,String key,Function<Element,Void> callback) throws UnsupportedEncodingException {
        Document document = Jsoup.parse(value.getString(key));
        walkTree(document,callback);
        String tableText = document.text().replaceAll("\\*","");

        if(StringUtils.isEmpty(tableText.trim())){
            element.remove();
        }else{
            value.put(key,document.body().html());
            attributes.put("value", "data:" + URLEncoderUtils.encodeURIComponent(value.toString()));
        }
    }

    public static void walkTree(Element element, Function<Element,Void> callback) throws UnsupportedEncodingException {
        Elements elements = element.children();
        for (Element child : elements){
            // 表格列头不处理
            if(child.nodeName().equals("colgroup") || child.nodeName().equals("col")) continue;
            // 处理 编辑器的 section 块
            if(child.nodeName().equals("section")){
                Attributes attributes = child.attributes();
                if(attributes.hasKey("type") && attributes.hasKey("name") && attributes.hasKey("value")){
                    String name = attributes.get("name");
                    String value = attributes.get("value");
                    JSONObject valueToJSON = StringUtils.getEditorSectionValueToJSON(value);
                    // 处理 table
                    if(name.equals("table")){
                        handleSection(child,attributes,valueToJSON,"html",callback);
                        continue;
                    }else if(name.equals("codeblock")){// 代码块
                        handleSection(child,attributes,valueToJSON,"code",callback);
                        continue;
                    }else if(name.equals("label")){// 标签
                        handleSection(child,attributes,valueToJSON,"label",callback);
                        continue;
                    }
                }
            }
            Elements childs = child.children();
            if(childs.size() == 0){
                callback.apply(child);
            }else{
                walkTree(child,callback);
            }
        }
        if(element.children().size() == 0 && element.hasParent()){
            element.remove();
        }
    }

    public static String subEditorContent(String content,String html,double scale){
        try {
            int total = StringUtils.removeHtmlTags(html).length();
            int totalLength = new BigDecimal(total).multiply(new BigDecimal(scale)).intValue();
            // 少于二十字，直接不显示
            if(totalLength < 20) return "";
            final int[] currentLength = {0};
            Document document = Jsoup.parse(content);
            walkTree(document,(Element node) -> {
                if(node == null) return null;
                String text = node.text();
                int textLength = text.length();

                // Section 模块中默认给1个字的长度，如 图片
                if(textLength == 0 && node.nodeName().equals("section")){
                    textLength = 1;
                }
                // 当前字符长度小于需要截断的长度
                if(currentLength[0] < totalLength){
                    // 当前字符长度加上当前文本长度大于需要截断的长度
                    if(currentLength[0] + textLength > totalLength){
                        int allowLen = totalLength - currentLength[0];
                        StringBuilder appendText = new StringBuilder(text.substring(0,allowLen));
                        // 表格中，隐藏超出字符的数据用 * 号代替
                        if(node.closest(new Evaluator.Tag("td")) != null) {
                            for (int i = 0; i < textLength - allowLen; i++) {
                                appendText.append("*");
                            }
                        }
                        node.text(appendText.toString());
                        currentLength[0] = totalLength;
                    }else{
                        currentLength[0] += textLength;
                    }
                }
                else if(node.closest(new Evaluator.Tag("td")) != null){
                    if(StringUtils.isNotEmpty(node.text())) node.text("***");
                }else {
                    node.remove();
                }
                return null;
            });
            return document.body().html();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
