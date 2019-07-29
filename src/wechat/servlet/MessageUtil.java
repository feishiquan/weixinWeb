package wechat.servlet;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageUtil {
    /**
     * ����΢�ŷ���������XML��
     * @param request
     * @return map
     * @throws Exception
     */
    public static Map<String,String> parseXml(HttpServletRequest request) throws Exception {
        // ����������洢��HashMap��
        Map<String,String> map = new HashMap();
        // ��request��ȡ��������
        InputStream inputStream = request.getInputStream();
        System.out.println("��ȡ������");
        // ��ȡ������
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // �õ�xml��Ԫ��
        Element root = document.getRootElement();
        // �õ���Ԫ�ص������ӽڵ�
        List<Element> elementList = root.elements();

        // ���������ӽڵ�
        for (Element e : elementList) {
            System.out.println(e.getName() + "|" + e.getText());
            map.put(e.getName(), e.getText());
        }

        // �ͷ���Դ
        inputStream.close();
        inputStream = null;
        return map;
    }

    /**
     * ������Ϣ���� ���췵����Ϣ
     */
    public static String buildXml(Map<String,String> map) {
        String result;
        String msgType = map.get("MsgType").toString();
        System.out.println("MsgType:" + msgType);
        if(msgType.toUpperCase().equals("TEXT")){
            result = buildTextMessage(map, "Cherry��СС��, ���ʿ͹���Ҫ��ɶ?");
		} /*
			 * else if(msgType.toUpperCase().equals("TEXT")){
			 * 
			 * }else if(msgType.toUpperCase().equals("TEXT")){
			 * 
			 * }else if(msgType.toUpperCase().equals("TEXT")){
			 * 
			 * }else if(msgType.toUpperCase().equals("TEXT")){
			 * 
			 * }
			 */else{
            String fromUserName = map.get("FromUserName");
            // ������΢�ź�
            String toUserName = map.get("ToUserName");
            result = String
                    .format(
                            "<xml>" +
                                    "<ToUserName><![CDATA[%s]]></ToUserName>" +
                                    "<FromUserName><![CDATA[%s]]></FromUserName>" +
                                    "<CreateTime>%s</CreateTime>" +
                                    "<MsgType><![CDATA[text]]></MsgType>" +
                                    "<Content><![CDATA[%s]]></Content>" +
                                    "</xml>",
                            fromUserName, toUserName, getUtcTime(),
                            "��ظ����¹ؼ��ʣ�\n�ı�\nͼƬ\n����\n��Ƶ\n����\nͼ��");
        }
        return result;
    }

    /**
     * �����ı���Ϣ
     *
     * @param map
     * @param content
     * @return
     */
    private static String buildTextMessage(Map<String,String> map, String content) {
        //���ͷ��ʺ�
        String fromUserName = map.get("FromUserName");
        // ������΢�ź�
        String toUserName = map.get("ToUserName");
        /**
         * �ı���ϢXML���ݸ�ʽ
         */
        return String.format(
                "<xml>" +
                        "<ToUserName><![CDATA[%s]]></ToUserName>" +
                        "<FromUserName><![CDATA[%s]]></FromUserName>" +
                        "<CreateTime>%s</CreateTime>" +
                        "<MsgType><![CDATA[text]]></MsgType>" +
                        "<Content><![CDATA[%s]]></Content>" + "</xml>",
                fromUserName, toUserName, getUtcTime(), content);
    }

    private static String getUtcTime() {
        Date dt = new Date();// �������Ҫ��ʽ,��ֱ����dt,dt���ǵ�ǰϵͳʱ��
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmm");// ������ʾ��ʽ
        String nowTime = df.format(dt);
        long dd = (long) 0;
        try {
            dd = df.parse(nowTime).getTime();
        } catch (Exception e) {

        }
        return String.valueOf(dd);
    }

	/**
     *  ����ͼƬ��Ϣ
     * @param map
     * @param picUrl
     * @return
     */
    private static String buildImageMessage(Map<String, String> map, String picUrl) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        /*����ָ����ͼƬ(��ͼƬ���ϴ�Ϊ�زĵ�,�����media_id)*/
        //String media_id = "UCWXNCogK5ub6YFFQf7QcEpvDIYLf3Zh0L5W9i4aEp2ehfnTrASeV59x3LMD88SS";

        /*�����û���������ͼƬ*/
        String media_id = map.get("MediaId");
        return String.format(
                "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<Image>" +
                "   <MediaId><![CDATA[%s]]></MediaId>" +
                "</Image>" +
                "</xml>",
                fromUserName,toUserName, getUtcTime(),media_id
        );
    }

 	/**
     * ����������Ϣ
     * @param map
     * @return
     */
    private static String buildVoiceMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        /*�����û�������������*/
        String media_id = map.get("MediaId");
        return String.format(
                "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[voice]]></MsgType>" +
                "<Voice>" +
                "   <MediaId><![CDATA[%s]]></MediaId>" +
                "</Voice>" +
                "</xml>",
                fromUserName,toUserName, getUtcTime(),media_id
        );
    }


	/**
     * �ظ���Ƶ��Ϣ
     * @param map
     * @return
     */
    private static String buildVideoMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        String title = "�͹ٷ���������ƵӴ~~";
        String description = "�͹�����,���ڿ϶��ܿ���,�Բ��� ����?";
        /*�����û�����������Ƶ*/
        //String media_id = map.get("MediaId");
        String media_id = "hTl1of-w78xO-0cPnF_Wax1QrTwhnFpG1WBkAWEYRr9Hfwxw8DYKPYFX-22hAwSs";
        return String.format(
                "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<Video>" +
                "   <MediaId><![CDATA[%s]]></MediaId>" +
                "   <Title><![CDATA[%s]]></Title>" +
                "   <Description><![CDATA[%s]]></Description>" +
                "</Video>" +
                "</xml>",
                fromUserName,toUserName, getUtcTime(),media_id,title,description
        );
    }

	/**
     * �ظ�������Ϣ
     * @param map
     * @return
     */
    private static String buildMusicMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        String title = "�װ���·��";
        String description = "�������� ������� ����?";
        String hqMusicUrl ="http://www.kugou.com/song/20qzz4f.html?frombaidu#hash=20C16B9CCCCF851D1D23AF52DD963986&album_id=0";
        return String.format(
                "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[music]]></MsgType>" +
                "<Music>" +
                "   <Title><![CDATA[%s]]></Title>" +
                "   <Description><![CDATA[%s]]></Description>" +
                "   <MusicUrl>< ![CDATA[%s] ]></MusicUrl>" +  //�Ǳ����� ��������
                "   <HQMusicUrl><![CDATA[%s]]></HQMusicUrl>"+ //�Ǳ����� �������������ӣ�WIFI��������ʹ�ø����Ӳ�������
                "</Music>" +
                "</xml>",
                fromUserName,toUserName, getUtcTime(),title,description,hqMusicUrl,hqMusicUrl
        );
    }

    /**
     * ����ͼ����Ϣ
     * @param map
     * @return
     */
    private static String buildNewsMessage(Map<String, String> map) {
        String fromUserName = map.get("FromUserName");
        String toUserName = map.get("ToUserName");
        String title1 = "HAP��Ƶ�ʵ�ֺ�ʹ��";
        String description1 = "����HAP����õ���Spring+SpringMVC+Mybatis������Mybatis�е�����������ѡ���ڱ����صķ���ǰ��ִ���Լ����߼�����������ͨ��������ʵ������ƹ��ܣ����û���ĳ��ʵ���������ɾ�Ĳ���ʱ���������������أ�Ȼ�󽫲��������ݼ�¼����Ʊ��У������û��Ժ���ơ�";
        String picUrl1 ="http://upload-images.jianshu.io/upload_images/7855203-b9e9c9ded8a732a1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
        String textUrl1 = "http://blog.csdn.net/a1786223749/article/details/78330890";

        String title2 = "KendoUI֮Grid���������";
        String description2 = "kendoLov������ֵ���� null��undefined";
        String picUrl2 ="https://demos.telerik.com/kendo-ui/content/shared/images/theme-builder.png";
        String textUrl2 = "http://blog.csdn.net/a1786223749/article/details/78330908";

        return String.format(
                "<xml>" +
                "<ToUserName><![CDATA[%s]]></ToUserName>" +
                "<FromUserName><![CDATA[%s]]></FromUserName>" +
                "<CreateTime>%s</CreateTime>" +
                "<MsgType><![CDATA[news]]></MsgType>" +
                "<ArticleCount>2</ArticleCount>" + //ͼ����Ϣ����������Ϊ8������
                "<Articles>" + //����ͼ����Ϣ��Ϣ��Ĭ�ϵ�һ��itemΪ��ͼ,ע�⣬���ͼ��������8���򽫻�����Ӧ
                    "<item>" +
                        "<Title><![CDATA[%s]]></Title> " +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]></PicUrl>" + //ͼƬ���ӣ�֧��JPG��PNG��ʽ���Ϻõ�Ч��Ϊ��ͼ360*200��Сͼ200*200
                        "<Url><![CDATA[%s]]></Url>" + //���ͼ����Ϣ��ת����
                    "</item>" +
                    "<item>" +
                        "<Title><![CDATA[%s]]></Title>" +
                        "<Description><![CDATA[%s]]></Description>" +
                        "<PicUrl><![CDATA[%s]]]></PicUrl>" +
                        "<Url><![CDATA[%s]]]></Url>" +
                    "</item>" +
                "</Articles>" +
                "</xml>"
                ,
                fromUserName,toUserName, getUtcTime(),
                title1,description1,picUrl1,textUrl1,
                title2,description2,picUrl2,textUrl2
        );
    }

}


