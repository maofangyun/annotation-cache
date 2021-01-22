package com.mfy.handler;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class ListHandler implements TypeHandler {
    @Override
    public boolean support(Type returnType) {
        // 获取泛型所代表的类型
        if(returnType instanceof ParameterizedType){
            return ((ParameterizedTypeImpl) returnType).getRawType().isAssignableFrom(List.class);
        }
        return returnType.equals(List.class);
    }

    @Override
    public Object handler(Object result, Type returnType) {
        if(returnType instanceof ParameterizedType){
            Type[] actualTypeArguments = ((ParameterizedTypeImpl) returnType).getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                return JSONArray.parseArray((String) result, actualTypeArguments);
            }
        }
        return JSONArray.parseArray((String) result, returnType.getClass());
    }
}
