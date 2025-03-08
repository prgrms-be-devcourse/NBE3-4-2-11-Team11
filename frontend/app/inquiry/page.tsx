'use client'

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import axios from 'axios';
import styles from './inquiryList.module.css';

type ReplyDetailResponse = {
  id: number;
  createdAt: string;
  content: string;
  type?: "comment" | "reply";
};

type CommentDetailResponse = ReplyDetailResponse;

type InquiryDetailResponse = {
  userId: number;
  id: number;
  subject: string;
  content: string;
  createdAt: string; // LocalDateTime을 문자열로 변환
  repliesAndComments: Array<ReplyDetailResponse | CommentDetailResponse>; // 댓글과 답변 리스트
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};


const InquiryPage = () => {
  const [inquiries, setInquiries] = useState<InquiryDetailResponse[]>([]);
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  const [token, setToken] = useState<string | null>(null); // token 상태 정의
  const [role, setRole] = useState<string | null>(null); // 역할 상태 추가

  useEffect(() => {
    // 로그인 상태 체크 API 호출
    const checkLoginStatus = async () => {
      try {
        const loginStatusResponse = await axios.get('/api/v1/auth/status', { withCredentials: true });
        if (!loginStatusResponse.data.isLoggedIn) {
            alert("로그인을 해야 가능한 서비스입니다.")
          window.location.href = '/login'; // 로그인되지 않으면 로그인 페이지로 리다이렉트
          return;
        }

        setToken("authenticated"); // 로그인된 상태
        setRole(loginStatusResponse.data.role); // 역할 정보 저장
      } catch (error) {
        console.error("로그인 상태 확인 오류:", error);
        window.location.href = '/login'; // 오류 발생 시 로그인 페이지로 리다이렉트
      }
    };

    // 로그인 상태 체크 후 데이터 가져오기
    checkLoginStatus();
  }, []);

  useEffect(() => {
    const fetchInquiries = async () => {
      try {
        const response = await axios.get<RsData<InquiryDetailResponse[]>>('/api/v1/common/inquiries');
        if (Array.isArray(response.data.data)) {
          setInquiries(response.data.data);
        } else {
          console.error('Data is not an array:', response.data.data);
        }
      } catch (error) {
        console.error('Error fetching inquiries:', error);
      }
    };

    fetchInquiries();
  }, []);  // 컴포넌트가 처음 렌더링될 때만 호출되도록 설정


  const handleCreateInquiry = () => {
    window.location.href = '/inquiry/create';
  };

  return (
    <div className={styles.inquiryContainer}>
      <h1 className={styles.inquiryHeader} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        문의하기
        <button 
          onClick={handleCreateInquiry} 
          className={styles.createButton} 
          style={{ 
            backgroundColor: '#0070f3',
            color: '#ffffff',
            padding: '10px 15px',
            fontSize: '16px',
            border: 'none',
            borderRadius: '5px',
            cursor: 'pointer'
          }}
        >
          작성하기
        </button>
      </h1>
      <ul>
        {inquiries.map((inquiry) => (
          <li key={inquiry.id} className={styles.inquiryBox}>
            <Link href={`/inquiry/${inquiry.id}`}>
              <div className={styles.inquirySubjectRow}>
                <div className={styles.inquirySubject}>{inquiry.subject}</div>
                <div className={styles.inquiryDate}>{new Date(inquiry.createdAt).toLocaleDateString('ko-KR')}</div>
              </div>
              <div className={styles.inquiryResponseStatus}>
                {inquiry.response === 0 ? '답변 예정' : '답변 완료'}
              </div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default InquiryPage;