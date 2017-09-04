package cn.flyzy2005.fztutil.database.dao;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

import cn.flyzy2005.fztutil.database.anno.PrimaryKey;

/**r
 * Created by Fly on 2017/5/22.
 * <p>
 * 建议使用时对entity都设置{@link PrimaryKey}，许多方法是根据这个字段来实现相应功能的
 * 实体类的成员变量支持类型包括double、float、int、byte、long、short、boolean、String、byte[]等
 * 如果功能不满足具体要求，可以通过{@code getDatabase()}获得SQLiteDatabase自己写具体方法
 * </p>
 */

interface IBaseDao<T> {
    /**
     * 根据primaryKey进行查询
     * 需要设置{@link PrimaryKey}
     *
     * @param primaryKey 主键的值
     * @return entity实例，没有则为null
     */
    T getByPrimaryKey(Object primaryKey);

    /**
     * 根据条件条件进行查询
     *
     * @param condition 查询条件{"name":"fly"}(封装在JSONObject中)
     * @return 满足条件的第一个实例，没有则为null
     */
    T getByParams(JSONObject condition);

    /**
     * 根据条件条件进行查询
     *
     * @param condition 查询条件{"name":"fly"}(封装在JSONObject中)
     * @return 所有满足条件的entity实例，没有则为空ArrayList
     */
    List<T> listByParams(JSONObject condition);

    /**
     * 查询表中所有数据
     *
     * @return 所有entity实例，没有则为空ArrayList
     */
    List<T> listAll();

    /**
     * 根据sql语句进行查询
     *
     * @param sql sql语句
     * @return 满足条件的第一个实例，没有则为null
     */
    T getBySql(String sql);

    /**
     * 根据sql语句进行查询
     *
     * @param sql sql语句
     * @return 满足查询条件的entity实例，没有则为空ArrayList
     */
    List<T> listBySql(String sql);

    /**
     * 添加一条记录
     *
     * @param model  entity实例
     * @param withId 是否需要插入PrimaryKey，不插入的话就采用SQLite自带的自增策略（不插入需要设置{@link PrimaryKey}）
     * @return 是否插入成功
     */
    boolean insert(T model, boolean withId);

    /**
     * 根据primaryKey删除一条记录
     *
     * @param model entity实例
     * @return 是否删除成功
     */
    boolean delete(T model);

    /**
     * 根据Id删除一条记录
     *
     * @param primaryKey primaryKey
     * @return 是否删除成功
     */
    boolean deleteByPrimaryKey(Object primaryKey);

    /**
     * 根据条件删除一条（多条）记录
     *
     * @param condition 删除条件
     * @return 是否删除成功
     */
    boolean deleteByParams(JSONObject condition);

    /**
     * 删除表中所有数据
     *
     * @return 是否删除成功
     */
    boolean deleteAll();

    /**
     * 修改一条记录，会根据传入的entity的primaryKey进行匹配修改，并且会用除primaryKey之外的所有其他属性的新值更新数据库
     *
     * @param model 修改的entity实例
     * @return 是否修改成功
     */
    boolean update(T model);

    /**
     * 根据条件修改一条记录，并且会用除primaryKey的所有其他属性的新值更新数据库
     *
     * @param model     修改的entity实例
     * @param condition 条件
     * @return 是否修改成功
     */
    boolean updateByParams(T model, JSONObject condition);

    /**
     * 所有行数
     *
     * @return int
     */
    int count();

    /**
     * 满足条件的所有行数
     *
     * @param condition 条件
     * @return int
     */
    int countByParams(JSONObject condition);
}
