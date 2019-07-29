package wechat.servlet;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * ÉÏ´«ËØ²Äservlet
 */
@WebServlet(name = "UploadMediaServlet")
public class UploadMediaServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UploadMediaApiUtil uploadMediaApiUtil = new UploadMediaApiUtil();
        String appId = this.getServletContext().getInitParameter("appId");
        String appSecret =  this.getServletContext().getInitParameter("appSecret");
		/*
		 * String appId = "wx0aa26453a7ec9df7"; 
		 * String appSecret ="2819f0c98199daef39cb6220b4d01b96";
		 */
        String accessToken = uploadMediaApiUtil.getAccessToken(appId,appSecret);

        String filePath = "D:\\weChartTestResource\\Image\\1.jpg";
        File file = new File(filePath);
        String type = "IMAGE";
        JSONObject jsonObject = uploadMediaApiUtil.uploadMedia(file,accessToken,type);
        System.out.println(jsonObject.toString());
    }
}


