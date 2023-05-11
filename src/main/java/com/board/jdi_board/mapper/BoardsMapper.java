package com.board.jdi_board.mapper;

import com.board.jdi_board.dto.BoardsDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardsMapper {

    List<BoardsDto> findAll();

    BoardsDto findById();
}
