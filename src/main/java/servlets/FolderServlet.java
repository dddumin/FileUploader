package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.NameMatchException;
import exceptions.NodeNotFoundException;
import model.BTree;
import model.FileInfo;
import util.Constants;
import util.FilesUtil;
import util.GsonExclusionStrategy;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

@WebServlet("/folder")
public class FolderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");

        req.setCharacterEncoding("utf-8");

        String path = req.getParameter("path");

        PrintWriter writer = resp.getWriter();
        if (path != null) {
            if (!Files.exists(Paths.get(Constants.SERVER_ROOT_DIR + File.separator + path))) {
                writer.write(new Gson().toJson(null));
            } else {
                try {
                    writer.write(new GsonBuilder()
                            .addSerializationExclusionStrategy(new GsonExclusionStrategy())
                            .create()
                            .toJson(FilesUtil.getBTreeFileInfo(Constants.SERVER_ROOT_DIR + File.separator + path)));
                } catch (NodeNotFoundException | NameMatchException e) {
                    e.printStackTrace();
                    resp.setStatus(400);
                }
            }

        }
    }
}
