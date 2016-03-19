package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator
{
    private static final int DATABASE_VERSION = 1;

    public static void main(String args[]) throws Exception
    {
        String outDir = args[0];

        Schema schema = new Schema(DATABASE_VERSION, "com.mn.tiger.download.db");
        DaoGenerator daoGenerator = new DaoGenerator();
        initDownloader(schema);

        daoGenerator.generateAll(schema, outDir);
    }

    private static void initDownloader(Schema schema) throws Exception
    {
        Entity downloader = schema.addEntity("Downloader");
        downloader.addLongProperty("id").notNull().primaryKey();
        downloader.addStringProperty("url").notNull();
        downloader.addStringProperty("params"); //参数
        downloader.addLongProperty("fileSize").notNull();//文件大小
        downloader.addLongProperty("completeSize").notNull();//已下载大小
        downloader.addIntProperty("downloadStatus").notNull();//文件下载状态，DonwnloadConstant:INIT:0  // DOWNLOADING:1,SUCCESS:2,FAILURE:3,PAUSE:4,SOURCE_ERROR:5(下载来源错误，文件长度或MD5校验出错)
        downloader.addStringProperty("savePath").notNull();
        downloader.addIntProperty("requestType").notNull();//请求类型
        downloader.addStringProperty("checkKey");//文件流校验加密字符串
        downloader.addBooleanProperty("accessRanges").notNull();
        downloader.addIntProperty("errorCode").notNull();
        downloader.addStringProperty("errorMsg");
        downloader.addStringProperty("taskClsName").notNull(); //自定义执行的任务类的名称
        downloader.addStringProperty("paramsClsName").notNull();//参数类的类名，用于反射生成参数类
        downloader.addStringProperty("downloadType");//用于区分不同类型下载任务，在同一客户端存在多个下载中心时使用
        downloader.addDateProperty("createTime").notNull();
        downloader.addBooleanProperty("softDelete").notNull();
        downloader.addStringProperty("extras");
        downloader.implementsSerializable();
    }

}
