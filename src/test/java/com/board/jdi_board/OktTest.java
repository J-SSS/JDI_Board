package com.board.jdi_board;

import org.junit.jupiter.api.Test;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.boot.test.context.SpringBootTest;
import scala.collection.Seq;

import java.util.List;

@SpringBootTest
public class OktTest {
    @Test
    void oktTest() {

        String text = "HTML, CSS, JavaScript를 이용하여 간단한 웹 페이지를 만드는 수업입니다.";

        // Normalize
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
        // HTML, CSS, JavaScript를 이용하여 간단한 웹 페이지를 만드는 수업입니다.
        System.out.println("정규화\n" + normalized);

        // Tokenize
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        // List(HTML(Alpha: 0, 4), ,(Punctuation: 4, 1),  (Space: 5, 1), CSS(Alpha: 6, 3), ,(Punctuation: 9, 1),
        // (Space: 10, 1), JavaScript(Alpha: 11, 10), 를(Noun: 21, 1),  (Space: 22, 1), 이용(Noun: 23, 2),
        // 하여(Verb(하다): 25, 2),  (Space: 27, 1), 간단한(Adjective(간단하다): 28, 3),  (Space: 31, 1),
        // 웹(Noun: 32, 1),  (Space: 33, 1), 페이지(Noun: 34, 3), 를(Josa: 37, 1),  (Space: 38, 1),
        // 만드는(Verb(만들다): 39, 3),  (Space: 42, 1), 수업(Noun: 43, 2), 입니다(Adjective(이다): 45, 3),
        // .(Punctuation: 48, 1))
        System.out.println("토큰화\n" + tokens);

        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
        // [HTML(Alpha: 0, 4), ,(Punctuation: 4, 1), CSS(Alpha: 6, 3), ,(Punctuation: 9, 1),
        // JavaScript(Alpha: 11, 10), 를(Noun: 21, 1), 이용(Noun: 23, 2), 하여(Verb(하다): 25, 2),
        // 간단한(Adjective(간단하다): 28, 3), 웹(Noun: 32, 1), 페이지(Noun: 34, 3), 를(Josa: 37, 1),
        // 만드는(Verb(만들다): 39, 3), 수업(Noun: 43, 2), 입니다(Adjective(이다): 45, 3), .(Punctuation: 48, 1)]
        System.out.println("어근화\n" + tokenList);

        // Phrase Extraction
        List<KoreanPhraseExtractor.KoreanPhrase> phrases = OpenKoreanTextProcessorJava.extractPhrases(tokens, true, true);
        // [HTML(Noun: 0, 4), CSS(Noun: 6, 3), JavaScript를(Noun: 11, 11), JavaScript를 이용(Noun: 11, 14),
        // 간단한 웹(Noun: 28, 5), 간단한 웹 페이지(Noun: 28, 9), 수업(Noun: 43, 2), JavaScript(Noun: 11, 10),
        // 이용(Noun: 23, 2), 페이지(Noun: 34, 3)]
        System.out.println("어구 추출\n" + phrases);
    }
}
