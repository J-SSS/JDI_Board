## TH-IDF 기법과 Cosine Similarity 분석을 활용한 연관 게시글 구현
<hr>

##### *) 참고한 내용
1. 자연어 처리 및 유사도 분석
  - 딥 러닝을 이용한 자연어 처리 입문(안상준, 유원준)
  - https://wikidocs.net/24603
  - https://needjarvis.tistory.com/516
2. TH-IDF 활용한 빈도 분석
  - https://needjarvis.tistory.com/678
3. 더미데이터 생성 : ChatGPT
<hr>


### Ⅰ. 개발환경
- JAVA / Spring Boot / Thymeleaf
- MySQL / MyBatis
- Gradle
- IntelliJ, Git & Git Hub
- Open Korean Text(OKT) 라이브러리 (형태소 추출)
<br><br>

### Ⅱ. 구현 내용
#### (1). 게시판 CRUD
- MVC 패턴 설계
- 게시글 리스트 보기, 상세보기 및 게시글 등록 기능 구현
  <br><br>
#### (2). 연관 게시글 기능
- 기본 로직
  - 각 게시글의 내용에서 명사에 해당하는 형태소 추출
  - TH-IDF 기법으로 빈도 분석
  - Cosine Similarity로 유사도 분석하여 높은 순으로 연관 게시글 등록
  <br><br>
- 로직 상세(요구사항 기반)
  - 게시글 생성시 연관 게시글 찾아서 연결 <br>
  - 

