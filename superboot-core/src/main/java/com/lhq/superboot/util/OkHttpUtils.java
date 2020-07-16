package com.lhq.superboot.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: okhttp工具类
 * @author: lihaoqi
 * @date: 2019年8月25日
 * @version: 1.0.0
 */
public class OkHttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtils.class);

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml;charset=utf-8");
    private static final MediaType MEDIA_TYPE_FILE = MediaType.parse("multipart/form-data");
    private static final int SUCCESS_CODE = 200;
    private static final OkHttpClient CLIENT;

    static {
        X509TrustManager x509tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (chain == null) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
                }
                if (chain.length <= 0) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                }
                if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
                    throw new CertificateException("checkServerTrusted: AuthType is not RSA");
                }
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                if (chain == null) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
                }
                if (chain.length <= 0) {
                    throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
                }
                if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
                    throw new CertificateException("checkServerTrusted: AuthType is not RSA");
                }
            }
        };
        CLIENT = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).sslSocketFactory(createSSLSocketFactory(x509tm), x509tm)
                .hostnameVerifier((hostname, session) -> true).build();
    }

    private static SSLSocketFactory createSSLSocketFactory(TrustManager trustManager) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            logger.error("", e);
        }
        return ssfFactory;
    }

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String get(String url, Map<String, String> queries) {
        StringBuilder sb = new StringBuilder(url);
        if (queries != null && !queries.keySet().isEmpty()) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().url(sb.toString()).build();
        return execute(request);
    }

    /**
     * post
     *
     * @param url    请求的url
     * @param params post form 提交的参数
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数
        if (params != null && !params.keySet().isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return execute(request);
    }

    /**
     * get
     *
     * @param url     请求的url
     * @param queries 请求的参数，在浏览器？后面的数据，没有可以传null
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getForHeader(String url, Map<String, String> queries) {
        StringBuilder sb = new StringBuilder(url);
        if (queries != null && !queries.keySet().isEmpty()) {
            boolean firstFlag = true;
            Iterator iterator = queries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry<String, String>) iterator.next();
                if (firstFlag) {
                    sb.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    firstFlag = false;
                } else {
                    sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        Request request = new Request.Builder().addHeader("key", "value").url(sb.toString()).build();
        return execute(request);
    }

    /**
     * Post请求发送JSON数据....{"name":"zhangsan","pwd":"123456"}
     *
     * @param url：请求Url
     * @param jsonParams：请求的JSON
     */
    public static String postJsonParams(String url, String jsonParams) {
        RequestBody requestBody = RequestBody.Companion.create(jsonParams, MEDIA_TYPE_JSON);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return execute(request);
    }

    /**
     * Post请求发送xml数据
     *
     * @param url：请求Url
     * @param xml：请求的xmlString
     */
    public static String postXmlParams(String url, String xml) {
        RequestBody requestBody = RequestBody.Companion.create(xml, MEDIA_TYPE_XML);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return execute(request);
    }

    /**
     * Post请求发送文件
     *
     * @param url：请求Url
     * @param file：请求的文件
     * @param fileName：请求文件名
     */
    public static String postFileParams(String url, File file, String fileName) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.Companion.create(file, MEDIA_TYPE_FILE))
                .build();

        Request request = new Request.Builder().url(url).post(requestBody).build();
        return execute(request);
    }

    private static String execute(Request request) {
        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.code() == SUCCESS_CODE) {
                ResponseBody body = response.body();
                if (body != null) {
                    return body.string();
                }
            }
        } catch (IOException e) {
            logger.error(request.toString(), e);
        }
        return null;
    }
}
