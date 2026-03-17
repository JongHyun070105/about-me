# [프로젝트 기획 및 개발 문서] 프로젝트명: Auto-Me (On-Device AI 기반 발신 데이터 학습 모델)

## 1. 프로젝트 개요
- **프로젝트명**: Auto-Me
- **목적**: 백그라운드에서 메신저 알림과 화면 텍스트 데이터를 수집해 사용자의 발신/수신 대화 맥락을 모두 로컬 DB에 학습하고, 온디바이스 AI(Gemini Nano)를 통해 특정 대화 상대에 맞춤화된 답장을 생성 및 전송하는 안드로이드 전용 애플리케이션.
- **코어 아키텍처**: UI가 최소화된 백그라운드 서비스 중심 설계. 사용자의 직접 발신 메시지까지 수집하기 위해 AccessibilityService와 NotificationListenerService를 하이브리드로 운용함.

## 2. 데이터 수집 아키텍처 (우선순위 기반)

### Primary Pipeline (자동 수집)
사용자의 수동 개입 없이 백그라운드에서 대화 데이터를 양방향(수신/발신)으로 적재하는 메인 파이프라인입니다.

1. **발신 데이터 (사용자 본인 말투 학습): AccessibilityService 활용**
   - 메신저 앱(예: 카카오톡)이 포그라운드에 실행 중일 때 활성화.
   - 사용자가 텍스트 입력창(EditText Node)에 메시지를 작성하고 전송 버튼을 누르는 이벤트를 감지하여 해당 텍스트를 가로채 로컬 DB에 '발신(Sent)' 상태로 저장.

2. **수신 데이터 (상대방 맥락 파악): NotificationListenerService 파싱**
   - 수신되는 푸시 알림 데이터 파싱.
   - 안드로이드 11 이상에서 지원되는 `Notification.MessagingStyle` 내의 `EXTRA_HISTORIC_MESSAGES` 및 `EXTRA_MESSAGES` 배열을 우선 탐색하여, 알림 한 건당 최대한 많은 최근 대화 히스토리를 한 번에 로컬 DB에 '수신(Received)' 상태로 저장.

### Fallback Pipeline (수동 수집)
Primary Pipeline이 OS 권한 이슈나 메신저 업데이트로 작동하지 않을 때 사용하는 최후의 보루입니다.

- **대화 내역 내보내기 파싱 (.txt Import)**:
  - 메신저 앱 기능인 '대화 내용 내보내기'로 추출된 텍스트 파일을 앱 내에서 Import.
  - 정규표현식을 통해 날짜, 발신자, 메시지 내용을 분리하여 로컬 DB에 일괄 적재(Bulk Insert). 초기 데이터 부족(Cold Start) 문제를 해결하는 데에도 사용.

## 3. 핵심 기능 정의 (PRD)

- **Background Data Aggregator**:
  - 기기 부팅 시 자동 실행. Room Database를 구축하여 Room_ID 단위로 수신/발신 메시지를 타임스탬프와 함께 로깅.
- **On-Device AI Generation (Gemini Nano)**:
  - 알림 수신 시 안드로이드 AICore 호출. 로컬 DB에서 해당 방의 최근 N개 메시지를 조회하여 프롬프트의 컨텍스트로 주입.
  - 네트워크 없이 기기 내부에서 3가지 페르소나(수락, 거절, 모호함) 텍스트 생성. (PII 마스킹 모듈 적용 필수)
- **Floating Options & Inline Reply**:
  - 시스템 최상단 플로팅 뷰(System_Alert_Window)로 3가지 생성 텍스트 노출. 터치 시 알림의 `RemoteInput` 액션을 트리거하여 백그라운드 답장 전송.

## 4. 기술 스택
- **Frontend (설정 UI)**: Flutter (권한 허용 안내, 로컬 DB 뷰어, .txt 파일 Import UI)
- **Native Core (Background)**: Kotlin (AccessibilityService, NotificationListenerService, Room)
- **AI Engine**: Google AICore (Gemini Nano)

## 5. 리스크 분석 (Fact vs Opinion)

### Fact (객관적 사실)
- **Google Play Store 정책 위반 리스크**: AccessibilityService API를 목적 외 용도로 사용할 경우 매우 엄격한 심사 대상임. '명시적 공개'와 사용자 동의 절차 필수.
- **히스토리 데이터의 파편화**: 메신저 개발사의 개별 구현 로직에 따라 데이터 수집 안정성이 달라질 수 있음.

### Opinion (분석 기반 의견)
- **모듈 분리 개발 전략**: 스토어 심사 거절 리스크를 고려하여 '텍스트 파일 파싱(Fallback)'을 기본으로 하되, 자동 수집 기능은 투트랙 전략으로 배포하는 것이 유리함.

## 6. 전체 요약 및 개선 방향 제안
- **요약**: Flutter와 Kotlin을 결합하여 양방향 대화 데이터를 로컬에 자동 적재하고, Gemini Nano를 통해 개인화된 답장을 생성하는 오프라인 중심 아키텍처.
- **개선 방향**: 데이터 파이프라인 중간에 **'정규표현식 기반 PII(개인식별정보) 마스킹 모듈'**을 추가하여 보안 신뢰성 확보. (현재 구현 완료)
