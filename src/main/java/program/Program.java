package program;

import com.google.gson.GsonBuilder;
import exceptions.NameMatchException;
import exceptions.NodeNotFoundException;
import model.BTree;
import model.FileInfo;
import net.lingala.zip4j.exception.ZipException;
import util.FilesUtil;
import util.GsonExclusionStrategy;
import util.StringPathUtil;

import java.io.IOException;
import java.util.ArrayList;

public class Program {
    public static void main(String[] args) throws NodeNotFoundException, NameMatchException {
        /*String parentPathWithoutServerRoot = StringPathUtil.getParentPathWithoutServerRoot("C:\\File_uploader_files\\Новая папка\\Новая папка");
        System.out.println(parentPathWithoutServerRoot);*/

        /*ArrayList<FileInfo> listFileInfoByPath = FilesUtil.getListFileInfoByPath("C:\\File_uploader_files\\Новая папка\\Новая папка");
        System.out.println(listFileInfoByPath);*/

        /*BTree<FileInfo> bTreeFileInfo = FilesUtil.getBTreeFileInfo("C:\\File_uploader_files");
        printJson(bTreeFileInfo);

        BTree<FileInfo> bTreeFileInfoUser = FilesUtil.getBTreeFileInfo("C:\\File_uploader_files\\Новая папка (2)");
        printJson(bTreeFileInfoUser);

        ArrayList<FileInfo> differenceBetweenBTree = BTree.getDifferenceBetweenBTree(bTreeFileInfoUser, bTreeFileInfo);
        System.out.println(differenceBetweenBTree);*/

        /*try {
            FilesUtil.processArchive("C:\\File_uploader_files\\Новая папка\\Новая папка.zip", "cp866");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public static void printJson(Object object) {
        System.out.println(new GsonBuilder().addSerializationExclusionStrategy(new GsonExclusionStrategy()).setPrettyPrinting().create().toJson(object));

    }
}
