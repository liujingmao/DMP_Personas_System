package com.imooc.dmp.mapper.phoenix;

import com.imooc.dmp.entity.phoenix.PhoenixT;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhoenixTMapper {

    List<PhoenixT> getPhoenixT();
}
