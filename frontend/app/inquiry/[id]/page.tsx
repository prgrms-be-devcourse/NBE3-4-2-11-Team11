"use client";

import React, { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
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
  const router = useRouter();
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

  const handleDelete = async () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.error('No access token found');
      alert('로그인이 필요합니다.');
      return;
    }

    try {
      await axios.delete(`/api/v1/common/inquiries/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      alert('문의가 성공적으로 삭제되었습니다!');
      router.push("/inquiry");
    } catch (error) {
      console.error('Error deleting inquiry:', error);
      alert('삭제 실패: 알 수 없는 오류가 발생했습니다.');
    }
  };

  const handleEditRedirect = () => {
    const token = localStorage.getItem('accessToken');
    if (!token) {
      console.error('No access token found');
      alert('로그인이 필요합니다.');
      return;
    }

    // 현재 로그인된 사용자의 user_id 가져오기
    const userId = localStorage.getItem('userId'); // 로그인 시 저장된 userId를 가져온다고 가정
    const noticeUserId = inquiry.userId; // inquiry 상태에서 userId를 가져온다고 가정

    // user_id 비교 (둘 다 문자열로 변환)
    if (userId !== noticeUserId.toString()) {
      alert('권한이 없습니다. 이 문의글을 수정할 수 없습니다.');
      return;
    }

    // 권한이 확인되면 수정 페이지로 리다이렉트
    router.push(`/inquiry/edit/${id}`);
  };

  if (!inquiry) return <div>Loading...</div>;

  return (
    <div className="p-4 relative">
      <div className="absolute top-4 right-4">
        <button 
          onClick={handleDelete} 
          className="bg-red-500 text-white px-4 py-2 rounded mr-2"
        >
          삭제
        </button>
        <button 
          onClick={handleEditRedirect}
          className="bg-blue-500 text-white px-4 py-2 rounded"
        >
          수정
        </button>
      </div>
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
    </div>
  );
};

export default InquiryDetailPage; 