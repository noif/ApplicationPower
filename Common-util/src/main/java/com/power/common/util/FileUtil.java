package com.power.common.util;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu on 2017/3/9.
 */
public class FileUtil {

    private static final String DEFAULT_CHARSET = "utf-8";

    /**
     * make dir
     *
     * @param path file path
     * @return boolean
     */
    public static boolean mkdir(String path) {
        File file = new File(path);
        return !file.exists() && file.mkdir();
    }

    /**
     * make dirs
     *
     * @param path file path
     * @return boolean
     */
    public static boolean mkdirs(String path) {
        File file = new File(path);
        return !file.exists() && file.mkdirs();
    }

    /**
     * use nio copy file
     *
     * @param source source file
     * @param target target file
     */
    public static void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inStream) {
                    inStream.close();
                }
                if (null != in) {
                    in.close();
                }
                if (null != outStream) {
                    outStream.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Convert InputStream to byte array
     *
     * @param inStream InputStream
     * @return byte array
     * @throws IOException io exception
     */
    public static byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    /**
     * write string contents to file
     *
     * @param source   string contents
     * @param filePath local file path
     * @param append   append operate
     * @return boolean
     */
    public static boolean writeFile(String source, String filePath, boolean append) {
        return writeFile(source, filePath, append, DEFAULT_CHARSET);
    }

    /**
     * write string contents to file,overwrite any existing file
     *
     * @param source   string content
     * @param filePath file path
     * @return boolean
     */
    public static boolean writeFileNotAppend(String source, String filePath) {
        return writeFile(source, filePath, false, DEFAULT_CHARSET);
    }

    /**
     * write by OutPutStreamWriter
     *
     * @param source   String source
     * @param filePath File path
     * @param append   is append
     * @param encoding encoding
     * @return boolean
     */
    public static boolean writeFile(String source, String filePath, boolean append, String encoding) {
        boolean flag;
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(filePath, append), encoding);
            osw.write(source);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * write by BufferedWriter
     *
     * @param source String source
     * @param file   File
     * @param append append operate
     * @return boolean
     */
    public static boolean writeFile(String source, File file, boolean append) {
        boolean flag;
        BufferedWriter output = null;
        try {
            file.createNewFile();
            output = new BufferedWriter(new FileWriter(file, append));
            output.write(source);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (null != output) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * get file content
     *
     * @param fileName file name
     * @return String
     */
    public static String getFileContent(String fileName) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(fileName);
            return getFileContent(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get String by input stream
     *
     * @param inputStream InputStream
     * @return String
     */
    public static String getFileContent(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuilder fileContent = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
                fileContent.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent.toString();
    }

    /**
     * get files from folder
     *
     * @param folder folder
     * @return files array
     */
    public static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        if(null == url){
            throw new RuntimeException("url is null");
        }
        String path = url.getPath();
        return new File(path).listFiles();
    }

    /**
     * Use nio write file
     *
     * @param filePath file path
     * @param contents string contents
     * @return boolean
     */
    public static boolean nioWriteFile(String filePath, String contents) {
        return nioWriteFile(filePath, contents, null);
    }

    /**
     * Appending The New Data To The Existing File
     *
     * @param filePath file path
     * @param contents string contents
     * @return boolean
     */
    public static boolean nioWriteAppendable(String filePath, String contents) {
        return nioWriteFile(filePath, contents, StandardOpenOption.APPEND);
    }

    /**
     * Use nio write file
     *
     * @param filePath   file path
     * @param contents   string contents
     * @param openOption open or create options
     * @return boolean
     */
    private static boolean nioWriteFile(String filePath, String contents, OpenOption openOption) {
        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            if (null == openOption) {
                Files.write(path, contents.getBytes(DEFAULT_CHARSET));
            } else {
                Files.write(path, contents.getBytes(DEFAULT_CHARSET), openOption);
            }
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
