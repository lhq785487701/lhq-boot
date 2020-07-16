package com.lhq.superboot.api.utils;

import com.lhq.superboot.api.domain.ApiResult;
import com.lhq.superboot.api.enums.ExecResult;
import com.lhq.superboot.exception.SuperBootException;
import com.lhq.superboot.util.SpringContextUtil;
import com.lhq.superboot.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: api执行方法反射
 * @author: lihaoqi
 * @date: 2019/12/23 22:46
 * @version: v1.0.0
 */
@Service("apiReflect")
public class ApiReflectUtils {

    private static final Logger logger = LoggerFactory.getLogger(ApiReflectUtils.class);

    /**
     * @param className      类全名
     * @param methodName     方法名
     * @param paramTypeList  参数类型列表
     * @param paramValueList 参数值列表
     * @Description: 动态反射类方法
     */
    @SuppressWarnings("unchecked")
    public ApiResult invoke(String className, String methodName, List<String> paramTypeList, List<Object> paramValueList) {
        // 返回结果
        String executeMsg = "";
        String executeResult;
        Object rtnData = null;
        Object[] params = new Object[0];

        if (paramValueList != null && !paramValueList.isEmpty()) {
            // 转化成object数组，调用反射
            params = new Object[paramValueList.size()];
            for (int i = 0; i < paramValueList.size(); i++) {
                params[i] = paramValueList.get(i);
            }
        }

        try {
            Class<?> clazz = Class.forName(className);
            // 获取首字母小写类名
            String simpleName = clazz.getSimpleName();
            String firstLowerName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
            //通过此方法去Spring容器中获取Bean实例
            Object objBean = SpringContextUtil.getBean(firstLowerName, clazz);
            Method[] methods = clazz.getDeclaredMethods();
            Method callMethod = null;
            boolean paramFlag = true;
            boolean methodFlag = true;
            boolean entityFlag = true;

            // 遍历所有方法
            for (Method method : methods) {
                // 判断方法名是否相同
                if (!method.getName().equals(methodName)) {
                    continue;
                }

                Class<?>[] type = method.getParameterTypes();
                // 判断参数个数
                if (type.length != params.length) {
                    continue;
                }
                // 判断参数类型是否一样
                // paramFlag方法中的参数类型与参数值类型是否匹配标志
                paramFlag = true;
                methodFlag = true;
                for (int i = 0; i < type.length; i++) {
                    // type[i]：方法中的参数类型；params[i]参数值；paramTypeList[i]数据库参数类型
                    if (!paramTypeList.get(i).contains(type[i].getName())) {
                        methodFlag = false;
                        break;
                    }
                    // 判断了方法中参数类型与数据库参数类型相等，判断参数值类型
                    if (!type[i].isInstance(params[i])) {
                        if (!(params[i] instanceof Map)) {
                            paramFlag = false;
                            break;
                        }
                        // 若为方法与数据库类型均为实体类，且为参数为map 则尝试将param[i]转换实体类
                        Object entity = mapToEntity((Map<String, Object>) params[i], type[i]);
                        // 实体类转换失败
                        if (entity == null) {
                            entityFlag = false;
                            break;
                        }
                        params[i] = entity;
                    }
                    // 存在list参数时
                    if (params[i] instanceof List) {
                        // 若为方法与数据库类型均为实体类，且为参数为list 则尝试将param[i]转换实体类
                        Object entity = listToEntity((List<Object>) params[i], paramTypeList.get(i));
                        // list中的实体类转换失败
                        if (entity == null) {
                            entityFlag = false;
                            break;
                        }
                        params[i] = entity;
                    }
                }
                if (paramFlag && entityFlag && methodFlag) {
                    callMethod = method;
                    break;
                }
            }
            if (callMethod != null) {
                callMethod.setAccessible(true);
                logger.info("[ApiReflectUtils] 反射进入方法：" + className + "." + methodName);
                rtnData = callMethod.invoke(objBean, params);
                executeMsg = "执行完毕，成功调用方法";
                executeResult = ExecResult.SUCCESS.code();
            } else {
                if (!paramFlag) {
                    logger.error("[ApiReflectUtils] '方法中的参数类型' 与 '传入参数值类型' 不同，请检查参数");
                    executeMsg = "执行失败，'方法中的参数类型' 与 '传入参数值类型' 不同，请检查参数";
                }
                if (!methodFlag) {
                    logger.error("[ApiReflectUtils] '方法中的参数类型' 与 '数据库中模板的参数类型不同'，请检查参数");
                    executeMsg = "执行失败，'方法中的参数类型' 与 '数据库中模板的参数类型不同'，请检查模板的参数类型";
                }
                if (!entityFlag) {
                    logger.error("[ApiReflectUtils] 存在参数值，无法转化成方法中的实体类型，参数转化实体类出错");
                    executeMsg = "方法中存在实体类，尝试将参数值转化成实体类型，转化失败，请查看参数是否正确";
                }
                executeResult = ExecResult.ERROR.code();
            }
        } catch (ClassNotFoundException e) {
            logger.error("[ApiReflectUtils] 异常信息，类" + className + "不存在：" + e.getMessage());
            executeMsg = "类" + className + "不存在";
            executeResult = ExecResult.ERROR.code();
        } catch (Exception e) {
            executeMsg = "执行方法内部执行异常，异常信息：" + e.getMessage();
            StringBuilder expMsg = new StringBuilder("反射类内部执行异常，异常信息：");
            Throwable ex = e.getCause();
            if (ex == null) {
                expMsg.append(e);
            }
            while (ex != null) {
                if (ex instanceof SuperBootException) {
                    expMsg.append(StringUtils.isEmpty(ex.getMessage()) ? ((SuperBootException) ex).getCode() : ex.getMessage());
                    break;
                }
                Throwable tex = ex.getCause();
                if (tex == null) {
                    expMsg.append(getStackTrace(ex));
                    break;
                } else {
                    ex = tex;
                }
            }
            logger.error("[ApiReflectUtils] 异常信息：" + expMsg);
            executeResult = ExecResult.ERROR.code();
        }
        // 存入结果集，返回结果
        return ApiResult.builder()
                .data(rtnData)
                .message(executeMsg)
                .result(executeResult).build();
    }

