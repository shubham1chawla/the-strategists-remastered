package com.strategists.game.service;

import java.io.File;

public interface DataSyncService {

	void downloadCSVFiles(File directory);

	void uploadCSVFiles(File directory);

}
