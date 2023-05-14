package com.board.jdi_board.service;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.mapper.BoardsMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardsServiceImpl implements BoardsService {

    private BoardsMapper boardsMapper;
    @Override
    public List<BoardsDto> list() {
        return boardsMapper.findAll();
    }

    @Override
    public int register(BoardsDto boardsDto) {
        return boardsMapper.insertOne(boardsDto);
    }

    @Override
    public BoardsDto detail(int bId) {
        return boardsMapper.findByBId(bId);
    }
}