    /**
     * @param throwable
     * @return
     * @Description: 获取堆栈详细信息
     */
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * @param map    需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空并返回null
     * @param entity 需要转化成的实体类
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @Description: Map转实体类
     */
    private <T> T mapToEntity(Map<String, Object> map, Class<T> entity) {
        T t;
        try {
            t = entity.getDeclaredConstructor().newInstance();
            for (String key : map.keySet()) {
                // 不存在该实体中的属性直接 NoSuchFieldException，必须一一对应
                Field field = entity.getDeclaredField(key);
                field.setAccessible(true);
                Object object = map.get(field.getName());
                if (object != null && field.getType().isAssignableFrom(object.getClass())) {
                    field.set(t, object);
                }
                field.setAccessible(false);
            }
            return t;
        } catch (NoSuchFieldException e) {
            logger.error("[ApiReflectUtils] map转实体类异常,实体类中无此属性，异常信息：" + e);
            return null;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("[ApiReflectUtils] 异常信息：" + e);
            return null;
        }
    }

    /**
     * @param list
     * @param paramType
     * @return
     * @Description: list转实体类(默认list中的object为map)
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> listToEntity(List<T> list, String paramType) {
        List<T> t;
        // 1.8后，list后跟#接实体类类型，#后面是list泛型中的实体类类型
        if (!paramType.contains("#")) {
            return list;
        }

        String entityTypeClassName = paramType.split("#")[1];
        try {
            Class<?> clazz = Class.forName(entityTypeClassName);
            t = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                t.add((T) mapToEntity((Map<String, Object>) list.get(i), clazz));
            }
            return t;
        } catch (ClassCastException e) {
            logger.error("[ApiReflectUtils] 类转化异常，请检查list参数与类型是否匹配，异常信息：" + e);
            return null;
        } catch (ClassNotFoundException e) {
            logger.error("[ApiReflectUtils] 无法找到list中的实体类，请检查list中的实体类类型是否正确，异常信息：" + e);
            return null;
        }
    }

}
