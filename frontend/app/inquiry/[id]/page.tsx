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
  const [token, setToken] = useState(null);
  const [replyExists, setReplyExists] = useState(false); // 답변 존재 여부 상태

  useEffect(() => {
    // 클라이언트 사이드에서만 localStorage 접근
    const storedToken = localStorage.getItem('accessToken');
    setToken(storedToken);
  }, []);

  useEffect(() => {
    const fetchInquiry = async () => {
      try {
        const response = await axios.get(`/api/v1/common/inquiries/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`, // 토큰을 헤더에 추가
          },
        });
        setInquiry(response.data.data);
        
        try {
          const replyResponse = await axios.get(`/api/v1/common/inquiries/${id}/reply`);
          setReply(replyResponse.data.data);
          if (replyResponse.data.data.length > 0) {
            setReplyExists(true); // 답변이 존재함
          }
        } catch (replyError) {
          // 답변이 없는 경우, reply를 null로 설정
          if (axios.isAxiosError(replyError) && replyError.response?.status === 404) {
            setReply(null); // 답변이 없을 경우 null로 설정
          } else {
            console.error('Error fetching reply:', replyError);
          }
        }
      } catch (error) {
        console.error('Error fetching inquiry detail:', error);
        alert('문의글 정보를 불러오는 중 오류가 발생했습니다.');
      }
    };

    if (id && token) {
      fetchInquiry();
    }
  }, [id, token]);

  const handleDelete = async () => {
    if (!inquiry) {
      alert('문의글 정보를 불러오는 중입니다.');
      return;
    }

    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    const confirmDelete = window.confirm('해당 문의글을 삭제하시겠습니까?');
    if (!confirmDelete) {
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
    console.log('Reply Exists:', replyExists); // 상태 확인
    if (replyExists) {
      alert("답변이 이미 존재합니다."); // 답변 존재 알림
      return;
    }

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
            </>
          )}
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '10px' }}>
          <button 
            onClick={handleReplyButtonClick}
            className={styles.replyButton}
          >
            답변 등록
          </button>
        </div>
      </div>
    </div>
  );
};

export default InquiryDetailPage;