package cn.flyzy2005.fztutil.database.dao;


import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.flyzy2005.fztutil.database.DataBaseConfig;

/**
 * Created by Fly on 2017/4/21.
 * 解析参数，拼接SQL语句
 */
class ParamsTransfer {
    static Object[] paramsAnalyseSQL(JSONObject params) {
        if (params != null) {
            List<String> names = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (String key : params.keySet()) {
                names.add(key);
                values.add(params.get(key) + "");
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < names.size() - 1; i++) {
                String columnName = names.get(i);
                append(sb, columnName, true);
            }
            String columnName = names.get(names.size() - 1);
            append(sb, columnName, false);

            return new Object[]{sb.toString(), values.toArray(new String[]{})};
        } else {
            return new Object[]{null, null};
        }
    }

    private static void append(StringBuilder builder, String columnName, boolean withAnd) {
        String likeStr = DataBaseConfig.LIKE_END_STRING;
        if (columnName.endsWith(likeStr)) {
            columnName = columnName.replace(likeStr, "");
            if (withAnd)
                builder.append(columnName + " like ? and ");
            else
                builder.append(columnName + " like ? ");
        } else {
            if (withAnd)
                builder.append(columnName + "=? and ");
            else
                builder.append(columnName + "=? ");
        }
    }
}
