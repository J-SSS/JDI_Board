package com.board.jdi_board.mapper;

import com.board.jdi_board.dto.RelationsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RelationsMapper {
    List<RelationsDto> findAll();
    RelationsDto findByBrId(int brId);
    String findByBId(int bId);
    int insertOne(RelationsDto relationsDto);
    int updateOne(RelationsDto relationsDto);
    int updateRel(RelationsDto relationsDto);
}
