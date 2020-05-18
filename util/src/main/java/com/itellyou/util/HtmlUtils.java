package com.itellyou.util;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class HtmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    private static void handleSection(WalkState state,Attributes attributes,JSONObject value,String key,Function<WalkState,Boolean> callback) throws UnsupportedEncodingException {
        Node element = state.getNode();
        String valueString = value.getString(key);
        Document document = Jsoup.parse(valueString);
        WalkState childState = new WalkState(document,state.getCurrentLength(),state.getTotalLength());
        walkTree(childState,callback);
        state.setCurrentLength(childState.getCurrentLength());

        String text = document.text().replaceAll("\\*","");

        if(StringUtils.isEmpty(text.trim()) && state.getCurrentLength() >= state.getTotalLength()){
            element.remove();
        }else{
            Element body = document.body();
            value.put(key,body == null ? "" : body.html());
            attributes.put("value", "data:" + URLEncoderUtils.encodeURIComponent(value.toString()));
        }
    }

    @Data
    @AllArgsConstructor
    private static class WalkState {
        private Node node;
        private Integer currentLength;
        private Integer totalLength;
    }

    public static void walkTree(WalkState state, Function<WalkState,Boolean> callback) throws UnsupportedEncodingException {
        try {
            Node element = state.getNode();
            List<Node> nodes = element.childNodes();

            List<Node> list = new ArrayList();
            list.addAll(nodes);

            Iterator<Node> iterator = list.iterator();
            while (iterator.hasNext()) {
                Node child = iterator.next();
                // 表格列头不处理
                if (child.nodeName().equals("colgroup") || child.nodeName().equals("col")) continue;
                // 处理 编辑器的 section 块
                if (child.nodeName().equals("section")) {
                    Attributes attributes = child.attributes();
                    if (attributes.hasKey("type") && attributes.hasKey("name") && attributes.hasKey("value")) {
                        String name = attributes.get("name");
                        String value = attributes.get("value");
                        JSONObject valueToJSON = StringUtils.getEditorSectionValueToJSON(value);
                        WalkState sectionState = new WalkState(child, state.getCurrentLength(),state.getTotalLength());
                        if (name.equals("table") || name.equals("codeblock") || name.equals("label")) {
                            if(name.equals("table"))
                                handleSection(sectionState, attributes, valueToJSON, "html", callback);
                            else if(name.equals("codeblock"))
                                handleSection(sectionState, attributes, valueToJSON, "code", callback);
                            else if(name.equals("label"))
                                handleSection(sectionState, attributes, valueToJSON, "label", callback);
                            state.setCurrentLength(sectionState.getCurrentLength());
                            if (nodes.size() == 0) break;
                            continue;
                        }
                    }
                }
                List<Node> childs = child.childNodes();
                if (childs.size() == 0) {
                    WalkState childState = new WalkState(child, state.getCurrentLength(),state.getTotalLength());
                    if(callback.apply(childState)) {
                        element.childNode(nodes.indexOf(child)).remove();
                    }
                    state.setCurrentLength(childState.getCurrentLength());
                } else {
                    WalkState childState = new WalkState(child, state.getCurrentLength(),state.getTotalLength());
                    walkTree(childState, callback);
                    state.setCurrentLength(childState.getCurrentLength());
                }
            }
            if (element.childNodeSize() == 0 && element.hasParent()) {
                element.remove();
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    public static String subEditorContent(String content,String html,double scale){
        try {
            int length = StringUtils.removeHtmlTags(html).length();
            int subLength = new BigDecimal(length).multiply(new BigDecimal(scale)).intValue();
            // 少于二十字，直接不显示
            if(subLength < 20) return "";
            if(StringUtils.isEmpty(content)) return "";
            Document document = Jsoup.parse(content);
            walkTree(new WalkState(document.body(),0,subLength),(WalkState state) -> {
                Node node = state.getNode();
                int totalLength = state.getTotalLength();
                int currentLength = state.getCurrentLength();
                if(node == null) return false;
                String text = "";
                if(node instanceof TextNode){
                    text = ((TextNode)node).text();
                }else if(node instanceof Element){
                    text = ((Element)node).text();
                }
                int textLength = text.length();

                // Section 模块中默认给1个字的长度，如 图片
                if(textLength == 0 && node.nodeName().equals("section")){
                    textLength = 1;
                }
                // 当前字符长度小于需要截断的长度
                if(currentLength < totalLength){
                    // 当前字符长度加上当前文本长度大于需要截断的长度
                    if(currentLength + textLength > totalLength){
                        int allowLen = totalLength - currentLength;
                        StringBuilder appendText = new StringBuilder(text.substring(0,allowLen));
                        // 表格中，隐藏超出字符的数据用 * 号代替
                        if(node instanceof Element && ((Element)node).closest(new Evaluator.Tag("td")) != null) {
                            for (int i = 0; i < textLength - allowLen; i++) {
                                appendText.append("*");
                            }
                        }
                        if(node instanceof TextNode){
                            ((TextNode)node).text(appendText.toString());
                        }else if(node instanceof Element){
                            ((Element)node).text(appendText.toString());
                        }
                        currentLength = totalLength;
                    }else{
                        currentLength += textLength;
                    }
                    state.setCurrentLength(currentLength);
                }else if(node instanceof Element && ((Element)node).closest(new Evaluator.Tag("td")) != null){
                    Element elementNode = ((Element)node);
                    if(StringUtils.isNotEmpty(elementNode.text()))elementNode.text("***");
                }else {
                    return true;
                }
                return false;
            });
            Element body = document.body();
            return body != null ? body.html() : null;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }
}
