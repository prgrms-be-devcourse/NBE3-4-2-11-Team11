"use client";

import React, { useState, useEffect } from 'react';
import {redirect, useParams, useRouter } from 'next/navigation';
import axios from 'axios';
import styles from './inquiryDetail.module.css';

type ReplyDetailResponse = {
  id: number;
  createdAt: string;
  content: string;
};

type CommentDetailResponse = {
  id: number;
  createdAt: string;
  content: string;
};

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

const InquiryDetailPage = () => {
  const { id } = useParams();
  const router = useRouter();

  const [inquiry, setInquiry] = useState<InquiryDetailResponse | null>(null);
  const [token, setToken] = useState(null);
  const [repliesAndComments, setRepliesAndComments] = useState<Array<ReplyDetailResponse | CommentDetailResponse>>([]);
  const [newContent, setNewContent] = useState(""); // 새 댓글/답변 입력값
  const [error, setError] = useState<string | null>(null); // 에러 상태 추가
  const [editingId, setEditingId] = useState<number | null>(null); // 현재 수정 중인 댓글/답변 ID

const handleCreate = async () => {
    if (!newContent.trim()) {
      alert("내용을 입력해주세요!");
      return;
    }

  if (!token) {
    alert("로그인이 필요합니다."); // 토큰 값이 없는 경우 처리
    return;
  }

const checkIfAdminFromToken = (token: string | null): boolean => {
  if (!token) return false; // 토큰이 없는 경우 관리자 아님

  try {
    const decodedToken = JSON.parse(atob(token.split(".")[1])); // JWT Payload 디코드
    console.log("Decoded Token:", decodedToken); // 디코딩된 토큰 정보 로그로 확인
    return decodedToken.auth === "ROLE_ADMIN"; // ROLE_ADMIN일 경우 관리자 true 반환
  } catch (err) {
    console.error("Invalid token:", err);
    return false; // 디코딩 실패 시 관리자 아님
  }
};

  // 토큰 권한 확인 및 엔드포인트 결정
  const isAdmin = checkIfAdminFromToken(token); // 관리자 여부 확인
  const endpoint = isAdmin
    ? `/api/v1/admin/inquiries/${id}/reply` : `/api/v1/user/inquiries/${id}/comment`;

  try {
    const payload = { inquiryId: id, content: newContent };

    const response = await axios.post(endpoint, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    });

    // 리스트 초기화 및 성공 메시지
    setNewContent("");
    setError(null);
    alert(isAdmin ? "답변이 성공적으로 등록되었습니다!" : "댓글이 성공적으로 등록되었습니다!");
    window.location.reload();
 } catch (err: any) {
   // 주어진 에러 객체를 분석
  if (err.response) {
     // 서버가 반환한 에러 응답 (status 코드 & data)
     console.error("Server response error:", err.response.status, err.response.data);
     setError(err.response.data?.message || "서버 오류가 발생했습니다.");
    } else if (err.request) {
    // 서버에 요청이 도달하지 못한 경우
     console.error("No response received:", err.request);
     setError("서버로부터 응답이 없습니다. 잠시 후 다시 시도해주세요.");
    } else {
      // 기타 클라이언트 측 에러
      console.error("Axios request error:", err.message);
      setError("요청 중 문제가 발생했습니다.");
      }
    }
  };

  useEffect(() => {
    // 클라이언트 사이드에서만 localStorage 접근
    const storedToken = localStorage.getItem('accessToken');
    setToken(storedToken);
  }, []);

  useEffect(() => {
    const fetchInquiry = async () => {
      try {
        const response = await axios.get<RsData<InquiryDetailResponse>>(
          `/api/v1/common/inquiries/${id}`,
          {
            headers: {
              Authorization: `Bearer ${token}`, // 토큰을 헤더에 추가
            },
          }
        );
        setInquiry(response.data.data); // Inquiry 정보와 repliesAndComments를 설정
      } catch (error) {
        console.error("Error fetching inquiry detail:", error);
        alert("문의글 정보를 불러오는 중 오류가 발생했습니다.");
      }
    };

    if (id && token) {
          fetchInquiry();
        }
      }, [id, token]);

    if (!inquiry) return <div>Loading...</div>;

   return (
       <div className="p-4 relative">
         <div className="absolute top-12 right-4 flex space-x-2">
           <button
             onClick={() => {
               if (confirm("해당 문의글을 삭제하시겠습니까?")) {
                 axios.delete(`/api/v1/user/inquiries/${id}`, {
                   headers: { Authorization: `Bearer ${token}` },
                 });
                 alert("문의가 성공적으로 삭제되었습니다!");
                 router.push("/inquiry");
               }
             }}
             className="text-sm text-gray-500 hover:underline"
           >
             삭제
           </button>
           <button
             onClick={() => router.push(`/inquiry/edit/${id}`)}
             className="text-sm text-gray-500 hover:underline"
           >
             수정
           </button>
         </div>
         <div className={styles.inquiryDetailContainer}>
         {/* 문의 제목 및 상세 정보 출력 */}
          <div className={styles.inquiryDetailSubjectRow}>
           <h1 className={styles.inquiryDetailHeader}>{inquiry.subject}</h1>
           <p className={styles.inquiryDetailDate}>{new Date(inquiry.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
          {/* 구분선 */}
          <hr className={styles.inquiryDetailDivider}/>
          {/* 본문 출력 */}
             <div className={styles.inquiryDetailContent}>{inquiry.content}</div>
      </div>

      {/* 댓글과 답변 */}
      <div className={styles.replyContainer}>
        <h2 className={styles.replyHeader}>댓글 및 답변</h2>
        {inquiry.repliesAndComments.map((replyOrComment) => {
                  if (replyOrComment.type === 'comment') {
                    return (
                      <div key={replyOrComment.id} className={styles.replyContainer}>
                       <div className={styles.replyContent}>
                         <strong style={{ fontSize: '20px' }}>댓글</strong>
                         <br />
                         {replyOrComment.content}
                       </div>
                        <span className={styles.replyDate}>{replyOrComment.createdAt}</span>
                      </div>
                    );
                  } else if (replyOrComment.type === 'reply') {
                    return (
                      <div key={replyOrComment.id} className={styles.replyContainer}>
                        <div className={styles.replyContent}>
                          <strong style={{ fontSize: '20px' }}>답변</strong>
                          <br />
                          {replyOrComment.content}
                        </div>
                        <span className={styles.replyDate}>{replyOrComment.createdAt}</span>
                      </div>
                    );
                  }
                })}
              </div>
              {/* 댓글/답변 작성 UI */}
                <div className="mt-4 text-right">
                    <textarea
                        value={newContent}
                        onChange={(e) => setNewContent(e.target.value)}
                        placeholder="댓글 또는 답변을 입력하세요"
                        rows={4}
                        className="w-full px-4 py-2 border rounded"
                    ></textarea>
                <button
                    onClick={handleCreate}
                    className="mt-2 bg-blue-500 text-white px-4 py-2 rounded"
                >
                작성하기
                </button>
                    {error && <p className="mt-2 text-red-500">{error}</p>}
            </div>
       </div>
   );
};



export default InquiryDetailPage;