package com.vtb;

import com.siebel.data.SiebelPropertySet;
import com.siebel.eai.SiebelBusinessService;
import com.siebel.eai.SiebelBusinessServiceException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
            int responseHttpCode;

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

                    int contentLength = client.getContentLength();
                    if (contentLength == -1) {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = in.readLine()) != null) {
                                response.append(line).append("\n");
                            }
                            responseHttpCode = client.getResponseCode();
                            String stringResponse = response.toString();
                            if (responseHttpCode == 204) {
                                stringResponse = "{\"Успешно удалены\":" + stringResponse + "\";\"Code\"" + responseHttpCode + "\"}";
                                output.setValue(stringResponse);
                                break;
                            } else {
                                stringResponse = "{\"Ошибка запроса\":" + " ; \"Code\"" + responseHttpCode + "\"}";
                                throw new SiebelBusinessServiceException("NO_SUCH_METHOD", stringResponse);
                            }
                        } catch (Exception e) {
                            throw new SiebelBusinessServiceException("ERROR_GET_BIN_METHOD", "Request falled ");
                        }
                    }
                    InputStream raw = null;
                    try {
                        raw = client.getInputStream();
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_GET_INPUT_STREAM", "{\"Ошибка запроса\":" + e +"\";\"Code\"" + "JBSError" + "\"}");
                    }
                    InputStream in = new BufferedInputStream(raw);
                    byte[] data = new byte[contentLength];
                    int bytesRead = 0;
                    int offset = 0;
                    try {
                        while (offset < contentLength) {
                            bytesRead = in.read(data, offset, data.length - offset);
                            if (bytesRead == -1)
                                break;
                            offset += bytesRead;
                        }
                        in.close();
                        if (offset != contentLength) {
                            throw new SiebelBusinessServiceException("NOT_INT", "offset != contentLength " + offset);
                        }
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_BYTEVALUE_TO_RQ", "Error to TRY in GET");
                    }
                    output.setByteValue(data);
                    break;

                case 2:
                    if (input.getByteValue() != null) {
                        value = input.getByteValue();
                    } else {
                        value = input.getValue().getBytes(StandardCharsets.UTF_8);
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
                        throw new SiebelBusinessServiceException("ERROR_OPEN_CONNECTION", "Connection post falled ");
                    }
                    client.setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
                    try {
                        client.setRequestMethod(httpMethod);
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_SET_REQUEST_METHOD", "Connection post falled");
                    }
                    try {
                        client.setDoOutput(true);
                        client.setRequestProperty("Content-Type", contentType);
                        client.setRequestProperty("Charset", charset);
                        client.setRequestProperty("Content-Disposition", contentDisposition);
                        client.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                        client.setRequestProperty("Authorization", authorization);
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_SET_REQUEST_PROP", "Error setRequestProperty to URL Connection ");
                    }
                    try (OutputStream os = client.getOutputStream()) {
                        os.write(value);
                        try (BufferedReader inBuf = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                            String line;
                            StringBuilder response = new StringBuilder();
                            while ((line = inBuf.readLine()) != null) {
                                response.append(line).append("\n");
                            }
                            output.setValue(response.toString());
                        } catch (Exception e) {
                            responseHttpCode = client.getResponseCode();
                            throw new SiebelBusinessServiceException("ERROR_BYTE_VALUE_POST", "Response error. Code -" + responseHttpCode);
                        }
                        break;
                    } catch (Exception e) {
                        throw new SiebelBusinessServiceException("ERROR_OUTPUTSTREAM", "Set OutputStream in POST-method error ");
                    }

                default:
                    throw new SiebelBusinessServiceException("ERROR_INPUT", "Method http is not. Falled");


            }

        }
    }
}
