package de.ruedigermoeller.serialization.testclasses.blog;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

import java.io.*;

/**
 * Copyright (c) 2012, Ruediger Moeller. All rights reserved.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * <p/>
 * Date: 10.10.13
 * Time: 19:48
 * To change this template use File | Settings | File Templates.
 */
public class BlogBenchMain {

    public ByteArrayOutputStream bout = new ByteArrayOutputStream(100000);
    ByteArrayInputStream bin;
    public long timWrite;
    public long timRead;
    Object testObject;
    int length;
    private int type = 0; // 0 - fst 1 - JDK
    int iterations;

    public BlogBenchMain(int type, int iterations) {
        this.type = type;
        this.iterations = iterations;
    }

    public void run( Object toWrite ) throws Exception {
        testObject = toWrite;

        System.gc(); // clean heap
        System.out.println("write ..");
        long startTim = System.currentTimeMillis();
        for ( int i = 0; i < iterations; i++ ) {
            bout.reset(); // very cheap
            if ( type == 0) {
                FSTObjectOutput out = FSTConfiguration.getDefaultConfiguration().getObjectOutput(bout);
                writeTest(out, toWrite );
            }
            else
                writeTest(new ObjectOutputStream(bout), toWrite);
        }
        timWrite = System.currentTimeMillis()-startTim;
        byte[] bytes = bout.toByteArray();
        length = bytes.length;
        bin = new ByteArrayInputStream(bytes);

        System.gc(); // clean heap
        System.out.println("read ..");
        startTim = System.currentTimeMillis();
        for ( int i = 0; i < iterations; i++ ) {
            if ( type == 0) {
//                FSTObjectInput in = new FSTObjectInput(bin);
                FSTObjectInput in = FSTConfiguration.getDefaultConfiguration().getObjectInput(bin);
                readTest(in);
            }
            else
                readTest(new ObjectInputStream(bin));
            bin.reset();
        }
        timRead = System.currentTimeMillis()-startTim;
    }

    public void dumpRes(String type) {
        System.out.println(type+"  Size:" + length + ",  TimeRead: " + (timRead * 1000 * 1000 / iterations) + " nanoseconds,   TimeWrite: " + (timWrite * 1000 * 1000 / iterations) + " nanoseconds");
    }

    protected void readTest(ObjectInput in) throws Exception {
        in.readObject();
//        in.flush();
    }

    protected void writeTest(ObjectOutput out, Object toWrite) throws Exception {
        out.writeObject(toWrite);
        out.flush();
    }

    public static void main(String args[]) throws Exception {

        int iterations = 3000000;

        BlogBenchMain fst = new BlogBenchMain(0,iterations);
        BlogBenchMain jdk = new BlogBenchMain(1,iterations);

        Object test = new BlogBench(13);

        // warm up
//        jdk.run(test);
        fst.run(test);

        //test
        fst.run(test);
        fst.dumpRes("FST - Plain Serializable");

//        jdk.run(test);
        jdk.dumpRes("JDK - Plain Serializable ");

        test = new BlogBenchExternalizable(13);
        //test
        fst.run(test);
        fst.dumpRes("FST - Externalizable");

//        jdk.run(test);
        jdk.dumpRes("JDK - Externalizable");

        test = new BlogBenchAnnotated(13);
        //test
        fst.run(test);
        fst.dumpRes("FST - Annotated");

//        jdk.run(test);
//        jdk.dumpRes("JDK - Externalizable");

    }
}
