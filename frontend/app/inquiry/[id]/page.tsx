"use client";

import React, { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import axios from 'axios';
import styles from './inquiryDetail.module.css';

type ReplyDetailResponse = {
  id: number; // 답변 내용
  createdAt: string; // 답변 생성일
};

type InquiryDetailResponse = {
  userId: number;
  id: number;
  subject: string;
  content: string;
  createdAt: string; // LocalDateTime을 문자열로 변환
  reply: ReplyDetailResponse | null; // 답변 추가
};

type RsData<T> = {
  resultCode: string;
  message: string;
  data: T;
};

type ReplyCreateResponse = {
  id: number; // 생성된 답변의 ID
};

const InquiryDetailPage = () => {
  const { id } = useParams();
  const router = useRouter();
  const [inquiry, setInquiry] = useState<InquiryDetailResponse | null>(null);
  const [reply, setReply] = useState<ReplyDetailResponse | null>(null);
  const [showInput, setShowInput] = useState(false);
  const [replyContent, setReplyContent] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchInquiry = async () => {
      try {
        const response = await axios.get(`/api/v1/common/inquiries/${id}`);
        setInquiry(response.data.data);
        const replyResponse = await axios.get(`/api/v1/common/inquiries/${id}/reply`);
        setReply(replyResponse.data.data);
      } catch (error) {
        console.error('Error fetching inquiry detail:', error);
      }
    };

    if (id) {
      fetchInquiry();
    }
  }, [id]);

  const token = localStorage.getItem('accessToken');

  const handleDelete = async () => {
    if (!inquiry) {
      alert('문의글 정보를 불러오는 중입니다.');
      return;
    }

    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    try {
      await axios.delete(`/api/v1/user/inquiries/${id}`, {
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
    if (!inquiry) {
      alert('문의글 정보를 불러오는 중입니다.');
      return;
    }

    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    router.push(`/inquiry/edit/${id}`);
  };

  const handleReplyButtonClick = () => {
    router.push(`/inquiry/${id}/reply/create`); // 답변 등록 페이지로 리다이렉트
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

        <div className={styles.replyContainer}>
          {reply === null ? (
            <p className={styles.replyContent}>답변 예정입니다.</p>
          ) : (
            <>
              <p className={styles.replyContent}>{reply.content}</p>
              <div className={styles.replyDate}>{new Date(reply.createdAt).toLocaleDateString('ko-KR')}</div>
            </>
          )}
        </div>

        {!showInput && (
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '10px' }}>
            <button 
              onClick={handleReplyButtonClick}
              className={styles.replyButton}
            >
              답변 등록
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default InquiryDetailPage;