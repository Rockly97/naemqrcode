package com.jb.goscanner.function.bean;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by liuyue on 2017/9/3.
 */

public class DetailItem implements Serializable {
    private String id; // 主键
    private String contactId; // 外键
    private String tag;
    private String value; // value为null只有一种情况：记录的内容是分组
    private String group; // 分组：phone／email/wechat/...

    public static final String GROUP_PHONE = "电话";
    public static final String GROUP_EMAIL = "邮箱";
    public static final String GROUP_WECHAT = "微信";
    public static final String GROUP_OTHER = "其他";
    public static final String GROUP_HEAD = "头部";
    public DetailItem() {
    }

    public DetailItem(String group) {
        this.group = group;
    }

    public DetailItem(String id, String tag, String value, String group, String contactId) {
        this.id = id;
        this.tag = tag;
        this.value = value;
        this.group = group;
        this.contactId = contactId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String parseToJsonString() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            JSONObject jsonDetail = new JSONObject();
            jsonDetail.put("id", id);
            jsonDetail.put("tag", tag);
            jsonDetail.put("value", value);
            jsonDetail.put("group", group);
            jsonDetail.put("contactId", contactId);

            jsonArray.put(jsonDetail);
            jsonObject.put("detail", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    public static DetailItem parseToDetailItem(String jsonString) {
        Gson gson = new Gson();
        DetailItem item = null;
        try {
            item = new DetailItem();

            //JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                if(jsonObject.getString("id")!=null){
                    item.id = jsonObject.getString("id");
                }else {
                    item.id="";
                }
                if(jsonObject.getString("tag")!=null){
                    item.tag = jsonObject.getString("tag");
                }else {
                    item.tag="";
                }
                if(jsonObject.getString("value")!=null){
                    item.value = jsonObject.getString("value");
                }else {
                    item.value="";
                }
                if(jsonObject.getString("group")!=null){
                    item.group = jsonObject.getString("group");
                }else {
                    item.group="";
                }
                if(jsonObject.getString("contactId")!=null){
                    item.contactId = jsonObject.getString("contactId");
                }else {
                    item.contactId="";
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }


    @Override
    public String toString() {
        return "DetailItem{" +
                "tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                ", group='" + group + '\'' +
                '}';

    }
}
