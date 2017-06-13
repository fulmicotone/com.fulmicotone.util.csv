package com.fulmicotone.util.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Function;


public class FnCSVWriter<T> implements Function<FnCSVWriter.CSVWriterArgs<T>,FnCSVWriter.CSVWriteResult> {





    @Override
    public FnCSVWriter.CSVWriteResult apply(CSVWriterArgs <T>csvWriterArgs) {

        try {
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(csvWriterArgs.lineSeparator);
        PrintWriter writer = new PrintWriter(csvWriterArgs.outputPath, "UTF-8");
       final CSVPrinter printer =new CSVPrinter(writer, csvFileFormat);
        printer.printRecord(csvWriterArgs.headers);
        FnAnyToCSVRow<T> mapper=csvWriterArgs.mapFunction;
            for(T sourceObject:csvWriterArgs.sources){
                List<String> record =  mapper.apply(sourceObject);
                printer.printRecord(record);
            }
             writer.flush();
             writer.close();
             printer.close();

        } catch (IOException e ){
            System.out.print("error in FnCSVWriter :{} "+e.toString());
            return new CSVWriteResult(e);

        }
        return new CSVWriteResult();
    }

    public static class CSVWriteResult{

        private final Throwable exception;

        public  CSVWriteResult(){ this(null);}

        public  CSVWriteResult(Throwable exception){ this.exception=exception;}

        public boolean isOK(){ return this.exception==null; }

        public Throwable getException(){ return this.exception; }
    }


    public static class CSVWriterArgs<T>{
      private String outputPath;
        private String lineSeparator;
        private List<String> headers;
        private List<T> sources;
        private FnAnyToCSVRow<T> mapFunction;


        public CSVWriterArgs(String outputDir,
                             List<String> headers,
                             FnAnyToCSVRow<T> mapFunction,
                             List<T> sources) {


            this(outputDir,headers,"\n",mapFunction,sources);
        }

        public CSVWriterArgs(String outputDir,
                             List<String> headers,
                             String lineSeparator,
                             FnAnyToCSVRow<T> mapFunction, List<T> sources) {
            this.outputPath = outputDir;
            this.lineSeparator = lineSeparator;
            this.mapFunction = mapFunction;
            this.headers=headers;
            this.sources=sources;
        }





    }
}
