package com.board.jdi_board.mapper;

import com.board.jdi_board.dto.RelationsDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RelationsMapper {
    int insertOne(RelationsDto relationsDto);
    int updateOne(RelationsDto relationsDto);
}
