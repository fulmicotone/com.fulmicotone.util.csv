package com.fulmicotone.util.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Created by dino on 29/09/16.
 */
public class FnCSVReaderRaw implements BiFunction<String, Boolean, Optional<Iterable<CSVRecord>>> {

    @Override
    public Optional<Iterable<CSVRecord>> apply(String filepath, Boolean withHeader) {
        try {
             Reader in = new FileReader(filepath);
             return Optional.of(withHeader ? CSVFormat.EXCEL
                     .withHeader().parse(in) : CSVFormat.EXCEL.parse(in));
        }
        catch (IOException e) {
            System.out.print("Error on csv reading: "+e.toString());
            return Optional.empty();
        }
    }
}
