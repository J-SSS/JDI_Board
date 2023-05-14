package com.board.jdi_board.mapper;

import com.board.jdi_board.dto.KeywordsDto;
import com.board.jdi_board.dto.RelationsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KeywordsMapper {
    List<KeywordsDto> findAll();
    KeywordsDto findByKId(int kId);
    int insertOne(KeywordsDto keywordsDto);
    int updateOne(KeywordsDto keywordsDto);
}
