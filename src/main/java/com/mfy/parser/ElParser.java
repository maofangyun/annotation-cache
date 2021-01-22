package com.mfy.parser;

public class ElParser {

    //private static ExpressionParser parser = new SpelExpressionParser();

    public static String getKey(String key,String[] paramNames,Object[] args) {
        //StandardEvaluationContext context = new StandardEvaluationContext();

        if(args.length <= 0) {
            return null;
        }

        for (int i = 0; i < args.length; i++) {
            //context.setVariable(paramNames[i],args[i]);
            key = key+"@"+paramNames[i]+":"+args[i];
        }
        return key;
        //Expression expression = parser.parseExpression(key);
        //return expression.getValue(context,String.class);
    }
}
