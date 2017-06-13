package com.fulmicotone.util.csv;

import java.io.File;

/**
 * Created by dino on 11/10/16.
 */
public class Path {


    private final String filename;
    private final String dir;
    private final String ext;

    public Path(String dir, String filename, String ext) {

        this.dir=dir;
        this.filename=filename;
        this.ext=ext;
    }


    @Override
    public String toString() {
        return   String.join("", this.dir,
                File.separator,
                this.filename,".",
                this.ext);

    }
}
