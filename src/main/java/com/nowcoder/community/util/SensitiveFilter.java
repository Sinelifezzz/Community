package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT="***";

    // 根节点
    private TrieNode rootNode=new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword=reader.readLine())!=null){
                // 构建前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("加载敏感词失败：" + e.getMessage());
        }
    }

    // 将敏感词添加至前缀树
    private void addKeyword(String keyword){
        TrieNode tmpNode=rootNode;
        for(int i=0;i<keyword.length();i++){
            char c=keyword.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if(subNode==null){
                subNode=new TrieNode();
                tmpNode.addSubNode(c,subNode);
            }
            tmpNode=subNode;
            // 设置结束标识
            if(i==keyword.length()-1){
                tmpNode.setKeywordEnd(true);
            }
        }

    }

    // 过滤文本
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }

        TrieNode tmpNode=rootNode;
        int begin=0;
        int position=0;
        StringBuilder res=new StringBuilder();
        while (position<text.length()){
            char c=text.charAt(position);
            if(isSymbol(c)){
                if(tmpNode==rootNode){
                    res.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tmpNode=tmpNode.getSubNode(c);
            if(tmpNode==null){
                res.append(text.charAt(begin));
                position=++begin;
                tmpNode=rootNode;
            }else if(tmpNode.isKeywordEnd()){
                res.append(REPLACEMENT);
                begin=++position;
                tmpNode=rootNode;
            }else {
                position++;
            }
        }

        res.append(text.substring(begin));

        return res.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    // 前缀树
    private class TrieNode {
        // 关键词结束的标识
        private boolean isKeywordEnd = false;

        // 子节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

}
