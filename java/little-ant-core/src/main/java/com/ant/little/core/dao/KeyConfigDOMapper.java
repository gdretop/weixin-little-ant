package com.ant.little.core.dao;

import com.ant.little.core.domain.KeyConfigDO;
import com.ant.little.core.domain.KeyConfigDOExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface KeyConfigDOMapper {
    long countByExample(KeyConfigDOExample example);

    int deleteByExample(KeyConfigDOExample example);

    int deleteByPrimaryKey(Long id);

    int insert(KeyConfigDO row);

    int insertSelective(KeyConfigDO row);

    List<KeyConfigDO> selectByExample(KeyConfigDOExample example);

    KeyConfigDO selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") KeyConfigDO row, @Param("example") KeyConfigDOExample example);

    int updateByExample(@Param("row") KeyConfigDO row, @Param("example") KeyConfigDOExample example);

    int updateByPrimaryKeySelective(KeyConfigDO row);

    int updateByPrimaryKey(KeyConfigDO row);
}