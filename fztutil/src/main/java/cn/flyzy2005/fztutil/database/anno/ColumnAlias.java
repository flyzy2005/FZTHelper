package cn.flyzy2005.fztutil.database.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Fly on 2017/9/4.
 * 数据库中对应的列的名字
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnAlias {
    String columnName();
}
