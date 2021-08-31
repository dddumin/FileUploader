package servlets;

import com.google.gson.Gson;
import model.FileInfo;
import model.ServerError;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import util.Constants;
import util.FilesUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

@WebServlet("/file")
public class FileServlet extends HttpServlet {
    /**
     * return List<FileInfo> (name, type, hash(only fo file)) by path
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        req.setCharacterEncoding("utf-8");

        String path = req.getParameter("path");
        String name = req.getParameter("name");

        if (path != null) {
            if (name == null) {
                List<FileInfo> list = FilesUtil.getListFileInfoByPath(Constants.SERVER_ROOT_DIR + File.separator + path);
                resp.getWriter().println(new Gson().toJson(list));
            } else {
                ServletContext cntx = req.getServletContext();
                String absolutePath = Constants.SERVER_ROOT_DIR + File.separator + path + File.separator + name;
                File file = new File(absolutePath);
                if (file.isDirectory()) {
                    file = FilesUtil.getZipFile(absolutePath);
                }
                String mime = cntx.getMimeType(file.getAbsolutePath());
                if (mime == null) {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().println("Incorrect type file");
                    resp.setStatus(500);
                    return;
                }
                try (FileInputStream in = new FileInputStream(file);
                     OutputStream out = resp.getOutputStream()) {
                    resp.setContentType(mime);
                    resp.setContentLength((int) file.length());
                    long length = in.transferTo(out);
                    System.out.println("Bytes transferred: " + length);
                } catch (FileNotFoundException e) {
                    resp.getWriter().println("Incorrect file name");
                    resp.setStatus(500);
                } catch (IOException e) {
                    resp.getWriter().println("File Error!");
                    resp.setStatus(500);
                }
                new File(absolutePath + ".zip").delete();
            }
        }
    }

    /**
     *
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        req.setCharacterEncoding("utf-8");

        PrintWriter writer = resp.getWriter();

        if (ServletFileUpload.isMultipartContent(req)) {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(new File(Constants.SERVER_ROOT_DIR));

            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                List<FileItem> formItems = upload.parseRequest(req);
                if (formItems != null && formItems.size() > 0) {
                    String rootDir = Constants.SERVER_ROOT_DIR;
                    String charset = null;
                    for (FileItem item : formItems) {
                        if (item.isFormField()) {
                            if (item.getFieldName().equals("cp866") || item.getFieldName().equals("utf-8")) {
                                charset = item.getFieldName();
                            } else {
                                rootDir += File.separator + item.getFieldName();
                            }
                        }
                        if (!item.isFormField()) {
                            String fileName = FilesUtil.getNameFile(rootDir, item.getName());
                            String filePath = rootDir + File.separator + fileName;

                            File file = new File(filePath);
                            item.write(file);


                            if (fileName.endsWith(".zip")) {
                                FilesUtil.processArchive(filePath, charset);
                            }

                        }
                    }
                }
            } catch (Exception e) {
                writer.println(new Gson().toJson(new ServerError(e.getMessage())));
                resp.setStatus(400);
            }
        } else {
            writer.println(new Gson().toJson(new ServerError("No multipart")));
            resp.setStatus(400);
        }
    }

    /**
     * Delete file from server by Path
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        req.setCharacterEncoding("utf-8");

        String path = req.getParameter("path");

        PrintWriter writer = resp.getWriter();

        if (path != null) {
            String curPath = Constants.SERVER_ROOT_DIR + File.separator + path;
            File file = new File(curPath);
            if (!file.isDirectory()) {
                if (file.delete()) {
                    List<FileInfo> list = FilesUtil.getListFileInfoByPath(file.getParent());
                    writer.println(new Gson().toJson(list));
                } else {
                    writer.println(new Gson().toJson(new ServerError("Ошибка при удалении файла!!!")));
                    resp.setStatus(400);
                }
            } else {
                try {
                    FileUtils.deleteDirectory(file);
                    List<FileInfo> list = FilesUtil.getListFileInfoByPath(file.getParent());
                    writer.println(new Gson().toJson(list));
                } catch (Exception e) {
                    writer.println(new Gson().toJson("Ошибка при удалении файла!!!"));
                    resp.setStatus(400);
                }
            }
        }
    }
}
