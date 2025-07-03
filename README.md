# Port-It Backend

| **개발자** | [홍시은](https://github.com/XIOZ119) | [한동근](https://github.com/l0o0lv) |
|-----------|:------------------------:|:---------------------------:|
| **프로필** | <img src="https://avatars.githubusercontent.com/u/63907578?v=4" width="150"/> | <img src="https://avatars.githubusercontent.com/u/128709695?s=400&u=1e67683655246f12e26a2c7aeaa2a9976b00b7c1&v=4" width="150"/> |
| **기술 스택** | <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" width="40" height="40"/> | <img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" width="40" height="40"/> |
| **역할** | `백엔드` | `백엔드` |
| **R&R** | 공공데이터 API<br>EC2<br>RDS<br>S3 | WebSocket<br>JWT |

## 📑 목차

- [Port-It Backend](#port-it-backend)
- [시스템 아키텍처](#시스템-아키텍처)
- [Database Schema (ERD)](#database-schema-erd)
- [기능 목록](#기능-목록)
- [API Reference](#api-reference)

# 시스템 아키텍처
<img width="900" alt="스크린샷 2025-07-03 오후 5 10 25" src="https://github.com/user-attachments/assets/a5ba4674-8aee-4274-aa97-7874f3f88447" />

# Database Schema (ERD)

![ERD](./docs/portIt_ERD.png)

# 기능 목록

| 구분 | 기능 |
|------|------|
| **회원/인증 관리** | 유저 및 기업 회원가입, 로그인, 로그아웃, JWT 인증, 토큰 재발급 |
| **작품 관리** | 작품 등록, 수정, 삭제, 조회, 태그 생성 |
| **기업 관리** | 공공데이터 기반 기업 등록, 기업 정보 관리, 작품 좋아요 |
| **결제** | 토스 결제 연동, 결제 정보 관리 |
| **프리미엄 관리** | 작품 프리미엄 등록 및 조회 |
| **채팅** | 실시간 채팅방 생성, 입장, 퇴장, 채팅 목록 조회 |
| **알림** | FCM 기반 푸시 알림 및 알림 관리 |
| **SMS/이메일 인증** | 휴대폰 및 이메일 인증번호 발송 및 검증 |

# API Reference
[![Swagger](https://img.shields.io/badge/Swagger-UI-green)](https://6-data-contest.github.io/portit-server/)
