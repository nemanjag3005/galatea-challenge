package com.quickstep.utils;

import com.google.common.base.Strings;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;

import static java.io.File.separator;

/**
 * User: Ben
 * Date: 13/07/11
 * Time: 3:24 PM
 */
@Singleton
public class PathService {

    /* Directories in base of workspace */
    private static final String TEMP_CONTENT = "temp_content";
    /* Temp Content Folders */
    private static final String TEMP_DIRECTORY = TEMP_CONTENT + separator + "temp";
    private static final String DEV_EMAIL_TEST_REPOSITORY = TEMP_CONTENT + separator + "dev_email_tests";
    private static final String FILE_UPLOAD_DIRECTORY = TEMP_CONTENT + separator + "files";

    private final File workspace;

    private final File devEmailTestRepository;
    private final File tempDirectory;
    private final File fileUploadDirectory;

    private final ServerUtils serverUtils;


    @Inject
    public PathService(@Named("demo.config.workspace") String workspaceLocation, ServerUtils serverUtils) {
        /* Initializes workspace base folder first */
        {
            workspace = new File(workspaceLocation);
            if (workspace.exists()) {
                if (!workspace.isDirectory()) {
                    throw new IllegalArgumentException("Base Workspace folder at \"" + workspaceLocation + "\" is not a directory!");
                }
            } else {
                workspace.mkdirs();
            }
        }
        tempDirectory = getNamedDirectory(TEMP_DIRECTORY, "Temp directory", true);
        //When in prod mode we do not need to validate that the email tests repository is there at it should never be used
        if (serverUtils.isEmailEnabled()) {
            devEmailTestRepository = new File(workspace, DEV_EMAIL_TEST_REPOSITORY);
        } else {
            devEmailTestRepository = getNamedDirectory(DEV_EMAIL_TEST_REPOSITORY, "Development email test repository", true);
        }
        fileUploadDirectory = getNamedDirectory(FILE_UPLOAD_DIRECTORY, "File upload test directory", true);
        this.serverUtils = serverUtils;
    }

    public File getTempDirectory() {
        return tempDirectory;
    }

    public File getDevEmailTestRepository() {
        return devEmailTestRepository;
    }

    public File getFileUploadDirectory() {
        return fileUploadDirectory;
    }

    private File getNamedDirectory(String filePath, String name, boolean createIfNotExist) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("System folders must be named!");
        }
        final File result = new File(workspace, filePath);
        if (result.exists()) {
            if (result.isDirectory()) {
                return result;
            } else {
                throw new RuntimeException("\"" + name + "\" at \"" + filePath + "\" is not a folder!");
            }
        } else {
            final Boolean createDirResult = createIfNotExist && result.mkdirs();
            if (createDirResult) {
                return result;
            } else {
                throw new RuntimeException("\"" + name + "\" does not exist at \"" + filePath + "\"");
            }
        }
    }
}
