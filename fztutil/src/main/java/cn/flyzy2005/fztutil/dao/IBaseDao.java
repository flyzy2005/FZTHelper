package cn.flyzy2005.fztutil.dao;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by Fly on 2017/5/22.
 * <p>
 *     很关键的一个字段就是<b>id</b>，并且判断就是通过id来判断的，并不会识别其他如bookId，carId
 *     在{@link AbstractDao}中对这些方法进行了封装，代码并不复杂，可以稍微理解一下最终效果再使用
 *     暂时插入和更新只支持String和int类型，查找自动映射只支持long、String、boolean，详见{@link cn.flyzy2005.fztutil.entityMapper.BeanUtils}
 *     如果功能不够，可以通过{@code getDatabase()}获得SQLiteDatabase自己写具体方法
 * </p>
 */

interface IBaseDao<T> {
    /**
     * 根据id进行查询
     * @param id id
     * @return entity实例
     */
    T findById(Object id);

    /**
     *根据条件条件进行查询
     * @param condition 查询条件{"name":"fly"}(封装在JSONObject中)
     * @return 所有满足条件的entity实例
     */
    List<T> findByParams(JSONObject condition);

    /**
     * 查询表中所有数据
     * @return 所有entity实例
     */
    List<T> findAll();

    /**
     * 根据sql语句进行查询
     * @param sql sql语句
     * @return 满足查询条件的entity实例
     */
    List<T> findBySql(String sql);

    /**
     * 添加一条记录
     * @param model entity实例
     * @param withId 是否需要插入Id，不插入的话就采用SQLite自带的自增策略
     * @return 是否插入成功
     */
    boolean insert(T model, boolean withId);

    /**
     * 根据Id删除一条记录，依然是使用entity的id属性来进行删除操作，并不会用到其他字段
     * @param model entity实例
     * @return 是否删除成功
     */
    boolean delete(T model);

    /**
     * 根据Id删除一条记录
     * @param id Id
     * @return 是否删除成功
     */
    boolean deleteById(Object id);

    /**
     * 根据条件删除一条记录
     * @param condition 删除条件
     * @return 是否删除成功
     */
    boolean deleteByParams(JSONObject condition);

    /**
     * 修改一条记录，会根据传入的entity的id进行匹配修改，并且会用除id的所有其他属性的新值更新数据库
     * @param model 修改的entity实例
     * @return 是否修改成功
     */
    boolean update(T model);

    /**
     * 根据条件修改一条记录，并且会用除id的所有其他属性的新值更新数据库
     * @param model 修改的entity实例
     * @param condition 条件
     * @return 是否修改成功
     */
    boolean updateByParams(T model, JSONObject condition);
}
