package com.imooc.dmp.mapper.clickhouse;

import com.imooc.dmp.entity.clickhouse.ClickHouseT;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 读取 ch 测试表
 */
@Repository
public interface ClickHouseTMapper {

    List<ClickHouseT> getClickHouseT();
}
