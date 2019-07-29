package accessToken;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public class AccessTokenServlet extends HttpServlet {
    static Logger logger = LoggerFactory.getLogger(AccessTokenServlet.class);

    @Override
    public void init() throws ServletException {
        System.out.println("-----����AccessTokenServlet-----");
        super.init();

        final String appId = this.getServletContext().getInitParameter("appId");
        final String appSecret =  this.getServletContext().getInitParameter("appSecret");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //��ȡaccessToken
                        AccessTokenInfo.accessToken = getAccessToken(appId, appSecret);
                        //��ȡ�ɹ�
                        if (AccessTokenInfo.accessToken != null) {
                            //��ȡ��access_token ����7000��,��Լ2��Сʱ����
                            Thread.sleep(7000 * 1000);
                        } else {
                            //��ȡʧ��
                            Thread.sleep(1000 * 3); //��ȡ��access_tokenΪ�� ����3��
                        }
                    } catch (Exception e) {
                        System.out.println("�����쳣��" + e.getMessage());
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000 * 10); //�����쳣����1��
                        } catch (Exception e1) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private AccessToken getAccessToken(String appId, String appSecret) {
        NetWorkUtil netHelper = new NetWorkUtil();
        /**
         * �ӿڵ�ַΪhttps://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET������grant_type�̶�дΪclient_credential���ɡ�
         */
        String Url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret);
        //������Ϊhttps��get���󣬷��ص����ݸ�ʽΪ{"access_token":"ACCESS_TOKEN","expires_in":7200}
        String result = netHelper.getHttpsResponse(Url, "");
        System.out.println("��ȡ����access_token="+result);

        //ʹ��FastJson��Json�ַ���������Json����
        JSONObject json = JSON.parseObject(result);
        AccessToken token = new AccessToken();
        token.setTokenName(json.getString("access_token"));
        token.setExpireSecond(json.getInteger("expires_in"));
        return token;
    }

}

