## TH-IDF 기법과 Cosine Similarity 분석을 활용한 연관 게시글 구현
<hr>

### Ⅰ. 개발환경
- JAVA / Spring Boot / Thymeleaf
- MySQL / MyBatis
- Gradle
- IntelliJ, Git & Git Hub
- Open Korean Text(OKT) 라이브러리 (형태소 추출)
<br><br>

### Ⅱ. 구현 내용
##### (1). 게시판 CRUD
- MVC 패턴 설계
- 리스트 보기, 글 상세보기 및 등록 기능 구현
  <br><br>
##### (2). 연관 게시글 기능
- 기본 로직
  1. 각 게시글의 내용에서 명사에 해당하는 형태소 추출
  2. TH-IDF 기법으로 빈도 분석
  3. Cosine Similarity로 상관도 파악
     <br><br>
- 상세 설명

