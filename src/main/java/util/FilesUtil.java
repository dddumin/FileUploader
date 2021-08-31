package util;

import com.google.gson.internal.bind.util.ISO8601Utils;
import exceptions.NameMatchException;
import exceptions.NodeNotFoundException;
import model.BTree;
import model.FileInfo;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import program.Program;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesUtil {
    public static ArrayList<FileInfo> getListFileInfoByPath(String path) {
        File rootFile = new File(path);
        File[] files = rootFile.listFiles();
        ArrayList<FileInfo> fileInfoArrayList = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                String hash = null;
                if (!isDirectory) {
                    try(InputStream inputStream = Files.newInputStream(file.toPath())) {
                        hash = DigestUtils.md5Hex(inputStream);
                    } catch (IOException ignored) {
                    }
                }
                String name = file.getName();
                String parentPath = StringPathUtil.getParentPathWithoutServerRoot(file.getParent());
                fileInfoArrayList.add(new FileInfo(name, parentPath, isDirectory, hash));
            }
        }
        return fileInfoArrayList;
    }

    public static BTree<FileInfo> getBTreeFileInfo(String path) throws NodeNotFoundException, NameMatchException {
        File root = new File(path);
        BTree<FileInfo> bTree = new BTree<>();
        if (root.exists() && root.isDirectory()) {
            String name = path.equals(Constants.SERVER_ROOT_DIR) ? "" : root.getName();
            String parentPath =  path.equals(Constants.SERVER_ROOT_DIR) ? "" : StringPathUtil.getParentPathWithoutServerRoot(root.getParent());
            bTree.add(null, "", new FileInfo(name, parentPath, true, null));
            FilesUtil.fillBTree(bTree, root, "");
        }
        return bTree;
    }

    private static void fillBTree(BTree<FileInfo> bTree, File rootFile, String rootNodeName) throws NodeNotFoundException, NameMatchException {
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                String hash = null;
                if (!isDirectory) {
                    try(InputStream inputStream = Files.newInputStream(file.toPath())) {
                        hash = DigestUtils.md5Hex(inputStream);
                    } catch (IOException ignored) {
                    }
                }
                String nodeName = rootNodeName.equals("") ? file.getName() : rootNodeName + File.separator + file.getName();
                bTree.add(rootNodeName, nodeName, new FileInfo(file.getName(), StringPathUtil.getParentPathWithoutServerRoot(file.getParent()), isDirectory, hash));
                if (isDirectory) {
                    fillBTree(bTree, file, nodeName);
                }
            }
        }
    }

    public static ArrayList<FileInfo> getListFileInfoByPathWithChildren (String path) {
        File file = new File(path);
        ArrayList<FileInfo> list = new ArrayList<>();
        fillListFileInfoWithChildren(file, list);
        return list;
    }

    private static void fillListFileInfoWithChildren (File rootFile, ArrayList<FileInfo> fileInfoArrayList) {
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean isDirectory = file.isDirectory();
                if (isDirectory) {
                    fileInfoArrayList.add(new FileInfo(file.getName(), file.getParent(), true, null));
                    fillListFileInfoWithChildren(file, fileInfoArrayList);
                } else {
                    try(InputStream inputStream = Files.newInputStream(file.toPath())) {
                        fileInfoArrayList.add(new FileInfo(file.getName(), file.getParent(), false, DigestUtils.md5Hex(inputStream)));
                    } catch (IOException ignored) {
                        System.out.println("Не удалось обработать файл " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private static boolean isFileExist(FileInfo fileInfo, List<FileInfo> fileInfoList) {
        String fileNameWithoutExtension = StringPathUtil.getFileNameWithoutExtension(fileInfo.getName());

        if (fileInfoList.stream()
                .filter(
                        curFileInfo -> curFileInfo.getName().equals(fileInfo.getName())
                                && curFileInfo.getHash().equals(fileInfo.getHash())
                ).findAny().orElse(null) != null) {
            return true;
        }
        String fileNameWithVersion = fileNameWithoutExtension + "(Version";
        return fileInfoList.stream()
                .filter(curFileInfo -> curFileInfo.getName().contains(fileNameWithVersion)
                        && curFileInfo.getHash().equals(fileInfo.getHash())).findAny().orElse(null) != null;
    }

    public static boolean isFileExist(FileItem fileItem, String rootPath) {
        File rootFile = new File(rootPath);
        File[] files = rootFile.listFiles();
        if (files != null) {
            if (Arrays.stream(files)
                    .filter(
                            file -> {
                                try {
                                    return file.getName().equals(fileItem.getName())
                            && DigestUtils.md5Hex(Files.newInputStream(file.toPath())).equals(DigestUtils.md5Hex(fileItem.getInputStream()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }).findAny().orElse(null) != null) {
                return true;
            } else {
                String fileNameWithVersion = StringPathUtil.getFileNameWithoutExtension(fileItem.getFieldName()) + "(Version";
                return Arrays.stream(files)
                        .filter(
                                file -> {
                                    try {
                                        return file.getName().contains(fileNameWithVersion)
                                                && DigestUtils.md5Hex(Files.newInputStream(file.toPath())).equals(DigestUtils.md5Hex(fileItem.getInputStream()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return false;
                                }).findAny().orElse(null) != null;
            }
        }
        return false;
    }

    public static String getNameFile(String path, String fileName) {
        String fileExtension = StringPathUtil.getFileExtension(fileName);
        String fileNameWithoutExtension = StringPathUtil.getFileNameWithoutExtension(fileName);

        File rootFolder = new File(path);
        String[] list = rootFolder.list();
        if (list != null) {
            List<String> names = Arrays.asList(list);
            if (!names.contains(fileNameWithoutExtension + fileExtension)) {
                return fileName;
            } else {
                String fileNameWithVersion = fileNameWithoutExtension + "(Version";
                int count = 0;
                for (String name : names) {
                    if (name.contains(fileNameWithVersion)) {
                        count++;
                    }
                }
                return fileNameWithoutExtension + String.format("(Version %d)", count + 1) + fileExtension;
            }
        }
        return fileName;
    }

    public static void processArchive(String path, String charset) throws NodeNotFoundException, NameMatchException, IOException {
        ZipFile zipFile = new ZipFile(path);
        zipFile.setCharset(Charset.forName(charset));
        File file = zipFile.getFile();
        String parentPathZipFile = file.getParent();

        BTree<FileInfo> bTreeFileInfoServerFolder = FilesUtil.getBTreeFileInfo(parentPathZipFile);

        String processFolderName = StringPathUtil.getFileNameWithoutExtension(file.getName()) + "-Processing";

        String processFolder = StringPathUtil.getFileNameWithoutExtension(file.getAbsolutePath())  + "-Processing";
        String folderForExtract = processFolder + File.separator + StringPathUtil.getFileNameWithoutExtension(file.getName());

        zipFile.extractAll(folderForExtract);

        ArrayList<FileInfo> listFileInfoByPathWithChildren = getListFileInfoByPathWithChildren(folderForExtract);
        listFileInfoByPathWithChildren.stream().filter(fileInfo -> fileInfo.getName().endsWith(".zip")).forEach(fileInfo -> {
            try {
                processArchive(fileInfo.getAbsolutePath(), charset);
            } catch (IOException | NodeNotFoundException | NameMatchException e) {
                System.out.println("Ошибка обработки внутреннего архива");
                e.printStackTrace();
            }
        });

        BTree<FileInfo> bTreeFileInfoProcessingFolder = FilesUtil.getBTreeFileInfo(processFolder);

        ArrayList<FileInfo> differenceBetweenBTree = BTree.getDifferenceBetweenBTree(bTreeFileInfoProcessingFolder, bTreeFileInfoServerFolder);

        for (FileInfo fileInfo : differenceBetweenBTree) {
            if (fileInfo.isFolder() || !isFileExist(fileInfo, getListFileInfoByPath(Constants.SERVER_ROOT_DIR + File.separator + fileInfo.getParentPath().replace(processFolderName, "")))) {
                Path source = Paths.get(Constants.SERVER_ROOT_DIR + File.separator + fileInfo.getAbsolutePath());
                String targetParentPath = Constants.SERVER_ROOT_DIR + File.separator + fileInfo.getParentPath().replace(processFolderName, "");
                Path target = Paths.get(targetParentPath + File.separator + getNameFile(targetParentPath, fileInfo.getName()));

                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        file.delete();
        FileUtils.deleteDirectory(new File(processFolder));
    }

    public static File getZipFile(String path) {
        ZipFile zipFile = new ZipFile(path + ".zip");
        File folder = new File(path);
        File[] files = folder.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                try {
                    if (file.isDirectory()) {
                        zipFile.addFolder(file);
                    } else {
                        zipFile.addFile(file);
                    }
                } catch (ZipException e) {
                    e.printStackTrace();
                }
            });
        }
        return zipFile.getFile();
    }
}
