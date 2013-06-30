package de.ruedigermoeller.heapofftest;

import de.ruedigermoeller.heapoff.FSTCompressor;
import de.ruedigermoeller.heapoff.structs.FSTStruct;
import de.ruedigermoeller.heapoff.structs.FSTStructArray;
import de.ruedigermoeller.heapoff.structs.FSTStructFactory;
import de.ruedigermoeller.heapoff.structs.structtypes.FSTEmbeddedList;
import de.ruedigermoeller.heapoff.structs.structtypes.FSTEmbeddedMap;
import de.ruedigermoeller.heapoff.structs.structtypes.FSTEmbeddedString;
import de.ruedigermoeller.serialization.util.FSTUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
 * Date: 24.06.13
 * Time: 19:45
 * To change this template use File | Settings | File Templates.
 */
public class TestStructs {

    public static class SimpleTest implements Serializable {
        Object nullObject = null;
        long id = 12345;
        Object [] anArray = { null, null, new FSTEmbeddedString("NotNull"), null };

        public Object getNullObject() {
            return nullObject;
        }

        public void setNullObject(Object nullObject) {
            this.nullObject = nullObject;
        }

        public Object anArray(int i) { return anArray[i]; }
        public void anArray(int i, Object val) { anArray[i] = val; }
        public int anArrayLen() { return anArray.length; }
    }

    public static class SubTestStruct implements Serializable {
        FSTEmbeddedString testString = new FSTEmbeddedString("HalloTest");
        long id = 12345;
        int legs[] = {19,18,17,16};
        Object [] anArray = { new FSTEmbeddedString("Hello"), new FSTEmbeddedString("Oha") };

        Object nullobj = null;

        public long getId() {
            return id;
        }

        public FSTEmbeddedString getTestString() {
            return testString;
        }

        public Object getNullobj() {
            return nullobj;
        }

        public void setNullobj(Object nullobj) {
            this.nullobj = nullobj;
        }

        public int legs(int i) { return legs[i]; }
        public void legs(int i, int val) {legs[i] = val;}
        public int legsLen() { return legs.length; }

        public Object anArray(int i) { return anArray[i]; }
        public void anArray(int i, Object val) { anArray[i] = val; }
        public int anArrayLen() { return anArray.length; }

    }

    public static class TestStruct implements Serializable {
        int intVar=64;
        boolean boolVar;
        int intarray[] = new int[50];
        SubTestStruct struct = new SubTestStruct();

        public TestStruct() {
            intarray[0] = Integer.MAX_VALUE-1;
            intarray[9] = Integer.MAX_VALUE;
        }

        public int getIntVar() {
            return intVar;
        }

        public void setIntVar(int intVar) {
            this.intVar = intVar;
        }

        public boolean isBoolVar() {
            return boolVar;
        }

        public boolean containsInt(int i) {
            for (int j = 0; j < intarrayLen(); j++) {
                int i1 = intarray(j);
                if ( i1 == i ) {
                    return true;
                }
            }
            return false;
        }

        public void setBoolVar(boolean boolVar) {
            this.boolVar = boolVar;
        }

        public void intarray(int i, int val) {
            intarray[i] = val;
        }

        public int intarray( int i ) {
            return intarray[i];
        }

        public int intarrayLen() {
            return intarray.length;
        }

        public SubTestStruct getStruct() {
            return struct;
        }
    }

