package com.fulmicotone.util.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


public class FnCSVReader<T>implements Function<
        FnCSVReader.CSVReaderArgs<T>,
        FnCSVReader.CSVReadResult> {



    @Override
    public CSVReadResult apply(CSVReaderArgs args) {

        try {
            Reader in = args.csvInputReader;
            Iterable<CSVRecord> records = args.withHeader ?
                    CSVFormat.EXCEL.withHeader().withDelimiter(args.delimiter).parse(in) :
                    CSVFormat.EXCEL.withDelimiter(args.delimiter).parse(in);
            Iterator<CSVRecord> i=records.iterator();
            List<T> resultList=new ArrayList<>();
            while(i.hasNext()) {
                Optional.ofNullable((T) args.mapFunction.apply(i.next())).ifPresent(resultList::add);
            }
            return new CSVReadResult(resultList);

        }
        catch (IOException e) {
            System.out.println("Error on csv reading: "+e.toString());
            return  new CSVReadResult(e);


        }
    }



    public static class CSVReaderArgs<T> {

        private Reader csvInputReader;
        private boolean withHeader = true;
        private FnCSVRowToAny<T> mapFunction;
        private char delimiter = ',';


        public CSVReaderArgs(Reader csvReader,
                             FnCSVRowToAny<T> mapFunction) {

            this.mapFunction = mapFunction;
            this.csvInputReader=csvReader;
        }

        public CSVReaderArgs(Reader csvReader,
                             boolean withHeader,
                             FnCSVRowToAny<T> mapFunction) {
            this.withHeader = withHeader;
            this.mapFunction = mapFunction;
            this.csvInputReader=csvReader;
        }

        public CSVReaderArgs(Reader csvReader,
                             boolean withHeader,
                             FnCSVRowToAny<T> mapFunction,
                             char delimiter) {
            this.withHeader = withHeader;
            this.mapFunction = mapFunction;
            this.csvInputReader=csvReader;
            this.delimiter=delimiter;
        }
    }




    public static class CSVReadResult<T>{

        private final Throwable exception;

        public List<T> getResultList() {
            return resultList;
        }

        private final List<T> resultList;

        public  CSVReadResult(List<T> resultList,Throwable exception){
            this.resultList=resultList;
            this.exception=exception;
        }

        public  CSVReadResult(List<T> resultList){
            this(resultList,null);
        }


        public  CSVReadResult(Throwable exception){ this(null,exception);}

        public boolean isOK(){ return this.exception==null; }

        public Throwable getException(){ return this.exception; }
    }

}

