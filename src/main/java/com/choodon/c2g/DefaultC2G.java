/*
 * Copyright 2018 The Choodon-C2G Project
 *
 *  The Choodon-C2G Project licenses this file to you under the Apache License,
 *  version 2.0 (the "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License
 */

package com.choodon.c2g;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultC2G
 *
 * @author michael
 * @since 2019-03-05
 */
public class DefaultC2G {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultC2G.class);

    private static final Map<Class, ThreadLocal> INSTANCE_CONTAINER = new ConcurrentHashMap();

    private static final Map<Field, Object> FIELD_DEFAULT_VALUE_CONTAINER = new HashMap<>();

    private static final Map<Class, Object> CLASS_CONTAINER = new ConcurrentHashMap<>();

    private static final Object VALUE = new Object();


    private static void init(Class clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz is null.");
        }
        ThreadLocal threadLocal = new ThreadLocal();
        try {
            threadLocal.set(clazz.newInstance());
            INSTANCE_CONTAINER.put(clazz, threadLocal);
        } catch (Exception e) {
            LOGGER.error("create instance exception: ", e);
            throw new IllegalArgumentException("create instance exception");
        }
    }

    public static <T> T allocate(Class<T> clazz) {
        return allocate(clazz, false, false);
    }

    public static <T> T allocate(Class<T> clazz, boolean keepDefValue, boolean clear) {
        if (!CLASS_CONTAINER.containsKey(clazz)) {
            synchronized (clazz) {
                if (!CLASS_CONTAINER.containsKey(clazz)) {
                    if (keepDefValue) {
                        T t;
                        try {
                            t = clazz.newInstance();
                        } catch (Exception e) {
                            LOGGER.error("create instance exception: ", e);
                            throw new IllegalArgumentException("create instance exception");
                        }
                        Field[] fields = clazz.getFields();
                        Field[] declaredFields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            try {
                                FIELD_DEFAULT_VALUE_CONTAINER.put(field, field.get(t));
                            } catch (Exception e) {
                                LOGGER.error("get field value exception: ", e);
                                throw new IllegalArgumentException("get field value exception");
                            }
                        }
                        for (Field field : declaredFields) {
                            field.setAccessible(true);
                            try {
                                FIELD_DEFAULT_VALUE_CONTAINER.put(field, field.get(t));
                            } catch (Exception e) {
                                LOGGER.error("get field value exception: ", e);
                                throw new IllegalArgumentException("get field value exception");
                            }
                        }
                    } else {
                        Field[] fields = clazz.getFields();
                        Field[] declaredFields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            try {
                                Class fieldClazz = field.getType();
                                if (!fieldClazz.isPrimitive()) {
                                    FIELD_DEFAULT_VALUE_CONTAINER.put(field, null);
                                } else {
                                    if (fieldClazz == byte.class || fieldClazz == short.class || fieldClazz == int.class || fieldClazz == long.class || fieldClazz == float.class || fieldClazz == double.class) {
                                        FIELD_DEFAULT_VALUE_CONTAINER.put(field, 0);
                                    } else if (fieldClazz == boolean.class) {
                                        FIELD_DEFAULT_VALUE_CONTAINER.put(field, false);
                                    } else if (fieldClazz == char.class) {
                                        FIELD_DEFAULT_VALUE_CONTAINER.put(field, '\u0000');
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("get field value exception: ", e);
                                throw new IllegalArgumentException("get field value exception");
                            }
                        }
                        for (Field field : declaredFields) {
                            field.setAccessible(true);
                            try {
                                Class fieldClazz = field.getType();
                                if (!fieldClazz.isPrimitive()) {
                                    FIELD_DEFAULT_VALUE_CONTAINER.put(field, null);
                                } else {
                                    if (fieldClazz == byte.class || fieldClazz == short.class || fieldClazz == int.class || fieldClazz == long.class || fieldClazz == float.class || fieldClazz == double.class) {
                                        FIELD_DEFAULT_VALUE_CONTAINER.put(field, 0);
                                    } else if (fieldClazz == boolean.class) {
                                        FIELD_DEFAULT_VALUE_CONTAINER.put(field, false);
                                    } else if (fieldClazz == char.class) {
                                        FIELD_DEFAULT_VALUE_CONTAINER.put(field, '\u0000');
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error("get field value exception: ", e);
                                throw new IllegalArgumentException("get field value exception");
                            }
                        }
                    }
                    CLASS_CONTAINER.put(clazz, VALUE);
                }

            }

        }
        if (INSTANCE_CONTAINER.containsKey(clazz)) {
            if (INSTANCE_CONTAINER.get(clazz).get() == null) {
                synchronized (clazz) {
                    if (INSTANCE_CONTAINER.get(clazz).get() == null) {
                        try {
                            INSTANCE_CONTAINER.get(clazz).set(clazz.newInstance());
                            return (T) INSTANCE_CONTAINER.get(clazz).get();
                        } catch (Exception e) {
                            LOGGER.error("create instance exception: ", e);
                            throw new IllegalArgumentException("create instance exception");
                        }
                    }
                }

            }
            if (clear) {
                clear(clazz);
            }
            return (T) INSTANCE_CONTAINER.get(clazz).get();
        } else {
            synchronized (clazz) {
                if (!INSTANCE_CONTAINER.containsKey(clazz)) {
                    init(clazz);
                    return (T) INSTANCE_CONTAINER.get(clazz).get();
                }
            }
            return (T) INSTANCE_CONTAINER.get(clazz).get();
        }

    }

    private static void clear(Class clazz) {
        Object obj = INSTANCE_CONTAINER.get(clazz).get();
        FIELD_DEFAULT_VALUE_CONTAINER.entrySet().stream().forEach(entry -> {
            try {
                entry.getKey().set(obj, entry.getValue());
            } catch (Exception e) {
                LOGGER.error("set field value exception: ", e);
                throw new IllegalArgumentException("set field value exception");
            }
        });

    }

}
