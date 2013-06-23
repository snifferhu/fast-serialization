package de.ruedigermoeller.serialization.testclasses.crosslanguage;

import de.ruedigermoeller.bridge.FSTBridge;
import de.ruedigermoeller.serialization.FSTClazzInfo;
import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectOutput;

import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Date: 22.12.12
 * Time: 12:52
 * To change this template use File | Settings | File Templates.
 */
public class Cross implements Serializable {

    public enum OHO {
        WAN,
        TU,
        SREE
    }
    public enum test {
        EINS,
        ZWEI,
        DREI
    }

    boolean a = true,b = false,c = false, d = false, e = true, aa = false,ab = false,ac = false, ad = false, ae = false, xx = false;
    int anInt = 1234567890;
    char achar = 12345;
    byte aByte = -13;
    short aShort = 12345;
    long aLong = 1234567890123456l;
    float aFloat = 1.2345678f;
    double aDouble = -1.876176d;
    byte testByte [] = {1,2,3,-1,-2,-3};
    char testChar [] = {1,2,3,38000};
    short testShort [] = {1,2,3,-18000,18000};
    int testInt [] = {-100,100,200,300,398475398,-398457398};
    long testlong [] = {-100,100,200,300,39458734235l,-34953456934l};
    double testDouble[] = { 234.0234234d, -112312.0234234d };
    float testFloat[] = { 234.0234234f, -112312.0234234f };
    String string = "üäö";
    CrossB crossB;
    CrossB crossBs[];
    Object object = new float[] { 234.0234234f, -112312.0234234f };
    Object other[] = { "Hallo", "Holla","Hallo" };
    Dimension dimension = new Dimension(44,44);
    Dimension dims[] = new Dimension[] { new Dimension(10,10), new Dimension(20,20)};
    List list = new ArrayList();
    public HashMap map = new HashMap();
    public Map map1 = new HashMap();
    test enu = test.ZWEI;
    OHO oho = OHO.SREE;

    {
        list.add("na so ein glück");
        map.put("test", 100);
        map.put(10000, 1);
        map1.put("map1test", 1100);
        map1.put(100001, 11);
    }
    public Cross(CrossB b) {
        crossB = b;
    }

    public static void main( String arg[]) throws IOException {
        FSTBridge generator = getFstBridge();

        FSTConfiguration conf = generator.getConf();

        FSTClazzInfo clInfo = conf.getCLInfoRegistry().getCLInfo(Cross.class);
        FSTClazzInfo.FSTFieldInfo[] fieldInfo = clInfo.getFieldInfo();
        System.out.println("{");
        for (int i = 0; i < fieldInfo.length; i++) {
            FSTClazzInfo.FSTFieldInfo fstFieldInfo = fieldInfo[i];
            System.out.println("  "+fstFieldInfo.getType().getSimpleName()+" "+fstFieldInfo.getField().getName() );
        }
        System.out.println("}");
        System.out.println();

//        generator.generateClasses( FSTBridge.Language.CPP,  "C:\\Users\\ruedi\\Documents\\Visual Studio 2012\\Projects\\FST\\FST");
        generator.generateClasses( FSTBridge.Language.JAVA, "f:\\work\\FSTCrossTest\\src\\de\\ruedigermoeller\\bridge\\java\\generated");
        generator.generateClasses( FSTBridge.Language.PY2, "f:\\work\\FSTCrossPy\\fstgen");

        FSTObjectOutput out = new FSTObjectOutput(new FileOutputStream("\\tmp\\crosstest.oos"), conf);
        out.writeObject(new Cross(new CrossB()));
        byte[] buffer = out.getBuffer();
        for ( int i = 0; i < out.getWritten(); i++ ) {
            System.out.println("["+i+"]"+buffer[i]);
        }
        out.close();
        int err[] = {15,97,14};
        for (int i = 0; i < err.length; i++) {
            System.out.println("id "+err[i]+" "+conf.getClassRegistry().getClazzFromId(err[i]).getClazz().getSimpleName());
        }

    }

    public static FSTBridge getFstBridge() {
        FSTBridge generator = new FSTBridge();
        generator.addClass(Cross.class);
        generator.addClass(CrossB.class);
        generator.addClass(test.class);
        generator.addClass(OHO.class);
        generator.addClass(Dimension.class);
        generator.addClass(Dimension[].class);
        return generator;
    }
}