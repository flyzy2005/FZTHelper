package cn.flyzy2005.fztutil.transfer;


import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fly on 2017/4/21.
 * 解析参数，拼接SQL语句
 */
public class ParamTransfer {
    public static Object[] paramsAnalyseSQL(JSONObject params) {

        if (params != null) {
            List<String> names = new ArrayList<String>();

            List<String> values = new ArrayList<String>();

            for (String key : params.keySet()) {

                names.add(key);

                values.add(params.get(key) + "");

            }

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < names.size() - 1; i++) {

                sb.append(names.get(i) + "=? and ");

            }

            sb.append(names.get(names.size() - 1) + "=? ");

            return new Object[]{sb.toString(), values.toArray(new String[]{})};

        } else {

            return new Object[]{null, null};

        }

    }
}
