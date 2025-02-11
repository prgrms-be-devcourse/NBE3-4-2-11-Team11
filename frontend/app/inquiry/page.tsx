"use client"

import React, { useState, useEffect } from 'react';
import Link from 'next/link';
import axios from 'axios';
import styles from './inquiryList.module.css';

type InquiryDetailResponse = {
  userId: number;
  id: number;
  subject: string;
  content: string;
  response: number;
  createdAt: string;
  reply: ReplyDetailResponse | null;
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

const InquiryPage = () => {
  const [inquiries, setInquiries] = useState<InquiryDetailResponse[]>([]);

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
  }, []);

  return (
    <div className={styles.inquiryContainer}>
      <h1 className={styles.inquiryHeader}>문의하기</h1>
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