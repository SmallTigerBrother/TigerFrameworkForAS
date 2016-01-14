package com.mn.tiger.utility;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.mn.tiger.log.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类作用及功能说明 文件和文件夹相关的操作
 *
 * @date 2014-2-11
 */
public class FileUtils
{
    private static final Logger LOG = Logger.getLogger(FileUtils.class);

    /**
     * 该方法的作用:获取SD卡路径
     *
     * @return
     * @date 2014年1月17日
     */
    public static String getSDCardPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 该方法的作用: 判断SD卡是否可用
     *
     * @return
     * @date 2013-10-28
     */
    public static boolean isSDCardAvailable()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    }

    /**
     * 该方法的作用: 获取SD卡剩余空间,单位byte，若SD卡不可用，返回0
     *
     * @return
     * @date 2013-10-28
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize()
    {
        if (isSDCardAvailable())
        {
            StatFs statFs = new StatFs(getSDCardPath());

            long blockSize = statFs.getBlockSize();

            long freeBlocks = statFs.getAvailableBlocks();
            return freeBlocks * blockSize;
        }

        return 0;
    }

    /**
     * 该方法的作用: 获取SD卡的总容量，单位byte，若SD卡不可用，返回0
     *
     * @return
     * @date 2013-10-28
     */
    @SuppressWarnings("deprecation")
    public static long getSDAllSize()
    {
        if (isSDCardAvailable())
        {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 该方法的作用:获取指定路径所在空间的剩余可用容量字节数
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     * @date 2014-1-23
     */
    @SuppressWarnings("deprecation")
    public static long getFreeBytes(String filePath)
    {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath()))
        {
            filePath = getSDCardPath();
        }
        else
        {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 该方法的作用:拷贝文件，通过返回值判断是否拷贝成功
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return
     * @date 2014-1-23
     */
    public static boolean copyFile(String sourcePath, String targetPath)
    {
        boolean isOK = false;
        if (!StringUtils.isEmptyOrNull(sourcePath) && !StringUtils.isEmptyOrNull(targetPath))
        {
            File sourceFile = new File(sourcePath);
            File targetFile = new File(targetPath);
            if (!sourceFile.exists())
            {
                return false;
            }
            if (sourceFile.isDirectory())
            {
                isOK = copyDirectory(sourceFile, targetFile);
            }
            else if (sourceFile.isFile())
            {
                if (!targetFile.exists())
                {
                    createFile(targetPath);
                }
                FileOutputStream outputStream = null;
                FileInputStream inputStream = null;
                try
                {
                    inputStream = new FileInputStream(sourceFile);
                    outputStream = new FileOutputStream(targetFile);
                    byte[] bs = new byte[1024];
                    int len;
                    while ((len = inputStream.read(bs)) != -1)
                    {
                        outputStream.write(bs, 0, len);
                    }
                    isOK = true;
                }
                catch (Exception e)
                {
                    LOG.e("[Method:copyFile]", e);
                    isOK = false;
                }
                finally
                {
                    if (inputStream != null)
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException e)
                        {
                            LOG.e("[Method:copyFile]", e);
                        }
                    }
                    if (outputStream != null)
                    {
                        try
                        {
                            outputStream.close();
                        }
                        catch (IOException e)
                        {
                            LOG.e("[Method:copyFile]", e);
                        }
                    }
                }
            }

            return isOK;
        }
        return false;
    }

    /**
     * 该方法的作用:删除文件
     *
     * @param path
     * @return
     * @date 2014-1-23
     */
    public static boolean deleteFile(String path)
    {
        if (!StringUtils.isEmptyOrNull(path))
        {
            File file = new File(path);
            if (!file.exists())
            {
                return false;
            }

            if (file.isFile())
            {
                try
                {
                    file.delete();
                }
                catch (Exception e)
                {
                    LOG.e("[Method:deleteFile]", e);
                    return false;
                }
            }
            else if (file.isDirectory())
            {
                FileUtils.deleteDirectory(path);
            }

            return true;
        }
        return false;
    }

    /**
     * 剪切文件，将文件拷贝到目标目录，再将源文件删除
     *
     * @param sourcePath
     * @param targetPath
     */
    public static boolean cutFile(String sourcePath, String targetPath)
    {
        boolean isSuccessful = copyFile(sourcePath, targetPath);
        if (isSuccessful)
        {
            // 拷贝成功则删除源文件
            return deleteFile(sourcePath);
        }
        return false;
    }

    /**
     * 该方法的作用: 拷贝目录
     *
     * @param sourceFile
     * @param targetFile
     * @return
     * @date 2014-1-23
     */
    public static boolean copyDirectory(File sourceFile, File targetFile)
    {
        if (sourceFile == null || targetFile == null)
        {
            return false;
        }
        if (!sourceFile.exists())
        {
            return false;
        }
        if (!targetFile.exists())
        {
            targetFile.mkdirs();
        }
        // 获取目录下所有文件和文件夹的列表
        File[] files = sourceFile.listFiles();
        if (files == null || files.length < 1)
        {
            return false;
        }
        File file = null;
        StringBuffer buffer = new StringBuffer();
        boolean isSuccessful = false;
        // 遍历目录下的所有文件文件夹，分别处理
        for (int i = 0; i < files.length; i++)
        {
            file = files[i];
            buffer.setLength(0);
            buffer.append(targetFile.getAbsolutePath()).append(File.separator)
                    .append(file.getName());
            if (file.isFile())
            {
                // 文件直接调用拷贝文件方法
                isSuccessful = copyFile(file.getAbsolutePath(), buffer.toString());
                if (!isSuccessful)
                {
                    return false;
                }
            }
            else if (file.isDirectory())
            {
                // 目录再次调用拷贝目录方法
                copyDirectory(file, new File(buffer.toString()));
            }

        }
        return true;
    }

    /**
     * 该方法的作用:剪切目录，先将目录拷贝完后再删除源目录
     *
     * @param sourceDirectory
     * @param targetDirectory
     * @return
     * @date 2014-1-23
     */
    public static boolean cutDirectory(String sourceDirectory, String targetDirectory)
    {
        File sourceFile = new File(sourceDirectory);
        File targetFile = new File(targetDirectory);
        boolean isCopySuccessful = copyDirectory(sourceFile, targetFile);
        if (isCopySuccessful)
        {
            return deleteDirectory(sourceDirectory);
        }
        return false;
    }

    /**
     * 该方法的作用:删除目录
     *
     * @param path
     * @return
     * @date 2014-2-12
     */
    public static boolean deleteDirectory(String path)
    {
        File file = new File(path);
        if (!file.exists())
        {
            return false;
        }
        File[] files = file.listFiles();
        boolean isSuccessful = false;
        if (files.length == 0)
        {
            file.delete();
            return true;
        }
        // 对所有列表中的路径进行判断是文件还是文件夹
        for (int i = 0; i < files.length; i++)
        {
            if (files[i].isDirectory())
            {
                isSuccessful = deleteDirectory(files[i].getAbsolutePath());
            }
            else if (files[i].isFile())
            {
                isSuccessful = deleteFile(files[i].getAbsolutePath());
            }

            if (!isSuccessful)
            {
                // 如果有删除失败的情况直接跳出循环
                break;
            }
        }
        if (isSuccessful)
        {
            file.delete();
        }
        return isSuccessful;
    }

    /**
     * 该方法的作用:将流写入指定文件
     *
     * @param inputStream
     * @param path
     * @return
     * @date 2014-2-12
     */
    public static boolean streamWriteFile(InputStream inputStream, String path)
    {
        File file = new File(path);
        boolean isSuccessful = true;
        FileOutputStream fileOutputStream = null;
        try
        {
            if (!file.exists())
            {
                File file2 = file.getParentFile();
                file2.mkdirs();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            byte[] bs = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(bs)) != -1)
            {
                fileOutputStream.write(bs, 0, length);
            }
        }
        catch (Exception e)
        {
            LOG.e("[Method:streamWriteFile]", e);
            isSuccessful = false;
        }
        finally
        {
            try
            {
                if (fileOutputStream != null)
                {
                    fileOutputStream.close();
                }
            }
            catch (IOException e)
            {
                LOG.e("[Method:streamWriteFile]", e);
            }
        }
        return isSuccessful;
    }

    /**
     * 该方法的作用:创建目录
     *
     * @param path
     * @date 2014-1-23
     */
    public static void createDir(String path)
    {
        File file = new File(path);
        if (!file.exists())
        {
            file.mkdirs();
        }
    }

    /**
     * 该方法的作用:修改文件读写权限
     *
     * @param fileAbsPath
     * @param mode
     * @date 2013-3-7
     */
    public static void chmodFile(String fileAbsPath, String mode)
    {
        String cmd = "chmod " + mode + " " + fileAbsPath;
        try
        {
            Runtime.getRuntime().exec(cmd);
        }
        catch (Exception e)
        {
            LOG.e("[Method:chmodFile]", e);
        }
    }

    /**
     * 该方法的作用:创建文件，并写入指定内容
     *
     * @param path
     * @param content
     * @date 2013-3-7
     */
    public static void createFileThroughContent(String path, String content)
    {
        File myFile = new File(path);
        if (!myFile.exists())
        {
            try
            {
                myFile.createNewFile();
            }
            catch (IOException e)
            {
                LOG.e("[Method:createFileThroughContent]", e);
            }
        }

        if (null != content)
        {
            FileWriter fw = null;
            try
            {
                fw = new FileWriter(myFile);
                fw.write(content);
                fw.flush();
            }
            catch (IOException e)
            {
                LOG.e("[Method:createFileThroughContent]", e);
            }
            finally
            {
                if (fw != null)
                {
                    try
                    {
                        fw.close();
                    }
                    catch (IOException e)
                    {
                        LOG.e("[Method:createFileThroughContent]", e);
                    }
                }
            }
        }
    }

    /**
     * 该方法的作用:将object对象写入outFile文件
     *
     * @param outFile
     * @param object
     * @param context
     * @date 2014-2-12
     */
    public static void writeObject(String outFile, Object object, Context context)
    {
        ObjectOutputStream out = null;
        FileOutputStream outStream = null;
        try
        {
            File dir = context.getDir("cache", Context.MODE_PRIVATE);
            outStream = new FileOutputStream(new File(dir, outFile));
            out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.writeObject(object);
            out.flush();
        }
        catch (Exception e)
        {
            LOG.e("[Method:writeObject]", e);
        }
        finally
        {
            if (outStream != null)
            {
                try
                {
                    outStream.close();
                }
                catch (IOException e)
                {
                    LOG.e("[Method:writeObject]", e);
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    LOG.e("[Method:writeObject]", e);
                }
            }
        }
    }

    /**
     * 该方法的作用:从outFile文件读取对象
     *
     * @param filePath
     * @param context
     * @return
     * @date 2014-2-12
     */
    public static Object readObject(String filePath, Context context)
    {
        Object object = null;
        ObjectInputStream in = null;
        FileInputStream inputStream = null;
        try
        {
            File dir = context.getDir("cache", Context.MODE_PRIVATE);
            File f = new File(dir, filePath);
            if (f == null || !f.exists())
            {
                return null;
            }
            inputStream = new FileInputStream(new File(dir, filePath));
            in = new ObjectInputStream(new BufferedInputStream(inputStream));
            object = in.readObject();
        }
        catch (Exception e)
        {
            LOG.e("[Method:readObject]", e);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    LOG.e("[Method:readObject]", e);
                }
            }
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    LOG.e("[Method:readObject]", e);
                }
            }

        }
        return object;
    }

    /**
     * 该方法的作用:读取指定路径下的文件内容
     *
     * @param path
     * @return 文件内容
     * @date 2013-3-7
     */
    public static String readFile(String path)
    {
        BufferedReader br = null;
        try
        {
            File myFile = new File(path);
            br = new BufferedReader(new FileReader(myFile));
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            while (line != null)
            {
                sb.append(line);
                line = br.readLine();
            }
            return sb.toString();
        }
        catch (Exception e)
        {
            LOG.e("[Method:readFile]", e);
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    LOG.e("[Method:readFile]", e);
                }
            }
        }
        return null;
    }

    /**
     * 从文件中按行读取String列表
     *
     * @param context
     * @param path
     * @return
     */
    public static List<String> readStringListByLine(Context context, String path)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            InputStream in = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null)
            {
                list.add(str);
            }

            return list;
        }
        catch (IOException e)
        {
            LOG.e("[Method:readStringListByLine]", e);
        }

        return list;
    }

    /**
     * 从Asset的文件中读取字符串
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readStringFromAsset(Context context, String fileName)
    {
        try
        {
            BufferedInputStream inputStream = new BufferedInputStream(context.getAssets().open(fileName));
            StringBuilder stringBuilder = new StringBuilder();
            byte[] buffer = new byte[4 * 1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1)
            {
                stringBuilder.append(new String(buffer, 0, length));
            }

            return stringBuilder.toString();
        }
        catch (IOException e)
        {
            LOG.e("[Method:readStringFromAsset]", e);
        }

        return null;
    }

    /**
     * 从Asset里面按行读取String列表
     *
     * @param context
     * @param fileName
     * @return
     */
    public static List<String> readStringListByLineFromAsset(Context context, String fileName)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            InputStream in = context.getResources().getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null)
            {
                list.add(str);
            }

            return list;
        }
        catch (IOException e)
        {
            LOG.e("[Method:readStringListByLineFromAsset]", e);
        }

        return list;
    }


    /**
     * 该方法的作用:创建文件，并修改读写权限
     *
     * @param filePath
     * @param mode
     * @return
     * @date 2013-3-7
     */
    public static File createFile(String filePath, String mode)
    {
        File desFile = null;
        try
        {
            String desDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
            File dir = new File(desDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            chmodFile(dir.getAbsolutePath(), mode);
            desFile = new File(filePath);
            if (!desFile.exists())
            {
                desFile.createNewFile();
            }
            chmodFile(desFile.getAbsolutePath(), mode);
        }
        catch (Exception e)
        {
            LOG.e("[Method:createFile]", e);
        }
        return desFile;
    }

    /**
     * 该方法的作用:根据指定路径，创建父目录及文件
     *
     * @param filePath
     * @return File 如果创建失败的话，返回null
     * @date 2013-3-6
     */
    public static File createFile(String filePath)
    {
        return createFile(filePath, "755");
    }

    /**
     * 该方法的作用:获取系统存储路径
     *
     * @return
     * @date 2014-1-23
     */
    public static String getRootDirectoryPath()
    {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 该方法的作用:获取外部存储路径
     *
     * @return
     * @date 2014-1-23
     */
    public static String getExternalStorageDirectoryPath()
    {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 该方法的作用: 根据文件路径获取文件大小
     *
     * @param path
     * @return
     * @date 2014年7月16日
     */
    public static long getFileSize(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            return file.length();
        }

        return -1;
    }

    /**
     * 该方法的作用: 根据路径获取文件名
     *
     * @param path
     * @return
     * @date 2014年7月16日
     */
    public static String getFileName(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            return file.getName();
        }

        return "";
    }

    /**
     * 该方法的作用: 根据路径获取文件
     *
     * @param path
     * @return
     * @date 2014年7月16日
     */
    public static File getFile(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return null;
        }

        File file = new File(path);
        if (file.exists())
        {
            return file;
        }

        return null;
    }

    /**
     * 获取目录/文件大小
     *
     * @param file
     * @return
     */
    public static double getDirSize(File file)
    {
        // 判断文件是否存在
        if (file.exists())
        {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory())
            {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                {
                    size += getDirSize(f);
                }
                return size;
            }
            else
            {// 如果是文件则直接返回其大小
                double size = (double) file.length();
                return size;
            }
        }
        else
        {
            LOG.w("[Method:getDirSize] file is not exist, filePath == " + file.getAbsolutePath());
            return 0.0;
        }
    }
}