    private static void benchIterAccess(FSTStructFactory fac, byte b[], int structLen, int max) {
        int times = 4;
        long tim;
        int sum;

        System.out.println("iter "+4*max+" direct");
        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++) {
            TestStruct struct = (TestStruct) fac.createStructWrapper(b,0);
            for ( int i=0; i<max; i++ ) {
                sum += struct.getIntVar();
                ((FSTStruct)struct)._addOffset(structLen);
            }
        }
        System.out.println("  iter int "+(System.currentTimeMillis()-tim)+" sum "+sum);

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++) {
            TestStruct struct = (TestStruct) fac.createStructWrapper(b,0);
            for ( int i=0; i<max; i++ ) {
                sum += struct.intarray(3);
                ((FSTStruct)struct)._addOffset(structLen);
            }
        }
        System.out.println("  iter int array[3]"+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++) {
            TestStruct struct = (TestStruct) fac.createStructWrapper(b,0);
            for ( int i=0; i<max; i++ ) {
                if ( struct.containsInt(77) ) {
                    sum = 0;
                }
                ((FSTStruct)struct)._addOffset(structLen);
            }
        }
        System.out.println("  iter int from this "+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++) {
            TestStruct struct = (TestStruct) fac.createStructWrapper(b,0);
            for ( int i=0; i<max; i++ ) {
                sum += struct.getStruct().getId();
                ((FSTStruct)struct)._addOffset(structLen);
            }
        }
        System.out.println("  iter substructure int "+(System.currentTimeMillis()-tim));
    }

    private static void benchAccess(TestStruct[] structs) {
        int times = 4;
        long tim;
        int sum;

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++)
            for ( int i=0; i<structs.length; i++ ) {
                sum += structs[i].getIntVar();
            }
        System.out.println("  read int "+(System.currentTimeMillis()-tim)+" sum: "+sum);

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++)
            for ( int i=0; i<structs.length; i++ ) {
                sum += structs[i].intarray(3);
            }
        System.out.println("  read int array[3]"+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        for (int ii=0;ii<times;ii++)
            for ( int i=0; i<structs.length; i++ ) {
                if ( structs[i].containsInt(77) ) {
                    sum = 0;
                }
            }
        System.out.println("  iter int from this "+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++)
            for ( int i=0; i<structs.length; i++ ) {
                sum += structs[i].getStruct().getId();
            }
        System.out.println("  read substructure int "+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        sum = 0;
        for (int ii=0;ii<times;ii++)
            for ( int i=0; i<structs.length; i++ ) {
                sum += structs[i].getStruct().legs(1);
            }
        System.out.println("  read substructure int[] "+(System.currentTimeMillis()-tim));
    }



    public static void benchFullGC() {
        for ( int i = 0; i < 3; i++ ) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            long tim = System.currentTimeMillis();
            System.gc();
            System.out.println("FULL GC TIME "+(System.currentTimeMillis()-tim)+" mem:"+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1000/1000+" MB");
        }
    }

    static TestStruct[] structs = new TestStruct[4000000];
    public static void main0(String arg[] ) throws Exception {

        FSTStructFactory fac = new FSTStructFactory();
        fac.registerClz(TestStruct.class);
        fac.registerClz(SubTestStruct.class);
        fac.registerClz(FSTEmbeddedString.class);
        fac.registerClz(FSTEmbeddedMap.class);

        FSTEmbeddedMap mp = new FSTEmbeddedMap(11);
        mp.put(new FSTEmbeddedString("Emil"),new FSTEmbeddedString("Möller-Lienemann"));
        mp.put(new FSTEmbeddedString("Felix"),new FSTEmbeddedString("Möller-Fricker"));
        mp.put(new FSTEmbeddedString("Rüdiger"),new FSTEmbeddedString("Möller"));

        System.out.println("hm:"+mp.get(new FSTEmbeddedString("Emil")));
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("POK")));
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("Felix")));
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("Rüdiger")));
        mp = fac.toStruct(mp);
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("Emil")));
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("POK")));
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("Felix")));
        System.out.println("hm:"+mp.get(new FSTEmbeddedString("Rüdiger")));

        HashMap<FSTEmbeddedString,FSTEmbeddedString> testMap = new HashMap<FSTEmbeddedString, FSTEmbeddedString>();
        for ( int i = 0; i < 100; i++ ) {
            testMap.put(new FSTEmbeddedString("oij"+i), new FSTEmbeddedString("val"+i));
        }
        FSTEmbeddedMap<FSTEmbeddedString,FSTEmbeddedString> stMap = new FSTEmbeddedMap<FSTEmbeddedString, FSTEmbeddedString>(testMap);

        FSTEmbeddedString toSearch = new FSTEmbeddedString("oij"+11);
        FSTEmbeddedString toNotFind = new FSTEmbeddedString("notThere");
        long tim = System.currentTimeMillis();
        for ( int i = 0; i < 8000000; i++) {
            if ( testMap.get(toSearch) == null ) {
                System.out.println("bug");
            }
        }
        for ( int i = 0; i < 8000000; i++) {
            if ( testMap.get(toNotFind) != null ) {
                System.out.println("bug");
            }
        }
        System.out.println("lookup hashmap "+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        for ( int i = 0; i < 8000000; i++) {
            if ( stMap.get(toSearch) == null ) {
                System.out.println("bug");
            }
        }
        for ( int i = 0; i < 8000000; i++) {
            if ( stMap.get(toNotFind) != null ) {
                System.out.println("bug");
            }
        }
        System.out.println("lookup structmap "+(System.currentTimeMillis()-tim));

        stMap = fac.toStruct(stMap);

        tim = System.currentTimeMillis();
        for ( int i = 0; i < 8000000; i++) {
            if ( stMap.get(toSearch) == null ) {
                System.out.println("bug");
            }
        }
        for ( int i = 0; i < 8000000; i++) {
            if ( stMap.get(toNotFind) != null ) {
                System.out.println("bug");
            }
        }
        System.out.println("lookup off structmap "+(System.currentTimeMillis()-tim));


        ArrayList<FSTEmbeddedString> stringList = new ArrayList<FSTEmbeddedString>(11111);
        for (int i = 0; i < 11111; i++) {
            stringList.add( new FSTEmbeddedString("pok "+i) );
        }

        FSTEmbeddedList<FSTEmbeddedString> embedList = new FSTEmbeddedList<FSTEmbeddedString>(stringList);
        FSTEmbeddedString sstring = new FSTEmbeddedString("pok 10000");
        tim = System.currentTimeMillis();
        for ( int i = 0; i < 1000; i++) {
            if (stringList.indexOf(sstring)!=10000)
                System.out.println("BUG");
        }
        System.out.println("index stringlist "+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        for ( int i = 0; i < 1000; i++) {
            if (embedList.indexOf(sstring)!=10000)
                System.out.println("BUG");
        }
        System.out.println("index embed stringlist "+(System.currentTimeMillis()-tim));

        embedList = fac.toStruct(embedList);
        tim = System.currentTimeMillis();
        for ( int i = 0; i < 1000; i++) {
            if (embedList.indexOf(sstring)!=10000)
                System.out.println("BUG");
        }
        System.out.println("index offheap embed stringlist "+(System.currentTimeMillis()-tim));

        SimpleTest simpleTest = fac.toStruct(new SimpleTest());
        System.out.println("st null " + simpleTest.getNullObject());
        System.out.println("st null arr " + simpleTest.anArray(0));
        System.out.println("st null arr filled " + simpleTest.anArray(2));
        System.out.println("st null arr " + simpleTest.anArray(3));

        SubTestStruct onHeap1 = new SubTestStruct();
        System.out.println("sub siz "+fac.calcStructSize(onHeap1));
        SubTestStruct subTest = fac.toStruct(onHeap1);
        System.out.println("sub id " + subTest.getId());
        System.out.println("sub arr 0 " + subTest.anArray(0));
        System.out.println("sub arr 1 " + subTest.anArray(1));

        FSTEmbeddedString os = fac.toStruct(new FSTEmbeddedString("Hallo"));
        System.out.println("POK:"+os);

        TestStruct onHeap = new TestStruct();

        FSTCompressor compressor = new FSTCompressor();

        tim = System.currentTimeMillis();
        for ( int i = 0; i < 1000000; i++) {
            compressor.compress2Byte(onHeap);
        }
        System.out.println("compress using serialization "+(System.currentTimeMillis()-tim));

        tim = System.currentTimeMillis();
        for ( int i = 0; i < 1000000; i++) {
            fac.toStruct(onHeap);
        }
        System.out.println("compress using structs "+(System.currentTimeMillis()-tim));
        compressor = null;

        TestStruct offHeap = fac.toStruct(onHeap);

        SubTestStruct struct = offHeap.getStruct();

        System.out.println(offHeap.getStruct().getId() + " '" + ((FSTEmbeddedString) offHeap.getStruct().anArray(0)) + "' '" + offHeap.getStruct().anArray(1) + "'");
        FSTEmbeddedString testString = offHeap.getStruct().getTestString();
        String s = testString.toString();
        System.out.println("pok1:"+ testString+"'");

        tim = System.currentTimeMillis();
        for (int i = 0; i < structs.length; i+=2) {
            structs[i] = new TestStruct();
        }
        for (int i = 1; i < structs.length; i+=2) {
            structs[i] = new TestStruct();
        }
        System.out.println("instantiation on heap "+(System.currentTimeMillis()-tim));
        benchFullGC();
        benchAccess(structs);

        tim = System.currentTimeMillis();
        for (int i = 0; i < structs.length; i++) {
            structs[i] = fac.toStruct(structs[i]);
        }
        System.out.println("moving off heap "+(System.currentTimeMillis()-tim));
        benchAccess(structs);
        benchFullGC();

        TestStruct template = new TestStruct();
        byte[] bytes = fac.toByteArray(template);
        tim = System.currentTimeMillis();
        for (int i = 0; i < structs.length; i++) {
            byte b[] = new byte[bytes.length];
            System.arraycopy(bytes,0,b,0,b.length);
            structs[i] = (TestStruct) fac.createStructWrapper(b,0);
        }
        System.out.println("instantiate off heap new byte[] per object "+(System.currentTimeMillis()-tim));
        benchAccess(structs);
        benchFullGC();

        tim = System.currentTimeMillis();
        byte hugeArray[] = new byte[bytes.length*structs.length];
        int off = 0;
        for (int i = 0; i < structs.length; i++) {
            System.arraycopy(bytes,0,hugeArray,off,bytes.length);
            structs[i] = (TestStruct) fac.createStructWrapper(hugeArray,off);
            off += bytes.length;
        }
        System.out.println("instantiate off heap huge single byte array " + (System.currentTimeMillis() - tim));
        benchAccess(structs);
        benchFullGC();
        int structLen = bytes.length / structs.length;
        int max = structs.length;
        structs = null;
        System.out.println("iterative access on huge array");
        benchIterAccess(fac,hugeArray, structLen,max);

        System.out.println("iterate structarray");
        FSTStructArray<TestStruct> arr = new FSTStructArray<TestStruct>(fac, new TestStruct(), max);
//        tim = System.currentTimeMillis();
//        for ( int j=0; j < 4; j++ )
//            for ( int i = 0; i < arr.size(); i++) {
//                arr.get(i).setIntVar(1);
//            }
//        System.out.println("   structarr set int " + (System.currentTimeMillis() - tim));

        tim = System.currentTimeMillis();
        int sum = 0;
        for ( int j=0; j < 4; j++ ) {
            final int size = arr.size();
            for ( int i = 0; i < size; i++) {
                sum += arr.get(i).getIntVar();
            }
        }
        System.out.println("   structarr get int " + (System.currentTimeMillis() - tim));

        tim = System.currentTimeMillis();
        sum = 0;
        for ( int j=0; j < 4; j++ )
            for (Iterator<TestStruct> iterator = arr.iterator(); iterator.hasNext(); ) {
                TestStruct next = iterator.next();
                sum += next.getIntVar();
            }
        System.out.println("   structarr iterator get int " + (System.currentTimeMillis() - tim) + " sum:"+sum);

        tim = System.currentTimeMillis();
        sum = 0;
        final TestStruct next = arr.createPointer(0);
        final int elemSiz = arr.getElemSiz();
        final int size = arr.size();
        for ( int j=0; j < 4; j++ ) {
            ((FSTStruct)next)._setOffset(FSTUtil.bufoff);
            for (int i= 0; i < size; i++ ) {
                sum+=next.getIntVar();
                ((FSTStruct)next)._addOffset(elemSiz);
            }
        }
        System.out.println("   structarr iterator offset iter get int " + (System.currentTimeMillis() - tim) + " sum:"+sum);

        benchFullGC();


        System.out.println(bytes.length); // avoid opt
//
//        System.out.println("iarr oheap "+testStruct.intarray(0));
//        System.out.println("iarr oheap "+testStruct.intarray(9));
//
//        System.out.println("ivar " + testStruct.getIntVar());
//        testStruct.setIntVar(9999);
//        System.out.println("ivar " + testStruct.getIntVar());
//
//        System.out.println("bool " + testStruct.isBoolVar());
//        testStruct.setBoolVar(true);
//        System.out.println("bool " + testStruct.isBoolVar());
//
//        testStruct.intarray(3, 4444);
//        System.out.println("POK " + testStruct.intarray(3));
//        testStruct.intarray(9);
//
//        SubTestStruct sub = testStruct.getStruct();
//        System.out.println("sub.id " + sub.getId());
//        System.out.println("sub.legs0 " + sub.legs(0));

//        testStruct.setStringVar("Pok");
    }

    public static void main(String arg[] ) throws Exception {
        main0(arg);
        System.out.println("BENCH FINISHED ------------------------------------------------------------------------");
        while( true )
            benchFullGC();
    }

}
