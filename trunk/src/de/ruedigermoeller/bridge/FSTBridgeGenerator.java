package de.ruedigermoeller.bridge;

import de.ruedigermoeller.bridge.cpp.FSTCFactoryGen;
import de.ruedigermoeller.bridge.cpp.FSTCFileGen;
import de.ruedigermoeller.bridge.cpp.FSTCHeaderGen;
import de.ruedigermoeller.bridge.java.FSTJavaFactoryGen;
import de.ruedigermoeller.bridge.java.FSTJavaFileGen;
import de.ruedigermoeller.serialization.FSTClazzInfo;
import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTCrossLanguageSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.*;

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
 * Date: 23.12.12
 * Time: 00:55
 * To change this template use File | Settings | File Templates.
 */
public class FSTBridgeGenerator {

    HashSet<Class> knownClasses = new HashSet<Class>();
    FSTConfiguration conf = FSTConfiguration.createCrossLanguageConfiguration();
    SortedSet<Class> sorted = new TreeSet<Class>( new Comparator<Class>() {
        @Override
        public int compare(Class o1, Class o2) {
            return o1.getName().compareTo(o2.getName());
        }
    });

    public FSTBridgeGenerator() {
        addClass(byte[].class);
        addClass(boolean[].class);
        addClass(char[].class);
        addClass(short[].class);
        addClass(int[].class);
        addClass(long[].class);
        addClass(float[].class);
        addClass(double[].class);

        addClass(Byte.class);
        addClass(Boolean.class);
        addClass(Character.class);
        addClass(Short.class);
        addClass(Integer.class);
        addClass(Long.class);
        addClass(Float.class);
        addClass(Double.class);
        addClass(Date.class);
        addClass(String.class);
        addClass(Object.class);
    }

    public FSTConfiguration getConf() {
        return conf;
    }

    public FSTClazzInfo getCLInfo( Class c ) {
        return getConf().getCLInfoRegistry().getCLInfo(c);
    }

    public void addClass(Class c) {
        if ( c.isArray() && ! c.getComponentType().isPrimitive() ) {
            c = c.getComponentType();
            if ( c.isArray() ) {
                throw new RuntimeException("multi dimensional arrays not supported !");
            }
        }
        isValidClassType(c);
        conf.getClassRegistry().registerClass(c);
        knownClasses.add(c);
        sorted.add(c);

        if ( ! c.isPrimitive() && ! c.isArray() ) {
            Class arrCl = Array.newInstance(c, 0).getClass();
            conf.getClassRegistry().registerClass(arrCl);
            knownClasses.add(arrCl);
            sorted.add(arrCl);
        }
    }

    public void isValidClassType(Class c) {
        FSTClazzInfo info = conf.getCLInfoRegistry().getCLInfo(c);
        if ( info.getSer() == null && info.useCompatibleMode() || info.isExternalizable() ) {
            throw new RuntimeException("cannot use class "+c.getName()+" for cross language messages. It defines JDK specific serialization methods.");
        }
        if ( info.getSer() != null && info.getSer() instanceof FSTCrossLanguageSerializer == false ) {
            System.out.println("warning: Serializer registered for "+c.getName()+" will be ignored. Not a cross-language serializer");
        }
        if ( info.getSer() instanceof FSTCrossLanguageSerializer) {
            knownClasses.add(((FSTCrossLanguageSerializer) info.getSer()).getCrossLangLayout());
//            sorted.add(((FSTCrossLanguageSerializer) info.getSer()).getCrossLangLayout());
        }
    }

    public int getIdForClass( Class c ) {
        return conf.getClassRegistry().getIdFromClazz(c);
    }

    public SortedSet<Class> getSortedClazzes() {
        return sorted;
    }

    public void generateClasses( Language lang, String outDir ) throws FileNotFoundException {
        new File(outDir).mkdirs();
        if ( lang == Language.CPP ) {
            FSTCFactoryGen hgen = new FSTCFactoryGen(this);
            hgen.generateFactory(outDir);
        }
        if ( lang == Language.JAVA ) {
            FSTJavaFactoryGen hgen = new FSTJavaFactoryGen (this);
            hgen.generateFactory(outDir);
        }
        for (Iterator<Class> iterator = sorted.iterator(); iterator.hasNext(); ) {
            Class next = iterator.next();
            if ( ! next.isArray() && next != String.class ) {
                if ( lang == Language.CPP ) {
                    FSTCHeaderGen gen = new FSTCHeaderGen(this);
                    gen.generateClazz(conf.getCLInfoRegistry().getCLInfo(next),outDir,"");

                    FSTCFileGen genf = new FSTCFileGen(this);
                    genf.generateClazz(conf.getCLInfoRegistry().getCLInfo(next),outDir,"");
                }
                if ( lang == Language.JAVA ) {
                    FSTJavaFileGen genf = new FSTJavaFileGen(this);
                    genf.generateClazz(conf.getCLInfoRegistry().getCLInfo(next),outDir,"");
                }
            }
        }
    }

    public boolean isRegistered(Class clazz) {
        return knownClasses.contains(clazz);
    }

    public enum Language {
        CPP,
        JAVA
    }
}
