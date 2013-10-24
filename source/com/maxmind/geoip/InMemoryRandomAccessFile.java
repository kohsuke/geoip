/*
 * silvertunnel.org Netlib - Java library to easily access anonymity networks
 * Copyright (c) 2009-2012 silvertunnel.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package com.maxmind.geoip;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class simulates most features of RandomAccessFile,
 * but it is hold in memory and it is read only.
 * 
 * @author hapke
 */
public class InMemoryRandomAccessFile implements RandomAccessFileInterface {
    /** this is the "file" content */
    private byte[] data;
    
    /** current position in the array */
    private int position;
    

    ///////////////////////////////////////////////////////
    // open and close
    ///////////////////////////////////////////////////////
    
    /**
     * Initialize an InMemoryRandomAccessFile with a given byte array
     * 
     * @param dataSource    the caller should not change the content of this array to avoid side effects
     */
    public InMemoryRandomAccessFile(byte[] dataSource) {
        data = dataSource;
        position = 0;
    }

    /**
     * Initialize an InMemoryRandomAccessFile from an InputStream.
     * 
     * Inside this constructor the complete InputStream will be read (blocking) and copied to an internal byte array.
     * 
     * @param dataSource
     * @param maxSize   limit the used memory to approximately maxSize bytes
     * @exception IOException if the dataSource could not be read
     */
    public InMemoryRandomAccessFile(InputStream dataSource, int maxSize) throws IOException {
        this(getBytesOfInputStream(dataSource, maxSize));
    }
    
    /**
     * Convert InputStream to byte array.
     * 
     * @param inputStream    data source
     * @param maxSize        stop conversion if maxSize is reached
     * @return inputStream converted into byte array
     * @throws IOException
     */
    public final static byte[] getBytesOfInputStream(InputStream inputStream, int maxSize) throws IOException {
        if (inputStream==null) {
            throw new IOException("invalid inputStream=null");
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
     
        // Read bytes from the input stream in bytes.length-sized chunks and write
        // them into the output stream
        int totalReadBytes = 0;
        int readBytes;
        while ((readBytes = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, readBytes);
            totalReadBytes += readBytes;
            if (totalReadBytes>maxSize) {
                // stop further reading
                break;
            }
        }
     
        // convert the contents of the output stream into a byte array
        byte[] result = outputStream.toByteArray();
        inputStream.close();
        outputStream.close();
     
        return result;
    }
    
    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#close()
     */

    public void close() {
    }


    ///////////////////////////////////////////////////////
    // 'Read' primitives
    ///////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#read()
     */

    public int read() throws IOException {
        if (position>=data.length) {
            // end of file
            return -1;
        } else {
            return data[position++];
        }
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#read(byte[], int, int)
     */

    public int read(byte b[], int off, int len) throws IOException {
           if (b==null) {
            throw new NullPointerException("");
        }
        
        // copy
        int numberOfBytesCopied = 0;
        while (off<b.length && position<data.length && len>0) {
            b[off] = data[position];
            off++;
            position++;
            numberOfBytesCopied++;
            len--;
        }
        
        return numberOfBytesCopied;
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#length()
     */

    public long length() throws IOException {
        return data.length;
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#seek(long)
     */

    public void seek(long pos) throws IOException {
        if (pos<0 || pos>=length() || pos>Integer.MAX_VALUE) {
            throw new IOException("seek() tried with invalid position="+pos);
        }
        position = (int)pos;
    }
    
    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#getFilePointer()
     */

    public long getFilePointer() throws IOException {
        return position;
    }


    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#read(byte[])
     */

    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    ///////////////////////////////////////////////////////
    //  Some "reading/writing Java data types" methods "stolen" from
    //  DataInputStream and DataOutputStream.
    ///////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readBoolean()
     */

    public final boolean readBoolean() throws IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readByte()
     */

    public final byte readByte() throws IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readUnsignedByte()
     */

    public final int readUnsignedByte() throws IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readShort()
     */

    public final short readShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readUnsignedShort()
     */

    public final int readUnsignedShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readChar()
     */

    public final char readChar() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readInt()
     */

    public final int readInt() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readLong()
     */

    public final long readLong() throws IOException {
        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readFloat()
     */

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readDouble()
     */

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /* (non-Javadoc)
     * @see de.apaxo.semrecsys.geolocation.services.RandomAccessFileInterface#readLine()
     */


    public final String readLine() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = read()) {
            case -1:
            case '\n':
                eol = true;
                break;
            case '\r':
                eol = true;
                long cur = getFilePointer();
                if ((read()) != '\n') {
                    seek(cur);
                }
                break;
            default:
                input.append((char)c);
                break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }


    public void readFully(byte[] delim) throws IOException {
        System.arraycopy(data, position, delim, 0, delim.length);
        position += delim.length;
    }


    public void readFully(byte[] dbbuffer, int i, int l) throws IOException {
        System.arraycopy(data, 0, dbbuffer, i, l);
        position += l;
    }
}
