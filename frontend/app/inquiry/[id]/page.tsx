"use client";

import React, { useState, useEffect } from 'react';
import { useParams } from 'next/navigation';
import axios from 'axios';
import styles from './inquiryDetail.module.css';

type ReplyDetailResponse = {
  // ReplyDetailResponse의 필드 정의
};

type InquiryDetailResponse = {
  userId: number;
  id: number;
  subject: string;
  content: string;
  createdAt: string; // LocalDateTime을 문자열로 변환
  reply: ReplyDetailResponse | null;
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

const InquiryDetailPage = () => {
  const { id } = useParams();
  const [inquiry, setInquiry] = useState<InquiryDetailResponse | null>(null);


  useEffect(() => {
    if (id) {
    const fetchInquiry = async () => {
      console.log('Fetching inquiry detail for ID:', id); // ID 로그 출력
      try {
        const response = await axios.get<RsData<InquiryDetailResponse>>(`/api/v1/common/inquiries/${id}`);
        console.log('Response data:', response.data); // 디버깅 로그
        setInquiry(response.data.data);
      } catch (error) {
        console.error('Error fetching inquiry detail:', error);
      }
    };

    fetchInquiry();
  }
  }, [id]);

  if (!inquiry) return <div>Loading...</div>;

  return (
    <div className={styles.inquiryDetailContainer}>
      <div className={styles.inquiryDetailSubjectRow}>
        <h1 className={styles.inquiryDetailHeader}>{inquiry.subject}</h1>
        <p className={styles.inquiryDetailDate}>{new Date(inquiry.createdAt).toLocaleDateString('ko-KR')}</p>
      </div>
      <hr className={styles.inquiryDetailDivider}/>
      <p className={styles.inquiryDetailContent}>{inquiry.content}</p>
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