package com.strategists.game.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.io.File;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DownloadGoogleDriveFilesRequest {

    private String googleDriveFolderId;
    private String mimetype;
    private String fileExtension;
    private File localDataDirectory;

}
