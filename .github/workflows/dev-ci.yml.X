#name: dev-ci
#on:
#  pull_request:
#    branches:
#      - release
#
#jobs:
#  test:
#    name: 코드 테스트
#    runs-on: ubuntu-latest
#
#    steps:
#      - name: 브랜치로 체크아웃
#        uses: actions/checkout@v4
#
#      - name: JDK 세팅
#        uses: actions/setup-java@v4
#        with:
#          java-version: '21'
#          distribution: 'temurin'
#
#      - name: 권한 세팅
#        run: chmod +x ./gradlew
#
#      - name: 테스트 수행
#        run: ./gradlew test --no-daemon