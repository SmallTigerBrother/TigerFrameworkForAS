package com.mn.tiger.request;

/**
 * Created by peng on 16/3/23.
 */
public class TGMediaType
{
    /**
     * 任意的二进制数据。通常这种类型的文件没有和任何应用程序相关联，已过去假设的软件比如Apache相反，
     * 这并不是一种未知文件的类型，而是应该在服务器端不确定内容的额类型，让客户端自行猜测类型
     */
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /**
     * JSON格式的数据，在RFC 4627中定义
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     *PDF文件，在 RFC 3778 中定义
     */
    public static final String APPLICATION_PDF = "application/pdf";

    /**
     * ZIP文件，一种压缩格式
     */
    public static final String APPLICATION_ZIP = "application/zip";

    /**
     * DTD文件， 在RFC 3023中定义
     */
    public static final String APPLICATION_XML_DTD = "application/xml-dtd";

    /**
     * XHTML文件，在RFC 3236中定义
     */
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";

    /**
     *JPEG 和JFIF格式，在RFC 2045 和 RFC 2046中定义
     */
    public static final String IMAGE_JPEG = "image/jpeg";

    /**
     *png格式，在 RFC 2083中定义
     */
    public static final String IMAGE_PNG = "image/png";

    /**
     * HTML格式，在RFC 2854中定义
     */
    public static final String TEXT_HTML = "text/html";

    /**
     * 原文数据，在RFC 2046和RFC 3676中定义
     */
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * MP4视频，在RFC 4337中定义
     */
    public static final String VIDEO_MP4 = "video/mp4";
}
