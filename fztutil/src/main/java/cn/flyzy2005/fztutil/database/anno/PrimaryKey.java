package cn.flyzy2005.fztutil.database.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Fly on 2017/9/4.
 * 主键，只能设置一个主键
 * 如果没有设置则默认为id（属性名和数据库中字段名都为id）
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
}
