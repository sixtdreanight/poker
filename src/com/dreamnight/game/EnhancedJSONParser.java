package com.dreamnight.game;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 增强的JSON解析器 - 修复版本 解决getString方法无法使用的问题
 */
public class EnhancedJSONParser {

    /**
     * JSON数据容器类 提供getString等便捷方法
     */
    public static class JSONData {

        private Map<String, String> data;

        public JSONData(Map<String, String> data) {
            this.data = data;
        }

        /**
         * 获取字符串值
         *
         * @param key 键
         * @return 对应的字符串值，如果键不存在返回null
         */
        public String getString(String key) {
            return data.get(key);
        }

        /**
         * 检查是否包含指定键
         *
         * @param key 键
         * @return 如果包含返回true，否则返回false
         */
        public boolean has(String key) {
            return data.containsKey(key);
        }

        /**
         * 获取所有键的集合
         *
         * @return 键的集合
         */
        public java.util.Set<String> keySet() {
            return data.keySet();
        }

        /**
         * 获取数据大小
         *
         * @return 键值对的数量
         */
        public int size() {
            return data.size();
        }

        /**
         * 检查是否为空
         *
         * @return 如果为空返回true，否则返回false
         */
        public boolean isEmpty() {
            return data.isEmpty();
        }
    }

    /**
     * 从JSON文件中解析键值对
     *
     * @param filePath JSON文件路径
     * @return 包含键值对的JSONData对象
     */
    public static JSONData parseJSONFile(String filePath) {
        Map<String, String> jsonMap = new HashMap<>();

        File file = new File(filePath);

        // 检查文件是否存在
        if (!file.exists()) {
            System.err.println("JSON文件不存在: " + file.getAbsolutePath());
            // 创建默认文件
            createDefaultPasswordFile(filePath);
            return new JSONData(jsonMap);
        }

        // 检查文件是否可读
        if (!file.canRead()) {
            System.err.println("无法读取JSON文件，请检查文件权限: " + filePath);
            return new JSONData(jsonMap);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            StringBuilder jsonContent = new StringBuilder();
            String line;

            // 读取文件内容
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }

            System.out.println("读取的JSON内容: " + jsonContent.toString());

            // 解析JSON内容
            parseJSONString(jsonContent.toString(), jsonMap);

        } catch (IOException e) {
            System.err.println("读取JSON文件失败: " + e.getMessage());
            e.printStackTrace();
        } catch (JSONParseException e) {
            System.err.println("解析JSON失败: " + e.getMessage());
            e.printStackTrace();
        }

        return new JSONData(jsonMap);
    }

    /**
     * 创建默认的密码文件
     *
     * @param filePath 文件路径
     */
    private static void createDefaultPasswordFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            String defaultContent = "{\n"
                    + "  \"admin\": \"admin123\",\n"
                    + "  \"test\": \"test123\",\n"
                    + "  \"user\": \"user123\"\n"
                    + "}";
            writer.write(defaultContent);
            writer.flush();
            System.out.println("已创建默认密码文件: " + filePath);
        } catch (IOException e) {
            System.err.println("创建默认密码文件失败: " + e.getMessage());
        }
    }

    /**
     * 解析JSON字符串
     */
    private static void parseJSONString(String jsonString, Map<String, String> jsonMap)
            throws JSONParseException {

        jsonString = jsonString.trim();

        // 检查是否是对象格式
        if (!jsonString.startsWith("{") || !jsonString.endsWith("}")) {
            throw new JSONParseException("JSON格式错误：应该以'{'开头，以'}'结尾，实际内容: " + jsonString);
        }

        // 移除外层的大括号
        String content = jsonString.substring(1, jsonString.length() - 1).trim();

        if (content.isEmpty()) {
            return;
        }

        // 分割键值对
        String[] pairs = splitKeyValuePairs(content);

        // 解析每个键值对
        for (String pair : pairs) {
            parseKeyValuePair(pair, jsonMap);
        }
    }

    /**
     * 分割键值对字符串
     */
    private static String[] splitKeyValuePairs(String jsonContent) throws JSONParseException {
        java.util.List<String> pairs = new java.util.ArrayList<>();
        StringBuilder currentPair = new StringBuilder();
        boolean inString = false;
        int braceDepth = 0;

        for (int i = 0; i < jsonContent.length(); i++) {
            char c = jsonContent.charAt(i);

            switch (c) {
                case '"':
                    inString = !inString;
                    currentPair.append(c);
                    break;

                case '{':
                case '[':
                    braceDepth++;
                    currentPair.append(c);
                    break;

                case '}':
                case ']':
                    braceDepth--;
                    currentPair.append(c);
                    break;

                case ',':
                    if (!inString && braceDepth == 0) {
                        pairs.add(currentPair.toString().trim());
                        currentPair = new StringBuilder();
                    } else {
                        currentPair.append(c);
                    }
                    break;

                default:
                    currentPair.append(c);
                    break;
            }
        }

        if (currentPair.length() > 0) {
            pairs.add(currentPair.toString().trim());
        }

        return pairs.toArray(new String[0]);
    }

    /**
     * 解析单个键值对
     */
    private static void parseKeyValuePair(String pair, Map<String, String> jsonMap)
            throws JSONParseException {

        int colonIndex = -1;
        boolean inString = false;

        for (int i = 0; i < pair.length(); i++) {
            char c = pair.charAt(i);

            if (c == '"') {
                inString = !inString;
            } else if (c == ':' && !inString) {
                colonIndex = i;
                break;
            }
        }

        if (colonIndex == -1) {
            throw new JSONParseException("键值对格式错误，缺少冒号: " + pair);
        }

        String keyPart = pair.substring(0, colonIndex).trim();
        String valuePart = pair.substring(colonIndex + 1).trim();

        String key = parseString(keyPart);
        String value = parseString(valuePart);

        jsonMap.put(key, value);
    }

    /**
     * 解析JSON字符串（移除引号）
     */
    private static String parseString(String str) throws JSONParseException {
        str = str.trim();

        if (!str.startsWith("\"") || !str.endsWith("\"")) {
            throw new JSONParseException("字符串格式错误，应该以'\"'开头和结尾: " + str);
        }

        String result = str.substring(1, str.length() - 1);

        // 处理转义字符
        result = result.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\/", "/")
                .replace("\\b", "\b")
                .replace("\\f", "\f")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");

        return result;
    }

    /**
     * JSON解析异常类
     */
    public static class JSONParseException extends Exception {

        public JSONParseException(String message) {
            super(message);
        }
    }
}
