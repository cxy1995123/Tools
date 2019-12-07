package chen.com.library.net.Okhttp.client;


import okhttp3.MediaType;

public class MediaTypeCode {

//    text/html ： HTML格式
//    text/plain ：纯文本格式
//    text/xml ：  XML格式
//    image/gif ：gif图片格式
//    image/jpeg ：jpg图片格式
//    image/png：png图片格式
//    以application开头的媒体格式类型：
//
//    application/xhtml+xml ：XHTML格式
//    application/xml     ： XML数据格式
//    application/atom+xml  ：Atom XML聚合格式
//    application/json    ： JSON数据格式
//    application/pdf       ：pdf格式
//    application/msword  ： Word文档格式
//    application/octet-stream ： 二进制流数据（如常见的文件下载）
//    application/x-www-form-urlencoded ： <form encType=””>中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）

    private static final String TYPE_STRING = "text/plain";
    private static final String TYPE_HTML = "text/html";
    private static final String TYPE_XML = "text/xml";
    private static final String TYPE_GIF = "image/gif";
    private static final String TYPE_PNG = "image/png";
    private static final String TYPE_JPEG = "image/jpeg";
    private static final String TYPE_XHTML = "text/html";
    private static final String TYPE_XML2 = "application/xhtml+xm";
    private static final String TYPE_ATOM = "application/atom+xml";
    private static final String TYPE_JSON = "application/json";
    private static final String TYPE_PDF = "application/pdf";
    private static final String TYPE_WORD = "application/msword";
    private static final String TYPE_OCTET_STREAM = "application/octet-stream";
    private static final String TYPE_FROM_URLENCODED = "application/x-www-form-urlencoded";

    /**
     * 文本
     **/
    public static MediaType getTypeString() {
        return MediaType.parse(TYPE_STRING);
    }

    /**
     * ATOM XML 混合数据
     **/
    public static MediaType getTypeAtom() {
        return MediaType.parse(TYPE_ATOM);
    }

    /**
     * Gif
     **/
    public static MediaType getTypeGif() {
        return MediaType.parse(TYPE_GIF);
    }

    /**
     * Html
     **/
    public static MediaType getTypeHtml() {
        return MediaType.parse(TYPE_HTML);
    }

    /**
     * jpeg
     **/
    public static MediaType getTypeJpeg() {
        return MediaType.parse(TYPE_JPEG);
    }

    /**
     * 表单默认格式
     **/
    public static MediaType getDefaultType() {
        return MediaType.parse(TYPE_FROM_URLENCODED);
    }

    /**
     * json
     **/
    public static MediaType getTypeJson() {
        return  MediaType.parse(TYPE_JSON);
    }

    /**
     * 二进制流
     **/
    public static MediaType getTypeOctetStream() {
        return MediaType.parse(TYPE_OCTET_STREAM);
    }

    /**
     * PDF
     **/
    public static MediaType getTypePdf() {
        return MediaType.parse(TYPE_PDF);
    }

    /**
     * PNG
     **/
    public static MediaType getTypePng() {
        return MediaType.parse(TYPE_PNG);
    }

    /**
     * Word 文档
     **/
    public static MediaType getTypeWord() {
        return MediaType.parse(TYPE_WORD);
    }

    /**
     * xml
     **/
    public static MediaType getTypeXml() {
        return MediaType.parse(TYPE_XML);
    }

    public static MediaType getTypeXml2() {
        return MediaType.parse(TYPE_XML2);
    }

    /**
     * XHTML
     **/
    public static MediaType getTypeXhtml() {
        return MediaType.parse(TYPE_XHTML);
    }
}
