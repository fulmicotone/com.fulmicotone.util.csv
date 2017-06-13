package com.fulmicotone.util.csv;


import java.io.*;
import java.util.List;
import java.util.Objects;

public class CSV {

    private CSV(){}

    public static class ReadOperation {

        private final FnCSVRowToAny decoderFn;
        private final boolean withHeaders;
        private Reader reader;

        private ReadOperation(FnCSVRowToAny decoderFn,
                              boolean withHeaders,
                              Reader reader) {
            this.decoderFn=decoderFn;
            this.withHeaders=withHeaders;
            this.reader =reader;
        }


        public FnCSVReader.CSVReadResult exec() {
            if(reader==null){return new FnCSVReader.CSVReadResult(new RuntimeException("reader is null"));}

            return  new FnCSVReader()
                    .apply(new FnCSVReader
                            .CSVReaderArgs<>(reader,
                            withHeaders,
                            decoderFn));
        }
    }

    public static class WriteOperation<Source> {
        private final List<Source> sources;
        private final FnAnyToCSVRow<Source> anyToCsvRowFn;
        private final CsvPath outputPath;
        private final List<String> headers;

        private WriteOperation(List<Source> sources,
                               List<String> headers,
                               FnAnyToCSVRow<Source> anyToCsvRowFn,
                               CsvPath outputPath) {

            this.sources = sources;
            this.anyToCsvRowFn = anyToCsvRowFn;
            this.outputPath = outputPath;
            this.headers=headers;

        }

        public FnCSVWriter.CSVWriteResult exec() {

            FnCSVWriter.CSVWriterArgs<Source> whereAndWhatWrite= new FnCSVWriter
                    .CSVWriterArgs<>(
                    outputPath.toString(),
                    headers,
                    anyToCsvRowFn,
                    sources);

            FnCSVWriter.CSVWriteResult writingResult =
                    new FnCSVWriter<Source>().apply(whereAndWhatWrite);
            return writingResult;
        }
    }

    public static <Source> ReadingBuilder  newReading(){ return new ReadingBuilder<Source>();}

    public static <Source> WriteBuilder  newWriting(){ return new WriteBuilder<Source>();}


    public static class ReadingBuilder<Target>{

        private FnCSVRowToAny<Target> func;
        private CsvPath csvPath;
        private boolean withHeaders=true;
        private ByteArrayOutputStream csvByteOutputStream;

        private  ReadingBuilder(){}

        public ReadingBuilder<Target> from(CsvPath csvFilePath){
            this.csvPath=csvFilePath;
            return this;
        }

        public ReadingBuilder<Target> from(ByteArrayOutputStream byteArrayOutputStream){
            this.csvByteOutputStream=byteArrayOutputStream;
            return this;
        }


        public ReadingBuilder<Target> withHeader(boolean withHeaders){
            this.withHeaders=withHeaders;
            return this;
        }


        public ReadingBuilder<Target> setDecoderFn(FnCSVRowToAny decoderFn){
            this.func=decoderFn;
            return this;
        }

        public ReadOperation create(){
            Objects.requireNonNull(this.func);

            Reader reader=null;
            if(this.csvByteOutputStream!=null){
                reader= new InputStreamReader(new ByteArrayInputStream(this.csvByteOutputStream.toByteArray()));
            }else if(this.csvPath!=null){
                try {
                    reader= new FileReader(this.csvPath.toString());
                } catch (FileNotFoundException e) {
                    System.out.println(e.toString());
                    reader=null;
                }
            }
            return new ReadOperation(func, withHeaders, reader);
        }
    }


    public  static class WriteBuilder<Source>{

        private List<Source> sources;
        private FnAnyToCSVRow<Source> func;
        private CsvPath csvPath;
        private List<String> headers;
        private  WriteBuilder(){}
        public WriteBuilder<Source> sources(List<Source> sources){ this.sources=sources; return this;}
        public WriteBuilder<Source> withHeaders(List<String> headers){this.headers=headers; return this;}
        public WriteBuilder<Source> setRowConverterFn(FnAnyToCSVRow<Source> anyToCsvRow){this.func =anyToCsvRow;
            return this;}
        public WriteBuilder<Source> onCsvFile(CsvPath csvPath){ this.csvPath=csvPath; return this;}

        public WriteOperation<Source> create(){

            Objects.requireNonNull(this.sources);
            Objects.requireNonNull(this.func);
            Objects.requireNonNull(this.csvPath);
            Objects.requireNonNull(this.headers);

            return new WriteOperation<>(sources, headers, func, csvPath);
        }
    }


}
