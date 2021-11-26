package com.vtb;

import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessService;
import com.siebel.eai.SiebelBusinessServiceException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class JavaHttpTransport extends SiebelBusinessService {

    public JavaHttpTransport() {
    }

    @Override
    public void doInvokeMethod(String methodName, SiebelPropertySet input,
                               SiebelPropertySet output)
            throws SiebelBusinessServiceException {


        if (!methodName.equals("SendHttp"))
            throw new SiebelBusinessServiceException("NO_SUCH_METHOD", "No such method");
        else {
            String url = input.getProperty("url");
            String authorization = input.getProperty("authorization");
            String charset = input.getProperty("charset");
            String contentType = input.getProperty("contentType");
            String httpMethod = input.getProperty("httpMethod");
            String contentDisposition = input.getProperty("contentDisposition");
            byte[] value;
            TrustManager[] trustAllCerts;
            HttpsURLConnection client;
            int responseHttpCode = 0;
            int valueMethod = 0;

            if (url == null || url.equals("") || (authorization == null)
                    || authorization.equals("") || (httpMethod == null)
                    || httpMethod.equals("") || (charset == null) || charset.equals("")
                    || (contentType == null) || contentType.equals(""))
                throw new SiebelBusinessServiceException("NO_PAR", "Missing param");
            try {

                if (httpMethod.equals("GET") || httpMethod.equals("DELETE")) {
                    valueMethod = 1;
                }
                if (httpMethod.equals("POST")) {
                    valueMethod = 2;
                }
            } catch (Exception e) {
                throw new SiebelBusinessServiceException("ERROR_CLASS", "Class get value falled " + e);
            }
            switch (valueMethod) {
                case 1:
                    trustAllCerts = new TrustManager[]{
                            new X509TrustManager() {

                                @Override
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                @Override
                                public void checkClientTrusted(
                                        java.security.cert.X509Certificate[] certs, String authType) {
                                }

                                @Override
                                public void checkServerTrusted(
                                        java.security.cert.X509Certificate[] certs, String authType) {
                                }
                            }};
                    try {
                        SSLContext sc = null;
                        sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                        client = (HttpsURLConnection) new URL(url).openConnection();
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_CONNECTION", "{ \"Ошибка запроса\": \"Error connection to java service\"}");
                    }
                    client.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                    try {
                        client.setRequestMethod(httpMethod);
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_METHOD", "{ \"Ошибка запроса\": \"Error set http method to java service\"}");
                    }
                    client.setRequestProperty("Content-Type", contentType);
                    client.setRequestProperty("Accept", "*/*");
                    client.setRequestProperty("Charset", charset);
                    client.setRequestProperty("Authorization", authorization);
                    int contentLength = 0;
                    try {
                        responseHttpCode = client.getResponseCode();
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_GET_RESPONSE_CODE", "Error get 'getResponseCode'.");
                    }
                    String stringResponse = "";
                    try {
                        if (String.valueOf(responseHttpCode).matches("2.*")) {
                            InputStream raw = client.getInputStream();
                            PushbackInputStream pis = new PushbackInputStream(raw);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            int code;
                            try {
                                while ((code = pis.read()) != -1) {
                                    baos.write(code);
                                }
                            } catch (Exception ignored) {
                            } finally {
                                if(pis != null) {
                                    pis.close();
                                }
                                if(raw != null) {
                                    raw.close();
                                }
                            }
                            byte[] data = baos.toByteArray();
                            //pis.unread(imageData);
                            contentLength = data.length;
                            if (String.valueOf(responseHttpCode).matches("204")) {
                                stringResponse = Arrays.toString(data);
                                stringResponse = "{\"Успешно\":" + stringResponse + "\";\"Code\"" + responseHttpCode + "\"}";
                                output.setValue(stringResponse);
                            }
                            pis.close();
                            baos.close();
                            output.setByteValue(data);
                        } else if (String.valueOf(responseHttpCode).matches("4.*") || String.valueOf(responseHttpCode).matches("5.*")) {
                            stringResponse = "{\"Ошибка запроса\":" + ";\"Ответ\"" + responseHttpCode + "\"}";
                            output.setValue(stringResponse);
                            throw new SiebelBusinessServiceException("ERROR_CODE", "Error response.Code - " + responseHttpCode);
                        }
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_TO_RQ", e + " Error to parse. Length- " + contentLength + ". Code- " + responseHttpCode);
                    }

                    break;

                case 2:
                    if (input.getByteValue() == null) {
                        value = input.getValue().getBytes();
                    } else {
                        value = input.getByteValue();
                    }
                    int postDataLength = value.length;

                    trustAllCerts = new TrustManager[]{
                            new X509TrustManager() {

                                @Override
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return null;
                                }

                                @Override
                                public void checkClientTrusted(
                                        java.security.cert.X509Certificate[] certs, String authType) {
                                }

                                @Override
                                public void checkServerTrusted(
                                        java.security.cert.X509Certificate[] certs, String authType) {
                                }
                            }};
                    try {
                        SSLContext sc = null;
                        sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                        client = (HttpsURLConnection) new URL(url).openConnection();
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_OPEN_CONNECTION", "Connection POST falled ");
                    }
                    client.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                    try {
                        client.setRequestMethod(httpMethod);
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_SET_REQUEST_METHOD", "Connection POST falled");
                    }
                    try {
                        client.setDoOutput(true);
                        client.setRequestProperty("Content-Type", contentType);
                        client.setRequestProperty("Charset", charset);
                        client.setRequestProperty("Content-Disposition", contentDisposition);
                        client.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                        client.setRequestProperty("Authorization", authorization);
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_SET_REQUEST_PROP",
                                "Error setRequestProperty to URL Connection ");
                    }
                    try (OutputStream os = client.getOutputStream()) {
                        os.write(value);
                        responseHttpCode = client.getResponseCode();
                        try (BufferedReader inBuf = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = inBuf.readLine()) != null) {
                                response.append(line).append("\n");
                            }
                            output.setValue(response.toString());
                        } catch (Exception e) {
                            responseHttpCode = client.getResponseCode();
                            throw new SiebelBusinessServiceException("ERROR_BYTE_VALUE_POST",
                                    "Response error. Code -" + responseHttpCode);
                        }
                        break;
                    } catch (Exception e) {
                        output.setValue(Arrays.toString(value));
                        throw new SiebelBusinessServiceException("ERROR_OUTPUT_STREAM", "Method falled. Length content -"
                                + value.length + ".Error code- " + responseHttpCode);
                    }

                default:
                    throw new SiebelBusinessServiceException("ERROR_INPUT", "Method http is not. Falled");


            }

        }
    }
}
