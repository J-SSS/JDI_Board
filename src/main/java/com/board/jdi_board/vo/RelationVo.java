package com.board.jdi_board.vo;

import com.board.jdi_board.dto.BoardsDto;
import com.board.jdi_board.dto.RelationsDto;
import com.board.jdi_board.service.RelationsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tomcat.util.json.JSONParser;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Component;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Data
@Component

public class RelationVo {
    public RelationVo(RelationsService relationsService) {
        this.relationsService = relationsService;
    }

    private RelationsService relationsService;

    public List<String> terms(HashSet<String> totalTerms, String content) {

        // 1. OKT 라이브러리로 게시글 내용을 형태소 단위로 분리
        // 2. 정규화(normalize) : 처리할 수 있는 문장으로 정제
        // 3. 토큰화(tokenize) 및 어근화(..koreanToken..) : 문장을 가능한 작은 단위로 쪼개고, 한국어 어근만을 필터링
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(content);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

        // 4. 다시 명사(한,영)에 해당하는 단어만을 필터링하여 리스트화
        List<String> myList = new ArrayList<>();

        for(KoreanTokenJava t : tokenList){
            if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                myList.add(t.getText());
                totalTerms.add(t.getText());

            }
        }


        return myList;
    }

    public void terms2(List<BoardsDto> boards) throws JsonProcessingException {

        for(BoardsDto board : boards){
            CharSequence normalized = OpenKoreanTextProcessorJava.normalize(board.getContent());
            Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
            List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

            List<String> tempList = new ArrayList<>();

            for(KoreanTokenJava t : tokenList){
                if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                    tempList.add(t.getText());
                }
            }
            RelationsDto dto = new RelationsDto();
            dto.setBId(board.getBId());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(tempList);
            dto.setTerms(jsonString);
            relationsService.register(dto);


        }


    }
}
