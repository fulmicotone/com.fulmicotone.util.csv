package com.fulmicotone.util.csv;


import org.apache.commons.csv.CSVRecord;

import java.util.function.Function;


/**
 * used for extract value from command arg
 */
public  abstract  class FnCSVRowToAny<T> implements Function<CSVRecord,T> {



}
