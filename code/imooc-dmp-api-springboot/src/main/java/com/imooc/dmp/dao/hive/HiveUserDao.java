package com.imooc.dmp.dao.hive;

import com.imooc.dmp.entity.hive.HiveUser;
import com.imooc.dmp.service.impl.hive.UserRowMapperImpl;
import org.springframework.stereotype.Repository;

@Repository
public class HiveUserDao extends HiveBaseDao {

    /**
     * 用户列表
     * @return User
     */
    public HiveUser getUsers(){
        String sql = "select * from `dwd.dim_user` limit 1";
        return this.getHiveJdbcTemplate().queryForObject(
                sql,
                new UserRowMapperImpl());
    }
}
