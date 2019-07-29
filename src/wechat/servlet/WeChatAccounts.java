package wechat.servlet;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeChatAccounts extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger logger = LoggerFactory.getLogger(WeChatAccounts.class);

    /*
    * �Զ���token, ��������ǩ��,�Ӷ���֤��ȫ��
    * */
    private final String TOKEN = "weixin";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //doGet(req,resp);
    	 // TODO ���ա�������Ӧ��΢�ŷ�����ת�����û����͸������ʺŵ���Ϣ
        // ��������Ӧ�ı��������ΪUTF-8����ֹ�������룩
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        System.out.println("�������");
        String result = "";
        try {
            Map<String,String> map = MessageUtil.parseXml(req);

            System.out.println("��ʼ������Ϣ");
            result = MessageUtil.buildXml(map);
            System.out.println(result);

            if(result.equals("")){
                result = "δ��ȷ��Ӧ";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("�����쳣��"+ e.getMessage());
        }
        resp.getWriter().println(result);
    }
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("-----��ʼУ��ǩ��-----");

        /**
         * ����΢�ŷ�������������ʱ���ݹ����Ĳ���
         */
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce"); //�����
        String echostr = req.getParameter("echostr");//����ַ���

        /**
         * ��token��timestamp��nonce�������������ֵ�������
         * ��ƴ��Ϊһ���ַ���
         */
        String sortStr = sort(TOKEN,timestamp,nonce);
        /**
         * �ַ�������shal����
         */
        String mySignature = shal(sortStr);
        /**
         * У��΢�ŷ��������ݹ�����ǩ�� ��  ���ܺ���ַ����Ƿ�һ��, ��һ����ǩ��ͨ��
         */
        if(!"".equals(signature) && !"".equals(mySignature) && signature.equals(mySignature)){
            System.out.println("-----ǩ��У��ͨ��-----");
            resp.getWriter().write(echostr);
        }else {
            System.out.println("-----У��ǩ��ʧ��-----");
        }
    }

    /**
     * ��������
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * �ַ�������shal����
     * @param str
     * @return
     */
    public String shal(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            // �ֽ�����ת��Ϊ ʮ������ ��
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}


