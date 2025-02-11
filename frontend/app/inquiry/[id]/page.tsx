"use client";

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import styles from './InquiryDetailPage.module.css';

type ReplyDetailResponse = {
  // ReplyDetailResponse의 필드 정의
};

type InquiryDetailResponse = {
  userId: number;
  id: number;
  subject: string;
  content: string;
  response: number;
  createdAt: string; // LocalDateTime을 문자열로 변환
  reply: ReplyDetailResponse | null;
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

const InquiryDetailPage = ({ params }) => {
  const [inquiry, setInquiry] = useState<InquiryDetailResponse | null>(null);

  useEffect(() => {
    const fetchInquiryDetail = async () => {
      try {
        const response = await axios.get<RsData<InquiryDetailResponse>>(`/api/v1/common/inquiries/${params.id}`);
        setInquiry(response.data.data);
      } catch (error) {
        console.error('Error fetching inquiry detail:', error);
      }
    };

    fetchInquiryDetail();
  }, [params.id]);

  if (!inquiry) return <div>Loading...</div>;

  return (
    <div className={styles.inquiryDetailContainer}>
      <h1 className={styles.inquiryDetailHeader}>{inquiry.subject}</h1>
      <p>{inquiry.content}</p>
      <p>작성자 ID: {inquiry.userId}</p>
      <p>응답 상태: {inquiry.response}</p>
      <p>작성일: {new Date(inquiry.createdAt).toLocaleDateString('ko-KR')}</p>
      {inquiry.reply && (
        <div>
          <h2>답변</h2>
          {/* 답변 내용 표시 */}
        </div>
      )}
    </div>
  );
};

export default InquiryDetailPage; 