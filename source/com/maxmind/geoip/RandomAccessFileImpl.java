package com.maxmind.geoip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

/**
 * An implementation for RandomAccessFileImpl
 * @author Manue Blechschmidt <blechschmidt@apaxo.de>
 *
 */
public class RandomAccessFileImpl extends RandomAccessFile implements
        RandomAccessFileInterface {

    public RandomAccessFileImpl(File file, String mode)
            throws FileNotFoundException {
        super(file, mode);
    }

}
