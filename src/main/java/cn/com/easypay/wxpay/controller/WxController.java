package cn.com.easypay.wxpay.controller;

import ch.qos.logback.core.util.TimeUtil;
import cn.com.easypay.wxpay.util.HttpUtils;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
 *@ClassName WxController
 *@Description TODO
 *@Author wangtao
 *@Date 2021/3/11 16:03
 */
@Controller
@Slf4j
@RequestMapping("/wxpay")
public class WxController {

//    String appid = "wxde94a63529b9b709";
//
//    String secret = "744225e226dede8dc663ceba20205dc5";
    String appid = "wx45bfd625a76f45df";

    String secret = "8737a5954e85fc247a07e87b5ada203b";

    //第一步授权url
    String authorizeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?";
    //重定向本地url
    String redirect_uri = "https://mtest.eycard.cn/wxpay/getToken";

    //第二步使用授权码获取token
    String tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?";

    //第三步获取用户信息
    String userUrl = "https://api.weixin.qq.com/sns/userinfo?";

    //重定向至首页
    String indexUrl = "https://mtest.eycard.cn/wxpay/html/index.html";

    @RequestMapping("/start")
    public void start(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = authorizeUrl + "appid=" + appid + "&redirect_uri=" + URLEncoder.encode(redirect_uri, "UTF-8") + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
//        url = URLEncoder.encode(url, "UTF-8");
        log.info("第一步获取授权url：" + url);
        response.sendRedirect(url);
    }

    @RequestMapping("/getToken")
    public void getToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        log.info("获取token接口code:" + code + "\r\n" + "state" + state);
        if (StringUtils.isBlank(code)) {
            log.info("获取授权码失败");
            return;
        }
        String url = tokenUrl + "appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        log.info("获取token请求微信：" + url);
        String result = HttpUtil.get(url);
        log.info("获取token微信应答：" + result);
        JSONObject json = JSON.parseObject(result);
        String openid = json.getString("openid");
        String access_token = json.getString("access_token");

        url = userUrl + "access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN";
        log.info("获取用户信息请求微信：" + url);
        result = HttpUtil.get(url);
        log.info("获取用户信息微信应答：" + result);
        json = JSON.parseObject(result);
        String nickname = json.getString("nickname");//昵称
        String sex = json.getString("sex");//性别
        String province = json.getString("province");//省
        String city = json.getString("city");//市
        String country = json.getString("country");//国家
        String headimgurl = json.getString("headimgurl");//头像
        response.setStatus(200);
        response.sendRedirect(indexUrl + "?nickname=" + nickname + "&sex=" + sex + "&province=" + province + "&city=" + city
                + "&country=" + country + "&headimgurl=" + headimgurl + "&time=" + System.currentTimeMillis());
    }

    public static void main(String[] args) {
//        toPay();
    }

    @RequestMapping("/toPay")
    @ResponseBody
    public String toPay(String tradeamt, String openid) {// b631223b4d9b4d8f8c8347c92ba3bcc8
        String channelid = "616161616161600";
        String merid = "W00000000001381";
        String termid = "W0001601";
        String opt = "wxPreOrder";// 微信
        String tradetype = "JSAPI";
        String tradetrace = UUID.randomUUID().toString().replace("-", "");
//        String tradeamt = "1";
        String body = "test";
        String notifyurl = "https://www.baidu.com";
        String returnurl = "https://www.baidu.com";
        String customerip = "127.0.0.1";
//        String openid = "oIcV7w_gfltIgTUKhjHHQ2GFQeXQ";
        String str1 = "channelid=" + channelid + "&merid=" + merid + "&termid=" + termid + "&opt=" + opt + "&openid="
                + openid + "&tradetype=" + tradetype + "&tradetrace=" + tradetrace + "&tradeamt=" + tradeamt + "&body="
                + body + "&notifyurl=" + notifyurl;
        if ("upPreOrder".equals(opt)) {
            str1 = str1 + "&returnurl=" + returnurl + "&customerip=" + customerip;
        }
        String key = "58isddado22df0ljal5dkifi88v7lav8";
        String sign = getSign(str1, key);
        System.out.println(sign);

        Map<String, String> param = new HashMap<String, String>();
        param.put("channelid", channelid);
        param.put("merid", merid);
        param.put("termid", termid);
        param.put("opt", opt);
        param.put("tradetype", tradetype);
        param.put("tradetrace", tradetrace);
        param.put("tradeamt", tradeamt);
        param.put("body", body);
        param.put("notifyurl", notifyurl);
        param.put("openid", openid);
        param.put("sign", sign);
        if (opt.equals("upPreOrder")) {
            param.put("returnurl", returnurl);
            param.put("customerip", customerip);
        }
        Map map = param;
        JSONObject json = new JSONObject(map);
        log.info("下单请求=====>" + json.toString());
        try {
//			Response result = HttpUtils.post("http://192.168.5.144:8080/WorthTech_Access_AppPaySystemV2/apppayacc",
//					param, HttpUtils.FORM_TYPE);
            Connection.Response result = HttpUtils.post(
                    "https://notify-test.eycard.cn:7443/WorthTech_Access_AppPaySystemV2/apppayacc", param,
                    HttpUtils.FORM_TYPE);// 203.107.46.35 180.168.215.66 https://notify-test.eycard.cn:7443
            // 192.168.5.203:8085
            log.info("下单应答" + result.body());
            JSONObject json1 = JSON.parseObject(result.body());
            return json1.getString("prepayid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
//		{"tradetype":"JSAPI","termid":"W0001081","opt":"wxPreOrder","tradetrace":"df4adiiddd5l7ddfd1did54dflldk3fl","returnmsg":"","wtorderid":"11620190506105323063121","sign":"495D6140A0E9B065ABF70F51D54FD4C5","merid":"W00000000001001","prepayid":"{\"timeStamp\":\"1557111204\",\"package\":\"prepay_id=wx06105324848383696b35d03e1322815783\",\"paySign\":\"jo5CJ6xgHhUzRMdOufMoDbiy8FpwQ7U0B0o8RaUZjvNQvkP53ceiVzWKvw/CZ/PRje8fi1uygSAt4DVkWh8tmL3PXLoGjckFeJDEXYflGZcBQ9OTBeBlqeD0T2njw5bnhRkes80eb9/bC9axTG5CAc1dbv68EoBxwPm2a4zwVkArMNqgbq84Ukcll4FiAUff4PtijOoK2vjpZTYUnJBQ1dmeQUZ5ua9jqhXeXC19+cg3gkahn0TDqyL4+b4Jb+Ef36b2C7W1VfperMfiVDT8hgcMds4YTvaEipdfUo9CT1GklQf9dEJH4GtVjWdse9pFC2ZBR26pZLLYY/uFMMrXYA==\",\"appId\":\"wx89d180221359e985\",\"signType\":\"RSA\",\"nonceStr\":\"Px3HhYNrrKTvEEexP3TGk0tuGjXtNzHM\"}","resultcode":"00","channelid":"616161616161618"}
    }

    // 获取sign
    public static String getSign(String str1, String key) {
        String[] strs = str1.split("&");
        List<String> mlist = Arrays.asList(strs);
        Collections.sort(mlist);
        String str2 = "";
        for (int i = 0; i < mlist.size(); i++) {
            str2 += mlist.get(i) + "&";
        }
        str2 += "key=" + key;
        System.out.println(str2);
        str2 = MD5(str2);
        return str2;
    }

    // md5加密
    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result.toUpperCase();
    }
}