package com.imooc.dmp.mapper.hbase;

import com.imooc.dmp.entity.hbase.HBaseT;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {

    List<HBaseT> getUsers();
}
