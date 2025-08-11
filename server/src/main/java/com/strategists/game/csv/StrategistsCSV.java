package com.strategists.game.csv;

import lombok.val;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface StrategistsCSV {

    List<String> getHeaders();

    List<Map<String, Object>> getRowMaps();

    String getDefaultFileName();

    default Object getValue(int index, String header) {
        var rowMap = getRowMap(index);
        if (!rowMap.containsKey(header)) {
            throw new IllegalArgumentException(String.format("Invalid header: '%s'", header));
        }
        return rowMap.get(header);
    }

    default List<Object> getRow(int index) {
        return getRowMap(index).values().stream().toList();
    }

    default Map<String, Object> getRowMap(int index) {
        if (index < 0 || index >= getRowsCount()) {
            throw new IllegalArgumentException(String.format("CSV index must be [0, %s)", getRowsCount()));
        }
        return getRowMaps().get(index);
    }

    default int getRowsCount() {
        return getRowMaps().size();
    }

    default List<List<Object>> getRows() {
        return getRowMaps().stream().map(m -> new ArrayList<>(m.values())).collect(Collectors.toUnmodifiableList());
    }

    default File export(File directory) throws IOException {
        return export(directory, getDefaultFileName());
    }

    default File export(File directory, String fileName) throws IOException {
        // Preparing CSV file
        fileName = fileName.trim();
        if (!fileName.endsWith(".csv")) {
            fileName += ".csv";
        }
        val csv = new File(directory, fileName);
        val format = CSVFormat.DEFAULT.builder().setHeader(getHeaders().toArray(String[]::new)).build();
        var printer = new CSVPrinter(new FileWriter(csv), format);

        // Adding rows to the CSV file
        for (var values : getRows()) {
            printer.printRecord(values);
        }

        printer.close();
        return csv;
    }

}
