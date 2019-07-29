package wechat.servlet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;
import org.apache.commons.httpclient.util.HttpURLConnection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import accessToken.NetWorkUtil;

/**
 * Author xiuhong.chen@hand-china.com
 * created on 2018/1/9
 * 上传media素材, 获取media_id
 */
public class UploadMediaApiUtil {
    // token 接口(GET)
    private static final String ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    // 素材上传(POST)URL
    private static final String UPLOAD_MEDIA = "https://api.weixin.qq.com/cgi-bin/media/upload";
    // 素材下载:不支持视频文件的下载(GET)
    private static final String DOWNLOAD_MEDIA = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s";

    public static String getTokenUrl(String appId, String appSecret) {
     return String.format(ACCESS_TOKEN, appId, appSecret);
    }

    public static String getDownloadUrl(String token, String mediaId) {
     return String.format(DOWNLOAD_MEDIA, token, mediaId);
    }

    /**
     * 通用接口获取token凭证
     * @param appId
     * @param appSecret
     * @return
     */
    public String getAccessToken(String appId, String appSecret) {
        NetWorkUtil netHelper = new NetWorkUtil();
        String Url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret);
        String result = netHelper.getHttpsResponse(Url, "");
        JSONObject json = JSON.parseObject(result);
        return json.getString("access_token");
    }

    /**
     * 素材上传到微信服务器
     * @param file  File file = new File(filePath); // 获取本地文件
     * @param token access_token
     * @param type type只支持四种类型素材(video/image/voice/thumb)
     * @return
     */
    public  JSONObject uploadMedia(File file, String token, String type) {
        if(file == null || token == null || type == null){
            return null;
        }
        if(!file.exists()){
            System.out.println("上传文件不存在,请检查!");
            return null;
        }
        JSONObject jsonObject = null;
        PostMethod post = new PostMethod(UPLOAD_MEDIA);
        post.setRequestHeader("Connection", "Keep-Alive");
        post.setRequestHeader("Cache-Control", "no-cache");
        FilePart media;
        HttpClient httpClient = new HttpClient();
        //信任任何类型的证书
        Protocol myhttps = new Protocol("https", new SSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);

        try {
            media = new FilePart("media", file);
            Part[] parts = new Part[]{
                    new StringPart("access_token", token),
                    new StringPart("type", type),
                    media
                    };
            MultipartRequestEntity entity = new MultipartRequestEntity(parts,post.getParams());
            post.setRequestEntity(entity);
            int status = httpClient.executeMethod(post);
            if (status == HttpStatus.SC_OK) {
                String text = post.getResponseBodyAsString();
                jsonObject = JSONObject.parseObject(text);
            } else {
                System.out.println("upload Media failure status is:" + status);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static File downloadMedia(String fileName, String token, String mediaId) {
        String path = getDownloadUrl(token, mediaId);
        //return httpRequestToFile(fileName, url, "GET", null);

        if (fileName == null || path == null) {
            return null;
        }
        File file = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        FileOutputStream fileOut = null;
        try {
             URL url = new URL(path);
             conn = (HttpURLConnection) url.openConnection();
             conn.setDoOutput(true);
             conn.setDoInput(true);
             conn.setUseCaches(false);
             conn.setRequestMethod("GET");

             inputStream = conn.getInputStream();
             if (inputStream != null) {
                 file = new File(fileName);
             } else {
                 return file;
             }

             //写入到文件
             fileOut = new FileOutputStream(file);
             if (fileOut != null) {
                 int c = inputStream.read();
                 while (c != -1) {
                     fileOut.write(c);
                     c = inputStream.read();
                 }
             }
        } catch (Exception e) {
        } finally {
             if (conn != null) {
                 conn.disconnect();
             }

             try {
                  inputStream.close();
                  fileOut.close();
               } catch (IOException execption) {
             }
        }
    return file;
    }

}




