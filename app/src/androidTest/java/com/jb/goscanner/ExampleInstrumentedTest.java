package com.jb.goscanner;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.jb.goscanner.function.bean.ContactInfo;
import com.jb.goscanner.function.bean.DetailItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.jb.goscanner", appContext.getPackageName());

        String m = "{\"contact\":[{\"detail0\":\"{\\\"detail\\\":[{\\\"id\\\":\\\"1608946762224\\\",\\\"tag\\\":\\\"对对对\\\",\\\"group\\\":\\\"啊啊啊\\\",\\\"contactId\\\":\\\"头部\\\"}]}\",\"detail1\":\"{\\\"detail\\\":[{\\\"group\\\":\\\"电话\\\"}]}\",\"detail2\":\"{\\\"detail\\\":[{\\\"tag\\\":\\\"电话\\\",\\\"value\\\":\\\"155558888888\\\",\\\"group\\\":\\\"电话\\\",\\\"contactId\\\":\\\"1608946762224\\\"}]}\",\"detail3\":\"{\\\"detail\\\":[{\\\"group\\\":\\\"邮箱\\\"}]}\",\"detail4\":\"{\\\"detail\\\":[{\\\"tag\\\":\\\"邮箱\\\",\\\"value\\\":\\\"555555555\\\",\\\"group\\\":\\\"邮箱\\\",\\\"contactId\\\":\\\"1608946762224\\\"}]}\",\"detail5\":\"{\\\"detail\\\":[{\\\"group\\\":\\\"微信\\\"}]}\",\"detail6\":\"{\\\"detail\\\":[{\\\"tag\\\":\\\"微信\\\",\\\"value\\\":\\\"啵啵啵啵啵啵啵\\\",\\\"group\\\":\\\"微信\\\",\\\"contactId\\\":\\\"1608946762224\\\"}]}\",\"detail7\":\"{\\\"detail\\\":[{\\\"group\\\":\\\"其他\\\"}]}\"}]}";

        //ContactInfo contactInfo = ContactInfo.parseToContactInfo(m);

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setId(UUID.randomUUID().toString());
        contactInfo.setName("AAA");
        contactInfo.setImgUrl("AARRRR");
        contactInfo.setRemark("OPK");
        contactInfo.setPhone(new DetailItem("id",  "tag",  "value",  "group",  "contactId"));
        contactInfo.setEmail(new DetailItem("id",  "tag",  "value",  "group",  "contactId"));
        contactInfo.setWechat(new DetailItem("id",  "tag",  "value",  "group",  "contactId"));
        contactInfo.setOther(new DetailItem("id",  "tag",  "value",  "group",  "contactId"));


        String s = ContactInfo.parseToString(contactInfo);
        ContactInfo contactInfo1 = ContactInfo.parseToContactInfo(s);

//        String test = "{\"detail0\":\"{\\\"detail\\\":[{\\\"id\\\":\\\"00cfd37b-38b1-4a25-aab2-2d5497cb20f8\\\",\\\"tag\\\":\\\"AAA\\\",\\\"value\\\":\\\"AARRRR\\\",\\\"group\\\":\\\"OPK\\\",\\\"contactId\\\":\\\"头部\\\"}]}\"}";
//        JSONObject object = new JSONObject(test);
//        JSONObject jsonObject = new JSONObject(object.getString("detail0"));
//        String detail = jsonObject.getString("detail");

        String test2 = "[{\"id\":\"19ad85c1-27c4-493f-b17d-21dc1050a5b1\",\"tag\":\"AAA\",\"value\":\"AARRRR\",\"group\":\"OPK\",\"contactId\":\"头部\"}]";
        //JSONArray


    }
}
