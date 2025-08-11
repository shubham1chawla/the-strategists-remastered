package com.strategists.game.service;

import com.strategists.game.request.DownloadGoogleDriveFilesRequest;
import com.strategists.game.request.UploadLocalFilesRequest;
import com.strategists.game.response.DownloadGoogleDriveFilesResponse;
import com.strategists.game.response.UploadLocalFilesResponse;

import java.util.Optional;

public interface StorageService {

    Optional<DownloadGoogleDriveFilesResponse> downloadGoogleDriveFiles(DownloadGoogleDriveFilesRequest request);

    Optional<UploadLocalFilesResponse> uploadLocalFiles(UploadLocalFilesRequest request);

}
