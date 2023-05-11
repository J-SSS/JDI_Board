package com.board.jdi_board.vo;

import lombok.Data;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Component;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
public class RelationVo {

    public List<String> terms(String content) {

        // 1. OKT 라이브러리로 게시글 내용을 형태소 단위로 분리
        // 2. 정규화(normalize) : 처리할 수 있는 문장으로 정제
        // 3. 토큰화(tokenize) 및 어근화(..koreanToken..) : 문장을 가능한 작은 단위로 쪼개고, 한국어 어근만을 필터링
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(content);
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

        // 4. 다시 명사(한,영)에 해당하는 단어만을 필터링하여 리스트화
        List<String> termsList = new ArrayList<>();

        for(KoreanTokenJava t : tokenList){
            if(t.getPos() == KoreanPosJava.Noun || t.getPos() == KoreanPosJava.Alpha){
                termsList.add(t.getText());
            }
        }


        return termsList;
    }
}
