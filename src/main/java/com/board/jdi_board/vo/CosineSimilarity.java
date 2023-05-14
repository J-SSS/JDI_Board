package com.board.jdi_board.vo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity {

    // 두 개의 TF-IDF 행렬을 비교하여 Cosine Similarity를 분석하는 메서드
    public Double cosineSimilarity(Map<String, Double> leftVector, Map<String, Double> rightVector) {

        // 두 행렬 간 교집합이 존재하는 Key를 찾으며, 만약 교집합인 키가 2개 미만인 경우 유사도 분석을 중단함
        Set<String> intersection = getIntersection(leftVector, rightVector);
//        System.out.println("교집합의 갯수 = " + intersection.size());
        if(intersection.size()<2) return 0.0;

        // 분자 부분 스칼라곱
         double dotProduct = dot(leftVector, rightVector, intersection);

        // 분모 부분
        double d1 = 0.0d;
        for ( Double value : leftVector.values()) {
            d1 += Math.pow(value, 2);
        }
        double d2 = 0.0d;
        for ( Double value : rightVector.values()) {
            d2 += Math.pow(value, 2);
        }

        // 분자/분모 = 코사인유사도 결과
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        } else {
            cosineSimilarity = (double) (dotProduct / (double) (Math.sqrt(d1) * Math.sqrt(d2)));
        }
        return cosineSimilarity;
    }

    // 두 TF-IDF 행렬의 교집합이 존재하는 Key를 찾아냄
    private Set<String> getIntersection( Map<String, Double> leftVector,
                                               Map<String, Double> rightVector) {
         Set<String> intersection = new HashSet<>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }

   // 공식의 분자 부분 = 스칼라곱
    private double dot( Map<String, Double> leftVector,  Map<String, Double> rightVector,
                        Set<String> intersection) {
        double dotProduct = 0;
        for ( String key : intersection) {
            dotProduct += leftVector.get(key) * rightVector.get(key);
        }
        return dotProduct;
    }

}