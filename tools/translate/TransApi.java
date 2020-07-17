package com.mapleworld.game.tapgame.tools.translate;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class TransApi {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private String appid;
    private String securityKey;
    private String cacheWord = "";
    private String cacheChinese = "";

    public TransApi(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public String getTransResult(String query, String from, String to) {
        if(cacheWord.equals(query)){
            return cacheChinese;
        }
        Map<String, String> params = buildParams(query, from, to);
        String json = HttpGet.get(TRANS_API_HOST, params);
        JSONObject jsonObject = new JSONObject(json);
        String trans_result = jsonObject.getJSONArray("trans_result").get(0).toString();
        System.out.println(trans_result);
        jsonObject = new JSONObject(trans_result);
        System.out.println(jsonObject);
        String result = jsonObject.getString("dst");
        cacheWord = query;
        cacheChinese = result;
        return result;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

}
