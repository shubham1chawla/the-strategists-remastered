package com.strategists.game.service;

import java.io.File;

public interface DataSyncService {

	void downloadGameCSVFiles(File directory);

	void uploadGameCSVFiles(File directory);

	void uploadAdviceCSVFiles(File directory);

}
